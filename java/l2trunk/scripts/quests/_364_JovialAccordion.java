package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;

public final class _364_JovialAccordion extends Quest {
    //NPCs
    private static final int BARBADO = 30959;
    private static final int SWAN = 30957;
    private static final int SABRIN = 30060;
    private static final int BEER_CHEST = 30960;
    private static final int CLOTH_CHEST = 30961;
    //Items
    private static final int KEY_1 = 4323;
    private static final int KEY_2 = 4324;
    private static final int BEER = 4321;
    private static final int ECHO = 4421;

    public _364_JovialAccordion() {
        super(false);
        addStartNpc(BARBADO);
        addTalkId(SWAN);
        addTalkId(SABRIN);
        addTalkId(BEER_CHEST);
        addTalkId(CLOTH_CHEST);
        addQuestItem(KEY_1);
        addQuestItem(KEY_2);
        addQuestItem(BEER);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        if (st.getState() == CREATED) {
            if (npcId != BARBADO)
                return htmltext;
            st.setCond(0);
            st.set("ok", 0);
        }

        int cond = st.getCond();
        if (npcId == BARBADO) {
            if (cond == 0)
                htmltext = "30959-01.htm";
            else if (cond == 3) {
                htmltext = "30959-03.htm";
                st.giveItems(ECHO);
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest(true);
            } else if (cond > 0)
                htmltext = "30959-02.htm";
        } else if (npcId == SWAN) {
            if (cond == 1)
                htmltext = "30957-01.htm";
            else if (cond == 3)
                htmltext = "30957-05.htm";
            else if (cond == 2)
                if (st.getInt("ok") == 1 && st.getQuestItemsCount(KEY_1) == 0) {
                    st.setCond(3);
                    htmltext = "30957-04.htm";
                } else
                    htmltext = "30957-03.htm";
        } else if (npcId == SABRIN && cond == 2 && st.haveQuestItem(BEER)) {
            st.set("ok", 1);
            st.takeItems(BEER);
            htmltext = "30060-01.htm";
        } else if (npcId == BEER_CHEST && cond == 2)
            htmltext = "30960-01.htm";
        else if (npcId == CLOTH_CHEST && cond == 2)
            htmltext = "30961-01.htm";

        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int _state = st.getState();
        int cond = st.getCond();
        if ("30959-02.htm".equalsIgnoreCase(event) && _state == CREATED && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        } else if ("30957-02.htm".equalsIgnoreCase(event) && _state == STARTED && cond == 1) {
            st.setCond(2);
            st.giveItems(KEY_1);
            st.giveItems(KEY_2);
        } else if ("30960-03.htm".equalsIgnoreCase(event) && cond == 2 && st.haveQuestItem(KEY_2) ) {
            st.takeItems(KEY_2);
            st.giveItems(BEER);
            htmltext = "30960-02.htm";
        } else if ("30961-03.htm".equalsIgnoreCase(event) && cond == 2 && st.haveQuestItem(KEY_1) ) {
            st.takeItems(KEY_1);
            htmltext = "30961-02.htm";
        }
        return htmltext;
    }
}
