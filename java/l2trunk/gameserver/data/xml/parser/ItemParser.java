package l2trunk.gameserver.data.xml.parser;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.OptionDataHolder;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.conditions.Condition;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.OptionDataTemplate;
import l2trunk.gameserver.templates.StatsSet;
import l2trunk.gameserver.templates.item.*;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.Iterator;

public final class ItemParser extends StatParser<ItemHolder> {
    private static final ItemParser _instance = new ItemParser();

    private ItemParser() {
        super(ItemHolder.getInstance());
    }

    public static ItemParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLDir() {
        return Config.DATAPACK_ROOT.resolve("data/items/");
    }

    @Override
    public String getDTDFileName() {
        return "item.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
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
                if (itemElement.getName().equalsIgnoreCase("weapon")) {
                    if (!set.containsKey("class")) {
                        if ((slot & ItemTemplate.SLOT_L_HAND) > 0) {
                            set.set("class", ItemTemplate.ItemClass.ARMOR);
                        } else {
                            set.set("class", ItemTemplate.ItemClass.WEAPON);
                        }
                    }
                    template = new WeaponTemplate(set);
                } else if (itemElement.getName().equalsIgnoreCase("armor")) {
                    if (!set.containsKey("class")) {
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
                // for(Map.Entry<String, Object> entry : set.entrySet())
                // {
                // info("set " + entry.getKey() + ":" + entry.getValue());
                // }
                warn("Fail create item: " + set.get("item_id"), e);
                continue;
            }

            for (Iterator<Element> subIterator = itemElement.elementIterator(); subIterator.hasNext(); ) {
                Element subElement = subIterator.next();
                String subName = subElement.getName();
                if (subName.equalsIgnoreCase("for")) {
                    parseFor(subElement, template);
                } else if (subName.equalsIgnoreCase("triggers")) {
                    parseTriggers(subElement, template);
                } else {
                    if (subName.equalsIgnoreCase("skills")) {
                        for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                            Element nextElement = nextIterator.next();
                            int id = Integer.parseInt(nextElement.attributeValue("id"));
                            int level = Integer.parseInt(nextElement.attributeValue("level"));

                            Skill skill = SkillTable.getInstance().getInfo(id, level);

                            if (skill != null) {
                                template.attachSkill(skill);
                            } else {
                                info("Skill not found(" + id + "," + level + ") for item:" + set.getObject("item_id") + "; file:" + getCurrentFileName());
                            }
                        }
                    } else if (subName.equalsIgnoreCase("enchant4_skill")) {
                        int id = Integer.parseInt(subElement.attributeValue("id"));
                        int level = Integer.parseInt(subElement.attributeValue("level"));

                        Skill skill = SkillTable.getInstance().getInfo(id, level);
                        if (skill != null) {
                            template.setEnchant4Skill(skill);
                        }
                    } else if (subName.equalsIgnoreCase("cond")) {
                        Condition condition = parseFirstCond(subElement);
                        if (condition != null) {
                            int msgId = parseNumber(subElement.attributeValue("msgId")).intValue();
                            condition.setSystemMsg(msgId);

                            template.setCondition(condition);
                        }
                    } else if (subName.equalsIgnoreCase("attributes")) {
                        int[] attributes = new int[6];
                        for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                            Element nextElement = nextIterator.next();

                            if (nextElement.getName().equalsIgnoreCase("attribute")) {
                                l2trunk.gameserver.model.base.Element element = l2trunk.gameserver.model.base.Element.getElementByName(nextElement.attributeValue("element"));
                                attributes[element.getId()] = Integer.parseInt(nextElement.attributeValue("value"));
                            }
                        }
                        template.setBaseAtributeElements(attributes);
                    } else {
                        if (subName.equalsIgnoreCase("capsuled_items")) {
                            for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                                Element nextElement = nextIterator.next();
                                if (nextElement.getName().equalsIgnoreCase("capsuled_item")) {
                                    int c_item_id = Integer.parseInt(nextElement.attributeValue("id"));
                                    int c_min_count = Integer.parseInt(nextElement.attributeValue("min_count"));
                                    int c_max_count = Integer.parseInt(nextElement.attributeValue("max_count"));
                                    double c_chance = Double.parseDouble(nextElement.attributeValue("chance"));
                                    template.addCapsuledItem(new ItemTemplate.CapsuledItem(c_item_id, c_min_count, c_max_count, c_chance));
                                }
                            }
                        } else if (subName.equalsIgnoreCase("enchant_options")) {
                            for (Iterator<Element> nextIterator = subElement.elementIterator(); nextIterator.hasNext(); ) {
                                Element nextElement = nextIterator.next();

                                if (nextElement.getName().equalsIgnoreCase("level")) {
                                    int val = Integer.parseInt(nextElement.attributeValue("val"));

                                    int i = 0;
                                    int[] options = new int[3];
                                    for (Element optionElement : nextElement.elements()) {
                                        OptionDataTemplate optionData = OptionDataHolder.getInstance().getTemplate(Integer.parseInt(optionElement.attributeValue("id")));
                                        if (optionData == null) {
                                            error("Not found option_data for id: " + optionElement.attributeValue("id") + "; item_id: " + set.get("item_id"));
                                        } else {
                                            options[(i++)] = optionData.getId();
                                        }
                                    }
                                    template.addEnchantOptions(val, options);
                                }
                            }
                        }
                    }
                }
            }
            getHolder().addItem(template);
        }
    }

    @Override
    protected Object getTableValue(String name) {
        return null;
    }
}
