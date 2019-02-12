package l2trunk.gameserver.model.actor.instances.player;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExNavitAdventEffect;
import l2trunk.gameserver.network.serverpackets.ExNavitAdventPointInfo;
import l2trunk.gameserver.network.serverpackets.ExNavitAdventTimeChange;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.AbnormalEffect;

import java.util.Calendar;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public final class NevitSystem {
    private static final int ADVENT_TIME = 14400; // 240 last minute period of constant scoring.
    private static final int MAX_POINTS = 7200;
    private static final int BONUS_EFFECT_TIME = 180; // 180 the effect lasts seconds bonus Nevit.

    private final Player player;
    private int points = 0;
    private int _time;
    private ScheduledFuture<?> _adventTask;
    private ScheduledFuture<?> _nevitEffectTask;
    private int percent;
    private boolean active;

    public NevitSystem(Player player) {
        this.player = player;
    }

    public void setPoints(int points, int time) {
        this.points = points;
        active = false;
        percent = getPercent(this.points);

        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.HOUR_OF_DAY, 6);
        temp.set(Calendar.MINUTE, 30);
        temp.set(Calendar.SECOND, 0);
        temp.set(Calendar.MILLISECOND, 0);
        if (player.getLastAccess() < temp.getTimeInMillis() / 1000L && System.currentTimeMillis() > temp.getTimeInMillis())
            _time = ADVENT_TIME;
        else
            _time = time;
    }

    public void restartSystem() {
        _time = ADVENT_TIME;
        player.sendPacket(new ExNavitAdventTimeChange(active, _time));
    }

    public void onEnterWorld() {
        player.sendPacket(new ExNavitAdventPointInfo(points));
        player.sendPacket(new ExNavitAdventTimeChange(active, _time));
        startNevitEffect(player.getVarInt("nevit"));
        if (percent >= 45 && percent < 50)
            player.sendPacket(SystemMsg.YOU_ARE_STARTING_TO_FEEL_THE_EFFECTS_OF_NEVITS_BLESSING);
        else if (percent >= 50 && percent < 75)
            player.sendPacket(SystemMsg.YOU_ARE_FURTHER_INFUSED_WITH_THE_BLESSINGS_OF_NEVIT_CONTINUE_TO_BATTLE_EVIL_WHEREVER_IT_MAY_LURK);
        else if (percent >= 75)
            player.sendPacket(SystemMsg.NEVITS_BLESSING_SHINES_STRONGLY_FROM_ABOVE_YOU_CAN_ALMOST_SEE_HIS_DIVINE_AURA);
    }

    public void startAdventTask() {
        if (!active) {
            active = true;
            if (_time > 0 && _adventTask == null)
                _adventTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new AdventTask(), 30000L, 30000L);

            player.sendPacket(new ExNavitAdventTimeChange(active, _time));
        }
    }

    private void startNevitEffect(int time) {
        if (getEffectTime() > 0) {
            stopNevitEffectTask(false);
            time += getEffectTime();
        }
        if (time > 0) {
            player.setVar("nevit", time);
            player.sendPacket(new ExNavitAdventEffect(time));
            player.sendPacket(SystemMsg.THE_ANGEL_NEVIT_HAS_BLESSED_YOU_FROM_ABOVE_YOU_ARE_IMBUED_WITH_FULL_VITALITY_AS_WELL_AS_A_VITALITY_REPLENISHING_EFFECT);
            player.startAbnormalEffect(AbnormalEffect.S_NAVIT);
            player.addVitality(Config.ALT_VITALITY_NEVIT_UP_POINT);
            _nevitEffectTask = ThreadPoolManager.INSTANCE.schedule(new NevitEffectEnd(), time * 1000L);
        }
    }

    public void stopTasksOnLogout() {
        stopNevitEffectTask(true);
        stopAdventTask(false);
    }

    public void stopAdventTask(boolean sendPacket) {
        if (_adventTask != null) {
            _adventTask.cancel(true);
            _adventTask = null;
        }
        active = false;
        if (sendPacket)
            player.sendPacket(new ExNavitAdventTimeChange(false, _time));
    }

    private void stopNevitEffectTask(boolean saveTime) {
        if (_nevitEffectTask != null) {
            if (saveTime) {
                int time = getEffectTime();
                if (time > 0)
                    player.setVar("nevit", time);
                else
                    player.unsetVar("nevit");
            }
            _nevitEffectTask.cancel(true);
            _nevitEffectTask = null;
        }
    }

    public boolean isActive() {
        return active;
    }

    public int getTime() {
        return _time;
    }

    public void setTime(int time) {
        _time = time;
    }

    public int getPoints() {
        return points;
    }

    public void addPoints(int val) {
        points += val;
        int percent = getPercent(points);
        if (this.percent != percent) {
            this.percent = percent;
            if (this.percent == 45)
                player.sendPacket(SystemMsg.YOU_ARE_STARTING_TO_FEEL_THE_EFFECTS_OF_NEVITS_BLESSING);
            else if (this.percent == 50)
                player.sendPacket(SystemMsg.YOU_ARE_FURTHER_INFUSED_WITH_THE_BLESSINGS_OF_NEVIT_CONTINUE_TO_BATTLE_EVIL_WHEREVER_IT_MAY_LURK);
            else if (this.percent == 75)
                player.sendPacket(SystemMsg.NEVITS_BLESSING_SHINES_STRONGLY_FROM_ABOVE_YOU_CAN_ALMOST_SEE_HIS_DIVINE_AURA);
        }
        if (points > MAX_POINTS) {
            this.percent = 0;
            points = 0;
            startNevitEffect(BONUS_EFFECT_TIME);
        }
        player.sendPacket(new ExNavitAdventPointInfo(points));
    }

    private int getPercent(int points) {
        return (int) (100.0D / MAX_POINTS * points);
    }

    public boolean isBlessingActive() {
        return getEffectTime() > 0;
    }

    private int getEffectTime() {
        if (_nevitEffectTask == null)
            return 0;
        return (int) Math.max(0, _nevitEffectTask.getDelay(TimeUnit.SECONDS));
    }

    private class AdventTask extends RunnableImpl {
        @Override
        public void runImpl() {
            _time -= 30;
            if (_time <= 0) {
                _time = 0;
                stopAdventTask(true);
            } else {
                addPoints(72);
                if ((_time % 60) == 0)
                    player.sendPacket(new ExNavitAdventTimeChange(true, _time));
            }
        }
    }

    private class NevitEffectEnd extends RunnableImpl {
        @Override
        public void runImpl() {
            player.sendPacket(new ExNavitAdventEffect(0));
            player.sendPacket(new ExNavitAdventPointInfo(points));
            player.sendPacket(SystemMsg.NEVITS_BLESSING_HAS_ENDED_CONTINUE_YOUR_JOURNEY_AND_YOU_WILL_SURELY_MEET_HIS_FAVOR_AGAIN_SOMETIME_SOON);
            player.stopAbnormalEffect(AbnormalEffect.S_NAVIT);
            player.unsetVar("nevit");
            stopNevitEffectTask(false);
        }
    }
}