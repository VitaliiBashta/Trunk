package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.HennaHolder;
import l2trunk.gameserver.templates.Henna;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class HennaParser extends AbstractFileParser<HennaHolder> {
    private static final HennaParser _instance = new HennaParser();

    private HennaParser() {
        super(HennaHolder.getInstance());
    }

    public static HennaParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/hennas.xml");
    }

    @Override
    public String getDTDFileName() {
        return "hennas.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element hennaElement = iterator.next();
            int symbolId = Integer.parseInt(hennaElement.attributeValue("symbol_id"));
            int dyeId = Integer.parseInt(hennaElement.attributeValue("dye_id"));
            long price = Integer.parseInt(hennaElement.attributeValue("price"));
            long drawCount = hennaElement.attributeValue("draw_count") == null ? 10 : Integer.parseInt(hennaElement.attributeValue("draw_count"));
            int wit = Integer.parseInt(hennaElement.attributeValue("wit"));
            int str = Integer.parseInt(hennaElement.attributeValue("str"));
            int _int = Integer.parseInt(hennaElement.attributeValue("int"));
            int con = Integer.parseInt(hennaElement.attributeValue("con"));
            int dex = Integer.parseInt(hennaElement.attributeValue("dex"));
            int men = Integer.parseInt(hennaElement.attributeValue("men"));

            List<Integer> list = new ArrayList<>();
            for (Iterator<Element> classIterator = hennaElement.elementIterator("class"); classIterator.hasNext(); ) {
                Element classElement = classIterator.next();
                list.add(Integer.parseInt(classElement.attributeValue("id")));
            }

            Henna henna = new Henna(symbolId, dyeId, price, drawCount, wit, _int, con, str, dex, men, list);

            getHolder().addHenna(henna);
        }
    }
}
