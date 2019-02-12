package l2trunk.gameserver.listener.zone.impl;

import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.boat.ClanAirShip;
import l2trunk.gameserver.model.instances.ClanAirShipControllerInstance;

public final class AirshipControllerZoneListener implements OnZoneEnterLeaveListener {
    private ClanAirShipControllerInstance _controllerInstance;

    @Override
    public void onZoneEnter(Zone zone, Player actor) {
//        if (_controllerInstance == null)
//            _controllerInstance = (ClanAirShipControllerInstance) actor;
//        else if (actor instanceof ClanAirShip)
//            _controllerInstance.setDockedShip((ClanAirShip) actor);
    }

}
