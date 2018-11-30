package l2trunk.gameserver.listener.zone.impl;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.PetDataTable;

public class NoLandingZoneListener implements OnZoneEnterLeaveListener {
    public static final OnZoneEnterLeaveListener STATIC = new NoLandingZoneListener();

    @Override
    public void onZoneEnter(Zone zone, Creature actor) {
        Player player = actor.getPlayer();
        if (player != null)
            if (player.isFlying() && player.getMountNpcId() == PetDataTable.WYVERN_ID) {
                Residence residence = ResidenceHolder.getInstance().getResidence(zone.getParams().getInteger("residence", 0));
                if (residence != null && player.getClan() != null && residence.getOwner() == player.getClan()) {
                    //
                } else {
                    player.stopMove();
                    player.sendPacket(SystemMsg.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN);
                    player.setMount(0, 0, 0);
                }
            }
    }

    @Override
    public void onZoneLeave(Zone zone, Creature cha) {
    }
}
