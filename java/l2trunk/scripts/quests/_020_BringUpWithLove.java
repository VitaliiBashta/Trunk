package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _020_BringUpWithLove extends Quest {
    private static final int TUNATUN = 31537;
    // Item
    private static final int BEAST_WHIP = 15473;
    private static final int CRYSTAL = 9553;
    private static final int JEWEL = 7185;

    public _020_BringUpWithLove() {
        addStartNpc(TUNATUN);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (npc.getNpcId() == TUNATUN) {
            if ("31537-12.htm".equalsIgnoreCase(event)) {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            } else if ("31537-03.htm".equalsIgnoreCase(event)) {
                if (st.haveQuestItem(BEAST_WHIP) )
                    return "31537-03a.htm";
                else
                    st.giveItems(BEAST_WHIP);
            } else if ("31537-15.htm".equalsIgnoreCase(event)) {
                st.unset("cond");
                st.takeItems(JEWEL);
                st.giveItems(CRYSTAL);
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmtext = "noquest";
        if (npc.getNpcId() == TUNATUN) {
            switch (st.getState()) {
                case CREATED:
                    if (st.player.getLevel() >= 82)
                        htmtext = "31537-01.htm";
                    else
                        htmtext = "31537-00.htm";
                    break;
                case STARTED:
                    if (st.getCond() == 1)
                        htmtext = "31537-13.htm";
                    else if (st.getCond() == 2)
                        htmtext = "31537-14.htm";
                    break;
            }
        }
        return htmtext;
    }
}