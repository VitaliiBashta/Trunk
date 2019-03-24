package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _125_InTheNameOfEvilPart1 extends Quest {
    private final int Mushika = 32114;
    private final int Karakawei = 32117;
    private final int UluKaimu = 32119;
    private final int BaluKaimu = 32120;
    private final int ChutaKaimu = 32121;
    private final int OrClaw = 8779;
    private final int DienBone = 8780;

    public _125_InTheNameOfEvilPart1() {
        addStartNpc(Mushika);
        addTalkId(Karakawei,UluKaimu,BaluKaimu,ChutaKaimu);
        addQuestItem(OrClaw, DienBone);
        addKillId(22742, 22743, 22744, 22745);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("32114-05.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("32114-07.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("32117-08.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("32117-13.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
        } else if ("stat1false".equalsIgnoreCase(event))
            htmltext = "32119-2.htm";
        else if ("stat1true".equalsIgnoreCase(event)) {
            st.setCond(6);
            htmltext = "32119-1.htm";
        } else if ("stat2false".equalsIgnoreCase(event))
            htmltext = "32120-2.htm";
        else if ("stat2true".equalsIgnoreCase(event)) {
            st.setCond(7);
            htmltext = "32120-1.htm";
        } else if ("stat3false".equalsIgnoreCase(event))
            htmltext = "32121-2.htm";
        else if ("stat3true".equalsIgnoreCase(event)) {
            st.giveItems(8781);
            st.setCond(8);
            htmltext = "32121-1.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Mushika) {
            if (cond == 0) {
                if (st.player.getLevel() > 76 && st.player.isQuestCompleted(_124_MeetingTheElroki.class))
                    htmltext = "32114.htm";
                else {
                    htmltext = "32114-0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "32114-05.htm";
            else if (cond == 8) {
                htmltext = "32114-08.htm";
                st.addExpAndSp(859195, 86603);
                st.playSound(SOUND_FINISH);
                st.complete();
                st.finish();
            }
        } else if (npcId == Karakawei) {
            if (cond == 2)
                htmltext = "32117.htm";
            else if (cond == 3)
                htmltext = "32117-09.htm";
            else if (cond == 4) {
                st.takeItems(DienBone);
                st.takeItems(OrClaw);
                htmltext = "32117-1.htm";
            }
        } else if (npcId == UluKaimu) {
            if (cond == 5)
                htmltext = "32119.htm";
        } else if (npcId == BaluKaimu) {
            if (cond == 6)
                htmltext = "32120.htm";
        } else if (npcId == ChutaKaimu)
            if (cond == 7)
                htmltext = "32121.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();

        if (st.getCond() == 3) {
            if ((npcId == 22744 || npcId == 22742) && st.getQuestItemsCount(OrClaw) < 2 && Rnd.chance(10 * Config.RATE_QUESTS_DROP)) {
                st.giveItems(OrClaw);
                st.playSound(SOUND_MIDDLE);
            }
            if ((npcId == 22743 || npcId == 22745) && st.getQuestItemsCount(DienBone) < 2 && Rnd.chance(10 * Config.RATE_QUESTS_DROP)) {
                st.giveItems(DienBone);
                st.playSound(SOUND_MIDDLE);
            }
            if (st.getQuestItemsCount(DienBone) >= 2 && st.getQuestItemsCount(OrClaw) >= 2)
                st.setCond(4);
        }
    }
}