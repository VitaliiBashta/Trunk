package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.utils.Location;

public final class Toma extends DefaultAI {
    private final Location[] _points = {
            new Location(151680, -174891, -1807, 41400),
            new Location(154153, -220105, -3402),
            new Location(178834, -184336, -352)};
    private static final long TELEPORT_PERIOD = 30 * 60 * 1000; // 30 min
    private long _lastTeleport = System.currentTimeMillis();

    public Toma(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        if (System.currentTimeMillis() - _lastTeleport < TELEPORT_PERIOD)
            return false;

        NpcInstance _thisActor = getActor();

        Location loc = _points[Rnd.get(_points.length)];
        if (_thisActor.getLoc().equals(loc))
            return false;

        _thisActor.broadcastPacketToOthers(new MagicSkillUse(_thisActor, _thisActor, 4671, 1, 1000, 0));
        ThreadPoolManager.getInstance().schedule(new Teleport(loc), 1000);
        _lastTeleport = System.currentTimeMillis();

        return true;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}