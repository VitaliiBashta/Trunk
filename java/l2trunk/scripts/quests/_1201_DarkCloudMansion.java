package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncMul;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class _1201_DarkCloudMansion extends Quest {
    private static final int INCSTANCED_ZONE_ID = 9;

    // items
    private static final int CC = 9690; // Contaminated Crystal

    // NPC
    private static final int YIYEN = 32282;
    private static final int SOFaith = 32288; // Symbol of Faith
    private static final int SOAdversity = 32289; // Symbol of Adversity
    private static final int SOAdventure = 32290; // Symbol of Anventure
    private static final int SOTruth = 32291; // Symbol of Truth
    private static final int BSM = 32324; // Black Stone Monolith
    private static final int SC = 22402; // Shadow Column

    // Mobs
    private static final int[] CCG = {18369, 18370}; // Chromatic Crystal Golem
    private static final List<Integer> BM = List.of(22272, 22273, 22274); // Beleth's Minions
    private static final int[] HG = {22264, 22265}; // [22318,22319] // Hall Guards
    private static final List<Integer> BS = List.of(
            18371, 18372, 18373, 18374, 18375, 18376, 18377); // Beleth's Samples

    // Doors/Walls
    private static final int D1 = 24230001; // Starting Room
    private static final int D2 = 24230002; // First Room
    private static final int D3 = 24230005; // Second Room
    private static final int D4 = 24230003; // Third Room
    private static final int D5 = 24230004; // Forth Room
    private static final int D6 = 24230006; // Fifth Room
    private static final int W1 = 24230007; // Wall 1
	/*
	private static final int W2 = 24230008; // Wall 2
	private static final int W3 = 24230009; // Wall 3
	private static final int W4 = 24230010; // Wall 4
	private static final int W5 = 24230011; // Wall 5
	private static final int W6 = 24230012; // Wall 6
	private static final int W7 = 24230013; // Wall 7
	 */

    // Second room - random monolith order
    private static final int[][] order = {
            {1, 2, 3, 4, 5, 6},
            {6, 5, 4, 3, 2, 1},
            {4, 5, 6, 3, 2, 1},
            {2, 6, 3, 5, 1, 4},
            {4, 1, 5, 6, 2, 3},
            {3, 5, 1, 6, 2, 4},
            {6, 1, 3, 4, 5, 2},
            {5, 6, 1, 2, 4, 3},
            {5, 2, 6, 3, 4, 1},
            {1, 5, 2, 6, 3, 4},
            {1, 2, 3, 6, 5, 4},
            {6, 4, 3, 1, 5, 2},
            {3, 5, 2, 4, 1, 6},
            {3, 2, 4, 5, 1, 6},
            {5, 4, 3, 1, 6, 2}};

    // Second room - golem spawn locatons - random
    private static final int[][] golems = {
            {CCG[0], 148060, 181389},
            {CCG[1], 147910, 181173},
            {CCG[0], 147810, 181334},
            {CCG[1], 147713, 181179},
            {CCG[0], 147569, 181410},
            {CCG[1], 147810, 181517},
            {CCG[0], 147805, 181281}};

    // forth room - random shadow column
    private static final int[][] rows = {
            {1, 1, 0, 1, 0},
            {0, 1, 1, 0, 1},
            {1, 0, 1, 1, 0},
            {0, 1, 0, 1, 1},
            {1, 0, 1, 0, 1}};

    // Fifth room - beleth order
    private static final int[][] beleths = {
            {1, 0, 1, 0, 1, 0, 0},
            {0, 0, 1, 0, 1, 1, 0},
            {0, 0, 0, 1, 0, 1, 1},
            {1, 0, 1, 1, 0, 0, 0},
            {1, 1, 0, 0, 0, 1, 0},
            {0, 1, 0, 1, 0, 1, 0},
            {0, 0, 0, 1, 1, 1, 0},
            {1, 0, 1, 0, 0, 1, 0},
            {0, 1, 1, 0, 0, 0, 1}};
    private static final Map<Integer, World> worlds = new HashMap<>();

    public _1201_DarkCloudMansion() {
        super(true);

        addStartNpc(YIYEN);
        addTalkId(SOTruth);
        addFirstTalkId(BSM);
        addAttackId(SC);
        addAttackId(BS);
        addKillId(BS);
        addKillId(BM);
        addKillId(CCG);
        addKillId(SC);
        addKillId(HG);

        //addKillId(22318);
        //addKillId(22319);
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        World world = worlds.get(player.getReflection().id);
        if (world.status == 4) {
            for (int[] npcObj : world.SecondRoom.monolith)
                if (npcObj[0] == npc.getStoredId())
                    checkStone(npc, world.SecondRoom.monolithOrder, npcObj, world);
            if (allStonesDone(world)) {
                removeMonoliths(world);
                runHall3(world);
            }
        }
        return null;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        Player player = st.player;
        if (npcId == YIYEN) {
            st.start();
            enterInstance(player);
            return null;
        }
        if (npc.getReflection() == ReflectionManager.DEFAULT)
            return null;
        World world = worlds.get(npc.getReflectionId());
        if (world != null)
            if (npcId == SOTruth) {
                player.sendPacket(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(2));
                player.getReflection().startCollapseTimer(2 * 60 * 1000L);
                player.setReflection(0);
                player.teleToLocation(Location.of(139968, 150367, -3111));
                if (!world.rewarded.contains(player.getStoredId())) {
                    st.giveItems(CC);
                    world.rewarded.add(player.getStoredId());
                }
            }
        return null;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        Player player = st.player;
        World world = worlds.get(npc.getReflectionId());
        if (world == null)
            return;

        switch (world.status) {
            case 0:
                if (checkKillProgress(npc, world.StartRoom))
                    runHall(world);
                break;
            case 1:
                if (checkKillProgress(npc, world.Hall))
                    runFirstRoom(world);
                break;
            case 2:
                if (checkKillProgress(npc, world.FirstRoom))
                    runHall2(world);
                break;
            case 3:
                if (checkKillProgress(npc, world.Hall))
                    runSecondRoom(world);
                break;
            case 5:
                if (checkKillProgress(npc, world.Hall))
                    runThirdRoom(world);
                break;
            case 6:
                if (checkKillProgress(npc, world.ThirdRoom))
                    runForthRoom(world);
                break;
            case 7:
                chkShadowColumn(world, npc);
                break;
            case 8:
                BelethSampleKilled(world, npc, player);
                break;
        }
    }

    @Override
    public void onAttack(NpcInstance npc, QuestState st) {
        Player player = st.player;
        World world = worlds.get(player.getReflectionId());
        if (world != null && world.status == 7)
            for (int[] mob : world.ForthRoom.npclist2)
                if (mob[0] == npc.getStoredId())
                    if (Rnd.chance(12) && npc.isBusy())
                        addSpawnToInstance(Rnd.get(BM), player.getLoc(), world.instanceId);

        if (world != null && world.status == 8)
            BelethSampleAttacked(world, npc, player);
    }

    private void endInstance(World world) {
        world.status = 9;
        addSpawnToInstance(SOTruth, Location.of(148911, 181940, -6117, 16383), world.instanceId);
        world.StartRoom = null;
        world.Hall = null;
        world.SecondRoom = null;
        world.ThirdRoom = null;
        world.ForthRoom = null;
        world.FifthRoom = null;
    }

    private void enterInstance(Player player) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(INCSTANCED_ZONE_ID))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(INCSTANCED_ZONE_ID)) {
            Reflection newInstance = ReflectionUtils.enterReflection(player, INCSTANCED_ZONE_ID);
            World world = new World();
            world.rewarded = new ArrayList<>();
            world.instanceId = newInstance.id;
            worlds.put(newInstance.id, world);
            runStartRoom(world);
            player.getParty().getMembersStream()
                    .filter(member -> member != player)
                    .forEach(member -> newQuestState(member, STARTED));
        }
    }

    private void runStartRoom(World world) {
        world.status = 0;
        world.StartRoom = new Room();
        world.StartRoom.npclist = new HashMap<>();
        NpcInstance newNpc;
        newNpc = addSpawnToInstance(BM.get(0), Location.of(146817, 180335, -6117), world.instanceId);
        world.StartRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(0), Location.of(146741, 180589, -6117), world.instanceId);
        world.StartRoom.npclist.put(newNpc, false);
    }

    private void spawnHall(World world) {
        world.Hall = new Room();
        world.Hall.npclist = new HashMap<>();
        NpcInstance newNpc = addSpawnToInstance(BM.get(1), Location.of(147217, 180112, -6117), world.instanceId);
        world.Hall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(2), Location.of(147217, 180209, -6117), world.instanceId);
        world.Hall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(1), Location.of(148521, 180112, -6117), world.instanceId);
        world.Hall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(0), Location.of(148521, 180209, -6117), world.instanceId);
        world.Hall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(1), Location.of(148525, 180910, -6117), world.instanceId);
        world.Hall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(2), Location.of(148435, 180910, -6117), world.instanceId);
        world.Hall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(1), Location.of(147242, 180910, -6117), world.instanceId);
        world.Hall.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(2), Location.of(147242, 180819, -6117), world.instanceId);
        world.Hall.npclist.put(newNpc, false);
    }

    private void runHall(World world) {
        world.status = 1;
        ReflectionManager.INSTANCE.get(world.instanceId).openDoor(D1);
        spawnHall(world);
    }

    private void runFirstRoom(World world) {
        world.status = 2;
        ReflectionManager.INSTANCE.get(world.instanceId).openDoor(D2);
        world.FirstRoom = new Room();
        world.FirstRoom.npclist = new HashMap<>();
        NpcInstance newNpc = addSpawnToInstance(HG[1], Location.of(147842, 179837, -6117), world.instanceId);
        world.FirstRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(HG[0], Location.of(147711, 179708, -6117), world.instanceId);
        world.FirstRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(HG[1], Location.of(147842, 179552, -6117), world.instanceId);
        world.FirstRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(HG[0], Location.of(147964, 179708, -6117), world.instanceId);
        world.FirstRoom.npclist.put(newNpc, false);
    }

    private void runHall2(World world) {
        world.status = 3;
        spawnHall(world);
    }

    private void runSecondRoom(World world) {
        addSpawnToInstance(SOFaith, Location.of(147818, 179643, -6117), world.instanceId);
        NpcInstance newNpc;
        world.status = 4;
        ReflectionManager.INSTANCE.get(world.instanceId).openDoor(D3);
        world.SecondRoom = new Room();
        world.SecondRoom.monolith = new ArrayList<>();
        int i = Rnd.get(order.length);
        world.SecondRoom.monolithOrder = new int[]{1, 0, 0, 0, 0, 0, 0};
        newNpc = addSpawnToInstance(BSM, Location.of(147800, 181150, -6117), world.instanceId);
        world.SecondRoom.monolith.add(new int[]{newNpc.getStoredId(), order[i][0], 0});
        newNpc = addSpawnToInstance(BSM, Location.of(147900, 181215, -6117), world.instanceId);
        world.SecondRoom.monolith.add(new int[]{newNpc.getStoredId(), order[i][1], 0});
        newNpc = addSpawnToInstance(BSM, Location.of(147900, 181345, -6117), world.instanceId);
        world.SecondRoom.monolith.add(new int[]{newNpc.getStoredId(), order[i][2], 0});
        newNpc = addSpawnToInstance(BSM, Location.of(147800, 181410, -6117), world.instanceId);
        world.SecondRoom.monolith.add(new int[]{newNpc.getStoredId(), order[i][3], 0});
        newNpc = addSpawnToInstance(BSM, Location.of(147700, 181345, -6117), world.instanceId);
        world.SecondRoom.monolith.add(new int[]{newNpc.getStoredId(), order[i][4], 0});
        newNpc = addSpawnToInstance(BSM, Location.of(147700, 181215, -6117), world.instanceId);
        world.SecondRoom.monolith.add(new int[]{newNpc.getStoredId(), order[i][5], 0});
    }

    private void runHall3(World world) {
        addSpawnToInstance(SOAdversity, Location.of(147808, 181281, -6117, 16383), world.instanceId);
        world.status = 5;
        spawnHall(world);
    }

    private void runThirdRoom(World world) {
        world.status = 6;
        ReflectionManager.INSTANCE.get(world.instanceId).openDoor(D4);
        world.ThirdRoom = new Room();
        world.ThirdRoom.npclist = new HashMap<>();
        NpcInstance newNpc = addSpawnToInstance(BM.get(1), Location.of(148765, 180450, -6117), world.instanceId);
        world.ThirdRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(2), Location.of(148865, 180190, -6117), world.instanceId);
        world.ThirdRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(1), Location.of(148995, 180190, -6117), world.instanceId);
        world.ThirdRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(0), Location.of(149090, 180450, -6117), world.instanceId);
        world.ThirdRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(1), Location.of(148995, 180705, -6117), world.instanceId);
        world.ThirdRoom.npclist.put(newNpc, false);
        newNpc = addSpawnToInstance(BM.get(2), Location.of(148865, 180705, -6117), world.instanceId);
        world.ThirdRoom.npclist.put(newNpc, false);
    }

    private void runForthRoom(World world) {
        world.status = 7;
        ReflectionManager.INSTANCE.get(world.instanceId).openDoor(D5);
        world.ForthRoom = new Room();
        world.ForthRoom.npclist2 = new ArrayList<>();
        world.ForthRoom.counter = 0;

        int[] temp = new int[7];
        int[][] templist = new int[7][];

        for (int i = 0; i < temp.length; i++)
            temp[i] = Rnd.get(rows.length);

        for (int i = 0; i < temp.length; i++)
            templist[i] = rows[temp[i]];

        int xx = 0;
        int yy = 0;

        for (int x = 148660; x <= 149160; x += 125) {
            yy = 0;
            for (int y = 179280; y >= 178530; y -= 125) {
                NpcInstance newNpc = addSpawnToInstance(SC, Location.of(x, y, -6115, 16215), world.instanceId);
                newNpc.setAI(new CharacterAI(newNpc));
                if (templist[yy][xx] == 0) {
                    newNpc.setBusy(true); // Используется здесь для определения "ненастощих" статуй.
                    newNpc.addStatFunc(new FuncMul(Stats.MAGIC_DEFENCE, 0x30, this, 1000));
                    newNpc.addStatFunc(new FuncMul(Stats.POWER_DEFENCE, 0x30, this, 1000));
                }

                world.ForthRoom.npclist2.add(new int[]{newNpc.getStoredId(), templist[yy][xx], yy});
                yy += 1;
            }
            xx += 1;
        }
    }

    private void runFifthRoom(World world) {
        world.status = 8;
        ReflectionManager.INSTANCE.get(world.instanceId).openDoor(D6);
        world.FifthRoom = new Room();
        addSpawnToInstance(SOAdventure, Location.of(148910, 178397, -6117, 16383), world.instanceId);
        spawnBelethSample(world);
    }

    private void spawnBelethSample(World world) {
        world.FifthRoom.npclist2 = new ArrayList<>();
        int[] beleth = Rnd.get(beleths);
        world.FifthRoom.belethOrder = new ArrayList<>();
        world.FifthRoom.belethOrder.add(beleth);
        int idx = 0;
        for (int x = 148720; x <= 149110; x += 65) {
            NpcInstance newNpc = addSpawnToInstance(BS.get(idx), Location.of(x, 182145, -6117, 48810), world.instanceId);
            world.FifthRoom.npclist2.add(new int[]{newNpc.getStoredId(), idx, beleth[idx]});
            idx += 1;
        }
    }

    private boolean checkKillProgress(NpcInstance npc, Room room) {
        if (room.npclist.containsKey(npc))
            room.npclist.put(npc, true);
        for (boolean value : room.npclist.values())
            if (!value)
                return false;
        return true;
    }

    private void spawnRndGolem(World world) {
        int i = Rnd.get(golems.length);
        int id = golems[i][0];
        int x = golems[i][1];
        int y = golems[i][2];
        addSpawnToInstance(id, Location.of(x, y, -6117), world.instanceId);
    }

    private void checkStone(NpcInstance npc, int[] order, int[] npcObj, World world) {
        for (int i = 1; i <= 6; i++)
            if (order[i] == 0 && order[i - 1] != 0)
                if (npcObj[1] == i && npcObj[2] == 0) {
                    order[i] = 1;
                    npcObj[2] = 1;
                    npc.broadcastPacket(new MagicSkillUse(npc, 5441));
                    return;
                }
        spawnRndGolem(world);
    }

    private void BelethSampleAttacked(World world, NpcInstance npc, Player player) {
        for (int[] list : world.FifthRoom.npclist2)
            if (list[0] == npc.getStoredId()) {
                if (list[2] == 1) {
                    Functions.npcSay(npc, "You have done well!");
                    npc.decayMe();
                    world.FifthRoom.counter += 1;
                    if (world.FifthRoom.counter >= 3) {
                        unspawnBelethSample(world);
                        endInstance(world);
                        return;
                    }
                } else
                    world.FifthRoom.counter = 0;
                return;
            }
    }

    private void BelethSampleKilled(World world, NpcInstance npc, Player player) {
        for (int[] list : world.FifthRoom.npclist2)
            if (list[0] == npc.getStoredId()) {
                world.FifthRoom.counter = 0;
                unspawnBelethSample(world);
                spawnBelethSample(world);
                return;
            }
    }

    private void unspawnBelethSample(World world) {
        for (int[] list : world.FifthRoom.npclist2) {
            GameObjectsStorage.getAsNpc(list[0]).decayMe();
        }
    }

    private void removeMonoliths(World world) {
        for (int[] list : world.SecondRoom.monolith) {
            GameObjectsStorage.getAsNpc(list[0]).decayMe();

        }
    }

    private boolean allStonesDone(World world) {
        for (int[] list : world.SecondRoom.monolith)
            if (list[2] != 1)
                return false;
        return true;
    }

    private void chkShadowColumn(World world, NpcInstance npc) {
        Reflection ref = ReflectionManager.INSTANCE.get(world.instanceId);
        for (int[] mob : world.ForthRoom.npclist2)
            if (mob[0] == npc.getStoredId())
                for (int i = 0; i <= 7; i++)
                    if (mob[2] == i && world.ForthRoom.counter == i) {
                        ref.openDoor(W1 + i);
                        world.ForthRoom.counter += 1;
                        if (world.ForthRoom.counter == 7)
                            runFifthRoom(world);
                    }
    }

    class World {
        int instanceId;
        int status;
        List<Integer> rewarded;
        Room StartRoom;
        Room Hall;
        Room FirstRoom;
        Room SecondRoom;
        Room ThirdRoom;
        Room ForthRoom;
        Room FifthRoom;
    }

    public class Room {
        Map<NpcInstance, Boolean> npclist;
        List<int[]> npclist2;
        List<int[]> monolith;
        int[] monolithOrder;
        List<int[]> belethOrder;
        int counter;
    }
}