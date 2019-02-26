package l2trunk.scripts.quests;

import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.RadarControl;
import l2trunk.gameserver.utils.Location;

public final class _307_ControlDeviceoftheGiants extends Quest {
    private static final int Droph = 32711;

    private static final int HekatonPrime = 25687;

    private static final int DrophsSupportItems = 14850;
    private static final int CaveExplorationText1Sheet = 14851;
    private static final int CaveExplorationText2Sheet = 14852;
    private static final int CaveExplorationText3Sheet = 14853;

    private static final long HekatonPrimeRespawn = 12 * 3600 * 1000L;

    private static final Location GorgolosLoc = Location.of(186096, 61501, -4075, 0);
    private static final Location LastTitanUtenusLoc = Location.of(186730, 56456, -4555, 0);
    private static final Location GiantMarpanakLoc = Location.of(194057, 53722, -4259, 0);
    private static final Location HekatonPrimeLoc = Location.of(192328, 56120, -7651, 0);

    public _307_ControlDeviceoftheGiants() {
        super(true);
        addStartNpc(Droph);
        addTalkId(Droph);
        addKillId(HekatonPrime);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("droph_q307_2.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
        } else if ("loc1".equalsIgnoreCase(event)) {
            htmltext = "droph_q307_2a_1.htm";
            RadarControl rc = new RadarControl(0, 1, GorgolosLoc);
            st.player.sendPacket(rc);
        } else if ("loc2".equalsIgnoreCase(event)) {
            htmltext = "droph_q307_2a_2.htm";
            RadarControl rc = new RadarControl(0, 1, LastTitanUtenusLoc);
            st.player.sendPacket(rc);
        } else if ("loc3".equalsIgnoreCase(event)) {
            htmltext = "droph_q307_2a_3.htm";
            RadarControl rc = new RadarControl(0, 1, GiantMarpanakLoc);
            st.player.sendPacket(rc);
        } else if ("summon_rb".equalsIgnoreCase(event)) {
            if (ServerVariables.getLong("HekatonPrimeRespawn", 0) < System.currentTimeMillis() && st.getQuestItemsCount(CaveExplorationText1Sheet) >= 1 && st.getQuestItemsCount(CaveExplorationText2Sheet) >= 1 && st.getQuestItemsCount(CaveExplorationText3Sheet) >= 1) {
                st.takeItems(CaveExplorationText1Sheet, 1);
                st.takeItems(CaveExplorationText2Sheet, 1);
                st.takeItems(CaveExplorationText3Sheet, 1);
                ServerVariables.set("HekatonPrimeRespawn", System.currentTimeMillis() + HekatonPrimeRespawn);
                NpcInstance boss = st.addSpawn(HekatonPrime, HekatonPrimeLoc);
                boss.getMinionList().spawnMinions();
                htmltext = "droph_q307_3a.htm";
            } else
                htmltext = "droph_q307_2b.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();

        if (npcId == Droph)
            if (cond == 0) {
                if (st.player.getLevel() >= 79)
                    htmltext = "droph_q307_1.htm";
                else {
                    htmltext = "droph_q307_0.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                if (st.getQuestItemsCount(CaveExplorationText1Sheet) >= 1 && st.getQuestItemsCount(CaveExplorationText2Sheet) >= 1 && st.getQuestItemsCount(CaveExplorationText3Sheet) >= 1)
                    if (ServerVariables.getLong("HekatonPrimeRespawn", 0) < System.currentTimeMillis())
                        htmltext = "droph_q307_3.htm";
                    else
                        htmltext = "droph_q307_4.htm";
                else
                    htmltext = "droph_q307_2a.htm";
            } else if (cond == 2) {
                htmltext = "droph_q307_5.htm";
                st.giveItems(DrophsSupportItems);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == HekatonPrime)
            st.setCond(2);
    }

}