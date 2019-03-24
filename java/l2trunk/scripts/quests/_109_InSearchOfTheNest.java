package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _109_InSearchOfTheNest extends Quest {
    //NPC
    private static final int PIERCE = 31553;
    private static final int CORPSE = 32015;
    private static final int KAHMAN = 31554;

    //QUEST ITEMS
    private static final int MEMO = 8083;
    private static final int GOLDEN_BADGE_RECRUIT = 7246;
    private static final int GOLDEN_BADGE_SOLDIER = 7247;

    public _109_InSearchOfTheNest() {
        addStartNpc(PIERCE);
        addTalkId(CORPSE,KAHMAN);

        addQuestItem(MEMO);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if ("Memo".equalsIgnoreCase(event) && cond == 1) {
            st.giveItems(MEMO);
            st.setCond(2);
            st.playSound(SOUND_ITEMGET);
            htmltext = "You've find something...";
        } else if ("merc_cap_peace_q0109_0301.htm".equalsIgnoreCase(event) && cond == 2) {
            st.takeItems(MEMO);
            st.setCond(3);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int id = st.getState();
        if (id == COMPLETED)
            return "completed";
        int cond = st.getCond();
        String htmltext = "noquest";

        if (id == CREATED) {
            if (st.player.getLevel() >= 66 && npcId == PIERCE && st.haveAnyQuestItems(GOLDEN_BADGE_RECRUIT,GOLDEN_BADGE_SOLDIER)) {
                st.start();
                st.playSound(SOUND_ACCEPT);
                st.setCond(1);
                htmltext = "merc_cap_peace_q0109_0105.htm";
            } else {
                htmltext = "merc_cap_peace_q0109_0103.htm";
                st.exitCurrentQuest();
            }
        } else if (id == STARTED)
            if (npcId == CORPSE) {
                if (cond == 1)
                    htmltext = "corpse_of_scout_q0109_0101.htm";
                else if (cond == 2)
                    htmltext = "corpse_of_scout_q0109_0203.htm";
            } else if (npcId == PIERCE) {
                if (cond == 1)
                    htmltext = "merc_cap_peace_q0109_0304.htm";
                else if (cond == 2)
                    htmltext = "merc_cap_peace_q0109_0201.htm";
                else if (cond == 3)
                    htmltext = "merc_cap_peace_q0109_0303.htm";
            } else if (npcId == KAHMAN && cond == 3) {
                htmltext = "merc_kahmun_q0109_0401.htm";
                st.addExpAndSp(701500, 50000);
                st.giveAdena( 161500);
                st.finish();
                st.playSound(SOUND_FINISH);
            }
        return htmltext;
    }

}