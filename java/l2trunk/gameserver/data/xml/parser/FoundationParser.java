package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.FoundationHolder;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum FoundationParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/foundation/foundation.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + FoundationHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("foundation"); iterator.hasNext(); ) {
            Element foundation = iterator.next();
            int simple = toInt(foundation.attributeValue("simple"));
            int found = toInt(foundation.attributeValue("found"));

            FoundationHolder.addFoundation(simple, found);
        }
    }
}