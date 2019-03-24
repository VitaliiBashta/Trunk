package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.entity.events.objects.TerritoryWardObject;
import l2trunk.gameserver.model.entity.residence.Dominion;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;

public final class ExShowOwnthingPos extends L2GameServerPacket {
    private final List<WardInfo> wards = new ArrayList<>(9);

    public ExShowOwnthingPos() {
        for (Dominion dominion : ResidenceHolder.getDominions()) {
            if (dominion.getSiegeDate().getTimeInMillis() == 0)
                continue;
            for (int dominionId : dominion.getFlags()) {
                TerritoryWardObject wardObject = dominion.getSiegeEvent().getFirstObject("ward_" + dominionId);
                Location loc = wardObject.getWardLocation();
                if (loc != null)
                    wards.add(new WardInfo(dominionId, loc));
            }
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0x93);
        writeD(wards.size());
        wards.forEach(wardInfo ->  {
            writeD(wardInfo.dominionId);
            writeD(wardInfo.loc.x);
            writeD(wardInfo.loc.y);
            writeD(wardInfo.loc.z);
        });
    }

    private static class WardInfo {
        private final int dominionId;
        private final Location loc;

        WardInfo(int territoryId, Location loc) {
            dominionId = territoryId;
            this.loc = loc;
        }
    }
}