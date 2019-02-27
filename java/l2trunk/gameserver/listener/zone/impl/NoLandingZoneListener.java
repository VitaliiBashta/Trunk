package l2trunk.gameserver.listener.zone.impl;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.PetDataTable;

public final class NoLandingZoneListener implements OnZoneEnterLeaveListener {
    public static final OnZoneEnterLeaveListener STATIC = new NoLandingZoneListener();

    @Override
    public void onZoneEnter(Zone zone, Player player) {
        if (player != null)
            if (player.isFlying() && player.getMountNpcId() == PetDataTable.WYVERN_ID) {
                Residence residence = ResidenceHolder.getResidence(zone.getParams().getInteger("residence", 0));
                if (residence == null || player.getClan() == null || residence.getOwner() != player.getClan()) {
                    player.stopMove();
                    player.sendPacket(SystemMsg.THIS_AREA_CANNOT_BE_ENTERED_WHILE_MOUNTED_ATOP_OF_A_WYVERN);
                    player.dismount();
                }

            }
    }

}
