package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.UserInfo;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class BetaNPCInstance extends NpcInstance {

    public BetaNPCInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }
        if (command.equalsIgnoreCase("change_sex")) {
            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement offline = con.prepareStatement("UPDATE characters SET sex = ? WHERE obj_Id = ?")) {
                offline.setInt(1, player.getSex() == 1 ? 0 : 1);
                offline.setInt(2, player.getObjectId());
                offline.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            player.changeSex();
            player.sendMessage("Your gender has been changed !!");
            Log.add("Character " + player + "  changed sex to " + (player.getSex() == 1 ? "male" : "female"), "renames");
        } else if (command.equalsIgnoreCase("add_clan_reputation")) {
            if (player.getClan() != null) {
                player.getClan().incReputation(10000, false, "BetaNpc");
                player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdate(player.getClan()));
                player.sendMessage("Your clan received 10 000 clan reputation!");
            } else {
                player.sendMessage("You don't have clan to use this feature!");
            }
        } else if (command.equalsIgnoreCase("add_exp_sp")) {
            player.addExpAndSp(999999999L, 999999999L);
        } else if (command.equalsIgnoreCase("add_fame")) {
            player.setFame(player.getFame() + 10000, "BetaNpc");
            player.sendPacket(new UserInfo(player));
            player.sendMessage("You received 10.000 fame points !");
        } else if (command.equalsIgnoreCase("give_noblesse")) {
            if (!player.isNoble()) {
                Olympiad.addNoble(player.getPlayer());
                player.getPlayer().setNoble(true);
                player.getPlayer().updatePledgeClass();
                player.getPlayer().updateNobleSkills();
                player.getPlayer().sendPacket(new SkillList(player.getPlayer()));
                player.getPlayer().broadcastUserInfo(true);
                player.getInventory().addItem(7694, 1L, "nobleTiara");
                player.sendMessage("Congratulations! You gained noblesse rank.");
                player.broadcastUserInfo(true);
            } else {
                player.sendMessage("You already have noblesse rank !");
            }
        } else if (command.equalsIgnoreCase("give_hero")) {
            if (!player.isHero()) {
                player.setHero(true);
                player.updatePledgeClass();
                player.addSkill(SkillTable.INSTANCE.getInfo(395, 1));
                player.addSkill(SkillTable.INSTANCE.getInfo(396, 1));
                player.addSkill(SkillTable.INSTANCE.getInfo(1374, 1));
                player.addSkill(SkillTable.INSTANCE.getInfo(1375, 1));
                player.addSkill(SkillTable.INSTANCE.getInfo(1376, 1));
                player.sendPacket(new SkillList(player));
                player.getPlayer().broadcastUserInfo(true);
                player.sendMessage("Congratulations! You gained hero rank.");
                player.broadcastUserInfo(true);
            } else if (player.isNoble()) {
                player.sendMessage("You already have hero rank !");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

    @Override
    public boolean isNpc() {
        return true;
    }

}