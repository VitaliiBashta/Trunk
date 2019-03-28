package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10271_TheEnvelopingDarkness extends Quest {
    // NPC's
    private static final int Orbyu = 32560;
    private static final int El = 32556;
    private static final int MedibalsCorpse = 32528;
    // ITEMS
    private static final int InspectorMedibalsDocument = 13852;

    public _10271_TheEnvelopingDarkness() {
        addStartNpc(Orbyu);
        addTalkId(El,MedibalsCorpse);
        addQuestItem(InspectorMedibalsDocument);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();

        if ("orbyu_q10271_3.htm".equalsIgnoreCase(event) && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("el_q10271_2.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("medibalscorpse_q10271_2.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
            st.giveItems(InspectorMedibalsDocument);
        } else if ("el_q10271_4.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
            st.takeItems(InspectorMedibalsDocument);
        } else if ("orbyu_q10271_5.htm".equalsIgnoreCase(event)) {
            st.giveAdena( 62516);
            st.addExpAndSp(377403, 37867);
            st.complete();
            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;

        if (npcId == Orbyu) {
            if (cond == 0) {
                // OTHERS
                int CC_MINIMUM = 36;
                if (player.getLevel() >= 75 && player.isQuestCompleted(_10269_ToTheSeedOfDestruction.class) && player.getParty() != null && player.getParty().getCommandChannel() != null && player.getParty().getCommandChannel().size() >= CC_MINIMUM)
                    htmltext = "orbyu_q10271_1.htm";
                else {
                    htmltext = "orbyu_q10271_0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 4)
                htmltext = "orbyu_q10271_4.htm";
        } else if (npcId == El) {
            if (cond == 1)
                htmltext = "el_q10271_1.htm";
            else if (cond == 3 && st.haveQuestItem(InspectorMedibalsDocument) )
                htmltext = "el_q10271_3.htm";
            else if (cond == 3 && !st.haveQuestItem(InspectorMedibalsDocument))
                htmltext = "el_q10271_0.htm";
        } else if (npcId == MedibalsCorpse)
            if (cond == 2)
                htmltext = "medibalscorpse_q10271_1.htm";
        return htmltext;
    }
}
