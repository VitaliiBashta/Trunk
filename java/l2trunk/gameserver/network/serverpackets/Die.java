package l2trunk.gameserver.network.serverpackets;

import Elemental.managers.GmEventManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.RestartType;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.pledge.Clan;

import java.util.HashMap;
import java.util.Map;

public final class Die extends L2GameServerPacket {
    private final int objectId;
    private final boolean fake;
    private final Map<RestartType, Boolean> types = new HashMap<>();
    private boolean sweepable;

    public Die(Creature cha) {
        objectId = cha.objectId();
        fake = !cha.isDead();

        if (cha instanceof MonsterInstance)
            sweepable = ((MonsterInstance) cha).isSweepActive();
        else if (cha instanceof Player && GmEventManager.INSTANCE.canResurrect((Player)cha)) {
            Player player = (Player) cha;
            put(RestartType.FIXED, player.getPlayerAccess().ResurectFixed || ((player.getInventory().getCountOf(10649) > 0 || player.getInventory().getCountOf(13300) > 0) && !player.isOnSiegeField()));
            put(RestartType.AGATHION, player.isAgathionResAvailable());
            put(RestartType.TO_VILLAGE, true);

            Clan clan = player.getClan();
            if (clan != null) {
                put(RestartType.TO_CLANHALL, clan.getHasHideout() > 0);
                put(RestartType.TO_CASTLE, clan.getCastle() > 0);
                put(RestartType.TO_FORTRESS, clan.getHasFortress() > 0);
            }

            cha.getEvents().forEach(e -> e.checkRestartLocs(player, types));

        }
    }

    @Override
    protected final void writeImpl() {
        if (fake)
            return;

        writeC(0x00);
        writeD(objectId);
        writeD(get(RestartType.TO_VILLAGE)); // to nearest village
        writeD(get(RestartType.TO_CLANHALL)); // to hide away
        writeD(get(RestartType.TO_CASTLE)); // to castle
        writeD(get(RestartType.TO_FLAG));// to siege HQ
        writeD(sweepable ? 0x01 : 0x00); // sweepable  (blue glow)
        writeD(get(RestartType.FIXED));// FIXED
        writeD(get(RestartType.TO_FORTRESS));// fortress
        writeC(0); //show die animation
        writeD(get(RestartType.AGATHION));//agathion ress button
        writeD(0x00); //additional free space
    }

    private void put(RestartType t, boolean b) {
        types.put(t, b);
    }

    private boolean get(RestartType t) {
        Boolean b = types.get(t);
        return b != null && b;
    }
}