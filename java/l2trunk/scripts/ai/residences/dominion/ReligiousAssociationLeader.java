package l2trunk.scripts.ai.residences.dominion;

import l2trunk.gameserver.Config;
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
import l2trunk.scripts.ai.residences.SiegeGuardFighter;
import l2trunk.scripts.quests._732_ProtectTheReligiousAssociationLeader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class ReligiousAssociationLeader extends SiegeGuardFighter {
    private static final Map<Integer, List<NpcString>> MESSAGES = new HashMap<>(9);

    static {
        MESSAGES.put(81, List.of(NpcString.PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GLUDIO, NpcString.THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GLUDIO_IS_DEAD));
        MESSAGES.put(82, List.of(NpcString.PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_DION, NpcString.THE_RELIGIOUS_ASSOCIATION_LEADER_OF_DION_IS_DEAD));
        MESSAGES.put(83, List.of(NpcString.PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GIRAN, NpcString.THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GIRAN_IS_DEAD));
        MESSAGES.put(84, List.of(NpcString.PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_OREN, NpcString.THE_RELIGIOUS_ASSOCIATION_LEADER_OF_OREN_IS_DEAD));
        MESSAGES.put(85, List.of(NpcString.PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_ADEN, NpcString.THE_RELIGIOUS_ASSOCIATION_LEADER_OF_ADEN_IS_DEAD));
        MESSAGES.put(86, List.of(NpcString.PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_INNADRIL, NpcString.THE_RELIGIOUS_ASSOCIATION_LEADER_OF_INNADRIL_IS_DEAD));
        MESSAGES.put(87, List.of(NpcString.PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GODDARD, NpcString.THE_RELIGIOUS_ASSOCIATION_LEADER_OF_GODDARD_IS_DEAD));
        MESSAGES.put(88, List.of(NpcString.PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_RUNE, NpcString.THE_RELIGIOUS_ASSOCIATION_LEADER_OF_RUNE_IS_DEAD));
        MESSAGES.put(89, List.of(NpcString.PROTECT_THE_RELIGIOUS_ASSOCIATION_LEADER_OF_SCHUTTGART, NpcString.THE_RELIGIOUS_ASSOCIATION_LEADER_OF_SCHUTTGART_IS_DEAD));
    }

    private final OnPlayerEnterListener listener = new OnPlayerEnterListenerImpl();

    public ReligiousAssociationLeader(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int dam) {
        super.onEvtAttacked(attacker, dam);

        NpcInstance actor = getActor();

        DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null)
            return;

        boolean first = actor.getParameter("dominion_first_attack", true);
        if (first) {
            actor.setParameter("dominion_first_attack", false);
            NpcString msg = MESSAGES.get(siegeEvent.getId()).get(0);
            Quest q = QuestManager.getQuest(_732_ProtectTheReligiousAssociationLeader.class);
            GameObjectsStorage.getAllPlayersStream()
                    .filter(player -> player.getEvent(DominionSiegeEvent.class) == siegeEvent)
                    .forEach(player -> {
                        player.sendPacket(new ExShowScreenMessage(msg));
                        QuestState questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
                        questState.setCond(1, false);
                        questState.setStateAndNotSave(Quest.STARTED);
                    });
            PlayerListenerList.addGlobal(listener);
        }
    }

    @Override
    public void onEvtDead(Creature killer) {
        super.onEvtDead(killer);

        NpcInstance actor = getActor();

        DominionSiegeEvent siegeEvent = actor.getEvent(DominionSiegeEvent.class);
        if (siegeEvent == null)
            return;

        NpcString msg = MESSAGES.get(siegeEvent.getId()).get(1);
        GameObjectsStorage.getAllPlayersStream()
                .filter(player -> player.getEvent(DominionSiegeEvent.class) == siegeEvent)
                .forEach(player -> {
                    player.sendPacket(new ExShowScreenMessage(msg));

                    QuestState questState = player.getQuestState(_732_ProtectTheReligiousAssociationLeader.class);
                    if (questState != null)
                        questState.abortQuest();
                });

        Player player = killer.getPlayer();
        if (player == null)
            return;

        if (player.getParty() == null) {
            DominionSiegeEvent siegeEvent2 = player.getEvent(DominionSiegeEvent.class);
            if (siegeEvent2 == null || siegeEvent2 == siegeEvent)
                return;
            siegeEvent2.addReward(player, DominionSiegeEvent.STATIC_BADGES, 5);
        } else {
            for (Player $member : player.getParty()) {
                if ($member.isInRange(player, Config.ALT_PARTY_DISTRIBUTION_RANGE)) {
                    DominionSiegeEvent siegeEvent2 = $member.getEvent(DominionSiegeEvent.class);
                    if (siegeEvent2 == null || siegeEvent2 == siegeEvent)
                        continue;
                    siegeEvent2.addReward($member, DominionSiegeEvent.STATIC_BADGES, 5);
                }
            }
        }
    }

    @Override
    public void onEvtDeSpawn() {
        PlayerListenerList.removeGlobal(listener);
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

            Quest q = QuestManager.getQuest(_732_ProtectTheReligiousAssociationLeader.class);

            QuestState questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
            questState.setCond(1, false);
            questState.setStateAndNotSave(Quest.STARTED);
        }
    }
}