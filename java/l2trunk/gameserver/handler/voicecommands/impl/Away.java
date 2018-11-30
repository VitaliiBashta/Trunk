package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.instancemanager.AwayManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;

import java.util.Arrays;
import java.util.List;

public class Away implements IVoicedCommandHandler {
    private final List<String> VOICED_COMMANDS = Arrays.asList("away", "back");

    public boolean useVoicedCommand(String command, Player activeChar, String text) {
        if (command.startsWith("away"))
            return away(activeChar, text);

        if (command.startsWith("back"))
            return back(activeChar);

        return false;
    }

    private boolean away(Player activeChar, String text) {
        SiegeEvent<?, ?> siege = (SiegeEvent<?, ?>) activeChar.getEvent(SiegeEvent.class);

        if (activeChar.isInAwayingMode()) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Already", activeChar));
            return false;
        }

        if ((!activeChar.isInZone(Zone.ZoneType.peace_zone)) && (Config.AWAY_PEACE_ZONE)) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.PieceOnly", activeChar));
            return false;
        }

        if ((activeChar.isMovementDisabled()) || (activeChar.isAlikeDead())) {
            return false;
        }

        if (siege != null) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Siege", activeChar));
            return false;
        }

        if (activeChar.isCursedWeaponEquipped()) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Cursed", activeChar));
            return false;
        }

        if (activeChar.isInDuel()) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Duel", activeChar));
            return false;
        }

        if ((activeChar.isInParty()) && (activeChar.getParty().isInDimensionalRift())) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Rift", activeChar));
            return false;
        }

        if ((activeChar.isInOlympiadMode()) || (activeChar.getOlympiadGame() != null)) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Olympiad", activeChar));
            return false;
        }

        if (activeChar.isInObserverMode()) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Observer", activeChar));
            return false;
        }

        if ((activeChar.getKarma() > 0) || (activeChar.getPvpFlag() > 0)) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Pvp", activeChar));
            return false;
        }

        if (text == null) {
            text = "";
        }

        if (text.length() > 10) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Text", activeChar));
            return false;
        }

        if (activeChar.getTarget() == null) {
            AwayManager.INSTANCE.setAway(activeChar, text);
        } else {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Target", activeChar));
            return false;
        }

        return true;
    }

    private boolean back(Player activeChar) {
        if (!activeChar.isInAwayingMode()) {
            activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.handler.voicecommands.impl.Away.Not", activeChar));
            return false;
        }
        AwayManager.INSTANCE.setBack(activeChar);
        return true;
    }

    public List<String> getVoicedCommandList() {
        return VOICED_COMMANDS;
    }
}