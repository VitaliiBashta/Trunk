package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _366_SilverHairedShaman extends Quest {
    //NPC
    private static final int DIETER = 30111;

    //MOBS
    private static final int SAIRON = 20986;
    private static final int SAIRONS_DOLL = 20987;
    private static final int SAIRONS_PUPPET = 20988;
    //VARIABLES
    private static final int ADENA_PER_ONE = 500;
    private static final int START_ADENA = 12070;

    //QUEST ITEMS
    private static final int SAIRONS_SILVER_HAIR = 5874;

    public _366_SilverHairedShaman() {
        super(false);
        addStartNpc(DIETER);

        addKillId(SAIRON);
        addKillId(SAIRONS_DOLL);
        addKillId(SAIRONS_PUPPET);

        addQuestItem(SAIRONS_SILVER_HAIR);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("30111-02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("30111-quit.htm".equalsIgnoreCase(event)) {
            st.takeItems(SAIRONS_SILVER_HAIR);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest(true);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();
        if (id == CREATED)
            st.setCond(0);
        else
            cond = st.getCond();
        if (npcId == 30111)
            if (cond == 0) {
                if (st.player.getLevel() >= 48)
                    htmltext = "30111-01.htm";
                else {
                    htmltext = "30111-00.htm";
                    st.exitCurrentQuest(true);
                }
            } else if (cond == 1 && !st.haveQuestItem(SAIRONS_SILVER_HAIR) )
                htmltext = "30111-03.htm";
            else if (cond == 1 && st.haveQuestItem(SAIRONS_SILVER_HAIR)) {
                st.giveItems(ADENA_ID, (st.getQuestItemsCount(SAIRONS_SILVER_HAIR) * ADENA_PER_ONE + START_ADENA));
                st.takeItems(SAIRONS_SILVER_HAIR);
                htmltext = "30111-have.htm";
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && Rnd.chance(66)) {
            st.giveItems(SAIRONS_SILVER_HAIR);
            st.playSound(SOUND_MIDDLE);
        }
    }
}