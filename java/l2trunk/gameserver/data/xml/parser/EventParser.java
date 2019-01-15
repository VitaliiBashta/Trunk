package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.actions.*;
import l2trunk.gameserver.model.entity.events.impl.*;
import l2trunk.gameserver.model.entity.events.objects.*;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SysString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum EventParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/events/");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + EventHolder.size() + " items");
    }

    private GlobalEvent getEventByName(String name, StatsSet set) {
        switch (name) {
            case "UndergroundColiseumEvent":
                return new UndergroundColiseumEvent(set);
            case "PlayerVsPlayerDuelEvent":
                return new PlayerVsPlayerDuelEvent(set);
            case "PartyVsPartyDuelEvent":
                return new PartyVsPartyDuelEvent(set);
            case "MonasteryFurnaceEvent":
                return new MonasteryFurnaceEvent(set);
            case "March8Event":
                return new March8Event(set);
            case "KrateisCubeRunnerEvent":
                return new KrateisCubeRunnerEvent(set);
            case "KrateisCubeEvent":
                return new KrateisCubeEvent(set);
            case "FortressSiegeEvent":
                return new FortressSiegeEvent(set);
            case "FantasiIsleParadEvent":
                return new FantasiIsleParadEvent(set);
            case "DominionSiegeRunnerEvent":
                return new DominionSiegeRunnerEvent(set);
            case "DominionSiegeEvent":
                return new DominionSiegeEvent(set);
            case "ClanHallTeamBattleEvent":
                return new ClanHallTeamBattleEvent(set);
            case "ClanHallSiegeEvent":
                return new ClanHallSiegeEvent(set);
            case "ClanHallNpcSiegeEvent":
                return new ClanHallNpcSiegeEvent(set);
            case "ClanHallMiniGameEvent":
                return new ClanHallMiniGameEvent(set);
            case "ClanHallAuctionEvent":
                return new ClanHallAuctionEvent(set);
            case "CastleSiegeEvent":
                return new CastleSiegeEvent(set);
            case "BoatWayEvent":
                return new BoatWayEvent(set);
            default:
                throw new IllegalArgumentException("no event with name:" + name);
        }
    }

    protected void readData(Element rootElement) {
        for (Iterator<Element> iterator = rootElement.elementIterator("event"); iterator.hasNext(); ) {
            Element eventElement = iterator.next();
            int id = toInt(eventElement.attributeValue("id"));
            String name = eventElement.attributeValue("name");
            String impl = eventElement.attributeValue("impl");
            EventType type = EventType.valueOf(eventElement.attributeValue("type"));

            StatsSet set = new StatsSet()
                    .set("id", id)
                    .set("name", name)
                    .set("eventClass", impl + "Event");

            for (Iterator<Element> parameterIterator = eventElement.elementIterator("parameter"); parameterIterator.hasNext(); ) {
                Element parameterElement = parameterIterator.next();
                set.set(parameterElement.attributeValue("name"), parameterElement.attributeValue("value"));
            }

            GlobalEvent event = getEventByName(impl + "Event", set);

            event.addOnStartActions(parseActions(eventElement.element("on_start"), Integer.MAX_VALUE));
            event.addOnStopActions(parseActions(eventElement.element("on_stop"), Integer.MAX_VALUE));
            event.addOnInitActions(parseActions(eventElement.element("on_init"), Integer.MAX_VALUE));

            Element onTime = eventElement.element("on_time");
            if (onTime != null)
                for (Iterator<Element> onTimeIterator = onTime.elementIterator("on"); onTimeIterator.hasNext(); ) {
                    Element on = onTimeIterator.next();
                    int time = toInt(on.attributeValue("time"));

                    List<EventAction> actions = parseActions(on, time);

                    event.addOnTimeActions(time, actions);
                }

            for (Iterator<Element> objectIterator = eventElement.elementIterator("objects"); objectIterator.hasNext(); ) {
                Element objectElement = objectIterator.next();
                String objectsName = objectElement.attributeValue("name");
                List<Object> objects = parseObjects(objectElement);

                event.addObjects(objectsName, objects);
            }
            EventHolder.addEvent(type, event);

//            } catch (NoSuchMethodException e) {
//                e.printStackTrace();
//            } catch (InstantiationException e) {
//                e.printStackTrace();
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            } catch (InvocationTargetException e) {
//                e.printStackTrace();


        }

    }

    private List<Object> parseObjects(Element element) {
        if (element == null)
            return Collections.emptyList();

        List<Object> objects = new ArrayList<>(2);
        for (Iterator<Element> objectsIterator = element.elementIterator(); objectsIterator.hasNext(); ) {
            Element objectsElement = objectsIterator.next();
            final String nodeName = objectsElement.getName();
            if (nodeName.equalsIgnoreCase("boat_point"))
                objects.add(BoatPoint.parse(objectsElement));
            else if (nodeName.equalsIgnoreCase("point"))
                objects.add(Location.parse(objectsElement));
            else if (nodeName.equalsIgnoreCase("spawn_ex"))
                objects.add(new SpawnExObject(objectsElement.attributeValue("name")));
            else if (nodeName.equalsIgnoreCase("door"))
                objects.add(new DoorObject(toInt(objectsElement.attributeValue("id"))));
            else if (nodeName.equalsIgnoreCase("static_object"))
                objects.add(new StaticObjectObject(toInt(objectsElement.attributeValue("id"))));
            else if (nodeName.equalsIgnoreCase("combat_flag")) {
                int x = toInt(objectsElement.attributeValue("x"));
                int y = toInt(objectsElement.attributeValue("y"));
                int z = toInt(objectsElement.attributeValue("z"));
                objects.add(new FortressCombatFlagObject(new Location(x, y, z)));
            } else if (nodeName.equalsIgnoreCase("territory_ward")) {
                int x = toInt(objectsElement.attributeValue("x"));
                int y = toInt(objectsElement.attributeValue("y"));
                int z = toInt(objectsElement.attributeValue("z"));
                int itemId = toInt(objectsElement.attributeValue("item_id"));
                int npcId = toInt(objectsElement.attributeValue("npc_id"));
                objects.add(new TerritoryWardObject(itemId, npcId, new Location(x, y, z)));
            } else if (nodeName.equalsIgnoreCase("siege_toggle_npc")) {
                int id = toInt(objectsElement.attributeValue("id"));
                int fakeId = toInt(objectsElement.attributeValue("fake_id"));
                int x = toInt(objectsElement.attributeValue("x"));
                int y = toInt(objectsElement.attributeValue("y"));
                int z = toInt(objectsElement.attributeValue("z"));
                int hp = toInt(objectsElement.attributeValue("hp"));
                Set<String> set = Collections.emptySet();
                for (Iterator<Element> oIterator = objectsElement.elementIterator(); oIterator.hasNext(); ) {
                    Element sub = oIterator.next();
                    if (set.isEmpty())
                        set = new HashSet<>();
                    set.add(sub.attributeValue("name"));
                }
                objects.add(new SiegeToggleNpcObject(id, fakeId, new Location(x, y, z), hp, set));
            } else if (nodeName.equalsIgnoreCase("castle_zone")) {
                long price = Long.parseLong(objectsElement.attributeValue("price"));
                objects.add(new CastleDamageZoneObject(objectsElement.attributeValue("name"), price));
            } else if (nodeName.equalsIgnoreCase("zone")) {
                objects.add(new ZoneObject(objectsElement.attributeValue("name")));
            } else if (nodeName.equalsIgnoreCase("ctb_team")) {
                int mobId = toInt(objectsElement.attributeValue("mob_id"));
                int flagId = toInt(objectsElement.attributeValue("id"));
                Location loc = Location.parse(objectsElement);

                objects.add(new CTBTeamObject(mobId, flagId, loc));
            }
        }

        return objects;
    }

    private List<EventAction> parseActions(Element element, int time) {
        if (element == null)
            return Collections.emptyList();

        IfElseAction lastIf = null;
        List<EventAction> actions = new ArrayList<>(0);
        for (Iterator<Element> iterator = element.elementIterator(); iterator.hasNext(); ) {
            Element actionElement = iterator.next();
            if (actionElement.getName().equalsIgnoreCase("start")) {
                String name = actionElement.attributeValue("name");
                StartStopAction startStopAction = new StartStopAction(name, true);
                actions.add(startStopAction);
            } else if ("stop".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                StartStopAction startStopAction = new StartStopAction(name, false);
                actions.add(startStopAction);
            } else if ("spawn".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                SpawnDespawnAction spawnDespawnAction = new SpawnDespawnAction(name, true);
                actions.add(spawnDespawnAction);
            } else if ("despawn".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                SpawnDespawnAction spawnDespawnAction = new SpawnDespawnAction(name, false);
                actions.add(spawnDespawnAction);
            } else if ("open".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                OpenCloseAction a = new OpenCloseAction(true, name);
                actions.add(a);
            } else if ("close".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                OpenCloseAction a = new OpenCloseAction(false, name);
                actions.add(a);
            } else if ("active".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                ActiveDeactiveAction a = new ActiveDeactiveAction(true, name);
                actions.add(a);
            } else if ("deactive".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                ActiveDeactiveAction a = new ActiveDeactiveAction(false, name);
                actions.add(a);
            } else if ("refresh".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                RefreshAction a = new RefreshAction(name);
                actions.add(a);
            } else if ("init".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                InitAction a = new InitAction(name);
                actions.add(a);
            } else if ("npc_say".equalsIgnoreCase(actionElement.getName())) {
                int npc = toInt(actionElement.attributeValue("npc"));
                ChatType chat = ChatType.valueOf(actionElement.attributeValue("chat"));
                int range = toInt(actionElement.attributeValue("range"));
                NpcString string = NpcString.valueOf(actionElement.attributeValue("text"));
                NpcSayAction action = new NpcSayAction(npc, range, chat, string);
                actions.add(action);
            } else if ("play_sound".equalsIgnoreCase(actionElement.getName())) {
                int range = toInt(actionElement.attributeValue("range"));
                String sound = actionElement.attributeValue("sound");
                PlaySound.Type type = PlaySound.Type.valueOf(actionElement.attributeValue("type"));

                PlaySoundAction action = new PlaySoundAction(range, sound, type);
                actions.add(action);
            } else if ("give_item".equalsIgnoreCase(actionElement.getName())) {
                int itemId = toInt(actionElement.attributeValue("id"));
                long count = toInt(actionElement.attributeValue("count"));

                GiveItemAction action = new GiveItemAction(itemId, count);
                actions.add(action);
            } else if ("announce".equalsIgnoreCase(actionElement.getName())) {
                String val = actionElement.attributeValue("val");
                if (val == null && time == Integer.MAX_VALUE) {
                    LOG.info("Can't get announce time." + element);
                    continue;
                }

                int val2 = val == null ? time : toInt(val);
                EventAction action = new AnnounceAction(val2);
                actions.add(action);
            } else if ("if".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                IfElseAction action = new IfElseAction(name, false);

                action.setIfList(parseActions(actionElement, time));
                actions.add(action);

                lastIf = action;
            } else if ("ifnot".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("name");
                IfElseAction action = new IfElseAction(name, true);

                action.setIfList(parseActions(actionElement, time));
                actions.add(action);

                lastIf = action;
            } else if ("else".equalsIgnoreCase(actionElement.getName())) {
                if (lastIf == null)
                    LOG.info("Not find <if> for <else> tag");
                else
                    lastIf.setElseList(parseActions(actionElement, time));
            } else if ("say".equalsIgnoreCase(actionElement.getName())) {
                ChatType chat = ChatType.valueOf(actionElement.attributeValue("chat"));
                int range = toInt(actionElement.attributeValue("range"));

                String how = actionElement.attributeValue("how");
                String text = actionElement.attributeValue("text");

                SysString sysString = SysString.valueOf2(how);

                SayAction sayAction;
                if (sysString != null)
                    sayAction = new SayAction(range, chat, sysString, SystemMsg.valueOf(text));
                else
                    sayAction = new SayAction(range, chat, how, NpcString.valueOf(text));

                actions.add(sayAction);
            } else if ("teleport_players".equalsIgnoreCase(actionElement.getName())) {
                String name = actionElement.attributeValue("id");
                TeleportPlayersAction a = new TeleportPlayersAction(name);
                actions.add(a);
            }
        }

        return actions.isEmpty() ? Collections.emptyList() : actions;
    }
}
