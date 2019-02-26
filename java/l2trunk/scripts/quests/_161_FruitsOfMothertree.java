package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _161_FruitsOfMothertree extends Quest {
    private static final int ANDELLRIAS_LETTER_ID = 1036;
    private static final int MOTHERTREE_FRUIT_ID = 1037;

    public _161_FruitsOfMothertree() {
        super(false);

        addStartNpc(30362);
        addTalkId(30371);

        addQuestItem(MOTHERTREE_FRUIT_ID,
                ANDELLRIAS_LETTER_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            st.set("id", 0);
            htmltext = "30362-04.htm";
            st.giveItems(ANDELLRIAS_LETTER_ID, 1);
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            st.start();
            st.setCond(0);
            st.set("id", 0);
        }
        if (npcId == 30362 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                if (st.player.getRace() != Race.elf)
                    htmltext = "30362-00.htm";
                else if (st.player.getLevel() >= 3)
                    return "30362-03.htm";
                else {
                    htmltext = "30362-02.htm";
                    st.exitCurrentQuest();
                }
            } else {
                htmltext = "30362-02.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30362 && st.getCond() > 0) {
            if (st.haveQuestItem(ANDELLRIAS_LETTER_ID)  && !st.haveQuestItem(MOTHERTREE_FRUIT_ID) )
                htmltext = "30362-05.htm";
            else if (st.haveQuestItem(MOTHERTREE_FRUIT_ID)) {
                htmltext = "30362-06.htm";
                st.giveItems(ADENA_ID, 1000);
                st.addExpAndSp(1000, 0);
                st.takeItems(MOTHERTREE_FRUIT_ID, 1);
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        } else if (npcId == 30371 && st.getCond() == 1)
            if (st.getQuestItemsCount(ANDELLRIAS_LETTER_ID) == 1) {
                if (st.getInt("id") != 161) {
                    st.set("id", 161);
                    htmltext = "30371-01.htm";
                    st.giveItems(MOTHERTREE_FRUIT_ID);
                    st.takeItems(ANDELLRIAS_LETTER_ID, 1);
                }
            } else if (st.haveQuestItem(MOTHERTREE_FRUIT_ID) )
                htmltext = "30371-02.htm";
        return htmltext;
    }
}