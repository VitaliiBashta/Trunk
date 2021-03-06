package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Rooney extends DefaultAI {
    private static final List<Location> points = List.of(
            new Location(184022, -117083, -3342),
            new Location(183516, -118815, -3093),
            new Location(185007, -115651, -1587),
            new Location(186191, -116465, -1587),
            new Location(189630, -115611, -1587));

    private static final long TELEPORT_PERIOD = 30 * 60 * 1000; // 30 min
    private long _lastTeleport = System.currentTimeMillis();

    public Rooney(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (System.currentTimeMillis() - _lastTeleport < TELEPORT_PERIOD)
            return false;

        for (Location point : points) {
            Location loc = Rnd.get(points);
            if (actor.getLoc().equals(loc))
                continue;

            actor.broadcastPacketToOthers(new MagicSkillUse(actor,  4671,  1000));
            ThreadPoolManager.INSTANCE.schedule(new Teleport(loc), 1000);
            _lastTeleport = System.currentTimeMillis();
            break;
        }
        return true;
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }
}