package l2trunk.gameserver.data.xml.parser;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.OptionDataHolder;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.OptionDataTemplate;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.Iterator;

public final class OptionDataParser extends StatParser<OptionDataHolder> {
    private static final OptionDataParser _instance = new OptionDataParser();

    private OptionDataParser() {
        super(OptionDataHolder.getInstance());
    }

    public static OptionDataParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLDir() {
        return Config.DATAPACK_ROOT.resolve("data/option_data");
    }


    @Override
    public String getDTDFileName() {
        return "option_data.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> itemIterator = rootElement.elementIterator(); itemIterator.hasNext(); ) {
            Element optionDataElement = itemIterator.next();
            OptionDataTemplate template = new OptionDataTemplate(Integer.parseInt(optionDataElement.attributeValue("id")));
            for (Iterator<Element> subIterator = optionDataElement.elementIterator(); subIterator.hasNext(); ) {
                Element subElement = subIterator.next();
                String subName = subElement.getName();
                if (subName.equalsIgnoreCase("for"))
                    parseFor(subElement, template);
                else if (subName.equalsIgnoreCase("triggers"))
                    parseTriggers(subElement, template);
                else if (subName.equalsIgnoreCase("skills")) {
                    for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                        Element nextElement = nextIterator.next();
                        int id = Integer.parseInt(nextElement.attributeValue("id"));
                        int level = Integer.parseInt(nextElement.attributeValue("level"));

                        Skill skill = SkillTable.getInstance().getInfo(id, level);

                        if (skill != null)
                            template.addSkill(skill);
                        else
                            info("Skill not found(" + id + "," + level + ") for option data:" + template.getId() + "; file:" + getCurrentFileName());
                    }
                }
            }
            getHolder().addTemplate(template);
        }
    }

    @Override
    protected Object getTableValue(String name) {
        return null;
    }
}
