package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.base.AcquireType;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class SkillAcquireHolder {
    private static final Logger LOG = LoggerFactory.getLogger(SkillAcquireHolder.class);
    // классовые зависимости
    private static final Map<Integer, List<SkillLearn>> NORMAL_SKILL_TREE = new HashMap<>();
    private static final Map<Integer, List<SkillLearn>> TRANSFER_SKILL_TREE = new HashMap<>();
    // расовые зависимости
    private static final Map<Integer, List<SkillLearn>> FISHING_SKILL_TREE = new HashMap<>();
    private static final Map<Integer, List<SkillLearn>> TRANSFORMATION_SKILL_TREE = new HashMap<>();
    // без зависимостей
    private static final List<SkillLearn> CERTIFICATION_SKILL_TREE = new ArrayList<>();
    private static final List<SkillLearn> COLLECTION_SKILL_TREE = new ArrayList<>();
    private static final List<SkillLearn> PLEDGE_SKILL_TREE = new ArrayList<>();
    private static final List<SkillLearn> SUB_UNIT_SKILL_TREE = new ArrayList<>();

    private SkillAcquireHolder() {
    }

    public static int getMinLevelForNewSkill(Player player, AcquireType type) {
        List<SkillLearn> skills;
        switch (type) {
            case NORMAL:
                skills = NORMAL_SKILL_TREE.get(player.getActiveClassId());
                if (skills == null) {
                    LOG.info("skill tree for class " + player.getActiveClassId() + " is not defined !");
                    return 0;
                }
                break;
            case TRANSFORMATION:
                skills = TRANSFORMATION_SKILL_TREE.get(player.getRace().ordinal());
                if (skills == null) {
                    LOG.info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
                    return 0;
                }
                break;
            case FISHING:
                skills = FISHING_SKILL_TREE.get(player.getRace().ordinal());
                if (skills == null) {
                    LOG.info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
                    return 0;
                }
                break;
            default:
                return 0;
        }
        int minlevel = 0;
        for (SkillLearn temp : skills)
            if (temp.getMinLevel() > player.getLevel())
                if (minlevel == 0 || temp.getMinLevel() < minlevel)
                    minlevel = temp.getMinLevel();
        return minlevel;
    }

    public static Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type) {
        return getAvailableSkills(player, type, null);
    }

    public static Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type, SubUnit subUnit) {
        Collection<SkillLearn> skills;
        switch (type) {
            case NORMAL:
                skills = NORMAL_SKILL_TREE.get(player.getActiveClassId());
                if (skills == null) {
                    LOG.info("skill tree for class " + player.getActiveClassId() + " is not defined !");
                    return Collections.emptyList();
                }
                return getAvaliableList(skills, player.getAllSkills(), player.getLevel());
            case COLLECTION:
                skills = COLLECTION_SKILL_TREE;
                return getAvaliableList(skills, player.getAllSkills(), player.getLevel());
            case TRANSFORMATION:
                skills = TRANSFORMATION_SKILL_TREE.get(player.getRace().ordinal());
                if (skills == null) {
                    LOG.info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
                    return Collections.emptyList();
                }
                return getAvaliableList(skills, player.getAllSkills(), player.getLevel());
            case TRANSFER_EVA_SAINTS:
            case TRANSFER_SHILLIEN_SAINTS:
            case TRANSFER_CARDINAL:
                skills = TRANSFER_SKILL_TREE.get(type.transferClassId());
                if (skills == null) {
                    LOG.info("skill tree for class " + type.transferClassId() + " is not defined !");
                    return Collections.emptyList();
                }
                if (player == null)
                    return skills;
                else {
                    Map<Integer, SkillLearn> skillLearnMap = new TreeMap<>();
                    for (SkillLearn temp : skills)
                        if (temp.getMinLevel() <= player.getLevel()) {
                            int knownLevel = player.getSkillLevel(temp.id);
                            if (knownLevel == -1)
                                skillLearnMap.put(temp.id(), temp);
                        }

                    return skillLearnMap.values();
                }
            case FISHING:
                skills = FISHING_SKILL_TREE.get(player.getRace().ordinal());
                if (skills == null) {
                    LOG.info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
                    return Collections.emptyList();
                }
                return getAvaliableList(skills, player.getAllSkills(), player.getLevel());
            case CLAN:
                skills = PLEDGE_SKILL_TREE;
                Collection<Skill> skls = player.getClan().getSkills(); //TODO [VISTALL] придумать другой способ

                return getAvaliableList(skills, skls, player.getClan().getLevel());
            case SUB_UNIT:
                skills = SUB_UNIT_SKILL_TREE;
                Collection<Skill> st = subUnit.getSkills(); //TODO [VISTALL] придумать другой способ

                return getAvaliableList(skills, st, player.getClan().getLevel());
            case CERTIFICATION:
                skills = CERTIFICATION_SKILL_TREE;
                if (player == null)
                    return skills;
                else
                    return getAvaliableList(skills, player.getAllSkills(), player.getLevel());
            default:
                return Collections.emptyList();
        }
    }

    private static Collection<SkillLearn> getAvaliableList(Collection<SkillLearn> skillLearns, Collection<Skill> skills, int level) {
        Map<Integer, SkillLearn> skillLearnMap = new TreeMap<>();
        for (SkillLearn temp : skillLearns)
            if (temp.getMinLevel() <= level) {
                boolean knownSkill = false;
                for (Skill skill : skills) {
                    if (knownSkill) continue;
                    if (skill.id == temp.id()) {
                        knownSkill = true;
                        if (skill.level == temp.getLevel() - 1)
                            skillLearnMap.put(temp.id(), temp);
                    }
                }
                if (!knownSkill && temp.getLevel() == 1)
                    skillLearnMap.put(temp.id(), temp);
            }

        return skillLearnMap.values();
    }

    public static SkillLearn getSkillLearn(Player player, int id, int level, AcquireType type) {
        List<SkillLearn> skills;
        switch (type) {
            case NORMAL:
                skills = NORMAL_SKILL_TREE.get(player.getActiveClassId());
                break;
            case COLLECTION:
                skills = COLLECTION_SKILL_TREE;
                break;
            case TRANSFORMATION:
                skills = TRANSFORMATION_SKILL_TREE.get(player.getRace().ordinal());
                break;
            case TRANSFER_CARDINAL:
            case TRANSFER_SHILLIEN_SAINTS:
            case TRANSFER_EVA_SAINTS:
                skills = TRANSFER_SKILL_TREE.get(player.getActiveClassId());
                break;
            case FISHING:
                skills = FISHING_SKILL_TREE.get(player.getRace().ordinal());
                break;
            case CLAN:
                skills = PLEDGE_SKILL_TREE;
                break;
            case SUB_UNIT:
                skills = SUB_UNIT_SKILL_TREE;
                break;
            case CERTIFICATION:
                skills = CERTIFICATION_SKILL_TREE;
                break;
            default:
                return null;
        }

        if (skills == null)
            return null;

        for (SkillLearn temp : skills)
            if (temp.getLevel() == level && temp.id() == id)
                return temp;

        return null;
    }

    public static boolean isSkillPossible(Player player, Skill skill, AcquireType type) {
        Clan clan;
        List<SkillLearn> skills;
        switch (type) {
            case NORMAL:
                skills = NORMAL_SKILL_TREE.get(player.getActiveClassId());
                break;
            case COLLECTION:
                skills = COLLECTION_SKILL_TREE;
                break;
            case TRANSFORMATION:
                skills = TRANSFORMATION_SKILL_TREE.get(player.getRace().ordinal());
                break;
            case FISHING:
                skills = FISHING_SKILL_TREE.get(player.getRace().ordinal());
                break;
            case TRANSFER_CARDINAL:
            case TRANSFER_EVA_SAINTS:
            case TRANSFER_SHILLIEN_SAINTS:
                int transferId = type.transferClassId();
                if (player.getActiveClassId() != transferId)
                    return false;

                skills = TRANSFER_SKILL_TREE.get(transferId);
                break;
            case CLAN:
                clan = player.getClan();
                if (clan == null)
                    return false;
                skills = PLEDGE_SKILL_TREE;
                break;
            case SUB_UNIT:
                clan = player.getClan();
                if (clan == null)
                    return false;

                skills = SUB_UNIT_SKILL_TREE;
                break;
            case CERTIFICATION:
                skills = CERTIFICATION_SKILL_TREE;
                break;
            default:
                return false;
        }

        return isSkillPossible(skills, skill);
    }

    private static boolean isSkillPossible(Collection<SkillLearn> skills, Skill skill) {
        return skills.stream()
                .filter(learn -> learn.id() == skill.id)
                .anyMatch(learn -> learn.getLevel() <= skill.level);
    }

    public static boolean isSkillPossible(Player player, Skill skill) {
        return Stream.of(AcquireType.VALUES)
                .anyMatch(aq -> isSkillPossible(player, skill, aq));
    }

    public static List<SkillLearn> getSkillLearnListByItemId(Player player, int itemId) {
        List<SkillLearn> learns = NORMAL_SKILL_TREE.get(player.getActiveClassId());
        if (learns == null)
            return Collections.emptyList();

        List<SkillLearn> l = new ArrayList<>(1);
        for (SkillLearn $i : learns)
            if ($i.getItemId() == itemId)
                l.add($i);

        return l;
    }

    public static List<Integer> getAllSpellbookIds() {
        return NORMAL_SKILL_TREE.values().stream()
                .flatMap(List::stream)
                .filter(learn -> learn.getItemId() > 0)
                .filter(SkillLearn::isClicked)
                .map(SkillLearn::getItemId)
                .collect(Collectors.toList());
    }

    public static void addAllNormalSkillLearns(Map<Integer, List<SkillLearn>> map) {
        int classID;

        for (ClassId classId : ClassId.VALUES) {
            if (!classId.name.startsWith("dummyEntry")) {
                classID = classId.id;

                List<SkillLearn> temp;

                temp = map.get(classID);
                if (temp == null) {
                    LOG.info("Not found NORMAL skill learn for class " + classID);
                    continue;
                }

                NORMAL_SKILL_TREE.put(classId.id, temp);

                classId = classId.parent;

                while (classId != null) {
                    List<SkillLearn> parentList = NORMAL_SKILL_TREE.get(classId.id);
                    temp.addAll(parentList);

                    classId = classId.parent;
                }
            }

        }
    }

    public static void addAllFishingLearns(int race, List<SkillLearn> s) {
        FISHING_SKILL_TREE.put(race, s);
    }

    public static void addAllTransferLearns(int classId, List<SkillLearn> s) {
        TRANSFER_SKILL_TREE.put(classId, s);
    }

    public static void addAllTransformationLearns(int race, List<SkillLearn> s) {
        TRANSFORMATION_SKILL_TREE.put(race, s);
    }

    public static void addAllCertificationLearns(List<SkillLearn> s) {
        CERTIFICATION_SKILL_TREE.addAll(s);
    }

    public static void addAllCollectionLearns(List<SkillLearn> s) {
        COLLECTION_SKILL_TREE.addAll(s);
    }

    public static void addAllSubUnitLearns(List<SkillLearn> s) {
        SUB_UNIT_SKILL_TREE.addAll(s);
    }

    public static void addAllPledgeLearns(List<SkillLearn> s) {
        PLEDGE_SKILL_TREE.addAll(s);
    }

    public static int size() {
        return NORMAL_SKILL_TREE.size() +
                FISHING_SKILL_TREE.size() +
                TRANSFER_SKILL_TREE.size() +
                CERTIFICATION_SKILL_TREE.size() +
                COLLECTION_SKILL_TREE.size() +
                PLEDGE_SKILL_TREE.size() +
                SUB_UNIT_SKILL_TREE.size();
    }

    public Collection<SkillLearn> getAvailableSkills(Player player, AcquireType type, SubUnit subUnit, int level) {
        Collection<SkillLearn> skills;
        switch (type) {
            case NORMAL:
                skills = NORMAL_SKILL_TREE.get(player.getActiveClassId());
                if (skills == null) {
                    LOG.info("skill tree for class " + player.getActiveClassId() + " is not defined !");
                    return Collections.emptyList();
                }
                return getAvaliableList(skills, player.getAllSkills(), level);
            case COLLECTION:
                skills = COLLECTION_SKILL_TREE;
                return getAvaliableList(skills, player.getAllSkills(), level);
            case TRANSFORMATION:
                skills = TRANSFORMATION_SKILL_TREE.get(player.getRace().ordinal());
                if (skills == null) {
                    LOG.info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
                    return Collections.emptyList();
                }
                return getAvaliableList(skills, player.getAllSkills(), level);
            case TRANSFER_EVA_SAINTS:
            case TRANSFER_SHILLIEN_SAINTS:
            case TRANSFER_CARDINAL:
                skills = TRANSFER_SKILL_TREE.get(type.transferClassId());
                if (skills == null) {
                    LOG.info("skill tree for class " + type.transferClassId() + " is not defined !");
                    return Collections.emptyList();
                }
                if (player == null)
                    return skills;
                else {
                    Map<Integer, SkillLearn> skillLearnMap = new TreeMap<>();
                    for (SkillLearn temp : skills)
                        if (temp.getMinLevel() <= player.getLevel()) {
                            int knownLevel = player.getSkillLevel(temp.id());
                            if (knownLevel == -1)
                                skillLearnMap.put(temp.id(), temp);
                        }

                    return skillLearnMap.values();
                }
            case FISHING:
                skills = FISHING_SKILL_TREE.get(player.getRace().ordinal());
                if (skills == null) {
                    LOG.info("skill tree for race " + player.getRace().ordinal() + " is not defined !");
                    return Collections.emptyList();
                }
                return getAvaliableList(skills, player.getAllSkills(), level);
            case CLAN:
                skills = PLEDGE_SKILL_TREE;
                Collection<Skill> skls = player.getClan().getSkills(); //TODO [VISTALL] придумать другой способ

                return getAvaliableList(skills, skls, level);
            case SUB_UNIT:
                skills = SUB_UNIT_SKILL_TREE;
                Collection<Skill> st = subUnit.getSkills(); //TODO [VISTALL] придумать другой способ

                return getAvaliableList(skills, st, level);
            case CERTIFICATION:
                skills = CERTIFICATION_SKILL_TREE;
                if (player == null)
                    return skills;
                else
                    return getAvaliableList(skills, player.getAllSkills(), level);
            default:
                return Collections.emptyList();
        }
    }

    public void log() {
        LOG.info("loadFile " + NORMAL_SKILL_TREE.size() + " normal learns for " + NORMAL_SKILL_TREE.size() + " classes.");
        LOG.info("loadFile " + TRANSFER_SKILL_TREE.size() + " transfer learns for " + TRANSFER_SKILL_TREE.size() + " classes.");
        LOG.info("loadFile " + TRANSFORMATION_SKILL_TREE.size() + " transformation learns for " + TRANSFORMATION_SKILL_TREE.size() + " races.");
        LOG.info("loadFile " + FISHING_SKILL_TREE.size() + " fishing learns for " + FISHING_SKILL_TREE.size() + " races.");
        LOG.info("loadFile " + CERTIFICATION_SKILL_TREE.size() + " certification learns.");
        LOG.info("loadFile " + COLLECTION_SKILL_TREE.size() + " collection learns.");
        LOG.info("loadFile " + PLEDGE_SKILL_TREE.size() + " pledge learns.");
        LOG.info("loadFile " + SUB_UNIT_SKILL_TREE.size() + " sub unit learns.");
    }

    public void clear() {
        NORMAL_SKILL_TREE.clear();
        FISHING_SKILL_TREE.clear();
        TRANSFER_SKILL_TREE.clear();
        CERTIFICATION_SKILL_TREE.clear();
        COLLECTION_SKILL_TREE.clear();
        PLEDGE_SKILL_TREE.clear();
        SUB_UNIT_SKILL_TREE.clear();
    }
}
