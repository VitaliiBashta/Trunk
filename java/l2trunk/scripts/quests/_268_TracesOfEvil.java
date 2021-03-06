package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _268_TracesOfEvil extends Quest {
    //NPC
    private static final int KUNAI = 30559;
    //MOBS
    private static final int SPIDER = 20474;
    private static final int FANG_SPIDER = 20476;
    private static final int BLADE_SPIDER = 20478;
    //ITEMS
    private final int CONTAMINATED = 10869;

    public _268_TracesOfEvil() {
        addStartNpc(KUNAI);
        addKillId(SPIDER, FANG_SPIDER, BLADE_SPIDER);
        addQuestItem(CONTAMINATED);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("trader_kunai_q0268_03.htm".equals(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        if (st.getCond() == 0)
            if (st.player.getLevel() < 15) {
                htmltext = "trader_kunai_q0268_02.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "trader_kunai_q0268_01.htm";
        else if (st.getQuestItemsCount(CONTAMINATED) >= 30) {
            htmltext = "trader_kunai_q0268_06.htm";
            st.giveAdena(2474);
            st.addExpAndSp(8738, 409);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else
            htmltext = "trader_kunai_q0268_04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        st.giveItemIfNotHave(CONTAMINATED, 30);
        if (st.haveQuestItem(CONTAMINATED, 30)) {
            st.playSound(SOUND_MIDDLE);
            st.setCond(2);
            st.start();
        }
    }
}