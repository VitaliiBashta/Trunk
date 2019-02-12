package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

public class NickNameChanged extends L2GameServerPacket {
    private final int objectId;
    private final String title;

    public NickNameChanged(Creature cha) {
        objectId = cha.objectId();
        title = cha.getTitle();
    }

    @Override
    protected void writeImpl() {
        writeC(0xCC);
        writeD(objectId);
        writeS(title);
    }
}