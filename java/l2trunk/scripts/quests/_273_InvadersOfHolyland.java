package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class _273_InvadersOfHolyland extends Quest {
    private final int BLACK_SOULSTONE = 1475;
    private final int RED_SOULSTONE = 1476;
    private final List<Integer> stones = List.of(BLACK_SOULSTONE,RED_SOULSTONE);

    public _273_InvadersOfHolyland() {
        super(false);

        addStartNpc(30566);
        addKillId(20311,
                20312,
                20313);
        addQuestItem(stones);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        switch (event) {
            case "atuba_chief_varkees_q0273_03.htm":
                st.setCond(1);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                break;
            case "atuba_chief_varkees_q0273_07.htm":
                st.setCond(0);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
                break;
            case "atuba_chief_varkees_q0273_08.htm":
                st.setCond(1);
                st.setState(STARTED);
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
            if (st.getPlayer().getRace() != Race.orc) {
                htmltext = "atuba_chief_varkees_q0273_00.htm";
                st.exitCurrentQuest(true);
            } else if (st.getPlayer().getLevel() < 6) {
                htmltext = "atuba_chief_varkees_q0273_01.htm";
                st.exitCurrentQuest(true);
            } else {
                htmltext = "atuba_chief_varkees_q0273_02.htm";
                return htmltext;
            }
        } else if (cond > 0)
            if (st.getQuestItemsCount(BLACK_SOULSTONE) == 0 && st.getQuestItemsCount(RED_SOULSTONE) == 0)
                htmltext = "atuba_chief_varkees_q0273_04.htm";
            else {
                long adena = 0;
                if (st.getQuestItemsCount(BLACK_SOULSTONE) > 0) {
                    htmltext = "atuba_chief_varkees_q0273_05.htm";
                    adena += st.getQuestItemsCount(BLACK_SOULSTONE) * 5;
                }
                if (st.getQuestItemsCount(RED_SOULSTONE) > 0) {
                    htmltext = "atuba_chief_varkees_q0273_06.htm";
                    adena += st.getQuestItemsCount(RED_SOULSTONE) * 50;
                }
                st.takeItems(stones);
                st.giveItems(ADENA_ID, adena);

                if (st.getPlayer().getClassId().getLevel() == 1 && !st.getPlayer().getVarB("p1q2")) {
                    st.getPlayer().setVar("p1q2", "1", -1);
                    st.getPlayer().sendPacket(new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n                  Go find the Newbie Guide."));
                    QuestState qs = st.getPlayer().getQuestState(_255_Tutorial.class);
                    if (qs != null && qs.getInt("Ex") != 10) {
                        st.showQuestionMark(26);
                        qs.set("Ex", "10");
                        if (st.getPlayer().getClassId().isMage) {
                            st.playTutorialVoice("tutorial_voice_027");
                            st.giveItems(5790, 3000);
                        } else {
                            st.playTutorialVoice("tutorial_voice_026");
                            st.giveItems(5789, 6000);
                        }
                    }
                }

                st.exitCurrentQuest(true);
                st.playSound(SOUND_FINISH);
            }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == 20311) {
            if (cond == 1) {
                if (Rnd.chance(90))
                    st.giveItems(BLACK_SOULSTONE, 1);
                else
                    st.giveItems(RED_SOULSTONE, 1);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20312) {
            if (cond == 1) {
                if (Rnd.chance(87))
                    st.giveItems(BLACK_SOULSTONE, 1);
                else
                    st.giveItems(RED_SOULSTONE, 1);
                st.playSound(SOUND_ITEMGET);
            }
        } else if (npcId == 20313)
            if (cond == 1) {
                if (Rnd.chance(77))
                    st.giveItems(BLACK_SOULSTONE, 1);
                else
                    st.giveItems(RED_SOULSTONE, 1);
                st.playSound(SOUND_ITEMGET);
            }
        return null;
    }
}