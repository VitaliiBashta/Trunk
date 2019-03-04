package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.instancemanager.HellboundManager;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _131_BirdInACage extends Quest {
    // NPC's
    private static final int KANIS = 32264;
    private static final int PARME = 32271;
    // ITEMS
    private static final int KANIS_ECHO_CRY = 9783;
    private static final int PARMES_LETTER = 9784;

    public _131_BirdInACage() {
        super(false);

        addStartNpc(KANIS);
        addTalkId(PARME);

        addQuestItem(KANIS_ECHO_CRY,PARMES_LETTER);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();

        if (event.equals("priest_kanis_q0131_04.htm") && cond == 0) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if (event.equals("priest_kanis_q0131_12.htm") && cond == 1) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
            st.giveItems(KANIS_ECHO_CRY, 1);
        } else if (event.equals("parme_131y_q0131_04.htm") && cond == 2) {
            st.setCond(3);
            st.giveItems(PARMES_LETTER, 1);
            st.playSound(SOUND_MIDDLE);
            st.player.teleToLocation(143472 + Rnd.get(-100, 100), 191040 + Rnd.get(-100, 100), -3696);
        } else if (event.equals("priest_kanis_q0131_17.htm") && cond == 3) {
            st.playSound(SOUND_MIDDLE);
            st.takeItems(PARMES_LETTER, -1);
        } else if (event.equals("priest_kanis_q0131_19.htm") && cond == 3) {
            st.playSound(SOUND_FINISH);
            st.takeItems(KANIS_ECHO_CRY, -1);
            st.addExpAndSp(250677, 25019);
            st.finish();
            if (HellboundManager.getHellboundLevel() == 0)
                ServerVariables.set("HellboundConfidence");
        } else if (event.equals("meet") && cond == 2)
            st.player.teleToLocation(153736, 142056, -9744);

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == KANIS) {
            if (cond == 0)
                if (st.player.getLevel() >= 78)
                    htmltext = "priest_kanis_q0131_01.htm";
                else {
                    htmltext = "priest_kanis_q0131_02.htm";
                    st.exitCurrentQuest();
                }
            else if (cond == 1)
                htmltext = "priest_kanis_q0131_05.htm";
            else if (cond == 2)
                htmltext = "priest_kanis_q0131_13.htm";
            else if (cond == 3)
                if (st.getQuestItemsCount(PARMES_LETTER) > 0)
                    htmltext = "priest_kanis_q0131_16.htm";
                else
                    htmltext = "priest_kanis_q0131_17.htm";
        } else if (npcId == PARME && cond == 2)
            htmltext = "parme_131y_q0131_02.htm";

        return htmltext;
    }
}