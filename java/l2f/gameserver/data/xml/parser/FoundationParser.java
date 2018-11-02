package l2f.gameserver.data.xml.parser;

import l2f.commons.data.xml.AbstractFileParser;
import l2f.gameserver.Config;
import l2f.gameserver.data.xml.holder.FoundationHolder;
import org.dom4j.Element;

import java.io.File;
import java.util.Iterator;

public final class FoundationParser extends AbstractFileParser<FoundationHolder> {
    private static final FoundationParser _instance = new FoundationParser();

    private FoundationParser() {
        super(FoundationHolder.getInstance());
    }

    public static FoundationParser getInstance() {
        return _instance;
    }

    @Override
    public File getXMLFile() {
        return new File(Config.DATAPACK_ROOT, "data/foundation/foundation.xml");
    }

    @Override
    public String getDTDFileName() {
        return "foundation.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("foundation"); iterator.hasNext(); ) {
            Element foundation = iterator.next();
            int simple = Integer.parseInt(foundation.attributeValue("simple"));
            int found = Integer.parseInt(foundation.attributeValue("found"));

            getHolder().addFoundation(simple, found);
        }
    }
}