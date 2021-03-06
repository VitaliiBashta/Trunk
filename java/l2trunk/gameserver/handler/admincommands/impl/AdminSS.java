package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.SevenSigns;

import java.util.List;

public final class AdminSS implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().Menu)
            return false;

        switch (comm) {
            case "admin_ssq_change": {
                if (wordList.length > 2) {
                    int period = Integer.parseInt(wordList[1]);
                    int minutes = Integer.parseInt(wordList[2]);
                    SevenSigns.INSTANCE.changePeriod(period, minutes * 60);
                } else if (wordList.length > 1) {
                    int period = Integer.parseInt(wordList[1]);
                    SevenSigns.INSTANCE.changePeriod(period);
                } else
                    SevenSigns.INSTANCE.changePeriod();
                break;
            }
            case "admin_ssq_time": {
                if (wordList.length > 1) {
                    int time = Integer.parseInt(wordList[1]);
                    SevenSigns.INSTANCE.setTimeToNextPeriodChange(time);
                }
                break;
            }
            case "admin_ssq_cabal": {
                if (wordList.length > 3) {
                    int player = Integer.parseInt(wordList[1]); // getPlayer objectid
                    int cabal = Integer.parseInt(wordList[2]); // null dusk dawn
                    int seal = Integer.parseInt(wordList[3]); // null avarice gnosis strife
                    SevenSigns.INSTANCE.setPlayerInfo(player, cabal, seal);
                }
                break;
            }
        }
        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_ssq_change",
                "admin_ssq_time",
                "admin_ssq_cabal");
    }
}