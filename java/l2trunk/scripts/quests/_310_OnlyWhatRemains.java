package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public final class _310_OnlyWhatRemains extends Quest {
    // NPC's
    private static final int KINTAIJIN = 32640;
    // MOBS's
    private static final List<Integer> MOBS = List.of(
            22617, 22624, 22625, 22626);
    // ITEMS's
    private static final int DIRTYBEAD = 14880;
    private static final int ACCELERATOR = 14832;
    private static final int JEWEL = 14835;

    public _310_OnlyWhatRemains() {
        addStartNpc(KINTAIJIN);
        addKillId(MOBS);
        addQuestItem(DIRTYBEAD);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("32640-3.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int id = st.getState();
        int cond = st.getCond();
        if (id == COMPLETED)
            htmltext = "32640-10.htm";
        else if (id == CREATED) {
            if (st.player.getLevel() >= 81 && st.player.isQuestCompleted(_240_ImTheOnlyOneYouCanTrust.class))
                htmltext = "32640-1.htm";
            else {
                htmltext = "32640-0.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1)
            htmltext = "32640-8.htm";
        else if (cond == 2) {
            st.takeItems(DIRTYBEAD, 500);
            st.giveItems(ACCELERATOR);
            st.giveItems(JEWEL);
            st.exitCurrentQuest();
            st.playSound(SOUND_FINISH);
            htmltext = "32640-9.htm";
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1) {
            st.giveItems(DIRTYBEAD);
            if (st.getQuestItemsCount(DIRTYBEAD) >= 500) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            } else
                st.playSound(SOUND_ITEMGET);
        }
    }
}