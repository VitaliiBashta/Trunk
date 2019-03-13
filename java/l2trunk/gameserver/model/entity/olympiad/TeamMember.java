package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.impl.DuelEvent;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.skills.TimeStamp;
import l2trunk.gameserver.taskmanager.CancelTaskManager;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Log;

import java.util.Set;

public final class TeamMember {
    private final int objId;
    private final OlympiadGame game;
    private final CompType type;
    private final int side;
    private final String name;
    private String clanName = "";
    private ClassId classId;
    private double damage;
    private boolean isDead;
    private Player player;
    private Location returnLoc = null;

    TeamMember(int obj_id, String name, Player player, OlympiadGame game, int side) {
        objId = obj_id;
        this.name = name;
        this.game = game;
        type = game.getType();
        this.side = side;

        this.player = player;
        if (this.player == null)
            return;

        clanName = player.getClan() == null ? "" : player.getClan().getName();
        classId = player.getActiveClassId();

        player.setOlympiadSide(side);
        player.setOlympiadGame(game);
    }

    public boolean isDead() {
        return isDead;
    }

    public void doDie() {
        isDead = true;
    }

    public StatsSet getStat() {
        return Olympiad.nobles.get(objId);
    }

    void incGameCount() {
        StatsSet set = getStat();
        switch (type) {
            case TEAM:
                set.inc(Olympiad.GAME_TEAM_COUNT);
                break;
            case CLASSED:
                set.inc(Olympiad.GAME_CLASSES_COUNT);
                break;
            case NON_CLASSED:
                set.inc(Olympiad.GAME_NOCLASSES_COUNT);
                break;
        }
    }

    void takePointsForCrash() {
        if (!checkPlayer()) {
            StatsSet stat = getStat();
            int points = stat.getInteger(Olympiad.POINTS);
            int diff = Math.min(OlympiadGame.MAX_POINTS_LOOSE, points / type.getLooseMult());
            stat.inc(Olympiad.POINTS, - diff);
            Log.add("Olympiad Result: " + name + " lost " + diff + " points for crash", "olympiad");

            Player player = this.player;
            if (player == null)
                Log.add("Olympiad info: " + name + " crashed coz getPlayer == null", "olympiad");
            else {
                if (player.isLogoutStarted())
                    Log.add("Olympiad info: " + name + " crashed coz getPlayer.isLogoutStarted()", "olympiad");
                if (!player.isOnline())
                    Log.add("Olympiad info: " + name + " crashed coz !getPlayer.isOnline()", "olympiad");
                if (!player.isConnected())
                    Log.add("Olympiad info: " + name + " crashed coz !getPlayer.isOnline()", "olympiad");
                if (player.getOlympiadGame() == null)
                    Log.add("Olympiad info: " + name + " crashed coz getPlayer.getOlympiadGame() == null", "olympiad");
                if (player.getOlympiadObserveGame() != null)
                    Log.add("Olympiad info: " + name + " crashed coz getPlayer.getOlympiadObserveGame() != null", "olympiad");
            }
        }
    }

    boolean checkPlayer() {
        Player player = this.player;
        return player != null && !player.isLogoutStarted() && player.getOlympiadGame() != null && !player.isInObserverMode();
    }

    void portPlayerToArena() {
        Player player = this.player;
        if (!checkPlayer() || player.isTeleporting()) {
            this.player = null;
            return;
        }

        //Fix for Cancel exploit
        CancelTaskManager.INSTANCE.cancelPlayerTasks(player);

        DuelEvent duel = player.getEvent(DuelEvent.class);
        if (duel != null)
            duel.abortDuel(player);

        returnLoc = player.stablePoint == null ? player.getReflection().getReturnLoc() == null ? player.getLoc() : player.getReflection().getReturnLoc() : player.stablePoint;

        if (player.isDead())
            player.setPendingRevive(true);
        if (player.isSitting())
            player.standUp();
        if (player.isRiding() || player.isFlying())
            player.dismount();

        player.setTarget(null);
        player.setIsInOlympiadMode(true);

        player.leaveParty();

        Reflection ref = game.getReflection();
        InstantZone instantZone = ref.getInstancedZone();

        Location tele = Location.findPointToStay(instantZone.getTeleportCoords().get(side - 1), 50, 50, ref.getGeoIndex());

        player.stablePoint = returnLoc;
        player.teleToLocation(tele, ref);

        if (type == CompType.TEAM)
            player.setTeam(side == 1 ? TeamType.BLUE : TeamType.RED);

        player.sendPacket(new ExOlympiadMode(side));
    }

    void portPlayerBack() {
        Player player = this.player;
        if (player == null)
            return;

        player.setOlympiadSide(-1); // эти параметры ставятся в конструкторе и должны очищаться всегда
        player.setOlympiadGame(null);

        if (returnLoc == null) // игрока не портнуло на стадион
            return;

        player.setIsInOlympiadMode(false);
        player.setOlympiadCompStarted(false);
        if (type == CompType.TEAM)
            player.setTeam(TeamType.NONE);

        removeBuffs(true);

        // Возвращаем клановые скиллы если репутация положительная.
        if (player.getClan() != null && player.getClan().getReputationScore() >= 0)
            player.getClan().enableSkills(player);

        // Add Hero Skills
        if (player.isHero())
            Hero.addSkills(player);

        if (player.isDead()) {
            player.broadcastPacket(new Revive(player));
        }

        player.setFullCp();
        player.setFullHpMp();

        // Обновляем скилл лист, после добавления скилов
        player.sendPacket(new SkillList(player));
        player.sendPacket(new ExOlympiadMode(0));
        player.sendPacket(new ExOlympiadMatchEnd());

        player.stablePoint = null;
        player.teleToLocation(returnLoc, ReflectionManager.DEFAULT);

    }

    void preparePlayer() {
        if (player == null)
            return;

        if (player.isInObserverMode())
            if (player.isInOlympiadObserverMode())
                player.leaveOlympiadObserverMode(true);
            else
                player.leaveObserverMode();

        // Un activate clan skills
        if (player.getClan() != null)
            player.getClan().disableSkills(player);

        // Remove Hero Skills
        if (player.isHero())
            Hero.removeSkills(player);

        // Abort casting if getPlayer casting
        if (player.isCastingNow())
            player.abortCast(true, true);

        // Удаляем баффы и чужие кубики
        removeBuffs(true);

        // unsummon agathion
        if (player.getAgathionId() > 0)
            player.setAgathion(0);

        // Сброс кулдауна всех скилов, время отката которых меньше 15 минут
        for (TimeStamp sts : player.getSkillReuses()) {
            if (sts == null)
                continue;

            Skill skill = player.getKnownSkill(sts.id());
            if (skill == null || skill.level != sts.level)
                continue;

            if (skill.getReuseDelay(player) <= 15 * 60000L)
                player.enableSkill(skill);
        }

        // Обновляем скилл лист, после удаления скилов
        player.sendPacket(new SkillList(player));
        // Обновляем куллдаун, после сброса
        player.sendPacket(new SkillCoolTime(player));

        // Remove Hero weapons
        player.getInventory().refreshEquip();

        // remove bsps/sps/ss automation
        Set<Integer> activeSoulShots = player.getAutoSoulShot();
        for (int itemId : activeSoulShots) {
            player.removeAutoSoulShot(itemId);
            player.sendPacket(new ExAutoSoulShot(itemId, false));
        }

        // Разряжаем заряженные соул и спирит шоты
        ItemInstance weapon = player.getActiveWeaponInstance();
        if (weapon != null) {
            weapon.setChargedSpiritshot(ItemInstance.CHARGED_NONE);
            weapon.setChargedSoulshot(ItemInstance.CHARGED_NONE);
        }

        heal();
    }

    void startComp() {
        Player player = this.player;
        if (player == null)
            return;
        this.player.setOlympiadCompStarted(true);
    }

    void stopComp() {
        Player player = this.player;
        if (player == null)
            return;
        this.player.setOlympiadCompStarted(false);
    }

    public void heal() {
        Player player = this.player;
        if (player == null)
            return;

        player.setFullHpMp();
        player.setFullCp();
        player.broadcastUserInfo(true);

    }

    void removeBuffs(boolean fromSummon) {
        Player player = this.player;
        if (player == null)
            return;

        player.abortAttack(true, false);
        if (player.isCastingNow())
            player.abortCast(true, true);

        player.getEffectList().getAllEffects().stream()
                .filter(e -> e.getEffectType() != EffectType.Cubic)
                .filter(e -> !e.skill.isToggle())
                .peek(e -> player.sendPacket(new SystemMessage(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.skill.id, e.skill.level)))
                .forEach(Effect::exit);


        if (player.isFrozen())
            player.stopFrozen();

        Summon servitor = player.getPet();
        if (servitor != null) {
            servitor.abortAttack(true, false);
            if (servitor.isCastingNow())
                servitor.abortCast(true, true);

            if (fromSummon) {
                if (servitor instanceof PetInstance)
                    servitor.unSummon();
                else
                    servitor.getEffectList().stopAllEffects();
            }

            if (servitor.isFrozen())
                servitor.stopFrozen();
        }
    }

    void saveNobleData() {
        OlympiadDatabase.saveNobleData(objId);
    }

    public void logout() {
        player = null;
    }

    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return name;
    }

    void addDamage(double d) {
        damage += d;
    }

    public double getDamage() {
        return damage;
    }

    public String getClanName() {
        return clanName;
    }

    public ClassId getClassId() {
        return classId;
    }

    public int getObjectId() {
        return objId;
    }
}