package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.matching.MatchingRoom;

public class PartyRoomInfo extends L2GameServerPacket {
    private final int _id;
    private final int _minLevel;
    private final int _maxLevel;
    private final int _lootDist;
    private final int _maxMembers;
    private final int _location;
    private final String _title;

    public PartyRoomInfo(MatchingRoom room) {
        _id = room.getId();
        _minLevel = room.getMinLevel();
        _maxLevel = room.getMaxLevel();
        _lootDist = room.getLootType();
        _maxMembers = room.getMaxMembersSize();
        _location = room.getLocationId();
        _title = room.getTopic();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x9d);
        writeD(_id); // room id
        writeD(_maxMembers); //max members
        writeD(_minLevel); //min level
        writeD(_maxLevel); //max level
        writeD(_lootDist); //loot distribution 1-Random 2-Random includ. etc
        writeD(_location); //location
        writeS(_title); // room name
    }
}