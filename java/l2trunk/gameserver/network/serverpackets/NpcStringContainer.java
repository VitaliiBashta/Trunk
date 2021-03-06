package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.network.serverpackets.components.NpcString;

abstract class NpcStringContainer extends L2GameServerPacket {
    private final NpcString _npcString;
    private final String[] _parameters = new String[5];

    NpcStringContainer(NpcString npcString, String... arg) {
        _npcString = npcString;
        System.arraycopy(arg, 0, _parameters, 0, arg.length);
    }

    void writeElements() {
        writeD(_npcString.getId());
        for (String st : _parameters)
            writeS(st);
    }
}
