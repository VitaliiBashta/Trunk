package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.pledge.Clan;

import java.util.ArrayList;
import java.util.List;

public final class ExShowFortressInfo extends L2GameServerPacket {
    private final List<FortressInfo> infos;

    public ExShowFortressInfo() {
        infos = new ArrayList<>();
        ResidenceHolder.getFortresses().forEach(fortress -> {
            Clan owner = fortress.getOwner();
            infos.add(new FortressInfo(owner == null ? "" : owner.getName(), fortress.getId(), fortress.getSiegeEvent().isInProgress(), owner == null ? 0 : (int) ((System.currentTimeMillis() - fortress.getOwnDate().getTimeInMillis()) / 1000L)));
        });
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x15);
        writeD(infos.size());
        infos.forEach(info -> {
            writeD(info.id);
            writeS(info.owner);
            writeD(info.status);
            writeD(info.siege);
        });
    }

    private static class FortressInfo {
        final int id;
        final int siege;
        final String owner;
        final boolean status;

        FortressInfo(String owner, int id, boolean status, int siege) {
            this.owner = owner;
            this.id = id;
            this.status = status;
            this.siege = siege;
        }
    }
}