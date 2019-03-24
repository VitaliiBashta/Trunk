package l2trunk.scripts.quests;


import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _451_LuciensAltar extends Quest {
    private static final int DAICHIR = 30537;
    private static final int REPLENISHED_BEAD = 14877;
    private static final int DISCHARGED_BEAD = 14878;
    private static final int ALTAR_1 = 32706;
    private static final int ALTAR_2 = 32707;
    private static final int ALTAR_3 = 32708;
    private static final int ALTAR_4 = 32709;
    private static final int ALTAR_5 = 32710;
    private static final List<Integer> ALTARS = List.of(ALTAR_1, ALTAR_2, ALTAR_3, ALTAR_4, ALTAR_5);

    public _451_LuciensAltar() {
        addStartNpc(DAICHIR);
        addTalkId(ALTARS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30537-03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.giveItems(REPLENISHED_BEAD, 5);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        Player player = st.player;

        if (npcId == DAICHIR) {
            if (cond == 0) {
                if (player.getLevel() < 80) {
                    htmltext = "30537-00.htm";
                    st.exitCurrentQuest();
                } else if (!canEnter(player)) {
                    htmltext = "30537-06.htm";
                    st.exitCurrentQuest();
                } else
                    htmltext = "30537-01.htm";
            } else if (cond == 1)
                htmltext = "30537-04.htm";
            else if (cond == 2) {
                htmltext = "30537-05.htm";
                st.giveAdena( 127690);
                st.takeItems(DISCHARGED_BEAD);
                st.exitCurrentQuest();
                st.playSound(SOUND_FINISH);
                st.player.setVar(name, System.currentTimeMillis());
            }
        } else if (cond == 1 && ALTARS.contains(npcId))
            if (npcId == ALTAR_1 && !st.isSet("Altar1") ) {
                htmltext = "recharge.htm";
                onAltarCheck(st);
                st.set("Altar1");
            } else if (npcId == ALTAR_2 && !st.isSet("Altar2") ) {
                htmltext = "recharge.htm";
                onAltarCheck(st);
                st.set("Altar2");
            } else if (npcId == ALTAR_3 && !st.isSet("Altar3") ) {
                htmltext = "recharge.htm";
                onAltarCheck(st);
                st.set("Altar3");
            } else if (npcId == ALTAR_4 && !st.isSet("Altar4") ) {
                htmltext = "recharge.htm";
                onAltarCheck(st);
                st.set("Altar4");
            } else if (npcId == ALTAR_5 && !st.isSet("Altar5") ) {
                htmltext = "recharge.htm";
                onAltarCheck(st);
                st.set("Altar5");
            } else
                htmltext = "findother.htm";
        return htmltext;
    }

    private void onAltarCheck(QuestState st) {
        st.takeItems(REPLENISHED_BEAD, 1);
        st.giveItems(DISCHARGED_BEAD);
        st.playSound(SOUND_ITEMGET);
        if (st.haveQuestItem(DISCHARGED_BEAD, 5)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        }
    }

    private boolean canEnter(Player player) {
        if (player.isGM())
            return true;
        return !player.isVarSet(name)  || player.getVarLong(name) - System.currentTimeMillis() > 24 * 60 * 60 * 1000;
    }
}