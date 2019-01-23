package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Recall extends Skill {
    private static final Map<Integer, Location> towns = new HashMap<>();
    private static final Map<Integer, Location> specialScrolls = new HashMap<>();

    static {
        towns.put(1, new Location(-83990, 243336, -3700));// Talking Island
        towns.put(2, new Location(45576, 49412, -2950));// Elven Village
        towns.put(3, new Location(12501, 16768, -4500)); // Dark Elven Village
        towns.put(4, new Location(-44884, -115063, -80));// Orc Village
        towns.put(5, new Location(115790, -179146, -890));// Dwarven Village
        towns.put(6, new Location(-14279, 124446, -3000)); // Town of Gludio
        towns.put(7, new Location(-82909, 150357, -3000));// Gludin Village
        towns.put(8, new Location(19025, 145245, -3107));// Town of Dion
        towns.put(9, new Location(82272, 147801, -3350));// Town of Giran
        towns.put(10, new Location(82323, 55466, -1480));// Town of Oren
        towns.put(11, new Location(144526, 24661, -2100));// Town of Aden
        towns.put(12, new Location(117189, 78952, -2210));// Hunters Village
        towns.put(13, new Location(110768, 219824, -3624));// Heine
        towns.put(14, new Location(43536, -50416, -800));// Rune Township
        towns.put(15, new Location(148288, -58304, -2979));// Town of Goddard
        towns.put(16, new Location(87776, -140384, -1536)); // Town of Schuttgart
        towns.put(17, new Location(-117081, 44171, 507));// Kamael Village
        towns.put(18, new Location(10568, -24600, -3648));// Primeval Isle
        towns.put(19, new Location(19025, 145245, -3107));// Floran Village
        towns.put(20, new Location(-16434, 208803, -3664));// Hellbound
        towns.put(21, new Location(-184200, 243080, 1568)); // Keucereus Alliance Base
        towns.put(22, new Location(8976, 252416, -1928));// Steel Citadel

        specialScrolls.put(7125, new Location(17144, 170156, -3502));// floran
        specialScrolls.put(7127, new Location(105918, 109759, -3207));// hardin's academy
        specialScrolls.put(7130, new Location(85475, 16087, -3672));// ivory
        specialScrolls.put(9716, new Location(-120000, 44500, 352));// Scroll of Escape: Kamael Village for starters
        specialScrolls.put(7618, new Location(149864, -81062, -5618));
        specialScrolls.put(7619, new Location(108275, -53785, -2524));
    }

    private final int townId;
    private final boolean clanhall;
    private final boolean castle;
    private final boolean fortress;
    private final Location loc;

    public Recall(StatsSet set) {
        super(set);
        townId = set.getInteger("townId", 0);
        clanhall = set.getBool("clanhall", false);
        castle = set.getBool("castle", false);
        fortress = set.getBool("fortress", false);
        String[] cords = set.getString("loc", "").split(";");
        if (cords.length == 3)
            loc = new Location(cords);
        else
            loc = null;
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        // BSOE the clan hall / lock only works if you have one
        if (hitTime == 200) {
            Player player = activeChar.getPlayer();
            if (clanhall) {
                if (player.getClan() == null || player.getClan().getHasHideout() == 0) {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemConsumeId.get(0)));
                    return false;
                }
            } else if (castle) {
                if (player.getClan() == null || player.getClan().getCastle() == 0) {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemConsumeId.get(0)));
                    return false;
                }
            } else if (fortress)
                if (player.getClan() == null || player.getClan().getHasFortress() == 0) {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemConsumeId.get(0)));
                    return false;
                }
        }

        if (activeChar.isPlayer()) {
            Player p = (Player) activeChar;
            if (p.getActiveWeaponFlagAttachment() != null) {
                activeChar.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
                return false;
            }
            if (p.isInDuel() || p.getTeam() != TeamType.NONE) {
                activeChar.sendMessage(new CustomMessage("common.RecallInDuel", p));
                return false;
            }
            if (p.isInOlympiadMode()) {
                activeChar.sendPacket(SystemMsg.YOU_CANNOT_USE_THAT_SKILL_IN_A_GRAND_OLYMPIAD_MATCH);
                return false;
            }

            if (targetType == SkillTargetType.TARGET_PARTY && activeChar.getReflection() != ReflectionManager.DEFAULT) {
                activeChar.sendMessage("This skill cannot be used inside instanced zones!");
                return false;
            }

            if (p.isJailed()) {
                p.sendMessage("You cannot escape from Jail!");
                return false;
            }
        }

        if (activeChar.isInZone(ZoneType.no_escape) || townId > 0 && activeChar.getReflection() != null && activeChar.getReflection().getCoreLoc() != null) {
            if (activeChar.isPlayer())
                activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Recall.Here", (Player) activeChar));
            return false;
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(final Creature activeChar, List<Creature> targets) {
        for (final Creature target : targets)
            if (target != null) {
                final Player pcTarget = target.getPlayer();
                if (pcTarget == null)
                    continue;
                if (!pcTarget.getPlayerAccess().UseTeleport)
                    continue;
                if (pcTarget.getActiveWeaponFlagAttachment() != null) {
                    activeChar.sendPacket(SystemMsg.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
                    continue;
                }
                if (pcTarget.isFestivalParticipant()) {
                    activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Recall.Festival", (Player) activeChar));
                    continue;
                }
                if (pcTarget.isInOlympiadMode()) {
                    activeChar.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD);
                    return;
                }
                if (pcTarget.isInObserverMode()) {
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(id, level));
                    return;
                }

                if (pcTarget.isJailed()) {
                    pcTarget.sendMessage("You cannot escape from Jail!");
                    return;
                }
                if (pcTarget.isInDuel() || pcTarget.getTeam() != TeamType.NONE) {
                    activeChar.sendMessage(new CustomMessage("common.RecallInDuel", (Player) activeChar));
                    return;
                }
                if (isItemHandler()) {
                    if (specialScrolls.containsKey(itemConsumeId.get(0))) {
                        pcTarget.teleToLocation(specialScrolls.get(itemConsumeId.get(0)));
                        return;
                    }

                }
                if (loc != null) {
                    pcTarget.teleToLocation(loc);
                    return;
                }

                if (towns.containsKey(townId)) {
                    pcTarget.teleToLocation(towns.get(townId), 0);
                    return;
                }

                if (castle) {// To castle
                    pcTarget.teleToCastle();
                    return;
                }
                if (clanhall) { // to clanhall
                    pcTarget.teleToClanhall();
                    return;
                }
                if (fortress) { // To fortress
                    pcTarget.teleToFortress();
                    return;
                }
                pcTarget.teleToClosestTown();
            }
        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}