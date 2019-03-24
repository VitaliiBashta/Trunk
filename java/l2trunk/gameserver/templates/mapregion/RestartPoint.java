package l2trunk.gameserver.templates.mapregion;

import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class RestartPoint {
    public final int bbs;
    public final int msgId;
    private final List<Location> restartPoints;
    private final List<Location> pkRestartPoints;

    public RestartPoint(int bbs, int msgId, List<Location> restartPoints, List<Location> PKrestartPoints) {
        this.bbs = bbs;
        this.msgId = msgId;
        this.restartPoints = restartPoints;
        pkRestartPoints = PKrestartPoints;
    }

    public List<Location> getRestartPoints() {
        return restartPoints;
    }

    public List<Location> getPKrestartPoints() {
        return pkRestartPoints;
    }
}
