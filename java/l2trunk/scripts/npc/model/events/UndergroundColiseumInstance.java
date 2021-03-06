package l2trunk.scripts.npc.model.events;

import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.instancemanager.UnderGroundColliseumManager;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.entity.Coliseum;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.StringTokenizer;

public final class UndergroundColiseumInstance extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(UndergroundColiseumInstance.class);
    private static final Map<Integer, Integer> minPlayerLevels = Map.of(
            32513, 40,
            32516, 50,
            32515, 60,
            32514, 70);


    public UndergroundColiseumInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    private int getMinLevel() {
        if (minPlayerLevels.containsKey(getNpcId()))
            return minPlayerLevels.get(getNpcId());
        return 1;
    }

    private int getMaxLevel() {
        if (minPlayerLevels.containsKey(getNpcId()))
            return minPlayerLevels.get(getNpcId()) + 9;
        return Experience.getMaxLevel();
    }

    @Override
    public void showChatWindow(Player player, int val) {
        String filename = "Coliseum/" + val + ".htm";
        NpcHtmlMessage html = new NpcHtmlMessage(player, this, filename, val);
        html.replace("%levelMin%", "" + getMinLevel());
        html.replace("%levelMax%", "" + getMaxLevel());
        player.sendPacket(html);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        player.sendActionFailed();
        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken(); // Get actual command
        if (actualCommand.startsWith("register")) {
            if (player.getParty() == null) {
                showChatWindow(player, 3);
                return;
            }
            if (!player.getParty().isLeader(player)) {
                showChatWindow(player, 3);
                return;
            }

            if (st.hasMoreTokens()) {
                Coliseum coliseum = UnderGroundColliseumManager.INSTANCE.getColiseumByLevelLimit(getMaxLevel());
                if (coliseum == null) {
                    showChatWindow(player, 3);
                    return;
                }
                if (coliseum.getWaitingPartys().size() > 4) {
                    showChatWindow(player, 3);
                    return;
                }
                if (player.getParty().getMembersStream()
                        .filter(member -> member.getLevel() > getMaxLevel() || member.getLevel() < getMinLevel())
                        .peek(member -> player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member)))
                        .findAny().isPresent())
                    return;


                if (player.getParty().getMembersStream()
                        .filter(Player::isCursedWeaponEquipped)
                        .peek(member -> player.sendPacket(new SystemMessage(SystemMessage.C1S_QUEST_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member)))
                        .findAny().isPresent())
                    return;
                Coliseum.register(player, getMinLevel(), getMaxLevel());
            } else {
                _log.info("Wrong data or cheater? try to register whithout lvl", "Coliseum");
            }
        } else if (actualCommand.startsWith("view")) {
            int count = 0;
            String filename = "Coliseum/" + 5 + ".htm";
            NpcHtmlMessage html = new NpcHtmlMessage(player, this, filename, 5);
            Coliseum coliseum = UnderGroundColliseumManager.INSTANCE.getColiseumByLevelLimit(getMaxLevel());
            if (coliseum != null) {
                for (Party team : coliseum.getWaitingPartys()) {
                    if (team != null) {
                        if (count == 0) {
                            html.replace("%Team1%", team.getLeader().getName());
                        } else if (count == 1) {
                            html.replace("%Team2%", team.getLeader().getName());
                        } else if (count == 2) {
                            html.replace("%Team3%", team.getLeader().getName());
                        } else if (count == 3) {
                            html.replace("%Team4%", team.getLeader().getName());
                        } else if (count == 4) {
                            html.replace("%Team5%", team.getLeader().getName());
                        }
                        count++;
                        if (count > 5) {
                            _log.info("We have six or more registred clans to UC WTF?", "UC");
                            continue;
                        }
                    }
                }
            }
            if (count == 0) {
                html.replace("%Team1%", "none");
                html.replace("%Team2%", "none");
                html.replace("%Team3%", "none");
                html.replace("%Team4%", "none");
                html.replace("%Team5%", "none");
            }
            player.sendPacket(html);
        }
        // TODO: диалог
        else if (actualCommand.startsWith("winner")) {
            String filename;
            NpcHtmlMessage html;
            /*
             * if(UnderGroundColliseumManager.INSTANCE().getColiseumByLevelLimit(getMaxLevel()).getPreviusWinners() != null)
             * {
             * filename = "Coliseum/"+ 7 + "htm";
             * html = new NpcHtmlMessage(getPlayer, this, filename, 7);
             * html.replace("winner", UnderGroundColliseumManager.INSTANCE().getColiseumByLevelLimit(getMaxLevel()).getPreviusWinners().getLeader().name());
             * }
             * else
             * {
             */
            filename = "Coliseum/" + 6 + ".htm";
            html = new NpcHtmlMessage(player, this, filename, 6);
            // }
            player.sendPacket(html);
        } else if (actualCommand.startsWith("Multisell") || actualCommand.startsWith("multisell")) {
            int listId = Integer.parseInt(command.substring(9).trim());
            Castle castle = getCastle(player);
            MultiSellHolder.INSTANCE.SeparateAndSend(listId, player, castle != null ? castle.getTaxRate() : 0);
        } else {
            super.onBypassFeedback(player, command);
        }
    }
}