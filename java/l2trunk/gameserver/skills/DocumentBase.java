package l2trunk.gameserver.skills;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.crypt.CryptUtil;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.stats.StatTemplate;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.conditions.*;
import l2trunk.gameserver.stats.conditions.ConditionGameTime.CheckGameTime;
import l2trunk.gameserver.stats.conditions.ConditionPlayerRiding.CheckPlayerRiding;
import l2trunk.gameserver.stats.conditions.ConditionPlayerState.CheckPlayerState;
import l2trunk.gameserver.stats.funcs.FuncTemplate;
import l2trunk.gameserver.stats.triggers.TriggerInfo;
import l2trunk.gameserver.stats.triggers.TriggerType;
import l2trunk.gameserver.templates.item.ArmorTemplate.ArmorType;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2trunk.gameserver.utils.PositionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toBoolean;

abstract class DocumentBase {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentBase.class);

    private final Path file;
    Map<String, List<String>> tables;

    DocumentBase(Path file) {
        this.file = file;
        tables = new HashMap<>();
    }

    void parse() {
        Document doc;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setIgnoringComments(true);
            InputStream stream = Files.newInputStream(file);
            InputStream output;
            if ((byte) stream.read() == 0x00) {
                byte[] bytes = new byte[0];
                output = new ByteArrayInputStream(bytes);
                output = CryptUtil.decrypt(stream, output);
            } else
                output = Files.newInputStream(file);
            doc = factory.newDocumentBuilder().parse(output);
        } catch (FileNotFoundException e) {
            LOG.error("Didn't find " + file, e);
            return;
        } catch (IOException | ParserConfigurationException | SAXException e) {
            LOG.error("Error loading file " + file, e);
            return;
        }

        try {
            parseDocument(doc);
        } catch (RuntimeException e) {
            LOG.error("Error in file " + file, e);
        }
    }

    protected abstract void parseDocument(Document doc);

    protected abstract String getTableValue(String name);

    protected abstract String getTableValue(String name, int idx);

    void resetTable() {
        tables = new HashMap<>();
    }

    private void setTable(String name, List<String> table) {
        tables.put(name, table);
    }

    void parseTemplate(Node n, StatTemplate template) {
        n = n.getFirstChild();
        if (n == null)
            return;
        for (; n != null; n = n.getNextSibling()) {
            String nodeName = n.getNodeName();
            if ("add".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Add");
            else if ("sub".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Sub");
            else if ("mul".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Mul");
            else if ("div".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Div");
            else if ("set".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Set");
            else if ("enchant".equalsIgnoreCase(nodeName))
                attachFunc(n, template, "Enchant");
            else if ("effect".equalsIgnoreCase(nodeName)) {
                if (template instanceof EffectTemplate)
                    throw new RuntimeException("Nested effects");
                attachEffect(n, template);
            } else if (template instanceof EffectTemplate)
                if ("def".equalsIgnoreCase(nodeName))
                    parseBeanSet(n, ((EffectTemplate) template).getParam(), ((Skill) ((EffectTemplate) template).getParam().getObject("object")).level);
                else {
                    Condition cond = parseCondition(n);
                    if (cond != null)
                        ((EffectTemplate) template).attachCond(cond);
                }
        }
    }

    void parseTrigger(Node n, StatTemplate template) {
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("trigger".equalsIgnoreCase(n.getNodeName())) {
                NamedNodeMap map = n.getAttributes();

                int id = parseNumber(map.getNamedItem("id").getNodeValue()).intValue();
                int level = parseNumber(map.getNamedItem("level").getNodeValue()).intValue();
                TriggerType t = TriggerType.valueOf(map.getNamedItem("type").getNodeValue());
                double chance = parseNumber(map.getNamedItem("chance").getNodeValue()).doubleValue();

                TriggerInfo trigger = new TriggerInfo(id, level, t, chance);

                for (Node n2 = n.getFirstChild(); n2 != null; n2 = n2.getNextSibling()) {
                    Condition condition = parseCondition(n.getFirstChild());
                    if (condition != null)
                        trigger.addCondition(condition);
                }

                template.addTrigger(trigger);
            }
        }
    }

    private void attachFunc(Node n, StatTemplate template, String name) {
        Stats stat = Stats.valueOfXml(n.getAttributes().getNamedItem("stat").getNodeValue());
        String order = n.getAttributes().getNamedItem("order").getNodeValue();
        int ord = parseNumber(order).intValue();
        Condition applyCond = parseCondition(n.getFirstChild());
        double val = 0;
        if (n.getAttributes().getNamedItem("val") != null)
            val = parseNumber(n.getAttributes().getNamedItem("val").getNodeValue()).doubleValue();

        template.attachFunc(new FuncTemplate(applyCond, name, stat, ord, val));
    }

    private void attachEffect(Node n, StatTemplate template) {
        NamedNodeMap attrs = n.getAttributes();
        StatsSet set = new StatsSet();

        set.set("name", attrs.getNamedItem("name").getNodeValue());
        set.set("object", template);
        if (attrs.getNamedItem("count") != null)
            set.set("count", parseNumber(attrs.getNamedItem("count").getNodeValue()).intValue());
        if (attrs.getNamedItem("time") != null)
            set.set("time", parseNumber(attrs.getNamedItem("time").getNodeValue()).intValue());

        set.set("value", attrs.getNamedItem("val") != null ? parseNumber(attrs.getNamedItem("val").getNodeValue()).doubleValue() : 0.);

        set.set("abnormal", AbnormalEffect.NULL);
        set.set("abnormal2", AbnormalEffect.NULL);
        set.set("abnormal3", AbnormalEffect.NULL);
        if (attrs.getNamedItem("abnormal") != null) {
            AbnormalEffect ae = AbnormalEffect.getByName(attrs.getNamedItem("abnormal").getNodeValue());
            if (ae.isSpecial())
                set.set("abnormal2", ae);
            if (ae.isEvent())
                set.set("abnormal3", ae);
            else
                set.set("abnormal", ae);
        }

        if (attrs.getNamedItem("stackType") != null)
            set.set("stackType", attrs.getNamedItem("stackType").getNodeValue());
        if (attrs.getNamedItem("stackType2") != null)
            set.set("stackType2", attrs.getNamedItem("stackType2").getNodeValue());
        if (attrs.getNamedItem("stackOrder") != null)
            set.set("stackOrder", parseNumber(attrs.getNamedItem("stackOrder").getNodeValue()).intValue());

        if (attrs.getNamedItem("applyOnCaster") != null &&toBoolean(attrs.getNamedItem("applyOnCaster").getNodeValue()))
            set.set("applyOnCaster");
        if (attrs.getNamedItem("applyOnSummon") != null && toBoolean(attrs.getNamedItem("applyOnSummon").getNodeValue()))
            set.set("applyOnSummon");

        if (attrs.getNamedItem("displayId") != null)
            set.set("displayId", parseNumber(attrs.getNamedItem("displayId").getNodeValue()).intValue());
        if (attrs.getNamedItem("displayLevel") != null)
            set.set("displayLevel", parseNumber(attrs.getNamedItem("displayLevel").getNodeValue()).intValue());
        if (attrs.getNamedItem("chance") != null)
            set.set("chance", parseNumber(attrs.getNamedItem("chance").getNodeValue()).intValue());
        if (attrs.getNamedItem("cancelOnAction") != null && toBoolean(attrs.getNamedItem("cancelOnAction").getNodeValue()))
            set.set("cancelOnAction");
        if (attrs.getNamedItem("isOffensive") != null && toBoolean(attrs.getNamedItem("isOffensive").getNodeValue()) )
            set.set("isOffensive");
        if (attrs.getNamedItem("isReflectable") != null && toBoolean(attrs.getNamedItem("isReflectable").getNodeValue()))
            set.set("isReflectable");

        EffectTemplate lt = new EffectTemplate(set);

        parseTemplate(n, lt);
        for (Node n1 = n.getFirstChild(); n1 != null; n1 = n1.getNextSibling()) {
            if ("triggers".equalsIgnoreCase(n1.getNodeName()))
                parseTrigger(n1, lt);
        }

        if (template instanceof Skill)
            ((Skill) template).attach(lt);
    }

    Condition parseCondition(Node n) {
        while (n != null && n.getNodeType() != Node.ELEMENT_NODE)
            n = n.getNextSibling();
        if (n == null)
            return null;
        String nodeName = n.getNodeName().toLowerCase();
        switch (nodeName) {
            case "and":
                return parseLogicAnd(n);
            case "or":
                return parseLogicOr(n);
            case "not":
                return parseLogicNot(n);
            case "getPlayer":
                return parsePlayerCondition(n);
            case "target":
                return parseTargetCondition(n);
            case "has":
                return parseHasCondition(n);
            case "using":
                return parseUsingCondition(n);
            case "game":
                return parseGameCondition(n);
            case "zone":
                return parseZoneCondition(n);
            default:
                return null;
        }
    }

    private Condition parseLogicAnd(Node n) {
        ConditionLogicAnd cond = new ConditionLogicAnd();
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
            if (n.getNodeType() == Node.ELEMENT_NODE)
                cond.add(parseCondition(n));
        if (cond.conditions == null || cond.conditions.size() == 0)
            LOG.error("Empty <and> condition in " + file);
        return cond;
    }

    private Condition parseLogicOr(Node n) {
        ConditionLogicOr cond = new ConditionLogicOr();
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
            if (n.getNodeType() == Node.ELEMENT_NODE)
                cond.add(parseCondition(n));
        if (cond.conditions == null || cond.conditions.size() == 0)
            LOG.error("Empty <or> condition in " + file);
        return cond;
    }

    private Condition parseLogicNot(Node n) {
        for (n = n.getFirstChild(); n != null; n = n.getNextSibling())
            if (n.getNodeType() == Node.ELEMENT_NODE)
                return new ConditionLogicNot(parseCondition(n));
        LOG.error("Empty <not> condition in " + file);
        return null;
    }

    private Condition parsePlayerCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            String nodeName = a.getNodeName().toLowerCase();
            switch (nodeName) {
                case "race":
                    cond = joinAnd(cond, new ConditionPlayerRace(a.getNodeValue()));
                    break;
                case "minlevel": {
                    int lvl = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionPlayerMinLevel(lvl));
                    break;
                }
                case "summon_siege_golem":
                    cond = joinAnd(cond, new ConditionPlayerSummonSiegeGolem());
                    break;
                case "maxlevel": {
                    int lvl = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionPlayerMaxLevel(lvl));
                    break;
                }
                case "maxpk":
                    int pk = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionPlayerMaxPK(pk));
                    break;
                case "resting": {
                    boolean val = Boolean.valueOf(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RESTING, val));
                    break;
                }
                case "moving": {
                    boolean val = Boolean.valueOf(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.MOVING, val));
                    break;
                }
                case "running": {
                    boolean val = Boolean.valueOf(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.RUNNING, val));
                    break;
                }
                case "standing": {
                    boolean val = Boolean.valueOf(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.STANDING, val));
                    break;
                }
                case "flying": {
                    boolean val = Boolean.valueOf(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING, val));
                    break;
                }
                case "flyingtransform": {
                    boolean val = Boolean.valueOf(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerState(CheckPlayerState.FLYING_TRANSFORM, val));
                    break;
                }
                case "olympiad": {
                    boolean val = Boolean.valueOf(a.getNodeValue());
                    cond = joinAnd(cond, new ConditionPlayerOlympiad(val));
                    break;
                }
                case "active_skill_id":
                    int skill_id = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionTargetActiveSkillId(skill_id));
                    break;
                case "percenthp":
                    int percentHP = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionPlayerPercentHp(percentHP));
                    break;
                case "percentmp":
                    int percentMP = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionPlayerPercentMp(percentMP));
                    break;
                case "percentcp":
                    int percentCP = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionPlayerPercentCp(percentCP));
                    break;
                case "agathion":
                    int agathionId = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionPlayerAgathion(agathionId));
                    break;
                case "cubic":
                    int cubicId = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionPlayerCubic(cubicId));
                    break;
                case "instance_zone": {
                    int id = parseNumber(a.getNodeValue()).intValue();
                    cond = joinAnd(cond, new ConditionPlayerInstanceZone(id));
                    break;
                }
                case "riding":
                    String riding = a.getNodeValue();
                    if ("strider".equals(riding))
                        cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.STRIDER));
                    else if ("wyvern".equals(riding))
                        cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.WYVERN));
                    else if ("none".equals(riding))
                        cond = joinAnd(cond, new ConditionPlayerRiding(CheckPlayerRiding.NONE));
                    break;
                case "classid":
                    cond = joinAnd(cond, new ConditionPlayerClassId(a.getNodeValue()));
                    break;
                case "hasbuffid": {
                    StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
                    int id = Integer.parseInt(st.nextToken().trim());
                    int level = -1;
                    if (st.hasMoreTokens())
                        level = Integer.parseInt(st.nextToken().trim());
                    cond = joinAnd(cond, new ConditionPlayerHasBuffId(id, level));
                    break;
                }
                case "hasbuff": {
                    StringTokenizer st = new StringTokenizer(a.getNodeValue(), ";");
                    EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
                    int level = -1;
                    if (st.hasMoreTokens())
                        level = Integer.parseInt(st.nextToken().trim());
                    cond = joinAnd(cond, new ConditionPlayerHasBuff(et, level));
                    break;
                }
                case "damage": {
                    String[] st = a.getNodeValue().split(";");
                    cond = joinAnd(cond, new ConditionPlayerMinMaxDamage(Double.parseDouble(st[0]), Double.parseDouble(st[1])));
                    break;
                }
            }
        }

        if (cond == null)
            LOG.error("Unrecognized <getPlayer> condition in " + file);
        return cond;
    }

    private Condition parseTargetCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            String nodeName = a.getNodeName().toLowerCase();
            String nodeValue = a.getNodeValue();
            switch (nodeName) {
                case "aggro":
                    cond = joinAnd(cond, new ConditionTargetAggro(Boolean.valueOf(nodeValue)));
                    break;
                case "pvp":
                    cond = joinAnd(cond, new ConditionTargetPlayable(Boolean.valueOf(nodeValue)));
                    break;
                case "getPlayer":
                    cond = joinAnd(cond, new ConditionTargetPlayer(Boolean.valueOf(nodeValue)));
                    break;
                case "summon":
                    cond = joinAnd(cond, new ConditionTargetSummon(Boolean.valueOf(nodeValue)));
                    break;
                case "mob":
                    cond = joinAnd(cond, new ConditionTargetMob(Boolean.valueOf(nodeValue)));
                    break;
                case "mobid":
                    cond = joinAnd(cond, new ConditionTargetMobId(Integer.parseInt(nodeValue)));
                    break;
                case "race":
                    cond = joinAnd(cond, new ConditionTargetRace(nodeValue));
                    break;
                case "npc_class":
                    cond = joinAnd(cond, new ConditionTargetNpcClass(nodeValue));
                    break;
                case "playerrace":
                    cond = joinAnd(cond, new ConditionTargetPlayerRace(nodeValue));
                    break;
                case "forbiddenclassids":
                    cond = joinAnd(cond, new ConditionTargetForbiddenClassId(nodeValue));
                    break;
                case "playerSameClan":
                    cond = joinAnd(cond, new ConditionTargetClan(nodeValue));
                    break;
                case "castledoor":
                    cond = joinAnd(cond, new ConditionTargetCastleDoor(Boolean.valueOf(nodeValue)));
                    break;
                case "direction":
                    cond = joinAnd(cond, new ConditionTargetDirection(PositionUtils.TargetDirection.valueOf(nodeValue.toUpperCase())));
                    break;
                case "percenthp":
                    cond = joinAnd(cond, new ConditionTargetPercentHp(parseNumber(a.getNodeValue()).intValue()));
                    break;
                case "percentmp":
                    cond = joinAnd(cond, new ConditionTargetPercentMp(parseNumber(a.getNodeValue()).intValue()));
                    break;
                case "percentcp":
                    cond = joinAnd(cond, new ConditionTargetPercentCp(parseNumber(a.getNodeValue()).intValue()));
                    break;
                case "hasbuffid": {
                    StringTokenizer st = new StringTokenizer(nodeValue, ";");
                    int id = Integer.parseInt(st.nextToken().trim());
                    int level = -1;
                    if (st.hasMoreTokens())
                        level = Integer.parseInt(st.nextToken().trim());
                    cond = joinAnd(cond, new ConditionTargetHasBuffId(id, level));
                    break;
                }
                case "hasbuff": {
                    StringTokenizer st = new StringTokenizer(nodeValue, ";");
                    EffectType et = Enum.valueOf(EffectType.class, st.nextToken().trim());
                    int level = -1;
                    if (st.hasMoreTokens())
                        level = Integer.parseInt(st.nextToken().trim());
                    cond = joinAnd(cond, new ConditionTargetHasBuff(et, level));
                    break;
                }
                case "hasforbiddenskill":
                    cond = joinAnd(cond, new ConditionTargetHasForbiddenSkill(parseNumber(a.getNodeValue()).intValue()));
                    break;
            }
        }
        if (cond == null)
            LOG.error("Unrecognized <target> condition in " + file);
        return cond;
    }

    private Condition parseUsingCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            String nodeName = a.getNodeName();
            String nodeValue = a.getNodeValue();
            if ("kind".equalsIgnoreCase(nodeName) || "weapon".equalsIgnoreCase(nodeName)) {
                long mask = 0;
                StringTokenizer st = new StringTokenizer(nodeValue, ",");
                tokens:
                while (st.hasMoreTokens()) {
                    String item = st.nextToken().trim();
                    for (WeaponType wt : WeaponType.VALUES)
                        if (wt.toString().equalsIgnoreCase(item)) {
                            mask |= wt.mask();
                            continue tokens;
                        }
                    for (ArmorType at : ArmorType.VALUES)
                        if (at.toString().equalsIgnoreCase(item)) {
                            mask |= at.mask();
                            continue tokens;
                        }
                    LOG.error("Invalid item kind: \"" + item + "\" in " + file);
                }
                if (mask != 0)
                    cond = joinAnd(cond, new ConditionUsingItemType(mask));
            } else if ("armor".equalsIgnoreCase(nodeName)) {
                ArmorType armor = ArmorType.valueOf(nodeValue.toUpperCase());
                cond = joinAnd(cond, new ConditionUsingArmor(armor));
            } else if ("skill".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionUsingSkill(Integer.parseInt(nodeValue)));
            else if ("slotitem".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(nodeValue, ";");
                int id = Integer.parseInt(st.nextToken().trim());
                int slot = Integer.parseInt(st.nextToken().trim());
                int enchant = 0;
                if (st.hasMoreTokens())
                    enchant = Integer.parseInt(st.nextToken().trim());
                cond = joinAnd(cond, new ConditionSlotItemId(slot, id, enchant));
            }
        }
        if (cond == null)
            LOG.error("Unrecognized <using> condition in " + file);
        return cond;
    }

    private Condition parseHasCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            String nodeName = a.getNodeName();
            String nodeValue = a.getNodeValue();
            if ("skill".equalsIgnoreCase(nodeName)) {
                StringTokenizer st = new StringTokenizer(nodeValue, ";");
                Integer id = parseNumber(st.nextToken().trim()).intValue();
                int level = parseNumber(st.nextToken().trim()).shortValue();
                cond = joinAnd(cond, new ConditionHasSkill(id, level));
            } else if ("success".equalsIgnoreCase(nodeName))
                cond = joinAnd(cond, new ConditionFirstEffectSuccess(Boolean.valueOf(nodeValue)));
        }
        if (cond == null)
            LOG.error("Unrecognized <has> condition in " + file);
        return cond;
    }

    private Condition parseGameCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            if ("night".equalsIgnoreCase(a.getNodeName())) {
                boolean val = Boolean.valueOf(a.getNodeValue());
                cond = joinAnd(cond, new ConditionGameTime(CheckGameTime.NIGHT, val));
            }
        }
        if (cond == null)
            LOG.error("Unrecognized <game> condition in " + file);
        return cond;
    }

    private Condition parseZoneCondition(Node n) {
        Condition cond = null;
        NamedNodeMap attrs = n.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Node a = attrs.item(i);
            if ("type".equalsIgnoreCase(a.getNodeName()))
                cond = joinAnd(cond, new ConditionZoneType(a.getNodeValue()));
        }
        if (cond == null)
            LOG.error("Unrecognized <zone> condition in " + file);
        return cond;
    }

    String[] parseTable(Node n) {
        NamedNodeMap attrs = n.getAttributes();
        String name = attrs.getNamedItem("name").getNodeValue();
        if (name.charAt(0) != '#')
            throw new IllegalArgumentException("Table name must start with #");
        StringTokenizer data = new StringTokenizer(n.getFirstChild().getNodeValue());
        List<String> array = new ArrayList<>();
        while (data.hasMoreTokens())
            array.add(data.nextToken());
        String[] res = array.toArray(new String[0]);
        setTable(name, array);
        return res;
    }

    void parseBeanSet(Node n, StatsSet set, int level) {
        try {
            String name = n.getAttributes().getNamedItem("name").getNodeValue().trim();
            String value = n.getAttributes().getNamedItem("val").getNodeValue().trim();
            char ch = value.length() == 0 ? ' ' : value.charAt(0);
            if (value.contains("#") && ch != '#')
                for (String str : value.split("[;: ]+"))
                    if (str.charAt(0) == '#')
                        value = value.replace(str, String.valueOf(getTableValue(str, level)));
            if (ch == '#') {
                String tableVal = getTableValue(value, level);
                Number parsedVal = parseNumber(tableVal);
                set.set(name, parsedVal == null ? tableVal : String.valueOf(parsedVal));
            } else if ((Character.isDigit(ch) || ch == '-') && !value.contains(" ") && !value.contains(";"))
                set.set(name, String.valueOf(parseNumber(value)));
            else
                set.set(name, value);
        } catch (DOMException e) {
            LOG.warn(n.getAttributes().getNamedItem("name") + " " + set.getString("skill_id"), e);
        }
    }

    Number parseNumber(String value) {
        if ("none".equalsIgnoreCase(value)) return null;
        if (value.charAt(0) == '#')
            value = getTableValue(value);
        if (value.equalsIgnoreCase("max"))
            return Double.POSITIVE_INFINITY;
        if (value.equalsIgnoreCase("min"))
            return Double.NEGATIVE_INFINITY;
        try {
            if (value.indexOf('.') == -1) {
                int radix = 10;
                if (value.length() > 2 && value.substring(0, 2).equalsIgnoreCase("0x")) {
                    value = value.substring(2);
                    radix = 16;
                }
                return Integer.valueOf(value, radix);
            }
            return Double.valueOf(value);
        } catch (NumberFormatException e) {
//            LOG.warn("of excetion with value :" + value);
            return null;
        }

    }

    private Condition joinAnd(Condition cond, Condition c) {
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
}