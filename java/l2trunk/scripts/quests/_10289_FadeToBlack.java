package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.StringTokenizer;

public final class _10289_FadeToBlack extends Quest {
    private static final int Greymore = 32757;
    private static final int Anays = 25701;
    private static final int MarkofSplendor = 15527;
    private static final int MarkofDarkness = 15528;

    public _10289_FadeToBlack() {
        super(PARTY_ALL);
        addStartNpc(Greymore);
        addKillId(Anays);
        addQuestItem(MarkofSplendor, MarkofDarkness);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("greymore_q10289_03.htm".equalsIgnoreCase(event)) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("showmark")) {
            if (st.getCond() == 2 && st.getQuestItemsCount(MarkofDarkness) > 0)
                htmltext = "greymore_q10289_06.htm";
            else if (st.getCond() == 3 && st.getQuestItemsCount(MarkofSplendor) > 0)
                htmltext = "greymore_q10289_07.htm";
            else
                htmltext = "greymore_q10289_08.htm";
        } else if (event.startsWith("exchange")) {
            StringTokenizer str = new StringTokenizer(event);
            str.nextToken();
            int id = Integer.parseInt(str.nextToken());
            switch (id) {
                case 1:
                    st.giveItems(15775);
                    st.giveItems(ADENA_ID, 420920);
                    break;
                case 2:
                    st.giveItems(15776);
                    st.giveItems(ADENA_ID, 420920);
                    break;
                case 3:
                    st.giveItems(15777);
                    st.giveItems(ADENA_ID, 420920);
                    break;
                case 4:
                    st.giveItems(15778);
                    break;
                case 5:
                    st.giveItems(15779);
                    st.giveItems(ADENA_ID, 168360);
                    break;
                case 6:
                    st.giveItems(15780);
                    st.giveItems(ADENA_ID, 168360);
                    break;
                case 7:
                    st.giveItems(15781);
                    st.giveItems(ADENA_ID, 252540);
                    break;
                case 8:
                    st.giveItems(15782);
                    st.giveItems(ADENA_ID, 357780);
                    break;
                case 9:
                    st.giveItems(15783);
                    st.giveItems(ADENA_ID, 357780);
                    break;
                case 10:
                    st.giveItems(15784);
                    st.giveItems(ADENA_ID, 505100);
                    break;
                case 11:
                    st.giveItems(15785);
                    st.giveItems(ADENA_ID, 505100);
                    break;
                case 12:
                    st.giveItems(15786);
                    st.giveItems(ADENA_ID, 505100);
                    break;
                case 13:
                    st.giveItems(15787);
                    st.giveItems(ADENA_ID, 505100);
                    break;
                case 14:
                    st.giveItems(15788);
                    st.giveItems(ADENA_ID, 505100);
                    break;
                case 15:
                    st.giveItems(15789);
                    st.giveItems(ADENA_ID, 505100);
                    break;
                case 16:
                    st.giveItems(15790);
                    st.giveItems(ADENA_ID, 496680);
                    break;
                case 17:
                    st.giveItems(15791);
                    st.giveItems(ADENA_ID, 496680);
                    break;
                case 18:
                    st.giveItems(15812);
                    st.giveItems(ADENA_ID, 563860);
                    break;
                case 19:
                    st.giveItems(15813);
                    st.giveItems(ADENA_ID, 509040);
                    break;
                case 20:
                    st.giveItems(15814);
                    st.giveItems(ADENA_ID, 454240);
                    break;
            }
            htmltext = "greymore_q10289_09.htm";
            st.takeItems(MarkofSplendor);
            st.takeItems(MarkofDarkness);
            st.exitCurrentQuest(false);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == Greymore) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82 && st.player.isQuestCompleted(_10288_SecretMission.class))
                    htmltext = "greymore_q10289_01.htm";
                else {
                    htmltext = "greymore_q10289_00.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                htmltext = "greymore_q10289_04.htm";
            else if (cond == 2 || cond == 3)
                htmltext = "greymore_q10289_05.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 1) {
            if (npc.getNpcId() == Anays) {
                if (Rnd.chance(30)) {
                    st.giveItems(MarkofSplendor);
                    st.setCond(3);
                } else {
                    st.giveItems(MarkofDarkness);
                    st.setCond(2);
                }
            }
        }
    }

}