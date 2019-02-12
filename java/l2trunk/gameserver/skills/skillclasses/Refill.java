package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.boat.ClanAirShip;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class Refill extends Skill {
    public Refill(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (target instanceof Player && target.isInBoat() && ((Player)target).getBoat() instanceof ClanAirShip) {
            return super.checkCondition(player, target, forceUse, dontMove, first);
        }
        player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(id, level));
        return false;

    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (target instanceof Player && !target.isDead() && target.isInBoat() && ((Player)target).getBoat() instanceof ClanAirShip) {
            ClanAirShip airship = (ClanAirShip) ((Player)target).getBoat();
            airship.setCurrentFuel(airship.getCurrentFuel() + (int) power);
        }


    }
}