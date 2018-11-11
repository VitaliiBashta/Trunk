package l2trunk.gameserver.network.serverpackets.components;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;

public interface IStaticPacket {
    L2GameServerPacket packet(Player player);
}
