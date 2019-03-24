package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _295_DreamsOfTheSkies extends Quest {
    private static final int FLOATING_STONE = 1492;
    private static final int RING_OF_FIREFLY = 1509;

    private static final int Arin = 30536;
    private static final int MagicalWeaver = 20153;

    public _295_DreamsOfTheSkies() {
        super(false);

        addStartNpc(Arin);
        addKillId(MagicalWeaver);

        addQuestItem(FLOATING_STONE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("elder_arin_q0295_03.htm".equalsIgnoreCase(event)) {
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

        if (id == CREATED)
            st.setCond(0);
        int cond = st.getCond();
        if (cond == 0) {
            if (st.player.getLevel() >= 11) {
                htmltext = "elder_arin_q0295_02.htm";
                return htmltext;
            }
            htmltext = "elder_arin_q0295_01.htm";
            st.exitCurrentQuest();
        } else if (cond == 1 || st.getQuestItemsCount(FLOATING_STONE) < 50)
            htmltext = "elder_arin_q0295_04.htm";
        else if (cond == 2 && st.getQuestItemsCount(FLOATING_STONE) == 50) {
            st.addExpAndSp(0, 500);
            st.playSound(SOUND_FINISH);
            st.exitCurrentQuest();
            if (!st.haveQuestItem(RING_OF_FIREFLY)) {
                htmltext = "elder_arin_q0295_05.htm";
                st.giveItems(RING_OF_FIREFLY);
            } else {
                htmltext = "elder_arin_q0295_06.htm";
                st.giveAdena( 2400);
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getCond() == 1 && st.getQuestItemsCount(FLOATING_STONE) < 50)
            if (Rnd.chance(25)) {
                st.giveItems(FLOATING_STONE);
                if (st.getQuestItemsCount(FLOATING_STONE) == 50) {
                    st.playSound(SOUND_MIDDLE);
                    st.setCond(2);
                } else
                    st.playSound(SOUND_ITEMGET);
            } else if (st.getQuestItemsCount(FLOATING_STONE) >= 48) {
                st.giveItems(FLOATING_STONE, 50 - st.getQuestItemsCount(FLOATING_STONE));
                st.playSound(SOUND_MIDDLE);
                st.setCond(2);
            } else {
                st.giveItems(FLOATING_STONE, 2);
                st.playSound(SOUND_ITEMGET);
            }
    }
}