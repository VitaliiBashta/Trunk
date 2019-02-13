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
            st.set("id", 0);
            st.setCond(1);
            st.setState(STARTED);
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
        if (id == CREATED) {
            st.setState(STARTED);
            st.set("id", 0);
        }
        if (npcId == 30031 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                if (st.player.getLevel() >= 21) {
                    htmltext = "30031-03.htm";
                    return htmltext;
                }
                htmltext = "30031-02.htm";
                st.exitCurrentQuest(true);
            } else {
                htmltext = "30031-02.htm";
                st.exitCurrentQuest(true);
            }
        } else if (npcId == 30031 && st.getCond() == 0)
            htmltext = "completed";
        else if (npcId == 30031 && st.getCond() != 0 && st.getQuestItemsCount(CLAY_TABLET_ID) == 0)
            htmltext = "30031-05.htm";
        else if (npcId == 30031 && st.getCond() != 0 && st.getQuestItemsCount(CLAY_TABLET_ID) != 0) {
            st.takeItems(CLAY_TABLET_ID, st.getQuestItemsCount(CLAY_TABLET_ID));
            st.playSound(SOUND_FINISH);
            st.giveItems(ADENA_ID, 1495);
            st.addExpAndSp(17818, 927);
            st.giveItems(ENCHANT_ARMOR_D);
            htmltext = "30031-06.htm";
            st.exitCurrentQuest(false);
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getQuestItemsCount(CLAY_TABLET_ID) == 0) {
            st.giveItems(CLAY_TABLET_ID);
            st.playSound(SOUND_MIDDLE);
            st.setCond(2);
        }
    }
}