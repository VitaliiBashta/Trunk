package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ExchangeItemHolder;
import l2trunk.gameserver.model.exchange.Change;
import l2trunk.gameserver.model.exchange.Variant;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ExchangeItemParser extends AbstractFileParser<ExchangeItemHolder> {
    private static final ExchangeItemParser _instance = new ExchangeItemParser();

    private ExchangeItemParser() {
        super(ExchangeItemHolder.getInstance());
    }

    public static ExchangeItemParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/exchange.xml");
    }

    @Override
    public String getDTDFileName() {
        return "exchange.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("change"); iterator.hasNext(); ) {
            Element change_data = iterator.next();
            int id = Integer.parseInt(change_data.attributeValue("id"));
            String name = change_data.attributeValue("name");
            String icon = change_data.attributeValue("icon");
            int cost_id = Integer.parseInt(change_data.attributeValue("cost_id"));
            long cost_count = Long.parseLong(change_data.attributeValue("cost_count"));
            boolean attribute_change = Boolean.parseBoolean(change_data.attributeValue("attribute_change"));
            boolean is_upgrade = Boolean.parseBoolean(change_data.attributeValue("is_upgrade"));

            getHolder().addChanges(new Change(id, name, icon, cost_id, cost_count, attribute_change, is_upgrade, parseVariants(change_data)));
        }
    }

    private List<Variant> parseVariants(Element n) {
        List<Variant> list = new ArrayList<>();
        for (Iterator<Element> iterator = n.elementIterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            if ("variant".equalsIgnoreCase(element.getName())) {
                int number = Integer.parseInt(element.attributeValue("number"));
                int id = Integer.parseInt(element.attributeValue("id"));
                String name = element.attributeValue("name");
                String icon = element.attributeValue("icon");

                list.add(new Variant(number, id, name, icon));
            }
        }
        return list;
    }
}
