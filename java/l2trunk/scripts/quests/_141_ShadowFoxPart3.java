package l2trunk.scripts.quests;

import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _141_ShadowFoxPart3 extends Quest {
    // NPC
    private final static int NATOOLS = 30894;

    // items
    private final static int REPORT = 10350;

    // Monsters
    private final static int CrokianWarrior = 20791;
    private final static int Farhite = 20792;
    private final static int Alligator = 20135;

    public _141_ShadowFoxPart3() {
        super(false);

        // Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
        addFirstTalkId(NATOOLS);
        addTalkId(NATOOLS);
        addQuestItem(REPORT);
        addKillId(CrokianWarrior, Farhite, Alligator);
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        if (player.isQuestCompleted(_140_ShadowFoxPart2.class) && player.getQuestState(this) == null)
            newQuestState(player, STARTED);
        return "";
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("30894-02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30894-04.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.start();
            st.playSound(SOUND_MIDDLE);
        } else if ("30894-15.htm".equalsIgnoreCase(event)) {
            st.setCond(4);
            st.start();
            st.unset("talk");
            st.playSound(SOUND_MIDDLE);
        } else if ("30894-18.htm".equalsIgnoreCase(event)) {
            if (st.getInt("reward") != 1) {
                st.playSound(SOUND_FINISH);
                st.giveItems(ADENA_ID, 88888);
                st.addExpAndSp(278005, 17058);
                st.set("reward", 1);
                htmltext = "getBonuses.htm";
            } else
                htmltext = "getBonuses.htm";
        } else if ("dawn".equalsIgnoreCase(event)) {
            Quest q1 = QuestManager.getQuest(_142_FallenAngelRequestOfDawn.class);
            if (q1 != null) {
                st.finish();
                QuestState qs1 = q1.newQuestState(st.player, STARTED);
                q1.notifyEvent("start", qs1, npc);
                return null;
            }
        } else if ("dusk".equalsIgnoreCase(event)) {
            Quest q1 = QuestManager.getQuest(_143_FallenAngelRequestOfDusk.class);
            if (q1 != null) {
                st.finish();
                QuestState qs1 = q1.newQuestState(st.player, STARTED);
                q1.notifyEvent("start", qs1, npc);
                return null;
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        String htmltext = "noquest";
        if (cond == 0) {
            if (st.player.getLevel() >= 37)
                htmltext = "30894-01.htm";
            else
                htmltext = "30894-00.htm";
        } else if (cond == 1)
            htmltext = "30894-02.htm";
        else if (cond == 2)
            htmltext = "30894-05.htm";
        else if (cond == 3) {
            if (st.getInt("talk") == 1)
                htmltext = "30894-07.htm";
            else {
                htmltext = "30894-06.htm";
                st.takeItems(REPORT);
                st.set("talk", 1);
            }
        } else if (cond == 4)
            htmltext = "30894-16.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 2 && st.rollAndGive(REPORT, 1, 1, 30, 80 * npc.getTemplate().rateHp))
            st.setCond(3);
    }
}