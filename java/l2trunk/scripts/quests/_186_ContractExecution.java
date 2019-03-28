package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _186_ContractExecution extends Quest {
    private static final int Luka = 31437;
    private static final int Lorain = 30673;
    private static final int Nikola = 30621;

    private static final int Certificate = 10362;
    private static final int MetalReport = 10366;
    private static final int Accessory = 10367;

    private static final int LetoLizardman = 20577;
    private static final int LetoLizardmanArcher = 20578;
    private static final int LetoLizardmanSoldier = 20579;
    private static final int LetoLizardmanWarrior = 20580;
    private static final int LetoLizardmanShaman = 20581;
    private static final int LetoLizardmanOverlord = 20582;
    private static final int TimakOrc = 20583;

    public _186_ContractExecution() {
        addTalkId(Luka, Nikola, Lorain);
        addFirstTalkId(Lorain);
        addKillId(LetoLizardman, LetoLizardmanArcher, LetoLizardmanSoldier, LetoLizardmanWarrior, LetoLizardmanShaman, LetoLizardmanOverlord, TimakOrc);
        addQuestItem(Certificate, MetalReport, Accessory);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("researcher_lorain_q0186_03.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.takeItems(Certificate);
            st.giveItems(MetalReport);
        } else if ("maestro_nikola_q0186_03.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("blueprint_seller_luka_q0186_06.htm".equalsIgnoreCase(event)) {
            st.giveAdena( 105083);
            st.addExpAndSp(285935, 18711);
            st.finish();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (st.getState() == STARTED)
            if (npcId == Lorain) {
                if (cond == 0)
                    if (st.player.getLevel() < 41)
                        htmltext = "researcher_lorain_q0186_02.htm";
                    else
                        htmltext = "researcher_lorain_q0186_01.htm";
                else if (cond == 1)
                    htmltext = "researcher_lorain_q0186_04.htm";
            } else if (npcId == Nikola) {
                if (cond == 1)
                    htmltext = "maestro_nikola_q0186_01.htm";
                else if (cond == 2)
                    htmltext = "maestro_nikola_q0186_04.htm";
            } else if (npcId == Luka)
                if (!st.haveQuestItem(Accessory))
                    htmltext = "blueprint_seller_luka_q0186_01.htm";
                else
                    htmltext = "blueprint_seller_luka_q0186_02.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() == STARTED     && st.getCond() == 2 && Rnd.get(5) == 0) {
            st.giveItemIfNotHave(Accessory);
            st.playSound(SOUND_MIDDLE);
        }
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        if (player.isQuestCompleted(_184_NikolasCooperationContract.class) && player.getQuestState(this) == null)
            newQuestState(player, STARTED);
        return "";
    }
}