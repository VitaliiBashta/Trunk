package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.FlyToLocation;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.ValidateLocation;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.PositionUtils;

import java.util.List;

public final class InstantJump extends Skill {
    public InstantJump(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (targets.size() == 0)
            return;

        Creature target = targets.get(0);
        if (Rnd.chance(target.calcStat(Stats.PSKILL_EVASION, 0, activeChar, this))) {
            if (activeChar.isPlayer())
                activeChar.sendPacket(new SystemMessage(SystemMessage.C1_DODGES_THE_ATTACK).addName(target));
            if (target.isPlayer())
                target.sendPacket(new SystemMessage(SystemMessage.C1_HAS_EVADED_C2S_ATTACK).addName(target).addName(activeChar));
            return;
        }
        int x, y, z;

        int px = target.getX();
        int py = target.getY();
        double ph = PositionUtils.convertHeadingToDegree(target.getHeading());

        ph += 180;

        if (ph > 360)
            ph -= 360;

        ph = (Math.PI * ph) / 180;

        x = (int) (px + (25 * Math.cos(ph)));
        y = (int) (py + (25 * Math.sin(ph)));
        z = target.getZ();

        Location loc = new Location(x, y, z);

        if (Config.ALLOW_GEODATA)
            loc = GeoEngine.moveCheck(activeChar.getX(), activeChar.getY(), activeChar.getZ(), x, y, activeChar.getReflection().getGeoIndex());

        target.setTarget(null);
        target.abortAttack(true, true);
        target.abortCast(true, true);
        target.stopMove(true, true);
        target.getAI().notifyEvent(CtrlEvent.EVT_THINK);

        activeChar.abortAttack(true, true);
        activeChar.abortCast(true, true);
        activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
        activeChar.broadcastPacket(new FlyToLocation(activeChar, loc, FlyToLocation.FlyType.DUMMY));
        activeChar.setLoc(loc);
        activeChar.setHeading(target.getHeading());
        activeChar.broadcastPacket(new ValidateLocation(activeChar));
    }
}
