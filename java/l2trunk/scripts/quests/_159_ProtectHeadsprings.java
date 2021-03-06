package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _159_ProtectHeadsprings extends Quest {
    private final int PLAGUE_DUST_ID = 1035;
    private final int HYACINTH_CHARM1_ID = 1071;
    private final int HYACINTH_CHARM2_ID = 1072;

    public _159_ProtectHeadsprings() {
        addStartNpc(30154);

        addKillId(27017);

        addQuestItem(PLAGUE_DUST_ID, HYACINTH_CHARM1_ID, HYACINTH_CHARM2_ID);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        if (event.equals("1")) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
            if (!st.haveQuestItem(HYACINTH_CHARM1_ID) ) {
                st.giveItems(HYACINTH_CHARM1_ID);
                htmltext = "30154-04.htm";
            }
        }
        return htmltext;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getRace() != Race.elf) {
                htmltext = "30154-00.htm";
                st.exitCurrentQuest();
            } else if (st.player.getLevel() >= 12) {
                htmltext = "30154-03.htm";
                return htmltext;
            } else {
                htmltext = "30154-02.htm";
                st.exitCurrentQuest();
            }
        } else if (cond == 1)
            htmltext = "30154-05.htm";
        else if (cond == 2) {
            st.takeAllItems(PLAGUE_DUST_ID,HYACINTH_CHARM1_ID);
            st.giveItems(HYACINTH_CHARM2_ID);
            st.setCond(3);
            htmltext = "30154-06.htm";
        } else if (cond == 3)
            htmltext = "30154-07.htm";
        else if (cond == 4) {
            st.takeAllItems(PLAGUE_DUST_ID, HYACINTH_CHARM2_ID);
            st.giveAdena( 18250);
            st.playSound(SOUND_FINISH);
            htmltext = "30154-08.htm";
            st.finish();
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        int cond = st.getCond();

        if (cond == 1 && Rnd.chance(60)) {
            st.giveItems(PLAGUE_DUST_ID);
            st.setCond(2);
            st.playSound(SOUND_MIDDLE);
        } else if (cond == 3 && Rnd.chance(60))
            if (st.getQuestItemsCount(PLAGUE_DUST_ID) == 4) {
                st.giveItems(PLAGUE_DUST_ID);
                st.setCond(4);
                st.playSound(SOUND_MIDDLE);
            } else {
                st.giveItems(PLAGUE_DUST_ID);
                st.playSound(SOUND_ITEMGET);
            }
    }
}