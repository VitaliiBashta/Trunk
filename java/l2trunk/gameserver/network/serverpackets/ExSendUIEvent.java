package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.NpcString;

public final class ExSendUIEvent extends NpcStringContainer {
    private final int objectId;
    private final boolean isHide;
    private final boolean isIncrease;
    private final int startTime;
    private final int endTime;

    public ExSendUIEvent(Player player, boolean isHide, boolean isIncrease, int startTime, int endTime, String... params) {
        this(player, isHide, isIncrease, startTime, endTime, NpcString.NONE, params);
    }

    public ExSendUIEvent(Player player, boolean isHide, boolean isIncrease, int startTime, int endTime, NpcString npcString, String... params) {
        super(npcString, params);
        objectId = player.getObjectId();
        this.isHide = isHide;
        this.isIncrease = isIncrease;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    protected void writeImpl() {
        writeC(0xFE);
        writeH(0x8E);
        writeD(objectId);
        writeD(isHide ? 0x01 : 0x00); // 0: show timer, 1: hide timer
        writeD(0x00); // unknown
        writeD(0x00); // unknown
        writeS(isIncrease ? "1" : "0"); // "0": count negative, "1": count positive
        writeS(String.valueOf(startTime / 60)); // timer starting minute(s)
        writeS(String.valueOf(startTime % 60)); // timer starting second(s)
        writeS(String.valueOf(endTime / 60)); // timer length minute(s) (timer will disappear 10 seconds before it ends)
        writeS(String.valueOf(endTime % 60)); // timer length second(s) (timer will disappear 10 seconds before it ends)
        writeElements();
    }
}