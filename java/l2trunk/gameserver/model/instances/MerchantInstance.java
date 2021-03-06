package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.BuyListHolder;
import l2trunk.gameserver.data.xml.holder.BuyListHolder.NpcTradeList;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.MapRegionHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.network.serverpackets.ExBuySellList;
import l2trunk.gameserver.network.serverpackets.ExGetPremiumItemList;
import l2trunk.gameserver.network.serverpackets.ShopPreviewList;
import l2trunk.gameserver.templates.mapregion.DomainArea;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;

public class MerchantInstance extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(MerchantInstance.class);

    public MerchantInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0)
            pom = "" + npcId;
        else
            pom = npcId + "-" + val;

        if (getTemplate().getHtmRoot() != null)
            return getTemplate().getHtmRoot() + pom + ".htm";

        String temp = "merchant/" + pom + ".htm";
        if (HtmCache.INSTANCE.getNullable(temp) != null)
            return temp;

        temp = "teleporter/" + pom + ".htm";
        if (HtmCache.INSTANCE.getNullable(temp) != null)
            return temp;

        temp = "petmanager/" + pom + ".htm";
        if (HtmCache.INSTANCE.getNullable(temp) != null)
            return temp;

        return "default/" + pom + ".htm";
    }

    private void showWearWindow(Player player, int val) {
        if (!player.getPlayerAccess().UseShop)
            return;

        NpcTradeList list = BuyListHolder.INSTANCE.getBuyList(val);

        if (list != null) {
            ShopPreviewList bl = new ShopPreviewList(list, player);
            player.sendPacket(bl);
        } else {
            _log.warn("no buylist with id:" + val);
            player.sendActionFailed();
        }
    }

    protected void showShopWindow(Player player, int listId, boolean tax) {
        if (!player.getPlayerAccess().UseShop)
            return;

        double taxRate = 0;

        if (tax) {
            Castle castle = getCastle(player);
            if (castle != null)
                taxRate = castle.getTaxRate();
        }

        NpcTradeList list = BuyListHolder.INSTANCE.getBuyList(listId);
        if (list == null || list.getNpcId() == getNpcId())
            player.sendPacket(new ExBuySellList.BuyList(list, player, taxRate), new ExBuySellList.SellRefundList(player, false));
        else {
            _log.warn("[L2MerchantInstance] possible client hacker: " + player.getName() + " attempting to buy from GM shop! < Ban him!");
            _log.warn("buylist id:" + listId + " / list_npc = " + list.getNpcId() + " / npc = " + getNpcId());
        }
    }

    void showShopWindow(Player player) {
        showShopWindow(player, 0, false);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken(); // Get actual command

        if ("Buy".equalsIgnoreCase(actualCommand) || "Sell".equalsIgnoreCase(actualCommand)) {
            int val = 0;
            if (st.countTokens() > 0)
                val = toInt(st.nextToken());
            showShopWindow(player, val, true);
        } else if ("Wear".equalsIgnoreCase(actualCommand)) {
            if (st.countTokens() < 1)
                return;
            int val = toInt(st.nextToken());
            showWearWindow(player, val);
        } else if ("Multisell".equalsIgnoreCase(actualCommand)) {
            if (st.countTokens() < 1)
                return;
            int val = toInt(st.nextToken());
            Castle castle = getCastle(player);
            MultiSellHolder.INSTANCE.SeparateAndSend(val, player, castle != null ? castle.getTaxRate() : 0);
        } else if (actualCommand.equalsIgnoreCase("ReceivePremium")) {
            if (player.getPremiumItemList().isEmpty()) {
                player.sendPacket(Msg.THERE_ARE_NO_MORE_VITAMIN_ITEMS_TO_BE_FOUND);
                return;
            }

            player.sendPacket(new ExGetPremiumItemList(player));
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public Castle getCastle(Player player) {
        if (Config.SERVICES_OFFSHORE_NO_CASTLE_TAX || (getReflection() == ReflectionManager.PARNASSUS && Config.SERVICES_PARNASSUS_NOTAX))
            return null;
        if (getReflection() == ReflectionManager.GIRAN_HARBOR || getReflection() == ReflectionManager.PARNASSUS) {
            String var = player.getVar("backCoords");
            if (var != null && !var.isEmpty()) {
                Location loc = Location.of(var);

                DomainArea domain = MapRegionHolder.getInstance().getRegionData(DomainArea.class, loc);
                if (domain != null)
                    return ResidenceHolder.getCastle(domain.getId());
            }

            return super.getCastle();
        }
        return super.getCastle(player);
    }

    @Override
    public boolean isMerchantNpc() {
        return true;
    }
}