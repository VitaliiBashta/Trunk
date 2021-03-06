package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _140_ShadowFoxPart2 extends Quest {
    // NPCs
    private final static int KLUCK = 30895;
    private final static int XENOVIA = 30912;

    // items
    private final static int CRYSTAL = 10347;
    private final static int OXYDE = 10348;
    private final static int CRYPT = 10349;

    // Monsters
    private final static int Crokian = 20789;
    private final static int Dailaon = 20790;
    private final static int CrokianWarrior = 20791;
    private final static int Farhite = 20792;

    public _140_ShadowFoxPart2() {
        addFirstTalkId(KLUCK);
        addTalkId(KLUCK, XENOVIA);
        addQuestItem(CRYSTAL, OXYDE, CRYPT);
        addKillId(Crokian, Dailaon, CrokianWarrior, Farhite);
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        if (player.isQuestCompleted(_139_ShadowFoxPart1.class) && player.getQuestState(this) == null)
            newQuestState(player, STARTED);
        return "";
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30895-02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30895-05.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.start();
            st.playSound(SOUND_MIDDLE);
        } else if ("30895-09.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.giveItems(ADENA_ID, 18775);
            st.addExpAndSp(30000, 2000);
            Quest q = QuestManager.getQuest(_141_ShadowFoxPart3.class);
            if (q != null)
                q.newQuestState(st.player, STARTED);
            st.finish();
        } else if ("30912-07.htm".equalsIgnoreCase(event)) {
            st.setCond(3);
            st.start();
            st.playSound(SOUND_MIDDLE);
        } else if ("30912-09.htm".equalsIgnoreCase(event)) {
            st.takeItems(CRYSTAL, 5);
            if (Rnd.chance(60)) {
                st.giveItems(OXYDE);
                if (st.getQuestItemsCount(OXYDE) >= 3) {
                    htmltext = "30912-09b.htm";
                    st.setCond(4);
                    st.start();
                    st.playSound(SOUND_MIDDLE);
                    st.takeAllItems(CRYSTAL, OXYDE);
                    st.giveItems(CRYPT);
                }
            } else
                htmltext = "30912-09a.htm";
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        String htmltext = "noquest";
        if (npcId == KLUCK) {
            if (cond == 0) {
                if (st.player.getLevel() >= 37)
                    htmltext = "30895-01.htm";
                else
                    htmltext = "30895-00.htm";
            } else if (cond == 1)
                htmltext = "30895-02.htm";
            else if (cond == 2 || cond == 3)
                htmltext = "30895-06.htm";
            else if (cond == 4)
                if (st.isSet("talk") )
                    htmltext = "30895-08.htm";
                else {
                    htmltext = "30895-07.htm";
                    st.takeItems(CRYPT);
                    st.set("talk");
                }
        } else if (npcId == XENOVIA)
            if (cond == 2)
                htmltext = "30912-01.htm";
            else if (cond == 3)
                if (st.haveQuestItem(CRYSTAL, 5))
                    htmltext = "30912-08.htm";
                else
                    htmltext = "30912-07.htm";
            else if (cond == 4)
                htmltext = "30912-10.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 3)
            st.rollAndGive(CRYSTAL, 1, 80 * npc.getTemplate().rateHp);
    }
}