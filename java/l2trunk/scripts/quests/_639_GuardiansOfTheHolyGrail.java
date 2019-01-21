package l2trunk.scripts.quests;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _639_GuardiansOfTheHolyGrail extends Quest {

    private static final int DROP_CHANCE = 10; // Для х1 мобов

    // NPCS
    private static final int DOMINIC = 31350;
    private static final int GREMORY = 32008;
    private static final int GRAIL = 32028;

    // ITEMS
    private static final int SCRIPTURES = 8069;
    private static final int WATER_BOTTLE = 8070;
    private static final int HOLY_WATER_BOTTLE = 8071;

    // QUEST REWARD
    private static final int EAS = 960;
    private static final int EWS = 959;

    public _639_GuardiansOfTheHolyGrail() {
        super(true);
        addStartNpc(DOMINIC);
        addTalkId(GREMORY);
        addTalkId(GRAIL);
        addQuestItem(SCRIPTURES);
        for (int i = 22789; i <= 22800; i++)
            addKillId(i);
        addKillId(18909);
        addKillId(18910);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        switch (event) {
            case "falsepriest_dominic_q0639_04.htm":
                st.setCond(1);
                st.setState(STARTED);
                st.playSound(SOUND_ACCEPT);
                break;
            case "falsepriest_dominic_q0639_09.htm":
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
                break;
            case "falsepriest_dominic_q0639_08.htm":
                st.giveItems(ADENA_ID, st.takeAllItems(SCRIPTURES) * 1625, false);
                break;
            case "falsepriest_gremory_q0639_05.htm":
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
                st.giveItems(WATER_BOTTLE, 1, false);
                break;
            case "holy_grail_q0639_02.htm":
                st.setCond(3);
                st.playSound(SOUND_MIDDLE);
                st.takeItems(WATER_BOTTLE);
                st.giveItems(HOLY_WATER_BOTTLE);
                break;
            case "falsepriest_gremory_q0639_09.htm":
                st.setCond(4);
                st.playSound(SOUND_MIDDLE);
                st.takeItems(HOLY_WATER_BOTTLE);
                break;
            case "falsepriest_gremory_q0639_11.htm":
                st.takeItems(SCRIPTURES, 4000);
                st.giveItems(EWS, 1, false);
                break;
            case "falsepriest_gremory_q0639_13.htm":
                st.takeItems(SCRIPTURES, 400);
                st.giveItems(EAS, 1, false);
                break;
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getInt("cond");
        if (npcId == DOMINIC) {
            if (id == CREATED) {
                if (st.getPlayer().getLevel() >= 73)
                    htmltext = "falsepriest_dominic_q0639_01.htm";
                else
                    htmltext = "falsepriest_dominic_q0639_02.htm";
                st.exitCurrentQuest(true);
            } else if (st.getQuestItemsCount(SCRIPTURES) >= 1)
                htmltext = "falsepriest_dominic_q0639_05.htm";
            else
                htmltext = "falsepriest_dominic_q0639_06.htm";
        } else if (npcId == GREMORY) {
            if (cond == 1)
                htmltext = "falsepriest_gremory_q0639_01.htm";
            else if (cond == 2)
                htmltext = "falsepriest_gremory_q0639_06.htm";
            else if (cond == 3)
                htmltext = "falsepriest_gremory_q0639_08.htm";
            else if (cond == 4 && st.getQuestItemsCount(SCRIPTURES) < 400)
                htmltext = "falsepriest_gremory_q0639_09.htm";
            else if (cond == 4 && st.getQuestItemsCount(SCRIPTURES) >= 4000)
                htmltext = "falsepriest_gremory_q0639_10.htm";
            else if (cond == 4 && st.getQuestItemsCount(SCRIPTURES) >= 400 && st.getQuestItemsCount(SCRIPTURES) < 4000)
                htmltext = "falsepriest_gremory_q0639_14.htm";
        } else if (npcId == GRAIL && cond == 2) {
            htmltext = "holy_grail_q0639_01.htm";
        }
        return htmltext;
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        st.rollAndGive(SCRIPTURES, (int) Config.RATE_QUESTS_DROP, DROP_CHANCE * npc.getTemplate().rateHp);

        return null;
    }
}