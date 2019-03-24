package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _643_RiseAndFallOfTheElrokiTribe extends Quest {
    private static final int DROP_CHANCE = 75;
    private static final int BONES_OF_A_PLAINS_DINOSAUR = 8776;

    private static final List<Integer> PLAIN_DINOSAURS = List.of(
            22208, 22209, 22210, 22211, 22212, 22213, 22221, 22222, 22226, 22227, 22742, 22743, 22744, 22745);
    private static final List<Integer> REWARDS = List.of(
            8712, 8713, 8714, 8715, 8716, 8717, 8718, 8719, 8720, 8721, 8722);

    public _643_RiseAndFallOfTheElrokiTribe() {
        super(true);

        addStartNpc(32106);
        addTalkId(32117);

        addKillId(PLAIN_DINOSAURS);

        addQuestItem(BONES_OF_A_PLAINS_DINOSAUR);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        long count = st.getQuestItemsCount(BONES_OF_A_PLAINS_DINOSAUR);
        if ("singsing_q0643_05.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("shaman_caracawe_q0643_06.htm".equalsIgnoreCase(event)) {
            if (count >= 300) {
                st.takeItems(BONES_OF_A_PLAINS_DINOSAUR, 300);
                st.giveItems(Rnd.get(REWARDS), 5);
            } else
                htmltext = "shaman_caracawe_q0643_05.htm";
        } else if ("None".equalsIgnoreCase(event))
            htmltext = null;
        else if ("Quit".equalsIgnoreCase(event)) {
            htmltext = null;
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        if (st.getCond() == 0) {
            if (st.player.getLevel() >= 75)
                htmltext = "singsing_q0643_01.htm";
            else {
                htmltext = "singsing_q0643_04.htm";
                st.exitCurrentQuest();
            }
        } else if (st.getState() == STARTED)
            if (npcId == 32106) {
                long count = st.getQuestItemsCount(BONES_OF_A_PLAINS_DINOSAUR);
                if (count == 0)
                    htmltext = "singsing_q0643_08.htm";
                else {
                    htmltext = "singsing_q0643_08.htm";
                    st.takeItems(BONES_OF_A_PLAINS_DINOSAUR);
                    st.giveItems(ADENA_ID, count * 1374, false);
                }
            } else if (npcId == 32117)
                htmltext = "shaman_caracawe_q0643_02.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1)
            st.rollAndGive(BONES_OF_A_PLAINS_DINOSAUR, 1, DROP_CHANCE);
    }
}