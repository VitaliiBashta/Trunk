package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.matching.MatchingRoom;

public class ExMpccRoomInfo extends L2GameServerPacket {
    private final int _index;
    private final int _memberSize;
    private final int _minLevel;
    private final int _maxLevel;
    private final int _lootType;
    private final int _locationId;
    private final String _topic;

    public ExMpccRoomInfo(MatchingRoom matching) {
        _index = matching.getId();
        _locationId = matching.getLocationId();
        _topic = matching.getTopic();
        _minLevel = matching.getMinLevel();
        _maxLevel = matching.getMaxLevel();
        _memberSize = matching.getMaxMembersSize();
        _lootType = matching.getLootType();
    }

    @Override
    public void writeImpl() {
        writeEx(0x9B);
        //
        writeD(_index); //index
        writeD(_memberSize); // member size 1-50
        writeD(_minLevel); //min occupation
        writeD(_maxLevel); //max occupation
        writeD(_lootType); //loot type
        writeD(_locationId); //location id as party room
        writeS(_topic); //topic
    }
}