package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.templates.manor.CropProcure;

import java.util.ArrayList;
import java.util.List;


/**
 * Format: (ch) dd [dddc]
 * d - manor id
 * d - size
 * [
 * d - crop id
 * d - sales
 * d - price
 * c - reward type
 * ]
 */
public final class RequestSetCrop extends L2GameClientPacket {
    private int count, manorId;

    private long[] items; // _size*4

    @Override
    protected void readImpl() {
        manorId = readD();
        count = readD();
        if (count * 21 > buf.remaining() || count > Short.MAX_VALUE || count < 1) {
            count = 0;
            return;
        }
        items = new long[count * 4];
        for (int i = 0; i < count; i++) {
            items[i * 4 + 0] = readD();
            items[i * 4 + 1] = readQ();
            items[i * 4 + 2] = readQ();
            items[i * 4 + 3] = readC();
            if (items[i * 4 + 0] < 1 || items[i * 4 + 1] < 0 || items[i * 4 + 2] < 0) {
                count = 0;
                return;
            }
        }
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || count == 0)
            return;

        if (activeChar.getClan() == null) {
            activeChar.sendActionFailed();
            return;
        }

        Castle caslte = ResidenceHolder.getCastle(manorId);
        if (caslte.getOwnerId() != activeChar.getClanId() // clan owns castle
                || (activeChar.getClanPrivileges() & Clan.CP_CS_MANOR_ADMIN) != Clan.CP_CS_MANOR_ADMIN) // has manor rights
        {
            activeChar.sendActionFailed();
            return;
        }

        List<CropProcure> crops = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int id = (int) items[i * 4];
            long sales = items[i * 4 + 1];
            long price = items[i * 4 + 2];
            int type = (int) items[i * 4 + 3];
            if (id > 0) {
                CropProcure s = CastleManorManager.INSTANCE.getNewCropProcure(id, sales, type, price, sales);
                crops.add(s);
            }
        }

        caslte.setCropProcure(crops, CastleManorManager.PERIOD_NEXT);
        caslte.saveCropData(CastleManorManager.PERIOD_NEXT);
    }
}