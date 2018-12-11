package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.templates.manor.SeedProduction;

import java.util.ArrayList;
import java.util.List;


/**
 * Format: (ch) dd [ddd]
 * d - manor id
 * d - size
 * [
 * d - seed id
 * d - sales
 * d - price
 * ]
 */
public final class RequestSetSeed extends L2GameClientPacket {
    private int _count, _manorId;

    private long[] _items; // _size*3

    @Override
    protected void readImpl() {
        _manorId = readD();
        _count = readD();
        if (_count * 20 > _buf.remaining() || _count > Short.MAX_VALUE || _count < 1) {
            _count = 0;
            return;
        }
        _items = new long[_count * 3];
        for (int i = 0; i < _count; i++) {
            _items[i * 3 + 0] = readD();
            _items[i * 3 + 1] = readQ();
            _items[i * 3 + 2] = readQ();
            if (_items[i * 3 + 0] < 1 || _items[i * 3 + 1] < 0 || _items[i * 3 + 2] < 0) {
                _count = 0;
                return;
            }
        }
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || _count == 0)
            return;

        if (activeChar.getClan() == null) {
            activeChar.sendActionFailed();
            return;
        }

        Castle caslte = ResidenceHolder.getResidence(Castle.class, _manorId);
        if (caslte.getOwnerId() != activeChar.getClanId() // clan owns castle
                || (activeChar.getClanPrivileges() & Clan.CP_CS_MANOR_ADMIN) != Clan.CP_CS_MANOR_ADMIN) // has manor rights
        {
            activeChar.sendActionFailed();
            return;
        }

        List<SeedProduction> seeds = new ArrayList<>(_count);
        for (int i = 0; i < _count; i++) {
            int id = (int) _items[i * 3 ];
            long sales = _items[i * 3 + 1];
            long price = _items[i * 3 + 2];
            if (id > 0) {
                SeedProduction s = CastleManorManager.INSTANCE.getNewSeedProduction(id, sales, price, sales);
                seeds.add(s);
            }
        }

        caslte.setSeedProduction(seeds, CastleManorManager.PERIOD_NEXT);
        caslte.saveSeedData(CastleManorManager.PERIOD_NEXT);
    }
}