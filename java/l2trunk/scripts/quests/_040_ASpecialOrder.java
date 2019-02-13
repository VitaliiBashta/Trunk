package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _040_ASpecialOrder extends Quest {
    // NPC
    private static final int Helvetia = 30081;
    private static final int OFulle = 31572;
    private static final int Gesto = 30511;

    // Items
    private static final int FatOrangeFish = 6452;
    private static final int NimbleOrangeFish = 6450;
    private static final int OrangeUglyFish = 6451;
    private static final int GoldenCobol = 5079;
    private static final int ThornCobol = 5082;
    private static final int GreatCobol = 5084;

    // Quest items
    private static final int FishChest = 12764;
    private static final int SeedJar = 12765;
    private static final int WondrousCubic = 10632;

    public _040_ASpecialOrder() {
        super(false);
        addStartNpc(Helvetia);

        addQuestItem(FishChest);
        addQuestItem(SeedJar);

        addTalkId(OFulle);
        addTalkId(Gesto);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("take")) {
            int rand = Rnd.get(1, 2);
            if (rand == 1) {
                st.setCond(2);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                htmltext = "Helvetia-gave-ofulle.htm";
            } else {
                st.setCond(5);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                htmltext = "Helvetia-gave-gesto.htm";
            }
        } else if (event.equals("6")) {
            st.setCond(6);
            htmltext = "Gesto-3.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == Helvetia) {
            if (cond == 0)
                if (st.player.getLevel() >= 40)
                    htmltext = "Helvetia-1.htm";
                else {
                    htmltext = "Helvetia-occupation.htm";
                    st.exitCurrentQuest(true);
                }
            else if (cond == 2 || cond == 3 || cond == 5 || cond == 6)
                htmltext = "Helvetia-whereismyfish.htm";
            else if (cond == 4) {
                st.takeItems(FishChest);
                st.giveItems(WondrousCubic);
                st.exitCurrentQuest(false);
                htmltext = "Helvetia-finish.htm";
            } else if (cond == 7) {
                st.takeItems(SeedJar);
                st.giveItems(WondrousCubic);
                st.exitCurrentQuest(false);
                htmltext = "Helvetia-finish.htm";
            }
        } else if (npcId == OFulle) {
            if (cond == 2) {
                htmltext = "OFulle-1.htm";
                st.setCond(3);
            } else if (cond == 3) {
                if (st.getQuestItemsCount(FatOrangeFish) >= 10 && st.getQuestItemsCount(NimbleOrangeFish) >= 10 && st.getQuestItemsCount(OrangeUglyFish) >= 10) {
                    st.takeItems(FatOrangeFish, 10);
                    st.takeItems(NimbleOrangeFish, 10);
                    st.takeItems(OrangeUglyFish, 10);
                    st.giveItems(FishChest);
                    st.setCond(4);
                    htmltext = "OFulle-2.htm";
                } else
                    htmltext = "OFulle-1.htm";
            } else if (cond == 5 || cond == 6)
                htmltext = "OFulle-3.htm";
        } else if (npcId == Gesto)
            if (cond == 5)
                htmltext = "Gesto-1.htm";
            else if (cond == 6) {
                if (st.getQuestItemsCount(GoldenCobol) >= 40 && st.getQuestItemsCount(ThornCobol) >= 40 && st.getQuestItemsCount(GreatCobol) >= 40) {
                    st.takeItems(GoldenCobol, 40);
                    st.takeItems(ThornCobol, 40);
                    st.takeItems(GreatCobol, 40);
                    st.giveItems(SeedJar);
                    st.setCond(7);
                    htmltext = "Gesto-4.htm";
                } else
                    htmltext = "Gesto-5.htm";
            } else if (cond == 7)
                htmltext = "Gesto-6.htm";
        return htmltext;
    }
}