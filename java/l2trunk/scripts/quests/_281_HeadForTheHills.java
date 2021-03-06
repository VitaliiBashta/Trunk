package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;

import java.util.Map;

public final class _281_HeadForTheHills extends Quest {
    //items
    private static final int ScrollOfEscape = 736;
    //NPC
    private final int Marcela = 32173;
    //Mobs
    private final int GreenGoblin = 22234;
    private final int MountainWerewolf = 22235;
    private final int MuertosArcher = 22236;
    private final int MountainFungus = 22237;
    private final int MountainWerewolfChief = 22238;
    private final int MuertosGuard = 22239;
    //QuestItem
    private final int HillsOfGoldMonsterClaw = 9796;
    //Drop Cond
    //# [ID, CHANCE]
    private final Map<Integer, Integer> DROPLIST_CHANCES = Map.of(
            GreenGoblin, 70,
            MountainWerewolf, 75,
            MuertosArcher, 80,
            MountainFungus, 70,
            MountainWerewolfChief, 90,
            MuertosGuard, 90
    );

    public _281_HeadForTheHills() {
        addStartNpc(Marcela);
        addKillId(DROPLIST_CHANCES.keySet());
        addQuestItem(HillsOfGoldMonsterClaw);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("zerstorer_morsell_q0281_03.htm".equalsIgnoreCase(event)) {
            if (st.getCond() == 0) {
                st.setCond(1);
                st.start();
                st.playSound(SOUND_ACCEPT);
            }
        } else if ("adena".equalsIgnoreCase(event)) {
            st.giveAdena(st.getQuestItemsCount(HillsOfGoldMonsterClaw) * 50);
            st.takeItems(HillsOfGoldMonsterClaw);
            tryGiveOneTimeRevard(st);
            htmltext = "zerstorer_morsell_q0281_06.htm";
        } else if ("soe".equalsIgnoreCase(event)) {
            if (st.haveQuestItem(HillsOfGoldMonsterClaw, 50)) {
                st.takeItems(HillsOfGoldMonsterClaw, 50);
                st.giveItems(ScrollOfEscape, 5);
                tryGiveOneTimeRevard(st);
                htmltext = "zerstorer_morsell_q0281_06.htm";
            } else
                htmltext = "zerstorer_morsell_q0281_04.htm";
        } else if ("zerstorer_morsell_q0281_09.htm".equalsIgnoreCase(event))
            st.exitCurrentQuest();

        return htmltext;
    }

    private void tryGiveOneTimeRevard(QuestState st) {
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

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        int cond = 0;
        if (id != CREATED)
            cond = st.getCond();
        if (npcId == Marcela)
            if (st.player.getLevel() < 6) {
                htmltext = "zerstorer_morsell_q0281_02.htm";
                st.exitCurrentQuest();
            } else if (cond == 0)
                htmltext = "zerstorer_morsell_q0281_01.htm";
            else if (cond == 1 && st.haveQuestItem(HillsOfGoldMonsterClaw))
                htmltext = "zerstorer_morsell_q0281_05.htm";
            else
                htmltext = "zerstorer_morsell_q0281_03.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        if (st.getCond() != 1)
            return;
        if (DROPLIST_CHANCES.keySet().contains(npcId))
            st.rollAndGive(HillsOfGoldMonsterClaw, 1, DROPLIST_CHANCES.get(npcId));
    }
}