package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _163_LegacyOfPoet extends Quest {
    private final int RUMIELS_POEM_1_ID = 1038;
    private final int RUMIELS_POEM_3_ID = 1039;
    private final int RUMIELS_POEM_4_ID = 1040;
    private final int RUMIELS_POEM_5_ID = 1041;

    public _163_LegacyOfPoet() {
        super(false);

        addStartNpc(30220);

        addTalkId(30220);
        addKillId(20372, 20373);

        addQuestItem(RUMIELS_POEM_1_ID,
                RUMIELS_POEM_3_ID,
                RUMIELS_POEM_4_ID,
                RUMIELS_POEM_5_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            st.unset("id");
            htmltext = "30220-07.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            st.start();
            st.setCond(0);
            st.unset("id");
        }
        if (npcId == 30220 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                if (st.player.getRace() == Race.darkelf)
                    htmltext = "30220-00.htm";
                else if (st.player.getLevel() >= 11) {
                    htmltext = "30220-03.htm";
                    return htmltext;
                } else {
                    htmltext = "30220-02.htm";
                    st.exitCurrentQuest();
                }
            } else {
                htmltext = "30220-02.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30220 && st.getCond() == 0)
            htmltext = "completed";
        else if (npcId == 30220 && st.getCond() > 0)
            if (st.haveAllQuestItems(RUMIELS_POEM_1_ID, RUMIELS_POEM_3_ID, RUMIELS_POEM_4_ID, RUMIELS_POEM_5_ID)) {
                if (!st.isSet("id")) {
                    st.set("id");
                    htmltext = "30220-09.htm";
                    st.takeAllItems(RUMIELS_POEM_1_ID, RUMIELS_POEM_3_ID, RUMIELS_POEM_4_ID, RUMIELS_POEM_5_ID);
                    st.giveItems(ADENA_ID, 13890);
                    st.addExpAndSp(21643, 943);
                    st.playSound(SOUND_FINISH);
                    st.finish();
                }
            } else
                htmltext = "30220-08.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == 20372 || npcId == 20373) {
            st.unset("id");
            if (st.getCond() == 1) {
                if (Rnd.chance(10) && st.getQuestItemsCount(RUMIELS_POEM_1_ID) == 0) {
                    st.giveItems(RUMIELS_POEM_1_ID);
                    if (st.getQuestItemsCount(RUMIELS_POEM_1_ID) + st.getQuestItemsCount(RUMIELS_POEM_3_ID) + st.getQuestItemsCount(RUMIELS_POEM_4_ID) + st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 4)
                        st.playSound(SOUND_MIDDLE);
                    else
                        st.playSound(SOUND_ITEMGET);
                }
                if (Rnd.chance(70) && st.getQuestItemsCount(RUMIELS_POEM_3_ID) == 0) {
                    st.giveItems(RUMIELS_POEM_3_ID);
                    if (st.getQuestItemsCount(RUMIELS_POEM_1_ID) + st.getQuestItemsCount(RUMIELS_POEM_3_ID) + st.getQuestItemsCount(RUMIELS_POEM_4_ID) + st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 4)
                        st.playSound(SOUND_MIDDLE);
                    else
                        st.playSound(SOUND_ITEMGET);
                }
                if (Rnd.chance(70) && st.getQuestItemsCount(RUMIELS_POEM_4_ID) == 0) {
                    st.giveItems(RUMIELS_POEM_4_ID);
                    if (st.getQuestItemsCount(RUMIELS_POEM_1_ID) + st.getQuestItemsCount(RUMIELS_POEM_3_ID) + st.getQuestItemsCount(RUMIELS_POEM_4_ID) + st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 4)
                        st.playSound(SOUND_MIDDLE);
                    else
                        st.playSound(SOUND_ITEMGET);
                }
                //if(st.getRandom(10)>5 && st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 0)
                if (Rnd.chance(50) && st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 0) {
                    st.giveItems(RUMIELS_POEM_5_ID);
                    if (st.getQuestItemsCount(RUMIELS_POEM_1_ID) + st.getQuestItemsCount(RUMIELS_POEM_3_ID) + st.getQuestItemsCount(RUMIELS_POEM_4_ID) + st.getQuestItemsCount(RUMIELS_POEM_5_ID) == 4)
                        st.playSound(SOUND_MIDDLE);
                    else
                        st.playSound(SOUND_ITEMGET);
                }
            }
        }
    }
}