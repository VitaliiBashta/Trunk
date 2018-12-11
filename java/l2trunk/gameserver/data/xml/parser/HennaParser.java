package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.HennaHolder;
import l2trunk.gameserver.templates.Henna;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum HennaParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/hennas.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + HennaHolder.size() + " items ");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element hennaElement = iterator.next();
            int symbolId = toInt(hennaElement.attributeValue("symbol_id"));
            int dyeId = toInt(hennaElement.attributeValue("dye_id"));
            long price = toInt(hennaElement.attributeValue("price"));
            long drawCount = hennaElement.attributeValue("draw_count") == null ? 10 : toInt(hennaElement.attributeValue("draw_count"));
            int wit = toInt(hennaElement.attributeValue("wit"));
            int str = toInt(hennaElement.attributeValue("str"));
            int _int = toInt(hennaElement.attributeValue("int"));
            int con = toInt(hennaElement.attributeValue("con"));
            int dex = toInt(hennaElement.attributeValue("dex"));
            int men = toInt(hennaElement.attributeValue("men"));

            List<Integer> list = new ArrayList<>();
            for (Iterator<Element> classIterator = hennaElement.elementIterator("class"); classIterator.hasNext(); ) {
                Element classElement = classIterator.next();
                list.add(toInt(classElement.attributeValue("id")));
            }

            Henna henna = new Henna(symbolId, dyeId, price, drawCount, wit, _int, con, str, dex, men, list);

            HennaHolder.addHenna(henna);
        }
    }
}
