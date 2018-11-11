package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;

/**
 * Format (ch)dddcc
 */
public class ExFishingStartCombat extends L2GameServerPacket {
    private final int _time;
    private final int _hp;
    private final int _lureType;
    private final int _deceptiveMode;
    private final int _mode;
    private final int char_obj_id;

    public ExFishingStartCombat(Creature character, int time, int hp, int mode, int lureType, int deceptiveMode) {
        char_obj_id = character.getObjectId();
        _time = time;
        _hp = hp;
        _mode = mode;
        _lureType = lureType;
        _deceptiveMode = deceptiveMode;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x27);

        writeD(char_obj_id);
        writeD(_time);
        writeD(_hp);
        writeC(_mode); // mode: 0 = resting, 1 = fighting
        writeC(_lureType); // 0 = newbie lure, 1 = normal lure, 2 = night lure
        writeC(_deceptiveMode); // Fish Deceptive Mode: 0 = no, 1 = yes
    }
}