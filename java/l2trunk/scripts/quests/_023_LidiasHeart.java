package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _023_LidiasHeart extends Quest {
    // ~~~~~ itemId List ~~~~~
    private static final int MapForestofDeadman = 7063;
    // ~~~~~~ npcId list: ~~~~~~
    private final int Innocentin = 31328;
    private final int BrokenBookshelf = 31526;
    private final int GhostofvonHellmann = 31524;
    private final int Tombstone = 31523;
    private final int Violet = 31386;
    private final int Box = 31530;
    private final int SilverKey = 7149;
    private final int LidiaHairPin = 7148;
    private final int LidiaDiary = 7064;
    private final int SilverSpear = 7150;

    public _023_LidiasHeart() {
        super(false);

        addStartNpc(Innocentin);

        addTalkId(BrokenBookshelf,GhostofvonHellmann,Tombstone,Violet,Box);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "31328-02.htm":
                st.giveItems(MapForestofDeadman);
                st.giveItems(SilverKey);
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "31328-03.htm":
                st.setCond(2);
                break;
            case "31526-01.htm":
                st.setCond(3);
                break;
            case "31526-05.htm":
                st.giveItems(LidiaHairPin);
                if (st.getQuestItemsCount(LidiaDiary) != 0)
                    st.setCond(4);
                break;
            case "31526-11.htm":
                st.giveItems(LidiaDiary);
                if (st.getQuestItemsCount(LidiaHairPin) != 0)
                    st.setCond(4);
                break;
            case "31328-19.htm":
                st.setCond(6);
                break;
            case "31524-04.htm":
                st.setCond(7);
                st.takeItems(LidiaDiary);
                break;
            case "31523-02.htm":
                st.addSpawn(GhostofvonHellmann, 120000);
                break;
            case "31523-05.htm":
                st.startQuestTimer("viwer_timer", 10000);
                break;
            case "viwer_timer":
                st.setCond(8);
                htmltext = "31523-06.htm";
                break;
            case "31530-02.htm":
                st.setCond(10);
                st.takeItems(SilverKey);
                st.giveItems(SilverSpear);
                break;
            case "i7064-02.htm":
                htmltext = "i7064-02.htm";
                break;
            case "31526-13.htm":
                st.startQuestTimer("read_book", 120000);
                break;
            case "read_book":
                htmltext = "i7064.htm";
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Innocentin) {
            if (cond == 0) {
                if (st.player.isQuestCompleted(_022_TragedyInVonHellmannForest.class))
                    htmltext = "31328-01.htm";
                else
                    htmltext = "31328-00.htm";
            } else if (cond == 1)
                htmltext = "31328-03.htm";
            else if (cond == 2)
                htmltext = "31328-07.htm";
            else if (cond == 4)
                htmltext = "31328-08.htm";
            else if (cond == 6)
                htmltext = "31328-19.htm";
        } else if (npcId == BrokenBookshelf) {
            if (cond == 2) {
                if (st.getQuestItemsCount(SilverKey) != 0)
                    htmltext = "31526-00.htm";
            } else if (cond == 3) {
                if (st.getQuestItemsCount(LidiaHairPin) == 0 && st.haveQuestItem(LidiaDiary) )
                    htmltext = "31526-12.htm";
                else if (st.haveQuestItem(LidiaHairPin)  && st.getQuestItemsCount(LidiaDiary) == 0)
                    htmltext = "31526-06.htm";
                else if (st.getQuestItemsCount(LidiaHairPin) == 0 && st.getQuestItemsCount(LidiaDiary) == 0)
                    htmltext = "31526-02.htm";
            } else if (cond == 4)
                htmltext = "31526-13.htm";
        } else if (npcId == GhostofvonHellmann) {
            if (cond == 6)
                htmltext = "31524-01.htm";
            else if (cond == 7)
                htmltext = "31524-05.htm";
        } else if (npcId == Tombstone) {
            if (cond == 6)
                if (st.isRunningQuestTimer("spawn_timer"))
                    htmltext = "31523-03.htm";
                else
                    htmltext = "31523-01.htm";
            if (cond == 7)
                htmltext = "31523-04.htm";
            else if (cond == 8)
                htmltext = "31523-06.htm";
        } else if (npcId == Violet) {
            if (cond == 8) {
                htmltext = "31386-01.htm";
                st.setCond(9);
            } else if (cond == 9)
                htmltext = "31386-02.htm";
            else if (cond == 10)
                if (st.getQuestItemsCount(SilverSpear) != 0) {
                    htmltext = "31386-03.htm";
                    st.takeItems(SilverSpear);
                    st.giveItems(ADENA_ID, 350000);
                    st.addExpAndSp(456893, 42112);
                    st.playSound(SOUND_FINISH);
                    st.finish();
                } else
                    htmltext = "You have no Silver Spear...";
        } else if (npcId == Box)
            if (cond == 9)
                if (st.getQuestItemsCount(SilverKey) != 0)
                    htmltext = "31530-01.htm";
                else
                    htmltext = "You have no key...";
            else if (cond == 10)
                htmltext = "31386-03.htm";
        return htmltext;
    }

}