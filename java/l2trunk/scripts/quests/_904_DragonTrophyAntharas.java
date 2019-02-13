package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _904_DragonTrophyAntharas extends Quest {
    private static final int Theodric = 30755;
    private static final int AntharasMax = 29068;
    private static final int MedalofGlory = 21874;

    public _904_DragonTrophyAntharas() {
        super(PARTY_ALL);
        addStartNpc(Theodric);
        addKillId(AntharasMax);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("theodric_q904_04.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("theodric_q904_07.htm".equalsIgnoreCase(event)) {
            st.giveItems(MedalofGlory, 30);
            st.setState(COMPLETED);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == Theodric) {
            switch (st.getState()) {
                case CREATED:
                    if (st.player.getLevel() >= 84) {
                        if (st.getQuestItemsCount(3865) > 0)
                            htmltext = "theodric_q904_01.htm";
                        else
                            htmltext = "theodric_q904_00b.htm";
                    } else {
                        htmltext = "theodric_q904_00.htm";
                        st.exitCurrentQuest(true);
                    }
                    break;
                case STARTED:
                    if (cond == 1)
                        htmltext = "theodric_q904_05.htm";
                    else if (cond == 2)
                        htmltext = "theodric_q904_06.htm";
                    break;
            }
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            if (npc.getNpcId() == AntharasMax)
                st.setCond(2);
        }
    }

}