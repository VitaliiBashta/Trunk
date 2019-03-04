package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.TradeHelper;

import java.util.ArrayList;
import java.util.List;

public final class RequestPrivateStoreBuy extends L2GameClientPacket {
    private int _sellerId;
    private int _count;
    private List<Integer> _items; // object id
    private List<Long> _itemQ; // count
    private List<Long> _itemP; // price

    @Override
    protected void readImpl() {
        _sellerId = readD();
        _count = readD();
        if (_count * 20 > buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }

        _items = new ArrayList<>(_count);
        _itemQ = new ArrayList<>(_count);
        _itemP = new ArrayList<>(_count);

        for (int i = 0; i < _count; i++) {
            _items.add(readD());
            _itemQ.add(readQ());
            _itemP.add(readQ());

            if (_itemQ.get(i) < 1 || _itemP.get(i) < 1 || _items.indexOf(i) < i) {
                _count = 0;
                break;
            }
        }
    }

    @Override
    protected void runImpl() {
        Player buyer = getClient().getActiveChar();
        if (buyer == null || _count == 0)
            return;

        if (buyer.isActionsDisabled() || buyer.isBlocked() || !Config.ALLOW_PRIVATE_STORES) {
            buyer.sendActionFailed();
            return;
        }

        if (buyer.isInStoreMode()) {
            buyer.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (buyer.isInTrade()) {
            buyer.sendActionFailed();
            return;
        }

        if (buyer.isFishing()) {
            buyer.sendPacket(SystemMsg.YOU_CANNOT_DO_THAT_WHILE_FISHING_2);
            return;
        }

        if (!buyer.getPlayerAccess().UseTrade) {
            buyer.sendPacket(SystemMsg.SOME_LINEAGE_II_FEATURES_HAVE_BEEN_LIMITED_FOR_FREE_TRIALS_____);
            return;
        }

        Player seller = (Player) buyer.getVisibleObject(_sellerId);
        if (seller == null || seller.getPrivateStoreType() != Player.STORE_PRIVATE_SELL && seller.getPrivateStoreType() != Player.STORE_PRIVATE_SELL_PACKAGE || !seller.isInRangeZ(buyer, Creature.INTERACTION_DISTANCE)) {
            buyer.sendPacket(SystemMsg.THE_ATTEMPT_TO_TRADE_HAS_FAILED);
            buyer.sendActionFailed();
            return;
        }

        TradeHelper.buyFromStore(seller, buyer, _count, _items, _itemQ, _itemP);
    }
}