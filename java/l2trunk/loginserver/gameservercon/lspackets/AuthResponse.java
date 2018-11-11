package l2trunk.loginserver.gameservercon.lspackets;

import l2trunk.loginserver.gameservercon.GameServer;
import l2trunk.loginserver.gameservercon.SendablePacket;

public class AuthResponse extends SendablePacket {
    private final int serverId;
    private final String name;

    public AuthResponse(GameServer gs) {
        serverId = gs.getId();
        name = gs.getName();
    }

    @Override
    protected void writeImpl() {
        writeC(0x00);
        writeC(serverId);
        writeS(name);
    }
}