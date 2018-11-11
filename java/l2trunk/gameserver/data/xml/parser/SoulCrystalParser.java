package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.SoulCrystalHolder;
import l2trunk.gameserver.templates.SoulCrystal;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.Iterator;

public final class SoulCrystalParser extends AbstractFileParser<SoulCrystalHolder> {
    private static final SoulCrystalParser _instance = new SoulCrystalParser();

    private SoulCrystalParser() {
        super(SoulCrystalHolder.getInstance());
    }

    public static SoulCrystalParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/soul_crystals.xml");
    }

    @Override
    public String getDTDFileName() {
        return "soul_crystals.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("crystal"); iterator.hasNext(); ) {
            Element element = iterator.next();
            int itemId = Integer.parseInt(element.attributeValue("item_id"));
            int level = Integer.parseInt(element.attributeValue("level"));
            int nextItemId = Integer.parseInt(element.attributeValue("next_item_id"));
            int cursedNextItemId = element.attributeValue("cursed_next_item_id") == null ? 0 : Integer.parseInt(element.attributeValue("cursed_next_item_id"));

            getHolder().addCrystal(new SoulCrystal(itemId, level, nextItemId, cursedNextItemId));
        }
    }
}
