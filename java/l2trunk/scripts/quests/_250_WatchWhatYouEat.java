package l2trunk.scripts.quests;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.Map;

public final class _250_WatchWhatYouEat extends Quest {
    // NPCs
    private static final int SALLY = 32743;
    // Mobs - items
    private static final Map<Integer, Integer> MOBS = Map.of(
            18864, 15493,
            18865, 15494,
            18868, 15495);

    public _250_WatchWhatYouEat() {
        super(false);
        addStartNpc(SALLY);
        addKillId(MOBS.keySet());
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {

        if (npc.getNpcId() == SALLY) {
            if ("32743-03.htm".equalsIgnoreCase(event)) {
                st.start();
                st.setCond(1);
                st.playSound(SOUND_ACCEPT);
            } else if ("32743-end.htm".equalsIgnoreCase(event)) {
                st.unset("cond");
                st.giveAdena(135661);
                st.addExpAndSp(698334, 76369);
                st.playSound(SOUND_FINISH);
                st.finish();
            }
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext = "noquest";
        int cond = st.getCond();
        if (npc.getNpcId() == SALLY) {
            switch (st.getState()) {
                case CREATED:
                    if (st.player.getLevel() >= 82)
                        htmltext = "32743-01.htm";
                    else
                        htmltext = "32743-00.htm";
                    break;
                case STARTED:
                    if (cond == 1) {
                        htmltext = "32743-04.htm";
                    } else if (cond == 2) {
                        if (st.haveAllQuestItems(MOBS.values())) {
                            htmltext = "32743-05.htm";
                            st.takeAllItems(MOBS.values());
                        } else
                            htmltext = "32743-06.htm";
                    }
                    break;
                case COMPLETED:
                    htmltext = "32743-done.htm";
                    break;
            }
        }
        return htmltext;
    }

    @Override
    public void onKill(NpcInstance npc, QuestState st) {
        if (st.getState() == STARTED && st.getCond() == 1) {
            if (MOBS.containsKey(npc.getNpcId())) {
                st.giveItemIfNotHave(MOBS.get(npc.getNpcId()));
            }
            if (st.haveAllQuestItems(MOBS.values())) {
                st.setCond(2);
                st.playSound(SOUND_MIDDLE);
            }
        }
    }
}
