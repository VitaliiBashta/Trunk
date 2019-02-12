package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.KrateisCubeEvent;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.Future;

public class KrateisCubePlayerObject implements Serializable, Comparable<KrateisCubePlayerObject> {
    private final Player player;
    private final long _registrationTime;
    private boolean _showRank;
    private int _points;
    private Future<?> _ressurectTask;

    public KrateisCubePlayerObject(Player player) {
        this.player = player;
        _registrationTime = System.currentTimeMillis();
    }

    public String getName() {
        return player.getName();
    }

    public boolean isShowRank() {
        return _showRank;
    }

    public void setShowRank(boolean showRank) {
        _showRank = showRank;
    }

    public int getPoints() {
        return _points;
    }

    public void setPoints(int points) {
        _points = points;
    }

    private long getRegistrationTime() {
        return _registrationTime;
    }

    public int getObjectId() {
        return player.objectId();
    }

    public Player getPlayer() {
        return player;
    }

    public void startRessurectTask() {
        if (_ressurectTask != null)
            return;

        _ressurectTask = ThreadPoolManager.INSTANCE.schedule(new RessurectTask(), 1000L);
    }

    public void stopRessurectTask() {
        if (_ressurectTask != null) {
            _ressurectTask.cancel(false);
            _ressurectTask = null;
        }
    }

    @Override
    public int compareTo(KrateisCubePlayerObject o) {
        if (getPoints() == o.getPoints())
            return (int) ((getRegistrationTime() - o.getRegistrationTime()) / 1000L);
        return getPoints() - o.getPoints();
    }

    private class RessurectTask extends RunnableImpl {
        private int _seconds = 10;

        RessurectTask() {
            //
        }

        @Override
        public void runImpl() {
            _seconds -= 1;
            if (_seconds == 0) {
                KrateisCubeEvent cubeEvent = player.getEvent(KrateisCubeEvent.class);
                List<Location> waitLocs = cubeEvent.getObjects(KrateisCubeEvent.WAIT_LOCS);

                _ressurectTask = null;

                player.teleToLocation(Rnd.get(waitLocs));
                player.doRevive();
            } else {
                player.sendPacket(new SystemMessage2(SystemMsg.RESURRECTION_WILL_TAKE_PLACE_IN_THE_WAITING_ROOM_AFTER_S1_SECONDS).addInteger(_seconds));
                _ressurectTask = ThreadPoolManager.INSTANCE.schedule(this, 1000L);
            }
        }
    }
}
