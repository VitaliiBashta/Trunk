package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10501_CapeEmbroideredSoulOne extends Quest {
    // NPC's
    private static final int OLF_ADAMS = 32612;
    // mob's
    private static final int ZAKEN_HIGH = 29181;
    // Quest Item's
    private static final int SOUL_ZAKEN = 21722;
    // Item's
    private static final int CLOAK_OF_ZAKEN = 21719;

    public _10501_CapeEmbroideredSoulOne() {
        super(PARTY_ALL);
        addStartNpc(OLF_ADAMS);
        addTalkId(OLF_ADAMS);
        addKillId(ZAKEN_HIGH);
        addQuestItem(SOUL_ZAKEN);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("olf_adams_q10501_02.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 78)
                htmltext = "olf_adams_q10501_01.htm";
            else {
                htmltext = "olf_adams_q10501_00.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1)
            htmltext = "olf_adams_q10501_03.htm";
        else if (cond == 2)
            if (st.haveQuestItem(SOUL_ZAKEN, 20)) {
                st.takeItems(SOUL_ZAKEN);
                st.giveItems(CLOAK_OF_ZAKEN);
                st.playSound(SOUND_FINISH);
                htmltext = "olf_adams_q10501_04.htm";
                st.finish();
            } else {
                st.setCond(1);
                htmltext = "olf_adams_q10501_03.htm";
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == ZAKEN_HIGH) {
            if (!st.haveQuestItem(SOUL_ZAKEN, 20))
                st.giveItems(SOUL_ZAKEN, Rnd.get(1, 3), false);
            if (st.haveQuestItem(SOUL_ZAKEN, 20)) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
        }
    }
}