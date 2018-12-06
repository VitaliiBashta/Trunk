package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.instancemanager.CursedWeaponsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class ExCursedWeaponList extends L2GameServerPacket {
    private final Set<Integer> cursedWeapon_ids;

    public ExCursedWeaponList() {
        cursedWeapon_ids = CursedWeaponsManager.INSTANCE.getCursedWeaponsIds();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x46);
        List<Integer> ids = new ArrayList<>(cursedWeapon_ids);
        writeDD(ids);
    }
}