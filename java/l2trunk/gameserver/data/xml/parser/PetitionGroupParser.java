package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.PetitionGroupHolder;
import l2trunk.gameserver.model.petition.PetitionMainGroup;
import l2trunk.gameserver.model.petition.PetitionSubGroup;
import l2trunk.gameserver.utils.Language;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.Iterator;

public class PetitionGroupParser extends AbstractFileParser<PetitionGroupHolder> {
    private static final PetitionGroupParser _instance = new PetitionGroupParser();

    private PetitionGroupParser() {
        super(PetitionGroupHolder.getInstance());
    }

    public static PetitionGroupParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/petition_group.xml");
    }

    @Override
    public String getDTDFileName() {
        return "petition_group.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element groupElement = iterator.next();
            PetitionMainGroup group = new PetitionMainGroup(Integer.parseInt(groupElement.attributeValue("id")));
            getHolder().addPetitionGroup(group);

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
