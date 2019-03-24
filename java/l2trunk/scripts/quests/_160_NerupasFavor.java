package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _160_NerupasFavor extends Quest {
    private static final int SILVERY_SPIDERSILK = 1026;
    private static final int UNOS_RECEIPT = 1027;
    private static final int CELS_TICKET = 1028;
    private static final int NIGHTSHADE_LEAF = 1029;
    private static final int LESSER_HEALING_POTION = 1060;

    private static final int NERUPA = 30370;
    private static final int UNOREN = 30147;
    private static final int CREAMEES = 30149;
    private static final int JULIA = 30152;

    /**
     * Delivery of Goods
     * Trader Unoren asked Nerupa to collect silvery spidersilks for him.
     * Norupa doesn't want to enter the village and asks you to deliver the silvery spidersilks to Trader Unoren in the weapons shop and bring back a nightshade leaf.	 *
     */
    private static final int COND1 = 1;

    /**
     * Nightshade Leaf
     * Nightshade leaves are very rare. Fortunately, Trader Creamees of the magic shop has obtained a few of them. Go see him with Unoren's receipt.
     */
    private static final int COND2 = 2;

    /**
     * Go to the Warehouse
     * Since nightshade leaf is so rare it has been stored in the warehouse. Take Creamees' ticket to Warehouse Keeper Julia.
     */
    private static final int COND3 = 3;

    /**
     * Goods to be Delivered to Nerupa
     * You've obtained the nightshade leaf that Creamees stored in the warehouse. Deliver it to Nerupa.
     */
    private static final int COND4 = 4;

    public _160_NerupasFavor() {
        addStartNpc(NERUPA);

        addTalkId(UNOREN, CREAMEES, JULIA);

        addQuestItem(SILVERY_SPIDERSILK, UNOS_RECEIPT, CELS_TICKET, NIGHTSHADE_LEAF);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equals("30370-04.htm")) {
            st.setCond(COND1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(SILVERY_SPIDERSILK);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == NERUPA) {
            if (st.getState() == CREATED) {
                if (st.player.getRace() != Race.elf)
                    htmltext = "30370-00.htm";
                else if (st.player.getLevel() < 3) {
                    htmltext = "30370-02.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "30370-03.htm";
            } else if (cond == COND1)
                htmltext = "30370-04.htm";
            else if (cond == COND4 && st.haveQuestItem(NIGHTSHADE_LEAF) ) {
                st.takeItems(NIGHTSHADE_LEAF);
                st.giveItems(LESSER_HEALING_POTION, 5);
                st.addExpAndSp(1000, 0);
                st.playSound(SOUND_FINISH);
                htmltext = "30370-06.htm";
                st.finish();
            } else
                htmltext = "30370-05.htm";
        } else if (npcId == UNOREN) {
            if (cond == COND1) {
                st.takeItems(SILVERY_SPIDERSILK);
                st.giveItems(UNOS_RECEIPT);
                st.setCond(COND2);
                htmltext = "30147-01.htm";
            } else if (cond == COND2 || cond == COND3)
                htmltext = "30147-02.htm";
            else if (cond == COND4)
                htmltext = "30147-03.htm";
        } else if (npcId == CREAMEES) {
            if (cond == COND2) {
                st.takeItems(UNOS_RECEIPT);
                st.giveItems(CELS_TICKET);
                st.setCond(COND3);
                htmltext = "30149-01.htm";
            } else if (cond == COND3)
                htmltext = "30149-02.htm";
            else if (cond == COND4)
                htmltext = "30149-03.htm";
        } else if (npcId == JULIA)
            if (cond == COND3) {
                st.takeItems(CELS_TICKET);
                st.giveItems(NIGHTSHADE_LEAF);
                htmltext = "30152-01.htm";
                st.setCond(COND4);
            } else if (cond == COND4)
                htmltext = "30152-02.htm";
        return htmltext;
    }
}