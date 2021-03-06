package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.items.TradeItem;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.templates.manor.SeedProduction;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class ManorManagerInstance extends MerchantInstance {
    public ManorManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (this != player.getTarget()) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(objectId(), player.getLevel() - getLevel()), new ValidateLocation(this));
        } else {
            MyTargetSelected my = new MyTargetSelected(objectId(), player.getLevel() - getLevel());
            player.sendPacket(my);
            if (!isInRange(player, INTERACTION_DISTANCE)) {
                player.getAI().setIntentionInteract(CtrlIntention.AI_INTENTION_INTERACT, this);
                player.sendActionFailed();
            } else {
                if (CastleManorManager.INSTANCE.isDisabled()) {
                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setFile("npcdefault.htm");
                    html.replace("%objectId%", objectId());
                    html.replace("%npcname%", getName());
                    player.sendPacket(html);
                } else if (!player.isGM() // Player is not GM
                        && player.isClanLeader() // Player is clan leader of clan (then he is the lord)
                        && getCastle() != null // Verification of castle
                        && getCastle().getOwnerId() == player.getClanId() // Player's clan owning the castle
                )
                    showMessageWindow(player, "manager-lord.htm");
                else
                    showMessageWindow(player, "manager.htm");
                player.sendActionFailed();
            }
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("manor_menu_select")) { // input string format:
            // manor_menu_select?ask=X&state=Y&time=X
            if (CastleManorManager.INSTANCE.isUnderMaintenance()) {
                player.sendPacket(ActionFail.STATIC, Msg.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
                return;
            }

            String params = command.substring(command.indexOf("?") + 1);
            StringTokenizer st = new StringTokenizer(params, "&");
            int ask = Integer.parseInt(st.nextToken().split("=")[1]);
            int state = Integer.parseInt(st.nextToken().split("=")[1]);
            int time = Integer.parseInt(st.nextToken().split("=")[1]);

            Castle castle = getCastle();

            int castleId;
            if (state == -1) // info for current manor
                castleId = castle.getId();
            else
                // info for requested manor
                castleId = state;

            switch (ask) { // Main action
                case 1: // Seed purchase
                    if (castleId != castle.getId())
                        player.sendPacket(Msg._HERE_YOU_CAN_BUY_ONLY_SEEDS_OF_S1_MANOR);
                    else {
                        NpcTradeList tradeList = new NpcTradeList(0);
                        List<SeedProduction> seeds = castle.getSeedProduction(CastleManorManager.PERIOD_CURRENT);

                        for (SeedProduction s : seeds) {
                            TradeItem item = new TradeItem();
                            item.setItemId(s.getId());
                            item.setOwnersPrice(s.getPrice());
                            item.setCount(s.getCanProduce());
                            if (item.getCount() > 0 && item.getOwnersPrice() > 0)
                                tradeList.addItem(item);
                        }

                        BuyListSeed bl = new BuyListSeed(tradeList, castleId, player.getAdena());
                        player.sendPacket(bl);
                    }
                    break;
                case 2: // Crop sales
                    player.sendPacket(new ExShowSellCropList(player, castleId, castle.getCropProcure(CastleManorManager.PERIOD_CURRENT)));
                    break;
                case 3: // Current seeds (Manor info)
                    if (time == 1 && !ResidenceHolder.getCastle(castleId).isNextPeriodApproved())
                        player.sendPacket(new ExShowSeedInfo(castleId, List.of()));
                    else
                        player.sendPacket(new ExShowSeedInfo(castleId, ResidenceHolder.getCastle(castleId).getSeedProduction(time)));
                    break;
                case 4: // Current crops (Manor info)
                    if (time == 1 && !ResidenceHolder.getCastle(castleId).isNextPeriodApproved())
                        player.sendPacket(new ExShowCropInfo(castleId, List.of()));
                    else
                        player.sendPacket(new ExShowCropInfo(castleId, ResidenceHolder.getCastle(castleId).getCropProcure(time)));
                    break;
                case 5: // Basic info (Manor info)
                    player.sendPacket(new ExShowManorDefaultInfo());
                    break;
                case 6: // Buy harvester
                    showShopWindow(player, toInt("3" + getNpcId()), false);
                    break;
                case 9: // Edit sales (Crop sales)
                    player.sendPacket(new ExShowProcureCropDetail(state));
                    break;
            }
        } else if (command.startsWith("help")) {
            StringTokenizer st = new StringTokenizer(command, " ");
            st.nextToken(); // discard first
            String filename = "manor_client_help00" + st.nextToken() + ".htm";
            showMessageWindow(player, filename);
        } else
            super.onBypassFeedback(player, command);
    }

    private String getHtmlPath() {
        return "manormanager/";
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        return "manormanager/manager.htm"; // Used only in parent method
        // to return from "Territory status"
        // to initial screen.
    }

    private void showMessageWindow(Player player, String filename) {
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile(getHtmlPath() + filename);
        html.replace("%objectId%", objectId());
        html.replace("%npcId%", getNpcId());
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }
}