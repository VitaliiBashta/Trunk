package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _266_PleaOfPixies extends Quest {
    private static final int PREDATORS_FANG = 1334;
    private static final int EMERALD = 1337;
    private static final int BLUE_ONYX = 1338;
    private static final int ONYX = 1339;
    private static final int GLASS_SHARD = 1336;
    private static final int REC_LEATHER_BOOT = 2176;
    private static final int REC_SPIRITSHOT = 3032;

    public _266_PleaOfPixies() {
        super(false);
        addStartNpc(31852);
        addKillId(20525, 20530, 20534, 20537);
        addQuestItem(PREDATORS_FANG);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("pixy_murika_q0266_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        if (st.getCond() == 0) {
            if (st.player.getRace() != Race.elf) {
                htmltext = "pixy_murika_q0266_00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() < 3) {
                htmltext = "pixy_murika_q0266_01.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "pixy_murika_q0266_02.htm";
        } else if (st.getQuestItemsCount(PREDATORS_FANG) < 100)
            htmltext = "pixy_murika_q0266_04.htm";
        else {
            st.takeItems(PREDATORS_FANG);
            int n = Rnd.get(100);
            if (n < 2) {
                st.giveItems(EMERALD);
                st.giveItems(REC_SPIRITSHOT);
                st.playSound(SOUND_JACKPOT);
            } else if (n < 20) {
                st.giveItems(BLUE_ONYX);
                st.giveItems(REC_LEATHER_BOOT);
            } else if (n < 45)
                st.giveItems(ONYX);
            else
                st.giveItems(GLASS_SHARD);
            htmltext = "pixy_murika_q0266_05.htm";
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1)
            st.rollAndGive(PREDATORS_FANG, 1, 1, 100, 60 + npc.getLevel() * 5);
    }
}