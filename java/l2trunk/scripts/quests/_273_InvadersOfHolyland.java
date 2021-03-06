package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _273_InvadersOfHolyland extends Quest {
    private final int BLACK_SOULSTONE = 1475;
    private final int RED_SOULSTONE = 1476;

    public _273_InvadersOfHolyland() {
        addStartNpc(30566);
        addKillId(20311, 20312, 20313);
        addQuestItem(BLACK_SOULSTONE, RED_SOULSTONE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        switch (event) {
            case "atuba_chief_varkees_q0273_03.htm":
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
            case "atuba_chief_varkees_q0273_07.htm":
                st.setCond(0);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
                break;
            case "atuba_chief_varkees_q0273_08.htm":
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
                break;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getRace() != Race.orc) {
                htmltext = "atuba_chief_varkees_q0273_00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() < 6) {
                htmltext = "atuba_chief_varkees_q0273_01.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "atuba_chief_varkees_q0273_02.htm";
                return htmltext;
            }
        } else if (cond > 0)
            if (!st.haveAnyQuestItems(BLACK_SOULSTONE,RED_SOULSTONE))
                htmltext = "atuba_chief_varkees_q0273_04.htm";
            else {
                long adena = 0;
                if (st.haveQuestItem(BLACK_SOULSTONE)) {
                    htmltext = "atuba_chief_varkees_q0273_05.htm";
                    adena += st.getQuestItemsCount(BLACK_SOULSTONE) * 5;
                }
                if (st.haveQuestItem(RED_SOULSTONE)) {
                    htmltext = "atuba_chief_varkees_q0273_06.htm";
                    adena += st.getQuestItemsCount(RED_SOULSTONE) * 50;
                }
                st.takeAllItems(BLACK_SOULSTONE,RED_SOULSTONE);
                st.giveAdena( adena);

                if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q2")) {
                    st.player.setVar("p1q2");
                    st.player.sendPacket(new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n                  Go find the Newbie Guide."));
                    QuestState qs = st.player.getQuestState(_255_Tutorial.class);
                    if (qs != null && qs.getInt("Ex") != 10) {
                        st.showQuestionMark(26);
                        qs.set("Ex", 10);
                        if (st.player.getClassId().isMage()) {
                            st.playTutorialVoice("tutorial_voice_027");
                            st.giveItems(5790, 3000);
                        } else {
                            st.playTutorialVoice("tutorial_voice_026");
                            st.giveItems(5789, 6000);
                        }
                    }
                }

                st.exitCurrentQuest();
                st.playSound(SOUND_FINISH);
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 20311) {
            if (cond == 1) {
                if (Rnd.chance(90))
                    st.giveItems(BLACK_SOULSTONE);
                else
                    st.giveItems(RED_SOULSTONE);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20312) {
            if (cond == 1) {
                if (Rnd.chance(87))
                    st.giveItems(BLACK_SOULSTONE);
                else
                    st.giveItems(RED_SOULSTONE);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20313)
            if (cond == 1) {
                if (Rnd.chance(77))
                    st.giveItems(BLACK_SOULSTONE);
                else
                    st.giveItems(RED_SOULSTONE);
                st.playSound(SOUND_ITEMGET);
            }
    }
}