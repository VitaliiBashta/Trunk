package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _051_OFullesSpecialBait extends Quest {
    private static final int IcyAirFishingLure = 7611;
    private static final int FishSkill = 1315;
    private final int OFulle = 31572;
    private final int FetteredSoul = 20552;
    private final int LostBaitIngredient = 7622;

    public _051_OFullesSpecialBait() {
        super(false);

        addStartNpc(OFulle);

        addTalkId(OFulle);

        addKillId(FetteredSoul);

        addQuestItem(LostBaitIngredient);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("fisher_ofulle_q0051_0104.htm")) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("fisher_ofulle_q0051_0201.htm"))
            if (st.getQuestItemsCount(LostBaitIngredient) < 100)
                htmltext = "fisher_ofulle_q0051_0202.htm";
            else {
                st.unset("cond");
                st.takeItems(LostBaitIngredient, -1);
                st.giveItems(IcyAirFishingLure, 4);
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
        if (npcId == OFulle)
            if (id == CREATED) {
                if (st.player.getLevel() < 36) {
                    htmltext = "fisher_ofulle_q0051_0103.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getSkillLevel(FishSkill) >= 11)
                    htmltext = "fisher_ofulle_q0051_0101.htm";
                else {
                    htmltext = "fisher_ofulle_q0051_0102.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 || cond == 2)
                if (st.getQuestItemsCount(LostBaitIngredient) < 100) {
                    htmltext = "fisher_ofulle_q0051_0106.htm";
                    st.setCond(1);
                } else
                    htmltext = "fisher_ofulle_q0051_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (npcId == FetteredSoul && st.getCond() == 1)
            if (st.getQuestItemsCount(LostBaitIngredient) < 100 && Rnd.chance(30)) {
                st.giveItems(LostBaitIngredient);
                if (st.getQuestItemsCount(LostBaitIngredient) == 100) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(2);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}
