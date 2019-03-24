package l2trunk.scripts.quests;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _138_TempleChampionPart2 extends Quest {
    // NPCs
    private static final int SYLVAIN = 30070;
    private static final int PUPINA = 30118;
    private static final int ANGUS = 30474;
    private static final int SLA = 30666;

    // ITEMs
    private static final int MANIFESTO = 10341;
    private static final int RELIC = 10342;
    private static final int ANGUS_REC = 10343;
    private static final int PUPINA_REC = 10344;

    // Monsters
    private final static int Wyrm = 20176;
    private final static int GuardianBasilisk = 20550;
    private final static int RoadScavenger = 20551;
    private final static int FetteredSoul = 20552;

    public _138_TempleChampionPart2() {
        // Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
        addFirstTalkId(SYLVAIN);
        addTalkId(SYLVAIN, PUPINA, ANGUS, SLA);
        addKillId(Wyrm, GuardianBasilisk, RoadScavenger, FetteredSoul);
        addQuestItem(MANIFESTO, RELIC, ANGUS_REC, PUPINA_REC);
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        if (player.isQuestCompleted(_137_TempleChampionPart1.class) && player.getQuestState(this) == null)
            newQuestState(player, STARTED);
        return "";
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("sylvain_q0138_04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
            st.giveItems(MANIFESTO);
        } else if ("sylvain_q0138_09.htm".equalsIgnoreCase(event)) {
            st.addExpAndSp(187062, 11307);
            st.giveItems(ADENA_ID, 84593);
            st.playSound(SOUND_FINISH);
            st.finish();
        } else if ("sylvain_q0138_06.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if ("pupina_q0138_08.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
        } else if ("pupina_q0138_11.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
            st.unset("talk");
            st.giveItems(PUPINA_REC);
        } else if ("grandmaster_angus_q0138_03.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
        } else if ("preacher_sla_q0138_03.htm".equalsIgnoreCase(event)) {
            st.set("talk");
            st.takeItems(PUPINA_REC);
        } else if ("preacher_sla_q0138_05.htm".equalsIgnoreCase(event)) {
            st.set("talk", 2);
            st.takeItems(MANIFESTO);
        } else if ("preacher_sla_q0138_12.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
            st.unset("talk");
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == SYLVAIN) {
            if (cond == 0) {
                if (st.player.getLevel() >= 36)
                    htmltext = "sylvain_q0138_01.htm";
                else
                    htmltext = "sylvain_q0138_03.htm";
            } else if (cond == 1)
                htmltext = "sylvain_q0138_04.htm";
            else if (cond >= 2 && cond <= 6)
                htmltext = "sylvain_q0138_06.htm";
            else if (cond == 7)
                htmltext = "sylvain_q0138_08.htm";
        } else if (npcId == PUPINA) {
            if (cond == 2)
                htmltext = "pupina_q0138_02.htm";
            else if (cond == 3 || cond == 4)
                htmltext = "pupina_q0138_09.htm";
            else if (cond == 5) {
                htmltext = "pupina_q0138_10.htm";
                st.takeItems(ANGUS_REC);
            } else if (cond == 6)
                htmltext = "pupina_q0138_13.htm";
        } else if (npcId == ANGUS) {
            if (cond == 3)
                htmltext = "grandmaster_angus_q0138_02.htm";
            else if (cond == 4) {
                if (st.getQuestItemsCount(RELIC) >= 10) {
                    htmltext = "grandmaster_angus_q0138_05.htm";
                    st.takeItems(RELIC);
                    st.giveItems(ANGUS_REC);
                    st.setCond(5);
                    st.playSound(SOUND_MIDDLE);
                } else
                    htmltext = "grandmaster_angus_q0138_04.htm";
            } else if (cond == 5)
                htmltext = "grandmaster_angus_q0138_06.htm";
        } else if (npcId == SLA)
            if (cond == 6) {
                if (!st.isSet("talk") )
                    htmltext = "preacher_sla_q0138_02.htm";
                else if (st.isSet("talk") )
                    htmltext = "preacher_sla_q0138_03.htm";
                else if (st.getInt("talk") == 2)
                    htmltext = "preacher_sla_q0138_05.htm";
            } else if (cond == 7)
                htmltext = "preacher_sla_q0138_13.htm";

        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;
        if (st.getCond() == 4)
            if (st.getQuestItemsCount(RELIC) < 10) {
                st.giveItems(RELIC);
                if (st.getQuestItemsCount(RELIC) >= 10)
                    st.playSound(SOUND_MIDDLE);
                else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}