package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class Teleport implements IVoicedCommandHandler {
    private static final List<String> COMMAND_LIST = List.of("pvp", "farm", "farm_hard", "farm_low");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        command = command.toLowerCase().intern();
        switch (command) {
            case "pvp":
                return pvp(activeChar);
            case "farm":
                return farm(activeChar);
            case "farm_hard":
                return farm_hard(activeChar);
            case "farm_low":
                return farm_low(activeChar);
            default:
                return false;
        }
    }

    private boolean pvp(Player activeChar) {
            final int CoinCountPvP = Config.PRICE_PVP;
        if (teleportForbidden(activeChar)) return false;
        if (CoinCountPvP != 0 && activeChar.getInventory().getItemByItemId(Config.PVP_TELEPORT_ITEM_ID).getCount() < CoinCountPvP) {
                    activeChar.sendMessage("You do not have enough money");
                    activeChar.sendActionFailed();
                    return false;
                }
                activeChar.teleToLocation(Config.PVP_X, Config.PVP_Y, Config.PVP_Z);
                removeItem(activeChar, Config.PVP_TELEPORT_ITEM_ID, CoinCountPvP, ".pvp");
                activeChar.sendMessage("You moved to the PvP Arena");
        return true;
    }

    private boolean farm(Player activeChar) {
            final int CoinCountFarm = Config.PRICE_FARM;
        if (teleportForbidden(activeChar)) return false;

        if (CoinCountFarm != 0 && activeChar.getInventory().getItemByItemId(Config.FARM_TELEPORT_ITEM_ID).getCount() < CoinCountFarm) {
                    activeChar.sendMessage("You do not have enough money");
                    activeChar.sendActionFailed();
                    return false;
                }
                activeChar.teleToLocation(Config.FARM_X, Config.FARM_Y, Config.FARM_Z);
                removeItem(activeChar, Config.FARM_TELEPORT_ITEM_ID, CoinCountFarm, ".farm");
                activeChar.sendMessage("You moved to the farm area");
        return true;
    }

    private boolean farm_hard(Player activeChar) {
            final int CoinCountFarmH = Config.PRICE_FARM_HARD;
        if (teleportForbidden(activeChar)) return false;

        if (CoinCountFarmH != 0 && activeChar.getInventory().getItemByItemId(Config.FARM_HARD_TELEPORT_ITEM_ID).getCount() < CoinCountFarmH) {
                    activeChar.sendMessage("You do not have enough money");
                    activeChar.sendActionFailed();
                    return false;
                }

                activeChar.teleToLocation(Config.FARM_HARD_X, Config.FARM_HARD_Y, Config.FARM_HARD_Z);
                removeItem(activeChar, Config.FARM_HARD_TELEPORT_ITEM_ID, CoinCountFarmH, ".farm_hard");
                activeChar.sendMessage("You moved to the farm area");
        return true;
    }

    private boolean farm_low(Player activeChar) {
            final int CoinCount = Config.PRICE_FARM_LOW;
        if (teleportForbidden(activeChar)) return false;

        if (CoinCount != 0 && activeChar.getInventory().getItemByItemId(Config.FARM_LOW_TELEPORT_ITEM_ID).getCount() < CoinCount) {
                    activeChar.sendMessage("You do not have enough money");
                    activeChar.sendActionFailed();
                    return false;
                }

                activeChar.teleToLocation(Config.FARM_LOW_X, Config.FARM_LOW_Y, Config.FARM_LOW_Z);
                removeItem(activeChar, Config.FARM_LOW_TELEPORT_ITEM_ID, CoinCount, ".farm_low");
                activeChar.sendMessage("You moved to the farm area");
        return true;
    }

    private boolean teleportForbidden(Player activeChar) {
        if (activeChar.isCursedWeaponEquipped() || activeChar.getReflectionId() != 0 || activeChar.isDead() || activeChar.isAlikeDead() || activeChar.isCastingNow() || activeChar.isInCombat() || activeChar.isAttackingNow() || activeChar.isInOlympiadMode() || activeChar.isFlying() || activeChar.isTerritoryFlagEquipped() || activeChar.isInZone(ZoneType.no_escape) || activeChar.isInZone(ZoneType.SIEGE) || activeChar.isInZone(ZoneType.epic)) {
            activeChar.sendMessage("Teleportation is not possible");
            return true;
        }
        return false;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMAND_LIST;
    }

}