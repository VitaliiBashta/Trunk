package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;

/**
 * sample
 * 06 8f19904b 2522d04b 00000000 80 950c0000 4af50000 08f2ffff 0000    - 0 damage (missed 0x80)
 * 06 85071048 bc0e504b 32000000 10 fc41ffff fd240200 a6f5ffff 0100 bc0e504b 33000000 10                                     3....
 * <p>
 * format
 * dddc dddh (ddc)
 */
public final class Attack extends L2GameServerPacket {
    public final boolean _soulshot;
    private final int _attackerId;
    private final int grade;
    private final int _x, _y, _z, _tx, _ty, _tz;
    private Hit[] hits;

    public Attack(Creature attacker, Creature target, boolean ss, int grade) {
        _attackerId = attacker.getObjectId();
        _soulshot = ss;
        this.grade = grade;
        _x = attacker.getX();
        _y = attacker.getY();
        _z = attacker.getZ();
        _tx = target.getX();
        _ty = target.getY();
        _tz = target.getZ();
        hits = new Hit[0];
    }

    /**
     * Add this hit (target, damage, miss, critical, shield) to the Server-Client packet Attack.<BR><BR>
     */
    public void addHit(GameObject target, int damage, boolean miss, boolean crit, boolean shld) {
        // Get the last position in the hits table
        int pos = hits.length;

        // Create a new Hit object
        Hit[] tmp = new Hit[pos + 1];

        // Add the new Hit object to hits table
        System.arraycopy(hits, 0, tmp, 0, hits.length);
        tmp[pos] = new Hit(target, damage, miss, crit, shld);
        hits = tmp;
    }

    /**
     * Return True if the Server-Client packet Attack conatins at least 1 hit.<BR><BR>
     */
    public boolean hasHits() {
        return hits.length > 0;
    }

    @Override
    protected final void writeImpl() {
        Player activeChar = getClient().getActiveChar();

        boolean shouldSeeShots = !(activeChar != null && activeChar.isNotShowBuffAnim());

        writeC(0x33);

        writeD(_attackerId);
        writeD(hits[0].targetId);
        writeD(hits[0].damage);
        writeC(shouldSeeShots ? hits[0].flags : 0);
        writeD(_x);
        writeD(_y);
        writeD(_z);
        writeH(hits.length - 1);
        for (int i = 1; i < hits.length; i++) {
            writeD(hits[i].targetId);
            writeD(hits[i].damage);
            writeC(shouldSeeShots ? hits[i].flags : 0);
        }
        writeD(_tx);
        writeD(_ty);
        writeD(_tz);
    }

    private class Hit {
        final int targetId;
        final int damage;
        int flags;

        Hit(GameObject target, int damage, boolean miss, boolean crit, boolean shld) {
            targetId = target.getObjectId();
            this.damage = damage;
            if (_soulshot)
                flags |= 0x10 | grade;
            if (crit)
                flags |= 0x20;
            if (shld)
                flags |= 0x40;
            if (miss)
                flags |= 0x80;
        }
    }
}