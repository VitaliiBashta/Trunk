package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _053_LinnaeusSpecialBait extends Quest {
    private static final int FlameFishingLure = 7613;
    private static final int FishSkill = 1315;
    private final int Linnaeu = 31577;
    private final int CrimsonDrake = 20670;
    private final int HeartOfCrimsonDrake = 7624;

    public _053_LinnaeusSpecialBait() {
        addStartNpc(Linnaeu);

        addKillId(CrimsonDrake);
        addQuestItem(HeartOfCrimsonDrake);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("fisher_linneaus_q0053_0104.htm")) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("fisher_linneaus_q0053_0201.htm"))
            if (st.getQuestItemsCount(HeartOfCrimsonDrake) < 100)
                htmltext = "fisher_linneaus_q0053_0202.htm";
            else {
                st.unset("cond");
                st.takeItems(HeartOfCrimsonDrake);
                st.giveItems(FlameFishingLure, 4);
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        int id = st.getState();
        if (npcId == Linnaeu)
            if (id == CREATED) {
                if (st.player.getLevel() < 60) {
                    htmltext = "fisher_linneaus_q0053_0103.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getSkillLevel(FishSkill) >= 21)
                    htmltext = "fisher_linneaus_q0053_0101.htm";
                else {
                    htmltext = "fisher_linneaus_q0053_0102.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 || cond == 2)
                if (st.getQuestItemsCount(HeartOfCrimsonDrake) < 100) {
                    htmltext = "fisher_linneaus_q0053_0106.htm";
                    st.setCond(1);
                } else
                    htmltext = "fisher_linneaus_q0053_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == CrimsonDrake && st.getCond() == 1)
            if (st.getQuestItemsCount(HeartOfCrimsonDrake) < 100 && Rnd.chance(30)) {
                st.giveItems(HeartOfCrimsonDrake);
                if (st.getQuestItemsCount(HeartOfCrimsonDrake) == 100) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(2);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}