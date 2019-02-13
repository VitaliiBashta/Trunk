package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.utils.Location;

public final class _616_MagicalPowerofFire2 extends Quest {
    // NPC
    private static final int KETRAS_HOLY_ALTAR = 31558;
    private static final int UDAN = 31379;

    // Quest items
    private static final int FIRE_HEART_OF_NASTRON = 7244;
    private static final int RED_TOTEM = 7243;

    private static final int Reward_First = 4589;
    private static final int Reward_Last = 4594;

    private static final int SoulOfFireNastron = 25306;
    private NpcInstance SoulOfFireNastronSpawn = null;

    public _616_MagicalPowerofFire2() {
        super(true);

        addStartNpc(UDAN);
        addTalkId(KETRAS_HOLY_ALTAR);

        addKillId(SoulOfFireNastron);
        addQuestItem(FIRE_HEART_OF_NASTRON);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfFireNastron);
        String htmltext = event;
        if ("quest_accept".equalsIgnoreCase(event)) {
            htmltext = "shaman_udan_q0616_0104.htm";
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("616_1".equalsIgnoreCase(event))
            if (ServerVariables.getLong(_616_MagicalPowerofFire2.class.getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
                htmltext = "totem_of_ketra_q0616_0204.htm";
            else if (st.getQuestItemsCount(RED_TOTEM) >= 1 && isQuest == null) {
                st.takeItems(RED_TOTEM, 1);
                SoulOfFireNastronSpawn = st.addSpawn(SoulOfFireNastron, Location.of(142528, -82528, -6496));
                SoulOfFireNastronSpawn.addListener(new DeathListener());
                st.playSound(SOUND_MIDDLE);
            } else
                htmltext = "totem_of_ketra_q0616_0203.htm";
        else if ("616_3".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(FIRE_HEART_OF_NASTRON) ) {
                st.takeItems(FIRE_HEART_OF_NASTRON);
                st.giveItems(Rnd.get(Reward_First, Reward_Last), 5, true);
                st.playSound(SOUND_FINISH);
                htmltext = "shaman_udan_q0616_0301.htm";
                st.exitCurrentQuest(true);
            } else
                htmltext = "shaman_udan_q0616_0302.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        NpcInstance isQuest = GameObjectsStorage.getByNpcId(SoulOfFireNastron);
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        switch (npcId) {
            case UDAN:
                if (cond == 0)
                    if (st.player.getLevel() >= 75)
                        if (st.getQuestItemsCount(RED_TOTEM) >= 1)
                            htmltext = "shaman_udan_q0616_0101.htm";
                        else {
                            htmltext = "shaman_udan_q0616_0102.htm";
                            st.exitCurrentQuest(true);
                        }
                    else {
                        htmltext = "shaman_udan_q0616_0103.htm";
                        st.exitCurrentQuest(true);
                    }
                else if (cond == 1)
                    htmltext = "shaman_udan_q0616_0105.htm";
                else if (cond == 2)
                    htmltext = "shaman_udan_q0616_0202.htm";
                else if (cond == 3 && st.haveQuestItem(FIRE_HEART_OF_NASTRON))
                    htmltext = "shaman_udan_q0616_0201.htm";
                break;
            case KETRAS_HOLY_ALTAR:
                if (ServerVariables.getLong(getClass().getSimpleName(), 0) + 3 * 60 * 60 * 1000 > System.currentTimeMillis())
                    htmltext = "totem_of_ketra_q0616_0204.htm";
                else if (npc.isBusy())
                    htmltext = "totem_of_ketra_q0616_0202.htm";
                else if (cond == 1)
                    htmltext = "totem_of_ketra_q0616_0101.htm";
                else if (cond == 2)
                    if (isQuest == null) {
                        SoulOfFireNastronSpawn = st.addSpawn(SoulOfFireNastron, Location.of(142528, -82528, -6496));
                        SoulOfFireNastronSpawn.addListener(new DeathListener());
                        htmltext = "totem_of_ketra_q0616_0204.htm";
                    } else
                        htmltext = "<html><body>Already in spawn.</body></html>";
                break;
        }
        return htmltext;
    }

    private static class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature actor, Creature killer) {
            ServerVariables.set(getClass().getSimpleName(), String.valueOf(System.currentTimeMillis()));
        }
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getQuestItemsCount(FIRE_HEART_OF_NASTRON) == 0) {
            st.giveItems(FIRE_HEART_OF_NASTRON);
            st.setCond(3);

            if (SoulOfFireNastronSpawn != null)
                SoulOfFireNastronSpawn.deleteMe();
            SoulOfFireNastronSpawn = null;
        }
    }
}