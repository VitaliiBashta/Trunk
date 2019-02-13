package l2trunk.gameserver.model.entity;

import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.CCPHelpers.*;
import l2trunk.gameserver.model.entity.CCPHelpers.itemLogs.CCPItemLogs;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.DeleteObject;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.scripts.quests._255_Tutorial;

import java.util.List;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

public enum CharacterControlPanel {
    INSTANCE;

    public String useCommand(Player activeChar, String text, String bypass) {
        // While some1 is currently writing secondary password
        if (activeChar.isBlocked() && !text.contains("secondaryPass")) {
            return null;
        }

        String[] param = text.split(" ");
        if (param.length == 0)
            return "char.htm";

        // Block Experience
        else if ("noe".equalsIgnoreCase(param[0])) {
            if (activeChar.isVarSet("NoExp")) {
                activeChar.unsetVar("NoExp");
            } else {
                activeChar.setVar("NoExp");
            }
        }
        // Auto Shoulshots
        else if ("soulshot".equalsIgnoreCase(param[0])) {
            if (activeChar.isVarSet("soulshot")) {
                activeChar.unsetVar("soulshot");
            } else {
                activeChar.setVar("soulshot");
            }
        }
        // Show Online Players
        else if ("online".equalsIgnoreCase(param[0])) {
            activeChar.sendMessage(CCPSmallCommands.showOnlineCount());
        } else if ("changeLog".equalsIgnoreCase(param[0])) {
            QuestState st = activeChar.getQuestState(_255_Tutorial.class);
            if (st != null) {
                String change = ChangeLogManager.INSTANCE.getChangeLog(ChangeLogManager.INSTANCE.getLatestChangeId());
                st.showTutorialHTML(change);
            }
        }
        // Item logs
        else if ("itemLogs".equalsIgnoreCase(param[0])) {
            CCPItemLogs.showPage(activeChar);
            return null;
        }
        // Show private stores Hide private stores / Fixed
        else if (Player.NO_TRADERS_VAR.equalsIgnoreCase(param[0])) {
            if (activeChar.isVarSet(Player.NO_TRADERS_VAR)) {
                activeChar.setNotShowTraders();
                activeChar.unsetVar(Player.NO_TRADERS_VAR);

                World.getAroundPlayers(activeChar).stream()
                        .filter(p -> p.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
                        .forEach(p -> p.broadcastUserInfo(true));
            } else {
                List<L2GameServerPacket> pls = World.getAroundPlayers(activeChar).stream()
                        .filter(p -> p.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
                        .map(DeleteObject::new)
                        .collect(Collectors.toList());

                activeChar.sendPacket(pls);
                activeChar.setNotShowTraders();
                activeChar.setVar(Player.NO_TRADERS_VAR);
            }
        }

        // Show skill animations
        else if (Player.NO_ANIMATION_OF_CAST_VAR.equalsIgnoreCase(param[0])) {
            if (activeChar.isVarSet(Player.NO_ANIMATION_OF_CAST_VAR)) {
                activeChar.setNotShowBuffAnim(false);
                activeChar.unsetVar(Player.NO_ANIMATION_OF_CAST_VAR);
            } else {
                activeChar.setNotShowBuffAnim(true);
                activeChar.setVar(Player.NO_ANIMATION_OF_CAST_VAR);
            }
        } else if ("blocktrade".equalsIgnoreCase(param[0])) {
            activeChar.setTradeRefusal(!activeChar.getTradeRefusal());
        } else if ("blockpartyinvite".equalsIgnoreCase(param[0])) {
            activeChar.setPartyInviteRefusal(!activeChar.getPartyInviteRefusal());
        } else if ("blockfriendinvite".equalsIgnoreCase(param[0])) {
            activeChar.setFriendInviteRefusal(!activeChar.getFriendInviteRefusal());
        } else if ("repairCharacter".equalsIgnoreCase(param[0])) {
            if (param.length > 1)
                CCPRepair.repairChar(activeChar, param[1]);
            else
                return null;
        } else if (param[0].startsWith("poll") || param[0].startsWith("Poll")) {
            CCPPoll.bypass(activeChar, param);
            return null;
        } else if ("otoad".equals(param[0])) {
            CCPSmallCommands.openToad(activeChar, -1);
            return null;
        } else if ("hwidPage".equals(param[0])) {
            return "cfgLockHwid.htm";
        } else if (param[0].startsWith("secondaryPass")) {
            CCPSecondaryPassword.startSecondaryPasswordSetup(activeChar, text);
            return null;
        } else if ("showPassword".equalsIgnoreCase(param[0])) {
            return "cfgPassword.htm";
        } else if ("changePassword".equals(param[0])) {
            StringTokenizer st = new StringTokenizer(text, " |");
            String[] passes = new String[st.countTokens() - 1];
            st.nextToken();
            for (int i = 0; i < passes.length; i++) {
                passes[i] = st.nextToken();
            }
            boolean newDialog = CCPPassword.setNewPassword(activeChar, passes);
            if (newDialog)
                return null;
            else
                return "cfgPassword.htm";
        } else if ("showRepair".equalsIgnoreCase(param[0])) {
            return "cfgRepair.htm";
        } else if ("ping".equalsIgnoreCase(param[0])) {
            CCPSmallCommands.getPing(activeChar);
            return null;
        } else if ("cwhPrivs".equalsIgnoreCase(param[0])) {
            if (param.length > 1) {
                String args = param[1] + (param.length > 2 ? " " + param[2] : "");
                return CCPCWHPrivilages.clanMain(activeChar, args);
            } else {
                return "cfgClan.htm";
            }
        } else if (param[0].equals("delevel")) {
            if (param.length > 1 && NumberUtils.isNumber(param[1])) {
                boolean success = CCPSmallCommands.decreaseLevel(activeChar, Integer.parseInt(param[1]));
                if (success)
                    return null;
            }

            return "cfgDelevel.htm";
        }

        return "char.htm";
    }

    public String replacePage(String currentPage, Player activeChar) {
        currentPage = currentPage.replaceFirst("%online%", CCPSmallCommands.showOnlineCount());
        currentPage = currentPage.replaceFirst("%antigrief%", getONOFF(activeChar.isVarSet("antigrief")));
        currentPage = currentPage.replaceFirst("%noe%", getONOFF(activeChar.isVarSet("NoExp")));
        currentPage = currentPage.replaceFirst("%soulshot%", getONOFF(activeChar.isVarSet("soulshot")));
        currentPage = currentPage.replaceFirst("%notraders%", getONOFF(activeChar.isVarSet("notraders")));
        currentPage = currentPage.replaceFirst("%notShowBuffAnim%", getONOFF(activeChar.isVarSet("notShowBuffAnim")));
        currentPage = currentPage.replaceFirst("%blocktrade%", getONOFF(activeChar.getTradeRefusal()));
        currentPage = currentPage.replaceFirst("%blockpartyinvite%", getONOFF(activeChar.getPartyInviteRefusal()));
        currentPage = currentPage.replaceFirst("%blockfriendinvite%", getONOFF(activeChar.getFriendInviteRefusal()));
        if (currentPage.contains("%charsOnAccount%"))
            currentPage = currentPage.replaceFirst("%charsOnAccount%", CCPRepair.getCharsOnAccount(activeChar.getName(), activeChar.getAccountName()));

        return currentPage;
    }

    private String getONOFF(boolean ON) {
        if (ON)
            return "ON";
        else
            return "OFF";
    }


}
