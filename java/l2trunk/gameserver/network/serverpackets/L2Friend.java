package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

public class L2Friend extends L2GameServerPacket {
    private final boolean _add;
    private final boolean _online;
    private final String _name;
    private final int _object_id;

    public L2Friend(Player player, boolean add) {
        _add = add;
        _name = player.getName();
        _object_id = player.objectId();
        _online = true;
    }

    public L2Friend(String name, boolean add, boolean online, int object_id) {
        _name = name;
        _add = add;
        _object_id = object_id;
        _online = online;
    }

    @Override
    protected final void writeImpl() {
        writeC(0x76);
        writeD(_add ? 1 : 3); // 1 - добавить друга в спикок, 3 удалить друга со списка
        writeD(0); //и снова тут идет ID персонажа в списке оффа, не object id
        writeS(_name);
        writeD(_online ? 1 : 0); // онлайн или оффлайн
        writeD(_object_id); //objectId if online
    }
}