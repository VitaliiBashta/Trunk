package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _103_SpiritOfCraftsman extends Quest {
    private final int KAROYDS_LETTER_ID = 968;
    private final int CECKTINONS_VOUCHER1_ID = 969;
    private final int CECKTINONS_VOUCHER2_ID = 970;
    private final int BONE_FRAGMENT1_ID = 1107;
    private final int SOUL_CATCHER_ID = 971;
    private final int PRESERVE_OIL_ID = 972;
    private final int ZOMBIE_HEAD_ID = 973;
    private final int STEELBENDERS_HEAD_ID = 974;
    private static final int BLOODSABER_ID = 975;

    public _103_SpiritOfCraftsman() {
        super(false);

        addStartNpc(30307);

        addTalkId(30132);
        addTalkId(30144);

        addKillId(20015);
        addKillId(20020);
        addKillId(20455);
        addKillId(20517);
        addKillId(20518);

        addQuestItem(KAROYDS_LETTER_ID, CECKTINONS_VOUCHER1_ID, CECKTINONS_VOUCHER2_ID, BONE_FRAGMENT1_ID, SOUL_CATCHER_ID, PRESERVE_OIL_ID, ZOMBIE_HEAD_ID, STEELBENDERS_HEAD_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("blacksmith_karoyd_q0103_05.htm")) {
            st.giveItems(KAROYDS_LETTER_ID, 1);
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED)
            st.setCond(0);
        if (npcId == 30307 && st.getCond() == 0) {
            if (st.player.getRace() != Race.darkelf)
                htmltext = "blacksmith_karoyd_q0103_00.htm";
            else if (st.player.getLevel() >= 10) {
                htmltext = "blacksmith_karoyd_q0103_03.htm";
                return htmltext;
            } else {
                htmltext = "blacksmith_karoyd_q0103_02.htm";
                st.exitCurrentQuest(true);
            }
        } else if (npcId == 30307 && st.getCond() == 0)
            htmltext = "completed";
        else if (id == STARTED)
            if (npcId == 30307 && st.getCond() >= 1 && (st.haveQuestItem(KAROYDS_LETTER_ID)  || st.getQuestItemsCount(CECKTINONS_VOUCHER1_ID) >= 1 || st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID) >= 1))
                htmltext = "blacksmith_karoyd_q0103_06.htm";
            else if (npcId == 30132 && st.getCond() == 1 && st.haveQuestItem(KAROYDS_LETTER_ID)) {
                htmltext = "cecon_q0103_01.htm";
                st.setCond(2);
                st.takeItems(KAROYDS_LETTER_ID, 1);
                st.giveItems(CECKTINONS_VOUCHER1_ID);
            } else if (npcId == 30132 && st.getCond() >= 2 && (st.haveQuestItem(CECKTINONS_VOUCHER1_ID)  || st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID) >= 1))
                htmltext = "cecon_q0103_02.htm";
            else if (npcId == 30144 && st.getCond() == 2 && st.haveQuestItem(CECKTINONS_VOUCHER1_ID) ) {
                htmltext = "harne_q0103_01.htm";
                st.setCond(3);
                st.takeItems(CECKTINONS_VOUCHER1_ID, 1);
                st.giveItems(CECKTINONS_VOUCHER2_ID);
            } else if (npcId == 30144 && st.getCond() == 3 && st.haveQuestItem(CECKTINONS_VOUCHER2_ID)  && st.getQuestItemsCount(BONE_FRAGMENT1_ID) < 10)
                htmltext = "harne_q0103_02.htm";
            else if (npcId == 30144 && st.getCond() == 4 && st.getQuestItemsCount(CECKTINONS_VOUCHER2_ID) == 1 && st.getQuestItemsCount(BONE_FRAGMENT1_ID) >= 10) {
                htmltext = "harne_q0103_03.htm";
                st.setCond(5);
                st.takeItems(CECKTINONS_VOUCHER2_ID, 1);
                st.takeItems(BONE_FRAGMENT1_ID, 10);
                st.giveItems(SOUL_CATCHER_ID);
            } else if (npcId == 30144 && st.getCond() == 5 && st.haveQuestItem(SOUL_CATCHER_ID) )
                htmltext = "harne_q0103_04.htm";
            else if (npcId == 30132 && st.getCond() == 5 && st.haveQuestItem(SOUL_CATCHER_ID) ) {
                htmltext = "cecon_q0103_03.htm";
                st.setCond(6);
                st.takeItems(SOUL_CATCHER_ID, 1);
                st.giveItems(PRESERVE_OIL_ID);
            } else if (npcId == 30132 && st.getCond() == 6 && st.haveQuestItem(PRESERVE_OIL_ID)  && st.getQuestItemsCount(ZOMBIE_HEAD_ID) == 0 && st.getQuestItemsCount(STEELBENDERS_HEAD_ID) == 0)
                htmltext = "cecon_q0103_04.htm";
            else if (npcId == 30132 && st.getCond() == 7 && st.haveQuestItem(ZOMBIE_HEAD_ID) ) {
                htmltext = "cecon_q0103_05.htm";
                st.setCond(8);
                st.takeItems(ZOMBIE_HEAD_ID, 1);
                st.giveItems(STEELBENDERS_HEAD_ID);
            } else if (npcId == 30132 && st.getCond() == 8 && st.haveQuestItem(STEELBENDERS_HEAD_ID) )
                htmltext = "cecon_q0103_06.htm";
            else if (npcId == 30307 && st.getCond() == 8 && st.haveQuestItem(STEELBENDERS_HEAD_ID) ) {
                htmltext = "blacksmith_karoyd_q0103_07.htm";
                st.takeItems(STEELBENDERS_HEAD_ID, 1);

                st.giveItems(BLOODSABER_ID);
                st.giveItems(ADENA_ID, 19799, false);
                st.player.addExpAndSp(46663, 3999);

                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q3")) {
                    st.player.setVar("p1q3", 1); // flag for helper
                    st.player.sendPacket(new ExShowScreenMessage("Now go find the Newbie Guide."));
                    st.giveItems(1060, 100); // healing potion
                    for (int item = 4412; item <= 4417; item++)
                        st.giveItems(item, 10); // echo cry
                    if (st.player.getClassId().isMage) {
                        st.playTutorialVoice("tutorial_voice_027");
                        st.giveItems(5790, 3000); // newbie sps
                    } else {
                        st.playTutorialVoice("tutorial_voice_026");
                        st.giveItems(5789, 6000); // newbie ss
                    }
                }

                st.exitCurrentQuest(false);
                st.playSound(SOUND_FINISH);
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if ((npcId == 20517 || npcId == 20518 || npcId == 20455) && st.getCond() == 3) {
            if (st.haveQuestItem(CECKTINONS_VOUCHER2_ID) && st.getQuestItemsCount(BONE_FRAGMENT1_ID) < 10)
                if (Rnd.chance(33)) {
                    st.giveItems(BONE_FRAGMENT1_ID);
                    if (st.getQuestItemsCount(BONE_FRAGMENT1_ID) == 10) {
                        st.playSound(SOUND_MIDDLE);
                        st.setCond(4);
                    } else
                        st.playSound(SOUND_ITEMGET);
                }
        } else if ((npcId == 20015 || npcId == 20020) && st.getCond() == 6)
            if (st.getQuestItemsCount(PRESERVE_OIL_ID) == 1)
                if (Rnd.chance(33)) {
                    st.giveItems(ZOMBIE_HEAD_ID);
                    st.playSound(SOUND_MIDDLE);
                    st.takeItems(PRESERVE_OIL_ID, 1);
                    st.setCond(7);
                }
    }
}
