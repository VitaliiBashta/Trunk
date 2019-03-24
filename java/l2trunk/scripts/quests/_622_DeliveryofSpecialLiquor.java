package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _622_DeliveryofSpecialLiquor extends Quest {
    //NPCs
    private static final int JEREMY = 31521;
    private static final int LIETTA = 31267;
    private static final int PULIN = 31543;
    private static final int NAFF = 31544;
    private static final int CROCUS = 31545;
    private static final int KUBER = 31546;
    private static final int BEOLIN = 31547;
    //Quest items
    private static final int SpecialDrink = 7207;
    private static final int FeeOfSpecialDrink = 7198;
    //items
    private static final int RecipeSealedTateossianRing = 6849;
    private static final int RecipeSealedTateossianEarring = 6847;
    private static final int RecipeSealedTateossianNecklace = 6851;
    private static final int HastePotion = 734;
    //Chances
    private static final int Tateossian_CHANCE = 20;

    public _622_DeliveryofSpecialLiquor() {
        addStartNpc(JEREMY);
        addTalkId(LIETTA,PULIN,NAFF,CROCUS,KUBER,BEOLIN);
        addQuestItem(SpecialDrink,FeeOfSpecialDrink);
    }

    private static void takeDrink(QuestState st, int setcond) {
        st.setCond(setcond);
        st.takeItems(SpecialDrink, 1);
        st.giveItems(FeeOfSpecialDrink);
        st.playSound(SOUND_MIDDLE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        int cond = st.getCond();
        long specialDrinkCount = st.getQuestItemsCount(SpecialDrink);

        if ("jeremy_q0622_0104.htm".equalsIgnoreCase(event) && state == CREATED) {
            st.start();
            st.setCond(1);
            st.takeAllItems(SpecialDrink,FeeOfSpecialDrink);
            st.giveItems(SpecialDrink, 5);
            st.playSound(SOUND_ACCEPT);
        } else if ("beolin_q0622_0201.htm".equalsIgnoreCase(event) && cond == 1 && specialDrinkCount > 0)
            takeDrink(st, 2);
        else if ("kuber_q0622_0301.htm".equalsIgnoreCase(event) && cond == 2 && specialDrinkCount > 0)
            takeDrink(st, 3);
        else if ("crocus_q0622_0401.htm".equalsIgnoreCase(event) && cond == 3 && specialDrinkCount > 0)
            takeDrink(st, 4);
        else if ("naff_q0622_0501.htm".equalsIgnoreCase(event) && cond == 4 && specialDrinkCount > 0)
            takeDrink(st, 5);
        else if ("pulin_q0622_0601.htm".equalsIgnoreCase(event) && cond == 5 && specialDrinkCount > 0)
            takeDrink(st, 6);
        else if ("jeremy_q0622_0701.htm".equalsIgnoreCase(event) && cond == 6 && st.getQuestItemsCount(FeeOfSpecialDrink) >= 5)
            st.setCond(7);
        else if ("warehouse_keeper_lietta_q0622_0801.htm".equalsIgnoreCase(event) && cond == 7 && st.getQuestItemsCount(FeeOfSpecialDrink) >= 5) {
            st.takeAllItems(SpecialDrink, FeeOfSpecialDrink);
            if (Rnd.chance(Tateossian_CHANCE)) {
                if (Rnd.chance(40))
                    st.giveItems(RecipeSealedTateossianRing);
                else if (Rnd.chance(40))
                    st.giveItems(RecipeSealedTateossianEarring);
                else
                    st.giveItems(RecipeSealedTateossianNecklace);
            } else {
                st.giveAdena(18800);
                st.giveItems(HastePotion);
            }

            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
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
                return "jeremy_q0622_0101.htm";
            }
            st.exitCurrentQuest();
            return "jeremy_q0622_0103.htm";
        }

        int cond = st.getCond();
        boolean haveSpecialDrink = st.haveQuestItem(SpecialDrink);
        long feeOfSpecialDrinkCount = st.getQuestItemsCount(FeeOfSpecialDrink);

        if (cond == 1 && npcId == BEOLIN && haveSpecialDrink )
            htmltext = "beolin_q0622_0101.htm";
        else if (cond == 2 && npcId == KUBER && haveSpecialDrink )
            htmltext = "kuber_q0622_0201.htm";
        else if (cond == 3 && npcId == CROCUS && haveSpecialDrink )
            htmltext = "crocus_q0622_0301.htm";
        else if (cond == 4 && npcId == NAFF && haveSpecialDrink )
            htmltext = "naff_q0622_0401.htm";
        else if (cond == 5 && npcId == PULIN && haveSpecialDrink )
            htmltext = "pulin_q0622_0501.htm";
        else if (cond == 6 && npcId == JEREMY && feeOfSpecialDrinkCount >= 5)
            htmltext = "jeremy_q0622_0601.htm";
        else if (cond == 7 && npcId == JEREMY && feeOfSpecialDrinkCount >= 5)
            htmltext = "jeremy_q0622_0703.htm";
        else if (cond == 7 && npcId == LIETTA && feeOfSpecialDrinkCount >= 5)
            htmltext = "warehouse_keeper_lietta_q0622_0701.htm";
        else if (cond > 0 && npcId == JEREMY && haveSpecialDrink )
            htmltext = "jeremy_q0622_0104.htm";
        return htmltext;
    }

}