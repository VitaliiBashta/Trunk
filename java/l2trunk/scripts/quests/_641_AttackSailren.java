package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _641_AttackSailren extends Quest {
    //NPC
    private static final int STATUE = 32109;

    //MOBS
    private static final int VEL1 = 22196;
    private static final int VEL2 = 22197;
    private static final int VEL3 = 22198;
    private static final int VEL4 = 22218;
    private static final int VEL5 = 22223;
    private static final int PTE = 22199;
    //items
    private static final int FRAGMENTS = 8782;
    private static final int GAZKH = 8784;

    public _641_AttackSailren() {
        super(true);

        addStartNpc(STATUE);

        addKillId(VEL1);
        addKillId(VEL2);
        addKillId(VEL3);
        addKillId(VEL4);
        addKillId(VEL5);
        addKillId(PTE);

        addQuestItem(FRAGMENTS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("statue_of_shilen_q0641_05.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (event.equalsIgnoreCase("statue_of_shilen_q0641_08.htm")) {
            st.playSound(SOUND_FINISH);
            st.takeItems(FRAGMENTS, -1);
            st.giveItems(GAZKH, 1);
            st.exitCurrentQuest(true);
            st.unset("cond");
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            QuestState qs = st.getPlayer().getQuestState(_126_IntheNameofEvilPart2.class);
            if (qs == null || !qs.isCompleted())
                htmltext = "statue_of_shilen_q0641_02.htm";
            else if (st.getPlayer().getLevel() >= 77)
                htmltext = "statue_of_shilen_q0641_01.htm";
            else
                st.exitCurrentQuest(true);
        } else if (cond == 1)
            htmltext = "statue_of_shilen_q0641_05.htm";
        else if (cond == 2)
            htmltext = "statue_of_shilen_q0641_07.htm";
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (st.getQuestItemsCount(FRAGMENTS) < 30) {
            st.giveItems(FRAGMENTS, 1);
            if (st.getQuestItemsCount(FRAGMENTS) == 30) {
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
                st.setState(STARTED);
            } else
                st.playSound(SOUND_ITEMGET);
        }
        return null;
    }
}