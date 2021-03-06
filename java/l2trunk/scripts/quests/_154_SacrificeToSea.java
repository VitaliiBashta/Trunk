package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _154_SacrificeToSea extends Quest {
    private static final int FOX_FUR_ID = 1032;
    private static final int FOX_FUR_YARN_ID = 1033;
    private static final int MAIDEN_DOLL_ID = 1034;
    private static final int MYSTICS_EARRING_ID = 113;

    public _154_SacrificeToSea() {
        super(false);

        addStartNpc(30312);

        addTalkId(30051,30055);

        addKillId(20481, 20544, 20545);
        addQuestItem(FOX_FUR_ID, FOX_FUR_YARN_ID, MAIDEN_DOLL_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            st.unset("id");
            htmltext = "30312-04.htm";
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        String htmltext = "noquest";
        int id = st.getState();
        if (id == CREATED) {
            st.start();
            st.setCond(0);
            st.unset("id");
        }
        if (npcId == 30312 && st.getCond() == 0) {
            if (st.getCond() < 15) {
                if (st.player.getLevel() >= 2) {
                    htmltext = "30312-03.htm";
                    return htmltext;
                }
                htmltext = "30312-02.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "30312-02.htm";
                st.exitCurrentQuest();
            }
        } else if (npcId == 30312 && st.getCond() == 0)
            htmltext = "completed";
        else if (npcId == 30312 && st.getCond() == 1 && st.getQuestItemsCount(FOX_FUR_YARN_ID) == 0 && st.getQuestItemsCount(MAIDEN_DOLL_ID) == 0 && st.getQuestItemsCount(FOX_FUR_ID) < 10)
            htmltext = "30312-05.htm";
        else if (npcId == 30312 && st.getCond() == 1 && st.haveQuestItem(FOX_FUR_ID, 10))
            htmltext = "30312-08.htm";
        else if (npcId == 30051 && st.getCond() == 1 && st.getQuestItemsCount(FOX_FUR_ID) < 10 && st.getQuestItemsCount(FOX_FUR_ID) > 0)
            htmltext = "30051-01.htm";
        else if (npcId == 30051 && st.getCond() == 1 && st.getQuestItemsCount(FOX_FUR_ID) >= 10 && st.getQuestItemsCount(FOX_FUR_YARN_ID) == 0 && st.getQuestItemsCount(MAIDEN_DOLL_ID) == 0 && st.getQuestItemsCount(MAIDEN_DOLL_ID) < 10) {
            htmltext = "30051-02.htm";
            st.giveItems(FOX_FUR_YARN_ID, 1);
            st.takeItems(FOX_FUR_ID, st.getQuestItemsCount(FOX_FUR_ID));
        } else if (npcId == 30051 && st.getCond() == 1 && st.haveQuestItem(FOX_FUR_YARN_ID, 1))
            htmltext = "30051-03.htm";
        else if (npcId == 30051 && st.getCond() == 1 && st.getQuestItemsCount(MAIDEN_DOLL_ID) == 1)
            htmltext = "30051-04.htm";
        else if (npcId == 30312 && st.getCond() == 1 && st.haveQuestItem(FOX_FUR_YARN_ID))
            htmltext = "30312-06.htm";
        else if (npcId == 30055 && st.getCond() == 1 && st.haveQuestItem(FOX_FUR_YARN_ID)) {
            htmltext = "30055-01.htm";
            st.giveItems(MAIDEN_DOLL_ID);
            st.takeItems(FOX_FUR_YARN_ID);
        } else if (npcId == 30055 && st.getCond() == 1 && st.haveQuestItem(MAIDEN_DOLL_ID) )
            htmltext = "30055-02.htm";
        else if (npcId == 30055 && st.getCond() == 1 && st.getQuestItemsCount(FOX_FUR_YARN_ID) == 0 && st.getQuestItemsCount(MAIDEN_DOLL_ID) == 0)
            htmltext = "30055-03.htm";
        else if (npcId == 30312 && st.getCond() == 1 && st.haveQuestItem(MAIDEN_DOLL_ID))
            if (!st.isSet("id")) {
                st.set("id");
                htmltext = "30312-07.htm";
                st.takeItems(MAIDEN_DOLL_ID);
                st.giveItems(MYSTICS_EARRING_ID);
                st.addExpAndSp(1000, 0);
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.getQuestItemsCount(FOX_FUR_YARN_ID) == 0)
            st.rollAndGive(FOX_FUR_ID, 1, 1, 10, 14);
    }
}