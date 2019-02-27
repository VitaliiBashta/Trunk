package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _260_HuntTheOrcs extends Quest {
    private static final int ORC_AMULET = 1114;
    private static final int ORC_NECKLACE = 1115;

    public _260_HuntTheOrcs() {
        super(false);

        addStartNpc(30221);

        addKillId(20468, 20469, 20470, 20471, 20472, 20473);

        addQuestItem(ORC_AMULET, ORC_NECKLACE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("sentinel_rayjien_q0260_03.htm".equals(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("sentinel_rayjien_q0260_06.htm".equals(event)) {
            st.setCond(0);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npcId == 30221)
            if (cond == 0) {
                if (st.player.getLevel() >= 6 && st.player.getRace() == Race.elf) {
                    htmltext = "sentinel_rayjien_q0260_02.htm";
                    return htmltext;
                } else if (st.player.getRace() != Race.elf) {
                    htmltext = "sentinel_rayjien_q0260_00.htm";
                    st.exitCurrentQuest();
                } else {
                    htmltext = "sentinel_rayjien_q0260_01.htm";
                    st.exitCurrentQuest();
                }
            } else if (cond == 1 && (st.haveAnyQuestItems(ORC_AMULET,ORC_NECKLACE))) {
                htmltext = "sentinel_rayjien_q0260_05.htm";
                int adenaPay = 0;
                if (st.haveQuestItem(ORC_AMULET, 40))
                    adenaPay += st.getQuestItemsCount(ORC_AMULET) * 14;
                else
                    adenaPay += st.getQuestItemsCount(ORC_AMULET) * 12;
                if (st.haveQuestItem(ORC_NECKLACE, 40))
                    adenaPay += st.getQuestItemsCount(ORC_NECKLACE) * 40;
                else
                    adenaPay += st.getQuestItemsCount(ORC_NECKLACE) * 30;
                st.giveItems(ADENA_ID, adenaPay, false);
                st.takeItems(ORC_AMULET);
                st.takeItems(ORC_NECKLACE);

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
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.getCond() > 0)
            if (npcId == 20468 || npcId == 20469 || npcId == 20470)
                st.rollAndGive(ORC_AMULET, 1, 14);
            else if (npcId == 20471 || npcId == 20472 || npcId == 20473)
                st.rollAndGive(ORC_NECKLACE, 1, 14);
    }
}