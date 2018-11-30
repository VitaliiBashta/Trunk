package l2trunk.gameserver.model.entity.events.impl.fightclub;

import l2trunk.commons.collections.MultiValueSet;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubPlayer;
import l2trunk.gameserver.model.entity.events.impl.AbstractFightClub;

public class LastManStandingEvent extends AbstractFightClub {
    private static final long MAX_DELAY_BETWEEN_DEATHS = 30000L;
    private FightClubPlayer _winner;
    private long lastKill;

    public LastManStandingEvent(MultiValueSet<String> set) {
        super(set);
        lastKill = 0L;
    }

    @Override
    public void onKilled(Creature actor, Creature victim) {
        if (actor != null && actor.isPlayable()) {
            FightClubPlayer fActor = getFightClubPlayer(actor.getPlayer());
            if (fActor != null && victim.isPlayer()) {
                fActor.increaseKills(true);
                updatePlayerScore(fActor);
                sendMessageToPlayer(fActor, MESSAGE_TYPES.GM, "You have killed " + victim.getName());
            } else {
                victim.isPet();
            }
            actor.getPlayer().sendUserInfo();
        }

        if (victim.isPlayer()) {
            FightClubPlayer fVictim = getFightClubPlayer(victim);
            fVictim.increaseDeaths();
            if (actor != null)
                sendMessageToPlayer(fVictim, MESSAGE_TYPES.GM, "You have been killed by " + actor.getName());
            victim.getPlayer().sendUserInfo();
            lastKill = System.currentTimeMillis();

            leaveEvent(fVictim.getPlayer(), true);

            checkRoundOver();
        }

        super.onKilled(actor, victim);
    }

    @Override
    public void startEvent() {
        super.startEvent();

        lastKill = System.currentTimeMillis();
        ThreadPoolManager.INSTANCE.schedule(new InactivityCheck(), 60000);
    }

    @Override
    public void startRound() {
        super.startRound();
        checkRoundOver();
    }

    @Override
    public boolean leaveEvent(Player player, boolean teleportTown) {
        boolean result = super.leaveEvent(player, teleportTown);
        if (result)
            checkRoundOver();
        return result;
    }

    private boolean checkRoundOver() {
        if (getState() != EVENT_STATE.STARTED)
            return true;

        int alivePlayers = 0;
        FightClubPlayer aliveFPlayer = null;

        for (FightClubPlayer iFPlayer : getPlayers(FIGHTING_PLAYERS)) {
            if (isPlayerActive(iFPlayer.getPlayer())) {
                alivePlayers++;
                aliveFPlayer = iFPlayer;
            }
            if (aliveFPlayer == null)
                if (!iFPlayer.getPlayer().isDead())
                    aliveFPlayer = iFPlayer;
        }

        if (alivePlayers <= 1) {
            _winner = aliveFPlayer;
            if (_winner != null) {
                _winner.increaseScore(1);
                announceWinnerPlayer(false, _winner);
            }
            updateScreenScores();
            setState(EVENT_STATE.OVER);

            ThreadPoolManager.INSTANCE.schedule(this::endRound, 5000L);
            if (_winner != null)
                FightClubEventManager.getInstance().sendToAllMsg(this, _winner.getPlayer().getName() + " Won Last Hero Event!");
            return true;
        }
        return false;
    }

    @Override
    protected boolean inScreenShowBeScoreNotKills() {
        return false;
    }

    private void killOnePlayer() {
        double playerToKillHp = Double.MAX_VALUE;
        Player playerToKill = null;
        for (FightClubPlayer fPlayer : getPlayers(FIGHTING_PLAYERS))
            if (fPlayer != null && fPlayer.getPlayer() != null && !fPlayer.getPlayer().isDead()) {
                if (fPlayer.isAfk()) {
                    playerToKillHp = -1.0;
                    playerToKill = fPlayer.getPlayer();
                } else if (fPlayer.getPlayer().getCurrentHpPercents() + (fPlayer.getKills(true) * 10) < playerToKillHp) {
                    playerToKill = fPlayer.getPlayer();
                    playerToKillHp = fPlayer.getPlayer().getCurrentHpPercents() + (fPlayer.getKills(true) * 10);
                }
            }

		if (playerToKill != null)
			playerToKill.doDie(null);
    }

    @Override
    protected int getRewardForWinningTeam(FightClubPlayer fPlayer, boolean atLeast1Kill) {
        if (fPlayer.equals(_winner))
            return (int) _badgeWin;
        return super.getRewardForWinningTeam(fPlayer, true);
    }

    @Override
    public String getVisibleTitle(Player player, String currentTitle, boolean toMe) {
        FightClubPlayer realPlayer = getFightClubPlayer(player);

        if (realPlayer == null)
            return currentTitle;

        return "Kills: " + realPlayer.getKills(true);
    }

    private class InactivityCheck extends RunnableImpl {

        @Override
        public void runImpl() {
            if (getState() == EVENT_STATE.NOT_ACTIVE)
                return;
            boolean finished = checkRoundOver();
            if (!finished && lastKill + MAX_DELAY_BETWEEN_DEATHS < System.currentTimeMillis()) {
                killOnePlayer();
            }

            ThreadPoolManager.INSTANCE.schedule(this, 60000);
        }
    }
}
