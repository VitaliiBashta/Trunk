package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _699_GuardianoftheSkies extends Quest {
    // NPC's
    private static final int engineer_recon = 32557;

    // ITEMS
    private static final int q_gold_feather_of_vulture = 13871;

    // MOB's
    private static final int vulture_rider_1lv = 22614;
    private static final int vulture_rider_2lv = 22615;
    private static final int vulture_rider_3lv = 25633;
    private static final int master_rider = 25623;

    public _699_GuardianoftheSkies() {
        super(PARTY_ALL);
        addStartNpc(engineer_recon);
        addTalkId(engineer_recon);
        addKillId(vulture_rider_1lv, vulture_rider_2lv, vulture_rider_3lv, master_rider);
        addQuestItem(q_gold_feather_of_vulture);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;

        if (event.equals("quest_accept") && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            htmltext = "engineer_recon_q0699_04.htm";
        } else if (event.equals("reply_2") && cond == 1)
            htmltext = "engineer_recon_q0699_08.htm";
        else if (event.equals("reply_3") && cond == 1) {
            st.playSound(SOUND_FINISH);
            htmltext = "engineer_recon_q0699_09.htm";
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == engineer_recon)
            if (cond == 0) {
                if (st.player.getLevel() >= 75 && st.player.isQuestCompleted(_10273_GoodDayToFly.class))
                    htmltext = "engineer_recon_q0699_01.htm";
                else {
                    htmltext = "engineer_recon_q0699_02.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 && st.getQuestItemsCount(q_gold_feather_of_vulture) < 1)
                htmltext = "engineer_recon_q0699_05.htm";
            else if (cond == 1 && st.haveQuestItem(q_gold_feather_of_vulture)  && st.getQuestItemsCount(q_gold_feather_of_vulture) < 10) {
                st.giveItems(ADENA_ID, st.getQuestItemsCount(q_gold_feather_of_vulture) * 1500);
                st.takeItems(q_gold_feather_of_vulture);
                htmltext = "engineer_recon_q0699_06.htm";
            } else if (cond == 1 && st.haveQuestItem(q_gold_feather_of_vulture,10)) {
                st.giveItems(ADENA_ID, st.getQuestItemsCount(q_gold_feather_of_vulture) * 1500 + 8335);
                st.takeItems(q_gold_feather_of_vulture);
                htmltext = "engineer_recon_q0699_06.htm";
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1)
            if (npcId == vulture_rider_1lv) {
                int i3 = Rnd.get(1000);
                if (i3 < 840) {
                    st.giveItems(q_gold_feather_of_vulture);
                    st.playSound(SOUND_ITEMGET);
                }
            } else if (npcId == vulture_rider_2lv) {
                int i3 = Rnd.get(1000);
                if (i3 < 857) {
                    st.giveItems(q_gold_feather_of_vulture);
                    st.playSound(SOUND_ITEMGET);
                }
            } else if (npcId == vulture_rider_3lv) {
                int i3 = Rnd.get(1000);
                if (i3 < 719) {
                    st.giveItems(q_gold_feather_of_vulture);
                    st.playSound(SOUND_ITEMGET);
                }
            } else if (npcId == master_rider) {
                int i0 = Rnd.get(1000);
                if (i0 < 215) {
                    int i1 = Rnd.get(10) + 90;
                    st.giveItems(q_gold_feather_of_vulture, i1);
                    st.playSound(SOUND_ITEMGET);
                } else if (i0 < 446) {
                    int i1 = Rnd.get(10) + 80;
                    st.giveItems(q_gold_feather_of_vulture, i1);
                    st.playSound(SOUND_ITEMGET);
                } else if (i0 < 715) {
                    int i1 = Rnd.get(10) + 70;
                    st.giveItems(q_gold_feather_of_vulture, i1);
                    st.playSound(SOUND_ITEMGET);
                } else if (i0 < 1000) {
                    int i1 = Rnd.get(10) + 60;
                    st.giveItems(q_gold_feather_of_vulture, i1);
                    st.playSound(SOUND_ITEMGET);
                }
            }
    }
}