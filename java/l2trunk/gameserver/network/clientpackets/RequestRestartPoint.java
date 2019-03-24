package l2trunk.gameserver.network.clientpackets;

import Elemental.managers.GmEventManager;
import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.lang.Pair;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.RestartType;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.entity.residence.ResidenceFunction;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.network.serverpackets.Die;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.TeleportUtils;

public final class RequestRestartPoint extends L2GameClientPacket {
    private RestartType restartType;

    private static Location defaultLoc(RestartType restartType, Player activeChar) {
        Location loc = null;
        Clan clan = activeChar.getClan();

        switch (restartType) {
            case TO_CLANHALL:
                if (clan != null && clan.getHasHideout() != 0) {
                    ClanHall clanHall = activeChar.getClanHall();
                    loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_CLANHALL);
                    if (clanHall.getFunction(ResidenceFunction.RESTORE_EXP) != null)
                        activeChar.restoreExp(clanHall.getFunction(ResidenceFunction.RESTORE_EXP).getLevel());
                }
                break;
            case TO_CASTLE:
                if (clan != null && clan.getCastle() != 0) {
                    Castle castle = activeChar.getCastle();
                    loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_CASTLE);
                    if (castle.getFunction(ResidenceFunction.RESTORE_EXP) != null)
                        activeChar.restoreExp(castle.getFunction(ResidenceFunction.RESTORE_EXP).getLevel());
                }
                break;
            case TO_FORTRESS:
                if (clan != null && clan.getHasFortress() != 0) {
                    Fortress fort = activeChar.getFortress();
                    loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_FORTRESS);
                    if (fort.getFunction(ResidenceFunction.RESTORE_EXP) != null)
                        activeChar.restoreExp(fort.getFunction(ResidenceFunction.RESTORE_EXP).getLevel());
                }
                break;
            case TO_VILLAGE:
            default:
                loc = TeleportUtils.getRestartLocation(activeChar, RestartType.TO_VILLAGE);
                break;
        }
        return loc;
    }

    @Override
    protected void readImpl() {
        restartType = ArrayUtils.valid(RestartType.VALUES, readD());
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();

        if (restartType == null || player == null)
            return;

        if (player.isFakeDeath()) {
            player.breakFakeDeath();
            return;
        }

        if (!player.isDead() && !player.isGM()) {
            player.sendActionFailed();
            return;
        }

        // Ady - If the getPlayer is in a Gm Event check if it can resurrect
        if (!GmEventManager.INSTANCE.canResurrect(player)) {
            return;
        }

        if (player.isFestivalParticipant()) {
            player.doRevive();
            return;
        }

        switch (restartType) {
            case AGATHION:
                if (player.isAgathionResAvailable())
                    player.doRevive(100);
                else
                    player.sendPacket(ActionFail.STATIC, new Die(player));
                break;
            case FIXED:
                if (player.getPlayerAccess().ResurectFixed)
                    player.doRevive(100);
                else if (ItemFunctions.removeItem(player, 13300, 1, "RequestRestartPoint") == 1) {
                    player.sendPacket(SystemMsg.YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT);
                    player.doRevive(100);
                } else if (ItemFunctions.removeItem(player, 10649, 1, "RequestRestartPoint") == 1) {
                    player.sendPacket(SystemMsg.YOU_HAVE_USED_THE_FEATHER_OF_BLESSING_TO_RESURRECT);
                    player.doRevive(100);
                } else
                    player.sendPacket(ActionFail.STATIC, new Die(player));
                break;
            default:
                Location loc = null;
                Reflection ref = player.getReflection();

                if (ref == ReflectionManager.DEFAULT)
                    for (GlobalEvent e : player.getEvents())
                        loc = e.getRestartLoc(player, restartType);

                if (loc == null)
                    loc = defaultLoc(restartType, player);

                if (loc != null) {
                    Pair<Integer, OnAnswerListener> ask = player.getAskListener(false);
                    if (ask != null && ask.getValue() instanceof ReviveAnswerListener && !((ReviveAnswerListener) ask.getValue()).isForPet())
                        player.getAskListener(true);

                    player.setPendingRevive(true);
                    player.teleToLocation(loc, ReflectionManager.DEFAULT);
                } else
                    player.sendPacket(ActionFail.STATIC, new Die(player));
                break;
        }
    }
}
