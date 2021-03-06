package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _278_HomeSecurity extends Quest {
    private static final int Tunatun = 31537;
    private static final List<Integer> FarmMonsters = List.of(18905, 18906);
    private static final int SelMahumMane = 15531;

    public _278_HomeSecurity() {
        addStartNpc(Tunatun);
        addKillId(FarmMonsters);
        addQuestItem(SelMahumMane);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("tunatun_q278_03.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Tunatun) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82)
                    htmltext = "tunatun_q278_01.htm";
                else {
                    htmltext = "tunatun_q278_00.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "tunatun_q278_04.htm";
            else if (cond == 2) {
                if (st.getQuestItemsCount(SelMahumMane) >= 300) {
                    htmltext = "tunatun_q278_05.htm";
                    st.takeItems(SelMahumMane);
                    switch (Rnd.get(1, 13)) {
                        case 1:
                            st.giveItems(960);
                            break;
                        case 2:
                            st.giveItems(960, 2);
                            break;
                        case 3:
                            st.giveItems(960, 3);
                            break;
                        case 4:
                            st.giveItems(960, 4);
                            break;
                        case 5:
                            st.giveItems(960, 5);
                            break;
                        case 6:
                            st.giveItems(960, 6);
                            break;
                        case 7:
                            st.giveItems(960, 7);
                            break;
                        case 8:
                            st.giveItems(960, 8);
                            break;
                        case 9:
                            st.giveItems(960, 9);
                            break;
                        case 10:
                            st.giveItems(960, 10);
                            break;
                        case 11:
                            st.giveItems(9553, 1);
                            break;
                        case 12:
                            st.giveItems(9553, 2);
                            break;
                        case 13:
                            st.giveItems(959);
                            break;
                    }
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest();
                } else
                    htmltext = "tunatun_q278_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1)
            if (FarmMonsters.contains(npcId)) {
                st.giveItems(SelMahumMane, 1, true);
                if (st.getQuestItemsCount(SelMahumMane) >= 300)
                    st.setCond(2);
            }
    }
}