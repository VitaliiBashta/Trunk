package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _613_ProveYourCourage extends Quest {
    private final static int DURAI = 31377;
    private final static int KETRAS_HERO_HEKATON = 25299;

    // Quest items
    private final static int HEAD_OF_HEKATON = 7240;
    private final static int FEATHER_OF_VALOR = 7229;

    public _613_ProveYourCourage() {
        super(true);

        addStartNpc(DURAI);
        addKillId(KETRAS_HERO_HEKATON);

        addQuestItem(HEAD_OF_HEKATON);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("quest_accept".equals(event)) {
            htmltext = "elder_ashas_barka_durai_q0613_0104.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("613_3".equals(event))
            if (st.haveQuestItem(HEAD_OF_HEKATON)) {
                htmltext = "elder_ashas_barka_durai_q0613_0201.htm";
                st.takeItems(HEAD_OF_HEKATON);
                st.giveItems(FEATHER_OF_VALOR);
                st.addExpAndSp(0, 10000);
                st.unset("cond");
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else
                htmltext = "elder_ashas_barka_durai_q0613_0106.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 75) {
                if (st.player.getVarka() >2 )
                    htmltext = "elder_ashas_barka_durai_q0613_0101.htm";
                else {
                    htmltext = "elder_ashas_barka_durai_q0613_0102.htm";
                    st.exitCurrentQuest();
                }
            } else {
                htmltext = "elder_ashas_barka_durai_q0613_0103.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1 && !st.haveQuestItem(HEAD_OF_HEKATON))
            htmltext = "elder_ashas_barka_durai_q0613_0106.htm";
        else if (cond == 2 && st.haveQuestItem(HEAD_OF_HEKATON))
            htmltext = "elder_ashas_barka_durai_q0613_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() == KETRAS_HERO_HEKATON && st.getCond() == 1) {
            st.giveItems(HEAD_OF_HEKATON);
            st.setCond(2);
            st.playSound(SOUND_ITEMGET);
        }
    }
}