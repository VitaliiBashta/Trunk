package l2trunk.loginserver.gameservercon.lspackets;

import l2trunk.loginserver.gameservercon.SendablePacket;

public class KickPlayer extends SendablePacket {
    private final String account;

    public KickPlayer(String login) {
        this.account = login;
    }

    @Override
    protected void writeImpl() {
        writeC(0x03);
        writeS(account);
    }
}