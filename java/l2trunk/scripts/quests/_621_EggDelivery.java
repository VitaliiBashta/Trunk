package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _621_EggDelivery extends Quest {
    //NPC
    private static final int JEREMY = 31521;
    private static final int VALENTINE = 31584;
    private static final int PULIN = 31543;
    private static final int NAFF = 31544;
    private static final int CROCUS = 31545;
    private static final int KUBER = 31546;
    private static final int BEOLIN = 31547;
    //Quest Items
    private static final int BoiledEgg = 7206;
    private static final int FeeOfBoiledEgg = 7196;
    //Items
    private static final int HastePotion = 734;
    private static final int RecipeSealedTateossianRing = 6849;
    private static final int RecipeSealedTateossianEarring = 6847;
    private static final int RecipeSealedTateossianNecklace = 6851;
    //Chances
    private static final int Tateossian_CHANCE = 20;

    public _621_EggDelivery() {
        super(false);
        addStartNpc(JEREMY);
        addTalkId(VALENTINE);
        addTalkId(PULIN);
        addTalkId(NAFF);
        addTalkId(CROCUS);
        addTalkId(KUBER);
        addTalkId(BEOLIN);
        addQuestItem(BoiledEgg);
        addQuestItem(FeeOfBoiledEgg);
    }

    private static void takeEgg(QuestState st, int setcond) {
        st.setCond(setcond);
        st.takeItems(BoiledEgg, 1);
        st.giveItems(FeeOfBoiledEgg);
        st.playSound(SOUND_MIDDLE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int _state = st.getState();
        int cond = st.getCond();
        long BoiledEgg_count = st.getQuestItemsCount(BoiledEgg);

        if ("jeremy_q0621_0104.htm".equalsIgnoreCase(event) && _state == CREATED) {
            st.takeItems(BoiledEgg);
            st.takeItems(FeeOfBoiledEgg);
            st.setState(STARTED);
            st.setCond(1);
            st.giveItems(BoiledEgg, 5);
            st.playSound(SOUND_ACCEPT);
        } else if ("pulin_q0621_0201.htm".equalsIgnoreCase(event) && cond == 1 && BoiledEgg_count > 0)
            takeEgg(st, 2);
        else if ("naff_q0621_0301.htm".equalsIgnoreCase(event) && cond == 2 && BoiledEgg_count > 0)
            takeEgg(st, 3);
        else if ("crocus_q0621_0401.htm".equalsIgnoreCase(event) && cond == 3 && BoiledEgg_count > 0)
            takeEgg(st, 4);
        else if ("kuber_q0621_0501.htm".equalsIgnoreCase(event) && cond == 4 && BoiledEgg_count > 0)
            takeEgg(st, 5);
        else if ("beolin_q0621_0601.htm".equalsIgnoreCase(event) && cond == 5 && BoiledEgg_count > 0)
            takeEgg(st, 6);
        else if ("jeremy_q0621_0701.htm".equalsIgnoreCase(event) && cond == 6 && st.getQuestItemsCount(FeeOfBoiledEgg) >= 5)
            st.setCond(7);
        else if ("brewer_valentine_q0621_0801.htm".equalsIgnoreCase(event) && cond == 7 && st.getQuestItemsCount(FeeOfBoiledEgg) >= 5) {
            st.takeItems(BoiledEgg);
            st.takeItems(FeeOfBoiledEgg);
            if (Rnd.chance(Tateossian_CHANCE)) {
                if (Rnd.chance(40))
                    st.giveItems(RecipeSealedTateossianRing);
                else if (Rnd.chance(40))
                    st.giveItems(RecipeSealedTateossianEarring);
                else
                    st.giveItems(RecipeSealedTateossianNecklace);
            } else {
                st.giveItems(ADENA_ID, 18800);
                st.giveItems(HastePotion, 1, true);
            }

            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        if (st.getState() == CREATED) {
            if (npcId != JEREMY)
                return htmltext;
            if (st.player.getLevel() >= 68) {
                st.setCond(0);
                return "jeremy_q0621_0101.htm";
            }
            st.exitCurrentQuest(true);
            return "jeremy_q0621_0103.htm";
        }

        int cond = st.getCond();
        long BoiledEgg_count = st.getQuestItemsCount(BoiledEgg);
        long FeeOfBoiledEgg_count = st.getQuestItemsCount(FeeOfBoiledEgg);

        if (cond == 1 && npcId == PULIN && BoiledEgg_count > 0)
            htmltext = "pulin_q0621_0101.htm";
        if (cond == 2 && npcId == NAFF && BoiledEgg_count > 0)
            htmltext = "naff_q0621_0201.htm";
        if (cond == 3 && npcId == CROCUS && BoiledEgg_count > 0)
            htmltext = "crocus_q0621_0301.htm";
        if (cond == 4 && npcId == KUBER && BoiledEgg_count > 0)
            htmltext = "kuber_q0621_0401.htm";
        if (cond == 5 && npcId == BEOLIN && BoiledEgg_count > 0)
            htmltext = "beolin_q0621_0501.htm";
        if (cond == 6 && npcId == JEREMY && FeeOfBoiledEgg_count >= 5)
            htmltext = "jeremy_q0621_0601.htm";
        if (cond == 7 && npcId == JEREMY && FeeOfBoiledEgg_count >= 5)
            htmltext = "jeremy_q0621_0703.htm";
        if (cond == 7 && npcId == VALENTINE && FeeOfBoiledEgg_count >= 5)
            htmltext = "brewer_valentine_q0621_0701.htm";
        else if (cond > 0 && npcId == JEREMY && BoiledEgg_count > 0)
            htmltext = "jeremy_q0621_0104.htm";
        return htmltext;
    }

}