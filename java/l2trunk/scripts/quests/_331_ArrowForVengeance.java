package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.Map;

public final class _331_ArrowForVengeance extends Quest {
    private static final int HARPY_FEATHER = 1452;
    private static final int MEDUSA_VENOM = 1453;
    private static final int WYRMS_TOOTH = 1454;
    private static final Map<Integer, Integer> npcRewards = Map.of(
            20145, HARPY_FEATHER,
            20158, MEDUSA_VENOM,
            20176, WYRMS_TOOTH);

    public _331_ArrowForVengeance() {
        addStartNpc(30125);

        addKillId(npcRewards.keySet());

        addQuestItem(npcRewards.values());
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("beltkem_q0331_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("beltkem_q0331_06.htm".equalsIgnoreCase(event)) {
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 32) {
                htmltext = "beltkem_q0331_02.htm";
                return htmltext;
            }
            htmltext = "beltkem_q0331_01.htm";
            st.exitCurrentQuest();
        } else if (cond == 1)
            if (st.haveAnyQuestItems(HARPY_FEATHER,MEDUSA_VENOM,WYRMS_TOOTH)) {
                st.giveItems(ADENA_ID, 80 * st.getQuestItemsCount(HARPY_FEATHER) + 90 * st.getQuestItemsCount(MEDUSA_VENOM) + 100 * st.getQuestItemsCount(WYRMS_TOOTH), false);
                st.takeAllItems(HARPY_FEATHER,MEDUSA_VENOM,WYRMS_TOOTH);
                htmltext = "beltkem_q0331_05.htm";
            } else
                htmltext = "beltkem_q0331_04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() > 0)
            if (npcRewards.keySet().contains(npc.getNpcId()))
                st.rollAndGive(npcRewards.get(npc.getNpcId()), 1, 33);
    }
}