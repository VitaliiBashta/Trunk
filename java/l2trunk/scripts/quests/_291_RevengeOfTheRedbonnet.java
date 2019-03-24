package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _291_RevengeOfTheRedbonnet extends Quest {
    //NPC
    private static final int MaryseRedbonnet = 30553;
    //Quest items
    private final int BlackWolfPelt = 1482;
    //Item
    private static final int ScrollOfEscape = 736;
    private static final int GrandmasPearl = 1502;
    private static final int GrandmasMirror = 1503;
    private static final int GrandmasNecklace = 1504;
    private static final int GrandmasHairpin = 1505;
    //Mobs
    private static final int BlackWolf = 20317;

    public _291_RevengeOfTheRedbonnet() {
        addStartNpc(MaryseRedbonnet);

        addKillId(BlackWolf);

        addQuestItem(BlackWolfPelt);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("marife_redbonnet_q0291_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();

        if (cond == 0) {
            if (st.player.getLevel() < 4) {
                htmltext = "marife_redbonnet_q0291_01.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "marife_redbonnet_q0291_02.htm";
        } else if (cond == 1)
            htmltext = "marife_redbonnet_q0291_04.htm";
        else if (cond == 2 && st.getQuestItemsCount(BlackWolfPelt) < 40) {
            htmltext = "marife_redbonnet_q0291_04.htm";
            st.setCond(1);
        } else if (cond == 2 && st.haveQuestItem(BlackWolfPelt, 40)) {
            int random = Rnd.get(100);
            st.takeItems(BlackWolfPelt);
            if (random < 3)
                st.giveItems(GrandmasPearl);
            else if (random < 21)
                st.giveItems(GrandmasMirror);
            else if (random < 46)
                st.giveItems(GrandmasNecklace);
            else {
                st.giveItems(ScrollOfEscape);
                st.giveItems(GrandmasHairpin);
            }
            htmltext = "marife_redbonnet_q0291_05.htm";
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.getQuestItemsCount(BlackWolfPelt) < 40) {
            st.giveItems(BlackWolfPelt);
            if (st.getQuestItemsCount(BlackWolfPelt) < 40)
                st.playSound(SOUND_ITEMGET);
            else {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
                st.start();
            }
        }
    }
}
