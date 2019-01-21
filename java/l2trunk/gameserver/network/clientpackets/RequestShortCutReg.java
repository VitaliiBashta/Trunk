package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.instances.player.ShortCut;
import l2trunk.gameserver.network.serverpackets.ShortCutRegister;

public final class RequestShortCutReg extends L2GameClientPacket {
    private int type, id, slot, page, lvl, characterType;

    @Override
    protected void readImpl() {
        type = readD();
        int slot = readD();
        id = readD();
        lvl = readD();
        characterType = readD();

        this.slot = slot % 12;
        page = slot / 12;
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null)
            return;

        if (page < 0 || page > ShortCut.PAGE_MAX) {
            activeChar.sendActionFailed();
            return;
        }

        ShortCut shortCut = new ShortCut(slot, page, type, id, lvl, characterType);
        activeChar.sendPacket(new ShortCutRegister(activeChar, shortCut));
        activeChar.registerShortCut(shortCut);
    }
}