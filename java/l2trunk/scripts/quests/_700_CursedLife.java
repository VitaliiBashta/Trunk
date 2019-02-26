package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _700_CursedLife extends Quest {
    // NPC's
    private static final int Orbyu = 32560;

    // ITEMS
    private static final int SwallowedSkull = 13872;
    private static final int SwallowedSternum = 13873;
    private static final int SwallowedBones = 13874;

    // MOB's
    private static final int MutantBird1 = 22602;
    private static final int MutantBird2 = 22603;
    private static final int DraHawk1 = 22604;
    private static final int DraHawk2 = 22605;
    private static final int Rok = 25624;

    //Prices
    private static final int SKULLPRICE = 50000;
    private static final int STERNUMPRICE = 5000;
    private static final int BONESPRICE = 500;

    public _700_CursedLife() {
        super(false);

        addStartNpc(Orbyu);
        addTalkId(Orbyu);
        addKillId(MutantBird1, MutantBird2, DraHawk1, DraHawk2);
        addQuestItem(SwallowedSkull, SwallowedSternum, SwallowedBones);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();
        String htmltext = event;

        if (event.equals("orbyu_q700_2.htm") && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("ex_bones") && cond == 1)
            if (st.haveAnyQuestItems(SwallowedSkull,SwallowedSternum,SwallowedBones)) {
                long _adenatogive = st.getQuestItemsCount(SwallowedSkull) * SKULLPRICE + st.getQuestItemsCount(SwallowedSternum) * STERNUMPRICE + st.getQuestItemsCount(SwallowedBones) * BONESPRICE;

                st.giveItems(ADENA_ID, _adenatogive);
                    st.takeItems(SwallowedSkull);
                    st.takeItems(SwallowedSternum);
                    st.takeItems(SwallowedBones);
                htmltext = "orbyu_q700_4.htm";
            } else
                htmltext = "orbyu_q700_3a.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Orbyu)
            if (cond == 0) {
                if (st.player.getLevel() >= 75 && st.player.isQuestCompleted(_10273_GoodDayToFly.class))
                    htmltext = "orbyu_q700_1.htm";
                else {
                    htmltext = "orbyu_q700_0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                if (st.haveAnyQuestItems(SwallowedSkull,SwallowedSternum,SwallowedBones) )
                    htmltext = "orbyu_q700_3.htm";
                else
                    htmltext = "orbyu_q700_3a.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1)
            if (npcId == MutantBird1 || npcId == MutantBird2 || npcId == DraHawk1 || npcId == DraHawk2) {
                st.giveItems(SwallowedBones);
                st.playSound(SOUND_ITEMGET);
                if (Rnd.chance(20))
                    st.giveItems(SwallowedSkull);
                else if (Rnd.chance(20))
                    st.giveItems(SwallowedSternum);
            } else if (npcId == Rok) {
                st.giveItems(SwallowedSternum, 50);
                st.giveItems(SwallowedSkull, 30);
                st.giveItems(SwallowedBones, 100);
                st.playSound(SOUND_ITEMGET);
            }
    }
}