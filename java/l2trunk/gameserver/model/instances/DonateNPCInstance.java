package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.UserInfo;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class DonateNPCInstance extends NpcInstance {

    public DonateNPCInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    private void becomeNoble(Player player) {
        if ((player == null) || player.isNoble()) {
            return;
        }

        Olympiad.addNoble(player);
        player.setNoble(true);
        player.updatePledgeClass();
        player.updateNobleSkills();
        player.sendPacket(new SkillList(player));
        player.broadcastUserInfo(true);
        player.sendMessage("Congratulations! You are now nobless!");
    }


    @Override
    public void onBypassFeedback(Player player, String command) {
        new Functions();

        if (!canBypassCheck(player, this)) {
            return;
        }

        NpcHtmlMessage html = new NpcHtmlMessage(player, this);

        if (command.equalsIgnoreCase("add_fame")) {
            html.setFile("default/" + getNpcId() + ".htm");
            html.replace("%donate_fame%", "" + Config.DONATOR_NPC_FAME);
            player.sendPacket(html);

            if (player.getFame() == 100000) {
                player.sendMessage("You have reached the maximum amount of fame!");
                return;
            }

            if (player.getInventory().destroyItemByItemId(Config.DONATOR_NPC_ITEM, Config.DONATOR_NPC_FAME, command, "AddedFame")) {
                player.addFame(Config.DONATOR_NPC_COUNT_FAME, "DonateNPC");
                player.sendMessage(Config.DONATOR_NPC_FAME + " " + Config.DONATOR_NPC_ITEM_NAME + " have disappeared!");
                player.sendPacket(new UserInfo(player));
                System.out.println("Character " + player + "  received fame via donation");
            } else {
                player.sendMessage("You don't have enough " + Config.DONATOR_NPC_ITEM_NAME + ".");
            }
        } else if ("add_clan_reputation".equalsIgnoreCase(command)) {
            html.setFile("default/" + getNpcId() + ".htm");
            html.replace("%donate_clanrep%", "" + Config.DONATOR_NPC_REP);
            html.replace("%nick%", player.getName());
            player.sendPacket(html);

            if (player.getClan() != null) {
                if (player.getInventory().destroyItemByItemId(Config.DONATOR_NPC_ITEM, Config.DONATOR_NPC_REP, command, "Destroyed")) {
                    player.getClan().incReputation(Config.DONATOR_NPC_COUNT_REP, false, "DonateNPC");
                    player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdate(player.getClan()));
                    player.sendMessage(Config.DONATOR_NPC_REP + " " + Config.DONATOR_NPC_ITEM_NAME + " have disappeared!");
                    player.sendMessage("Your clan received " + Config.DONATOR_NPC_COUNT_REP + " clan reputation!");
                    System.out.println("Character " + player + "  received clan reputation points via donation");
                } else {
                    player.sendMessage("You don't have enough " + Config.DONATOR_NPC_ITEM_NAME + ".");
                }
            } else {
                player.sendMessage("Sorry, but you do not have clan!");
            }
        } else if ("give_noblesse".equalsIgnoreCase(command)) {
            html.setFile("default/" + getNpcId() + ".htm");
            html.replace("%donate_nobless%", "" + Config.DONATOR_NPC_COUNT_NOBLESS);
            html.replace("%nick%", player.getName());
            player.sendPacket(html);

            if (player.isNoble()) {
                player.sendMessage("You are already Nobless!");
                return;
            }

            if (player.getSubLevel() < 75) {
                player.sendMessage("You must make sub class occupation 75 first!");
                return;
            }

            if (player.getInventory().destroyItemByItemId(Config.DONATOR_NPC_ITEM, Config.DONATOR_NPC_COUNT_NOBLESS, command, "Destroyed")) {
                becomeNoble(player);
                System.out.println("Character " + player + "  received via Donation Nobless Status");
            } else {
                player.sendMessage("You don't have enough " + Config.DONATOR_NPC_ITEM_NAME + ".");
            }
        } else if ("change_sex".equalsIgnoreCase(command)) {
            html.setFile("default/" + getNpcId() + ".htm");
            html.replace("%donate_sex%", "" + Config.DONATOR_NPC_COUNT_SEX);
            html.replace("%nick%", player.getName());
            player.sendPacket(html);

            if (player.getRace() == Race.kamael) {
                player.sendMessage("Not available for Kamael.");
                return;
            }
            if (!player.haveItem(Config.DONATOR_NPC_ITEM, Config.DONATOR_NPC_COUNT_SEX)) {
                player.sendMessage("You don't have enough " + Config.DONATOR_NPC_ITEM_NAME + ".");
                return;
            }

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement offline = con.prepareStatement("UPDATE characters SET sex = ? WHERE obj_Id = ?")) {
                offline.setInt(1, player.isMale() ? 1 : 0);
                offline.setInt(2, player.objectId());
                offline.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            removeItem(player, Config.DONATOR_NPC_ITEM, Config.DONATOR_NPC_COUNT_SEX, "Removed");
            player.changeSex();
            player.setTransformation(251);
            player.sendMessage("Your gender has been changed!");
            player.broadcastUserInfo(true);
            player.setTransformation(0);
            System.out.println("Character " + player + "  changed sex via donation");
        } else if (command.equalsIgnoreCase("give_level")) {
            html.setFile("default/" + getNpcId() + ".htm");
            html.replace("%donate_level%", "" + Config.DONATOR_NPC_COUNT_LEVEL);
            html.replace("%nick%", player.getName());
            player.sendPacket(html);

            int lvl = player.isSubClassActive() ? Experience.getMaxSubLevel() : Experience.getMaxLevel();

            if (player.getLevel() == lvl) {
                player.sendMessage("Your occupation is already at maximum!");
                return;
            }

            if (!player.haveItem(Config.DONATOR_NPC_ITEM, Config.DONATOR_NPC_COUNT_LEVEL)) {
                player.sendMessage("You don't have enough " + Config.DONATOR_NPC_ITEM_NAME + ".");
                return;
            }
            removeItem(player, Config.DONATOR_NPC_ITEM, Config.DONATOR_NPC_COUNT_LEVEL, "Removed");
            long exp_add = Experience.LEVEL[lvl] - player.getExp();
            player.addExpAndSp(exp_add, 0);
            player.sendMessage("Congratulations! You are now occupation " + lvl + "!");
            System.out.println("Character " + player + "  lvl up via donation");

        } else {
            super.onBypassFeedback(player, command);
        }
    }
}