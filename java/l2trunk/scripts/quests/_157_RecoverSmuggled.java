package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _157_RecoverSmuggled extends Quest {
    private final int ADAMANTITE_ORE_ID = 1024;
    private static final int BUCKLER = 20;

    public _157_RecoverSmuggled() {
        super(false);

        addStartNpc(30005);

        addTalkId(30005);

        addKillId(20121);

        addQuestItem(ADAMANTITE_ORE_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("1".equals(event)) {
            st.unset("id");
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            htmltext = "30005-05.htm";
        } else if ("157_1".equals(event)) {
            htmltext = "30005-04.htm";
            return htmltext;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            st.setCond(0);
            st.unset("id");
        }
        if (npcId == 30005 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                if (st.player.getLevel() >= 5)
                    htmltext = "30005-03.htm";
                else {
                    htmltext = "30005-02.htm";
                    st.exitCurrentQuest();
                }
            } else {
                htmltext = "30005-02.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30005 && st.getCond() != 0 && st.getQuestItemsCount(ADAMANTITE_ORE_ID) < 20)
            htmltext = "30005-06.htm";
        else if (npcId == 30005 && st.getCond() != 0 && st.haveQuestItem(ADAMANTITE_ORE_ID, 20)) {
            st.takeItems(ADAMANTITE_ORE_ID, st.getQuestItemsCount(ADAMANTITE_ORE_ID));
            st.playSound(SOUND_FINISH);
            st.giveItems(BUCKLER, 1);
            htmltext = "30005-07.htm";
            st.finish();
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == 20121) {
            st.unset("id");
            if (st.getCond() != 0 && st.getQuestItemsCount(ADAMANTITE_ORE_ID) < 20 && Rnd.chance(14)) {
                st.giveItems(ADAMANTITE_ORE_ID);
                if (st.haveQuestItem(ADAMANTITE_ORE_ID,20))
                    st.playSound(SOUND_MIDDLE);
                else
                    st.playSound(SOUND_ITEMGET);
            }
        }
    }
}