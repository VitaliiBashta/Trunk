package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public class RequestShowBoard extends L2GameClientPacket {
    @SuppressWarnings("unused")
    private int _unknown;

    /**
     * packet type id 0x5E
     * <p>
     * sample
     * <p>
     * 5E
     * 01 00 00 00
     * <p>
     * format:		cd
     */
    @Override
    public void readImpl() {
        _unknown = readD();
    }

    @Override
    public void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || activeChar.isBlocked())
            return;
        if (activeChar.isCursedWeaponEquipped())
            return;
        if (Config.COMMUNITYBOARD_ENABLED && !activeChar.isJailed()) {
            ICommunityBoardHandler handler = CommunityBoardManager.getInstance().getCommunityHandler(Config.BBS_DEFAULT);
            if (handler != null)
                handler.onBypassCommand(activeChar, Config.BBS_DEFAULT);
        } else
            activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
    }
}
