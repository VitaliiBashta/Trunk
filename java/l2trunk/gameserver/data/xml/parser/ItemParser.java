package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.OptionDataHolder;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.conditions.Condition;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.OptionDataTemplate;
import l2trunk.gameserver.templates.item.*;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum ItemParser /*extends StatParser<ItemHolder>*/ {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    Path xml = Config.DATAPACK_ROOT.resolve("data/items/");

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + ItemHolder.size() + " items ");
    }

    private void readData(Element rootElement) {
        for (Iterator<Element> itemIterator = rootElement.elementIterator(); itemIterator.hasNext(); ) {
            Element itemElement = itemIterator.next();
            StatsSet set = new StatsSet();
            set.set("item_id", itemElement.attributeValue("id"));
            set.set("name", itemElement.attributeValue("name"));
            set.set("add_name", itemElement.attributeValue("add_name", ""));

            int slot = 0;
            for (Iterator<Element> subIterator = itemElement.elementIterator(); subIterator.hasNext(); ) {
                Element subElement = subIterator.next();
                String subName = subElement.getName();
                if (subName.equalsIgnoreCase("set")) {
                    set.set(subElement.attributeValue("name"), subElement.attributeValue("value"));
                } else if (subName.equalsIgnoreCase("equip")) {
                    for (Iterator<Element> slotIterator = subElement.elementIterator(); slotIterator.hasNext(); ) {
                        Element slotElement = slotIterator.next();
                        Bodypart bodypart = Bodypart.valueOf(slotElement.attributeValue("id"));
                        if (bodypart.getReal() != null) {
                            slot = bodypart.mask();
                        } else {
                            slot |= bodypart.mask();
                        }
                    }
                }
            }

            set.set("bodypart", slot);

            ItemTemplate template;
            try {
                if ("weapon".equalsIgnoreCase(itemElement.getName())) {
                    if (!set.isSet("class")) {
                        if ((slot & ItemTemplate.SLOT_L_HAND) > 0) {
                            set.set("class", ItemTemplate.ItemClass.ARMOR);
                        } else {
                            set.set("class", ItemTemplate.ItemClass.WEAPON);
                        }
                    }
                    template = new WeaponTemplate(set);
                } else if ("armor".equalsIgnoreCase(itemElement.getName())) {
                    if (!set.isSet("class")) {
                        if ((slot & ItemTemplate.SLOTS_ARMOR) > 0) {
                            set.set("class", ItemTemplate.ItemClass.ARMOR);
                        } else if ((slot & ItemTemplate.SLOTS_JEWELRY) > 0) {
                            set.set("class", ItemTemplate.ItemClass.JEWELRY);
                        } else {
                            set.set("class", ItemTemplate.ItemClass.ACCESSORY);
                        }
                    }
                    template = new ArmorTemplate(set);
                } else {
                    template = new EtcItemTemplate(set);
                }
            } catch (RuntimeException e) {
                LOG.warn("Fail create item: " + set.get("item_id"), e);
                continue;
            }

            for (Iterator<Element> subIterator = itemElement.elementIterator(); subIterator.hasNext(); ) {
                Element subElement = subIterator.next();
                String subName = subElement.getName();
                if (subName.equalsIgnoreCase("for")) {
                    StatParser.parseFor(subElement, template);
                } else if (subName.equalsIgnoreCase("triggers")) {
                    StatParser.parseTriggers(subElement, template);
                } else {
                    if (subName.equalsIgnoreCase("skills")) {
                        for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                            Element nextElement = nextIterator.next();
                            int id = toInt(nextElement.attributeValue("id"));
                            int level = toInt(nextElement.attributeValue("level"));

                            Skill skill = SkillTable.INSTANCE.getInfo(id, level);

                            if (skill != null) {
                                template.attachSkill(skill);
                            } else {
                                LOG.info("Skill not found(" + id + "," + level + ") for item:" + set.getObject("item_id") + "; element:" + itemElement);
                            }
                        }
                    } else if ("enchant4_skill".equalsIgnoreCase(subName)) {
                        int id = toInt(subElement.attributeValue("id"));
                        int level = toInt(subElement.attributeValue("level"));

                        Skill skill = SkillTable.INSTANCE.getInfo(id, level);
                        if (skill != null) {
                            template.setEnchant4Skill(skill);
                        }
                    } else if ("cond".equalsIgnoreCase(subName)) {
                        Condition condition = StatParser.parseFirstCond(subElement);
                        if (condition != null) {
                            int msgId = StatParser.parseNumber(subElement.attributeValue("msgId")).intValue();
                            condition.setSystemMsg(msgId);

                            template.setCondition(condition);
                        }
                    } else if ("attributes".equalsIgnoreCase(subName)) {
                        int[] attributes = new int[6];
                        for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                            Element nextElement = nextIterator.next();

                            if (nextElement.getName().equalsIgnoreCase("attribute")) {
                                l2trunk.gameserver.model.base.Element element = l2trunk.gameserver.model.base.Element.getElement(nextElement.attributeValue("element"));
                                attributes[element.getId()] = toInt(nextElement.attributeValue("value"));
                            }
                        }
                        template.setBaseAtributeElements(attributes);
                    } else {
                        if (subName.equalsIgnoreCase("capsuled_items")) {
                            for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                                Element nextElement = nextIterator.next();
                                if (nextElement.getName().equalsIgnoreCase("capsuled_item")) {
                                    int c_item_id = toInt(nextElement.attributeValue("id"));
                                    int c_min_count = toInt(nextElement.attributeValue("min_count"));
                                    int c_max_count = toInt(nextElement.attributeValue("max_count"));
                                    double c_chance = Double.parseDouble(nextElement.attributeValue("chance"));
                                    template.addCapsuledItem(new ItemTemplate.CapsuledItem(c_item_id, c_min_count, c_max_count, c_chance));
                                }
                            }
                        } else if (subName.equalsIgnoreCase("enchant_options")) {
                            for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                                Element nextElement = nextIterator.next();

                                if (nextElement.getName().equalsIgnoreCase("level")) {
                                    int val = toInt(nextElement.attributeValue("val"));

                                    int i = 0;
                                    int[] options = new int[3];
                                    for (Element optionElement : nextElement.elements()) {
                                        OptionDataTemplate optionData = OptionDataHolder.getTemplate(toInt(optionElement.attributeValue("id")));
                                        if (optionData == null) {
                                            LOG.error("Not found option_data for id: " + optionElement.attributeValue("id") + "; item_id: " + set.get("item_id"));
                                        } else {
                                            options[(i++)] = optionData.id;
                                        }
                                    }
                                    template.addEnchantOptions(val, options);
                                }
                            }
                        }
                    }
                }
            }
            ItemHolder.addItem(template);
        }
    }
}
