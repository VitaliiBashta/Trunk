package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.math.SafeMath;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.PcFreight;
import l2trunk.gameserver.model.items.PcInventory;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.List;

public final class RequestPackageSend extends L2GameClientPacket {
    private static final long _FREIGHT_FEE = 1000;

    private int _objectId;
    private int _count;
    private List<Integer> _items = new ArrayList<>();
    private List<Long> _itemQ = new ArrayList<>();

    @Override
    protected void readImpl() {
        _objectId = readD();
        _count = readD();
        if (((_count * 12) > _buf.remaining()) || (_count > Short.MAX_VALUE) || (_count < 1)) {
            _count = 0;
            return;
        }

        for (int i = 0; i < _count; i++) {
            int id = readD();
            long q = readQ();
            _items.add(id);
            _itemQ.add(q);
            if ((q < 1) || (_items.indexOf(id) < i)) {
                _count = 0;
                return;
            }
        }
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if ((player == null) || (_count == 0)) {
            return;
        }

        if (!player.getPlayerAccess().UseWarehouse) {
            player.sendActionFailed();
            return;
        }

        if (player.isActionsDisabled()) {
            player.sendActionFailed();
            return;
        }

        if (player.isInStoreMode()) {
            player.sendPacket(SystemMsg.WHILE_OPERATING_A_PRIVATE_STORE_OR_WORKSHOP_YOU_CANNOT_DISCARD_DESTROY_OR_TRADE_AN_ITEM);
            return;
        }

        if (player.isInTrade()) {
            player.sendActionFailed();
            return;
        }

        // To detect the npc and distance
        NpcInstance whkeeper = player.getLastNpc();
        if (!player.isInRangeZ(whkeeper, Creature.INTERACTION_DISTANCE)) {
            return;
        }

        if (!player.getAccountChars().containsKey(_objectId)) {
            return;
        }

        PcInventory inventory = player.getInventory();
        PcFreight freight = new PcFreight(_objectId);
        freight.restore();

        inventory.writeLock();
        freight.writeLock();
        try {
            int slotsleft;
            long adenaDeposit = 0;

            slotsleft = Config.FREIGHT_SLOTS - freight.getSize();

            int items = 0;

            // Create a new list of items passed on the basis of the data
            for (int i = 0; i < _count; i++) {
                ItemInstance item = inventory.getItemByObjectId(_items.get(i));
                if ((item == null) || (item.getCount() < _itemQ.get(i)) || !item.getTemplate().isFreightable()) {
                    _items.set(i, 0); // Null, a thing not to be transferred
                    _itemQ.set(i, 0L);
                    continue;
                }

                if (!item.isStackable() || (freight.getItemByItemId(item.getItemId()) == null)) // вещь требует слота
                {
                    if (slotsleft <= 0) // если слоты кончились нестекуемые вещи и отсутствующие стекуемые пропускаем
                    {
                        _items.set(i, 0); // Обнуляем, вещь не будет передана
                        _itemQ.set(i, 0L);
                        continue;
                    }
                    slotsleft--; // если слот есть то его уже нет
                }

                if (item.getItemId() == ItemTemplate.ITEM_ID_ADENA) {
                    adenaDeposit = _itemQ.get(i);
                }

                items++;
            }

            // Сообщаем о том, что слоты кончились
            if (slotsleft <= 0) {
                player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            }

            if (items == 0) {
                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
                return;
            }

            // Проверяем, хватит ли у нас денег на уплату налога
            long fee = SafeMath.mulAndCheck(items, _FREIGHT_FEE);

            if ((fee + adenaDeposit) > player.getAdena()) {
                player.sendPacket(SystemMsg.YOU_LACK_THE_FUNDS_NEEDED_TO_PAY_FOR_THIS_TRANSACTION);
                return;
            }

            if (!player.reduceAdena(fee, true, "Freight")) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }

            for (int i = 0; i < _count; i++) {
                if (_items.get(i) == 0) {
                    continue;
                }
                ItemInstance item = inventory.removeItemByObjectId(_items.get(i), _itemQ.get(i), "Freight");
                freight.addItem(item, "Freight " + player.toString(), "Freight");
            }
        } catch (ArithmeticException ae) {
            // TODO audit
            player.sendPacket(SystemMsg.YOU_HAVE_EXCEEDED_THE_QUANTITY_THAT_CAN_BE_INPUTTED);
            return;
        } finally {
            freight.writeUnlock();
            inventory.writeUnlock();
        }

        // Обновляем параметры персонажа
        player.sendChanges();
        player.sendPacket(SystemMsg.THE_TRANSACTION_IS_COMPLETE);
    }
}
