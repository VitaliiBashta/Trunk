package l2trunk.gameserver.network.clientpackets;

import Elemental.managers.OfflineBufferManager;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.handler.admincommands.AdminCommandHandler;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2trunk.gameserver.instancemanager.BypassManager.DecodedBypass;
import l2trunk.gameserver.instancemanager.OlympiadHistoryManager;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.OlympiadManagerInstance;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Scripts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public final class RequestBypassToServer extends L2GameClientPacket {
    //Format: cS
    private static final Logger _log = LoggerFactory.getLogger(RequestBypassToServer.class);
    private DecodedBypass bp = null;

    private static void comeHere(GameClient client) {
        GameObject obj = client.getActiveChar().getTarget();
        if (obj != null && obj.isNpc()) {
            NpcInstance temp = (NpcInstance) obj;
            Player activeChar = client.getActiveChar();
            temp.setTarget(activeChar);
            temp.moveToLocation(activeChar.getLoc(), 0, true);
        }
    }

    private static void playerHelp(Player activeChar, String path) {
        NpcHtmlMessage html = new NpcHtmlMessage(5);
        html.setFile(path);
        activeChar.sendPacket(html);
    }

    @Override
    protected void readImpl() {
        String bypass = readS();
        if (!bypass.isEmpty())
            bp = getClient().getActiveChar().decodeBypass(bypass);
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if (activeChar == null || bp == null || activeChar.isJailed() || (activeChar.isBlocked() && !activeChar.isInObserverMode() && (bp.bypass == null || !(bp.bypass.contains("secondaryPassS") || bp.bypass.contains("user_report")))))
            return;
        if (bp.handler == null && !Config.ALLOW_TALK_TO_NPCS)
            return;
        if (bp.handler != null && !Config.COMMUNITYBOARD_ENABLED)
            return;
        if (bp.handler != null && activeChar.isCursedWeaponEquipped())
            return;
        try {
            NpcInstance npc = activeChar.getLastNpc();
            GameObject target = activeChar.getTarget();
            if (npc == null && target != null && target.isNpc())
                npc = (NpcInstance) target;

            if (bp.bypass.startsWith("admin_"))
                AdminCommandHandler.INSTANCE.useAdminCommandHandler(activeChar, bp.bypass);
            else if (bp.bypass.equals("come_here") && activeChar.isGM())
                comeHere(getClient());
            else if (bp.bypass.startsWith("player_help "))
                playerHelp(activeChar, bp.bypass.substring(12));
            else if (bp.bypass.startsWith("scripts_")) {
                String command = bp.bypass.substring(8).trim();
                String[] word = command.split("\\s+");
                String[] args = command.substring(word[0].length()).trim().split("\\s+");
                String[] path = word[0].split(":");
                if (path.length != 2) {
                    _log.warn("Bad Script bypass!");
                    return;
                }

                Map<String, Object> variables = null;
                if (npc != null) {
                    variables = new HashMap<>(1);
                    variables.put("npc", npc.getRef());
                }

                if (word.length == 1)
                    Scripts.INSTANCE.callScripts(activeChar, path[0], path[1], new Object[0], variables);
                else
                    Scripts.INSTANCE.callScripts(activeChar, path[0], path[1], new Object[]{args}, variables);
            } else if (bp.bypass.startsWith("user_")) {
                String command = bp.bypass.substring(5).trim();
                String word = command.split("\\s+")[0];
                String args = command.substring(word.length()).trim();
                IVoicedCommandHandler vch = VoicedCommandHandler.INSTANCE.getVoicedCommandHandler(word);

                if (vch != null)
                    vch.useVoicedCommand(word, activeChar, args);
                else
                    _log.warn("Unknow voiced command '" + word + "'");
            } else if (bp.bypass.startsWith("npc_")) {
                int endOfId = bp.bypass.indexOf('_', 5);
                String id;
                if (endOfId > 0)
                    id = bp.bypass.substring(4, endOfId);
                else
                    id = bp.bypass.substring(4);
                GameObject object = activeChar.getVisibleObject(Integer.parseInt(id));
                if (object != null && object.isNpc() && endOfId > 0 && activeChar.isInRange(object.getLoc(), Creature.INTERACTION_DISTANCE)) {
                    activeChar.setLastNpc((NpcInstance) object);
                    ((NpcInstance) object).onBypassFeedback(activeChar, bp.bypass.substring(endOfId + 1));
                }
            } else if (bp.bypass.startsWith("_olympiad?")) {
                String[] ar = bp.bypass.replace("_olympiad?", "").split("&");
                String firstVal = ar[0].split("=")[1];
                String secondVal = ar[1].split("=")[1];

                if (firstVal.equalsIgnoreCase("move_op_field")) {
                    if (!Config.ENABLE_OLYMPIAD_SPECTATING)
                        return;

                    // Transition in view of Olympiad is allowed only from the manager or from the arena.
                    if ((activeChar.getLastNpc() instanceof OlympiadManagerInstance && activeChar.getLastNpc().isInRange(activeChar, Creature.INTERACTION_DISTANCE)) || activeChar.getOlympiadObserveGame() != null)
                        Olympiad.addSpectator(Integer.parseInt(secondVal) - 1, activeChar);
                }
            } else if (bp.bypass.startsWith("_diary")) {
                String params = bp.bypass.substring(bp.bypass.indexOf("?") + 1);
                StringTokenizer st = new StringTokenizer(params, "&");
                int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                int heropage = Integer.parseInt(st.nextToken().split("=")[1]);
                int heroid = Hero.INSTANCE.getHeroByClass(heroclass);
                if (heroid > 0)
                    Hero.INSTANCE.showHeroDiary(activeChar, heroclass, heroid, heropage);
            } else if (bp.bypass.startsWith("_match")) {
                String params = bp.bypass.substring(bp.bypass.indexOf("?") + 1);
                StringTokenizer st = new StringTokenizer(params, "&");
                int heroclass = Integer.parseInt(st.nextToken().split("=")[1]);
                int heropage = Integer.parseInt(st.nextToken().split("=")[1]);

                OlympiadHistoryManager.INSTANCE.showHistory(activeChar, heroclass, heropage);
            } else if (bp.bypass.startsWith("manor_menu_select?")) // Navigate throught Manor windows
            {
                GameObject object = activeChar.getTarget();
                if (object != null && object.isNpc())
                    ((NpcInstance) object).onBypassFeedback(activeChar, bp.bypass);
            } else if (bp.bypass.startsWith("partyMatchingInvite")) {
                try {
                    String targetName = bp.bypass.substring(20);
                    Player receiver = World.getPlayer(targetName);
                    SystemMessage sm;

                    if (receiver == null) {
                        activeChar.sendMessage("First getBonuses a user to invite to your party.");
                        return;
                    }

                    if ((!receiver.isOnline())) {
                        activeChar.sendMessage("Player is in offline mode.");
                        return;
                    }

                    if (!activeChar.isGM() && receiver.isInvisible() || !activeChar.isInvisible() && receiver.isInvisible() && !activeChar.isGM()) {
                        activeChar.sendMessage("Incorrect target.");
                        return;
                    }

                    if (receiver.isInParty()) {
                        activeChar.sendMessage("Player " + receiver.getName() + " is already in a party.");
                        return;
                    }

                    if (activeChar.getBlockList().contains(receiver)) {
                        activeChar.sendMessage("Player " + receiver.getName() + " is in your ignore list.");
                        return;
                    }

                    if (receiver == activeChar) {
                        activeChar.sendMessage("Wrong target.");
                        return;
                    }

                    if (receiver.isCursedWeaponEquipped() || activeChar.isCursedWeaponEquipped()) {
                        receiver.sendMessage("You cannot invite this person to join in your party right now.");
                        return;
                    }

                    if (receiver.isInJail() || activeChar.isInJail()) {
                        activeChar.sendMessage("You cannot invite a player while is in Jail.");
                        return;
                    }

                    if (receiver.isInOlympiadMode() || activeChar.isInOlympiadMode()) {
                        if ((receiver.isInOlympiadMode() != activeChar.isInOlympiadMode()) || (receiver.getOlympiadGame().getId() != activeChar.getOlympiadGame().getId()) || (receiver.getOlympiadSide() != activeChar.getOlympiadSide())) {
                            activeChar.sendMessage("You cannot invite this player to join your party right now.");
                            return;
                        }
                    }

                    activeChar.sendMessage("You invited " + receiver.getName() + " to join your party.");

                    if (!activeChar.isInParty()) {
                        Party newparty = new Party(activeChar, Party.ITEM_LOOTER);
                        activeChar.setParty(newparty);
                        receiver.joinParty(newparty);
                    } else {
                        if (activeChar.getParty().isInDimensionalRift()) {
                            activeChar.sendMessage("You cannot invite a player when you are in the Dimensional Rift.");
                        } else {
                            Party plparty = activeChar.getParty();
                            receiver.joinParty(plparty);
                        }
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
            } else if (bp.bypass.startsWith("multisell "))
                MultiSellHolder.INSTANCE.SeparateAndSend(Integer.parseInt(bp.bypass.substring(10)), activeChar, 0);
            else if (bp.bypass.startsWith("Quest ")) {
                String p = bp.bypass.substring(6).trim();
                int idx = p.indexOf(' ');
                if (idx < 0)
                    activeChar.processQuestEvent(QuestManager.getQuest(p), "", npc);
                else
                    activeChar.processQuestEvent(p.substring(0, idx), p.substring(idx).trim(), npc);
            }
            // Ady - Bypass for Buff Store
            else if (bp.bypass.startsWith("BuffStore")) {
                try {
                    OfflineBufferManager.INSTANCE.processBypass(activeChar, bp.bypass);
                } catch (Exception ex) {
                }
            } else if (bp.handler != null) {
                if (!Config.COMMUNITYBOARD_ENABLED)
                    activeChar.sendPacket(new SystemMessage2(SystemMsg.THE_COMMUNITY_SERVER_IS_CURRENTLY_OFFLINE));
                else
                    bp.handler.onBypassCommand(activeChar, bp.bypass);
            }
        } catch (Exception e) {
            String st = "Char '" + activeChar.getName() + "' sent Bad RequestBypassToServer: " + bp.bypass;
            GameObject target = activeChar.getTarget();
            if (target != null && target.isNpc())
                st = st + " via NPC #" + ((NpcInstance) target).getNpcId();
            _log.error(st, e);
        }
    }
}