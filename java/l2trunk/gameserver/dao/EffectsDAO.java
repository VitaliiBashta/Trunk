package l2trunk.gameserver.dao;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.SqlBatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public enum EffectsDAO {
    INSTANCE;
    private static final int SUMMON_SKILL_OFFSET = 100000;
    private final Logger _log = LoggerFactory.getLogger(EffectsDAO.class);

    public void restoreEffects(final Playable playable, final boolean heal, final double healToHp, final double healToCp, final double healToMp) {
        int id = getId(playable);
        if (id == 0) return;
        int objectId = playable.isPlayer() ? playable.getObjectId() : playable.getPlayer().getObjectId();

        if (playable.getPlayer().isInOlympiadMode()) {
            if (heal) {
                heal(playable, healToHp, healToCp, healToMp);
            }
            return;
        }

        final List<Effect> effectsToRestore = new LinkedList<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement select = con.prepareStatement("SELECT `skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration` FROM `character_effects_save` WHERE `object_id`=? AND `id`=? ORDER BY `order` ASC");
             PreparedStatement delete = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id = ? AND id=?")) {

            select.setInt(1, objectId);
            select.setInt(2, id);
            ResultSet rset = select.executeQuery();
            while (rset.next()) {
                int skillId = rset.getInt("skill_id");
                int skillLvl = rset.getInt("skill_level");
                int effectCount = rset.getInt("effect_count");
                long effectCurTime = rset.getLong("effect_cur_time");
                long duration = rset.getLong("duration");

                Skill skill = SkillTable.INSTANCE.getInfo(skillId, skillLvl);
                if (skill == null)
                    continue;

                for (EffectTemplate et : skill.getEffectTemplates()) {
                    if (et == null)
                        continue;
                    Env env = new Env(playable, playable, skill);
                    Effect effect = et.getEffect(env);
                    if (effect == null || effect.isOneTime())
                        continue;

                    effect.setCount(effectCount);
                    effect.setPeriod(effectCount == 1 ? duration - effectCurTime : duration);

                    effectsToRestore.add(effect);
                }
            }

            delete.setInt(1, objectId);
            delete.setInt(2, id);
            delete.execute();
        } catch (SQLException e) {
            _log.error("Could not restore active effects data!", e);
        }

        ThreadPoolManager.INSTANCE.execute(() -> {
            for (Effect e : effectsToRestore) {
                e.schedule();

                try {
                    Thread.sleep(5);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            if (heal)
                heal(playable, healToHp, healToCp, healToMp);
        });
    }

    public void deleteEffects(int objectId, int skillId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id = ? AND id=?")) {
            statement.setInt(1, objectId);
            statement.setInt(2, SUMMON_SKILL_OFFSET + skillId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Could not delete effects active effects data!" + e, e);
        }
    }

    private void heal(Playable playable, double hp, double cp, double mp) {
        if (!playable.isPlayer()) {
            hp = playable.getMaxHp();
            cp = playable.getMaxCp();
            mp = playable.getMaxMp();
        }
        playable.setCurrentHpMp(hp, mp);
        playable.setCurrentCp(cp);
    }

    private int getId(Playable playable) {
        int id = 0;
        if (playable.isPlayer()) {
            id = ((Player) playable).getActiveClassId();
        } else if (playable.isSummon()) {
            id = ((SummonInstance) playable).getEffectIdentifier() + SUMMON_SKILL_OFFSET;
        }
        return id;
    }

    public void insert(Playable playable) {
        int id = getId(playable);
        if (id == 0) return;
        int objectId = playable.isPlayer() ? playable.getObjectId() : playable.getPlayer().getObjectId();


        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            Statement statement = con.createStatement();

            int order = 0;
            SqlBatch b = new SqlBatch("INSERT IGNORE INTO `character_effects_save` (`object_id`,`skill_id`,`skill_level`,`effect_count`,`effect_cur_time`,`duration`,`order`,`id`) VALUES");

            StringBuilder sb;
            List<Effect> allSavableEffects = playable.getEffectList().getAllEffects()
                    .filter(Effect::isInUse)
                    .filter(e -> !e.getSkill().isToggle())
                    .filter(e -> e.getEffectType() != EffectType.HealOverTime)
                    .filter(e -> e.getEffectType() != EffectType.CombatPointHealOverTime)
                    .filter(e -> !(playable.isSummon() && e.getSkill().isOffensive))// Summons should not store debuffs, only buffs
                    .collect(Collectors.toList());
            for (Effect effect : allSavableEffects) {

                if (effect.isSaveable()) {
                    sb = new StringBuilder("(");
                    sb.append(objectId).append(",");
                    sb.append(effect.getSkill().id).append(",");
                    sb.append(effect.getSkill().level).append(",");
                    sb.append(effect.getCount()).append(",");
                    sb.append(effect.getTime()).append(",");
                    sb.append(effect.getPeriod()).append(",");
                    sb.append(order).append(",");
                    sb.append(id).append(")");
                    b.write(sb.toString());
                }
                while ((effect = effect.getNext()) != null && effect.isSaveable()) {
                    sb = new StringBuilder("(");
                    sb.append(objectId).append(",");
                    sb.append(effect.getSkill().id).append(",");
                    sb.append(effect.getSkill().level).append(",");
                    sb.append(effect.getCount()).append(",");
                    sb.append(effect.getTime()).append(",");
                    sb.append(effect.getPeriod()).append(",");
                    sb.append(order).append(",");
                    sb.append(id).append(")");
                    b.write(sb.toString());
                }
                order++;
            }

            if (!b.isEmpty())
                statement.executeUpdate(b.close());
        } catch (SQLException e) {
            _log.error("Could not store active effects data!", e);
        }
    }
}
