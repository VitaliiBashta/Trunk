package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.TeleportLocation;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.residence.*;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.item.support.MerchantGuard;
import l2trunk.gameserver.utils.Location;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum ResidenceParser {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());
    Path xml = Config.DATAPACK_ROOT.resolve("data/residences/");

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + ResidenceHolder.size() + " items ");
    }

    private Residence getResidencebyName(String name, StatsSet set) {
        switch (name) {
            case "Fortress":
                return new Fortress(set);
            case "Castle":
                return new Castle(set);
            case "Dominion":
                return new Dominion(set);
            case "ClanHall":
                return new ClanHall(set);

            default:
                throw new IllegalArgumentException("No residence for name: " + name);
        }
    }

    private void readData(Element rootElement) {
        String impl = rootElement.attributeValue("impl");

        StatsSet set = new StatsSet();
        for (Iterator<Attribute> iterator = rootElement.attributeIterator(); iterator.hasNext(); ) {
            Attribute element = iterator.next();
            set.set(element.getName(), element.getValue());
        }

        Residence residence = getResidencebyName(impl, set);
        ResidenceHolder.addResidence(residence);

        for (Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext(); ) {
            Element element = iterator.next();
            String nodeName = element.getName();
            int level = element.attributeValue("level") == null ? 0 : toInt(element.attributeValue("level"));
            int lease = (int) ((element.attributeValue("lease") == null ? 0 : toInt(element.attributeValue("lease"))) * Config.RESIDENCE_LEASE_FUNC_MULTIPLIER);
            int npcId = element.attributeValue("npcId") == null ? 0 : toInt(element.attributeValue("npcId"));
            int listId = element.attributeValue("listId") == null ? 0 : toInt(element.attributeValue("listId"));

            ResidenceFunction function = null;
            if (nodeName.equalsIgnoreCase("teleport")) {
                function = checkAndGetFunction(residence, ResidenceFunction.TELEPORT);
                List<TeleportLocation> targets = new ArrayList<>();
                for (Iterator<Element> it2 = element.elementIterator(); it2.hasNext(); ) {
                    Element teleportElement = it2.next();
                    if ("target".equalsIgnoreCase(teleportElement.getName())) {
                        int npcStringId = Integer.parseInt(teleportElement.attributeValue("name"));
                        long price = Long.parseLong(teleportElement.attributeValue("price"));
                        int itemId = teleportElement.attributeValue("item") == null ? ItemTemplate.ITEM_ID_ADENA : Integer.parseInt(teleportElement.attributeValue("item"));
                        String nameString = teleportElement.attributeValue("StringName");
                        String nameStringLang = teleportElement.attributeValue("StringNameLang");
                        TeleportLocation loc = new TeleportLocation(itemId, price, npcStringId, nameString, nameStringLang, 0);
                        loc.set(Location.of(teleportElement.attributeValue("loc")));
                        targets.add(loc);
                    }
                }
                function.addTeleports(level, targets);
            } else if (nodeName.equalsIgnoreCase("support")) {
                if (level > 9 && !Config.ALT_CH_ALLOW_1H_BUFFS)
                    continue;
                function = checkAndGetFunction(residence, ResidenceFunction.SUPPORT);
                function.addBuffs(level);
            } else if (nodeName.equalsIgnoreCase("item_create")) {
                function = checkAndGetFunction(residence, ResidenceFunction.ITEM_CREATE);
                function.addBuylist(level, new int[]{npcId, listId});
            } else if (nodeName.equalsIgnoreCase("curtain"))
                function = checkAndGetFunction(residence, ResidenceFunction.CURTAIN);
            else if (nodeName.equalsIgnoreCase("platform"))
                function = checkAndGetFunction(residence, ResidenceFunction.PLATFORM);
            else if (nodeName.equalsIgnoreCase("restore_exp"))
                function = checkAndGetFunction(residence, ResidenceFunction.RESTORE_EXP);
            else if (nodeName.equalsIgnoreCase("restore_hp"))
                function = checkAndGetFunction(residence, ResidenceFunction.RESTORE_HP);
            else if (nodeName.equalsIgnoreCase("restore_mp"))
                function = checkAndGetFunction(residence, ResidenceFunction.RESTORE_MP);
            else if (nodeName.equalsIgnoreCase("skills")) {
                for (Iterator<Element> nextIterator = element.elementIterator(); nextIterator.hasNext(); ) {
                    Element nextElement = nextIterator.next();
                    int id2 = Integer.parseInt(nextElement.attributeValue("id"));
                    int level2 = Integer.parseInt(nextElement.attributeValue("level"));

                    Skill skill = SkillTable.INSTANCE.getInfo(id2, level2);
                    if (skill != null)
                        residence.addSkill(skill);
                }
            } else if ("banish_points".equalsIgnoreCase(nodeName)) {
                for (Iterator<Element> banishPointsIterator = element.elementIterator(); banishPointsIterator.hasNext(); ) {
                    Location loc = Location.of(banishPointsIterator.next());

                    residence.addBanishPoint(loc);
                }
            } else if ("owner_restart_points".equalsIgnoreCase(nodeName)) {
                for (Iterator<Element> ownerRestartPointsIterator = element.elementIterator(); ownerRestartPointsIterator.hasNext(); ) {
                    Location loc = Location.of(ownerRestartPointsIterator.next());

                    residence.addOwnerRestartPoint(loc);
                }
            } else if ("other_restart_points".equalsIgnoreCase(nodeName)) {
                for (Iterator<Element> otherRestartPointsIterator = element.elementIterator(); otherRestartPointsIterator.hasNext(); ) {
                    Location loc = Location.of(otherRestartPointsIterator.next());

                    residence.addOtherRestartPoint(loc);
                }
            } else if ("chaos_restart_points".equalsIgnoreCase(nodeName)) {
                for (Iterator<Element> chaosRestartPointsIterator = element.elementIterator(); chaosRestartPointsIterator.hasNext(); ) {
                    Location loc = Location.of(chaosRestartPointsIterator.next());

                    residence.addChaosRestartPoint(loc);
                }
            } else if ("related_fortresses".equalsIgnoreCase(nodeName)) {
                for (Iterator<Element> subElementIterator = element.elementIterator(); subElementIterator.hasNext(); ) {
                    Element subElement = subElementIterator.next();
                    if (subElement.getName().equalsIgnoreCase("domain"))
                        ((Castle) residence).addRelatedFortress(Fortress.DOMAIN, Integer.parseInt(subElement.attributeValue("fortress")));
                    else if (subElement.getName().equalsIgnoreCase("boundary"))
                        ((Castle) residence).addRelatedFortress(Fortress.BOUNDARY, Integer.parseInt(subElement.attributeValue("fortress")));
                }
            } else if ("merchant_guards".equalsIgnoreCase(nodeName)) {
                for (Iterator<Element> subElementIterator = element.elementIterator(); subElementIterator.hasNext(); ) {
                    Element subElement = subElementIterator.next();

                    int itemId = Integer.parseInt(subElement.attributeValue("item_id"));
                    int npcId2 = Integer.parseInt(subElement.attributeValue("npc_id"));
                    int maxGuard = Integer.parseInt(subElement.attributeValue("max"));
                    Set<Integer> intSet = new HashSet<>(3);
                    String[] ssq = subElement.attributeValue("ssq").split(";");
                    for (String q : ssq) {
                        if (q.equalsIgnoreCase("cabal_null"))
                            intSet.add(SevenSigns.CABAL_NULL);
                        else if (q.equalsIgnoreCase("cabal_dusk"))
                            intSet.add(SevenSigns.CABAL_DUSK);
                        else if (q.equalsIgnoreCase("cabal_dawn"))
                            intSet.add(SevenSigns.CABAL_DAWN);
                        else
                            LOG.error("Unknown ssq type: " + q);
                    }

                    ((Castle) residence).addMerchantGuard(new MerchantGuard(itemId, npcId2, maxGuard, intSet));
                }
            }

            if (function != null)
                function.addLease(level, lease);
        }
        ResidenceHolder.buildFastLook();
    }

    private ResidenceFunction checkAndGetFunction(Residence residence, int type) {
        ResidenceFunction function = residence.getFunction(type);
        if (function == null) {
            function = new ResidenceFunction(residence.getId(), type);
            residence.addFunction(function);
        }
        return function;
    }
}
