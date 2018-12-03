package l2trunk.gameserver.handler.usercommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.GameTimeController;
import l2trunk.gameserver.handler.usercommands.IUserCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Support for /time command
 */
public final class Time implements IUserCommandHandler {
    private static final int COMMAND_ID = 77;

    private static final NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);
    private static final SimpleDateFormat sf = new SimpleDateFormat("H:mm");

    static {
        df.setMinimumIntegerDigits(2);
    }

    @Override
    public boolean useUserCommand(int id, Player activeChar) {
        if (COMMAND_ID != id)
            return false;

        int h = GameTimeController.INSTANCE.getGameHour();
        int m = GameTimeController.INSTANCE.getGameMin();

        SystemMessage2 sm;
        if (GameTimeController.INSTANCE.isNowNight())
            sm = new SystemMessage2(SystemMsg.THE_CURRENT_TIME_IS_S1S2_);
        else
            sm = new SystemMessage2(SystemMsg.THE_CURRENT_TIME_IS_S1S2);
        sm.addString(df.format(h)).addString(df.format(m));

        activeChar.sendPacket(sm);

        if (Config.ALT_SHOW_SERVER_TIME)
            activeChar.sendMessage(new CustomMessage("usercommandhandlers.Time.ServerTime", activeChar, sf.format(new Date(System.currentTimeMillis()))));

        return true;
    }

    @Override
    public final List<Integer> getUserCommandList() {
        return Collections.singletonList(COMMAND_ID);
    }
}
