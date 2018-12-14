package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.BossInstance;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.Arrays;
import java.util.List;

public class OrfenInstance extends BossInstance {
    private static final Location nest = new Location(43728, 17220, -4342);

    private static final List<Location> locs = Arrays.asList(
            new Location(55024, 17368, -5412),
            new Location(53504, 21248, -5496),
            new Location(53248, 24576, -5272));

    public OrfenInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void setTeleported(boolean flag) {
        super.setTeleported(flag);
        Location loc = flag ? nest : Rnd.get(locs);
        setSpawnedLoc(loc);
        getAggroList().clear(true);
        getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null, null);
        teleToLocation(loc);
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();
        setTeleported(false);
        broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS01_A", 1, 0, getLoc()));
    }

    @Override
    protected void onDeath(Creature killer) {
        broadcastPacketToOthers(new PlaySound(PlaySound.Type.MUSIC, "BS02_D", 1, 0, getLoc()));
        super.onDeath(killer);
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflect, boolean transferDamage, boolean isDot, boolean sendMessage) {
        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflect, transferDamage, isDot, sendMessage);
        if (!isTeleported() && getCurrentHpPercents() <= 50)
            setTeleported(true);
    }
}