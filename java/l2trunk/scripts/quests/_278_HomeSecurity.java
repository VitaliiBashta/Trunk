package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.Arrays;
import java.util.List;

public final class _278_HomeSecurity extends Quest {
    private static final int Tunatun = 31537;
    private static final List<Integer> FarmMonsters = List.of(18905, 18906);
    private static final int SelMahumMane = 15531;

    public _278_HomeSecurity() {
        super(false);
        addStartNpc(Tunatun);
        addKillId(FarmMonsters);
        addQuestItem(SelMahumMane);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("tunatun_q278_03.htm")) {
            st.setState(STARTED);
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
                if (st.getPlayer().getLevel() >= 82)
                    htmltext = "tunatun_q278_01.htm";
                else {
                    htmltext = "tunatun_q278_00.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                htmltext = "tunatun_q278_04.htm";
            else if (cond == 2) {
                if (st.getQuestItemsCount(SelMahumMane) >= 300) {
                    htmltext = "tunatun_q278_05.htm";
                    st.takeItems(SelMahumMane);
                    int rnd = Rnd.get(1, 13);
                    if (rnd < 11)
                        st.giveItems(960, rnd);
                    else if (rnd ==11)
                        st.giveItems(9553);
                    else if (rnd ==12)
                        st.giveItems(9553, 2);
                    else if (rnd ==13)
                        st.giveItems(959);
                    st.playSound(SOUND_FINISH);
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "tunatun_q278_04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1)
            if (FarmMonsters.contains(npcId) && st.getQuestItemsCount(SelMahumMane) < 300) {
                st.giveItems(SelMahumMane, 1, true);
                if (st.getQuestItemsCount(SelMahumMane) >= 300)
                    st.setCond(2);
            }
        return null;
    }
}