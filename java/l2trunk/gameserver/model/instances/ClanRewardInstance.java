package l2trunk.gameserver.model.instances;


import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.PledgeShowInfoUpdate;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class ClanRewardInstance extends NpcInstance {

    public ClanRewardInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this)) {
            return;
        }

        if ("getClanReward".equalsIgnoreCase(command)) {
            if (player.getClan() != null) {
                if (player.getClan().getOnlineMembers().size() < 15) {
                    player.sendMessage("You must have atleast 15 members online to receive reward.");
                    return;
                }

                if (!player.isClanLeader()) {
                    player.sendMessage("You must be a clan leader in order to receive reward.");
                    return;
                }

                if (player.getClan().getLevel() >= 6) {
                    player.sendMessage("Your clan is already occupation 6 and can't receive reward.");
                    return;
                }

                if (player.getInventory().getCountOf(37007) < 5) {
                    player.sendMessage("You don't have enough Vote coins!");
                    return;
                }

                player.getClan().setLevel(6);
                player.getClan().incReputation(30000, false, "ClanRewardNpc");
                player.getClan().broadcastToOnlineMembers(new PledgeShowInfoUpdate(player.getClan()));
                player.sendMessage("Your clan received 30 000 clan reputation and occupation 6 from Clan Reward!");
                player.getInventory().addItem(9816, 30, "ClanReward Earth Eggs");
                player.getInventory().addItem(9818, 10, "ClanReward Angelic Essence");
                player.getInventory().addItem(9817, 20, "ClanReward Angelic Essence");
                player.getInventory().addItem(9815, 20, "ClanReward Angelic Essence");
                player.getInventory().addItem(8176, 1, "ClanReward Destruction Tombstone");
                player.getInventory().destroyItemByItemId(37007, 5, "Clan Reward NPC");
            } else {
                player.sendMessage("You don't have clan to use this feature!");
            }
        } else {
            super.onBypassFeedback(player, command);
        }
    }

}