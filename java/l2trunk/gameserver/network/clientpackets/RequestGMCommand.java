package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.*;

import java.util.List;

public final class RequestGMCommand extends L2GameClientPacket {
    private String _targetName;
    private int _command;

    @Override
    protected void readImpl() {
        _targetName = readS();
        _command = readD();
        // readD();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        Player target = World.getPlayer(_targetName);
        if (player == null || target == null)
            return;
        if (!player.getPlayerAccess().CanViewChar)
            return;

        switch (_command) {
            case 1:
                player.sendPacket(new GMViewCharacterInfo(target));
                player.sendPacket(new GMHennaInfo(target));
                break;
            case 2:
                if (target.getClan() != null)
                    player.sendPacket(new GMViewPledgeInfo(target));
                break;
            case 3:
                player.sendPacket(new GMViewSkillInfo(target));
                break;
            case 4:
                player.sendPacket(new GMViewQuestInfo(target));
                break;
            case 5:
                List<ItemInstance> items = target.getInventory().getItems();
                int questSize = (int) items.stream()
                        .filter(item -> item.getTemplate().isQuest())
                        .count();
                player.sendPacket(new GMViewItemList(target, items, items.size() - questSize));
                player.sendPacket(new ExGMViewQuestItemList(target, items, questSize));

                player.sendPacket(new GMHennaInfo(target));
                break;
            case 6:
                player.sendPacket(new GMViewWarehouseWithdrawList(target));
                break;
        }
    }
}