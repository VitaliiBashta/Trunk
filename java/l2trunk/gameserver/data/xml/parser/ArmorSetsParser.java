package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ArmorSetsHolder;
import l2trunk.gameserver.model.ArmorSet;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

public enum  ArmorSetsParser {
    INSTANCE;
    private static Path xmlFile = Config.DATAPACK_ROOT.resolve("data/armor_sets.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xmlFile).forEach(this::readData);
        LOG.info("Loaded " + ArmorSetsHolder.size() + " item(s) ");
    }

    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("set"); iterator.hasNext(); ) {
            String[] chest = null, legs = null, head = null, gloves = null, feet = null, skills = null, shield = null, shield_skills = null, enchant6skills = null;
            Element element = iterator.next();
            int id = Integer.parseInt(element.attributeValue("id"));
            if (element.attributeValue("chest") != null)
                chest = element.attributeValue("chest").split(";");
            if (element.attributeValue("legs") != null)
                legs = element.attributeValue("legs").split(";");
            if (element.attributeValue("head") != null)
                head = element.attributeValue("head").split(";");
            if (element.attributeValue("gloves") != null)
                gloves = element.attributeValue("gloves").split(";");
            if (element.attributeValue("feet") != null)
                feet = element.attributeValue("feet").split(";");
            if (element.attributeValue("skills") != null)
                skills = element.attributeValue("skills").split(";");
            if (element.attributeValue("shield") != null)
                shield = element.attributeValue("shield").split(";");
            if (element.attributeValue("shield_skills") != null)
                shield_skills = element.attributeValue("shield_skills").split(";");
            if (element.attributeValue("enchant6skills") != null)
                enchant6skills = element.attributeValue("enchant6skills").split(";");

            ArmorSetsHolder.addArmorSet(new ArmorSet(id, chest, legs, head, gloves, feet, skills, shield, shield_skills, enchant6skills));
        }
    }
}