package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

/**
 * User: Keiichi
 * Date: 06.10.2008
 * Time: 11:31:36
 * Info: Один из 2х квестов для прохода на остров Hellbound.
 * Info: Пройдя его ведьма Galate открывает ТП до Beleth's stronghold on Hellbound Island
 */
public final class _133_ThatsBloodyHot extends Quest {
    // NPC's
    private static final int KANIS = 32264;
    private static final int GALATE = 32292;
    // ITEMS
    private static final int CRYSTAL_SAMPLE = 9785;

    public _133_ThatsBloodyHot() {
        super(false);

        addStartNpc(KANIS);
        addTalkId(KANIS);
        addTalkId(GALATE);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        int cond = st.getCond();

        if (event.equals("priest_kanis_q0133_04.htm") && cond == 0) {
            st.setCond(1);
            st.setState(STARTED);
            st.playSound(SOUND_ACCEPT);
        }

        if (event.equals("priest_kanis_q0133_12.htm") && cond == 1) {
            st.setCond(2);
            st.giveItems(CRYSTAL_SAMPLE, 1);
        }

        if (event.equals("Galate_q0133_06.htm") && cond == 2) {
            st.playSound(SOUND_FINISH);
            st.takeItems(CRYSTAL_SAMPLE, -1);
            st.giveItems(ADENA_ID, 254247);
            st.addExpAndSp(331457, 32524);
            st.exitCurrentQuest(false);
        }

        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int id = st.getState();
        int cond = st.getCond();

        if (npcId == KANIS) {
            if (cond == 0) {
                //_131_BirdInACage
                QuestState BirdInCage = st.player.getQuestState(_131_BirdInACage.class);
                if (BirdInCage != null) {
                    if (st.player.isQuestCompleted(_131_BirdInACage.class)) {
                        if (st.player.getLevel() >= 78)
                            htmltext = "priest_kanis_q0133_01.htm";
                    } else
                        htmltext = "priest_kanis_q0133_03.htm";
                    st.exitCurrentQuest(true);
                }
            }
        } else if (id == STARTED)
            if (npcId == GALATE)
                if (cond == 2)
                    htmltext = "Galate_q0133_02.htm";

        return htmltext;
    }
}
