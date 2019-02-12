package l2trunk.scripts.quests;

import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.SkillList;

public final class _247_PossessorOfaPreciousSoul4 extends Quest {
    private static final int CARADINE = 31740;
    private static final int LADY_OF_LAKE = 31745;

    private static final int CARADINE_LETTER_LAST = 7679;
    private static final int NOBLESS_TIARA = 7694;

    public _247_PossessorOfaPreciousSoul4() {
        super(false);

        addStartNpc(CARADINE);

        addTalkId(LADY_OF_LAKE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int cond = st.getCond();
        if (cond == 0 && event.equals("caradine_q0247_03.htm")) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if (cond == 1) {
            if (event.equals("caradine_q0247_04.htm"))
                return htmltext;
            else if (event.equals("caradine_q0247_05.htm")) {
                st.setCond(2);
                st.takeItems(CARADINE_LETTER_LAST, 1);
                st.player.teleToLocation(143230, 44030, -3030);
                return htmltext;
            }
        } else if (cond == 2) {
            switch (event) {
                case "caradine_q0247_06.htm":
                    return htmltext;
                case "caradine_q0247_05.htm":
                    st.player.teleToLocation(143230, 44030, -3030);
                    return htmltext;
                case "lady_of_the_lake_q0247_02.htm":
                    return htmltext;
                case "lady_of_the_lake_q0247_03.htm":
                    return htmltext;
                case "lady_of_the_lake_q0247_04.htm":
                    return htmltext;
                case "lady_of_the_lake_q0247_05.htm":
                    if (st.player.getLevel() >= 75) {
                        st.giveItems(NOBLESS_TIARA, 1);
                        st.addExpAndSp(93836, 0);
                        st.playSound(SOUND_FINISH);
                        st.unset("cond");
                        st.exitCurrentQuest(false);
                        Olympiad.addNoble(st.player);
                        st.player.setNoble(true);
                        st.player.updatePledgeClass();
                        st.player.updateNobleSkills();
                        st.player.sendPacket(new SkillList(st.player));
                        st.player.broadcastUserInfo(true);
                    } else
                        htmltext = "lady_of_the_lake_q0247_06.htm";
                    break;
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        if (!st.player.isSubClassActive())
            return "Subclass only!";

        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (npcId == CARADINE) {
            QuestState previous = st.player.getQuestState(_246_PossessorOfaPreciousSoul3.class);
            if (id == CREATED && previous != null && previous.getState() == COMPLETED) {
                if (st.player.getLevel() < 75) {
                    htmltext = "caradine_q0247_02.htm";
                    st.exitCurrentQuest(true);
                } else
                    htmltext = "caradine_q0247_01.htm";
            } else if (cond == 1)
                htmltext = "caradine_q0247_03.htm";
            else if (cond == 2)
                htmltext = "caradine_q0247_06.htm";
        } else if (npcId == LADY_OF_LAKE && cond == 2) {
            if (st.player.getLevel() >= 75)
                htmltext = "lady_of_the_lake_q0247_01.htm";
            else
                htmltext = "lady_of_the_lake_q0247_06.htm";
        }
        return htmltext;
    }
}