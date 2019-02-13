package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.utils.Location;

import java.util.HashMap;
import java.util.Map;


public final class PartyMemberPosition extends L2GameServerPacket {
    private final Map<Integer, Location> positions = new HashMap<>();

    public PartyMemberPosition add(Player actor) {
        positions.put(actor.objectId(), actor.getLoc());
        return this;
    }

    public int size() {
        return positions.size();
    }

    @Override
    protected final void writeImpl() {
        writeC(0xba);
        writeD(positions.size());
        positions.forEach((k, v) -> {
            writeD(k);
            writeD(v.x);
            writeD(v.y);
            writeD(v.z);
        });
    }
}