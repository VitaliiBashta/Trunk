package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.TeleportLocation;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.reward.RewardData;
import l2trunk.gameserver.model.reward.RewardGroup;
import l2trunk.gameserver.model.reward.RewardList;
import l2trunk.gameserver.model.reward.RewardType;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.AbsorbInfo;
import l2trunk.gameserver.templates.npc.Faction;
import l2trunk.gameserver.templates.npc.MinionData;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum NpcParser {
    INSTANCE;
    private static Path xmldir = Config.DATAPACK_ROOT.resolve("data/npc/");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xmldir).forEach(this::readData);
        LOG.info("Loaded  " + NpcHolder.size() + " item(s)");
    }

    public void reload() {
        LOG.info("reload start...");
        NpcHolder.clear();
        load();
    }

    private void readData(org.dom4j.Element rootElement) {
        for (Iterator<org.dom4j.Element> npcIterator = rootElement.elementIterator(); npcIterator.hasNext(); ) {
            org.dom4j.Element npcElement = npcIterator.next();
            int npcId = toInt(npcElement.attributeValue("id"));
            int templateId = npcElement.attributeValue("template_id") == null ? 0 : toInt(npcElement.attributeValue("template_id"));
            String name = npcElement.attributeValue("name");
            String title = npcElement.attributeValue("title");

            StatsSet set = new StatsSet();
            set.set("npcId", npcId);
            set.set("displayId", templateId);
            set.set("name", name);
            set.set("title", title);
            set.set("baseCpReg", 0);
            set.set("baseCpMax", 0);

            for (Iterator<org.dom4j.Element> firstIterator = npcElement.elementIterator(); firstIterator.hasNext(); ) {
                org.dom4j.Element firstElement = firstIterator.next();
                if (firstElement.getName().equalsIgnoreCase("set")) {
                    set.set(firstElement.attributeValue("name"), firstElement.attributeValue("value"));
                } else if (firstElement.getName().equalsIgnoreCase("equip")) {
                    for (Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext(); ) {
                        org.dom4j.Element eElement = eIterator.next();
                        set.set(eElement.getName(), eElement.attributeValue("item_id"));
                    }
                } else if (firstElement.getName().equalsIgnoreCase("ai_params")) {
                    StatsSet ai = new StatsSet();
                    for (Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext(); ) {
                        org.dom4j.Element eElement = eIterator.next();
                        ai.set(eElement.attributeValue("name"), eElement.attributeValue("value"));
                    }
                    set.set("aiParams", ai);
                } else if (firstElement.getName().equalsIgnoreCase("attributes")) {
                    int[] attributeAttack = new int[6];
                    int[] attributeDefence = new int[6];
                    for (Iterator<org.dom4j.Element> eIterator = firstElement.elementIterator(); eIterator.hasNext(); ) {
                        org.dom4j.Element eElement = eIterator.next();
                        Element element;
                        if (eElement.getName().equalsIgnoreCase("defence")) {
                            element = Element.getElement(eElement.attributeValue("attribute"));
                            attributeDefence[element.getId()] = toInt(eElement.attributeValue("value"));
                        } else if (eElement.getName().equalsIgnoreCase("attack")) {
                            element = Element.getElement(eElement.attributeValue("attribute"));
                            attributeAttack[element.getId()] = toInt(eElement.attributeValue("value"));
                        }
                    }

                    set.set("baseAttributeAttack", attributeAttack);
                    set.set("baseAttributeDefence", attributeDefence);
                }
            }

            NpcTemplate template = new NpcTemplate(set);

            for (Iterator<org.dom4j.Element> secondIterator = npcElement.elementIterator(); secondIterator.hasNext(); ) {
                org.dom4j.Element secondElement = secondIterator.next();
                String nodeName = secondElement.getName();
                if (nodeName.equalsIgnoreCase("faction")) {
                    String factionId = secondElement.attributeValue("name");
                    Faction faction = new Faction(factionId);
                    int factionRange = toInt(secondElement.attributeValue("range"));
                    faction.setRange(factionRange);
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        final org.dom4j.Element nextElement = nextIterator.next();
                        int ignoreId = toInt(nextElement.attributeValue("npc_id"));
                        faction.addIgnoreNpcId(ignoreId);
                    }
                    template.setFaction(faction);
                } else if (nodeName.equalsIgnoreCase("rewardlist")) {
                    RewardType type = RewardType.valueOf(secondElement.attributeValue("type"));
                    boolean autoLoot = secondElement.attributeValue("auto_loot") != null && Boolean.parseBoolean(secondElement.attributeValue("auto_loot"));
                    RewardList list = new RewardList(type, autoLoot);

                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        final org.dom4j.Element nextElement = nextIterator.next();
                        final String nextName = nextElement.getName();
                        if (nextName.equalsIgnoreCase("group")) {
                            double enterChance = nextElement.attributeValue("chance") == null ? RewardList.MAX_CHANCE : Double.parseDouble(nextElement.attributeValue("chance")) * 10000;

                            RewardGroup group = new RewardGroup(enterChance * Config.RATE_CHANCE_GROUP_DROP_ITEMS);
                            for (Iterator<org.dom4j.Element> rewardIterator = nextElement.elementIterator(); rewardIterator.hasNext(); ) {
                                org.dom4j.Element rewardElement = rewardIterator.next();
                                RewardData data = parseReward(rewardElement, 1);
                                if (type == RewardType.SWEEP || type == RewardType.NOT_RATED_NOT_GROUPED)
                                    LOG.warn("Can't loadFile rewardlist from group: " + npcId + "; type: " + type);
                                else
                                    group.addData(data);
                            }

                            list.add(group);
                        } else if (nextName.equalsIgnoreCase("reward")) {
                            if (type != RewardType.SWEEP && type != RewardType.NOT_RATED_NOT_GROUPED) {
                                LOG.warn("Reward can't be without group(and not grouped): " + npcId + "; type: " + type);
                                continue;
                            }
                            RewardData data;
                            if (type == RewardType.SWEEP)
                                data = parseReward(nextElement, 2);
                            else
                                data = parseReward(nextElement, 3);
                            RewardGroup g = new RewardGroup(RewardList.MAX_CHANCE);
                            g.addData(data);
                            list.add(g);
                        }
                    }

                    if (type == RewardType.RATED_GROUPED || type == RewardType.NOT_RATED_GROUPED)
                        if (!list.validate())
                            LOG.warn("Problems with rewardlist for npc: " + npcId + "; type: " + type);

                    template.putRewardList(type, list);
                } else if ("skills".equalsIgnoreCase(nodeName)) {
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        org.dom4j.Element nextElement = nextIterator.next();
                        int id = toInt(nextElement.attributeValue("id"));
                        int level = toInt(nextElement.attributeValue("level"));

                        Skill skill = SkillTable.INSTANCE.getInfo(id, level);
                        // Для определения расы используется скилл 4416
                        if (id == 4416) template.setRace(level);

                        if (skill != null) template.addSkill(skill);
                    }
                } else if ("minions".equalsIgnoreCase(nodeName)) {
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        org.dom4j.Element nextElement = nextIterator.next();
                        int id = toInt(nextElement.attributeValue("npc_id"));
                        int count = toInt(nextElement.attributeValue("count"));

                        template.addMinion(new MinionData(id, count));
                    }
                } else if ("teach_classes".equalsIgnoreCase(nodeName)) {
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        org.dom4j.Element nextElement = nextIterator.next();

                        int id = toInt(nextElement.attributeValue("id"));

                        template.addTeachInfo(ClassId.VALUES.get(id));
                    }
                } else if ("absorblist".equalsIgnoreCase(nodeName)) {
                    for (Iterator<org.dom4j.Element> nextIterator = secondElement.elementIterator(); nextIterator.hasNext(); ) {
                        org.dom4j.Element nextElement = nextIterator.next();

                        int chance = toInt(nextElement.attributeValue("chance"));
                        int cursedChance = nextElement.attributeValue("cursed_chance") == null ? 0 : toInt(nextElement.attributeValue("cursed_chance"));
                        int minLevel = toInt(nextElement.attributeValue("min_level"));
                        int maxLevel = toInt(nextElement.attributeValue("max_level"));
                        boolean skill = nextElement.attributeValue("skill") != null && Boolean.parseBoolean(nextElement.attributeValue("skill"));
                        AbsorbInfo.AbsorbType absorbType = AbsorbInfo.AbsorbType.valueOf(nextElement.attributeValue("type"));

                        template.addAbsorbInfo(new AbsorbInfo(skill, absorbType, chance, cursedChance, minLevel, maxLevel));
                    }
                } else if ("teleportlist".equalsIgnoreCase(nodeName)) {
                    for (Iterator<org.dom4j.Element> sublistIterator = secondElement.elementIterator(); sublistIterator.hasNext(); ) {
                        org.dom4j.Element subListElement = sublistIterator.next();
                        int id = toInt(subListElement.attributeValue("id"));
                        List<TeleportLocation> list = new ArrayList<>();
                        for (Iterator<org.dom4j.Element> targetIterator = subListElement.elementIterator(); targetIterator.hasNext(); ) {
                            org.dom4j.Element targetElement = targetIterator.next();
                            int itemId = toInt(targetElement.attributeValue("item_id", "57"));
                            long price = toInt(targetElement.attributeValue("price"));
                            int npcStringId = toInt(targetElement.attributeValue("name"));
                            String nameString = targetElement.attributeValue("StringName");
                            String nameStringLang = targetElement.attributeValue("StringNameLang");
                            int castleId = toInt(targetElement.attributeValue("castle_id", "0"));
                            TeleportLocation loc = new TeleportLocation(itemId, price, npcStringId, nameString, nameStringLang, castleId);
                            loc.set(Location.of(targetElement.attributeValue("loc")));
                            list.add(loc);
                        }
                        template.addTeleportList(id, list);
                    }
                }
            }
            NpcHolder.addTemplate(template);
        }
    }

    private RewardData parseReward(org.dom4j.Element rewardElement, int id) {
        int itemId = toInt(rewardElement.attributeValue("item_id"));
        int min = toInt(rewardElement.attributeValue("min"));
        int max = toInt(rewardElement.attributeValue("max"));
        // переводим в системный вид
        double chance = Double.parseDouble(rewardElement.attributeValue("chance")) * 10000;
        double chance_dop = chance * Config.RATE_CHANCE_DROP_ITEMS;
        double chance_h = chance * Config.RATE_CHANCE_DROP_HERBS;
        double chance_sp = chance * Config.RATE_CHANCE_SPOIL;
        double chance_weapon = chance * Config.RATE_CHANCE_DROP_WEAPON_ARMOR_ACCESSORY;
        double chance_weapon_sp = chance * Config.RATE_CHANCE_SPOIL_WEAPON_ARMOR_ACCESSORY;
        double chance_epolet = chance * Config.RATE_CHANCE_DROP_EPOLET;
        if (chance_dop > 1000000)
            chance_dop = 1000000;
        if (chance_h > 1000000)
            chance_h = 1000000;
        if (chance_sp > 1000000)
            chance_sp = 1000000;
        if (chance_weapon > 1000000)
            chance_weapon = 1000000;
        if (chance_weapon_sp > 1000000)
            chance_weapon_sp = 1000000;
        if (chance_epolet > 1000000)
            chance_epolet = 1000000;

        RewardData data = new RewardData(itemId);
        if (id == 1) {
            if (data.getItem().isCommonItem())
                data.setChance(chance * Config.RATE_DROP_COMMON_ITEMS);
            else if (data.getItem().isHerb())
                data.setChance(chance_h);
            else if (data.getItem().isWeapon() || data.getItem().isArmor() || data.getItem().isAccessory())
                data.setChance(chance_weapon);
            else if (data.getItem().isEpolets())
                data.setChance(chance_epolet);
            else
                data.setChance(chance_dop);
        } else if (id == 2) {
            if (data.getItem().isWeapon() || data.getItem().isArmor() || data.getItem().isAccessory())
                data.setChance(chance_weapon_sp);
            else
                data.setChance(chance_sp);
        } else if (id == 3)
            data.setChance(chance);

        data.setMinDrop(min);
        data.setMaxDrop(max);

        return data;
    }
}
