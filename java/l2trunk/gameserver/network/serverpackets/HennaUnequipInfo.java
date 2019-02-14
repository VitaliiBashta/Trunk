package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.Henna;

public final class HennaUnequipInfo extends L2GameServerPacket {
    private final int _str;
    private final int _con;
    private final int _dex;
    private final int _int;
    private final int _wit;
    private final int _men;
    private final long _adena;
    private final Henna henna;

    public HennaUnequipInfo(Henna henna, Player player) {
        this.henna = henna;
        _adena = player.getAdena();
        _str = player.getSTR();
        _dex = player.getDEX();
        _con = player.getCON();
        _int = player.getINT();
        _wit = player.getWIT();
        _men = player.getMEN();
    }

    @Override
    protected final void writeImpl() {

        writeC(0xE7);
        writeD(henna.symbolId); //symbol Id
        writeD(henna.dyeId); //item id of dye

        writeQ(henna.drawCount);
        writeQ(henna.price);
        writeD(1); //able to draw or not 0 is false and 1 is true
        writeQ(_adena);

        writeD(_int); //current INT
        writeC(_int + henna.statINT); //equip INT
        writeD(_str); //current STR
        writeC(_str + henna.statSTR); //equip STR
        writeD(_con); //current CON
        writeC(_con + henna.statCON); //equip CON
        writeD(_men); //current MEM
        writeC(_men + henna.statMEN); //equip MEM
        writeD(_dex); //current DEX
        writeC(_dex + henna.statDEX); //equip DEX
        writeD(_wit); //current WIT
        writeC(_wit + henna.statWIT); //equip WIT
    }
}