package l2trunk.scripts.ai.residences.dominion;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.listener.PlayerListenerList;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.scripts.quests._730_ProtectTheSuppliesSafe;

import java.util.HashMap;
import java.util.Map;

public final class SuppliesSafe extends DefaultAI {
    private static final Map<Integer, NpcString[]> MESSAGES = new HashMap<>(9);

    static {
        MESSAGES.put(81, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_GLUDIO, NpcString.THE_SUPPLIES_SAFE_OF_GLUDIO_HAS_BEEN_DESTROYED});
        MESSAGES.put(82, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_DION, NpcString.THE_SUPPLIES_SAFE_OF_DION_HAS_BEEN_DESTROYED});
        MESSAGES.put(83, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_GIRAN, NpcString.THE_SUPPLIES_SAFE_OF_GIRAN_HAS_BEEN_DESTROYED});
        MESSAGES.put(84, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_OREN, NpcString.THE_SUPPLIES_SAFE_OF_OREN_HAS_BEEN_DESTROYED});
        MESSAGES.put(85, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_ADEN, NpcString.THE_SUPPLIES_SAFE_OF_ADEN_HAS_BEEN_DESTROYED});
        MESSAGES.put(86, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_INNADRIL, NpcString.THE_SUPPLIES_SAFE_OF_INNADRIL_HAS_BEEN_DESTROYED});
        MESSAGES.put(87, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_GODDARD, NpcString.THE_SUPPLIES_SAFE_OF_GODDARD_HAS_BEEN_DESTROYED});
        MESSAGES.put(88, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_RUNE, NpcString.THE_SUPPLIES_SAFE_OF_RUNE_HAS_BEEN_DESTROYED});
        MESSAGES.put(89, new NpcString[]{NpcString.PROTECT_THE_SUPPLIES_SAFE_OF_SCHUTTGART, NpcString.THE_SUPPLIES_SAFE_OF_SCHUTTGART_HAS_BEEN_DESTROYED});
    }

    private final OnPlayerEnterListener _listener = new OnPlayerEnterListenerImpl();

    public SuppliesSafe(NpcInstance actor) {
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
            NpcString msg = MESSAGES.get(siegeEvent.getId())[0];
            Quest q = QuestManager.getQuest(_730_ProtectTheSuppliesSafe.class);
            GameObjectsStorage.getAllPlayersStream()
                    .filter(player -> player.getEvent(DominionSiegeEvent.class) == siegeEvent)
                    .forEach(player -> {
                        player.sendPacket(new ExShowScreenMessage(msg));
                        QuestState questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
                        questState.setCond(1, false);
                        questState.setStateAndNotSave(Quest.STARTED);
                    });
            PlayerListenerList.addGlobal(_listener);
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

        NpcString msg = MESSAGES.get(siegeEvent.getId())[1];
        GameObjectsStorage.getAllPlayersStream()
                .filter(p -> p.getEvent(DominionSiegeEvent.class) == siegeEvent)
                .forEach(p -> {
                    p.sendPacket(new ExShowScreenMessage(msg));
                    QuestState questState = p.getQuestState(_730_ProtectTheSuppliesSafe.class);
                    if (questState != null)
                        questState.abortQuest();
                });

        if (!(killer instanceof Playable))
            return;
        Player player = ((Playable)killer).getPlayer();

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

            Quest q = QuestManager.getQuest(_730_ProtectTheSuppliesSafe.class);

            QuestState questState = q.newQuestStateAndNotSave(player, Quest.CREATED);
            questState.setCond(1, false);
            questState.setStateAndNotSave(Quest.STARTED);
        }
    }
}
