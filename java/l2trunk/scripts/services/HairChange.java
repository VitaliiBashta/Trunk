package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ConfirmDlg;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Util;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class HairChange extends Functions {
    private static final int[] Male = {1, 1, 1, 1, 1, 0, 0};

    private void show() {
        if (player == null)
            return;

        if (Config.SERVICES_HAIR_CHANGE_ITEM_ID == -1) {
            player.sendMessage("This Service is turned off.");
            return;
        }

        NpcHtmlMessage html = new NpcHtmlMessage(5).setFile("scripts/services/HairChange/index.htm");

        for (int i = 0; i < 7; i++) {
            String button = "<button action=\"bypass -h scripts_services.HairChange:ask " + i + "\" width=34 height=34 back=\"L2UI_CT1.ItemWindow_DF_Frame_Down\" fore=\"L2UI_CT1.ItemWindow_DF_Frame\"/>";
            String prohibited = "<img src=\"L2UI_CT1.ItemWindow_DF_SlotBox_Disable\" width=\"32\" height=\"32\">";
            boolean result = player.getHairStyle() != i && (player.isMale()  || Male[i] != 0);

            html.replace("%hair_" + (i + 1) + "%", result ? button : prohibited);
            html.replace("%color_" + (i + 1) + "%", result ? "99CC00" : "CC3333");
        }

        html.replace("%now%", HairTypeName(player.getHairStyle()));

        player.sendPacket(html);
    }

    public void ask(String[] arg) {
        if (player == null)
            return;

        int id = toInt(arg[0]);

        String msg = new CustomMessage("Want to change your hairstyle from Type {0} to Type {1}? Change cost: {2}").addString(HairTypeName(player.getHairStyle())).addString(HairTypeName(id)).addString(Util.formatPay(player, Config.SERVICES_HAIR_CHANGE_COUNT, Config.SERVICES_HAIR_CHANGE_ITEM_ID)).toString();
        ConfirmDlg ask = new ConfirmDlg(SystemMsg.S1, 60000);
        ask.addString(msg);

        player.ask(ask, new AnswerListener(player, id));
    }

    private static boolean isCorrect(int id) {
        return true;
    }

    private static String HairTypeName(int id) {
        switch (id) {
            case 0:
                return "A";
            case 1:
                return "B";
            case 2:
                return "C";
            case 3:
                return "D";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "G";
        }
        return "?";
    }

    private static void changeHair(Player player, int id) {
        if (Util.getPay(player, Config.SERVICES_HAIR_CHANGE_ITEM_ID, Config.SERVICES_HAIR_CHANGE_COUNT, true)) {
            player.setHairStyle(id);
            player.sendMessage("Hairstyle successfully changed.");
            player.broadcastPacket(new MagicSkillUse(player,  6696));
            player.broadcastCharInfo();
        }
    }

    private class AnswerListener implements OnAnswerListener {
        private final Player _player;
        private final int _id;

        AnswerListener(Player player, int id) {
            _player = player;
            _id = id;
        }

        @Override
        public void sayYes() {
            if (_player == null || !_player.isOnline() || !isCorrect(_id)) {
                return;
            }
            changeHair(_player, _id);
            show();
        }

    }
}
