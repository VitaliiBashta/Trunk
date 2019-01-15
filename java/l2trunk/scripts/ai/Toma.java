package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Toma extends DefaultAI {
    private static final long TELEPORT_PERIOD = 30 * 60 * 1000; // 30 min
    private final List<Location> _points = List.of(
            new Location(151680, -174891, -1807, 41400),
            new Location(154153, -220105, -3402),
            new Location(178834, -184336, -352));
    private long _lastTeleport = System.currentTimeMillis();

    public Toma(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        if (System.currentTimeMillis() - _lastTeleport < TELEPORT_PERIOD)
            return false;

        NpcInstance _thisActor = getActor();

        Location loc = Rnd.get(_points);
        if (_thisActor.getLoc().equals(loc))
            return false;

        _thisActor.broadcastPacketToOthers(new MagicSkillUse(_thisActor, 4671, 1000));
        ThreadPoolManager.INSTANCE.schedule(new Teleport(loc), 1000);
        _lastTeleport = System.currentTimeMillis();

        return true;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}