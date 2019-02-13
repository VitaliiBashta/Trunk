package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Request;
import l2trunk.gameserver.model.Request.L2RequestType;
import l2trunk.gameserver.network.serverpackets.SendTradeRequest;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.TradeStart;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Util;

import java.util.concurrent.CopyOnWriteArrayList;

public final class TradeRequest extends L2GameClientPacket {
    //Format: cd
    private int _objectId;

    @Override
    protected void readImpl() {
        _objectId = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (activeChar.isOutOfControl()) {
            activeChar.sendActionFailed();
            return;
        }

        if (!activeChar.getPlayerAccess().UseTrade) {
            activeChar.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_);
            activeChar.sendActionFailed();
            return;
        }

        if (activeChar.isInStoreMode()) {
            activeChar.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (activeChar.isFishing()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING);
            return;
        }

        if (activeChar.isInTrade()) {
            activeChar.sendPacket(SystemMsg.YOU_ARE_ALREADY_TRADING_WITH_SOMEONE);
            return;
        }

        if (activeChar.isInAwayingMode()) {
            activeChar.sendMessage(new CustomMessage("Away.ActionFailed", new Object[0]));
            return;
        }

        if (activeChar.isProcessingRequest()) {
            activeChar.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
            return;
        }

        long tradeBan = activeChar.getVarLong("tradeBan");
        if ((tradeBan == -1) || tradeBan >= System.currentTimeMillis()) {
            if (tradeBan ==-1)
                activeChar.sendMessage(new CustomMessage("common.TradeBannedPermanently"));
            else
                activeChar.sendMessage(new CustomMessage("common.TradeBanned").addString(Util.formatTime((int) (tradeBan / 1000L - System.currentTimeMillis() / 1000L))));
            return;
        }

        GameObject target = activeChar.getVisibleObject(_objectId);
        if (!(target instanceof Player) || target == activeChar) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        if (!activeChar.isInRangeZ(target, Creature.INTERACTION_DISTANCE)) {
            activeChar.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return;
        }

        Player reciever = (Player) target;
        if (!reciever.getPlayerAccess().UseTrade) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        if (reciever.isInCombat()) {
            activeChar.sendMessage("Target cannot trade right now!");
            return;
        }

        tradeBan = reciever.getVarLong("tradeBan");
        if (tradeBan != 0 && (tradeBan ==-1 || tradeBan >= System.currentTimeMillis())) {
            activeChar.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return;
        }

        if (reciever.isInBlockList(activeChar)) {
            activeChar.sendPacket(SystemMsg.YOU_HAVE_BEEN_BLOCKED_FROM_CHATTING_WITH_THAT_CONTACT);
            return;
        }

        if (reciever.getTradeRefusal() || reciever.isBusy()) {
            activeChar.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addString(reciever.getName()));
            return;
        }

        if (reciever.isInAwayingMode()) {
            reciever.sendMessage(new CustomMessage("Away.ActionFailed", new Object[0]));
            return;
        }

        if (activeChar.isGM() && activeChar.isInvisible())//Automatically starting trade if activeChar is GM in invisible mode
        {
            new Request(L2RequestType.TRADE, activeChar, reciever);
            reciever.setTradeList(new CopyOnWriteArrayList<>());
            reciever.sendPacket(new SystemMessage2(SystemMsg.YOU_BEGIN_TRADING_WITH_C1).addString(activeChar.getName()), new TradeStart(reciever, activeChar));
            activeChar.setTradeList(new CopyOnWriteArrayList<>());
            activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_BEGIN_TRADING_WITH_C1).addString(reciever.getName()), new TradeStart(activeChar, reciever));
        } else {
            new Request(L2RequestType.TRADE_REQUEST, activeChar, reciever).setTimeout(10000L);
            reciever.sendPacket(new SendTradeRequest(activeChar.objectId()));
            activeChar.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_REQUESTED_A_TRADE_WITH_C1).addString(reciever.getName()));
        }
    }
}