package l2trunk.scripts.quests;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.PetDataTable.L2Pet;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class _421_LittleWingAdventures extends Quest {
    // NPCs
    private static final int Cronos = 30610;
    private static final int Mimyu = 30747;
    // Mobs
    private static final int Fairy_Tree_of_Wind = 27185;
    private static final int Fairy_Tree_of_Star = 27186;
    private static final int Fairy_Tree_of_Twilight = 27187;
    private static final int Fairy_Tree_of_Abyss = 27188;
    private static final int Soul_of_Tree_Guardian = 27189;
    // items
    private static final int Dragonflute_of_Wind = L2Pet.HATCHLING_WIND.getControlItemId();
    private static final int Dragonflute_of_Star = L2Pet.HATCHLING_STAR.getControlItemId();
    private static final int Dragonflute_of_Twilight = L2Pet.HATCHLING_TWILIGHT.getControlItemId();
    private static final int Dragon_Bugle_of_Wind = L2Pet.STRIDER_WIND.getControlItemId();
    private static final int Dragon_Bugle_of_Star = L2Pet.STRIDER_STAR.getControlItemId();
    private static final int Dragon_Bugle_of_Twilight = L2Pet.STRIDER_TWILIGHT.getControlItemId();
    // Quest items
    private static final int Fairy_Leaf = 4325;

    private static final int Min_Fairy_Tree_Attaks = 110;

    public _421_LittleWingAdventures() {
        super(false);
        addStartNpc(Cronos);
        addTalkId(Mimyu);
        addKillId(Fairy_Tree_of_Wind, Fairy_Tree_of_Star, Fairy_Tree_of_Twilight, Fairy_Tree_of_Abyss);
        addAttackId(Fairy_Tree_of_Wind, Fairy_Tree_of_Star, Fairy_Tree_of_Twilight, Fairy_Tree_of_Abyss);
        addQuestItem(Fairy_Leaf);
    }

    private static ItemInstance GetDragonflute(QuestState st) {
        List<ItemInstance> Dragonflutes = new ArrayList<>();
        for (ItemInstance item : st.player.getInventory().getItems())
            if (item != null && (item.getItemId() == Dragonflute_of_Wind || item.getItemId() == Dragonflute_of_Star || item.getItemId() == Dragonflute_of_Twilight))
                Dragonflutes.add(item);

        if (Dragonflutes.isEmpty())
            return null;
        if (Dragonflutes.size() == 1)
            return Dragonflutes.get(0);
        if (st.getState() == CREATED)
            return null;

        int dragonflute_id = st.getInt("dragonflute");

        for (ItemInstance item : Dragonflutes)
            if (item.objectId() == dragonflute_id)
                return item;

        return null;
    }

    private static boolean HatchlingSummoned(QuestState st, boolean CheckObjID) {
        Summon _pet = st.player.getPet();
        if (_pet == null)
            return false;
        if (CheckObjID) {
            int dragonflute_id = st.getInt("dragonflute");
            if (dragonflute_id == 0)
                return false;
            if (_pet.getControlItemObjId() != dragonflute_id)
                return false;
        }
        ItemInstance dragonflute = GetDragonflute(st);
        if (dragonflute == null)
            return false;
        return PetDataTable.getControlItemId(_pet.getNpcId()) == dragonflute.getItemId();
    }

    private static boolean CheckTree(QuestState st, int Fairy_Tree_id) {
        return st.getInt(String.valueOf(Fairy_Tree_id)) == 1000000;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        ItemInstance dragonflute = GetDragonflute(st);
        int dragonflute_id = st.getInt("dragonflute");
        int cond = st.getCond();

        if (event.equalsIgnoreCase("30610_05.htm") && _state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (("30747_03.htm".equalsIgnoreCase(event) || "30747_04.htm".equalsIgnoreCase(event)) && _state == STARTED && cond == 1) {
            if (dragonflute == null)
                return "noquest";
            if (dragonflute.objectId() != dragonflute_id) {
                if (Rnd.chance(10)) {
                    st.takeItems(dragonflute.getItemId(), 1);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                }
                return "30747_00.htm";
            }
            if (!HatchlingSummoned(st, false))
                return "30747_04.htm".equalsIgnoreCase(event) ? "30747_04a.htm" : "30747_02.htm";
            if ("30747_04.htm".equalsIgnoreCase(event)) {
                st.setCond(2);
                st.takeItems(Fairy_Leaf, -1);
                st.giveItems(Fairy_Leaf, 4);
                st.playSound(SOUND_MIDDLE);
            }
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int _state = st.getState();
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        ItemInstance dragonflute = GetDragonflute(st);
        int dragonflute_id = st.getInt("dragonflute");

        if (_state == CREATED) {
            if (npcId != Cronos)
                return "noquest";
            if (st.player.getLevel() < 45) {
                st.exitCurrentQuest();
                return "30610_01.htm";
            }
            if (dragonflute == null) {
                st.exitCurrentQuest();
                return "30610_02.htm";
            }
            if (dragonflute.getEnchantLevel() < 55) {
                st.exitCurrentQuest();
                return "30610_03.htm";
            }
            st.setCond(0);
            st.set("dragonflute", dragonflute.objectId());
            return "30610_04.htm";
        }

        if (_state != STARTED)
            return "noquest";

        if (npcId == Cronos) {
            if (dragonflute == null)
                return "30610_02.htm";
            return dragonflute.objectId() == dragonflute_id ? "30610_07.htm" : "30610_06.htm";
        }

        if (npcId == Mimyu) {
            if (st.haveAnyQuestItems(Dragon_Bugle_of_Wind, Dragon_Bugle_of_Star, Dragon_Bugle_of_Twilight))
                return "30747_00b.htm";
            if (dragonflute == null)
                return "noquest";
            if (cond == 1)
                return "30747_01.htm";
            if (cond == 2) {
                if (!HatchlingSummoned(st, false))
                    return "30747_09.htm";
                if (!st.haveQuestItem(Fairy_Leaf)) {
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                    return "30747_11.htm";
                }
                return "30747_10.htm";
            }
            if (cond == 3) {
                if (dragonflute.objectId() != dragonflute_id)
                    return "30747_00a.htm";
                if (st.haveQuestItem(Fairy_Leaf)) {
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                    return "30747_11.htm";
                }
                if (!(CheckTree(st, Fairy_Tree_of_Wind) && CheckTree(st, Fairy_Tree_of_Star) && CheckTree(st, Fairy_Tree_of_Twilight) && CheckTree(st, Fairy_Tree_of_Abyss))) {
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                    return "30747_11.htm";
                }
                if (!st.isSet("welldone")) {
                    if (!HatchlingSummoned(st, false))
                        return "30747_09.htm";
                    st.set("welldone");
                    return "30747_12.htm";
                }
                if (HatchlingSummoned(st, false) || st.player.getPet() != null)
                    return "30747_13a.htm";

                dragonflute.setItemId(Dragon_Bugle_of_Wind + dragonflute.getItemId() - Dragonflute_of_Wind);
                dragonflute.setJdbcState(JdbcEntityState.UPDATED);
                dragonflute.update();
                st.player.sendPacket(new InventoryUpdate().addModifiedItem(dragonflute));

                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
                return "30747_13.htm";
            }
        }

        return "noquest";
    }

    /*
     * благодаря ai.Quest421FairyTree вызовется только при атаке от L2PetInstance
     */
    @Override
    public void onAttack(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED || st.getCond() != 2 || !HatchlingSummoned(st, true) || st.getQuestItemsCount(Fairy_Leaf) == 0)
            return;

        String npcID = String.valueOf(npc.getNpcId());
        int attaked_times = st.getInt(npcID);
        if (CheckTree(st, npc.getNpcId()))
            return;
        if (attaked_times > Min_Fairy_Tree_Attaks) {
            st.set(npcID, 1000000);
            Functions.npcSay(npc, "Give me the leaf!");
            st.takeItems(Fairy_Leaf, 1);
            if (CheckTree(st, Fairy_Tree_of_Wind) && CheckTree(st, Fairy_Tree_of_Star) && CheckTree(st, Fairy_Tree_of_Twilight) && CheckTree(st, Fairy_Tree_of_Abyss)) {
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
            } else
                st.playSound(SOUND_ITEMGET);
        } else
            st.set(npcID, attaked_times + 1);
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        ThreadPoolManager.INSTANCE.schedule(new GuardiansSpawner(npc, st, Rnd.get(15, 20)), 1000);
    }

    public class GuardiansSpawner extends RunnableImpl {
        private SimpleSpawner _spawn;
        private String agressor;
        private String agressors_pet = null;
        private List<String> agressors_party = null;
        private int tiks = 0;

        GuardiansSpawner(NpcInstance npc, QuestState st, int _count) {
            _spawn = new SimpleSpawner(Soul_of_Tree_Guardian);
            for (int i = 0; i < _count; i++) {
                _spawn.setLoc(Location.findPointToStay(npc, 50, 200))
                        .setAmount(1)
                        .doSpawn(true);

                agressor = st.player.getName();
                if (st.player.getPet() != null)
                    agressors_pet = st.player.getPet().getName();
                if (st.player.getParty() != null) {
                    agressors_party = st.player.getParty().getMembersStream()
                            .filter(member -> !member.equals(st.player))
                            .map(Creature::getName)
                            .collect(Collectors.toList());
                }
            }
            _spawn.stopRespawn();
            updateAgression();
        }

        private void AddAgression(Playable player, int aggro) {
            if (player == null)
                return;
            for (NpcInstance mob : _spawn.getAllSpawned()) {
                mob.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, aggro);
            }
        }

        private void updateAgression() {
            Player _player = World.getPlayer(agressor);
            if (_player != null) {
                if (agressors_pet != null && _player.getPet() != null && _player.getPet().getName().equalsIgnoreCase(agressors_pet))
                    AddAgression(_player.getPet(), 10);
                AddAgression(_player, 2);
            }
            if (agressors_party != null)
                for (String _agressor : agressors_party)
                    AddAgression(World.getPlayer(_agressor), 1);
        }

        @Override
        public void runImpl() {
            if (_spawn == null)
                return;
            tiks++;
            if (tiks < 600) {
                updateAgression();
                ThreadPoolManager.INSTANCE.schedule(this, 1000);
                return;
            }
            _spawn.deleteAll();
        }
    }
}