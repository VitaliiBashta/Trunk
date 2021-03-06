package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _432_BirthdayPartySong extends Quest {
    //NPC
    private static final int MELODY_MAESTRO_OCTAVIA = 31043;
    //MOB
    private static final int ROUGH_HEWN_ROCK_GOLEMS = 21103;
    //Quest items
    private static final int RED_CRYSTALS = 7541;
    private static final int BIRTHDAY_ECHO_CRYSTAL = 7061;

    public _432_BirthdayPartySong() {
        addStartNpc(MELODY_MAESTRO_OCTAVIA);

        addKillId(ROUGH_HEWN_ROCK_GOLEMS);

        addQuestItem(RED_CRYSTALS);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if ("muzyko_q0432_0104.htm".equalsIgnoreCase(event)) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("muzyko_q0432_0201.htm".equalsIgnoreCase(event))
            if (st.haveQuestItem(RED_CRYSTALS, 50)) {
                st.takeItems(RED_CRYSTALS);
                st.giveItems(BIRTHDAY_ECHO_CRYSTAL, 25);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else
                htmltext = "muzyko_q0432_0202.htm";
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int condition = st.getCond();
        int npcId = npc.getNpcId();
        if (npcId == MELODY_MAESTRO_OCTAVIA)
            if (condition == 0) {
                if (st.player.getLevel() >= 31)
                    htmltext = "muzyko_q0432_0101.htm";
                else {
                    htmltext = "muzyko_q0432_0103.htm";
                    st.exitCurrentQuest();
                }
            } else if (condition == 1)
                htmltext = "muzyko_q0432_0106.htm";
            else if (condition == 2 && st.haveQuestItem(RED_CRYSTALS,50))
                htmltext = "muzyko_q0432_0105.htm";
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() != STARTED)
            return;
        int npcId = npc.getNpcId();

        if (npcId == ROUGH_HEWN_ROCK_GOLEMS)
            if (st.getCond() == 1 && st.getQuestItemsCount(RED_CRYSTALS) < 50) {
                st.giveItems(RED_CRYSTALS);

                if (st.haveQuestItem(RED_CRYSTALS, 50)) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(2);
                } else
                    st.playSound(SOUND_ITEMGET);
            }
    }
}