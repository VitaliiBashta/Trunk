package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

public final class _257_GuardIsBusy extends Quest {
    private final int GLUDIO_LORDS_MARK = 1084;
    private final int ORC_AMULET = 752;
    private final int ORC_NECKLACE = 1085;
    private final int WEREWOLF_FANG = 1086;
    private final int ADENA = 57;

    public _257_GuardIsBusy() {
        super(false);

        addStartNpc(30039);
        addKillId(20130, 20131, 20132, 20342, 20343, 20006, 20093, 20096, 20098);
        addQuestItem(ORC_AMULET, ORC_NECKLACE, WEREWOLF_FANG, GLUDIO_LORDS_MARK);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("gilbert_q0257_03.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            st.takeItems(GLUDIO_LORDS_MARK);
            st.giveItems(GLUDIO_LORDS_MARK);
        } else if ("257_2".equals(event)) {
            htmltext = "gilbert_q0257_05.htm";
            st.takeItems(GLUDIO_LORDS_MARK);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
        } else if ("257_3".equals(event))
            htmltext = "gilbert_q0257_06.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();

        if (cond == 0) {
            if (st.player.getLevel() >= 6) {
                htmltext = "gilbert_q0257_02.htm";
                return htmltext;
            }
            htmltext = "gilbert_q0257_01.htm";
            st.exitCurrentQuest();
        } else if (cond == 1 && st.getQuestItemsCount(ORC_AMULET) < 1 && st.getQuestItemsCount(ORC_NECKLACE) < 1 && st.getQuestItemsCount(WEREWOLF_FANG) < 1)
            htmltext = "gilbert_q0257_04.htm";
        else if (cond == 1 && (st.haveAnyQuestItems(ORC_AMULET,ORC_NECKLACE,WEREWOLF_FANG) )) {
            st.giveItems(ADENA, 12 * st.getQuestItemsCount(ORC_AMULET) + 20 * st.getQuestItemsCount(ORC_NECKLACE) + 25 * st.getQuestItemsCount(WEREWOLF_FANG), false);

            if (st.player.getClassId().occupation() == 0 && !st.player.isVarSet("p1q2")) {
                st.player.setVar("p1q2");
                st.player.sendPacket(new ExShowScreenMessage("Acquisition of Soulshot for beginners complete.\n                  Go find the Newbie Guide."));
                QuestState qs = st.player.getQuestState(_255_Tutorial.class);
                if (qs != null && qs.getInt("Ex") != 10) {
                    st.showQuestionMark(26);
                    qs.set("Ex", 10);
                    if (st.player.getClassId().isMage) {
                        st.playTutorialVoice("tutorial_voice_027");
                        st.giveItems(5790, 3000);
                    } else {
                        st.playTutorialVoice("tutorial_voice_026");
                        st.giveItems(5789, 6000);
                    }
                }
            }

            st.takeItems(ORC_AMULET);
            st.takeItems(ORC_NECKLACE);
            st.takeItems(WEREWOLF_FANG);
            htmltext = "gilbert_q0257_07.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.haveQuestItem(GLUDIO_LORDS_MARK)  && st.getCond() > 0)
            if (npcId == 20130 || npcId == 20131 || npcId == 20006)
                st.rollAndGive(ORC_AMULET, 1, 50);
            else if (npcId == 20093 || npcId == 20096 || npcId == 20098)
                st.rollAndGive(ORC_NECKLACE, 1, 50);
            else if (npcId == 20132)
                st.rollAndGive(WEREWOLF_FANG, 1, 33);
            else if (npcId == 20343)
                st.rollAndGive(WEREWOLF_FANG, 1, 50);
            else if (npcId == 20342)
                st.rollAndGive(WEREWOLF_FANG, 1, 75);
    }
}