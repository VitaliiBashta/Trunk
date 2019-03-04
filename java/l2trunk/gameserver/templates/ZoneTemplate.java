package l2trunk.gameserver.templates;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.configuration.ExProperties;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Territory;
import l2trunk.gameserver.model.Zone.ZoneTarget;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class ZoneTemplate {
    private final String name;
    private final ZoneType type;

    private final Territory territory;

    private final boolean isEnabled;

    public final List<Location> restartPoints;
    private final List<Location> pkRestartPoints;
    private final long restartTime;

    private final int enteringMessageId;
    private final int leavingMessageId;

    /**
     * Раса на которую применим эффект
     */
    private final Race affectRace;

    private final ZoneTarget target;

    private final Skill skill;
    private final int skillProb;
    private final int initialDelay;
    private final int unitTick;
    private final int randomTick;

    /**
     * Сообщение которое шлется при уроне от зоны (не скилла)
     * К примеру на осадах. Пока это только 686 (You have received $s1 damage from the fire of magic.)
     */
    private final int damageMessageId;

    /**
     * Урон от зоны по хп
     */
    private final int damageOnHP;

    /**
     * Урон от зоны по мп
     */
    private final int damageOnMP;


    /**
     * Бонус/штраф к скорости движения
     */
    private final double moveBonus;

    /**
     * Бонус регенерации хп
     */
    private final double regenBonusHP;

    /**
     * Бонус регенерации мп
     */
    private final double regenBonusMP;

    private final int eventId;

    private final List<String> blockedActions;

    private final int index;

    private final StatsSet params;

    private final boolean isEpicPvP;

    public ZoneTemplate(StatsSet set) {
        name = set.getString("name");
        type = ZoneType.valueOf(set.getString("type"));
        territory = set.getTerritory("territory");

        enteringMessageId = set.getInteger("entering_message_no");
        leavingMessageId = set.getInteger("leaving_message_no");

        target = ZoneTarget.valueOf(set.getString("target", "pc"));
        affectRace = set.getString("affect_race", "all").equals("all") ? null : Race.valueOf(set.getString("affect_race"));

        //Зона с эффектом
        String s = set.getString("skill_name");
        Skill skill = null;
        if (s != null) {
            String[] sk = s.split("[\\s,;]+");
            skill = SkillTable.INSTANCE.getInfo(toInt(sk[0]), toInt(sk[1]));
        }
        this.skill = skill;
        skillProb = set.getInteger("skill_prob", 100);
        initialDelay = set.getInteger("initial_delay", 1);
        unitTick = set.getInteger("unit_tick", 1);
        randomTick = set.getInteger("random_time");

        //Зона с бонусами
        moveBonus = set.getDouble("move_bonus");
        regenBonusHP = set.getDouble("hp_regen_bonus");
        regenBonusMP = set.getDouble("mp_regen_bonus");

        //Зона с дамагом
        damageOnHP = set.getInteger("damage_on_hp");
        damageOnMP = set.getInteger("damage_on_mp");
        damageMessageId = set.getInteger("message_no");

        eventId = set.getInteger("eventId");

        isEnabled = set.isSet("enabled");

        restartPoints = set.getLocations("restart_points");
        pkRestartPoints = set.getLocations("PKrestart_points");
        restartTime = set.getLong("restart_time");

        s = set.getString("blocked_actions");
        if (s != null)
            blockedActions = Arrays.asList(s.split(ExProperties.defaultDelimiter));
        else
            blockedActions = List.of();

        isEpicPvP = set.isSet("epicPvP");

        index = set.getInteger("index");

        params = set;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public String getName() {
        return name;
    }

    public ZoneType getType() {
        return type;
    }

    public Territory getTerritory() {
        return territory;
    }

    public int getEnteringMessageId() {
        return enteringMessageId;
    }

    public int getLeavingMessageId() {
        return leavingMessageId;
    }

    public Skill getZoneSkill() {
        return skill;
    }

    public int getSkillProb() {
        return skillProb;
    }

    public int getInitialDelay() {
        return initialDelay;
    }

    public int getUnitTick() {
        return unitTick;
    }

    public int getRandomTick() {
        return randomTick;
    }

    public ZoneTarget getZoneTarget() {
        return target;
    }

    public Race getAffectRace() {
        return affectRace;
    }

    public List<String> getBlockedActions() {
        return blockedActions;
    }

    /**
     * Номер системного вообщения которое будет отослано игроку при нанесении урона зоной
     *
     * @return SystemMessage ID
     */
    public int getDamageMessageId() {
        return damageMessageId;
    }

    /**
     * Сколько урона зона нанесет по хп
     *
     * @return количество урона
     */
    public int getDamageOnHP() {
        return damageOnHP;
    }

    /**
     * Сколько урона зона нанесет по мп
     *
     * @return количество урона
     */
    public int getDamageOnMP() {
        return damageOnMP;
    }

    public double getMoveBonus() {
        return moveBonus;
    }

    public double getRegenBonusHP() {
        return regenBonusHP;
    }

    public double getRegenBonusMP() {
        return regenBonusMP;
    }

    public long getRestartTime() {
        return restartTime;
    }

    public List<Location> getPKRestartPoints() {
        return pkRestartPoints;
    }

    public int getEventId() {
        return eventId;
    }

    public boolean isEpicPvP() {
        return isEpicPvP;
    }

    public StatsSet getParams() {
        return params;
    }
}
