package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.Henna;

public final class HennaItemInfo extends L2GameServerPacket {
    private final Player player;
    private final Henna henna;

    public HennaItemInfo(Henna henna, Player player) {
        this.player = player;
        this.henna = henna;
    }

    @Override
    protected final void writeImpl() {

        writeC(0xe4);
        writeD(henna.symbolId); //symbol Id
        writeD(henna.dyeId); //item id of dye
        writeQ(henna.drawCount);
        writeQ(henna.price);
        writeD(1); //able to draw or not 0 is false and 1 is true
        writeQ(player.getAdena());
        writeD(player.getINT()); //current INT
        writeC(player.getINT() + henna.statINT); //equip INT
        writeD(player.getSTR()); //current STR
        writeC(player.getSTR() + henna.statSTR); //equip STR
        writeD(player.getCON()); //current CON
        writeC(player.getCON() + henna.statCON); //equip CON
        writeD(player.getMEN()); //current MEM
        writeC(player.getMEN() + henna.statMEN); //equip MEM
        writeD(player.getDEX()); //current DEX
        writeC(player.getDEX() + henna.statDEX); //equip DEX
        writeD(player.getWIT()); //current WIT
        writeC(player.getWIT() + henna.statWIT); //equip WIT
    }
}