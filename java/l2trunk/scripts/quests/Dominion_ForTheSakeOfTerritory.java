package l2trunk.scripts.quests;

import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.listener.event.OnStartStopListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.listener.PlayerListenerList;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;

import java.util.List;

public abstract class Dominion_ForTheSakeOfTerritory extends Quest {
    private final OnPlayerEnterListener _onPlayerEnterListener = new OnPlayerEnterListenerImpl();
    private List<Integer> supplyBoxes = List.of(36591, 36592, 36593, 36594, 36595, 36596, 36597, 36598, 36599);
    private List<Integer> catapultas = List.of(36499, 36500, 36501, 36502, 36503, 36504, 36505, 36506, 36507);
    private List<Integer> militaryUnitLeaders = List.of(36508, 36514, 36520, 36526, 36532, 36538, 36544, 36550, 36556);
    private List<Integer> religionUnitLeaders = List.of(36510, 36516, 36522, 36528, 36534, 36540, 36546, 36552, 36558);
    private List<Integer> economicUnitLeaders = List.of(36513, 36519, 36525, 36531, 36537, 36543, 36549, 36555, 36561);

    Dominion_ForTheSakeOfTerritory() {
        super(PARTY_ALL);
        DominionSiegeEvent siegeEvent = EventHolder.getEvent(EventType.SIEGE_EVENT, getDominionId());
        siegeEvent.setForSakeQuest(this);
        siegeEvent.addListener(new OnStartStopListenerImpl());

        DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.addBreakQuest(this);

        addKillId(supplyBoxes);
        addKillId(catapultas);
        addKillId(militaryUnitLeaders);
        addKillId(religionUnitLeaders);
        addKillId(economicUnitLeaders);
    }

    protected abstract int getDominionId();

    private boolean isValidNpcKill(Player killer, NpcInstance npc) {
        DominionSiegeEvent npcSiegeEvent = npc.getEvent(DominionSiegeEvent.class);
        DominionSiegeEvent killerSiegeEvent = killer.getEvent(DominionSiegeEvent.class);

        if (npcSiegeEvent == null || killerSiegeEvent == null)
            return false;
        return npcSiegeEvent != killerSiegeEvent;
    }

    private void handleReward(QuestState st) {
        Player player = st.getPlayer();
        if (player == null)
            return;

        DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);
        if (siegeEvent != null)
            siegeEvent.addReward(player, DominionSiegeEvent.STATIC_BADGES, 10);
    }

    private boolean isUnitLeader(int npcId) {
        return militaryUnitLeaders.contains(npcId)
                || religionUnitLeaders.contains(npcId)
                || economicUnitLeaders.contains(npcId);
    }

    @Override
    public String onKill(NpcInstance npc, QuestState st) {
        if (!isValidNpcKill(st.getPlayer(), npc))
            return null;
        int npcId = npc.getNpcId();
        int condID = st.getCond();
        switch (condID) {
            case 1:
                if (catapultas.contains(npcId))
                    st.setCond(2);
                else if (supplyBoxes.contains(npcId))
                    st.setCond(3);
                else if (isUnitLeader(npcId))
                    st.setCond(4);
                break;
            case 2:
                if (supplyBoxes.contains(npcId))
                    st.setCond(5);
                else if (isUnitLeader(npcId))
                    st.setCond(6);

            case 3:
                if (catapultas.contains(npcId))
                    st.setCond(7);
                else if (isUnitLeader(npcId))
                    st.setCond(8);
                break;
            case 4:
                if (catapultas.contains(npcId))
                    st.setCond(9);
                else if (supplyBoxes.contains(npcId))
                    st.setCond(10);
                break;
            case 5:
                if (isUnitLeader(npcId)) {
                    st.setCond(11);
                    handleReward(st);
                }
                break;
            case 6: {
                if (supplyBoxes.contains(npcId)) {
                    st.setCond(11);
                    handleReward(st);
                }
            }
            case 7: {
                if (isUnitLeader(npcId)) {
                    st.setCond(11);
                    handleReward(st);
                }
            }
            case 8: {
                if (catapultas.contains(npcId)) {
                    st.setCond(11);
                    handleReward(st);
                }
            }
            case 9: {
                if (supplyBoxes.contains(npcId)) {
                    st.setCond(11);
                    handleReward(st);
                }
            }
            case 10:
                if (catapultas.contains(npcId)) {
                    st.setCond(11);
                    handleReward(st);
                }

        }
        return null;
    }

    @Override
    public boolean canAbortByPacket() {
        return false;
    }

    private class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            DominionSiegeEvent siegeEvent = player.getEvent(DominionSiegeEvent.class);
            if (siegeEvent == null || siegeEvent.getId() != getDominionId())
                return;

            QuestState questState = player.getQuestState(Dominion_ForTheSakeOfTerritory.this);
            if (player.getLevel() > 61 && questState == null) {
                questState = newQuestState(player, Quest.CREATED);
                questState.setState(Quest.STARTED);
                questState.setCond(1);
            }
        }
    }

    public class OnStartStopListenerImpl implements OnStartStopListener {
        @Override
        public void onStart(GlobalEvent event) {
            PlayerListenerList.addGlobal(_onPlayerEnterListener);
        }

        @Override
        public void onStop(GlobalEvent event) {
            PlayerListenerList.removeGlobal(_onPlayerEnterListener);
        }
    }
}
