package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.SkillAcquireHolder;
import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Race;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum SkillAcquireParser {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    private final Path xmlFile = Config.DATAPACK_ROOT.resolve("data/skill_tree/");

    public void load() {
        ParserUtil.INSTANCE.load(xmlFile).forEach(this::readData);
        LOG.info("loaded " + SkillAcquireHolder.size() + " items");
    }


    private void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("certification_skill_tree"); iterator.hasNext(); )
            SkillAcquireHolder.addAllCertificationLearns(parseSkillLearn(iterator.next()));

        for (Iterator<Element> iterator = rootElement.elementIterator("sub_unit_skill_tree"); iterator.hasNext(); )
            SkillAcquireHolder.addAllSubUnitLearns(parseSkillLearn(iterator.next()));

        for (Iterator<Element> iterator = rootElement.elementIterator("pledge_skill_tree"); iterator.hasNext(); )
            SkillAcquireHolder.addAllPledgeLearns(parseSkillLearn(iterator.next()));

        for (Iterator<Element> iterator = rootElement.elementIterator("collection_skill_tree"); iterator.hasNext(); )
            SkillAcquireHolder.addAllCollectionLearns(parseSkillLearn(iterator.next()));

        for (Iterator<Element> iterator = rootElement.elementIterator("fishing_skill_tree"); iterator.hasNext(); ) {
            Element nxt = iterator.next();
            for (Iterator<Element> classIterator = nxt.elementIterator("race"); classIterator.hasNext(); ) {
                Element classElement = classIterator.next();
                Race race = Race.of(toInt(classElement.attributeValue("id")));
                List<SkillLearn> learns = parseSkillLearn(classElement);
                SkillAcquireHolder.addAllFishingLearns(race, learns);
            }
        }

        for (Iterator<Element> iterator = rootElement.elementIterator("transfer_skill_tree"); iterator.hasNext(); ) {
            Element nxt = iterator.next();
            for (Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext(); ) {
                Element classElement = classIterator.next();
                ClassId classId = ClassId.getById(classElement.attributeValue("id"));
                List<SkillLearn> learns = parseSkillLearn(classElement);
                SkillAcquireHolder.addAllTransferLearns(classId, learns);
            }
        }

        for (Iterator<Element> iterator = rootElement.elementIterator("normal_skill_tree"); iterator.hasNext(); ) {
            Map<Integer, List<SkillLearn>> map = new HashMap<>();
            Element nxt = iterator.next();
            for (Iterator<Element> classIterator = nxt.elementIterator("class"); classIterator.hasNext(); ) {
                Element classElement = classIterator.next();
                int classId = Integer.parseInt(classElement.attributeValue("id"));
                List<SkillLearn> learns = parseSkillLearn(classElement);

                map.put(classId, learns);
            }

            SkillAcquireHolder.addAllNormalSkillLearns(map);
        }

        for (Iterator<Element> iterator = rootElement.elementIterator("transformation_skill_tree"); iterator.hasNext(); ) {
            Element nxt = iterator.next();
            for (Iterator<Element> classIterator = nxt.elementIterator("race"); classIterator.hasNext(); ) {
                Element classElement = classIterator.next();
                Race race = Race.of(toInt(classElement.attributeValue("id")));
                List<SkillLearn> learns = parseSkillLearn(classElement);
                SkillAcquireHolder.addAllTransformationLearns(race, learns);
            }
        }
    }

    private List<SkillLearn> parseSkillLearn(Element tree) {
        List<SkillLearn> skillLearns = new ArrayList<>();
        for (Iterator<Element> iterator = tree.elementIterator("skill"); iterator.hasNext(); ) {
            Element element = iterator.next();

            int id = Integer.parseInt(element.attributeValue("id"));
            int level = Integer.parseInt(element.attributeValue("level"));
            int cost = element.attributeValue("cost") == null ? 0 : Integer.parseInt(element.attributeValue("cost"));
            int min_level = Integer.parseInt(element.attributeValue("min_level"));
            int item_id = element.attributeValue("item_id") == null ? 0 : Integer.parseInt(element.attributeValue("item_id"));
            long item_count = element.attributeValue("item_count") == null ? 1 : Long.parseLong(element.attributeValue("item_count"));
            boolean clicked = element.attributeValue("clicked") != null && Boolean.parseBoolean(element.attributeValue("clicked"));

            skillLearns.add(new SkillLearn(id, level, min_level, cost, item_id, item_count, clicked));
        }

        return skillLearns;
    }
}
