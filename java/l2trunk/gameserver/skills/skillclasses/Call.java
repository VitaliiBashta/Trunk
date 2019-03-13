package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.Objects;

import static l2trunk.gameserver.model.Zone.ZoneType.no_restart;
import static l2trunk.gameserver.model.Zone.ZoneType.no_summon;

public final class Call extends Skill {
    private final boolean party;

    public Call(StatsSet set) {
        super(set);
        party = set.isSet("party");
    }

    public static SystemMessage canSummonHere(Player activeChar) {
        if (activeChar.isAlikeDead() || activeChar.isInOlympiadMode() || activeChar.isInObserverMode() || activeChar.isFlying() || activeChar.isFestivalParticipant())
            return Msg.NOTHING_HAPPENED;

        if (activeChar.isJailed()) {
            activeChar.sendMessage("You cannot escape from Jail!");
            return Msg.NOTHING_HAPPENED;
        }

        // "You can not call the characters to / from the free PvP"
        // "In the zone of sediment"
        // "At the Olympiad Stadium"
        // "In the area of certain raid bosses and epic bosses"

        if (activeChar.isInZoneBattle() || activeChar.isInZone(Zone.ZoneType.SIEGE) || activeChar.isInZone(no_restart) || activeChar.isInZone(no_summon) || activeChar.isInBoat() || activeChar.getReflection() != ReflectionManager.DEFAULT)
            return Msg.YOU_MAY_NOT_SUMMON_FROM_YOUR_CURRENT_LOCATION;

        // if (activeChar.isInCombat())
        // return Msg.YOU_CANNOT_SUMMON_DURING_COMBAT;

        if (activeChar.isInStoreMode() || activeChar.isProcessingRequest())
            return Msg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_THE_PRIVATE_SHOPS;

        return null;
    }

    public static SystemMessage canBeSummoned(Creature cha, Creature target) {
        if (target instanceof Player) {
            Player playerTarget = (Player) target;
            if ((!playerTarget.isTerritoryFlagEquipped()) && (!playerTarget.isFlying()) && (!playerTarget.isInObserverMode()) && (!playerTarget.isFestivalParticipant()) && (playerTarget.getPlayerAccess().UseTeleport)) {
                if (playerTarget.isInOlympiadMode()) {
                    return Msg.YOU_CANNOT_SUMMON_PLAYERS_WHO_ARE_CURRENTLY_PARTICIPATING_IN_THE_GRAND_OLYMPIAD;
                }
                if ((playerTarget.isInZoneBattle()) || (playerTarget.isInZone(Zone.ZoneType.SIEGE)) || (playerTarget.isInZone(no_restart)) || (playerTarget.isInZone(no_summon)) || (playerTarget.getReflection() != ReflectionManager.DEFAULT) || (playerTarget.isInBoat())) {
                    return Msg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING;
                }

                if (playerTarget.isAlikeDead()) {
                    return new SystemMessage(SystemMessage.S1_IS_DEAD_AT_THE_MOMENT_AND_CANNOT_BE_SUMMONED).addString(playerTarget.getName());
                }

                if ((playerTarget.getPvpFlag() != 0) || (playerTarget.isInCombat())) {
                    return new SystemMessage(SystemMessage.S1_IS_ENGAGED_IN_COMBAT_AND_CANNOT_BE_SUMMONED).addString(target.getName());
                }

                if (playerTarget.isJailed()) {
                    playerTarget.sendMessage("You cannot escape from Jail!");
                    return Msg.YOUR_TARGET_IS_IN_AN_AREA_WHICH_BLOCKS_SUMMONING;
                }

                if ((playerTarget.getPvpFlag() != 0) || (playerTarget.isInCombat())) {
                    return new SystemMessage(1843).addString(target.getName());
                }

                if ((playerTarget.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) || (playerTarget.isProcessingRequest())) {
                    return new SystemMessage(SystemMessage.S1_IS_CURRENTLY_TRADING_OR_OPERATING_A_PRIVATE_STORE_AND_CANNOT_BE_SUMMONED).addString(target.getName());
                }
                if (cha instanceof Player) {
                    Player caster = (Player) cha;

                    if (caster.getReflection() != ReflectionManager.DEFAULT) {
                        InstantZone iz = caster.getReflection().getInstancedZone();
                        if ((iz != null) && ((playerTarget.getLevel() < iz.getMinLevel()) || (playerTarget.getLevel() > iz.getMaxLevel()))) {
                            return Msg.INVALID_TARGET;
                        }
                    }
                }
                return null;
            } else {
                return Msg.INVALID_TARGET;
            }
        } else {
            return Msg.INVALID_TARGET;
        }
    }

    @Override
    public boolean checkCondition(Player activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (party && activeChar.getParty() == null)
            return false;

        SystemMessage msg = canSummonHere(activeChar);
        if (msg != null) {
            activeChar.sendPacket(msg);
            return false;
        }

        // This check is only for a single target
        if (!party) {
            if (activeChar == target)
                return false;

            msg = canBeSummoned(activeChar, target);
            if (msg != null) {
                activeChar.sendPacket(msg);
                return false;
            }
        }

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (!(activeChar instanceof Player))
            return;
        Player player = (Player) activeChar;
        SystemMessage msg = canSummonHere(player);
        if (msg != null) {
            player.sendPacket(msg);
            return;
        }

        if (party) {
            if (player.getParty() != null)
                player.getParty().getMembersStream().stream()
                        .filter(target -> !target.equals(player))
                        .filter(target -> canBeSummoned(player, target) == null)
                        .filter(target -> !target.isTerritoryFlagEquipped())
                        .forEach(target -> {
                            target.stopMove();
                            target.teleToLocation(Location.findPointToStay(player, 100, 150), player.getGeoIndex());
                            getEffects(player, target, activateRate > 0, false);
                        });

            if (isSSPossible())
                player.unChargeShots(isMagic());
            return;
        }

        targets.stream()
                .filter(Objects::nonNull)
                .filter(target -> canBeSummoned(activeChar, target) == null)
                .forEach(target -> {
                    ((Player) target).summonCharacterRequest(activeChar, Location.findAroundPosition(activeChar, 100, 150), id == 1403 || id == 1404 ? 1 : 0);

                    getEffects(activeChar, target, activateRate > 0, false);
                });

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}