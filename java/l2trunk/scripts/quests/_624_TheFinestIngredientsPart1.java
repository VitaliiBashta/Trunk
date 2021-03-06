package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _624_TheFinestIngredientsPart1 extends Quest {
    //NPC
    private static final int JEREMY = 31521;

    //MOBS
    private static final int HOT_SPRINGS_ATROX = 21321;
    private static final int HOT_SPRINGS_NEPENTHES = 21319;
    private static final int HOT_SPRINGS_ATROXSPAWN = 21317;
    private static final int HOT_SPRINGS_BANDERSNATCHLING = 21314;

    //QUEST ITEMS
    private static final int SECRET_SPICE = 7204;
    private static final int TRUNK_OF_NEPENTHES = 7202;
    private static final int FOOT_OF_BANDERSNATCHLING = 7203;
    private static final int CRYOLITE = 7080;
    private static final int SAUCE = 7205;

    public _624_TheFinestIngredientsPart1() {
        super(true);

        addStartNpc(JEREMY);

        addKillId(HOT_SPRINGS_ATROX, HOT_SPRINGS_NEPENTHES, HOT_SPRINGS_ATROXSPAWN, HOT_SPRINGS_BANDERSNATCHLING);

        addQuestItem(TRUNK_OF_NEPENTHES, FOOT_OF_BANDERSNATCHLING, SECRET_SPICE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("jeremy_q0624_0104.htm".equalsIgnoreCase(event))
            if (st.player.getLevel() >= 73) {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            } else {
                htmltext = "jeremy_q0624_0103.htm";
                st.exitCurrentQuest();
            }
        else if ("jeremy_q0624_0201.htm".equalsIgnoreCase(event))
            if (st.getQuestItemsCount(TRUNK_OF_NEPENTHES) == 50 && st.getQuestItemsCount(FOOT_OF_BANDERSNATCHLING) == 50 && st.getQuestItemsCount(SECRET_SPICE) == 50) {
                st.takeItems(TRUNK_OF_NEPENTHES);
                st.takeItems(FOOT_OF_BANDERSNATCHLING);
                st.takeItems(SECRET_SPICE);
                st.playSound(SOUND_FINISH);
                st.giveItems(SAUCE);
                st.giveItems(CRYOLITE);
                htmltext = "jeremy_q0624_0201.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "jeremy_q0624_0202.htm";
                st.setCond(1);
            }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (cond == 0)
            htmltext = "jeremy_q0624_0101.htm";
        else if (cond != 3)
            htmltext = "jeremy_q0624_0106.htm";
        else
            htmltext = "jeremy_q0624_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();
        if (st.getCond() == 1) {
            if (npcId == HOT_SPRINGS_NEPENTHES && st.getQuestItemsCount(TRUNK_OF_NEPENTHES) < 50)
                st.rollAndGive(TRUNK_OF_NEPENTHES, 1, 1, 50, 100);
            else if (npcId == HOT_SPRINGS_BANDERSNATCHLING && st.getQuestItemsCount(FOOT_OF_BANDERSNATCHLING) < 50)
                st.rollAndGive(FOOT_OF_BANDERSNATCHLING, 1, 1, 50, 100);
            else if ((npcId == HOT_SPRINGS_ATROX || npcId == HOT_SPRINGS_ATROXSPAWN) && st.getQuestItemsCount(SECRET_SPICE) < 50)
                st.rollAndGive(SECRET_SPICE, 1, 1, 50, 100);
            onKillCheck(st);
        }
    }

    private void onKillCheck(QuestState st) {
        if (st.haveQuestItem(TRUNK_OF_NEPENTHES, 50)
                && st.haveQuestItem(FOOT_OF_BANDERSNATCHLING, 50)
                && st.haveQuestItem(SECRET_SPICE, 50)) {
            st.playSound(SOUND_MIDDLE);
            st.setCond(3);
        } else
            st.playSound(SOUND_ITEMGET);
    }
}