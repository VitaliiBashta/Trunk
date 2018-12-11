package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.PetitionGroupHolder;
import l2trunk.gameserver.model.petition.PetitionMainGroup;
import l2trunk.gameserver.model.petition.PetitionSubGroup;
import l2trunk.gameserver.utils.Language;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

public enum PetitionGroupParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/petition_group.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + PetitionGroupHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element groupElement = iterator.next();
            PetitionMainGroup group = new PetitionMainGroup(Integer.parseInt(groupElement.attributeValue("id")));
            PetitionGroupHolder.addPetitionGroup(group);

            for (Iterator<Element> subIterator = groupElement.elementIterator(); subIterator.hasNext(); ) {
                Element subElement = subIterator.next();
                if ("name".equals(subElement.getName()))
                    group.setName(Language.valueOf(subElement.attributeValue("lang")), subElement.getText());
                else if ("description".equals(subElement.getName()))
                    group.setDescription(Language.valueOf(subElement.attributeValue("lang")), subElement.getText());
                else if ("sub_group".equals(subElement.getName())) {
                    PetitionSubGroup subGroup = new PetitionSubGroup(Integer.parseInt(subElement.attributeValue("id")), subElement.attributeValue("handler"));
                    group.addSubGroup(subGroup);
                    for (Iterator<Element> sub2Iterator = subElement.elementIterator(); sub2Iterator.hasNext(); ) {
                        Element sub2Element = sub2Iterator.next();
                        if ("name".equals(sub2Element.getName()))
                            subGroup.setName(Language.valueOf(sub2Element.attributeValue("lang")), sub2Element.getText());
                        else if ("description".equals(sub2Element.getName()))
                            subGroup.setDescription(Language.valueOf(sub2Element.attributeValue("lang")), sub2Element.getText());
                    }
                }
            }
        }
    }
}
