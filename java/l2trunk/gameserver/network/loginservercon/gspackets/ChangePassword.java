package l2trunk.gameserver.network.loginservercon.gspackets;

import l2trunk.gameserver.network.loginservercon.SendablePacket;


public final class ChangePassword extends SendablePacket {
    private final String account;
    private final String oldPass;
    private final String newPass;
    private final String hwid;

    public ChangePassword(String account, String oldPass, String newPass, String hwid) {
        this.account = account;
        this.oldPass = oldPass;
        this.newPass = newPass;
        this.hwid = hwid;
    }

    protected void writeImpl() {
        writeC(0x08);
        writeS(account);
        writeS(oldPass);
        writeS(newPass);
        writeS(hwid);
    }
}
