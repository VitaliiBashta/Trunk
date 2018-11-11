package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.entity.events.objects.TerritoryWardObject;
import l2trunk.gameserver.model.entity.residence.Dominion;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 */
public class ExShowOwnthingPos extends L2GameServerPacket {
    private final List<WardInfo> _wardList = new ArrayList<>(9);

    public ExShowOwnthingPos() {
        for (Dominion dominion : ResidenceHolder.getInstance().getResidenceList(Dominion.class)) {
            if (dominion.getSiegeDate().getTimeInMillis() == 0)
                continue;

            int[] flags = dominion.getFlags();
            for (int dominionId : flags) {
                TerritoryWardObject wardObject = dominion.getSiegeEvent().getFirstObject("ward_" + dominionId);
                Location loc = wardObject.getWardLocation();
                if (loc != null)
                    _wardList.add(new WardInfo(dominionId, loc.x, loc.y, loc.z));
            }
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0x93);
        writeD(_wardList.size());
        for (WardInfo wardInfo : _wardList) {
            writeD(wardInfo.dominionId);
            writeD(wardInfo._x);
            writeD(wardInfo._y);
            writeD(wardInfo._z);
        }
    }

    private static class WardInfo {
        private final int dominionId;
        private final int _x;
        private final int _y;
        private final int _z;

        WardInfo(int territoryId, int x, int y, int z) {
            dominionId = territoryId;
            _x = x;
            _y = y;
            _z = z;
        }
    }
}