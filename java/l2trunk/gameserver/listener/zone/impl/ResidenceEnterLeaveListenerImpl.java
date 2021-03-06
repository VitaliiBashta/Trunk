package l2trunk.gameserver.listener.zone.impl;

import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.entity.residence.ResidenceFunction;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncMul;

public final class ResidenceEnterLeaveListenerImpl implements OnZoneEnterLeaveListener {
    public static final OnZoneEnterLeaveListener STATIC = new ResidenceEnterLeaveListenerImpl();

    @Override
    public void onZoneEnter(Zone zone, Player player) {
        Residence residence = zone.getParams().getResidence("residence");

        if (residence.getOwner() == null || residence.getOwner() != player.getClan())
            return;

        if (residence.isFunctionActive(ResidenceFunction.RESTORE_HP)) {
            double value = 1. + residence.getFunction(ResidenceFunction.RESTORE_HP).getLevel() / 100.;

            player.addStatFunc(new FuncMul(Stats.REGENERATE_HP_RATE, 0x30, residence, value));
        }

        if (residence.isFunctionActive(ResidenceFunction.RESTORE_MP)) {
            double value = 1. + residence.getFunction(ResidenceFunction.RESTORE_MP).getLevel() / 100.;

            player.addStatFunc(new FuncMul(Stats.REGENERATE_MP_RATE, 0x30, residence, value));
        }
    }

    @Override
    public void onZoneLeave(Zone zone, Player actor) {
        Residence residence = zone.getParams().getResidence("residence");

        actor.removeStatsOwner(residence);
    }
}
