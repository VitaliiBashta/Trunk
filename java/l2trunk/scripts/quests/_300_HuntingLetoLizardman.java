package l2trunk.scripts.quests;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _300_HuntingLetoLizardman extends Quest {
    //NPCs
    private static final int RATH = 30126;
    //items
    private static final int BRACELET_OF_LIZARDMAN = 7139;
    private static final int ANIMAL_BONE = 1872;
    private static final int ANIMAL_SKIN = 1867;
    //Chances
    private static final int BRACELET_OF_LIZARDMAN_CHANCE = 70;

    public _300_HuntingLetoLizardman() {
        super(false);
        addStartNpc(RATH);
        for (int lizardman_id = 20577; lizardman_id <= 20582; lizardman_id++)
            addKillId(lizardman_id);
        addQuestItem(BRACELET_OF_LIZARDMAN);
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        if (npc.getNpcId() != RATH)
            return htmltext;
        if (st.getState() == CREATED) {
            if (st.player.getLevel() < 34) {
                htmltext = "rarshints_q0300_0103.htm";
                st.exitCurrentQuest();
            } else {
                htmltext = "rarshints_q0300_0101.htm";
                st.setCond(0);
            }
        } else if (st.haveQuestItem(BRACELET_OF_LIZARDMAN, 60)) {
            htmltext = "rarshints_q0300_0105.htm";
        } else {
            htmltext = "rarshints_q0300_0106.htm";
            st.setCond(1);
        }
        return htmltext;
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        String htmltext = event;
        int _state = st.getState();
        if ("rarshints_q0300_0104.htm".equalsIgnoreCase(event) && _state == CREATED) {
            st.start();
            st.setCond(1);
            st.playSound(SOUND_ACCEPT);
        } else if ("rarshints_q0300_0201.htm".equalsIgnoreCase(event) && _state == STARTED)
            if (st.haveQuestItem(BRACELET_OF_LIZARDMAN, 60)) {
                st.takeItems(BRACELET_OF_LIZARDMAN);
                switch (Rnd.get(3)) {
                    case 0:
                        st.giveItems(ADENA_ID, 30000, true);
                        break;
                    case 1:
                        st.giveItems(ANIMAL_BONE, 50, true);
                        break;
                    case 2:
                        st.giveItems(ANIMAL_SKIN, 50, true);
                        break;
                }
                st.playSound(SOUND_FINISH);
                st.exitCurrentQuest();
            } else {
                htmltext = "rarshints_q0300_0202.htm";
                st.setCond(1);
            }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState qs) {
        if (qs.getState() != STARTED)
            return;

        long _count = qs.getQuestItemsCount(BRACELET_OF_LIZARDMAN);
        if (_count < 60 && Rnd.chance(BRACELET_OF_LIZARDMAN_CHANCE)) {
            qs.giveItems(BRACELET_OF_LIZARDMAN);
            if (_count == 59) {
                qs.setCond(2);
                qs.playSound(SOUND_MIDDLE);
            } else
                qs.playSound(SOUND_ITEMGET);
        }
    }
}