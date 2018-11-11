package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.NpcString;


public class NpcSay extends NpcStringContainer {
    private final int _objId;
    private final int _type;
    private final int _id;

    public NpcSay(NpcInstance npc, ChatType chatType, String text) {
        this(npc, chatType, NpcString.NONE, text);
    }

    public NpcSay(NpcInstance npc, ChatType chatType, NpcString npcString, String... params) {
        super(npcString, params);
        _objId = npc.getObjectId();
        _id = npc.getNpcId();
        _type = chatType.ordinal();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x30);
        writeD(_objId);
        writeD(_type);
        writeD(1000000 + _id);
        writeElements();
    }
}