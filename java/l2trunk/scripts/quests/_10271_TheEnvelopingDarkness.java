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
        super(false);

        addStartNpc(Orbyu);
        addTalkId(Orbyu);
        addTalkId(El);
        addTalkId(MedibalsCorpse);
        addQuestItem(InspectorMedibalsDocument);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();

        if (event.equalsIgnoreCase("orbyu_q10271_3.htm") && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("el_q10271_2.htm")) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("medibalscorpse_q10271_2.htm")) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
            st.giveItems(InspectorMedibalsDocument, 1);
        } else if (event.equalsIgnoreCase("el_q10271_4.htm")) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
            st.takeItems(InspectorMedibalsDocument, -1);
        } else if (event.equalsIgnoreCase("orbyu_q10271_5.htm")) {
            st.giveItems(ADENA_ID, 62516);
            st.addExpAndSp(377403, 37867);
            st.setState(COMPLETED);
            st.exitCurrentQuest(false);
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
        QuestState ToTheSeedOfDestruction = player.getQuestState(_10269_ToTheSeedOfDestruction.class);

        if (npcId == Orbyu) {
            if (cond == 0) {
                // OTHERS
                int CC_MINIMUM = 36;
                if (player.getLevel() >= 75 && ToTheSeedOfDestruction != null && ToTheSeedOfDestruction.isCompleted() && player.getParty() != null && player.getParty().getCommandChannel() != null && player.getParty().getCommandChannel().size() >= CC_MINIMUM)
                    htmltext = "orbyu_q10271_1.htm";
                else {
                    htmltext = "orbyu_q10271_0.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 4)
                htmltext = "orbyu_q10271_4.htm";
        } else if (npcId == El) {
            if (cond == 1)
                htmltext = "el_q10271_1.htm";
            else if (cond == 3 && st.getQuestItemsCount(InspectorMedibalsDocument) >= 1)
                htmltext = "el_q10271_3.htm";
            else if (cond == 3 && st.getQuestItemsCount(InspectorMedibalsDocument) < 1)
                htmltext = "el_q10271_0.htm";
        } else if (npcId == MedibalsCorpse)
            if (cond == 2)
                htmltext = "medibalscorpse_q10271_1.htm";
        return htmltext;
    }
}
