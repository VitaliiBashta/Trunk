package l2trunk.scripts.quests;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;

public final class _625_TheFinestIngredientsPart2 extends Quest {
    // NPCs
    private static final int Jeremy = 31521;
    private static final int Yetis_Table = 31542;
    // Mobs
    private static final int RB_Icicle_Emperor_Bumbalump = 25296;
    // Items
    private static final int Soy_Sauce_Jar = 7205;
    private static final int Food_for_Bumbalump = 7209;
    private static final int Special_Yeti_Meat = 7210;
    private static final int Reward_First = 4589;
    private static final int Reward_Last = 4594;

    public _625_TheFinestIngredientsPart2() {
        super(true);
        addStartNpc(Jeremy);
        addTalkId(Yetis_Table);
        addKillId(RB_Icicle_Emperor_Bumbalump);
        addQuestItem(Food_for_Bumbalump, Special_Yeti_Meat);
    }

    private static boolean BumbalumpSpawned() {
        return GameObjectsStorage.getByNpcId(RB_Icicle_Emperor_Bumbalump) != null;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        int cond = st.getCond();
        if (event.equalsIgnoreCase("jeremy_q0625_0104.htm") && _state == CREATED) {
            if (st.getQuestItemsCount(Soy_Sauce_Jar) == 0) {
                st.exitCurrentQuest(true);
                return "jeremy_q0625_0102.htm";
            }
            st.setState(STARTED);
            st.setCond(1);
            st.takeItems(Soy_Sauce_Jar, 1);
            st.giveItems(Food_for_Bumbalump, 1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("jeremy_q0625_0301.htm") && _state == STARTED && cond == 3) {
            st.exitCurrentQuest(true);
            if (st.getQuestItemsCount(Special_Yeti_Meat) == 0)
                return "jeremy_q0625_0302.htm";
            st.takeItems(Special_Yeti_Meat, 1);
            st.giveItems(Rnd.get(Reward_First, Reward_Last), 5, true);
        } else if (event.equalsIgnoreCase("yetis_table_q0625_0201.htm") && _state == STARTED && cond == 1) {
            if (ServerVariables.getLong(_625_TheFinestIngredientsPart2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
                return "yetis_table_q0625_0204.htm";
            if (st.getQuestItemsCount(Food_for_Bumbalump) == 0)
                return "yetis_table_q0625_0203.htm";
            if (BumbalumpSpawned())
                return "yetis_table_q0625_0202.htm";
            st.takeItems(Food_for_Bumbalump, 1);
            st.setCond(2);
            ThreadPoolManager.INSTANCE.schedule(new BumbalumpSpawner(), 1000);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int _state = st.getState();
        int npcId = npc.getNpcId();
        if (_state == CREATED) {
            if (npcId != Jeremy)
                return "noquest";
            if (st.getPlayer().getLevel() < 73) {
                st.exitCurrentQuest(true);
                return "jeremy_q0625_0103.htm";
            }
            if (st.getQuestItemsCount(Soy_Sauce_Jar) == 0) {
                st.exitCurrentQuest(true);
                return "jeremy_q0625_0102.htm";
            }
            st.setCond(0);
            return "jeremy_q0625_0101.htm";
        }

        if (_state != STARTED)
            return "noquest";
        int cond = st.getCond();

        if (npcId == Jeremy) {
            if (cond == 1)
                return "jeremy_q0625_0105.htm";
            if (cond == 2)
                return "jeremy_q0625_0202.htm";
            if (cond == 3)
                return "jeremy_q0625_0201.htm";
        }

        if (npcId == Yetis_Table) {
            if (ServerVariables.getLong(_625_TheFinestIngredientsPart2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
                return "yetis_table_q0625_0204.htm";
            if (cond == 1)
                return "yetis_table_q0625_0101.htm";
            if (cond == 2) {
                if (BumbalumpSpawned())
                    return "yetis_table_q0625_0202.htm";
                ThreadPoolManager.INSTANCE.schedule(new BumbalumpSpawner(), 1000);
                return "yetis_table_q0625_0201.htm";
            }
            if (cond == 3)
                return "yetis_table_q0625_0204.htm";
        }

        return "noquest";
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {

        if (st.getCond() == 1 || st.getCond() == 2) {
            if (st.getQuestItemsCount(Food_for_Bumbalump) > 0)
                st.takeItems(Food_for_Bumbalump, 1);
            st.giveItems(Special_Yeti_Meat, 1);
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        }

        return null;
    }

    private static class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature actor, Creature killer) {
            ServerVariables.set(_625_TheFinestIngredientsPart2.class.getSimpleName(), String.valueOf(System.currentTimeMillis()));
        }
    }

    public class BumbalumpSpawner extends RunnableImpl {
        private SimpleSpawner spawn = null;
        private int tiks = 0;

        BumbalumpSpawner() {
            if (BumbalumpSpawned())
                return;
            spawn = (SimpleSpawner) new SimpleSpawner(RB_Icicle_Emperor_Bumbalump)
                    .setLoc(new Location(158240, -121536, -2253, Rnd.get(0, 0xFFFF)))
                    .setAmount(1)
                    .stopRespawn();
            spawn.doSpawn(true);
            spawn.getAllSpawned().forEach(npc -> npc.addListener(new DeathListener()));
        }

        void say(String test) {
            spawn.getAllSpawned().forEach(npc -> Functions.npcSay(npc, test));
        }

        @Override
        public void runImpl() {
            if (spawn == null)
                return;
            if (tiks == 0)
                say("I will crush you!");
            if (tiks < 1200 && BumbalumpSpawned()) {
                tiks++;
                if (tiks == 1200)
                    say("May the gods forever condemn you! Your power weakens!");
                ThreadPoolManager.INSTANCE.schedule(this, 1000);
                return;
            }
            spawn.deleteAll();
        }
    }
}