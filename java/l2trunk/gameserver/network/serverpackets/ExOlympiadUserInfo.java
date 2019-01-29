package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

public class ExOlympiadUserInfo extends L2GameServerPacket {
    private final int _side;
    private final int class_id;
    private final int curHp;
    private final int maxHp;
    private final int curCp;
    private final int maxCp;
    private final String _name;
    private int obj_id = 0;

    public ExOlympiadUserInfo(Player player, int side) {
        _side = side;
        obj_id = player.getObjectId();
        class_id = player.getClassId().id;
        _name = player.getName();
        curHp = (int) player.getCurrentHp();
        maxHp = player.getMaxHp();
        curCp = (int) player.getCurrentCp();
        maxCp = player.getMaxCp();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x7a);
        writeC(_side);
        writeD(obj_id);
        writeS(_name);
        writeD(class_id);
        writeD(curHp);
        writeD(maxHp);
        writeD(curCp);
        writeD(maxCp);
    }
}