package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.network.serverpackets.KeyPacket;
import l2trunk.gameserver.network.serverpackets.SendStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtocolVersion extends L2GameClientPacket {
    private static final Logger _log = LoggerFactory.getLogger(ProtocolVersion.class);

    private int protocol;

    protected void readImpl() {
        protocol = readD();

        if ((_buf.remaining() > 260)) {
            _buf.position(_buf.position() + 260);
        }
    }

    protected void runImpl() {
        if (protocol == -2) {
            _client.closeNow(false);
            return;
        } else if (protocol == -3) {
            _log.info("Status request from IP : " + getClient().getIpAddr());
            getClient().close(new SendStatus());
            return;
        } else if (protocol < Config.MIN_PROTOCOL_REVISION || protocol > Config.MAX_PROTOCOL_REVISION) {
            _log.warn("Unknown protocol revision : " + protocol + ", client : " + _client);
            getClient().close(new KeyPacket(null));
            return;
        }

        _client.setSystemVersion(Config.LATEST_SYSTEM_VER);

        sendPacket(new KeyPacket(_client.enableCrypt()));
    }

    private boolean setHwidAndVer() {

            return false;

//        byte[] result = _client.getDecryptedProtocol(hwidData);
//        Charset encoding = Charset.forName("UTF-8");
//
//        String strangeHwid = new String(Arrays.copyOfRange(result, 0, 38), encoding);
//        StringBuilder builder = new StringBuilder();
//        for (int i = 0; i < strangeHwid.length(); i += 2)
//            builder.append(strangeHwid, i, i + 1);
//
//        String fileId = new String(Arrays.copyOfRange(result, 40, 58), encoding);
//
//        byte[] systemVerArray = Arrays.copyOfRange(result, 60, 66);
//        Collections.reverse(Arrays.asList(systemVerArray));
//        int systemVer = ByteBuffer.wrap(systemVerArray).getInt();
//
//        if (systemVer != Config.LATEST_SYSTEM_VER || !builder.toString().contains("-")) {
//            return false;
//        }
//
//        _client.setSystemVersion(systemVer);
//        getClient().setFileId(fileId);
//
//        return true;
    }

    @Override
    public String getType() {
        return getClass().getSimpleName();
    }
}