package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

public class ExGoodsInventoryInfo extends L2GameServerPacket {

    public ExGoodsInventoryInfo(Player player) {

    }

    @Override
    protected void writeImpl() {
		/*
		* 203DA858   PUSH Engine.205127AC                      ASCII "QdSSQccSSh"
203DA8D0   PUSH Engine.20506EFC                      ASCII "dd"*/
    }
}
