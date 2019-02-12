package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _640_TheZeroHour extends Quest  {
    // NPC's
    private static final int KAHMAN = 31554;
    // ITEMS
    private static final int STAKATO_QUEENS_FANG = 14859;
    private static final int KAHMANS_SUPPLY_BOX = 14849;
    // MOB's
    private static final int QUEEN_SHYEED_ID = 25671;

    public _640_TheZeroHour() {
        super(true);

        addStartNpc(KAHMAN);
        addTalkId(KAHMAN);
        addKillId(QUEEN_SHYEED_ID);
        addQuestItem(STAKATO_QUEENS_FANG);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;

        if ("merc_kahmun_q0640_0103.htm".equals(event) && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }

        if ("reward".equals(event) && cond == 2) {
            htmltext = "merc_kahmun_q0640_0107.htm";
            st.takeItems(STAKATO_QUEENS_FANG);
            st.giveItems(KAHMANS_SUPPLY_BOX);
            st.exitCurrentQuest(false);
        }

        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == KAHMAN)
            if (cond == 0)
                if (st.player.getLevel() >= 81) {
                    if (st.player.isQuestCompleted(_109_InSearchOfTheNest.class))
                        htmltext = "merc_kahmun_q0640_0101.htm";
                    else
                        htmltext = "merc_kahmun_q0640_0104.htm";
                } else
                    htmltext = "merc_kahmun_q0640_0102.htm";

            else if (cond == 1 && st.getQuestItemsCount(STAKATO_QUEENS_FANG) < 1)
                htmltext = "merc_kahmun_q0640_0105.htm";
            else if (cond == 2)
                htmltext = "merc_kahmun_q0640_0106.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() == STARTED) {
            st.setCond(2);
            st.giveItems(STAKATO_QUEENS_FANG);
        }
    }
}