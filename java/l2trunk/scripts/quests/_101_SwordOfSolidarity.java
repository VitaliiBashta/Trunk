package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _101_SwordOfSolidarity extends Quest {
    private static final int SWORD_OF_SOLIDARITY = 738;
    private final int ROIENS_LETTER = 796;
    private final int HOWTOGO_RUINS = 937;
    private final int BROKEN_SWORD_HANDLE = 739;
    private final int BROKEN_BLADE_BOTTOM = 740;
    private final int BROKEN_BLADE_TOP = 741;
    private final int ALLTRANS_NOTE = 742;

    public _101_SwordOfSolidarity() {
        addStartNpc(30008);
        addTalkId(30283);

        addKillId(20361, 20362);

        addQuestItem(ALLTRANS_NOTE, HOWTOGO_RUINS, BROKEN_BLADE_TOP, BROKEN_BLADE_BOTTOM, ROIENS_LETTER, BROKEN_SWORD_HANDLE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("roien_q0101_04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.giveItems(ROIENS_LETTER);
        } else if ("blacksmith_alltran_q0101_02.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.takeItems(ROIENS_LETTER);
            st.giveItems(HOWTOGO_RUINS);
        } else if ("blacksmith_alltran_q0101_07.htm".equalsIgnoreCase(event)) {
            st.takeItems(BROKEN_SWORD_HANDLE);

            st.giveItems(SWORD_OF_SOLIDARITY);
            st.giveItems(ADENA_ID, 10981, false);
            st.player.addExpAndSp(25747, 2171);

            if (st.player.getClassId().occupation() == 0) {
                st.player.setVar("p1q3"); // flag for helper
                st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                st.giveItems(1060, 100); // healing potion
                for (int item = 4412; item <= 4417; item++)
                    st.giveItems(item, 10); // echo cry
                if (st.player.getClassId().isMage()) {
                    st.playTutorialVoice("tutorial_voice_027");
                    st.giveItems(5790, 3000); // newbie sps
                } else {
                    st.playTutorialVoice("tutorial_voice_026");
                    st.giveItems(5789, 6000); // newbie ss
                }
            }

            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 30008) {
            if (cond == 0) {

                if (st.player.getRace() != Race.human)
                    htmltext = "roien_q0101_00.htm";
                else if (st.player.getLevel() >= 9) {
                    htmltext = "roien_q0101_02.htm";
                    return htmltext;
                } else {
                    htmltext = "roien_q0101_08.htm";
                    st.exitCurrentQuest();
                }

            } else if (cond == 1 && st.haveQuestItem(ROIENS_LETTER))
                htmltext = "roien_q0101_05.htm";
            else if (cond >= 2 && st.getQuestItemsCount(ROIENS_LETTER) == 0 && st.getQuestItemsCount(ALLTRANS_NOTE) == 0) {
                if (st.haveAllQuestItems(BROKEN_BLADE_TOP, BROKEN_BLADE_BOTTOM))
                    htmltext = "roien_q0101_12.htm";
                if (st.getQuestItemsCount(BROKEN_BLADE_TOP) + st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) <= 1)
                    htmltext = "roien_q0101_11.htm";
                if (st.haveQuestItem(BROKEN_SWORD_HANDLE))
                    htmltext = "roien_q0101_07.htm";
                if (st.haveQuestItem(HOWTOGO_RUINS))
                    htmltext = "roien_q0101_10.htm";
            } else if (cond == 4 && st.haveQuestItem(ALLTRANS_NOTE)) {
                htmltext = "roien_q0101_06.htm";
                st.setCond(5);
                st.takeItems(ALLTRANS_NOTE);
                st.giveItems(BROKEN_SWORD_HANDLE);
            }
        } else if (npcId == 30283)
            if (cond == 1 && st.haveQuestItem(ROIENS_LETTER))
                htmltext = "blacksmith_alltran_q0101_01.htm";
            else if (cond >= 2 && st.haveQuestItem(HOWTOGO_RUINS)) {
                if (st.getQuestItemsCount(BROKEN_BLADE_TOP) + st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) == 1)
                    htmltext = "blacksmith_alltran_q0101_08.htm";
                else if (st.getQuestItemsCount(BROKEN_BLADE_TOP) + st.getQuestItemsCount(BROKEN_BLADE_BOTTOM) == 0)
                    htmltext = "blacksmith_alltran_q0101_03.htm";
                else if (st.haveAllQuestItems(BROKEN_BLADE_TOP, BROKEN_BLADE_BOTTOM)) {
                    htmltext = "blacksmith_alltran_q0101_04.htm";
                    st.setCond(4);
                    st.takeAllItems(HOWTOGO_RUINS, BROKEN_BLADE_TOP, BROKEN_BLADE_BOTTOM);
                    st.giveItems(ALLTRANS_NOTE);
                } else if (cond == 4 && st.haveQuestItem(ALLTRANS_NOTE))
                    htmltext = "blacksmith_alltran_q0101_05.htm";
            } else if (cond == 5 && st.haveQuestItem(BROKEN_SWORD_HANDLE))
                htmltext = "blacksmith_alltran_q0101_06.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if ((npcId == 20361 || npcId == 20362) && st.haveQuestItem(HOWTOGO_RUINS)) {
            if (Rnd.chance(60))
                st.giveItemIfNotHave(BROKEN_BLADE_TOP);
            else if (Rnd.chance(60))
                st.giveItemIfNotHave(BROKEN_BLADE_BOTTOM);
            st.playSound(SOUND_MIDDLE);
            if (st.haveAllQuestItems(BROKEN_BLADE_TOP, BROKEN_BLADE_BOTTOM))
                st.setCond(3);
        }
    }
}