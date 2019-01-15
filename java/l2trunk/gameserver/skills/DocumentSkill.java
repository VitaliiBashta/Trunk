package l2trunk.gameserver.skills;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Skill.SkillType;
import l2trunk.gameserver.model.base.EnchantSkillLearn;
import l2trunk.gameserver.stats.conditions.Condition;
import l2trunk.gameserver.tables.SkillTreeTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class DocumentSkill extends DocumentBase {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentSkill.class);
    private final Set<String> usedTables = new HashSet<>();
    private final List<Skill> skillsInFile = new LinkedList<>();
    private SkillData currentSkill;

    DocumentSkill(Path file) {
        super(file);
    }

    @Override
    protected void resetTable() {
        if (!usedTables.isEmpty())
            for (String table : tables.keySet())
                if (!usedTables.contains(table))
                    if (LOG.isDebugEnabled())
                        LOG.debug("Unused table " + table + " for skill " + currentSkill.id);
        usedTables.clear();
        super.resetTable();
    }

    private void setCurrentSkill(SkillData skill) {
        currentSkill = skill;
    }

    List<Skill> getSkills() {
        return skillsInFile;
    }

    @Override
    protected String getTableValue(String name) {
//        try {
        usedTables.add(name);
        List<String> a = tables.get(name);

        a.stream().filter(s -> !s.matches(".*\\d+.*")).findFirst().ifPresent(s -> LOG.error("found not number " + s));
        if (a.size() - 1 >= currentSkill.currentLevel)
            return a.get(currentSkill.currentLevel);
        return a.get((a.size() - 1));
//        } catch (RuntimeException e) {
//            LOG.error("Error in table " + name + " of skill Id " + currentSkill.id, e);
//            return 0;
//        }
    }

    @Override
    protected Object getTableValue(String name, int idx) {
        idx--;
        try {
            usedTables.add(name);
            List<String> a = tables.get(name);
            if (a.size() - 1 >= idx)
                return a.get(idx);
            return a.get(a.size() - 1);
        } catch (RuntimeException e) {
            LOG.error("Wrong level count in skill Id " + currentSkill.id + " table " + name + " level " + idx, e);
            return 0;
        }
    }

    @Override
    protected void parseDocument(Document doc) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling())
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
                    if ("skill".equalsIgnoreCase(d.getNodeName())) {
                        setCurrentSkill(new SkillData());
                        parseSkill(d);
                        skillsInFile.addAll(currentSkill.skills);
                        resetTable();
                    }
            } else if ("skill".equalsIgnoreCase(n.getNodeName())) {
                setCurrentSkill(new SkillData());
                parseSkill(n);
                skillsInFile.addAll(currentSkill.skills);
            }
    }

    private void parseSkill(Node n) {
        NamedNodeMap attrs = n.getAttributes();
        int skillId = toInt(attrs.getNamedItem("id").getNodeValue());
        String skillName = attrs.getNamedItem("name").getNodeValue();
        String levels = attrs.getNamedItem("levels").getNodeValue();
        int lastLvl = toInt(levels);

        try {
            Map<Integer, Integer> displayLevels = new HashMap<>();

            // iterate enchants
            Node enchant;
            Map<String, String[]> etables = new HashMap<>();
            int count = 0;
            int eLevels = 0;
            Node d = n.cloneNode(true);
            for (int k = 0; k < d.getChildNodes().getLength(); k++) {
                enchant = d.getChildNodes().item(k);
                if (!enchant.getNodeName().startsWith("enchant"))
                    continue;
                if (eLevels == 0)
                    if (enchant.getAttributes().getNamedItem("levels") != null)
                        eLevels = toInt(enchant.getAttributes().getNamedItem("levels").getNodeValue());
                    else
                        eLevels = 30;
                String ename = enchant.getAttributes().getNamedItem("name").getNodeValue();
                for (int r = 1; r <= eLevels; r++) {
                    int level = lastLvl + eLevels * count + r;
                    EnchantSkillLearn e = new EnchantSkillLearn(skillId, 100 * (count + 1) + r, skillName, "+" + r + " " + ename, (r == 1 ? lastLvl : 100 * (count + 1) + r - 1), lastLvl, eLevels);

                    List<EnchantSkillLearn> t = SkillTreeTable._enchant.get(skillId);
                    if (t == null)
                        t = new ArrayList<>();
                    t.add(e);
                    SkillTreeTable._enchant.put(skillId, t);
                    displayLevels.put(level, ((count + 1) * 100 + r));
                }
                count++;
                Node first = enchant.getFirstChild();
                Node curr;
                for (curr = first; curr != null; curr = curr.getNextSibling())
                    if ("table".equalsIgnoreCase(curr.getNodeName())) {
                        NamedNodeMap a = curr.getAttributes();
                        String name = a.getNamedItem("name").getNodeValue();
                        String[] table = parseTable(curr);
                        table = fillTableToSize(table, eLevels);
                        String[] fulltable = etables.get(name);
                        if (fulltable == null)
                            fulltable = new String[lastLvl + eLevels * 8 + 1];
                        System.arraycopy(table, 0, fulltable, lastLvl + (count - 1) * eLevels, eLevels);
                        etables.put(name, fulltable);
                    }
            }
            lastLvl += eLevels * count;

            currentSkill.id = skillId;
            currentSkill.name = skillName;
            currentSkill.sets = new ArrayList<>();

            for (int i = 0; i < lastLvl; i++) {
                currentSkill.sets.add(new StatsSet()
                        .set("skill_id", currentSkill.id)
                        .set("level", i + 1)
                        .set("name", currentSkill.name)
                        .set("base_level", levels));
            }

            if (currentSkill.sets.size() != lastLvl)
                throw new RuntimeException("SkillData id=" + skillId + " number of levels missmatch, " + lastLvl + " levels expected");

            Node first = n.getFirstChild();
            for (n = first; n != null; n = n.getNextSibling())
                if ("table".equalsIgnoreCase(n.getNodeName()))
                    parseTable(n);

            // handle table merging them with enchants
            for (String tn : tables.keySet()) {
                String[] et = etables.get(tn);
                if (et != null) {
                    String[] t = tables.get(tn).toArray(new String[0]);
                    String max = t[t.length - 1];
                    System.arraycopy(t, 0, et, 0, t.length);
                    for (int j = 0; j < et.length; j++)
                        if (et[j] == null)
                            et[j] = max;
                    List<String> objs = List.of(et);
                    tables.put(tn, objs);
                }
            }

            for (int i = 1; i <= lastLvl; i++)
                for (n = first; n != null; n = n.getNextSibling())
                    if ("set".equalsIgnoreCase(n.getNodeName()))
                        parseBeanSet(n, currentSkill.sets.get(i - 1), i);

            makeSkills();
            for (int i = 0; i < lastLvl; i++) {
                currentSkill.currentLevel = i;
                Skill current = currentSkill.currentSkills.get(i);
                if (displayLevels.get(current.getLevel()) != null)
                    current.setDisplayLevel(displayLevels.get(current.getLevel()));
                current.setEnchantLevelCount(eLevels);

                for (n = first; n != null; n = n.getNextSibling()) {
                    if ("cond".equalsIgnoreCase(n.getNodeName())) {
                        Condition condition = parseCondition(n.getFirstChild());
                        if (condition != null) {
                            Node msgAttribute = n.getAttributes().getNamedItem("msgId");
                            if (msgAttribute != null) {
                                int msgId = parseNumber(msgAttribute.getNodeValue()).intValue();
                                condition.setSystemMsg(msgId);
                            }
                            current.attach(condition);
                        }
                    } else if ("for".equalsIgnoreCase(n.getNodeName()))
                        parseTemplate(n, current);
                    else if ("triggers".equalsIgnoreCase(n.getNodeName()))
                        parseTrigger(n, current);
                }
            }

            currentSkill.skills.addAll(currentSkill.currentSkills);
        } catch (RuntimeException e) {
            LOG.error("Error loading skill " + skillId, e);
        }
    }

    private String[] fillTableToSize(String[] table, int size) {
        if (table.length < size) {
            String[] ret = new String[size];
            System.arraycopy(table, 0, ret, 0, table.length);
            table = ret;
        }
        for (int j = 1; j < size; j++)
            if (table[j] == null)
                table[j] = table[j - 1];
        return table;
    }

    private void makeSkills() {
        currentSkill.currentSkills = currentSkill.sets.stream()
                .map(set -> set.getEnum("skillType", SkillType.class).makeSkill(set))
                .collect(Collectors.toList());
    }

    class SkillData {
        final List<Skill> skills = new ArrayList<>();
        int id;
        String name;
        List<StatsSet> sets;
        int currentLevel;
        List<Skill> currentSkills = new ArrayList<>();
    }
}