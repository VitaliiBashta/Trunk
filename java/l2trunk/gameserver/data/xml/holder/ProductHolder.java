package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.ProductItem;
import l2trunk.gameserver.model.ProductItemComponent;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toBoolean;
import static l2trunk.commons.lang.NumberUtils.toInt;

public final class ProductHolder {
    private static final Logger _log = LoggerFactory.getLogger(ProductHolder.class.getName());
    private static ProductHolder _instance = new ProductHolder();
    private final Map<Integer, ProductItem> _itemsList =new TreeMap<>();

    private ProductHolder() {
        try {
            Path file = Config.DATAPACK_ROOT.resolve("data/item-mall.xml");
            DocumentBuilderFactory factory1 = DocumentBuilderFactory.newInstance();
            factory1.setValidating(false);
            factory1.setIgnoringComments(true);
            Document doc1 = factory1.newDocumentBuilder().parse(file.toFile());

            for (Node n1 = doc1.getFirstChild(); n1 != null; n1 = n1.getNextSibling())
                if ("list".equalsIgnoreCase(n1.getNodeName()))
                    for (Node d1 = n1.getFirstChild(); d1 != null; d1 = d1.getNextSibling())
                        if ("product".equalsIgnoreCase(d1.getNodeName())) {
                            Node onSaleNode = d1.getAttributes().getNamedItem("on_sale");
                            boolean onSale = onSaleNode != null && toBoolean(onSaleNode.getNodeValue());
                            if (!onSale)
                                continue;

                            int productId = toInt(d1.getAttributes().getNamedItem("id").getNodeValue());

                            Node categoryNode = d1.getAttributes().getNamedItem("category");
                            int category = categoryNode != null ? toInt(categoryNode.getNodeValue()) : 5;

                            Node priceNode = d1.getAttributes().getNamedItem("price");
                            int price = priceNode != null ? toInt(priceNode.getNodeValue()) : 0;

                            Node isEventNode = d1.getAttributes().getNamedItem("is_event");
                            Boolean isEvent = isEventNode != null && toBoolean(isEventNode.getNodeValue());

                            Node isBestNode = d1.getAttributes().getNamedItem("is_best");
                            Boolean isBest = isBestNode != null && toBoolean(isBestNode.getNodeValue());

                            Node isNewNode = d1.getAttributes().getNamedItem("is_new");
                            Boolean isNew = isNewNode != null && toBoolean(isNewNode.getNodeValue());

                            int tabId = getProductTabId(isEvent, isBest, isNew);

                            Node startTimeNode = d1.getAttributes().getNamedItem("sale_start_date");
                            long startTimeSale = startTimeNode != null ? getMillisecondsFromString(startTimeNode.getNodeValue()) : 0;

                            Node endTimeNode = d1.getAttributes().getNamedItem("sale_end_date");
                            long endTimeSale = endTimeNode != null ? getMillisecondsFromString(endTimeNode.getNodeValue()) : 0;

                            ArrayList<ProductItemComponent> components = new ArrayList<>();
                            ProductItem pr = new ProductItem(productId, category, price, tabId, startTimeSale, endTimeSale);
                            for (Node t1 = d1.getFirstChild(); t1 != null; t1 = t1.getNextSibling())
                                if ("component".equalsIgnoreCase(t1.getNodeName())) {
                                    int item_id = toInt(t1.getAttributes().getNamedItem("item_id").getNodeValue());
                                    int count = toInt(t1.getAttributes().getNamedItem("count").getNodeValue());
                                    ProductItemComponent component = new ProductItemComponent(item_id, count);
                                    components.add(component);
                                }

                            pr.setComponents(components);
                            _itemsList.put(productId, pr);
                        }

            _log.info(String.format("ProductItemTable: Loaded %d product item on sale.", _itemsList.size()));
        } catch (DOMException | IOException | NumberFormatException | ParserConfigurationException e) {
            _log.warn("ProductItemTable: Lists could not be initialized.", e);
        } catch (SAXException e) {
            _log.warn("ProductItemTable: Lists could not be initialized. SAXException: ", e);
        }
    }

    public static ProductHolder getInstance() {
        if (_instance == null)
            _instance = new ProductHolder();
        return _instance;
    }

    private static int getProductTabId(boolean isEvent, boolean isBest, boolean isNew) {
        //TODO: Заюзать isNew
        if (isEvent && isBest)
            return 3;

        if (isEvent)
            return 1;

        if (isBest)
            return 2;

        return 4;
    }

    private static long getMillisecondsFromString(String datetime) {
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        try {
            Date time = df.parse(datetime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(time);

            return calendar.getTimeInMillis();
        } catch (ParseException e) {
            _log.error("Error while gettingMillisecondsFromString ", e);
        }

        return 0L;
    }

    public void reload() {
        _instance = new ProductHolder();
    }

    public Collection<ProductItem> getAllItems() {
        return _itemsList.values();
    }

    public ProductItem getProduct(int id) {
        return _itemsList.get(id);
    }
}
