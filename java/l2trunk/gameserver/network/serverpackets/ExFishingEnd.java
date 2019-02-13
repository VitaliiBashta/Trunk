package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

/**
 * Format: (ch) dc
 * d: character object id
 * c: 1 if won 0 if failed
 */
public class ExFishingEnd extends L2GameServerPacket {
    private final int _charId;
    private final boolean _win;

    public ExFishingEnd(Player character, boolean win) {
        _charId = character.objectId();
        _win = win;
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x1f);
        writeD(_charId);
        writeC(_win ? 1 : 0);
    }
}