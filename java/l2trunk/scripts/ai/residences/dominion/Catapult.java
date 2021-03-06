package l2trunk.scripts.ai.residences.dominion;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.listener.PlayerListenerList;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.scripts.quests._729_ProtectTheTerritoryCatapult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Catapult extends DefaultAI {
    private static final Map<Integer, List<NpcString>> MESSAGES = new HashMap<>(9);

    static {
        MESSAGES.put(81, List.of(NpcString.PROTECT_THE_CATAPULT_OF_GLUDIO, NpcString.THE_CATAPULT_OF_GLUDIO_HAS_BEEN_DESTROYED));
        MESSAGES.put(82, List.of(NpcString.PROTECT_THE_CATAPULT_OF_DION, NpcString.THE_CATAPULT_OF_DION_HAS_BEEN_DESTROYED));
        MESSAGES.put(83, List.of(NpcString.PROTECT_THE_CATAPULT_OF_GIRAN, NpcString.THE_CATAPULT_OF_GIRAN_HAS_BEEN_DESTROYED));
        MESSAGES.put(84, List.of(NpcString.PROTECT_THE_CATAPULT_OF_OREN, NpcString.THE_CATAPULT_OF_OREN_HAS_BEEN_DESTROYED));
        MESSAGES.put(85, List.of(NpcString.PROTECT_THE_CATAPULT_OF_ADEN, NpcString.THE_CATAPULT_OF_ADEN_HAS_BEEN_DESTROYED));
        MESSAGES.put(86, List.of(NpcString.PROTECT_THE_CATAPULT_OF_INNADRIL, NpcString.THE_CATAPULT_OF_INNADRIL_HAS_BEEN_DESTROYED));
        MESSAGES.put(87, List.of(NpcString.PROTECT_THE_CATAPULT_OF_GODDARD, NpcString.THE_CATAPULT_OF_GODDARD_HAS_BEEN_DESTROYED));
        MESSAGES.put(88, List.of(NpcString.PROTECT_THE_CATAPULT_OF_RUNE, NpcString.THE_CATAPULT_OF_RUNE_HAS_BEEN_DESTROYED));
        MESSAGES.put(89, List.of(NpcString.PROTECT_THE_CATAPULT_OF_SCHUTTGART, NpcString.THE_CATAPULT_OF_SCHUTTGART_HAS_BEEN_DESTROYED));
    }

    private final OnPlayerEnterListener _listener = new OnPlayerEnterListenerImpl();

    public Catapult(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return false;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int dam) {
        NpcInstance actor = getActor();

        DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null)
            return;

        boolean first = actor.getParameter("dominion_first_attack", true);
        if (first) {
            actor.setParameter("dominion_first_attack", false);
            NpcString msg = MESSAGES.get(siegeEvent.getId()).get(0);
            Quest q = QuestManager.getQuest(_729_ProtectTheTerritoryCatapult.class);
            GameObjectsStorage.getAllPlayersStream()
                    .filter(p -> p.getEvent(DominionSiegeEvent.class) == siegeEvent)
                    .forEach(p -> {
                        p.sendPacket(new ExShowScreenMessage(msg));
                        QuestState questState = p.getQuestState(_729_ProtectTheTerritoryCatapult.class);
                        if (questState == null) {
                            questState = q.newQuestStateAndNotSave(p, Quest.CREATED);
                            questState.setCond(1, false);
                            questState.setStateAndNotSave(Quest.STARTED);
                        }
                    });
        }
    }

    @Override
    public void onEvtAggression(Creature attacker, int d) {
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();
        super.onEvtDead(killer);
        DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null)
            return;

        NpcString msg = MESSAGES.get(siegeEvent.getId()).get(1);
        GameObjectsStorage.getAllPlayersStream()
                .filter(player -> player.getEvent(DominionSiegeEvent.class) == siegeEvent)
                .forEach(player -> {
                    player.sendPacket(new ExShowScreenMessage(msg));
                    QuestState questState = player.getQuestState(_729_ProtectTheTerritoryCatapult.class);
                    if (questState != null)
                        questState.abortQuest();
                });

        siegeEvent.doorAction(DominionSiegeEvent.CATAPULT_DOORS, true);

        Player player = killer.getPlayer();
        if (player == null)
            return;

        if (player.getParty() == null) {
            DominionSiegeEvent siegeEvent2 = player.getEvent(DominionSiegeEvent.class);
            if (siegeEvent2 == null || siegeEvent2 == siegeEvent)
                return;
            siegeEvent2.addReward(player, DominionSiegeEvent.STATIC_BADGES, 15);
        } else {
            for (Player $member : player.getParty()) {
                if ($member.isInRange(player, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                    DominionSiegeEvent siegeEvent2 = $member.getEvent(DominionSiegeEvent.class);
                    if (siegeEvent2 == null || siegeEvent2 == siegeEvent)
                        continue;
                    siegeEvent2.addReward($member, DominionSiegeEvent.STATIC_BADGES, 15);
                }
            }
        }
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        getActor().setParameter("dominion_first_attack", true);

        PlayerListenerList.addGlobal(_listener);
    }

    @Override
    public void onEvtDeSpawn() {
        super.onEvtDeSpawn();

        PlayerListenerList.removeGlobal(_listener);
    }

    private class OnPlayerEnterListenerImpl implements OnPlayerEnterListener {
        @Override
        public void onPlayerEnter(Player player) {
            NpcInstance actor = getActor();
            DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
            if (siegeEvent == null)
                return;

            if (player.getEvent(DominionSiegeEvent.class) != siegeEvent)
                return;

            Quest q = QuestManager.getQuest(_729_ProtectTheTerritoryCatapult.class);

            QuestState questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
            questState.setCond(1, false);
            questState.setStateAndNotSave(Quest.STARTED);
        }
    }
}
