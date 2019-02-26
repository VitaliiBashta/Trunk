package l2trunk.gameserver.utils;


public class AutoHuntingPunish {
    // Kind of punish
    private final Punish _AutoHuntingPunishPunishment;
    // Time the punish will last
    private final long _punishTime;
    // Punis time (in secs)
    private final int _punishDuration;

    public AutoHuntingPunish(Punish punish, int mins) {
        _AutoHuntingPunishPunishment = punish;
        _punishTime = System.currentTimeMillis() + (mins * 60 * 1000);
        _punishDuration = mins * 60;
    }

    /**
     * Returns the current punishment type
     *
     * @return Punish (BotPunish enum)
     */
    public Punish getBotPunishType() {
        return _AutoHuntingPunishPunishment;
    }

    /**
     * Returns the duration (in seconds) of the applied
     * punish
     *
     * @return int
     */
    public int getDuration() {
        return _punishDuration;
    }

    /**
     * Return the time left to end up this punish
     *
     * @return long
     */
    public long getPunishTimeLeft() {
        return System.currentTimeMillis() - _punishTime;
    }

    /**
     * @return true if the getPlayer punishment has
     * expired
     */
    public boolean canWalk() {
        if (_AutoHuntingPunishPunishment == Punish.MOVEBAN
                && System.currentTimeMillis() - _punishTime <= 0)
            return false;
        return true;
    }

    /**
     * @return true if the getPlayer punishment has
     * expired
     */
    public boolean canTalk() {
        if (_AutoHuntingPunishPunishment == Punish.CHATBAN
                && System.currentTimeMillis() - _punishTime <= 0)
            return false;
        return true;
    }

    /**
     * @return true if the getPlayer punishment has
     * expired
     */
    public boolean canJoinParty() {
        if (_AutoHuntingPunishPunishment == Punish.PARTYBAN
                && System.currentTimeMillis() - _punishTime <= 0)
            return false;
        return true;
    }

    /**
     * @return true if the getPlayer punishment has
     * expired
     */
    public boolean canPerformAction() {
        if (_AutoHuntingPunishPunishment == Punish.ACTIONBAN
                && System.currentTimeMillis() - _punishTime <= 0)
            return false;
        return true;
    }

    // Type of punishments
    public enum Punish {
        CHATBAN,
        MOVEBAN,
        PARTYBAN,
        ACTIONBAN
    }
}