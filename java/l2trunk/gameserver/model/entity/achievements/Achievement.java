package l2trunk.gameserver.model.entity.achievements;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.reward.RewardItemResult;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.utils.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public final class Achievement {
    private final int id;
    private final int level;
    final String name;
    private final int categoryId;
    private final String icon;
    private final String desc;
    private final long pointsToComplete;
    private final String achievementType;
    private final int fame;
    private final List<RewardItemResult> rewards;

    public Achievement(int id, int level, String name, int categoryId, String icon, String desc, long pointsToComplete, String achievementType, int fame) {
        this.id = id;
        this.level = level;
        this.name = name;
        this.categoryId = categoryId;
        this.icon = icon;
        this.desc = desc;
        this.pointsToComplete = pointsToComplete;
        this.achievementType = achievementType;
        this.fame = fame;
        rewards = new LinkedList<>();
    }

    public boolean isDone(long playerPoints) {
        return playerPoints >= pointsToComplete;
    }

    public String getNotDoneHtml(Player pl, int playerPoints) {
        String oneAchievement = HtmCache.INSTANCE.getNotNull("achievements/oneAchievement.htm", pl);

        int greenbar = (int) (24 * (playerPoints * 100 / pointsToComplete) / 100);
        greenbar = Math.max(greenbar, 0);

        if (greenbar > 24) {
            pl.sendMessage(new CustomMessage("l2r.gameserver.achievements.iachievement.applying_fix"));
            return "";
        }

        oneAchievement = oneAchievement.replaceFirst("%fame%", "" + fame);
        oneAchievement = oneAchievement.replaceAll("%bar1%", "" + greenbar);
        oneAchievement = oneAchievement.replaceAll("%bar2%", "" + (24 - greenbar));

        oneAchievement = oneAchievement.replaceFirst("%cap1%", greenbar > 0 ? "Gauge_DF_Food_Left" : "Gauge_DF_Exp_bg_Left");
        oneAchievement = oneAchievement.replaceFirst("%cap2%", "Gauge_DF_Exp_bg_Right");

        oneAchievement = oneAchievement.replaceFirst("%desc%", desc.replaceAll("%need%", "" + Math.max(0, pointsToComplete - playerPoints)));

        oneAchievement = oneAchievement.replaceFirst("%bg%", id % 2 == 0 ? "090908" : "0f100f");
        oneAchievement = oneAchievement.replaceFirst("%icon%", icon);
        oneAchievement = oneAchievement.replaceFirst("%name%", name + (level > 1 ? (" Lv. " + level) : ""));
        return oneAchievement;
    }

    public String getDoneHtml() {
        String oneAchievement = HtmCache.INSTANCE.getNullable("achievements/oneAchievement.htm");

        oneAchievement = oneAchievement.replaceFirst("%fame%", "" + fame);
        oneAchievement = oneAchievement.replaceAll("%bar1%", "24");
        oneAchievement = oneAchievement.replaceAll("%bar2%", "0");

        oneAchievement = oneAchievement.replaceFirst("%cap1%", "Gauge_DF_Food_Left");
        oneAchievement = oneAchievement.replaceFirst("%cap2%", "Gauge_DF_Food_Right");

        oneAchievement = oneAchievement.replaceFirst("%desc%", "Done.");

        oneAchievement = oneAchievement.replaceFirst("%bg%", id % 2 == 0 ? "090908" : "0f100f");
        oneAchievement = oneAchievement.replaceFirst("%icon%", icon);
        oneAchievement = oneAchievement.replaceFirst("%name%", name + (level > 1 ? (" Lv. " + level) : ""));
        return oneAchievement;
    }

    public void reward(Player player) {
        synchronized (player.getAchievements()) {
            player.sendChatMessage(player.objectId(), 20, "Achievement Completed!", name);
            player.getAchievements().put(id, level);

            player.setFame(player.getFame() + getFame());
            Log.add("game", "Achievements: Player " + player.getName() + " recived " + getFame() + " fame from achievement " + name);

            InventoryUpdate iu = new InventoryUpdate();
            for (ItemInstance item : getRewards().stream().map(RewardItemResult::createItem).collect(Collectors.toList())) {
                player.getInventory().addItem(item, "Achievement:" + name);
                iu.addNewItem(item);
            }

            player.broadcastPacket(iu, new MagicSkillUse(player, 2528, 0, 500));
        }
    }

    private List<RewardItemResult> getRewards() {
        return rewards;
    }

    public String getDesc() {
        return desc;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public void addReward(int itemId, long itemCount) {
        rewards.add(new RewardItemResult(itemId, itemCount));
    }

    public String getType() {
        return achievementType;
    }

    public long getPointsToComplete() {
        return pointsToComplete;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public String getIcon() {
        return icon;
    }

    public int getFame() {
        return fame;
    }
}
