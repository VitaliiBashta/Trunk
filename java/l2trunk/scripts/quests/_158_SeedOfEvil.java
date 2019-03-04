package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _158_SeedOfEvil extends Quest {
    private static final int CLAY_TABLET_ID = 1025;
    private static final int ENCHANT_ARMOR_D = 956;

    public _158_SeedOfEvil() {
        super(false);

        addStartNpc(30031);

        addKillId(27016);

        addQuestItem(CLAY_TABLET_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("1".equals(event)) {
            st.unset("id");
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            htmltext = "30031-04.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        boolean haveQuestItem = st.haveQuestItem(CLAY_TABLET_ID);
        if (id == CREATED) {
            st.start();
            st.unset("id");
        }
        if (npcId == 30031 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                if (st.player.getLevel() >= 21) {
                    htmltext = "30031-03.htm";
                    return htmltext;
                }
                htmltext = "30031-02.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "30031-02.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30031 && st.getCond() == 0)
            htmltext = "completed";
        else if (npcId == 30031 && st.getCond() != 0 && !haveQuestItem)
            htmltext = "30031-05.htm";
        else if (npcId == 30031 && st.getCond() != 0 && haveQuestItem) {
            st.takeItems(CLAY_TABLET_ID);
            st.playSound(SOUND_FINISH);
            st.giveItems(ADENA_ID, 1495);
            st.addExpAndSp(17818, 927);
            st.giveItems(ENCHANT_ARMOR_D);
            htmltext = "30031-06.htm";
            st.finish();
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.giveItemIfNotHave(CLAY_TABLET_ID);
        st.playSound(SOUND_MIDDLE);
        st.setCond(2);
    }
}