package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.crypt.CryptUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.items.TradeItem;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum BuyListHolder {
    INSTANCE;
    private final Logger _log = LoggerFactory.getLogger(BuyListHolder.class);
    private final Map<Integer, NpcTradeList> lists = new HashMap<>();

    public void init() {
        try {
            Path filelists = Config.DATAPACK_ROOT.resolve("data/merchant_filelists.xml");
            DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
            factory1.setValidating(false);
            factory1.setIgnoringComments(true);
            Document doc1 = factory1.newDocumentBuilder().parse(CryptUtil.decryptOnDemand(filelists));

            int counterFiles = 0;
            int counterItems = 0;
            for (Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
                if ("list".equalsIgnoreCase(n1.getNodeName()))
                    for (Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
                        if ("file".equalsIgnoreCase(d1.getNodeName())) {
                            final String filename = d1.getAttributes().getNamedItem("name").getNodeValue();

                            Path file = Config.DATAPACK_ROOT.resolve("data/" + filename);
                            DocumentBuilderFactory factory2 = DocumentBuilderFactory.newInstance();
                            factory2.setValidating(false);
                            factory2.setIgnoringComments(true);
                            Document doc2 = factory2.newDocumentBuilder().parse(CryptUtil.decryptOnDemand(file));
                            counterFiles++;

                            for (Node n2 = doc2.getFirstChild(); n2 != null; n2 = n2.getNextSibling())
                                if ("list".equalsIgnoreCase(n2.getNodeName()))
                                    for (Node d2 = n2.getFirstChild(); d2 != null; d2 = d2.getNextSibling())
                                        if ("tradelist".equalsIgnoreCase(d2.getNodeName())) {
                                            String[] npcs = d2.getAttributes().getNamedItem("npc").getNodeValue().split(";");
                                            String[] shopIds = d2.getAttributes().getNamedItem("shop").getNodeValue().split(";");
                                            String[] markups = new String[0];
                                            boolean haveMarkups = false;
                                            if (d2.getAttributes().getNamedItem("markup") != null) {
                                                markups = d2.getAttributes().getNamedItem("markup").getNodeValue().split(";");
                                                haveMarkups = true;
                                            }

                                            int size = npcs.length;
                                            if (!haveMarkups) {
                                                markups = new String[size];
                                                for (int i = 0; i < size; i++)
                                                    markups[i] = "0";
                                            }

                                            if (shopIds.length != size || markups.length != size) {
                                                _log.warn("Do not correspond to the size of arrays");
                                                continue;
                                            }

                                            for (int n = 0; n < size; n++) {
                                                final int npc_id = Integer.parseInt(npcs[n]);
                                                final int shop_id = Integer.parseInt(shopIds[n]);
                                                final double markup = npc_id > 0 ? 1. + Double.parseDouble(markups[n]) / 100. : 0.;
                                                NpcTradeList tl = new NpcTradeList(shop_id);
                                                tl.setNpcId(npc_id);
                                                for (Node i = d2.getFirstChild(); i != null; i = i.getNextSibling())
                                                    if ("item".equalsIgnoreCase(i.getNodeName())) {
                                                        final int itemId = Integer.parseInt(i.getAttributes().getNamedItem("id").getNodeValue());
                                                        final ItemTemplate template = ItemHolder.getTemplate(itemId);
                                                        if (template == null) {
                                                            _log.warn("Template not found for itemId: " + itemId + " for shop " + shop_id);
                                                            continue;
                                                        }
                                                        if (!checkItem(template))
                                                            continue;
                                                        counterItems++;

                                                        long price = i.getAttributes().getNamedItem("price") != null ? Long.parseLong(i.getAttributes().getNamedItem("price").getNodeValue()) : Math.round(template.referencePrice * markup);
                                                        TradeItem item = new TradeItem();
                                                        item.setItemId(itemId);
                                                        final int itemCount = i.getAttributes().getNamedItem("count") != null ? Integer.parseInt(i.getAttributes().getNamedItem("count").getNodeValue()) : 0;
                                                        // Время респауна задается минутах
                                                        final int itemRechargeTime = i.getAttributes().getNamedItem("time") != null ? Integer.parseInt(i.getAttributes().getNamedItem("time").getNodeValue()) : 0;
                                                        item.setOwnersPrice(price);
                                                        item.setCount(itemCount);
                                                        item.setCurrentValue(itemCount);
                                                        item.setLastRechargeTime((int) (System.currentTimeMillis() / 60000));
                                                        item.setRechargeTime(itemRechargeTime);
                                                        tl.addItem(item);
                                                    }
                                                lists.put(shop_id, tl);
                                            }
                                        }
                        }

            _log.info("TradeController: Loaded " + counterFiles + " file(s).");
            _log.info("TradeController: Loaded " + counterItems + " Items.");
            _log.info("TradeController: Loaded " + lists.size() + " Buylists.");
        } catch (DOMException | IOException | NumberFormatException | ParserConfigurationException | SAXException e) {
            _log.warn("TradeController: Buy lists could not be initialized.", e);
        }
    }

    public void reload() {
        init();
    }

    private boolean checkItem(ItemTemplate template) {
        if (template.isCommonItem() && !Config.ALT_ALLOW_SELL_COMMON)
            return false;
        if (template.isEquipment() && !template.isForPet() && Config.ALT_SHOP_PRICE_LIMITS.size() > 0)
            for (int i = 0; i < Config.ALT_SHOP_PRICE_LIMITS.size(); i += 2)
                if (template.getBodyPart() == Config.ALT_SHOP_PRICE_LIMITS.get(i)) {
                    if (template.referencePrice > Config.ALT_SHOP_PRICE_LIMITS.get(i + 1))
                        return false;
                    break;
                }
        if (Config.ALT_SHOP_UNALLOWED_ITEMS.size() > 0)
            return Config.ALT_SHOP_UNALLOWED_ITEMS.stream()
                    .noneMatch(i -> template.itemId() == i);
        return true;
    }

    public NpcTradeList getBuyList(int listId) {
        return lists.get(listId);
    }

    public static class NpcTradeList {
        private final List<TradeItem> tradeList = new ArrayList<>();
        private final int _id;
        private int _npcId;

        public NpcTradeList(int id) {
            _id = id;
        }

        public int getListId() {
            return _id;
        }

        public int getNpcId() {
            return _npcId;
        }

        void setNpcId(int id) {
            _npcId = id;
        }

        public void addItem(TradeItem ti) {
            tradeList.add(ti);
        }

        public synchronized List<TradeItem> getItems() {
            List<TradeItem> result = new ArrayList<>();
            long currentTime = System.currentTimeMillis() / 60000L;
            for (TradeItem ti : tradeList) {
                // А не пора ли обновить количество лимитированных предметов в трейд листе?
                if (ti.isCountLimited()) {
                    if (ti.getCurrentValue() < ti.getCount() && ti.getLastRechargeTime() + ti.getRechargeTime() <= currentTime) {
                        ti.setLastRechargeTime(ti.getLastRechargeTime() + ti.getRechargeTime());
                        ti.setCurrentValue(ti.getCount());
                    }

                    if (ti.getCurrentValue() == 0)
                        continue;
                }

                result.add(ti);
            }

            return result;
        }

        public TradeItem getItemByItemId(int itemId) {
            return tradeList.stream()
                    .filter(ti -> ti.getItemId() == itemId)
                    .findFirst().orElse(null);
        }

        public synchronized void updateItems(List<TradeItem> buyList) {
            buyList.forEach(ti -> {
                TradeItem ic = getItemByItemId(ti.getItemId());
                if (ic.isCountLimited())
                    ic.setCurrentValue(Math.max(ic.getCurrentValue() - ti.getCount(), 0));
            });
        }
    }
}