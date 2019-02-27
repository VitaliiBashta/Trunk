package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.olympiad.CompType;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.entity.olympiad.OlympiadDatabase;
import l2trunk.gameserver.network.serverpackets.ExHeroList;
import l2trunk.gameserver.network.serverpackets.ExReceiveOlympiad;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.loginserver.database.L2DatabaseFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class OlympiadManagerInstance extends NpcInstance {
    public OlympiadManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        if (Config.ENABLE_OLYMPIAD && (template.npcId == 31688 || template.npcId == 39018))
            Olympiad.addOlympiadNpc(this);
    }

    private static void checkRank(Player player, int classId) {
        final int comp_matches_to_show = Config.OLYMPIAD_BATTLES_FOR_REWARD;
        int points, comp_done, pos = 0;
        String char_name;
        String className = ClassId.getById(classId).name;

        NpcHtmlMessage nhm = new NpcHtmlMessage(5);
        StringBuilder html = new StringBuilder();
        html.append("<html><head><title>Grand Olympiad Ranking</title></head><body><center><font color=66cc00>Olympiad Ranking Online System</font></center><br><center>").append(className).append("</center><br1><center><img src=\"L2UI.SquareWhite\" width=300 height=1><img src=\"L2UI.SquareBlank\" width=1 height=3></center><table width=300 border=0 bgcolor=\"000000\"><tr><td>Position</td><center><td>|</td></center><td><center>Name</center></td><center><td>|</td></center><td><center>Points</center></td><center><td>|</td></center><td><center>Fights</center></td></tr>");

        try (Connection con = L2DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT characters.char_name,  olympiad_nobles.competitions_done, olympiad_nobles.olympiad_points  FROM olympiad_nobles, characters WHERE characters.obj_Id = olympiad_nobles.char_id AND olympiad_nobles.class_id AND class_id=? AND olympiad_nobles.competitions_done >= ? order by olympiad_points desc, competitions_done desc")) {
            statement.setInt(1, classId);
            statement.setInt(2, comp_matches_to_show);
            ResultSet rset = statement.executeQuery();
            while (rset.next()) {
                char_name = rset.getString("char_name");
                points = rset.getInt("olympiad_points");
                comp_done = rset.getInt("competitions_done");
                pos++;
                html.append("<tr><td><center>").append(pos).append("</td><center><td></td></center><td><center>").append(char_name).append("</center></td><center><td></td></center><td><center>").append(points).append("</center></td><center><td></td></center><td><center>").append(comp_done).append("</center></td></tr>");
            }
            html.append("</table></body></html>");
            nhm.setHtml(html.toString());
            player.sendPacket(nhm);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (checkForDominionWard(player))
            return;

        if (!Config.ENABLE_OLYMPIAD)
            return;

        if (command.startsWith("OlympiadNoble")) {

            int val = toInt(command.substring(14));
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);

            switch (val) {
                case 1:
                    Olympiad.unRegisterNoble(player);
                    showChatWindow(player, 0);
                    break;
                case 2:
                    if (Olympiad.isRegistered(player)) {
                        player.sendPacket(html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_noregister.htm"));
                    } else {
                        player.sendPacket(html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_register.htm"));
                        html.replace("%1%", Olympiad.getPeriod());
                        html.replace("%2%", Olympiad.getCurrentCycle());
                        html.replace("%3%", Olympiad.getCountOpponents());
                        player.sendPacket(html);
                    }
                    break;
                case 4:
                    Olympiad.registerNoble(player, CompType.NON_CLASSED);
                    break;
                case 5:
                    Olympiad.registerNoble(player, CompType.CLASSED);
                    break;
                case 6:
                    int passes = Olympiad.getNoblessePasses(player);
                    if (passes > 0) {
                        player.getInventory().addItem(Config.ALT_OLY_COMP_RITEM, passes, "Olympiad End Reward");
                        player.sendPacket(SystemMessage2.obtainItems(Config.ALT_OLY_COMP_RITEM, passes, 0));
                    } else
                        player.sendPacket(html.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_nopoints.htm"));
                    break;
                case 7:
                    MultiSellHolder.INSTANCE.SeparateAndSend(102, player, 0);
                    break;
                case 9:
                    MultiSellHolder.INSTANCE.SeparateAndSend(103, player, 0);
                    break;
                case 10:
                    Olympiad.registerNoble(player, CompType.TEAM);
                    break;
                case 3:
                case 8:
                default:
                    //_log.warn("Olympiad System: Couldnt send packet for request " + val);
                    break;
            }
        } else if (command.startsWith("Olympiad")) {
            int val = toInt(command.substring(9, 10));

            NpcHtmlMessage reply = new NpcHtmlMessage(player, this);

            switch (val) {
                case 1:
                    if (!Olympiad.inCompPeriod() || Olympiad.isOlympiadEnd()) {
                        player.sendPacket(SystemMsg.THE_GRAND_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
                        return;
                    }
                    player.sendPacket(new ExReceiveOlympiad.MatchList());
                    break;
                case 2:
                    // for example >> Olympiad 1_88
                    int classId = toInt(command.substring(11));
                    if (classId >= 88) {
                        reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_ranking.htm");

                        List<String> names = OlympiadDatabase.getClassLeaderBoard(classId);

                        int index = 1;
                        for (String name : names) {
                            reply.replace("%place" + index + "%", index);
                            reply.replace("%rank" + index + "%", name);
                            index++;
                            if (index > 10)
                                break;
                        }
                        for (; index <= 10; index++) {
                            reply.replace("%place" + index + "%", "");
                            reply.replace("%rank" + index + "%", "");
                        }

                        player.sendPacket(reply);
                    }
                    // TODO Send getPlayer each class rank
                    break;
                case 3:
                    if (!Config.ENABLE_OLYMPIAD_SPECTATING)
                        break;
                    Olympiad.addSpectator(toInt(command.substring(11)), player);
                    break;
                case 4:
                    player.sendPacket(new ExHeroList());
                    break;
                case 5:
                    if (Hero.INSTANCE.isInactiveHero(player.objectId())) {
                        Hero.INSTANCE.activateHero(player);
                        reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "monument_give_hero.htm");
                    } else
                        reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "monument_dont_hero.htm");
                    player.sendPacket(reply);
                    break;
                case 6://Getting Best players by current olympiad scores
                    // for example >> Olympiad 6_88
                    classId = toInt(command.substring(11));
                    if (classId >= 88) {
                        reply.setFile(Olympiad.OLYMPIAD_HTML_PATH + "manager_ranking.htm");

                        List<String> names = OlympiadDatabase.getClassLeaderBoardCurrent(classId);

                        int index = 1;
                        for (String name : names) {
                            reply.replace("%place" + index + "%", index);
                            reply.replace("%rank" + index + "%", name);
                            index++;
                            if (index > 10)
                                break;
                        }
                        for (; index <= 10; index++) {
                            reply.replace("%place" + index + "%", "");
                            reply.replace("%rank" + index + "%", "");
                        }

                        player.sendPacket(reply);
                    }
                    break;
                default:
                    //_log.warn("Olympiad System: Couldnt send packet for request " + val);
                    break;
            }
        }
        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken();

        if ("openfile".equalsIgnoreCase(actualCommand)) {
            String name = st.nextToken();
            NpcHtmlMessage html = new NpcHtmlMessage(objectId());
            html.setFile("olympiad/ranks/" + name + ".htm");
            html.replace("%objectId%", objectId());
            html.replace("%name%", player.getName());
            player.sendPacket(html);
        } else if ("gofolder".equalsIgnoreCase(actualCommand)) {
            String name = st.nextToken();
            NpcHtmlMessage html = new NpcHtmlMessage(objectId());
            html.setFile("olympiad/" + name + "/index.htm");
            html.replace("%objectId%", objectId());
            html.replace("%name%", player.getName());
            player.sendPacket(html);
        } else if ("rank".equalsIgnoreCase(actualCommand)) {
            int val = toInt(st.nextToken());
            checkRank(player, val);
        } else if ("back".equalsIgnoreCase(actualCommand)) {
            NpcHtmlMessage html = new NpcHtmlMessage(objectId());
            html.setFile("olympiad/index.htm");
            html.replace("%objectId%", objectId());
            html.replace("%name%", player.getName());
            player.sendPacket(html);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        if (checkForDominionWard(player))
            return;

        String fileName = Olympiad.OLYMPIAD_HTML_PATH;
        int npcId = getNpcId();
        switch (npcId) {
            case 31688: // Grand Olympiad Manager
            case 39018:
                fileName += "manager";
                break;
            default: // Monument of Heroes
                fileName += "monument";
                break;
        }
        if (player.isNoble())
            fileName += "_n";
        if (val > 0)
            fileName += "-" + val;
        fileName += ".htm";
        player.sendPacket(new NpcHtmlMessage(player, this, fileName, val));
    }
}