package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.network.serverpackets.ConfirmDlg;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Util;

public final class LevelPanel extends Functions {
    public void show() {
        if (player == null)
            return;

        if ((!Config.SERVICES_LEVEL_UP_ENABLE) && (!Config.SERVICES_DELEVEL_ENABLE)) {
            player.sendMessage("Service is turned off.");
            return;
        }
        NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("scripts/services/LevelPanel/index.htm");

        String up;
        String lower;
        if (!Config.SERVICES_LEVEL_UP_ENABLE) {
            up = HtmCache.INSTANCE.getNotNull("scripts/services/LevelPanel/up_off.htm", player);
            up = up.replace("{cost}", "<font color=\"CC3333\">" + new CustomMessage("Service is turned off.").toString() + "</font>");
            html.replace("%up%", up);
        } else {
            up = HtmCache.INSTANCE.getNotNull("scripts/services/LevelPanel/up.htm", player);
            up = up.replace("{cost}", Util.formatPay(player, Config.SERVICES_LEVEL_UP[1], Config.SERVICES_LEVEL_UP[0]));
            html.replace("%up%", up);
        }
        if (!Config.SERVICES_DELEVEL_ENABLE) {
            lower = HtmCache.INSTANCE.getNotNull("scripts/services/LevelPanel/lower_off.htm", player);
            lower = lower.replace("{cost}", "<font color=\"CC3333\">" + "Service is not allowed.".toString() + "</font>");
            html.replace("%lower%", lower);
        } else {
            lower = HtmCache.INSTANCE.getNotNull("scripts/services/LevelPanel/lower.htm", player);
            lower = lower.replace("{cost}", Util.formatPay(player, Config.SERVICES_DELEVEL[1], Config.SERVICES_DELEVEL[0]));
            html.replace("%lower%", lower);
        }
        html.replace("%up_info%", up);
        html.replace("%lower_info%", lower);

        player.sendPacket(html);
    }

    public void calc() {
        if (player == null) {
            return;
        }
        if (player.getLevel() >= 85) {
            player.sendMessage("You are already max occupation.");
            return;
        }
        String msg = "Do you want to maximize your occupation for 10 coins?";
        ConfirmDlg ask = new ConfirmDlg(SystemMsg.S1, 60000);
        ask.addString(msg);
        player.ask(ask, new AnswerListener(player));
    }

    private class AnswerListener implements OnAnswerListener {
        private final Player _player;

        AnswerListener(Player player) {
            _player = player;
        }

        @Override
        public void sayYes() {
            if (!_player.isOnline()) {
                return;
            }
            int level = 85;
            if (!LevelPanel.correct(level, _player.getActiveClass().isBase())) {
                _player.sendMessage("Incorrect occupation!");
                return;
            }
            if ((!Config.SERVICES_LEVEL_UP_ENABLE)) {
                _player.sendMessage("You can not levelUp.");
                return;
            }
            if (Util.getPay(_player, Config.SERVICES_LEVEL_UP[0], 10, true)) {
                _player.sendMessage(new CustomMessage("occupation.change").addNumber(_player.getLevel()).addNumber(level));
                long exp = Experience.getExpForLevel(level) - _player.getExp();
                _player.addExpAndSp(exp, 899999999);
            }
        }

    }

    private static boolean correct(int level, boolean base) {
        return level <= (base ? Experience.getMaxLevel() : Experience.getMaxSubLevel());
    }
}
