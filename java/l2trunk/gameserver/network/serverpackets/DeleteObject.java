package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;

/**
 * Пример:
 * 08
 * a5 04 31 48 ObjectId
 * 00 00 00 7c unk
 * <p>
 * format  d
 */
public final class DeleteObject extends L2GameServerPacket {
    private final int objectId;

    public DeleteObject(GameObject obj) {
        objectId = obj.objectId();
    }

    @Override
    protected final void writeImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || activeChar.objectId() == objectId)
            return;

        writeC(0x08);
        writeD(objectId);
        writeD(0x01); // Что-то странное. Если объект сидит верхом то при 0 он сперва будет ссажен, при 1 просто пропадет.
    }

    @Override
    public String getType() {
        return super.getType() + " " + GameObjectsStorage.findObject(objectId) + " (" + objectId + ")";
    }
}