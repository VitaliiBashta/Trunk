package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _907_DragonTrophyValakas extends Quest {
    private static final int Klein = 31540;
    private static final int Valakas = 29028;
    private static final int MedalofGlory = 21874;

    public _907_DragonTrophyValakas() {
        super(PARTY_ALL);
        addStartNpc(Klein);
        addKillId(Valakas);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("klein_q907_04.htm")) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("klein_q907_07.htm")) {
            st.giveItems(MedalofGlory, 30);
            st.complete();
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == Klein) {
            switch (st.getState()) {
                case CREATED:
                    if (st.player.getLevel() >= 84) {
                        if (st.getQuestItemsCount(7267) > 0)
                            htmltext = "klein_q907_01.htm";
                        else
                            htmltext = "klein_q907_00b.htm";
                    } else {
                        htmltext = "klein_q907_00.htm";
                        st.exitCurrentQuest();
                    }
                    break;
                case STARTED:
                    if (cond == 1)
                        htmltext = "klein_q907_05.htm";
                    else if (cond == 2)
                        htmltext = "klein_q907_06.htm";
                    break;
            }
        }

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            if (npc.getNpcId() == Valakas)
                st.setCond(2);
        }
    }
}