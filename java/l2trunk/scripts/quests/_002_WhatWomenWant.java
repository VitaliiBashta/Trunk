package l2trunk.scripts.quests;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _002_WhatWomenWant extends Quest {
    private final int ARUJIEN = 30223;
    private final int MIRABEL = 30146;
    private final int HERBIEL = 30150;
    private final int GREENIS = 30157;

    private final int ARUJIENS_LETTER1 = 1092;
    private final int ARUJIENS_LETTER2 = 1093;
    private final int ARUJIENS_LETTER3 = 1094;
    private final int POETRY_BOOK = 689;
    private final int GREENIS_LETTER = 693;

    public _002_WhatWomenWant() {
        super(false);
        addStartNpc(ARUJIEN);

        addTalkId(MIRABEL);
        addTalkId(HERBIEL);
        addTalkId(GREENIS);

        addQuestItem(GREENIS_LETTER, ARUJIENS_LETTER3, ARUJIENS_LETTER1, ARUJIENS_LETTER2, POETRY_BOOK);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        switch (event) {
            case "quest_accept":
                htmltext = "arujien_q0002_04.htm";
                st.giveItems(ARUJIENS_LETTER1);
                st.setCond(1);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                break;
            case "2_1":
                htmltext = "arujien_q0002_08.htm";
                st.takeItems(ARUJIENS_LETTER3);
                st.giveItems(POETRY_BOOK);
                st.setCond(4);
                st.playSound(SOUND_MIDDLE);
                break;
            case "2_2":
                htmltext = "arujien_q0002_09.htm";
                st.takeItems(ARUJIENS_LETTER3);
                st.giveItems(ADENA_ID, 2300, true);
                st.player.addExpAndSp(4254, 335);
                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("ng1"))
                    st.player.sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide."));
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
                break;
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == ARUJIEN) {
            if (cond == 0) {
                if (st.player.getRace() != Race.elf && st.player.getRace() != Race.human)
                    htmltext = "arujien_q0002_00.htm";
                else if (st.player.getLevel() >= 2)
                    htmltext = "arujien_q0002_02.htm";
                else {
                    htmltext = "arujien_q0002_01.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1 && st.getQuestItemsCount(ARUJIENS_LETTER1) > 0)
                htmltext = "arujien_q0002_05.htm";
            else if (cond == 2 && st.getQuestItemsCount(ARUJIENS_LETTER2) > 0)
                htmltext = "arujien_q0002_06.htm";
            else if (cond == 3 && st.getQuestItemsCount(ARUJIENS_LETTER3) > 0)
                htmltext = "arujien_q0002_07.htm";
            else if (cond == 4 && st.getQuestItemsCount(POETRY_BOOK) > 0)
                htmltext = "arujien_q0002_11.htm";
            else if (cond == 5 && st.getQuestItemsCount(GREENIS_LETTER) > 0) {
                htmltext = "arujien_q0002_09.htm";
                st.takeItems(GREENIS_LETTER);
                int MYSTICS_EARRING = 113;
                st.giveItems(MYSTICS_EARRING);
                st.giveItems(ADENA_ID, (int) ((Config.RATE_QUESTS_REWARD - 1) * 620 + 1850 * Config.RATE_QUESTS_REWARD), false); // T2
                st.player.addExpAndSp(4254, 335);
                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("ng1"))
                    st.player.sendPacket(new ExShowScreenMessage("  Delivery duty complete.\nGo find the Newbie Guide."));
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(false);
            }
        } else if (npcId == MIRABEL) {
            if (cond == 1 && st.getQuestItemsCount(ARUJIENS_LETTER1) > 0) {
                htmltext = "mint_q0002_01.htm";
                st.takeItems(ARUJIENS_LETTER1);
                st.giveItems(ARUJIENS_LETTER2);
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            } else if (cond == 2)
                htmltext = "mint_q0002_02.htm";
        } else if (npcId == HERBIEL) {
            if (cond == 2 && st.getQuestItemsCount(ARUJIENS_LETTER2) > 0) {
                htmltext = "green_q0002_01.htm";
                st.takeItems(ARUJIENS_LETTER2);
                st.giveItems(ARUJIENS_LETTER3);
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
            } else if (cond == 3)
                htmltext = "green_q0002_02.htm";
        } else if (npcId == GREENIS)
            if (cond == 4 && st.getQuestItemsCount(POETRY_BOOK) > 0) {
                htmltext = "grain_q0002_02.htm";
                st.takeItems(POETRY_BOOK);
                st.giveItems(GREENIS_LETTER);
                st.setCond(5);
                st.playSound(SOUND_MIDDLE);
            } else if (cond == 5 && st.getQuestItemsCount(GREENIS_LETTER) > 0)
                htmltext = "grain_q0002_03.htm";
            else
                htmltext = "grain_q0002_01.htm";
        return htmltext;
    }
}
