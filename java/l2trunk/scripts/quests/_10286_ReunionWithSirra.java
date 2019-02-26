package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import static l2trunk.scripts.quests._10283_RequestOfIceMerchant.JINIA;
import static l2trunk.scripts.quests._10283_RequestOfIceMerchant.RAFFORTY;
import static l2trunk.scripts.quests._10285_MeetingSirra.JINIA_2;
import static l2trunk.scripts.quests._10285_MeetingSirra.SIRRA;


public final class _10286_ReunionWithSirra extends Quest {

    public _10286_ReunionWithSirra() {
        super(false);
        addStartNpc(RAFFORTY);
        addTalkId(JINIA, JINIA_2, SIRRA);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("rafforty_q10286_02.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("enterinstance".equalsIgnoreCase(event)) {
            st.setCond(2);
            _10287_StoryOfThoseLeft.enterInstance(st.player);
            return null;
        } else if ("sirraspawn".equalsIgnoreCase(event)) {
            st.setCond(3);
            NpcInstance sirra = st.player.getReflection().addSpawnWithoutRespawn(SIRRA, Location.of(-23848, -8744, -5413, 49152));
            Functions.npcSay(sirra, "You are so enthusiastic in the road and that's all you do? Ha ha ha ...");
            return null;
        } else if ("sirra_q10286_04.htm".equalsIgnoreCase(event)) {
            st.giveItems(15470, 5);
            st.setCond(4);
            npc.deleteMe();
        } else if ("leaveinstance".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.player.getReflection().collapse();
            return null;
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == RAFFORTY) {
            if (cond == 0) {
                if (st.player.getLevel() >= 82 && st.player.isQuestCompleted(_10285_MeetingSirra.class))
                    htmltext = "rafforty_q10286_01.htm";
                else {
                    htmltext = "rafforty_q10286_00.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 || cond == 2 || cond == 3 || cond == 4)
                htmltext = "rafforty_q10286_03.htm";
        } else if (npcId == JINIA) {
            if (cond == 2)
                htmltext = "jinia_q10286_01.htm";
            else if (cond == 3)
                htmltext = "jinia_q10286_01a.htm";
            else if (cond == 4)
                htmltext = "jinia_q10286_05.htm";
        } else if (npcId == SIRRA) {
            if (cond == 3)
                htmltext = "sirra_q10286_01.htm";
        } else if (npcId == JINIA_2) {
            if (cond == 5)
                htmltext = "jinia2_q10286_01.htm";
            else if (cond == 6)
                htmltext = "jinia2_q10286_04.htm";
            else if (cond == 7) {
                htmltext = "jinia2_q10286_05.htm";
                st.addExpAndSp(2152200, 181070);
                st.complete();
                st.finish();
            }
        }
        return htmltext;
    }

}