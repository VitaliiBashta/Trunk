package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ExchangeItemHolder;
import l2trunk.gameserver.model.exchange.Change;
import l2trunk.gameserver.model.exchange.Variant;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum ExchangeItemParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/exchange.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + ExchangeItemHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("change"); iterator.hasNext(); ) {
            Element change_data = iterator.next();
            int id = toInt(change_data.attributeValue("id"));
            String name = change_data.attributeValue("name");
            String icon = change_data.attributeValue("icon");
            int cost_id = toInt(change_data.attributeValue("cost_id"));
            long cost_count = Long.parseLong(change_data.attributeValue("cost_count"));
            boolean attribute_change = Boolean.parseBoolean(change_data.attributeValue("attribute_change"));
            boolean is_upgrade = Boolean.parseBoolean(change_data.attributeValue("is_upgrade"));

            ExchangeItemHolder.addChanges(new Change(id, name, icon, cost_id, cost_count, attribute_change, is_upgrade, parseVariants(change_data)));
        }
    }

    private List<Variant> parseVariants(Element n) {
        List<Variant> list = new ArrayList<>();
        for (Iterator<Element> iterator = n.elementIterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            if ("variant".equalsIgnoreCase(element.getName())) {
                int number = toInt(element.attributeValue("number"));
                int id = toInt(element.attributeValue("id"));
                String name = element.attributeValue("name");
                String icon = element.attributeValue("icon");

                list.add(new Variant(number, id, name, icon));
            }
        }
        return list;
    }
}
