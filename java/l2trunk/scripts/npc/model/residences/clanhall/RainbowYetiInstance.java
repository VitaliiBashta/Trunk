package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.events.impl.ClanHallMiniGameEvent;
import l2trunk.gameserver.model.entity.events.objects.CMGSiegeClanObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public final class RainbowYetiInstance extends NpcInstance {
    private static final int ItemA = 8035;
    private static final int ItemB = 8036;
    private static final int ItemC = 8037;
    private static final int ItemD = 8038;
    private static final int ItemE = 8039;
    private static final int ItemF = 8040;
    private static final int ItemG = 8041;
    private static final int ItemH = 8042;
    private static final int ItemI = 8043;
    private static final int ItemK = 8045;
    private static final int ItemL = 8046;
    private static final int ItemN = 8047;
    private static final int ItemO = 8048;
    private static final int ItemP = 8049;
    private static final int ItemR = 8050;
    private static final int ItemS = 8051;
    private static final int ItemT = 8052;
    private static final int ItemU = 8053;
    private static final int ItemW = 8054;
    private static final int ItemY = 8055;
    private static final List<Word> WORLD_LIST = List.of(
            new Word("BABYDUCK", Map.of(ItemB, 2, ItemA, 1, ItemY, 1, ItemD, 1, ItemU, 1, ItemC, 1, ItemK, 1)),
            new Word("ALBATROS", Map.of(ItemA, 2, ItemL, 1, ItemB, 1, ItemT, 1, ItemR, 1, ItemO, 1, ItemS, 1)),
            new Word("PELICAN", Map.of(ItemP, 1, ItemE, 1, ItemL, 1, ItemI, 1, ItemC, 1, ItemA, 1, ItemN, 1)),
            new Word("KINGFISHER", Map.of(ItemK, 1, ItemN, 1, ItemG, 1, ItemF, 1, ItemI, 1, ItemS, 1, ItemH, 1, ItemE, 1, ItemR, 1)),
            new Word("CYGNUS", Map.of(ItemC, 1, ItemY, 1, ItemG, 1, ItemN, 1, ItemU, 1, ItemS, 1)),
            new Word("TRITON", Map.of(ItemT, 2, ItemR, 1, ItemI, 1, ItemN, 1)),
            new Word("RAINBOW", Map.of(ItemR, 1, ItemA, 1, ItemI, 1, ItemN, 1, ItemB, 1, ItemO, 1, ItemW, 1)),
            new Word("SPRING", Map.of(ItemS, 1, ItemP, 1, ItemR, 1, ItemI, 1, ItemN, 1, ItemG, 1)));

    private final List<GameObject> _mobs = new ArrayList<>();
    private Future<?> task = null;

    public RainbowYetiInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        _hasRandomWalk = false;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        ClanHallMiniGameEvent event = getEvent(ClanHallMiniGameEvent.class);
        if (event == null)
            return;

        World.getAroundPlayers(this, 750, 100)
                .filter(player -> {
                    CMGSiegeClanObject siegeClanObject = event.getSiegeClan(ClanHallMiniGameEvent.ATTACKERS, player.getClan());
                    return (siegeClanObject == null || !siegeClanObject.getPlayers().contains(player.getObjectId()));
                })
                .forEach(player ->
                        player.teleToLocation(event.getResidence().getOtherRestartPoint()));
        task = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new GenerateTask(), 10000L, 300000L);
    }

    @Override
    public void onDelete() {
        super.onDelete();
        if (task != null) {
            task.cancel(false);
            task = null;
        }

        for (GameObject object : _mobs)
            object.deleteMe();

        _mobs.clear();
    }

    void teleportFromArena() {
        ClanHallMiniGameEvent event = getEvent(ClanHallMiniGameEvent.class);
        if (event == null)
            return;
        World.getAroundPlayers(this, 750, 100)
                .forEach(p -> p.teleToLocation(event.getResidence().getOtherRestartPoint()));
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        int generated = -1;
        if ("get".equalsIgnoreCase(command)) {
            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            boolean has;
            has = false;
            if (!has)
                msg.setFile("residence2/clanhall/watering_manager002.htm");
            else
                msg.setFile("residence2/clanhall/watering_manager004.htm");

            player.sendPacket(msg);
        } else if (command.equalsIgnoreCase("see")) {
            NpcHtmlMessage msg = new NpcHtmlMessage(player, this);
            msg.setFile("residence2/clanhall/watering_manager005.htm");
            msg.replaceNpcString("%word%", NpcString.UNDECIDED);
            player.sendPacket(msg);
        } else
            super.onBypassFeedback(player, command);
    }

    private void addItem(Player player, int itemId) {
        ClanHallMiniGameEvent event = getEvent(ClanHallMiniGameEvent.class);
        if (event == null)
            return;

        ItemInstance item = ItemFunctions.createItem(itemId);
        item.addEvent(event);

        player.getInventory().addItem(item, "RainbowYeti");
        player.sendPacket(SystemMessage2.obtainItems(item));
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        showChatWindow(player, "residence2/clanhall/watering_manager001.htm");
    }

    public void addMob(GameObject object) {
        _mobs.add(object);
    }

    private static class Word {
        private final String name;
        private final Map<Integer, Integer> items;

        Word(String name, Map<Integer, Integer> items) {
            this.name = name;
            this.items = items;
        }

        String getName() {
            return name;
        }

        Map<Integer, Integer> getItems() {
            return items;
        }
    }

    private class GenerateTask extends RunnableImpl {
        @Override
        public void runImpl() {
            Word word = Rnd.get(WORLD_LIST);

            ExShowScreenMessage msg = new ExShowScreenMessage(NpcString.NONE, word.getName());
            World.getAroundPlayers(RainbowYetiInstance.this, 750, 100).forEach(player -> player.sendPacket(msg));
        }
    }
}
