package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _139_ShadowFoxPart1 extends Quest {
    // NPC
    private final static int MIA = 30896;

    // items
    private final static int FRAGMENT = 10345;
    private final static int CHEST = 10346;

    // Monsters
    private final static int TasabaLizardman1 = 20784;
    private final static int TasabaLizardman2 = 21639;
    private final static int TasabaLizardmanShaman1 = 20785;
    private final static int TasabaLizardmanShaman2 = 21640;

    public _139_ShadowFoxPart1() {
        super(false);

        // Нет стартового NPC, чтобы квест не появлялся в списке раньше времени
        addFirstTalkId(MIA);
        addTalkId(MIA);
        addQuestItem(FRAGMENT, CHEST);
        addKillId(TasabaLizardman1, TasabaLizardman2, TasabaLizardmanShaman1, TasabaLizardmanShaman2);
    }

    @Override
    public String onFirstTalk(NpcInstance npc, Player player) {
        if (player.isQuestCompleted(_138_TempleChampionPart2.class) && player.getQuestState(this) == null)
            newQuestState(player, STARTED);
        return "";
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("30896-03.htm")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        } else if ("30896-11.htm".equalsIgnoreCase(event)) {
            st.setCond(2);
            st.start();
            st.playSound(SOUND_MIDDLE);
        } else if ("30896-14.htm".equalsIgnoreCase(event)) {
            st.takeItems(FRAGMENT);
            st.takeItems(CHEST);
            st.set("talk", 1);
        } else if ("30896-16.htm".equalsIgnoreCase(event)) {
            st.playSound(SOUND_FINISH);
            st.giveItems(ADENA_ID, 14050);
            st.addExpAndSp(30000, 2000);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        int npcId = npc.getNpcId();
        if (npcId == MIA)
            if (cond == 0) {
                if (st.player.getLevel() >= 37)
                    htmltext = "30896-01.htm";
                else
                    htmltext = "30896-00.htm";
            } else if (cond == 1)
                htmltext = "30896-03.htm";
            else if (cond == 2)
                if (st.getQuestItemsCount(FRAGMENT) >= 10 && st.getQuestItemsCount(CHEST) >= 1)
                    htmltext = "30896-13.htm";
                else if (st.getInt("talk") == 1)
                    htmltext = "30896-14.htm";
                else
                    htmltext = "30896-12.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();
        if (cond == 2) {
            st.giveItems(FRAGMENT);
            st.playSound(SOUND_ITEMGET);
            if (Rnd.chance(10))
                st.giveItems(CHEST);
        }
    }
}