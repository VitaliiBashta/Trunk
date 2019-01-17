package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

public final class ExBR_ExtraUserInfo extends L2GameServerPacket {
    private final int _objectId;
    private final int _effect3;
    private final int _lectureMark;

    public ExBR_ExtraUserInfo(Player cha) {
        _objectId = cha.getObjectId();
        _effect3 = cha.getAbnormalEffect3();
        _lectureMark = cha.getLectureMark();
    }

    @Override
    protected void writeImpl() {
        writeEx(0xDA);
        writeD(_objectId); //object id of player
        writeD(_effect3); // event effect id
        writeC(_lectureMark);
    }
}