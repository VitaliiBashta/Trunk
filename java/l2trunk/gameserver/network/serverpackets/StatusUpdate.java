package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Даные параметры актуальны для С6(Interlude), 04/10/2007, протокол 746
 */
public final class StatusUpdate extends L2GameServerPacket {
    /**
     * Даный параметр отсылается оффом в паре с MAX_HP
     * Сначала CUR_HP, потом MAX_HP
     */
    public final static int CUR_HP = 0x09;
    public final static int MAX_HP = 0x0a;

    /**
     * Даный параметр отсылается оффом в паре с MAX_MP
     * Сначала CUR_MP, потом MAX_MP
     */
    public final static int CUR_MP = 0x0b;
    public final static int MAX_MP = 0x0c;

    /**
     * Меняется отображение только в инвентаре, для статуса требуется UserInfo
     */
    public final static int CUR_LOAD = 0x0e;

    /**
     * Меняется отображение только в инвентаре, для статуса требуется UserInfo
     */
    public final static int MAX_LOAD = 0x0f;

    public final static int PVP_FLAG = 0x1a;
    public final static int KARMA = 0x1b;

    /**
     * Даный параметр отсылается оффом в паре с MAX_CP
     * Сначала CUR_CP, потом MAX_CP
     */
    public final static int CUR_CP = 0x21;
    public final static int MAX_CP = 0x22;

    private final int _objectId;
    private final List<Pair<Integer,Integer>> attributes = new ArrayList<>();

    public StatusUpdate(int objectId) {
        _objectId = objectId;
    }

    public void addAttribute(int id, int level) {
        attributes.add(Pair.of(id, level));
    }

    @Override
    protected final void writeImpl() {
        writeC(0x18);
        writeD(_objectId);
        writeD(attributes.size());

        attributes.forEach(attr-> {
            writeD(attr.getKey());
            writeD(attr.getValue());
        });
    }

    public boolean hasAttributes() {
        return !attributes.isEmpty();
    }

}