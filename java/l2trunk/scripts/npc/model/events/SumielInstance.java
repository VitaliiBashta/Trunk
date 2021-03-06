package l2trunk.scripts.npc.model.events;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

public final class SumielInstance extends NpcInstance {
    private final int i_quest9 = 0;
    private final int interval_time = 3;
    private final long storage;
    private Player c_ai0 = null;
    private Player c_ai1 = null;
    private int i_ai0 = 0;
    private int i_ai1 = 0;
    private int i_ai2 = 0;
    private int i_ai3 = 0;
    private int i_ai4 = 0;
    private int i_ai5 = 0;
    private int i_ai6 = 0;
    private int i_ai7 = 0;
    private int i_ai8 = 0;
    private int i_ai9 = 0;
    private int i_quest0 = 0;
    private int i_quest1 = 0;
    private int i_quest2 = 0;
    private ScheduledFuture<?> HURRY_UP_1;
    private ScheduledFuture<?> HURRY_UP2_1;
    private ScheduledFuture<?> HURRY_UP_2;
    private ScheduledFuture<?> HURRY_UP2_2;
    private ScheduledFuture<?> GAME_TIME;
    private ScheduledFuture<?> PC_TURN;
    private ScheduledFuture<?> GAME_TIME_EXPIRED;
    private ScheduledFuture<?> TIMER_0;
    private ScheduledFuture<?> TIMER_1;
    private ScheduledFuture<?> TIMER_2;
    private ScheduledFuture<?> TIMER_3;
    private ScheduledFuture<?> TIMER_4;
    private ScheduledFuture<?> TIMER_5;
    private ScheduledFuture<?> TIMER_6;
    private ScheduledFuture<?> TIMER_7;
    private ScheduledFuture<?> TIMER_8;
    private ScheduledFuture<?> TIMER_9;

    public SumielInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        storage = getStoredId();
    }

    @Override
    public void showChatWindow(Player player, int val) {
        if (val == 0) {
            String htmlpath = null;
            if (c_ai0 == null && i_quest2 == 0) {
                htmlpath = "event/monastyre/minigame_instructor001.htm";
                c_ai1 = player;
            } else if (c_ai0 == null && i_quest2 == 1)
                htmlpath = "event/monastyre/minigame_instructor008.htm";
            else if (c_ai0 == player && i_quest0 == 1 && i_quest1 == 0)
                htmlpath = "event/monastyre/minigame_instructor002.htm";
            else if (c_ai0 == player && i_quest0 == 2 && i_quest1 == 0)
                htmlpath = "event/monastyre/minigame_instructor003.htm";
            else if (c_ai0 != player)
                htmlpath = "event/monastyre/minigame_instructor004.htm";
            else if (i_quest1 == 1)
                htmlpath = "event/monastyre/minigame_instructor007.htm";

            player.sendPacket(new NpcHtmlMessage(player, this, htmlpath, val));
        } else
            super.showChatWindow(player, val);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        switch (command) {
            case "teleport":
                showChatWindow(player, "event/monastyre/minigame_instructor006.htm");
                break;
            case "teleport1":
                switch (getAISpawnParam()) {
                    case 1:
                        player.teleToLocation(110705, -81328, -1600);
                        break;
                    case 2:
                        player.teleToLocation(114866, -71627, -560);
                        break;
                }
                break;
            case "teleport2":
                player.teleToLocation(110712, -81352, -2688);
                break;
            case "start":
                if (!player.haveItem(15540))
                    showChatWindow(player, "event/monastyre/minigame_instructor005.htm");
                else if (c_ai1 != player)
                    showChatWindow(player, "event/monastyre/minigame_instructor004.htm");
                else {
                    switch (getAISpawnParam()) {
                        case 1:
                            if (HURRY_UP_1 != null) {
                                HURRY_UP_1.cancel(false);
                                HURRY_UP_1 = null;
                            }
                            if (HURRY_UP2_1 != null) {
                                HURRY_UP2_1.cancel(false);
                                HURRY_UP2_1 = null;
                            }
                            break;
                        case 2:
                            if (HURRY_UP_2 != null) {
                                HURRY_UP_2.cancel(false);
                                HURRY_UP_2 = null;
                            }
                            if (HURRY_UP2_2 != null) {
                                HURRY_UP2_2.cancel(false);
                                HURRY_UP2_2 = null;
                            }
                            break;
                    }

                    player.getInventory().destroyItemByItemId(15540, "SumielInstance");
                    player.getInventory().addItem(15485, 1, "SumielInstance");
                    Functions.npcShout(this, NpcString.FURNFACE1);
                    i_ai1 = Rnd.get(9) + 1;
                    i_ai2 = Rnd.get(9) + 1;
                    i_ai3 = Rnd.get(9) + 1;
                    i_ai4 = Rnd.get(9) + 1;
                    i_ai5 = Rnd.get(9) + 1;
                    i_ai6 = Rnd.get(9) + 1;
                    i_ai7 = Rnd.get(9) + 1;
                    i_ai8 = Rnd.get(9) + 1;
                    i_ai9 = Rnd.get(9) + 1;
                    c_ai0 = player;

                    switch (getAISpawnParam()) {
                        case 1:
                            HURRY_UP_1 = ThreadPoolManager.INSTANCE.schedule(new HURRY_UP(), 2 * 60 * 1000);
                            break;
                        case 2:
                            HURRY_UP_2 = ThreadPoolManager.INSTANCE.schedule(new HURRY_UP(), 2 * 60 * 1000);
                            break;
                    }
                    GAME_TIME = ThreadPoolManager.INSTANCE.schedule(() -> i_quest2 = 0, 3 * 60 * 1000 + 10 * 1000);
                    TIMER_0 = ThreadPoolManager.INSTANCE.schedule(new TIMER_0(), 1000);
                }
                break;
            default:
                super.onBypassFeedback(player, command);
                break;
        }
    }

    public void setSCE_POT_ON(int i) {
        if (i == i_ai1 && i_ai0 == 1) {
            i_ai0 = 2;
        } else if (i == i_ai2 && i_ai0 == 2) {
            i_ai0 = 3;
        } else if (i == i_ai3 && i_ai0 == 3) {
            i_ai0 = 4;
        } else if (i == i_ai4 && i_ai0 == 4) {
            i_ai0 = 5;
        } else if (i == i_ai5 && i_ai0 == 5) {
            i_ai0 = 6;
        } else if (i == i_ai6 && i_ai0 == 6) {
            i_ai0 = 7;
        } else if (i == i_ai7 && i_ai0 == 7) {
            i_ai0 = 8;
        } else if (i == i_ai8 && i_ai0 == 8) {
            i_ai0 = 9;
        } else if (i == i_ai9 && i_ai0 == 9) {
            getAroundFurnface().forEach(FurnfaceInstance::setSCE_GAME_END);

            SimpleSpawner sp = new SimpleSpawner(18934);
            switch (getAISpawnParam()) {
                case 1:
                    sp.setLoc(new Location(110772, -82063, -1584));
                    break;
                case 2:
                    sp.setLoc(new Location(114915, -70998, -544));
                    break;
            }
            sp.doSpawn(true);
            Functions.npcShout(this, NpcString.FURNFACE6);
            switch (getAISpawnParam()) {
                case 1:
                    if (HURRY_UP_1 != null) {
                        HURRY_UP_1.cancel(false);
                        HURRY_UP_1 = null;
                    }
                    if (HURRY_UP2_1 != null) {
                        HURRY_UP2_1.cancel(false);
                        HURRY_UP2_1 = null;
                    }
                    break;
                case 2:
                    if (HURRY_UP_2 != null) {
                        HURRY_UP_2.cancel(false);
                        HURRY_UP_2 = null;
                    }
                    if (HURRY_UP2_2 != null) {
                        HURRY_UP2_2.cancel(false);
                        HURRY_UP2_2 = null;
                    }
                    break;
            }
            c_ai0 = null;
            i_quest0 = 0;
            i_quest1 = 0;
        } else {
            getAroundFurnface().forEach(FurnfaceInstance::setSCE_GAME_FAILURE);
            if (i_quest0 < 2) {
                i_quest0 = i_quest0 + 1;
                Functions.npcShout(this, NpcString.FURNFACE7);
                i_quest1 = 0;
            } else {
                switch (getAISpawnParam()) {
                    case 1:
                        if (HURRY_UP_1 != null) {
                            HURRY_UP_1.cancel(false);
                            HURRY_UP_1 = null;
                        }
                        if (HURRY_UP2_1 != null) {
                            HURRY_UP2_1.cancel(false);
                            HURRY_UP2_1 = null;
                        }
                        break;
                    case 2:
                        if (HURRY_UP_2 != null) {
                            HURRY_UP_2.cancel(false);
                            HURRY_UP_2 = null;
                        }
                        if (HURRY_UP2_2 != null) {
                            HURRY_UP2_2.cancel(false);
                            HURRY_UP2_2 = null;
                        }
                        break;
                }
                Functions.npcShout(this, NpcString.FURNFACE8);
                c_ai0 = null;
                i_quest0 = 0;
                i_quest1 = 0;
            }
        }
    }

    private void setActiveAi(int ai) {
        getAroundFurnface().forEach(npc -> npc.setActive2114001(ai));
    }

    private Stream<FurnfaceInstance> getAroundFurnface() {
        return GameObjectsStorage.getAllNpcs()
                .filter(Objects::nonNull)
                .filter(npc -> npc.getNpcId() == 18913)
                .filter(npc -> getDistance(npc) <= 1200)
                .map(npc -> (FurnfaceInstance) npc);
    }


    private class TIMER_0 extends RunnableImpl {
        @Override
        public void runImpl() {
            getAroundFurnface().forEach(FurnfaceInstance::setActive2114002);
            TIMER_1 = ThreadPoolManager.INSTANCE.schedule(new TIMER_1(), interval_time * 2000);
        }
    }

    private class TIMER_1 extends RunnableImpl {
        @Override
        public void runImpl() {
            setActiveAi(i_ai1);
            TIMER_2 = ThreadPoolManager.INSTANCE.schedule(new TIMER_2(), interval_time * 1000);
        }
    }

    private class TIMER_2 extends RunnableImpl {
        @Override
        public void runImpl() {
            setActiveAi(i_ai2);
            TIMER_3 = ThreadPoolManager.INSTANCE.schedule(new TIMER_3(), interval_time * 1000);
        }
    }

    private class TIMER_3 extends RunnableImpl {
        @Override
        public void runImpl() {
            setActiveAi(i_ai3);
            TIMER_4 = ThreadPoolManager.INSTANCE.schedule(new TIMER_4(), interval_time * 1000);
        }
    }

    private class TIMER_4 extends RunnableImpl {
        @Override
        public void runImpl() {
            setActiveAi(i_ai4);
            TIMER_5 = ThreadPoolManager.INSTANCE.schedule(new TIMER_5(), interval_time * 1000);
        }
    }

    private class TIMER_5 extends RunnableImpl {
        @Override
        public void runImpl() {
            setActiveAi(i_ai5);
            TIMER_6 = ThreadPoolManager.INSTANCE.schedule(new TIMER_6(), interval_time * 1000);
        }
    }

    private class TIMER_6 extends RunnableImpl {
        @Override
        public void runImpl() {
            setActiveAi(i_ai6);
            TIMER_7 = ThreadPoolManager.INSTANCE.schedule(new TIMER_7(), interval_time * 1000);
        }
    }

    private class TIMER_7 extends RunnableImpl {
        @Override
        public void runImpl() {
            setActiveAi(i_ai7);
            TIMER_8 = ThreadPoolManager.INSTANCE.schedule(new TIMER_8(), interval_time * 1000);
        }
    }

    private class TIMER_8 extends RunnableImpl {
        @Override
        public void runImpl() {
            setActiveAi(i_ai8);
            TIMER_9 = ThreadPoolManager.INSTANCE.schedule(new TIMER_9(), interval_time * 1000);
        }
    }

    private class TIMER_9 extends RunnableImpl {
        @Override
        public void runImpl() {
            setActiveAi(i_ai9);
            PC_TURN = ThreadPoolManager.INSTANCE.schedule(new PC_TURN(), interval_time * 1000);
        }
    }

    private class HURRY_UP extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance npc = GameObjectsStorage.getAsNpc(storedId);
            Functions.npcShout(npc, NpcString.FURNFACE2);
            switch (getAISpawnParam()) {
                case 1:
                    HURRY_UP2_1 = ThreadPoolManager.INSTANCE.schedule(new HURRY_UP2(), 60 * 1000);
                    break;
                case 2:
                    HURRY_UP2_2 = ThreadPoolManager.INSTANCE.schedule(new HURRY_UP2(), 60 * 1000);
                    break;
            }
        }
    }

    private class HURRY_UP2 extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance npc = GameObjectsStorage.getAsNpc(storedId);
            Functions.npcShout(npc, NpcString.FURNFACE3);
            GAME_TIME_EXPIRED = ThreadPoolManager.INSTANCE.schedule(new GAME_TIME_EXPIRED(), 10 * 1000);
        }
    }

    private class PC_TURN extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance npc1 = GameObjectsStorage.getAsNpc(storedId);
            Functions.npcShout(npc1, NpcString.FURNFACE4);
            GameObjectsStorage.getAllNpcs()
                    .filter(Objects::nonNull)
                    .filter(npc -> npc.getNpcId() == 18913)
                    .filter(npc -> getDistance(npc) <= 1200)
                    .map(npc -> (FurnfaceInstance) npc)
                    .forEach(FurnfaceInstance::setSCE_GAME_PLAYER_START);
            i_ai0 = 1;
        }
    }

    private class GAME_TIME_EXPIRED extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance npc1 = GameObjectsStorage.getAsNpc(storedId);
            Functions.npcShout(npc1, NpcString.FURNFACE5);
            getAroundFurnface().forEach(FurnfaceInstance::setSCE_GAME_END);
            c_ai0 = null;
            i_quest0 = 0;
            i_quest1 = 0;
        }
    }

}