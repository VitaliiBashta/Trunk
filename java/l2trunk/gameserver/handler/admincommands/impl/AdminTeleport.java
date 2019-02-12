package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.dao.CharacterDAO;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ConfirmDlg;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static l2trunk.commons.lang.NumberUtils.toInt;


public final class AdminTeleport implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanTeleport)
            return false;

        switch (command) {
            case admin_show_moves:
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/teleports.htm"));
                break;
            case admin_show_moves_other:
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/tele/other.htm"));
                break;
            case admin_show_teleport:
                showTeleportCharWindow(activeChar);
                break;
            case admin_teleport_to_character:
                teleportToCharacter(activeChar, activeChar.getTarget());
                break;
            case admin_teleport_to:
            case admin_teleportto:
            case admin_goto:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //teleportto charName");
                    return false;
                }
                String chaName = Util.joinStrings(" ", wordList, 1);
                Player cha = GameObjectsStorage.getPlayer(chaName);
                if (cha == null) {
                    activeChar.sendMessage("Player '" + chaName + "' not found in world");
                    return false;
                }
                teleportToCharacter(activeChar, cha);
                break;
            case admin_move_to:
            case admin_moveto:
            case admin_teleport:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //teleport x y z [ref]");
                    return false;
                }
                activeChar.teleToLocation(Location.of(Util.joinStrings(" ", wordList, 1, 3)), (ArrayUtils.valid(wordList, 4) != null && !ArrayUtils.valid(wordList, 4).isEmpty() ? Integer.parseInt(wordList[4]) : 0));
                break;
            case admin_walk:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //walk x y z");
                    return false;
                }
                try {
                    activeChar.moveToLocation(Location.of(Util.joinStrings(" ", wordList, 1)), 0, true);
                } catch (IllegalArgumentException e) {
                    activeChar.sendMessage("USAGE: //walk x y z");
                    return false;
                }
                break;
            case admin_gonorth:
            case admin_gosouth:
            case admin_goeast:
            case admin_gowest:
            case admin_goup:
            case admin_godown:
                int val = wordList.length < 2 ? 150 : toInt(wordList[1]);
                int x = activeChar.getX();
                int y = activeChar.getY();
                int z = activeChar.getZ();
                if (command == Commands.admin_goup)
                    z += val;
                else if (command == Commands.admin_godown)
                    z -= val;
                else if (command == Commands.admin_goeast)
                    x += val;
                else if (command == Commands.admin_gowest)
                    x -= val;
                else if (command == Commands.admin_gosouth)
                    y += val;
                else y -= val;

                activeChar.teleToLocation(x, y, z);
                showTeleportWindow(activeChar);
                break;
            case admin_tele:
                showTeleportWindow(activeChar);
                break;
            case admin_teleto:
            case admin_tele_to:
            case admin_instant_move:
                if (wordList.length > 1 && "r".equalsIgnoreCase(wordList[1]))
                    activeChar.setTeleMode(2);
                else if (wordList.length > 1 && "end".equalsIgnoreCase(wordList[1]))
                    activeChar.setTeleMode(0);
                else
                    activeChar.setTeleMode(1);
                break;
            case admin_tonpc:
            case admin_to_npc:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //tonpc npcId|npcName");
                    return false;
                }
                String npcName = Util.joinStrings(" ", wordList, 1);
                NpcInstance npc;
                if ((npc = GameObjectsStorage.getByNpcId(toInt(npcName))) != null) {
                    teleportToCharacter(activeChar, npc);
                    return true;
                }
                if ((npc = GameObjectsStorage.getNpc(npcName)) != null) {
                    teleportToCharacter(activeChar, npc);
                    return true;
                }
                activeChar.sendMessage("Npc " + npcName + " not found");
                break;
            case admin_toobject:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //toobject objectId");
                    return false;
                }
                int target = Integer.parseInt(wordList[1]);
                GameObject obj;
                if ((obj = GameObjectsStorage.findObject(target)) != null) {
                    teleportToCharacter(activeChar, obj);
                    return true;
                }
                activeChar.sendMessage("Object " + target + " not found");
                break;
            case admin_autorecall:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //autorecall true | //autorecall false");
                    return false;
                }
                activeChar.addQuickVar("autoRecall", Boolean.parseBoolean(wordList[1]));
                activeChar.sendMessage("Worked!");
                break;
        }

        if (!activeChar.getPlayerAccess().CanEditChar)
            return false;

        String targetName = wordList.length == 2 ? wordList[1] : "";
        Player target = GameObjectsStorage.getPlayer(targetName.isEmpty() ? null : targetName);
        switch (command) {
            case admin_teleport_character:
                if (wordList.length < 3) {
                    activeChar.sendMessage("USAGE: //teleport_character x y z");
                    return false;
                }
                activeChar.teleToLocation(Location.of(Util.joinStrings(" ", wordList, 1)));
                showTeleportCharWindow(activeChar);
                break;
            case admin_recall:
                if (target != null) {
                    recall(activeChar, target);
                    return true;
                }

                int obj_id = CharacterDAO.getObjectIdByName(targetName);
                if (obj_id > 0) {
                    teleportCharacter_offline(obj_id, activeChar.getLoc());
                    activeChar.sendMessage(targetName + " is offline. Offline teleport used...");
                } else
                    activeChar.sendMessage("->" + targetName + "<- is incorrect.");
                break;
            case admin_recallparty:
                if (target != null) {
                    recall(activeChar, target.isInParty() ? target.getParty().getMembers().toArray(new Player[0]) : new Player[]{target});
                    return true;
                } else
                    activeChar.sendMessage("->" + targetName + "<- is incorrect.");
                break;
            case admin_recallcc:
                if (target != null) {
                    recall(activeChar, target.getPlayerGroup().getMembers().toArray(new Player[0]));
                    return true;
                } else
                    activeChar.sendMessage("->" + targetName + "<- is incorrect.");
                break;
            case admin_recallinstance:
                if (target != null && !target.getReflection().isDefault()) {
                    recall(activeChar, target.getReflection().getPlayers().toArray(Player[]::new));
                    return true;
                } else
                    activeChar.sendMessage("->" + targetName + "<- is incorrect, or reflection is default.");
                break;
            case admin_recallserver:
                final Player[] targets = GameObjectsStorage.getAllPlayersStream()
                        .filter(plr -> (!plr.isInBuffStore()
                                || !plr.isInStoreMode()
                                || plr.isOnline()
                                || !plr.isInOlympiadMode()
                                || !Olympiad.isRegistered(plr)
                                || !plr.isJailed()
                                || !plr.isInZone(ZoneType.SIEGE)
                                || plr.getReflection() == ReflectionManager.DEFAULT
                                || plr.getPvpFlag() <= 0
                                || plr.getKarma() <= 0))
                        .toArray(Player[]::new);
                activeChar.sendMessage("Recalling " + targets.length + " players out of " + GameObjectsStorage.getAllPlayersCount() + " players. Ignored: Offline shops, instance, event, olympiad participants and jailed players.");
                recall(activeChar, true, true, targets);
                break;
            case admin_setref: {
                if (wordList.length < 2) {
                    activeChar.sendMessage("Usage: //setref <reflection>");
                    return false;
                }

                int ref_id = toInt(wordList[1]);
                if (ref_id != 0 && ReflectionManager.INSTANCE.get(ref_id) == null) {
                    activeChar.sendMessage("Reflection <" + ref_id + "> not found.");
                    return false;
                }

                GameObject targetObj = activeChar;
                GameObject obj = activeChar.getTarget();
                if (obj != null)
                    targetObj = obj;

                targetObj.setReflection(ref_id);
                targetObj.decayMe();
                targetObj.spawnMe();
                break;
            }
            case admin_getref:
                if (target == null) {
                    activeChar.sendMessage("Player '" + wordList[1] + "' not found in world");
                    return false;
                }
                activeChar.sendMessage("Player '" + wordList[1] + "' in reflection: " + target.getReflectionId() + ", name: " + target.getReflection().getName());
                break;
        }
        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;

        if (command == Commands.admin_recall_npc) {
            recallNPC(activeChar);
        }

        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private void showTeleportWindow(Player activeChar) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        String replyMSG = "<html><title>teleport Menu</title>" + "<body>" +
                "<br>" +
                "<center><table>" +
                "<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td><button value=\"North\" action=\"bypass -h admin_gonorth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td><button value=\"Up\" action=\"bypass -h admin_goup\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>" +
                "<tr><td><button value=\"West\" action=\"bypass -h admin_gowest\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td><button value=\"East\" action=\"bypass -h admin_goeast\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>" +
                "<tr><td><button value=\"  \" action=\"bypass -h admin_tele\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td><button value=\"South\" action=\"bypass -h admin_gosouth\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td><button value=\"Down\" action=\"bypass -h admin_godown\" width=70 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>" +
                "</table></center>" +
                "</body></html>";
        adminReply.setHtml(replyMSG);
        activeChar.sendPacket(adminReply);
    }

    private void showTeleportCharWindow(Player activeChar) {
        GameObject target = activeChar.getTarget();
        Player player;
        if (target instanceof Player)
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        String replyMSG = "<html><title>teleport Character</title>" + "<body>" +
                "The character you will teleport is " + player.getName() + "." +
                "<br>" +
                "Co-ordinate x" +
                "<edit var=\"char_cord_x\" width=110>" +
                "Co-ordinate y" +
                "<edit var=\"char_cord_y\" width=110>" +
                "Co-ordinate z" +
                "<edit var=\"char_cord_z\" width=110>" +
                "<button value=\"teleport\" action=\"bypass -h admin_teleport_character $char_cord_x $char_cord_y $char_cord_z\" width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">" +
                "<button value=\"teleport near you\" action=\"bypass -h admin_teleport_character " + activeChar.getX() + " " + activeChar.getY() + " " + activeChar.getZ() + "\" width=115 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">" +
                "<center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>" +
                "</body></html>";
        adminReply.setHtml(replyMSG);
        activeChar.sendPacket(adminReply);
    }

    private void recall(Player activeChar, Player... targets) {
        recall(activeChar, false, false, targets);
    }

    private void recall(Player activeChar, boolean askToReturn, boolean randomTp, Player... targets) {
        for (Player target : targets) {
            if (askToReturn) {
                target.setVar("lastRecallLoc", target.getLoc().toXYZString(), System.currentTimeMillis() + 120000);
                ConfirmDlg packet = new ConfirmDlg(SystemMsg.S1, 120000).addString("You have been teleported. Would you like to stay here?");
                target.ask(packet, new OnAnswerListener() {
                    @Override
                    public void sayYes() {
                        target.unsetVar("lastRecallLoc");
                    }

                    @Override
                    public void sayNo() {
                        Location loc = Location.of(target.getVar("lastRecallLoc"));
                        if (loc != null) {
                            target.teleToLocation(loc);
                        }
                    }
                });
            }

            if (!target.equals(activeChar))
                target.sendMessage("Admin is teleporting you.");

            target.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            if (randomTp)
                target.teleToLocation(new Location(activeChar.getX() + Rnd.get(-400, 400), activeChar.getY() + Rnd.get(-400, 400), activeChar.getZ()), activeChar.getReflectionId());
            else
                target.teleToLocation(activeChar.getLoc(), activeChar.getReflectionId());

            if (target.equals(activeChar))
                activeChar.sendMessage("You have been teleported to " + activeChar.getLoc() + ", reflection id: " + activeChar.getReflectionId());
        }
    }

    private void teleportCharacter_offline(int obj_id, Location loc) {
        if (obj_id == 0)
            return;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement("UPDATE characters SET x=?,y=?,z=? WHERE obj_Id=? LIMIT 1")) {
            st.setInt(1, loc.x);
            st.setInt(2, loc.y);
            st.setInt(3, loc.z);
            st.setInt(4, obj_id);
            st.executeUpdate();
        } catch (Exception ignored) {

        }
    }

    private void teleportToCharacter(Player activeChar, GameObject target) {
        if (target == null)
            return;

        activeChar.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        activeChar.teleToLocation(target.getLoc(), target.getReflectionId());

        activeChar.sendMessage("You have teleported to " + target);
    }

    private void recallNPC(Player activeChar) {
        GameObject obj = activeChar.getTarget();
        if (obj instanceof NpcInstance) {
            obj.setLoc(activeChar.getLoc());
            ((NpcInstance) obj).broadcastCharInfo();
            activeChar.sendMessage("You teleported npc " + obj.getName() + " to " + activeChar.getLoc().toString() + ".");
        } else
            activeChar.sendMessage("Target is't npc.");
    }

    private enum Commands {
        admin_show_moves,
        admin_show_moves_other,
        admin_show_teleport,
        admin_teleport_to_character,
        admin_teleportto,
        admin_teleport_to,
        admin_move_to,
        admin_moveto,
        admin_teleport,
        admin_teleport_character,
        admin_recall,
        admin_recallparty,
        admin_recallcc,
        admin_recallinstance,
        admin_recallserver,
        admin_walk,
        admin_recall_npc,
        admin_gonorth,
        admin_gosouth,
        admin_goeast,
        admin_goto,
        admin_gowest,
        admin_goup,
        admin_godown,
        admin_tele,
        admin_teleto,
        admin_tele_to,
        admin_instant_move,
        admin_tonpc,
        admin_to_npc,
        admin_toobject,
        admin_setref,
        admin_getref,
        admin_autorecall
    }
}