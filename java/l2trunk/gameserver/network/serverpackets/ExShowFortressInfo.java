package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.pledge.Clan;

import java.util.ArrayList;
import java.util.List;

public final class ExShowFortressInfo extends L2GameServerPacket {
    private final List<FortressInfo> _infos;

    public ExShowFortressInfo() {
        List<Fortress> forts = ResidenceHolder.getInstance().getResidenceList(Fortress.class);
        _infos = new ArrayList<>(forts.size());
        for (Fortress fortress : forts) {
            Clan owner = fortress.getOwner();
            _infos.add(new FortressInfo(owner == null ? StringUtils.EMPTY : owner.getName(), fortress.getId(), fortress.getSiegeEvent().isInProgress(), owner == null ? 0 : (int) ((System.currentTimeMillis() - fortress.getOwnDate().getTimeInMillis()) / 1000L)));
        }
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x15);
        writeD(_infos.size());
        for (FortressInfo _info : _infos) {
            writeD(_info._id);
            writeS(_info._owner);
            writeD(_info._status);
            writeD(_info._siege);
        }
    }

    static class FortressInfo {
        final int _id;
        final int _siege;
        final String _owner;
        final boolean _status;

        FortressInfo(String owner, int id, boolean status, int siege) {
            _owner = owner;
            _id = id;
            _status = status;
            _siege = siege;
        }
    }
}