package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public final class RequestBlock extends L2GameClientPacket {
    // format: cd(S)
    private static final Logger LOG = LoggerFactory.getLogger(RequestBlock.class);

    private final static int BLOCK = 0;
    private final static int UNBLOCK = 1;
    private final static int BLOCKLIST = 2;
    private final static int ALLBLOCK = 3;
    private final static int ALLUNBLOCK = 4;

    private Integer type;
    private String targetName = null;

    @Override
    protected void readImpl() {
        type = readD(); //0x00 - setBlock, 0x01 - setBlock, 0x03 - allblock, 0x04 - allunblock

        if (type == BLOCK || type == UNBLOCK)
            targetName = readS(16);
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        switch (type) {
            case BLOCK:
                activeChar.addToBlockList(targetName);
                break;
            case UNBLOCK:
                activeChar.removeFromBlockList(targetName);
                break;
            case BLOCKLIST:
                activeChar.sendPacket(SystemMsg.IGNORE_LIST);
                activeChar.getBlockList().forEach(activeChar::sendMessage);
                activeChar.sendPacket(SystemMsg.__EQUALS__);
                break;
            case ALLBLOCK:
                activeChar.setBlockAll(true);
                activeChar.sendPacket(SystemMsg.YOU_ARE_NOW_BLOCKING_EVERYTHING);
                activeChar.sendEtcStatusUpdate();
                break;
            case ALLUNBLOCK:
                activeChar.setBlockAll(false);
                activeChar.sendPacket(SystemMsg.YOU_ARE_NO_LONGER_BLOCKING_EVERYTHING);
                activeChar.sendEtcStatusUpdate();
                break;
            default:
                LOG.info("Unknown 0x0a setBlock type: " + type);
        }
    }
}