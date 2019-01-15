package l2trunk.gameserver.model.entity.achievements;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.listener.actor.OnKillListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.GuardInstance;
import l2trunk.gameserver.network.serverpackets.TutorialShowQuestionMark;
import l2trunk.scripts.npc.model.TreasureChestInstance;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

public final class AchievementNotification {
    private static AchievementNotification _instance;
    private static Listener _listener;
    private ScheduledFuture<?> _globalNotification;

    private AchievementNotification(int intervalInMiliseconds) {
        _globalNotification = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(() ->
                GameObjectsStorage.getAllPlayersStream().forEach(player ->  {

                    for (Entry<Integer, Integer> arco : player.getAchievements().entrySet()) {
                        int achievementId = arco.getKey();
                        int achievementLevel = arco.getValue();
                        if (Achievements.INSTANCE.getMaxLevel(achievementId) <= achievementLevel)
                            continue;

                        Achievement nextLevelAchievement = Achievements.INSTANCE.getAchievement(achievementId, ++achievementLevel);
                        if (nextLevelAchievement != null && nextLevelAchievement.isDone(player.getCounters().getPoints(nextLevelAchievement.getType()))) {
                            // Make a question mark button.
                            player.sendPacket(new TutorialShowQuestionMark(player.getObjectId()));
                            break;
                        }
                    }
                }), intervalInMiliseconds, intervalInMiliseconds);

        _listener = new Listener();
        CharListenerList.addGlobal(_listener);
    }

    public static AchievementNotification getInstance() {
        if (_instance == null)
            _instance = new AchievementNotification(3000);
        return _instance;
    }

    public void stopNotification() {
        if (_globalNotification != null) {
            _globalNotification.cancel(true);
            _globalNotification = null;
        }
    }

    private static class Listener implements OnKillListener {
        @Override
        public void onKill(Creature actor, Creature victim) {
            if (!Config.ENABLE_ACHIEVEMENTS)
                return;

            Player player = actor.getPlayer();
            if (player == null)
                return;

            if (victim.isPlayer())
                victim.getPlayer().getCounters().timesDied++;

            if (victim.isNpc()) {
                if (victim instanceof TreasureChestInstance)
                    player.getCounters().treasureBoxesOpened++;
                else if (victim instanceof GuardInstance)
                    player.getCounters().townGuardsKilled++;
                else if (victim instanceof SiegeGuardInstance)
                    player.getCounters().siegeGuardsKilled++;
            }

            if (player.getLevel() - victim.getLevel() >= 10) // 10 levels difference
                return;

            if (victim.isMonster())
                player.getCounters().mobsKilled++;

            if (victim.isRaid())
                player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().raidsKilled++);

            if (victim.isChampion())
                player.getCounters().championsKilled++;

            if (victim.isNpc()) {
                switch (victim.getNpcId()) {
                    case 29001: // Queen Ant
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().antQueenKilled++);
                        break;
                    case 29006: // Core
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().coreKilled++);
                        break;
                    case 29014: // Orfen
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().orfenKilled++);
                        break;
                    case 29019: // Antharas
                    case 29066: // Antharas
                    case 29067: // Antharas
                    case 29068: // Antharas
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().antharasKilled++);
                        break;
                    case 29020: // Baium
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().baiumKilled++);
                        break;
                    case 29022: // Zaken Lv. 60
                    case 29176: // Zaken Lv. 60
                    case 29181: // Zaken Lv. 83
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().zakenKilled++);
                        break;
                    case 29028: // Valakas
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().valakasKilled++);
                        break;
                    case 29047: // Scarlet van Halisha / Frintezza
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().frintezzaKilled++);
                        break;
                    case 29065: // Sailren
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().sailrenKilled++);
                        break;
                    case 29099: // Baylor
                    case 29186: // Baylor
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().baylorKilled++);
                        break;
                    case 29118: // Beleth
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().belethKilled++);
                        break;
                    case 29163: // Tiat
                    case 29175: // Tiat
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().tiatKilled++);
                        break;
                    case 29179: // Freya Normal
                    case 29180: // Freya Hard
                        player.getPlayerGroup().getMembersInRange(victim, 5000).forEach(plr -> plr.getCounters().freyaKilled++);
                        break;
                }
            }
        }

        @Override
        public boolean ignorePetOrSummon() {
            return true;
        }
    }
}
