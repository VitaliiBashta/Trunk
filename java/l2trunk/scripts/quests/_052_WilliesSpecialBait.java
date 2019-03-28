package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _052_WilliesSpecialBait extends Quest {
    private final static int Willie = 31574;
    private final static List<Integer> TarlkBasilisks = List.of(20573, 20574);
    private final static int EyeOfTarlkBasilisk = 7623;
    private final static int EarthFishingLure = 7612;
    private final static Integer FishSkill = 1315;

    public _052_WilliesSpecialBait() {
        addStartNpc(Willie);

        addKillId(TarlkBasilisks);

        addQuestItem(EyeOfTarlkBasilisk);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("fisher_willeri_q0052_0104.htm")) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("fisher_willeri_q0052_0201.htm"))
            if (st.getQuestItemsCount(EyeOfTarlkBasilisk) < 100)
                htmltext = "fisher_willeri_q0052_0202.htm";
            else {
                st.unset("cond");
                st.takeItems(EyeOfTarlkBasilisk);
                st.giveItems(EarthFishingLure, 4);
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
        if (npcId == Willie)
            if (id == CREATED) {
                if (st.player.getLevel() < 48) {
                    htmltext = "fisher_willeri_q0052_0103.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getSkillLevel(FishSkill) >= 16)
                    htmltext = "fisher_willeri_q0052_0101.htm";
                else {
                    htmltext = "fisher_willeri_q0052_0102.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 || cond == 2)
                if (st.getQuestItemsCount(EyeOfTarlkBasilisk) < 100) {
                    htmltext = "fisher_willeri_q0052_0106.htm";
                    st.setCond(1);
                } else
                    htmltext = "fisher_willeri_q0052_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (TarlkBasilisks.contains(npc.getNpcId()) && st.getCond() == 1)
            if (Rnd.chance(30)) {
                st.giveItemIfNotHave(EyeOfTarlkBasilisk, 100);
                if (st.getQuestItemsCount(EyeOfTarlkBasilisk) == 100) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(2);
                }
            }
    }
}