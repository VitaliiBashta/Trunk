package l2trunk.scripts.ai;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExSendUIEvent;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

public final class ZakenDaytime extends Fighter {
    private static final Location[] _locations = new Location[]{
            new Location(55272, 219112, -3496),
            new Location(56296, 218072, -3496),
            new Location(54232, 218072, -3496),
            new Location(54248, 220136, -3496),
            new Location(56296, 220136, -3496),
            new Location(55272, 219112, -3224),
            new Location(56296, 218072, -3224),
            new Location(54232, 218072, -3224),
            new Location(54248, 220136, -3224),
            new Location(56296, 220136, -3224),
            new Location(55272, 219112, -2952),
            new Location(56296, 218072, -2952),
            new Location(54232, 218072, -2952),
            new Location(54248, 220136, -2952),
            new Location(56296, 220136, -2952)
    };

    private long _teleportSelfTimer = 0L;
    private final long _teleportSelfReuse = 120000L;          // 120 secs
    private final NpcInstance actor = getActor();

    public ZakenDaytime(NpcInstance actor) {
        super(actor);
        MAX_PURSUE_RANGE = Integer.MAX_VALUE / 2;
    }

    @Override
    public void thinkAttack() {
        if (_teleportSelfTimer + _teleportSelfReuse < System.currentTimeMillis()) {
            _teleportSelfTimer = System.currentTimeMillis();
            if (Rnd.chance(20)) {
                actor.doCast(SkillTable.getInstance().getInfo(4222, 1), actor, false);
                ThreadPoolManager.getInstance().schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() {
                        actor.teleToLocation(_locations[Rnd.get(_locations.length)]);
                        actor.getAggroList().clear(true);
                    }
                }, 500);
            }
        }
        super.thinkAttack();
    }

    @Override
    public void onEvtDead(Creature killer) {
        Reflection r = actor.getReflection();
        r.setReenterTime(System.currentTimeMillis());
        for (Player p : r.getPlayers())
            p.sendPacket(new ExSendUIEvent(p, true, true, 0, 0));
        actor.broadcastPacket(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, actor.getObjectId(), actor.getLoc()));
        super.onEvtDead(killer);
    }

    @Override
    public void teleportHome() {
        return;
    }
}