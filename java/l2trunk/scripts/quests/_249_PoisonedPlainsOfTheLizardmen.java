package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _249_PoisonedPlainsOfTheLizardmen extends Quest {
    private static final int MOUEN = 30196;
    private static final int JOHNNY = 32744;

    public _249_PoisonedPlainsOfTheLizardmen() {
        addStartNpc(MOUEN);
        addTalkId(JOHNNY);
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {

        if (npc.getNpcId() == MOUEN) {
            if ("30196-03.htm".equalsIgnoreCase(event)) {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            }
        } else if (npc.getNpcId() == JOHNNY && "32744-03.htm".equalsIgnoreCase(event)) {
            st.unset("cond");
            st.giveAdena( 83056);
            st.addExpAndSp(477496, 58743);
            st.playSound(SOUND_FINISH);
            st.finish();
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int npcId = npc.getNpcId();
        int cond = st.getCond();
        if (npcId == MOUEN) {
            switch (st.getState()) {
                case CREATED:
                    if (st.player.getLevel() >= 82)
                        htmltext = "30196-01.htm";
                    else
                        htmltext = "30196-00.htm";
                    break;
                case STARTED:
                    if (cond == 1)
                        htmltext = "30196-04.htm";
                    break;
                case COMPLETED:
                    htmltext = "30196-05.htm";
                    break;
            }
        } else if (npcId == JOHNNY) {
            if (cond == 1)
                htmltext = "32744-01.htm";
            else if (st.isCompleted())
                htmltext = "32744-04.htm";
        }
        return htmltext;
    }
}