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

    private final boolean _isEnabled;

    private final List<Location> restartPoints;
    private final List<Location> pkRestartPoints;
    private final long _restartTime;

    private final int _enteringMessageId;
    private final int _leavingMessageId;

    /**
     * Раса на которую применим эффект
     */
    private final Race _affectRace;

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
    private final int _damageMessageId;

    /**
     * Урон от зоны по хп
     */
    private final int _damageOnHP;

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
    private final double _regenBonusHP;

    /**
     * Бонус регенерации мп
     */
    private final double _regenBonusMP;

    private final int _eventId;

    private final List<String> blockedActions;

    private final int _index;

    private final StatsSet params;

    private final boolean isEpicPvP;

    public ZoneTemplate(StatsSet set) {
        name = set.getString("name");
        type = ZoneType.valueOf(set.getString("type"));
        territory = set.getTerritory("territory");

        _enteringMessageId = set.getInteger("entering_message_no", 0);
        _leavingMessageId = set.getInteger("leaving_message_no", 0);

        target = ZoneTarget.valueOf(set.getString("target", "pc"));
        _affectRace = set.getString("affect_race", "all").equals("all") ? null : Race.valueOf(set.getString("affect_race"));

        //Зона с эффектом
        String s = set.getString("skill_name", null);
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
        moveBonus = set.getDouble("move_bonus", 0.);
        _regenBonusHP = set.getDouble("hp_regen_bonus", 0.);
        _regenBonusMP = set.getDouble("mp_regen_bonus", 0.);

        //Зона с дамагом
        _damageOnHP = set.getInteger("damage_on_hp");
        damageOnMP = set.getInteger("damage_on_mp");
        _damageMessageId = set.getInteger("message_no");

        _eventId = set.getInteger("eventId");

        _isEnabled = set.getBool("enabled", true);

        restartPoints = set.getLocations("restart_points");
        pkRestartPoints = set.getLocations("PKrestart_points");
        _restartTime = set.getLong("restart_time");

        s = set.getString("blocked_actions");
        if (s != null)
            blockedActions = Arrays.asList(s.split(ExProperties.defaultDelimiter));
        else
            blockedActions = List.of();

        isEpicPvP = set.getBool("epicPvP", false);

        _index = set.getInteger("index", 0);

        params = set;
    }

    public boolean isEnabled() {
        return _isEnabled;
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
        return _enteringMessageId;
    }

    public int getLeavingMessageId() {
        return _leavingMessageId;
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
        return _affectRace;
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
        return _damageMessageId;
    }

    /**
     * Сколько урона зона нанесет по хп
     *
     * @return количество урона
     */
    public int getDamageOnHP() {
        return _damageOnHP;
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
        return _regenBonusHP;
    }

    public double getRegenBonusMP() {
        return _regenBonusMP;
    }

    public long getRestartTime() {
        return _restartTime;
    }

    public List<Location> getRestartPoints() {
        return restartPoints;
    }

    public List<Location> getPKRestartPoints() {
        return pkRestartPoints;
    }

    public int getIndex() {
        return _index;
    }

    public int getEventId() {
        return _eventId;
    }

    public boolean isEpicPvP() {
        return isEpicPvP;
    }

    public StatsSet getParams() {
        return params;
    }
}
