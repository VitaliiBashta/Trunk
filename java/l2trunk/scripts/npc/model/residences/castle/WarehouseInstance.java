package l2trunk.scripts.npc.model.residences.castle;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.WarehouseFunctions;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public class WarehouseInstance extends NpcInstance {
    private static final int COND_ALL_FALSE = 0;
    private static final int COND_BUSY_BECAUSE_OF_SIEGE = 1;
    private static final int COND_OWNER = 2;

    private static final int ITEM_BLOOD_ALLI = 9911; // Blood Alliance
    private static final int ITEM_BLOOD_OATH = 9910; // Blood Oath

    public WarehouseInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ((player.getClanPrivileges() & Clan.CP_CS_USE_FUNCTIONS) != Clan.CP_CS_USE_FUNCTIONS) {
            player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return;
        }

        if (player.getEnchantScroll() != null) {
            Log.add("Player " + player.getName() + " trying to use enchant exploit[CastleWarehouse], ban this getPlayer!", "illegal-actions");
            player.kick();
            return;
        }

        if (command.startsWith("WithdrawP")) {
            int val = Integer.parseInt(command.substring(10));
            if (val == 99) {
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile("warehouse/personal.htm");
                player.sendPacket(html);
            } else
                WarehouseFunctions.showRetrieveWindow(player, val);
        } else if (command.equals("DepositP"))
            WarehouseFunctions.showDepositWindow(player);
        else if (command.startsWith("WithdrawC")) {
            int val = Integer.parseInt(command.substring(10));
            if (val == 99) {
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile("warehouse/clan.htm");
                player.sendPacket(html);
            } else
                WarehouseFunctions.showWithdrawWindowClan(player, val);
        } else if (command.equals("DepositC"))
            WarehouseFunctions.showDepositWindowClan(player);
        else if ("CheckHonoraryItems".equalsIgnoreCase(command)) {
            String filename;
            if (!player.isClanLeader())
                filename = "castle/warehouse/castlewarehouse-notcl.htm";
            else
                filename = "castle/warehouse/castlewarehouse-5.htm";

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile(filename);
            html.replace("%total_items%", getCastle().getRewardCount());
            player.sendPacket(html);
        } else if (command.equalsIgnoreCase("ExchangeBloodAlli")) {
            if (!player.isClanLeader()) {
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile("castle/warehouse/castlewarehouse-notcl.htm");
                player.sendPacket(html);
            } else if (removeItem(player, ITEM_BLOOD_ALLI, 1, "ExchangeBloodAlli") == 0)
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            else
                addItem(player, ITEM_BLOOD_OATH, 30, "ExchangeBloodAlli");
        } else if (command.equalsIgnoreCase("ReciveBloodAlli")) {
            Castle castle = getCastle();
            String filename;

            int count = castle.getRewardCount();
            if (!player.isClanLeader())
                filename = "castle/warehouse/castlewarehouse-notcl.htm";
            else if (count > 0) {
                filename = "castle/warehouse/castlewarehouse-3.htm";

                castle.setRewardCount(0);
                castle.setJdbcState(JdbcEntityState.UPDATED);
                castle.update();

                addItem(player, ITEM_BLOOD_ALLI, count);
            } else
                filename = "castle/warehouse/castlewarehouse-4.htm";

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile(filename);
            player.sendPacket(html);
        } else if (command.startsWith("Chat")) {
            int val = 0;
            try {
                val = Integer.parseInt(command.substring(5));
            } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
            }
            showChatWindow(player, val);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        player.sendActionFailed();
        String filename = "castle/warehouse/castlewarehouse-no.htm";

        int condition = validateCondition(player);
        if (condition > COND_ALL_FALSE)
            if (condition == COND_BUSY_BECAUSE_OF_SIEGE)
                filename = "castle/warehouse/castlewarehouse-busy.htm"; // Busy because of siege
            else if (condition == COND_OWNER)
                if (val == 0)
                    filename = "castle/warehouse/castlewarehouse.htm";
                else
                    filename = "castle/warehouse/castlewarehouse-" + val + ".htm";

        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile(filename);
        player.sendPacket(html);
    }

    private int validateCondition(Player player) {
        if (player.isGM())
            return COND_OWNER;
        if (getCastle() != null && getCastle().getId() > 0)
            if (player.getClan() != null)
                if (getCastle().getSiegeEvent().isInProgress())
                    return COND_BUSY_BECAUSE_OF_SIEGE; // Busy because of siege
                else if (getCastle().getOwnerId() == player.getClanId()) // Clan owns castle
                    return COND_OWNER;
        return COND_ALL_FALSE;
    }
}