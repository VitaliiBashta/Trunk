package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;

public final class _120_PavelsResearch extends Quest {
    //NPC
    private static final int Yumi = 32041;
    private static final int Weather1 = 32042;
    private static final int Weather2 = 32043;
    private static final int Weather3 = 32044;
    private static final int BookShelf = 32045;
    private static final int Stones = 32046;
    private static final int Wendy = 32047;
    //ITEMS
    private static final int EarPhoenix = 6324;
    private static final int Report = 8058;
    private static final int Report2 = 8059;
    private static final int Enigma = 8060;
    private static final int Flower = 8290;
    private static final int Heart = 8291;
    private static final int Necklace = 8292;

    public _120_PavelsResearch() {
        addStartNpc(Stones);

        addQuestItem(Report, Report2, Enigma, Flower, Heart, Necklace);

        addTalkId(BookShelf,Stones,Weather1,Weather2,Weather3,Wendy,Yumi);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("32041-03.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound("ItemSound.quest_middle");
        } else if ("32041-04.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound("ItemSound.quest_middle");
        } else if ("32041-12.htm".equalsIgnoreCase(event)) {
            st.setCond(8);
            st.playSound("ItemSound.quest_middle");
        } else if ("32041-16.htm".equalsIgnoreCase(event)) {
            st.setCond(16);
            st.giveItems(Enigma);
            st.playSound("ItemSound.quest_middle");
        } else if ("32041-22.htm".equalsIgnoreCase(event)) {
            st.setCond(17);
            st.takeItems(Enigma);
            st.playSound("ItemSound.quest_middle");
        } else if ("32041-32.htm".equalsIgnoreCase(event)) {

            st.takeItems(Necklace);
            st.giveItems(EarPhoenix);
            st.giveAdena( 783720);
            st.addExpAndSp(3447315, 272615);
            st.finish();
            st.playSound(SOUND_FINISH);
        } else if ("32042-06.htm".equalsIgnoreCase(event)) {
            if (st.getCond() == 10)
                if (st.isSet("talk") &&  st.isSet("talk1")) {
                    st.setCond(11);
                    st.unset("talk");
                    st.unset("talk1");
                    st.playSound("ItemSound.quest_middle");
                } else
                    htmltext = "32042-03.htm";
        } else if ("32042-10.htm".equalsIgnoreCase(event)) {
            if (st.isSet("talk") && st.isSet("talk1") && st.isSet("talk2") )
                htmltext = "32042-14.htm";
        } else if ("32042-11.htm".equalsIgnoreCase(event)) {
                st.set("talk");
        } else if ("32042-12.htm".equalsIgnoreCase(event)) {
                st.set("talk1");
        } else if ("32042-13.htm".equalsIgnoreCase(event)) {
                st.set("talk2");
        } else if ("32042-15.htm".equalsIgnoreCase(event)) {
            st.setCond(12);
            st.unset("talk");
            st.unset("talk1");
            st.unset("talk2");
            st.playSound("ItemSound.quest_middle");
        } else if ("32043-06.htm".equalsIgnoreCase(event)) {
            if (st.getCond() == 17)
                if (st.isSet("talk") && st.isSet("talk1") ) {
                    st.setCond(18);
                    st.unset("talk");
                    st.unset("talk1");
                    st.playSound("ItemSound.quest_middle");
                } else
                    htmltext = "32043-03.htm";
        } else if ("32043-15.htm".equalsIgnoreCase(event)) {
            if (st.isSet("talk") && st.isSet("talk1"))
                htmltext = "32043-29.htm";
        } else if ("32043-18.htm".equalsIgnoreCase(event)) {
            if (st.isSet("talk") )
                htmltext = "32043-21.htm";
        } else if ("32043-20.htm".equalsIgnoreCase(event)) {
            st.set("talk");
            st.playSound("AmbSound.ed_drone_02");
        } else if ("32043-28.htm".equalsIgnoreCase(event))
            st.set("talk1");
        else if ("32043-30.htm".equalsIgnoreCase(event)) {
            st.setCond(19);
            st.unset("talk");
            st.unset("talk1");
        } else if ("32044-06.htm".equalsIgnoreCase(event)) {
            if (st.getCond() == 20)
                if (st.isSet("talk") && st.isSet("talk1")) {
                    st.setCond(21);
                    st.unset("talk");
                    st.unset("talk1");
                    st.playSound("ItemSound.quest_middle");
                } else
                    htmltext = "32044-03.htm";
        } else if ("32044-08.htm".equalsIgnoreCase(event)) {
            if (st.isSet("talk") && st.isSet("talk1"))
                htmltext = "32044-11.htm";
        } else if ("32044-09.htm".equalsIgnoreCase(event)) {
            if (!st.isSet("talk"))
                st.set("talk");
        } else if ("32044-10.htm".equalsIgnoreCase(event)) {
            if (!st.isSet("talk1"))
                st.set("talk1");
        } else if ("32044-17.htm".equalsIgnoreCase(event)) {
            st.setCond(22);
            st.unset("talk");
            st.unset("talk1");
            st.playSound("ItemSound.quest_middle");
        } else if ("32045-02.htm".equalsIgnoreCase(event)) {
            st.setCond(15);
            st.playSound("ItemSound.quest_middle");
            st.giveItems(Report);
            Player player = st.player;
            if (player != null)
                npc.broadcastPacket(new MagicSkillUse(npc, player, 5073, 5, 1500, 0));
        } else if ("32046-04.htm".equalsIgnoreCase(event) || "32046-05.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();
        else if ("32046-06.htm".equalsIgnoreCase(event)) {
            if (st.player.getLevel() >= 50) {
                st.playSound("ItemSound.quest_accept");
                st.setCond(1);
                st.start();
            } else {
                htmltext = "32046-00.htm";
                st.exitCurrentQuest();
            }
        } else if ("32046-08.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound("ItemSound.quest_middle");
        } else if ("32046-12.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.playSound("ItemSound.quest_middle");
            st.giveItems(Flower);
        } else if ("32046-22.htm".equalsIgnoreCase(event)) {
            st.setCond(10);
            st.playSound("ItemSound.quest_middle");
        } else if ("32046-29.htm".equalsIgnoreCase(event)) {
            st.setCond(13);
            st.playSound("ItemSound.quest_middle");
        } else if ("32046-35.htm".equalsIgnoreCase(event)) {
            st.setCond(20);
            st.playSound("ItemSound.quest_middle");
        } else if ("32046-38.htm".equalsIgnoreCase(event)) {
            st.setCond(23);
            st.playSound("ItemSound.quest_middle");
            st.giveItems(Heart);
        } else if ("32047-06.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.playSound("ItemSound.quest_middle");
        } else if ("32047-10.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.playSound("ItemSound.quest_middle");
            st.takeItems(Flower);
        } else if ("32047-15.htm".equalsIgnoreCase(event)) {
            st.setCond(9);
            st.playSound("ItemSound.quest_middle");
        } else if ("32047-18.htm".equalsIgnoreCase(event)) {
            st.setCond(14);
            st.playSound("ItemSound.quest_middle");
        } else if ("32047-26.htm".equalsIgnoreCase(event)) {
            st.setCond(24);
            st.playSound("ItemSound.quest_middle");
            st.takeItems(Heart);
        } else if ("32047-32.htm".equalsIgnoreCase(event)) {
            st.setCond(25);
            st.playSound("ItemSound.quest_middle");
            st.giveItems(Necklace);
        } else if ("w1_1".equalsIgnoreCase(event)) {
            st.set("talk");
            htmltext = "32042-04.htm";
        } else if ("w1_2".equalsIgnoreCase(event)) {
            st.set("talk1");
            htmltext = "32042-05.htm";
        } else if ("w2_1".equalsIgnoreCase(event)) {
            st.set("talk");
            htmltext = "32043-04.htm";
        } else if ("w2_2".equalsIgnoreCase(event)) {
            st.set("talk1");
            htmltext = "32043-05.htm";
        } else if ("w3_1".equalsIgnoreCase(event)) {
            st.set("talk");
            htmltext = "32044-04.htm";
        } else if ("w3_2".equalsIgnoreCase(event)) {
            st.set("talk1");
            htmltext = "32044-05.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "<html><head><body>You are either not on a quest that involves this NPC, or you don't meet this NPC's minimum quest requirements.</body></html>";
        int cond = st.getCond();
        if (npcId == Stones) {
            QuestState q = st.player.getQuestState(_114_ResurrectionOfAnOldManager.class);
            if (q == null)
                return htmltext;
            else if (cond == 0) {
                if (st.player.getLevel() >= 70 && q.isCompleted())
                    htmltext = "32046-01.htm";
                else {
                    htmltext = "32046-00.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1)
                htmltext = "32046-06.htm";
            else if (cond == 2)
                htmltext = "32046-09.htm";
            else if (cond == 5)
                htmltext = "32046-10.htm";
            else if (cond == 6)
                htmltext = "32046-13.htm";
            else if (cond == 9)
                htmltext = "32046-14.htm";
            else if (cond == 10)
                htmltext = "32046-23.htm";
            else if (cond == 12)
                htmltext = "32046-26.htm";
            else if (cond == 13)
                htmltext = "32046-30.htm";
            else if (cond == 19)
                htmltext = "32046-31.htm";
            else if (cond == 20)
                htmltext = "32046-36.htm";
            else if (cond == 22)
                htmltext = "32046-37.htm";
            else if (cond == 23)
                htmltext = "32046-39.htm";
        } else if (npcId == Wendy) {
            if (cond == 2 || cond == 3 || cond == 4)
                htmltext = "32047-01.htm";
            else if (cond == 5)
                htmltext = "32047-07.htm";
            else if (cond == 6)
                htmltext = "32047-08.htm";
            else if (cond == 7)
                htmltext = "32047-11.htm";
            else if (cond == 8)
                htmltext = "32047-12.htm";
            else if (cond == 9)
                htmltext = "32047-15.htm";
            else if (cond == 13)
                htmltext = "32047-16.htm";
            else if (cond == 14)
                htmltext = "32047-19.htm";
            else if (cond == 15)
                htmltext = "32047-20.htm";
            else if (cond == 23)
                htmltext = "32047-21.htm";
            else if (cond == 24)
                htmltext = "32047-26.htm";
            else if (cond == 25)
                htmltext = "32047-33.htm";
        } else if (npcId == Yumi) {
            if (cond == 2)
                htmltext = "32041-01.htm";
            else if (cond == 3)
                htmltext = "32041-05.htm";
            else if (cond == 4)
                htmltext = "32041-06.htm";
            else if (cond == 7)
                htmltext = "32041-07.htm";
            else if (cond == 8)
                htmltext = "32041-13.htm";
            else if (cond == 15)
                htmltext = "32041-14.htm";
            else if (cond == 16) {
                if (st.haveQuestItem(Report2))
                    htmltext = "32041-18.htm";
                else
                    htmltext = "32041-17.htm";
            } else if (cond == 17)
                htmltext = "32041-22.htm";
            else if (cond == 25)
                htmltext = "32041-26.htm";
        } else if (npcId == Weather1) {
            if (cond == 10)
                htmltext = "32042-01.htm";
            else if (cond == 11) {
                if (st.isSet("talk") && st.isSet("talk1") &&  st.isSet("talk2"))
                    htmltext = "32042-14.htm";
                else
                    htmltext = "32042-06.htm";
            } else if (cond == 12)
                htmltext = "32042-15.htm";
        } else if (npcId == Weather2) {
            if (cond == 17)
                htmltext = "32043-01.htm";
            else if (cond == 18) {
                if (st.isSet("talk") && st.isSet("talk1"))
                    htmltext = "32043-29.htm";
                else
                    htmltext = "32043-06.htm";
            } else if (cond == 19)
                htmltext = "32043-30.htm";
        } else if (npcId == Weather3) {
            if (cond == 20)
                htmltext = "32044-01.htm";
            else if (cond == 21)
                htmltext = "32044-06.htm";
            else if (cond == 22)
                htmltext = "32044-18.htm";
        } else if (npcId == BookShelf)
            if (cond == 14)
                htmltext = "32045-01.htm";
            else if (cond == 15)
                htmltext = "32045-03.htm";
        return htmltext;
    }
}