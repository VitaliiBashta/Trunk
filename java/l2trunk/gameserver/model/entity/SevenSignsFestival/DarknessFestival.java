package l2trunk.gameserver.model.entity.SevenSignsFestival;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.instances.FestivalMonsterInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcLocation;

import java.util.List;
import java.util.concurrent.Future;

public final class DarknessFestival extends Reflection {
    private static final int FESTIVAL_LENGTH = 1080000; // 18 mins
    private static final int FESTIVAL_FIRST_SPAWN = 60000; // 1 min
    private static final int FESTIVAL_SECOND_SPAWN = 540000; // 9 mins
    private static final int FESTIVAL_CHEST_SPAWN = 900000; // 15 mins
    private final int _levelRange;
    private final int _cabal;
    private int currentState = 0;
    private boolean _challengeIncreased = false;
    private Future<?> _spawnTimerTask;

    public DarknessFestival(Party party, int cabal, int level) {
        super();
        onCreate();
        setName("Darkness Festival");
        setParty(party);
        _levelRange = level;
        _cabal = cabal;
        startCollapseTimer(FESTIVAL_LENGTH + FESTIVAL_FIRST_SPAWN);

        FestivalSpawn _witchSpawn;
        FestivalSpawn _startLocation;
        if (cabal == SevenSigns.CABAL_DAWN) {
            _witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_WITCH_SPAWNS.get(_levelRange));
            _startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DAWN_PLAYER_SPAWNS.get(_levelRange));
        } else {
            _witchSpawn = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_WITCH_SPAWNS.get(_levelRange));
            _startLocation = new FestivalSpawn(FestivalSpawn.FESTIVAL_DUSK_PLAYER_SPAWNS.get(_levelRange));
        }

        party.setReflection(this);
        setReturnLoc(party.getLeader().getLoc());
        for (Player p : party.getMembers()) {
            p.setVar("backCoords", p.getLoc().toXYZString(), -1);
            p.getEffectList().stopAllEffects();
            p.teleToLocation(Location.findPointToStay(_startLocation.loc, 20, 100, getGeoIndex()), this);
        }

        scheduleNext();
        // Spawn the festival witch for this arena
        SimpleSpawner npcSpawn = (SimpleSpawner) new SimpleSpawner(_witchSpawn.npcId)
                .setLoc(_witchSpawn.loc)
                .setReflection(this);
        npcSpawn.doSpawn(true);
        addSpawn(npcSpawn);
        sendMessageToParticipants("The festival will begin in 1 minute.");
    }

    private void scheduleNext() {
        switch (currentState) {
            case 0:
                currentState = FESTIVAL_FIRST_SPAWN;

                _spawnTimerTask = ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() {
                        spawnFestivalMonsters(0);
                        sendMessageToParticipants("Go!");
                        scheduleNext();
                    }
                }, FESTIVAL_FIRST_SPAWN);
                break;
            case FESTIVAL_FIRST_SPAWN:
                currentState = FESTIVAL_SECOND_SPAWN;

                _spawnTimerTask = ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() {
                        spawnFestivalMonsters(2);
                        sendMessageToParticipants("Next wave arrived!");
                        scheduleNext();
                    }
                }, FESTIVAL_SECOND_SPAWN - FESTIVAL_FIRST_SPAWN);
                break;
            case FESTIVAL_SECOND_SPAWN:
                currentState = FESTIVAL_CHEST_SPAWN;

                _spawnTimerTask = ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() {
                        spawnFestivalMonsters(3);
                        sendMessageToParticipants("The chests have spawned! Be quick, the festival will end soon.");
                    }
                }, FESTIVAL_CHEST_SPAWN - FESTIVAL_SECOND_SPAWN);
                break;
        }
    }

    private void spawnFestivalMonsters(int spawnType) {
        List<NpcLocation> spawns = null;
        switch (spawnType) {
            case 0:
            case 1:
                spawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_PRIMARY_SPAWNS.get(_levelRange) : FestivalSpawn.FESTIVAL_DUSK_PRIMARY_SPAWNS.get(_levelRange);
                break;
            case 2:
                spawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_SECONDARY_SPAWNS.get(_levelRange) : FestivalSpawn.FESTIVAL_DUSK_SECONDARY_SPAWNS.get(_levelRange);
                break;
            case 3:
                spawns = _cabal == SevenSigns.CABAL_DAWN ? FestivalSpawn.FESTIVAL_DAWN_CHEST_SPAWNS.get(_levelRange) : FestivalSpawn.FESTIVAL_DUSK_CHEST_SPAWNS.get(_levelRange);
                break;
        }

        if (spawns != null)
            spawns.forEach(element -> {
                FestivalSpawn currSpawn = new FestivalSpawn(element);

                SimpleSpawner npcSpawn = new SimpleSpawner(currSpawn.npcId);
                npcSpawn.setReflection(this);
                npcSpawn.setLoc(currSpawn.loc)
                        .setAmount(1)
                        .setRespawnDelay(FestivalSpawn.FESTIVAL_DEFAULT_RESPAWN)
                        .startRespawn();
                FestivalMonsterInstance festivalMob = (FestivalMonsterInstance) npcSpawn.doSpawn(true);
                // Set the offering bonus to 2x or 5x the amount per kill, if this spawn is part of an increased challenge or is a festival chest.
                if (spawnType == 1)
                    festivalMob.setOfferingBonus(2);
                else if (spawnType == 3)
                    festivalMob.setOfferingBonus(5);
                addSpawn(npcSpawn);
            });
    }

    public boolean increaseChallenge() {
        if (_challengeIncreased)
            return false;
        // Set this flag to true to make sure that this can only be done once.
        _challengeIncreased = true;
        // Spawn more festival monsters, but this time with a twist.
        spawnFestivalMonsters(1);
        return true;
    }

    @Override
    public void collapse() {
        if (isCollapseStarted())
            return;

        if (_spawnTimerTask != null) {
            _spawnTimerTask.cancel(false);
            _spawnTimerTask = null;
        }

        if (SevenSigns.INSTANCE.getCurrentPeriod() == SevenSigns.PERIOD_COMPETITION && getParty() != null) {
            Player player = getParty().getLeader();
            ItemInstance bloodOfferings = player.getInventory().getItemByItemId(SevenSignsFestival.FESTIVAL_BLOOD_OFFERING);
            long offeringCount = bloodOfferings == null ? 0 : bloodOfferings.getCount();
            // Check if the player collected any blood offerings during the festival.
            if (player.getInventory().destroyItem(bloodOfferings, "DarknessFestival")) {
                long offeringScore = offeringCount * SevenSignsFestival.FESTIVAL_OFFERING_VALUE;
                boolean isHighestScore = SevenSignsFestival.INSTANCE.setFinalScore(getParty(), _cabal, _levelRange, offeringScore);
                // Send message that the contribution score has increased.
                player.sendPacket(new SystemMessage(SystemMessage.YOUR_CONTRIBUTION_SCORE_IS_INCREASED_BY_S1).addNumber(offeringScore));

                sendCustomMessageToParticipants();
                if (isHighestScore)
                    sendMessageToParticipants("Your score is highest!");
            } else
                player.sendMessage(new CustomMessage("l2trunk.gameserver.model.instances.L2FestivalGuideInstance.BloodOfferings", player));
        }

        super.collapse();
    }

    private void sendMessageToParticipants(String s) {
        getPlayers().forEach(p -> p.sendMessage(s));
    }

    private void sendCustomMessageToParticipants() {
        getPlayers().forEach(p -> p.sendMessage(new CustomMessage("l2trunk.gameserver.model.entity.SevenSignsFestival.Ended", p)));
    }

    public void partyMemberExited() {
        if (getParty() == null || getParty().size() <= 1)
            collapse();
    }

    @Override
    public boolean canChampions() {
        return true;
    }

    @Override
    public boolean isAutolootForced() {
        return true;
    }
}