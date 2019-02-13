package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.templates.PlayerTemplate;

import java.util.ArrayList;
import java.util.List;


public final class NewCharacterSuccess extends L2GameServerPacket {
    // dddddddddddddddddddd
    private final List<PlayerTemplate> chars = new ArrayList<>();

    public void addChar(PlayerTemplate template) {
        chars.add(template);
    }

    @Override
    protected final void writeImpl() {
        writeC(0x0d);
        writeD(chars.size());

        chars.forEach(cha -> {
            writeD(cha.race.ordinal());
            writeD(cha.classId.id);
            writeD(0x46);
            writeD(cha.baseSTR);
            writeD(0x0a);
            writeD(0x46);
            writeD(cha.baseDEX);
            writeD(0x0a);
            writeD(0x46);
            writeD(cha.baseCON);
            writeD(0x0a);
            writeD(0x46);
            writeD(cha.baseINT);
            writeD(0x0a);
            writeD(0x46);
            writeD(cha.baseWIT);
            writeD(0x0a);
            writeD(0x46);
            writeD(cha.baseMEN);
            writeD(0x0a);
        });
    }
}