package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class _642_APowerfulPrimevalCreature extends Quest implements ScriptFile {
    // NPCs
    private static final int Dinn = 32105;
    // Mobs
    private static final int Ancient_Egg = 18344;
    private static final List<Integer> Dino = List.of(
            22196, 22197, 22198, 22199, 22200, 22201, 22202, 22203, 22204, 22205, 22218,
            22219, 22220, 22223, 22224, 22225, 22226, 22227, 22742, 22743, 22744, 22745);
    // Items
    private static final List<Integer> Rewards = List.of(
            8690, 8692, 8694, 8696, 8698, 8700, 8702, 8704, 8706, 8708, 8710);
    // Quest Items
    private static final int Dinosaur_Tissue = 8774;
    private static final int Dinosaur_Egg = 8775;
    // Chances
    private static final int Dinosaur_Tissue_Chance = 33;
    private static final int Dinosaur_Egg_Chance = 15;

    public _642_APowerfulPrimevalCreature() {
        super(true);
        addStartNpc(Dinn);
        addKillId(Ancient_Egg);
        addKillId(Dino);
        addQuestItem(Dinosaur_Tissue);
        addQuestItem(Dinosaur_Egg);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int state = st.getState();
        long Dinosaur_Tissue_Count = st.getQuestItemsCount(Dinosaur_Tissue);
        if (event.equalsIgnoreCase("dindin_q0642_04.htm") && state == CREATED) {
            st.setState(STARTED);
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("dindin_q0642_12.htm") && state == STARTED) {
            if (Dinosaur_Tissue_Count == 0)
                return "dindin_q0642_08a.htm";
            st.takeItems(Dinosaur_Tissue, -1);
            st.giveItems(ADENA_ID, Dinosaur_Tissue_Count * 3000, false);
            st.playSound(SOUND_MIDDLE);
        } else if (event.equalsIgnoreCase("0"))
            return null;
        else if (state == STARTED) {
            int rew_id = toInt(event);
            if (Dinosaur_Tissue_Count < 150 || st.getQuestItemsCount(Dinosaur_Egg) == 0)
                return "dindin_q0642_08a.htm";
            if (Rewards.stream()
                    .filter(reward -> reward == rew_id)
                    .peek(reward -> {
                        st.takeItems(Dinosaur_Tissue, 150);
                        st.takeItems(Dinosaur_Egg, 1);
                        st.giveItems(reward, 1);
                        st.giveItems(ADENA_ID, 44000);
                        st.playSound(SOUND_MIDDLE);

                    })
                    .findFirst().isPresent())
                return "dindin_q0642_12.htm";
            return null;
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (npc.getNpcId() != Dinn)
            return "noquest";
        int _state = st.getState();
        if (_state == CREATED) {
            if (st.getPlayer().getLevel() < 75) {
                st.exitCurrentQuest(true);
                return "dindin_q0642_01a.htm";
            }
            st.setCond(0);
            return "dindin_q0642_01.htm";
        }
        if (_state == STARTED) {
            long Dinosaur_Tissue_Count = st.getQuestItemsCount(Dinosaur_Tissue);
            if (Dinosaur_Tissue_Count == 0)
                return "dindin_q0642_08a.htm";
            if (Dinosaur_Tissue_Count < 150 || st.getQuestItemsCount(Dinosaur_Egg) == 0)
                return "dindin_q0642_07.htm";
            return "dindin_q0642_07a.htm";
        }

        return "noquest";
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED || st.getCond() != 1)
            return null;
        if (npc.getNpcId() == Ancient_Egg)
            st.rollAndGive(Dinosaur_Egg, 1, Dinosaur_Egg_Chance);
        else
            st.rollAndGive(Dinosaur_Tissue, 1, Dinosaur_Tissue_Chance);
        return null;
    }
}