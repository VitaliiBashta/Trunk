package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.Henna;

import java.util.ArrayList;
import java.util.List;

public final class GMHennaInfo extends L2GameServerPacket {
    private final List<Henna> hennas = new ArrayList<>();
    private final int _str;
    private final int _con;
    private final int _dex;
    private final int _int;
    private final int _wit;
    private final int _men;

    public GMHennaInfo(final Player cha) {
        _str = cha.getHennaStatSTR();
        _con = cha.getHennaStatCON();
        _dex = cha.getHennaStatDEX();
        _int = cha.getHennaStatINT();
        _wit = cha.getHennaStatWIT();
        _men = cha.getHennaStatMEN();

        for (int i = 0; i < 3; i++) {
            Henna h = cha.getHenna(i + 1);
            if (h != null)
                hennas.add(h);
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xf0);

        writeC(_int);
        writeC(_str);
        writeC(_con);
        writeC(_men);
        writeC(_dex);
        writeC(_wit);
        writeD(3);
        writeD(hennas.size());
        hennas.forEach(henna ->  {
            writeD(henna.symbolId);
            writeD(henna.symbolId);
        });
    }
}