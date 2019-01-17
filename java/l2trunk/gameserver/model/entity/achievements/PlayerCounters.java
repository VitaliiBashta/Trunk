package l2trunk.gameserver.model.entity.achievements;

import l2trunk.gameserver.model.Player;

public final class PlayerCounters {
    // Player
    public int pvpKills = 0;
    public int pkInARowKills = 0;
    public int highestKarma = 0;
    public int timesDied = 0;
    public int playersRessurected = 0;
    public int duelsWon = 0;
    public int fameAcquired = 0;
    public long expAcquired = 0;
    public int manorSeedsSow = 0;
    public int critsDone = 0;
    public int mcritsDone = 0;
    public int maxSoulCrystalLevel = 0;
    public int fishCaught = 0;
    public int treasureBoxesOpened = 0;
    public long adenaDestroyed = 0;
    public int recommendsMade = 0;
    public int foundationItemsMade = 0;
    public long distanceWalked = 0;

    // Enchants
    public int enchantNormalSucceeded = 0;
    public int enchantBlessedSucceeded = 0;
    public int highestEnchant = 0;

    // Clan & Olympiad
    public int olyHiScore = 0;
    public int olyGamesWon = 0;
    public int olyGamesLost = 0;
    public int timesHero = 0;
    public int castleSiegesWon = 0;
    public int fortSiegesWon = 0;
    public int dominionSiegesWon = 0;

    // Epic Bosses.
    public int antharasKilled = 0;
    public int baiumKilled = 0;
    public int valakasKilled = 0;
    public int orfenKilled = 0;
    public int antQueenKilled = 0;
    public int coreKilled = 0;
    public int belethKilled = 0;
    public int sailrenKilled = 0;
    public int baylorKilled = 0;
    public int zakenKilled = 0;
    public int tiatKilled = 0;
    public int freyaKilled = 0;
    public int frintezzaKilled = 0;
    // Other kills
    public int mobsKilled = 0;
    public int raidsKilled = 0;
    public int championsKilled = 0;
    public int townGuardsKilled = 0;
    public int siegeGuardsKilled = 0;
    public int playersKilledInSiege = 0;
    public int playersKilledInDominion = 0;


    // Here comes the code...
    private Player activeChar;

    public PlayerCounters(Player activeChar) {
        this.activeChar = activeChar;
    }

    public long getPoints(String fieldName) {
        if (activeChar == null)
            return 0;
        try {
            return getClass().getField(fieldName).getLong(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
