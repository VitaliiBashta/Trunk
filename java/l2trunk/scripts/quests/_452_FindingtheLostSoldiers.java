package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _452_FindingtheLostSoldiers extends Quest {
    private static final int JAKAN = 32773;
    private static final int TAG_ID = 15513;
    private static final List<Integer> SOLDIER_CORPSES = List.of(32769, 32770, 32771, 32772);

    public _452_FindingtheLostSoldiers() {
        addStartNpc(JAKAN);
        addTalkId(SOLDIER_CORPSES);
        addQuestItem(TAG_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {

        if (npc == null)
            return event;
        int npcId = npc.getNpcId();
        if (npcId == JAKAN) {
            if ("32773-3.htm".equalsIgnoreCase(event)) {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            }
        } else if (SOLDIER_CORPSES.contains(npcId) && st.getCond() == 1) {
            st.giveItems(TAG_ID, 1);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
            npc.deleteMe();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc == null)
            return htmltext;
        int cond = st.getCond();
        if (npc.getNpcId() == JAKAN) {
            switch (st.getState()) {
                case CREATED:
                    if (st.player.getLevel() >= 84) {
                        if (st.isNowAvailable())
                            htmltext = "32773-1.htm";
                        else
                            htmltext = "32773-6.htm";
                    } else
                        htmltext = "32773-0.htm";
                    break;
                case STARTED:
                    if (cond == 1)
                        htmltext = "32773-4.htm";
                    else if (cond == 2) {
                        htmltext = "32773-5.htm";
                        st.unset("cond");
                        st.takeItems(TAG_ID, 1);
                        st.giveAdena( 95200);
                        st.addExpAndSp(435024, 50366);
                        st.playSound(SOUND_FINISH);
                        st.exitCurrentQuest(this);
                    }
                    break;
            }
        } else if (SOLDIER_CORPSES.contains(npc.getNpcId()))
            if (cond == 1)
                htmltext = "corpse-1.htm";

        return htmltext;
    }
}