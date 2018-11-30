package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.CubicHolder;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.CubicTemplate;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.*;

public final class CubicParser extends AbstractFileParser<CubicHolder> {
    private static final CubicParser _instance = new CubicParser();

    private CubicParser() {
        super(CubicHolder.getInstance());
    }

    public static CubicParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/cubics.xml");
    }

    @Override
    public String getDTDFileName() {
        return "cubics.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element cubicElement = iterator.next();
            int id = Integer.parseInt(cubicElement.attributeValue("id"));
            int level = Integer.parseInt(cubicElement.attributeValue("level"));
            int delay = Integer.parseInt(cubicElement.attributeValue("delay"));
            CubicTemplate template = new CubicTemplate(id, level, delay);
            getHolder().addCubicTemplate(template);

            // skills
            for (Iterator<Element> skillsIterator = cubicElement.elementIterator(); skillsIterator.hasNext(); ) {
                Element skillsElement = skillsIterator.next();
                int chance = Integer.parseInt(skillsElement.attributeValue("chance"));
                List<CubicTemplate.SkillInfo> skills = new ArrayList<>(1);
                // skill
                for (Iterator<Element> skillIterator = skillsElement.elementIterator(); skillIterator.hasNext(); ) {
                    Element skillElement = skillIterator.next();
                    int id2 = Integer.parseInt(skillElement.attributeValue("id"));
                    int level2 = Integer.parseInt(skillElement.attributeValue("level"));
                    int chance2 = skillElement.attributeValue("chance") == null ? 0 : Integer.parseInt(skillElement.attributeValue("chance"));
                    boolean canAttackDoor = Boolean.parseBoolean(skillElement.attributeValue("can_attack_door"));
                    CubicTemplate.ActionType type = CubicTemplate.ActionType.valueOf(skillElement.attributeValue("action_type"));

                    Map<Integer, Integer> set = new HashMap<>();
                    for (Iterator<Element> chanceIterator = skillElement.elementIterator(); chanceIterator.hasNext(); ) {
                        Element chanceElement = chanceIterator.next();
                        int min = Integer.parseInt(chanceElement.attributeValue("min"));
                        int max = Integer.parseInt(chanceElement.attributeValue("max"));
                        int value = Integer.parseInt(chanceElement.attributeValue("value"));
                        for (int i = min; i <= max; i++)
                            set.put(i, value);
                    }

                    if (chance2 == 0 && set.isEmpty()) {
                        LOG.warn("Wrong skill chance. Cubic: " + id + "/" + level);
                    }
                    Skill skill = SkillTable.INSTANCE().getInfo(id2, level2);
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
