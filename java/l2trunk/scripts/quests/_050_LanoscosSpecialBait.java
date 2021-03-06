package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _050_LanoscosSpecialBait extends Quest {
    // NPC
    private final int Lanosco = 31570;
    private final int SingingWind = 21026;
    // items
    private static final int EssenceofWind = 7621;
    private static final int WindFishingLure = 7610;
    // Skill
    private static final Integer FishSkill = 1315;

    public _050_LanoscosSpecialBait() {
        addStartNpc(Lanosco);

        addKillId(SingingWind);

        addQuestItem(EssenceofWind);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("fisher_lanosco_q0050_0104.htm")) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("fisher_lanosco_q0050_0201.htm"))
            if (st.getQuestItemsCount(EssenceofWind) < 100)
                htmltext = "fisher_lanosco_q0050_0202.htm";
            else {
                st.unset("cond");
                st.takeItems(EssenceofWind);
                st.giveItems(WindFishingLure, 4);
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
        if (npcId == Lanosco)
            if (id == CREATED) {
                if (st.player.getLevel() < 27) {
                    htmltext = "fisher_lanosco_q0050_0103.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getSkillLevel(FishSkill) >= 8)
                    htmltext = "fisher_lanosco_q0050_0101.htm";
                else {
                    htmltext = "fisher_lanosco_q0050_0102.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 || cond == 2)
                if (st.getQuestItemsCount(EssenceofWind) < 100) {
                    htmltext = "fisher_lanosco_q0050_0106.htm";
                    st.setCond(1);
                } else
                    htmltext = "fisher_lanosco_q0050_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == SingingWind && st.getCond() == 1)
            if (Rnd.chance(30)) {
                st.giveItemIfNotHave(EssenceofWind, 100);
                if (st.getQuestItemsCount(EssenceofWind) == 100) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(2);
                }
            }
    }
}