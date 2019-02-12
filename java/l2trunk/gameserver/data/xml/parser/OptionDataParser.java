package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.OptionDataHolder;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.OptionDataTemplate;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum OptionDataParser /*extends StatParser<OptionDataHolder>*/ {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    Path xml = Config.DATAPACK_ROOT.resolve("data/option_data");

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + OptionDataHolder.size() + " items ");
    }

    protected void readData(Element rootElement) {
        for (Iterator<Element> itemIterator = rootElement.elementIterator(); itemIterator.hasNext(); ) {
            Element optionDataElement = itemIterator.next();
            OptionDataTemplate template = new OptionDataTemplate(toInt(optionDataElement.attributeValue("id"),0));
            for (Iterator<Element> subIterator = optionDataElement.elementIterator(); subIterator.hasNext(); ) {
                Element subElement = subIterator.next();
                String subName = subElement.getName();
                if (subName.equalsIgnoreCase("for"))
                    StatParser.parseFor(subElement, template);
                else if (subName.equalsIgnoreCase("triggers"))
                    StatParser.parseTriggers(subElement, template);
                else if (subName.equalsIgnoreCase("skills")) {
                    for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                        Element nextElement = nextIterator.next();
                        int id = Integer.parseInt(nextElement.attributeValue("id"));
                        int level = Integer.parseInt(nextElement.attributeValue("level"));

                        Skill skill = SkillTable.INSTANCE.getInfo(id, level);

                        if (skill != null)
                            template.addSkill(skill);
                        else
                            LOG.info("Skill not found(" + id + "," + level + ") for option data:" + template.id() + "; element:" + optionDataElement);
                    }
                }
            }
            OptionDataHolder.addTemplate(template);
        }
    }

    protected Object getTableValue(String name) {
        return null;
    }
}
