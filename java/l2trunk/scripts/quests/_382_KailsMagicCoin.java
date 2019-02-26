package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class _382_KailsMagicCoin extends Quest {
    //Quest items
    private static final int ROYAL_MEMBERSHIP = 5898;
    //NPCs
    private static final int VERGARA = 30687;
    //MOBs and CHANCES
    private static final Map<Integer, List<Integer>> MOBS = new HashMap<>();

    static {
        MOBS.put(21017, List.of(5961)); // Fallen Orc
        MOBS.put(21019, List.of(5962)); // Fallen Orc Archer
        MOBS.put(21020, List.of(5963)); // Fallen Orc Shaman
        MOBS.put(21022, List.of(5961, 5962, 5963)); // Fallen Orc Captain
        MOBS.put(21258, List.of( 5961, 5962, 5963 )); // Fallen Orc Shaman - WereTiger
        MOBS.put(21259, List.of( 5961, 5962, 5963 )); // Fallen Orc Shaman - WereTiger, transformed
    }

    public _382_KailsMagicCoin() {
        super(false);
        addStartNpc(VERGARA);
        addKillId(MOBS.keySet());
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("head_blacksmith_vergara_q0382_03.htm".equalsIgnoreCase(event))
            if (st.player.getLevel() >= 55 && st.getQuestItemsCount(ROYAL_MEMBERSHIP) > 0) {
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
            } else {
                htmltext = "head_blacksmith_vergara_q0382_01.htm";
                st.exitCurrentQuest();
            }
        else if ("list".equalsIgnoreCase(event)) {
            MultiSellHolder.INSTANCE.SeparateAndSend(382, st.player, 0);
            htmltext = null;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        if (st.getQuestItemsCount(ROYAL_MEMBERSHIP) == 0 || st.player.getLevel() < 55) {
            htmltext = "head_blacksmith_vergara_q0382_01.htm";
            st.exitCurrentQuest();
        } else if (cond == 0)
            htmltext = "head_blacksmith_vergara_q0382_02.htm";
        else
            htmltext = "head_blacksmith_vergara_q0382_04.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED || st.getQuestItemsCount(ROYAL_MEMBERSHIP) == 0)
            return;

        List<Integer> droplist = MOBS.get(npc.getNpcId());
        st.rollAndGive(Rnd.get(droplist), 1, 10);
    }
}