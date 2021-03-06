package l2trunk.scripts.quests;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

public final class _114_ResurrectionOfAnOldManager extends Quest {
    // NPC
    private static final int NEWYEAR = 31961;
    private static final int YUMI = 32041;
    private static final int STONES = 32046;
    private static final int WENDY = 32047;
    private static final int BOX = 32050;

    // MOBS
    private static final int GUARDIAN = 27318;

    // QUEST ITEMS
    private static final int DETECTOR = 8090;
    private static final int DETECTOR2 = 8091;
    private static final int STARSTONE = 8287;
    private static final int LETTER = 8288;
    private static final int STARSTONE2 = 8289;

    private NpcInstance GUARDIAN_SPAWN = null;

    public _114_ResurrectionOfAnOldManager() {
        addStartNpc(YUMI);

        addTalkId(WENDY,BOX,STONES,NEWYEAR);
        addFirstTalkId(STONES);

        addKillId(GUARDIAN);

        addQuestItem(DETECTOR,DETECTOR2,STARSTONE,LETTER,STARSTONE2);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int choice;

        if ("head_blacksmith_newyear_q0114_02.htm".equalsIgnoreCase(event)) {
            st.setCond(22);
            st.takeItems(LETTER, 1);
            st.giveItems(STARSTONE2);
            st.playSound(SOUND_MIDDLE);
        }
        if ("collecter_yumi_q0114_04.htm".equalsIgnoreCase(event)) {
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.setCond(1);
            st.set("talk", 0);
        } else if ("collecter_yumi_q0114_08.htm".equalsIgnoreCase(event))
            st.set("talk");
        else if ("collecter_yumi_q0114_09.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
            st.unset("talk");
        } else if ("collecter_yumi_q0114_12.htm".equalsIgnoreCase(event)) {
            choice = st.getInt("choice");
            if (choice == 1)
                htmltext = "collecter_yumi_q0114_12.htm";
            else if (choice == 2)
                htmltext = "collecter_yumi_q0114_13.htm";
            else if (choice == 3)
                htmltext = "collecter_yumi_q0114_14.htm";
        } else if ("collecter_yumi_q0114_15.htm".equalsIgnoreCase(event))
            st.set("talk");
        else if ("collecter_yumi_q0114_23.htm".equalsIgnoreCase(event))
            st.set("talk", 2);
        else if ("collecter_yumi_q0114_26.htm".equalsIgnoreCase(event)) {
            st.setCond(6);
            st.playSound(SOUND_MIDDLE);
            st.unset("talk");
        } else if ("collecter_yumi_q0114_31.htm".equalsIgnoreCase(event)) {
            st.setCond(17);
            st.playSound(SOUND_MIDDLE);
            st.giveItems(DETECTOR);
        } else if ("collecter_yumi_q0114_34.htm".equalsIgnoreCase(event)) {
            st.takeItems(DETECTOR2);
            st.set("talk");
        } else if ("collecter_yumi_q0114_38.htm".equalsIgnoreCase(event)) {
            choice = st.getInt("choice");
            if (choice > 1)
                htmltext = "collecter_yumi_q0114_37.htm";
        } else if ("collecter_yumi_q0114_40.htm".equalsIgnoreCase(event)) {
            st.setCond(21);
            st.giveItems(LETTER);
            st.playSound(SOUND_MIDDLE);
        } else if ("collecter_yumi_q0114_39.htm".equalsIgnoreCase(event)) {
            st.setCond(20);
            st.playSound(SOUND_MIDDLE);
        } else if ("pavel_atlanta_q0114_03.htm".equalsIgnoreCase(event)) {
            st.setCond(19);
            st.playSound(SOUND_MIDDLE);
        } else if ("pavel_atlanta_q0114_07.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.addExpAndSp(1846611, 144270);
            st.finish();
        } else if ("chaos_secretary_wendy_q0114_01.htm".equalsIgnoreCase(event)) {
            if (st.isSet("talk") && st.isSet("talk1"))
                htmltext = "chaos_secretary_wendy_q0114_05.htm";
            else if (st.getInt("talk") + st.getInt("talk1") + st.getInt("talk2") == 6)
                htmltext = "chaos_secretary_wendy_q0114_06a.htm";
        } else if ("chaos_secretary_wendy_q0114_02.htm".equalsIgnoreCase(event)) {
            if (!st.isSet("talk") )
                st.set("talk");
        } else if ("chaos_secretary_wendy_q0114_03.htm".equalsIgnoreCase(event)) {
            if (!st.isSet("talk1"))
                st.set("talk1");
        } else if ("chaos_secretary_wendy_q0114_06.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.playSound(SOUND_MIDDLE);
            st.unset("talk");
            st.set("choice");
            st.unset("talk1");
        } else if ("chaos_secretary_wendy_q0114_07.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.playSound(SOUND_MIDDLE);
            st.unset("talk");
            st.set("choice", 2);
            st.unset("talk1");
        } else if ("chaos_secretary_wendy_q0114_09.htm".equalsIgnoreCase(event)) {
            st.setCond(5);
            st.playSound(SOUND_MIDDLE);
            st.unset("talk");
            st.set("choice", 3);
            st.unset("talk1");
        } else if ("chaos_secretary_wendy_q0114_14ab.htm".equalsIgnoreCase(event)) {
            st.setCond(7);
            st.playSound(SOUND_MIDDLE);
        } else if ("chaos_secretary_wendy_q0114_14b.htm".equalsIgnoreCase(event)) {
            st.setCond(10);
            st.playSound(SOUND_MIDDLE);
        } else if ("chaos_secretary_wendy_q0114_12c.htm".equalsIgnoreCase(event)) {
            if (!st.isSet("talk"))
                st.set("talk");
        } else if ("chaos_secretary_wendy_q0114_15b.htm".equalsIgnoreCase(event)) {
            if (GUARDIAN_SPAWN == null || !st.player.knowsObject(GUARDIAN_SPAWN) || !GUARDIAN_SPAWN.isVisible()) {
                GUARDIAN_SPAWN = st.addSpawn(GUARDIAN, Location.of(96977, -110625, -3280),0, 900000);
                Functions.npcSay(GUARDIAN_SPAWN, "You, " + st.player.getName() + ", you attacked Wendy. Prepare to die!");
                GUARDIAN_SPAWN.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, st.player, 999);
            } else
                htmltext = "chaos_secretary_wendy_q0114_17b.htm";
        } else if ("chaos_secretary_wendy_q0114_20b.htm".equalsIgnoreCase(event)) {
            st.setCond(12);
            st.playSound(SOUND_MIDDLE);
        } else if ("chaos_secretary_wendy_q0114_17c.htm".equalsIgnoreCase(event))
            st.set("talk", 2);
        else if ("chaos_secretary_wendy_q0114_20c.htm".equalsIgnoreCase(event)) {
            st.setCond(13);
            st.playSound(SOUND_MIDDLE);
            st.unset("talk");
        } else if ("chaos_secretary_wendy_q0114_23c.htm".equalsIgnoreCase(event)) {
            st.setCond(15);
            st.playSound(SOUND_MIDDLE);
            st.takeItems(STARSTONE, 1);
        } else if ("chaos_secretary_wendy_q0114_16a.htm".equalsIgnoreCase(event))
            st.set("talk", 2);
        else if ("chaos_secretary_wendy_q0114_20a.htm".equalsIgnoreCase(event)) {
            if (st.getCond() == 7) {
                st.setCond(8);
                st.unset("talk");
                st.playSound(SOUND_MIDDLE);
            } else if (st.getCond() == 8) {
                st.setCond(9);
                st.playSound(SOUND_MIDDLE);
                htmltext = "chaos_secretary_wendy_q0114_21a.htm";
            }
        } else if ("chaos_secretary_wendy_q0114_21a.htm".equalsIgnoreCase(event)) {
            st.setCond(9);
            st.playSound(SOUND_MIDDLE);
        } else if ("chaos_secretary_wendy_q0114_29c.htm".equalsIgnoreCase(event)) {
            st.giveItems(STARSTONE2);
            st.takeItems(ADENA_ID, 3000);
            st.setCond(26);
            st.playSound(SOUND_MIDDLE);
        } else if ("chaos_box2_q0114_01r.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_ARMOR_WOOD_3);
            st.set("talk");
        } else if ("chaos_box2_q0114_03.htm".equalsIgnoreCase(event)) {
            st.setCond(14);
            st.giveItems(STARSTONE);
            st.playSound(SOUND_MIDDLE);
            st.unset("talk");
        }
        return htmltext;
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        QuestState st = player.getQuestState(this);
        if (st == null || st.isCompleted())
            return "";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == STONES && cond == 17) {
            st.playSound(SOUND_MIDDLE);
            st.takeItems(DETECTOR, 1);
            st.giveItems(DETECTOR2);
            st.setCond(18);
            player.sendPacket(new ExShowScreenMessage(NpcString.THE_RADIO_SIGNAL_DETECTOR_IS_RESPONDING_A_SUSPICIOUS_PILE_OF_STONES_CATCHES_YOUR_EYE));
        }
        return "";
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        int talk = st.getInt("talk");
        int talk1 = st.getInt("talk1");
        if (npcId == YUMI) {
            if (id == CREATED) {
                QuestState Pavel = st.player.getQuestState(_121_PavelTheGiants.class);
                if (Pavel == null)
                    return "collecter_yumi_q0114_01.htm";
                if (st.player.getLevel() >= 70 && Pavel.getState() == COMPLETED)
                    htmltext = "collecter_yumi_q0114_02.htm";
                else {
                    htmltext = "collecter_yumi_q0114_01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1) {
                if (talk == 0)
                    htmltext = "collecter_yumi_q0114_04.htm";
                else
                    htmltext = "collecter_yumi_q0114_08.htm";
            } else if (cond == 2)
                htmltext = "collecter_yumi_q0114_10.htm";
            else if (cond == 3 || cond == 4 || cond == 5) {
                if (talk == 0)
                    htmltext = "collecter_yumi_q0114_11.htm";
                else if (talk == 1)
                    htmltext = "collecter_yumi_q0114_15.htm";
                else
                    htmltext = "collecter_yumi_q0114_23.htm";
            } else if (cond == 6)
                htmltext = "collecter_yumi_q0114_27.htm";
            else if (cond == 9 || cond == 12 || cond == 16)
                htmltext = "collecter_yumi_q0114_28.htm";
            else if (cond == 17)
                htmltext = "collecter_yumi_q0114_32.htm";
            else if (cond == 19) {
                if (talk == 0)
                    htmltext = "collecter_yumi_q0114_33.htm";
                else
                    htmltext = "collecter_yumi_q0114_34.htm";
            } else if (cond == 20)
                htmltext = "collecter_yumi_q0114_39.htm";
            else if (cond == 21)
                htmltext = "collecter_yumi_q0114_40z.htm";
            else if (cond == 22 || cond == 26) {
                htmltext = "collecter_yumi_q0114_41.htm";
                st.setCond(27);
                st.playSound(SOUND_MIDDLE);
            } else if (cond == 27)
                htmltext = "collecter_yumi_q0114_42.htm";
        } else if (npcId == WENDY) {
            if (cond == 2) {
                if (talk + talk1 < 2)
                    htmltext = "chaos_secretary_wendy_q0114_01.htm";
                else if (talk + talk1 == 2)
                    htmltext = "chaos_secretary_wendy_q0114_05.htm";
            } else if (cond == 3)
                htmltext = "chaos_secretary_wendy_q0114_06b.htm";
            else if (cond == 4 || cond == 5)
                htmltext = "chaos_secretary_wendy_q0114_08.htm";
            else if (cond == 6) {
                int choice = st.getInt("choice");
                if (choice == 1) {
                    if (talk == 0)
                        htmltext = "chaos_secretary_wendy_q0114_11a.htm";
                    else if (talk == 1)
                        htmltext = "chaos_secretary_wendy_q0114_17c.htm";
                    else
                        htmltext = "chaos_secretary_wendy_q0114_16a.htm";
                } else if (choice == 2)
                    htmltext = "chaos_secretary_wendy_q0114_11b.htm";
                else if (choice == 3)
                    if (talk == 0)
                        htmltext = "chaos_secretary_wendy_q0114_11c.htm";
                    else if (talk == 1)
                        htmltext = "chaos_secretary_wendy_q0114_12c.htm";
                    else
                        htmltext = "chaos_secretary_wendy_q0114_17c.htm";
            } else if (cond == 7) {
                if (talk == 0)
                    htmltext = "chaos_secretary_wendy_q0114_11c.htm";
                else if (talk == 1)
                    htmltext = "chaos_secretary_wendy_q0114_12c.htm";
                else
                    htmltext = "chaos_secretary_wendy_q0114_17c.htm";
            } else if (cond == 8)
                htmltext = "chaos_secretary_wendy_q0114_16a.htm";
            else if (cond == 9)
                htmltext = "chaos_secretary_wendy_q0114_25c.htm";
            else if (cond == 10)
                htmltext = "chaos_secretary_wendy_q0114_18b.htm";
            else if (cond == 11)
                htmltext = "chaos_secretary_wendy_q0114_19b.htm";
            else if (cond == 12)
                htmltext = "chaos_secretary_wendy_q0114_25c.htm";
            else if (cond == 13)
                htmltext = "chaos_secretary_wendy_q0114_20c.htm";
            else if (cond == 14)
                htmltext = "chaos_secretary_wendy_q0114_22c.htm";
            else if (cond == 15) {
                htmltext = "chaos_secretary_wendy_q0114_24c.htm";
                st.setCond(16);
                st.playSound(SOUND_MIDDLE);
            } else if (cond == 16)
                htmltext = "chaos_secretary_wendy_q0114_25c.htm";
            else if (cond == 20)
                htmltext = "chaos_secretary_wendy_q0114_26c.htm";
            else if (cond == 26)
                htmltext = "chaos_secretary_wendy_q0114_32c.htm";
        } else if (npcId == BOX) {
            if (cond == 13) {
                if (talk == 0)
                    htmltext = "chaos_box2_q0114_01.htm";
                else
                    htmltext = "chaos_box2_q0114_02.htm";
            } else if (cond == 14)
                htmltext = "chaos_box2_q0114_04.htm";
        } else if (npcId == STONES) {
            if (cond == 18)
                htmltext = "pavel_atlanta_q0114_02.htm";
            else if (cond == 19)
                htmltext = "pavel_atlanta_q0114_03.htm";
            else if (cond == 27)
                htmltext = "pavel_atlanta_q0114_04.htm";
        } else if (npcId == NEWYEAR)
            if (cond == 21)
                htmltext = "head_blacksmith_newyear_q0114_01.htm";
            else if (cond == 22)
                htmltext = "head_blacksmith_newyear_q0114_03.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;

        int npcId = npc.getNpcId();
        if (st.getCond() == 10)
            if (npcId == GUARDIAN) {
                Functions.npcSay(npc, "This enemy is far too powerful for me to fight. I must withdraw");
                st.setCond(11);
                st.playSound(SOUND_MIDDLE);
            }
    }
}