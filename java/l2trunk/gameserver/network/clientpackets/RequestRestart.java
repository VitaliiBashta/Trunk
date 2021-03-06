package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.PartyMatchingBBSManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2trunk.gameserver.network.GameClient.GameClientState;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.network.serverpackets.CharacterSelectionInfo;
import l2trunk.gameserver.network.serverpackets.RestartResponse;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public class RequestRestart extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();

        if (activeChar == null) {
            return;
        }

        if (activeChar.isInObserverMode()) {
            activeChar.sendPacket(SystemMsg.OBSERVERS_CANNOT_PARTICIPATE, RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }

        if (activeChar.isInCombat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_RESTART_WHILE_IN_COMBAT, RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2, RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }

        if (activeChar.isInJail()) {
            activeChar.standUp();
            activeChar.setBlock(false);
        }

        if (activeChar.isVarSet("isPvPevents")) {
            activeChar.sendMessage("You can follow any responses did not leave while participating in the event!");
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.Logout.Olympiad"));
            activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }

        if (activeChar.isInStoreMode() && !activeChar.isInZone(Zone.ZoneType.offshore)) {
            activeChar.sendMessage(new CustomMessage("trade.OfflineNoTradeZoneOnlyOffshore"));
            activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
            return;
        }
        // Prevent getPlayer from restarting if they are a festival participant
        // and it is in progress, otherwise notify party members that the getPlayer
        // is not longer a participant.
        if (activeChar.isFestivalParticipant()) {
            if (SevenSignsFestival.INSTANCE.isFestivalInitialized()) {
                activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestRestart.Festival"));
                activeChar.sendPacket(RestartResponse.FAIL, ActionFail.STATIC);
                return;
            }
        }

        if (PartyMatchingBBSManager.getInstance().partyMatchingPlayersList.contains(activeChar)) {
            PartyMatchingBBSManager.getInstance().partyMatchingPlayersList.remove(activeChar);
            PartyMatchingBBSManager.getInstance().partyMatchingDescriptionList.remove(activeChar.objectId());
        }

        if (getClient() != null) {
            getClient().setState(GameClientState.AUTHED);
        }
        activeChar.restart();
        // send char list
        CharacterSelectionInfo cl = new CharacterSelectionInfo(getClient().getLogin(), getClient().getSessionKey().playOkID1);
        sendPacket(RestartResponse.OK, cl);
        getClient().setCharSelection(cl.getCharInfo());
    }
}