package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _10503_CapeEmbroideredSoulThree extends Quest {
    // NPC's
    private static final int OLF_ADAMS = 32612;
    // mob's
    private static final int FRINTEZZA = 29047;
    // Quest Item's
    private static final int SOUL_FRINTEZZA = 21724;
    // Item's
    private static final int CLOAK_FRINTEZZA = 21721;

    public _10503_CapeEmbroideredSoulThree() {
        super(PARTY_ALL);
        addStartNpc(OLF_ADAMS);
        addTalkId(OLF_ADAMS);
        addKillId(FRINTEZZA);
        addQuestItem(SOUL_FRINTEZZA);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if (event.equalsIgnoreCase("olf_adams_q10503_02.htm")) {
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
            if (st.player.getLevel() >= 80)
                htmltext = "olf_adams_q10503_01.htm";
            else {
                htmltext = "olf_adams_q10503_00.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1)
            htmltext = "olf_adams_q10503_03.htm";
        else if (cond == 2)
            if (st.getQuestItemsCount(SOUL_FRINTEZZA) < 20) {
                st.setCond(1);
                htmltext = "olf_adams_q10503_03.htm";
            } else {
                st.takeItems(SOUL_FRINTEZZA);
                st.giveItems(CLOAK_FRINTEZZA);
                st.playSound(SOUND_FINISH);
                htmltext = "olf_adams_q10503_04.htm";
                st.finish();
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (cond == 1 && npcId == FRINTEZZA) {
            if (st.getQuestItemsCount(SOUL_FRINTEZZA) < 20)
                st.giveItems(SOUL_FRINTEZZA, Rnd.get(1, 3), false);
            if (st.getQuestItemsCount(SOUL_FRINTEZZA) >= 20) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
        }
    }
}