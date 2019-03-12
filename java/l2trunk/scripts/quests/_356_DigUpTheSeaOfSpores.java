package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _356_DigUpTheSeaOfSpores extends Quest {
    //NPC
    private static final int GAUEN = 30717;

    //MOBS
    private static final int SPORE_ZOMBIE = 20562;
    private static final int ROTTING_TREE = 20558;

    //QUEST ITEMS
    private static final int CARNIVORE_SPORE = 5865;
    private static final int HERBIBOROUS_SPORE = 5866;

    public _356_DigUpTheSeaOfSpores() {
        super(false);
        addStartNpc(GAUEN);

        addKillId(SPORE_ZOMBIE,ROTTING_TREE);

        addQuestItem(CARNIVORE_SPORE, HERBIBOROUS_SPORE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long carn = st.getQuestItemsCount(CARNIVORE_SPORE);
        long herb = st.getQuestItemsCount(HERBIBOROUS_SPORE);
        if ("magister_gauen_q0356_06.htm".equalsIgnoreCase(event)) {
            if (st.player.getLevel() >= 43) {
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
            } else {
                htmltext = "magister_gauen_q0356_01.htm";
                st.exitCurrentQuest();
            }
        } else if (("magister_gauen_q0356_20.htm".equalsIgnoreCase(event) || "magister_gauen_q0356_17.htm".equalsIgnoreCase(event)) && carn >= 50 && herb >= 50) {
            st.takeAllItems(CARNIVORE_SPORE,HERBIBOROUS_SPORE);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            if ("magister_gauen_q0356_17.htm".equalsIgnoreCase(event))
                st.giveAdena(44000);
            else
                st.addExpAndSp(36000, 2600);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (cond == 0)
            htmltext = "magister_gauen_q0356_02.htm";
        else if (cond != 3)
            htmltext = "magister_gauen_q0356_07.htm";
        else htmltext = "magister_gauen_q0356_10.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        long carn = st.getQuestItemsCount(CARNIVORE_SPORE);
        long herb = st.getQuestItemsCount(HERBIBOROUS_SPORE);
        if (npcId == SPORE_ZOMBIE) {
            if (carn < 50) {
                st.giveItems(CARNIVORE_SPORE);
                if (carn == 49) {
                    st.playSound(SOUND_MIDDLE);
                    if (herb >= 50) {
                        st.setCond(3);
                        st.start();
                    }
                } else
                    st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == ROTTING_TREE)
            if (herb < 50) {
                st.giveItems(HERBIBOROUS_SPORE);
                if (herb == 49) {
                    st.playSound(SOUND_MIDDLE);
                    if (carn >= 50) {
                        st.setCond(3);
                        st.start();
                    }
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}