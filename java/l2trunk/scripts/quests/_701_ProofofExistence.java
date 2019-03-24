package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _701_ProofofExistence extends Quest {
    // NPC's
    private static final int Artius = 32559;

    // ITEMS
    private static final int DeadmansRemains = 13875;
    private static final int BansheeQueensEye = 13876;

    // MOB's
    private static final int Enira = 25625;
    private static final int FloatingSkull1 = 22606;
    private static final int FloatingSkull2 = 22607;
    private static final int FloatingZombie1 = 22608;
    private static final int FloatingZombie2 = 22609;

    public _701_ProofofExistence() {
        addStartNpc(Artius);
        addKillId(Enira, FloatingSkull1, FloatingSkull2, FloatingZombie1, FloatingZombie2);
        addQuestItem(DeadmansRemains, BansheeQueensEye);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;

        if (event.equals("artius_q701_2.htm") && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("ex_mons") && cond == 1) {
            if (st.haveQuestItem(DeadmansRemains) ) {
                st.giveAdena( st.getQuestItemsCount(DeadmansRemains) * 2500); // умножается на рейт квестов
                st.takeItems(DeadmansRemains);
                htmltext = "artius_q701_4.htm";
            } else
                htmltext = "artius_q701_3a.htm";
        } else if (event.equals("ex_boss") && cond == 1)
            if (st.haveQuestItem(BansheeQueensEye)) {
                st.giveAdena( st.getQuestItemsCount(BansheeQueensEye) * 1000000); // умножается на рейт квестов
                st.takeItems(BansheeQueensEye);
                htmltext = "artius_q701_4.htm";
            } else
                htmltext = "artius_q701_3a.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Artius)
            if (cond == 0) {
                if (st.player.getLevel() >= 78 && st.player.isQuestCompleted(_10273_GoodDayToFly.class))
                    htmltext = "artius_q701_1.htm";
                else {
                    htmltext = "artius_q701_0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                if (st.haveAnyQuestItems(DeadmansRemains,BansheeQueensEye))
                    htmltext = "artius_q701_3.htm";
                else
                    htmltext = "artius_q701_3a.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId != Enira) {
            st.giveItems(DeadmansRemains);
            st.playSound(SOUND_ITEMGET);
        } else if (cond == 1) {
            st.giveItems(BansheeQueensEye);
            st.playSound(SOUND_ITEMGET);
        }
    }
}