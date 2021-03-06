package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

public final class _176_StepsForHonor extends Quest {
    private static final int RAPIDUS = 36479;

    public _176_StepsForHonor() {
        super(PARTY_ALL);
        addStartNpc(RAPIDUS);
    }

    @Override
    public void onKill(Player killed, QuestState st) {
        int cond = st.getCond();
        if (!isValidKill(killed, st.player))
            return;
        if (cond == 1 || cond == 3 || cond == 5 || cond == 7) {
            st.inc("kill");
            if (st.getInt("kill") >= calculatePlayersToKill(cond))
                st.setCond(cond + 1);
        }
    }

    private static int calculatePlayersToKill(int cond) {
        switch (cond) {
            case 1:
                return 9;
            case 3:
                return 9 + 18;
            case 5:
                return 9 + 18 + 27;
            case 7:
                return 9 + 18 + 27 + 36;
            default:
                return 0;
        }
    }

    @Override
    public String onEvent(String event, QuestState st, NpcInstance npc) {
        if ("rapidus_q176_04.htm".equalsIgnoreCase(event)) {
            st.setCond(1);
            st.start();
            st.playSound(SOUND_ACCEPT);
        }
        return event;
    }

    @Override
    public String onTalk(NpcInstance npc, QuestState st) {
        String htmltext;
        int cond = st.getCond();
        DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
        if (runnerEvent.isInProgress())
            htmltext = "rapidus_q176_05.htm";
        else {
            switch (cond) {
                default:
                    if (st.player.getLevel() >= 80)
                        htmltext = "rapidus_q176_03.htm";
                    else {
                        htmltext = "rapidus_q176_02.htm";
                        st.exitCurrentQuest();
                    }
                    break;
                case 1:
                    htmltext = "rapidus_q176_06.htm";
                    break;
                case 2:
                    htmltext = "rapidus_q176_07.htm";
                    st.setCond(3);
                    st.playSound(SOUND_MIDDLE);
                    break;
                case 3:
                    htmltext = "rapidus_q176_08.htm";
                    break;
                case 4:
                    htmltext = "rapidus_q176_09.htm";
                    st.setCond(5);
                    st.playSound(SOUND_MIDDLE);
                    break;
                case 5:
                    htmltext = "rapidus_q176_10.htm";
                    break;
                case 6:
                    htmltext = "rapidus_q176_11.htm";
                    st.setCond(7);
                    st.playSound(SOUND_MIDDLE);
                    break;
                case 7:
                    htmltext = "rapidus_q176_12.htm";
                    break;
                case 8:
                    htmltext = "rapidus_q176_13.htm";
                    st.giveItems(14603);
                    st.finish();
                    st.playSound(SOUND_FINISH);
                    break;
            }
        }
        return htmltext;
    }

    private boolean isValidKill(Player killed, Player killer) {
        DominionSiegeEvent killedSiegeEvent = killed.getEvent(DominionSiegeEvent.class);
        DominionSiegeEvent killerSiegeEvent = killer.getEvent(DominionSiegeEvent.class);

        if (killedSiegeEvent == null || killerSiegeEvent == null)
            return false;
        if (killedSiegeEvent == killerSiegeEvent)
            return false;
        return killed.getLevel() >= 61;
    }

    @Override
    public void onCreate(QuestState qs) {
        qs.addPlayerOnKillListener();
    }

    @Override
    public void onAbort(QuestState qs) {
        qs.removePlayerOnKillListener();
    }
}