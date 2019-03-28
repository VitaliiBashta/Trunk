package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _329_CuriosityOfDwarf extends Quest {
    private final int GOLEM_HEARTSTONE = 1346;
    private final int BROKEN_HEARTSTONE = 1365;

    public _329_CuriosityOfDwarf() {
        addStartNpc(30437);
        addKillId(20083,20085);

        addQuestItem(BROKEN_HEARTSTONE,GOLEM_HEARTSTONE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("trader_rolento_q0329_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("trader_rolento_q0329_06.htm".equalsIgnoreCase(event)) {
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int id = st.getState();
        long heart;
        long broken;
        if (id == CREATED)
            st.setCond(0);
        if (st.getCond() == 0) {
            if (st.player.getLevel() >= 33)
                htmltext = "trader_rolento_q0329_02.htm";
            else {
                htmltext = "trader_rolento_q0329_01.htm";
                st.exitCurrentQuest();
            }
        } else {
            heart = st.getQuestItemsCount(GOLEM_HEARTSTONE);
            broken = st.getQuestItemsCount(BROKEN_HEARTSTONE);
            if (broken + heart > 0) {
                st.giveAdena(50 * broken + 1000 * heart);
                st.takeAllItems(BROKEN_HEARTSTONE,GOLEM_HEARTSTONE);
                htmltext = "trader_rolento_q0329_05.htm";
            } else
                htmltext = "trader_rolento_q0329_04.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int n = Rnd.get(1, 100);
        if (npcId == 20085) {
            if (n < 5) {
                st.giveItems(GOLEM_HEARTSTONE);
                st.playSound(SOUND_ITEMGET);
            } else if (n < 58) {
                st.giveItems(BROKEN_HEARTSTONE);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20083)
            if (n < 6) {
                st.giveItems(GOLEM_HEARTSTONE);
                st.playSound(SOUND_ITEMGET);
            } else if (n < 56) {
                st.giveItems(BROKEN_HEARTSTONE);
                st.playSound(SOUND_ITEMGET);
            }
    }
}