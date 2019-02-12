package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _296_SilkOfTarantula extends Quest {


    private static final int TARANTULA_SPIDER_SILK = 1493;
    private static final int TARANTULA_SPINNERETTE = 1494;
    private static final int RING_OF_RACCOON = 1508;
    private static final int RING_OF_FIREFLY = 1509;

    public _296_SilkOfTarantula() {
        super(false);
        addStartNpc(30519);
        addTalkId(30548);

        addKillId(20394);
        addKillId(20403);
        addKillId(20508);

        addQuestItem(TARANTULA_SPIDER_SILK);
        addQuestItem(TARANTULA_SPINNERETTE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("trader_mion_q0296_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("quit".equalsIgnoreCase(event)) {
            htmltext = "trader_mion_q0296_06.htm";
            st.takeItems(TARANTULA_SPINNERETTE);
            st.exitCurrentQuest(true);
            st.playSound(SOUND_FINISH);
        } else if ("exchange".equalsIgnoreCase(event))
            if (st.getQuestItemsCount(TARANTULA_SPINNERETTE) >= 1) {
                htmltext = "defender_nathan_q0296_03.htm";
                st.giveItems(TARANTULA_SPIDER_SILK, 17);
                st.takeItems(TARANTULA_SPINNERETTE);
            } else
                htmltext = "defender_nathan_q0296_02.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == 30519) {
            if (cond == 0) {
                if (st.player.getLevel() >= 15) {
                    if (st.haveAnyQuestItems(RING_OF_RACCOON,RING_OF_FIREFLY) )
                        htmltext = "trader_mion_q0296_02.htm";
                    else {
                        htmltext = "trader_mion_q0296_08.htm";
                        return htmltext;
                    }
                } else {
                    htmltext = "trader_mion_q0296_01.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1)
                if (st.haveQuestItem(TARANTULA_SPIDER_SILK)) {
                    if (st.haveQuestItem(TARANTULA_SPIDER_SILK) ) {
                        htmltext = "trader_mion_q0296_05.htm";
                        st.giveItems(ADENA_ID, st.getQuestItemsCount(TARANTULA_SPIDER_SILK) * 23);
                        st.takeItems(TARANTULA_SPIDER_SILK);

                        if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q4")) {
                            st.player.setVar("p1q4", 1);
                            st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                        }
                    }
                } else htmltext = "trader_mion_q0296_04.htm";
        } else if (npcId == 30548 && cond == 1)
            htmltext = "defender_nathan_q0296_01.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1)
            if (Rnd.chance(50))
                st.rollAndGive(TARANTULA_SPINNERETTE, 1, 45);
            else
                st.rollAndGive(TARANTULA_SPIDER_SILK, 1, 45);
    }
}