package l2trunk.gameserver.instancemanager.itemauction;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.time.cron.SchedulingPattern;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public enum ItemAuctionManager {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(ItemAuctionManager.class);

    private final Map<Integer, ItemAuctionInstance> _managerInstances = new HashMap<>();
    private final AtomicInteger _nextId = new AtomicInteger();

    public void init() {
        LOG.info("Initializing ItemAuctionManager");
        if (Config.ALT_ITEM_AUCTION_ENABLED)
            load();
    }

    private void load() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT auctionId FROM item_auction ORDER BY auctionId DESC LIMIT 0, 1");
             ResultSet rset = statement.executeQuery()) {
            if (rset.next())
                _nextId.set(rset.getInt(1));
        } catch (SQLException e) {
            LOG.error("ItemAuctionManager: Failed loading auctions.", e);
        }

        Path file = Config.DATAPACK_ROOT.resolve("data/item_auctions.xml");
        if (!Files.exists(file)) {
            LOG.warn("ItemAuctionManager: Missing item_auctions.xml!");
            return;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document doc = builder.parse(file.toFile());
            for (Node na = doc.getFirstChild(); na != null; na = na.getNextSibling())
                if ("list".equalsIgnoreCase(na.getNodeName()))
                    for (Node nb = na.getFirstChild(); nb != null; nb = nb.getNextSibling())
                        if ("instance".equalsIgnoreCase(nb.getNodeName())) {
                            NamedNodeMap nab = nb.getAttributes();
                            int instanceId = Integer.parseInt(nab.getNamedItem("id").getNodeValue());

                            if (_managerInstances.containsKey(instanceId))
                                throw new IllegalArgumentException("Duplicate instanceId " + instanceId);

                            SchedulingPattern dateTime = new SchedulingPattern(nab.getNamedItem("schedule").getNodeValue());

                            List<AuctionItem> items = new ArrayList<>();

                            for (Node nc = nb.getFirstChild(); nc != null; nc = nc.getNextSibling())
                                if ("item".equalsIgnoreCase(nc.getNodeName())) {
                                    NamedNodeMap nac = nc.getAttributes();
                                    int auctionItemId = Integer.parseInt(nac.getNamedItem("auctionItemId").getNodeValue());
                                    int auctionLenght = Integer.parseInt(nac.getNamedItem("auctionLenght").getNodeValue());
                                    long auctionInitBid = Integer.parseInt(nac.getNamedItem("auctionInitBid").getNodeValue());

                                    int itemId = Integer.parseInt(nac.getNamedItem("itemId").getNodeValue());
                                    int itemCount = Integer.parseInt(nac.getNamedItem("itemCount").getNodeValue());
                                    boolean altByItem = Boolean.parseBoolean(nac.getNamedItem("altByItem").getNodeValue());

                                    if (auctionLenght < 1)
                                        throw new IllegalArgumentException("auctionLenght < 1 for instanceId: " + instanceId + ", itemId " + itemId);

                                    for (AuctionItem tmp : items)
                                        if (tmp.getAuctionItemId() == auctionItemId)
                                            throw new IllegalArgumentException("Dublicated auction item id " + auctionItemId + "for instanceId: " + instanceId);

                                    StatsSet itemExtra = new StatsSet();
                                    for (Node nd = nc.getFirstChild(); nd != null; nd = nd.getNextSibling())
                                        if ("extra".equalsIgnoreCase(nd.getNodeName())) {
                                            NamedNodeMap nad = nd.getAttributes();
                                            for (int i = nad.getLength(); i-- > 0; ) {
                                                Node n = nad.item(i);
                                                if (n != null)
                                                    itemExtra.set(n.getNodeName(), n.getNodeValue());
                                            }
                                        }

                                    AuctionItem item = new AuctionItem(auctionItemId, auctionLenght, auctionInitBid, itemId, itemCount, altByItem, itemExtra);
                                    items.add(item);
                                }

                            if (items.isEmpty())
                                throw new IllegalArgumentException("No items defined for instanceId: " + instanceId);

                            ItemAuctionInstance instance = new ItemAuctionInstance(instanceId, dateTime, items);
                            _managerInstances.put(instanceId, instance);
                        }

            LOG.info("ItemAuctionManager: Loaded " + _managerInstances.size() + " instance(s).");
        } catch (NumberFormatException e) {
            LOG.error("ItemAuctionManager: NumberFormatException while loading ItemAuctions.xml!", e);
        } catch (DOMException | IllegalArgumentException | SchedulingPattern.InvalidPatternException | ParserConfigurationException | SAXException e) {
            LOG.error("ItemAuctionManager: Error while loading ItemAuctions.xml!", e);
        } catch (IOException e) {
            LOG.error("ItemAuctionManager: IOException while loading ItemAuctions.xml!", e);
        }
    }

    public void shutdown() {
        _managerInstances.values().forEach(ItemAuctionInstance::shutdown);
    }

    public ItemAuctionInstance getManagerInstance(int instanceId) {
        return _managerInstances.get(instanceId);
    }

    public int getNextId() {
        return _nextId.incrementAndGet();
    }

    void deleteAuction(int auctionId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            PreparedStatement statement = con.prepareStatement("DELETE FROM item_auction WHERE auctionId=?");
            statement.setInt(1, auctionId);
            statement.execute();
            statement.close();

            statement = con.prepareStatement("DELETE FROM item_auction_bid WHERE auctionId=?");
            statement.setInt(1, auctionId);
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            LOG.error("ItemAuctionManager: Failed deleting auction: " + auctionId, e);
        }
    }
}