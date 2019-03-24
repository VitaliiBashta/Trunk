package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _269_InventionAmbition extends Quest {
    //NPC
    private static final int INVENTOR_MARU = 32486;
    //MOBS
    private static final List<Integer> MOBS = List.of(
            21124,            // Red Eye Barbed Bat
            21125,            // Northern Trimden
            21126,            // Kerope Werewolf
            21127,            // Northern Goblin
            21128,            // Spine Golem
            21129,            // Kerope Werewolf Chief
            21130,            // Northern Goblin Leader
            21131);            // Enchanted Spine Golem
    //ITEMS
    private final int ENERGY_ORES = 10866;

    public _269_InventionAmbition() {
        addStartNpc(INVENTOR_MARU);
        addKillId(MOBS);
        addQuestItem(ENERGY_ORES);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("inventor_maru_q0269_04.htm".equals(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("inventor_maru_q0269_07.htm".equals(event)) {
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        long count = st.getQuestItemsCount(ENERGY_ORES);
        if (st.getState() == CREATED)
            if (st.player.getLevel() < 18) {
                htmltext = "inventor_maru_q0269_02.htm";
                st.exitCurrentQuest();
            } else
                htmltext = "inventor_maru_q0269_01.htm";
        else if (count > 0) {
            st.giveAdena(count * 50 + 2044 * (count / 20));
            st.takeItems(ENERGY_ORES);
            htmltext = "inventor_maru_q0269_06.htm";
        } else
            htmltext = "inventor_maru_q0269_05.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() == STARTED && Rnd.chance(60)) {
            st.giveItems(ENERGY_ORES);
            st.playSound(SOUND_ITEMGET);
        }
    }
}