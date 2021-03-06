package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;

public final class _061_LawEnforcement extends Quest {
    /**
     * The one who knows everything
     * Visit Kekropus in Kamael Village to learn more about the Inspector and Judicator.
     */
    private static final int COND1 = 1;
    /**
     * Nostra's Successor
     * It is said that Nostra's successor is in Kamael Village. He is the first Inspector and master of souls. Find him.
     */
    private static final int COND2 = 2;

    private static final int Liane = 32222;
    private static final int Kekropus = 32138;
    private static final int Eindburgh = 32469;

    public _061_LawEnforcement() {
        addStartNpc(Liane);
        addTalkId(Kekropus, Eindburgh);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "ask":
                if (st.player.getRace() != Race.kamael) {
                    htmltext = "grandmaste_piane_q0061_03.htm";
                    st.exitCurrentQuest();
                } else if (st.player.getClassId() != ClassId.inspector || st.player.getLevel() < 76) {
                    htmltext = "grandmaste_piane_q0061_02.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "grandmaste_piane_q0061_04.htm";
                break;
            case "accept":
                st.start();
                st.setCond(COND1);
                st.playSound(SOUND_ACCEPT);
                htmltext = "grandmaste_piane_q0061_05.htm";
                break;
            case "kekrops_q0061_09.htm":
                st.setCond(COND2);
                break;
            case "subelder_aientburg_q0061_08.htm":
            case "subelder_aientburg_q0061_09.htm":
                st.giveAdena( 26000);
                st.player.setClassId(ClassId.judicator, false, true);
                st.player.broadcastCharInfo();
                st.player.broadcastPacket(new MagicSkillUse(st.player, 4339, 1, 6000));
                st.player.broadcastPacket(new MagicSkillUse(npc, 4339, 1, 6000));
                st.exitCurrentQuest();
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Liane) {
            if (st.getState() == CREATED)
                htmltext = "grandmaste_piane_q0061_01.htm";
            else
                htmltext = "grandmaste_piane_q0061_06.htm";
        } else if (npcId == Kekropus) {
            if (cond == COND1)
                htmltext = "kekrops_q0061_01.htm";
            else if (cond == COND2)
                htmltext = "kekrops_q0061_10.htm";
        } else if (npcId == Eindburgh && cond == COND2)
            htmltext = "subelder_aientburg_q0061_01.htm";
        return htmltext;
    }

}