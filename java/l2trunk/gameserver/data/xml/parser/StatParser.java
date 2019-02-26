package l2trunk.gameserver.data.xml.parser;

import l2trunk.gameserver.model.entity.residence.ResidenceType;
import l2trunk.gameserver.stats.StatTemplate;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.conditions.*;
import l2trunk.gameserver.stats.funcs.FuncTemplate;
import l2trunk.gameserver.stats.triggers.TriggerInfo;
import l2trunk.gameserver.stats.triggers.TriggerType;
import l2trunk.gameserver.templates.item.ArmorTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.*;

public final class StatParser {
    public static final Logger LOG = LoggerFactory.getLogger(StatParser.class);

    private StatParser() {
    }

    static Condition parseFirstCond(Element sub) {
        List<Element> e = sub.elements();
        if (e.isEmpty())
            return null;
        Element element = e.get(0);

        return parseCond(element);
    }

    private static Condition parseCond(Element element) {
        String name = element.getName();
        if (name.equalsIgnoreCase("and"))
            return parseLogicAnd(element);
        else if (name.equalsIgnoreCase("or"))
            return parseLogicOr(element);
        else if (name.equalsIgnoreCase("not"))
            return parseLogicNot(element);
        else if (name.equalsIgnoreCase("target"))
            return parseTargetCondition(element);
        else if (name.equalsIgnoreCase("getPlayer"))
            return parsePlayerCondition(element);
        else if (name.equalsIgnoreCase("using"))
            return parseUsingCondition(element);
        else if (name.equalsIgnoreCase("zone"))
            return parseZoneCondition(element);

        return null;
    }

    private static Condition parseLogicAnd(Element n) {
        ConditionLogicAnd cond = new ConditionLogicAnd();
        for (Iterator<Element> iterator = n.elementIterator(); iterator.hasNext(); ) {
            Element condElement = iterator.next();
            cond.add(parseCond(condElement));
        }

        if (cond.conditions == null || cond.conditions.size() == 0)
            LOG.error("Empty <and> condition in " + n);
        return cond;
    }

    private static Condition parseLogicOr(Element n) {
        ConditionLogicOr cond = new ConditionLogicOr();
        for (Iterator<Element> iterator = n.elementIterator(); iterator.hasNext(); ) {
            Element condElement = iterator.next();
            cond.add(parseCond(condElement));
        }

        if (cond.conditions == null || cond.conditions.size() == 0)
            LOG.error("Empty <or> condition in " + n);
        return cond;
    }

    private static Condition parseLogicNot(Element n) {
        if (!n.elements().isEmpty())
            return new ConditionLogicNot(parseCond(n.elements().get(0)));
        LOG.error("Empty <not> condition in " + n);
        return null;
    }

    private static Condition parseTargetCondition(Element element) {
        Condition cond = null;
        for (Iterator<Attribute> iterator = element.attributeIterator(); iterator.hasNext(); ) {
            Attribute attribute = iterator.next();
            String name = attribute.getName();
            String value = attribute.getValue();
            if (name.equalsIgnoreCase("pvp"))
                cond = joinAnd(cond, new ConditionTargetPlayable(toBoolean(value)));
        }

        return cond;
    }

    private static Condition parseZoneCondition(Element element) {
        Condition cond = null;
        for (Iterator<Attribute> iterator = element.attributeIterator(); iterator.hasNext(); ) {
            Attribute attribute = iterator.next();
            String name = attribute.getName();
            String value = attribute.getValue();
            if (name.equalsIgnoreCase("type"))
                cond = joinAnd(cond, new ConditionZoneType(value));
        }

        return cond;
    }

    private static Condition parsePlayerCondition(Element element) {
        Condition cond = null;
        for (Iterator<Attribute> iterator = element.attributeIterator(); iterator.hasNext(); ) {
            Attribute attribute = iterator.next();
            String name = attribute.getName();
            String value = attribute.getValue();
            if ("residence".equalsIgnoreCase(name)) {
                String[] st = value.split(";");
                cond = joinAnd(cond, new ConditionPlayerResidence(Integer.parseInt(st[1]), ResidenceType.valueOf(st[0])));
            } else if (name.equalsIgnoreCase("classId"))
                cond = joinAnd(cond, new ConditionPlayerClassId(value));
            else if ("olympiad".equalsIgnoreCase(name))
                cond = joinAnd(cond, new ConditionPlayerOlympiad(Boolean.valueOf(value)));
            else if (name.equalsIgnoreCase("instance_zone"))
                cond = joinAnd(cond, new ConditionPlayerInstanceZone(toInt(value)));
            else if (name.equalsIgnoreCase("race"))
                cond = joinAnd(cond, new ConditionPlayerRace(value));
            else if (name.equalsIgnoreCase("damage")) {
                String[] st = value.split(";");
                cond = joinAnd(cond, new ConditionPlayerMinMaxDamage(toDouble(st[0]), toDouble(st[1])));
            }
        }

        return cond;
    }

    private static Condition parseUsingCondition(Element element) {
        Condition cond = null;
        for (Iterator<Attribute> iterator = element.attributeIterator(); iterator.hasNext(); ) {
            Attribute attribute = iterator.next();
            String name = attribute.getName();
            String value = attribute.getValue();
            if (name.equalsIgnoreCase("slotitem")) {
                StringTokenizer st = new StringTokenizer(value, ";");
                int id = Integer.parseInt(st.nextToken().trim());
                int slot = Integer.parseInt(st.nextToken().trim());
                int enchant = 0;
                if (st.hasMoreTokens())
                    enchant = Integer.parseInt(st.nextToken().trim());
                cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
            } else if (name.equalsIgnoreCase("kind") || name.equalsIgnoreCase("weapon")) {
                long mask = 0;
                StringTokenizer st = new StringTokenizer(value, ",");
                tokens:
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    for (WeaponTemplate.WeaponType wt : WeaponTemplate.WeaponType.VALUES)
                        if (wt.toString().equalsIgnoreCase(item)) {
                            mask |= wt.mask();
                            continue tokens;
                        }
                    for (ArmorTemplate.ArmorType at : ArmorTemplate.ArmorType.VALUES)
                        if (at.toString().equalsIgnoreCase(item)) {
                            mask |= at.mask();
                            continue tokens;
                        }

                    LOG.error("Invalid item kind: \"" + item + "\" in " + element);
                }
                if (mask != 0)
                    cond = joinAnd(cond, new ConditionUsingItemType(mask));
            } else if (name.equalsIgnoreCase("skill"))
                cond = joinAnd(cond, new ConditionUsingSkill(Integer.parseInt(value)));
        }
        return cond;
    }

    private static Condition joinAnd(Condition cond, Condition c) {
        if (cond == null)
            return c;
        if (cond instanceof ConditionLogicAnd) {
            ((ConditionLogicAnd) cond).add(c);
            return cond;
        }
        ConditionLogicAnd and = new ConditionLogicAnd();
        and.add(cond);
        and.add(c);
        return and;
    }

    static void parseFor(Element forElement, StatTemplate template) {
        for (Iterator<Element> iterator = forElement.elementIterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            final String elementName = element.getName();
            if (elementName.equalsIgnoreCase("add"))
                attachFunc(element, template, "Add");
            else if (elementName.equalsIgnoreCase("set"))
                attachFunc(element, template, "Set");
            else if (elementName.equalsIgnoreCase("sub"))
                attachFunc(element, template, "Sub");
            else if (elementName.equalsIgnoreCase("mul"))
                attachFunc(element, template, "Mul");
            else if (elementName.equalsIgnoreCase("div"))
                attachFunc(element, template, "Div");
            else if (elementName.equalsIgnoreCase("enchant"))
                attachFunc(element, template, "Enchant");
        }
    }

    static void parseTriggers(Element f, StatTemplate triggerable) {
        for (Iterator<Element> iterator = f.elementIterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            int id = parseNumber(element.attributeValue("id")).intValue();
            int level = parseNumber(element.attributeValue("level")).intValue();
            TriggerType t = TriggerType.valueOf(element.attributeValue("type"));
            double chance = parseNumber(element.attributeValue("chance")).doubleValue();

            TriggerInfo trigger = new TriggerInfo(id, level, t, chance);

            triggerable.addTrigger(trigger);
            for (Iterator<Element> subIterator = element.elementIterator(); subIterator.hasNext(); ) {
                Element subElement = subIterator.next();

                Condition condition = parseFirstCond(subElement);
                if (condition != null)
                    trigger.addCondition(condition);
            }
        }
    }

    private static void attachFunc(Element n, StatTemplate template, String name) {
        Stats stat = Stats.valueOfXml(n.attributeValue("stat"));
        String order = n.attributeValue("order");
        int ord = parseNumber(order).intValue();
        Condition applyCond = parseFirstCond(n);
        double val = 0;
        if (n.attributeValue("value") != null)
            val = parseNumber(n.attributeValue("value")).doubleValue();

        template.attachFunc(new FuncTemplate(applyCond, name, stat, ord, val));
    }

    static Number parseNumber(String value) {
        if (value.indexOf('.') == -1) {
            int radix = 10;
            if (value.length() > 2 && value.substring(0, 2).equalsIgnoreCase("0x")) {
                value = value.substring(2);
                radix = 16;
            }
            return Integer.valueOf(value, radix);
        }
        return Double.valueOf(value);
    }

}
