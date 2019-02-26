package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.CCPHelpers.CCPPassword;
import l2trunk.gameserver.scripts.Functions;

import java.util.Collections;
import java.util.List;

public class Password extends Functions implements IVoicedCommandHandler {
    private static final List<String> COMMANDS = List.of("password");

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        if (args.length() > 0)
            CCPPassword.setNewPassword(player, args.split(" "));
        else
            player.sendMessage("Use it like that: .password oldPassword newPassword newPassword");
        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMANDS;
    }
}