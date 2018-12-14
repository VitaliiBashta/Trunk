package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.CubicHolder;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.CubicTemplate;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum CubicParser {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    private final Path xmlFile = Config.DATAPACK_ROOT.resolve("data/cubics.xml");

    public void load() {
        ParserUtil.INSTANCE.load(xmlFile).forEach(this::readData);
        LOG.info("loaded " + CubicHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element cubicElement = iterator.next();
            int id = toInt(cubicElement.attributeValue("id"));
            int level = toInt(cubicElement.attributeValue("level"));
            int delay = toInt(cubicElement.attributeValue("delay"));
            CubicTemplate template = new CubicTemplate(id, level, delay);
            CubicHolder.addCubicTemplate(template);

            // skills
            for (Iterator<Element> skillsIterator = cubicElement.elementIterator(); skillsIterator.hasNext(); ) {
                Element skillsElement = skillsIterator.next();
                int chance = toInt(skillsElement.attributeValue("chance"));
                List<CubicTemplate.SkillInfo> skills = new ArrayList<>(1);
                // skill
                for (Iterator<Element> skillIterator = skillsElement.elementIterator(); skillIterator.hasNext(); ) {
                    Element skillElement = skillIterator.next();
                    int id2 = toInt(skillElement.attributeValue("id"));
                    int level2 = toInt(skillElement.attributeValue("level"));
                    int chance2 = skillElement.attributeValue("chance") == null ? 0 : toInt(skillElement.attributeValue("chance"));
                    boolean canAttackDoor = Boolean.parseBoolean(skillElement.attributeValue("can_attack_door"));
                    CubicTemplate.ActionType type = CubicTemplate.ActionType.valueOf(skillElement.attributeValue("action_type"));

                    Map<Integer, Integer> set = new HashMap<>();
                    for (Iterator<Element> chanceIterator = skillElement.elementIterator(); chanceIterator.hasNext(); ) {
                        Element chanceElement = chanceIterator.next();
                        int min = toInt(chanceElement.attributeValue("min"));
                        int max = toInt(chanceElement.attributeValue("max"));
                        int value = toInt(chanceElement.attributeValue("value"));
                        for (int i = min; i <= max; i++)
                            set.put(i, value);
                    }

                    if (chance2 == 0 && set.isEmpty()) {
                        LOG.warn("Wrong skill chance. Cubic: " + id + "/" + level);
                    }
                    Skill skill = SkillTable.INSTANCE.getInfo(id2, level2);
                    if (skill != null) {
                        skill.setCubicSkill(true);
                        skills.add(new CubicTemplate.SkillInfo(skill, chance2, type, canAttackDoor, set));
                    }
                }

                template.putSkills(chance, skills);
            }
        }
    }
}
