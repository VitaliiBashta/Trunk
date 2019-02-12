package l2trunk.scripts.actions;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.RaidBossInstance;
import l2trunk.gameserver.model.reward.*;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.HtmlUtils;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;
import l2trunk.scripts.services.community.CommunityDropCalculator;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class RewardListInfo {
    private static final NumberFormat pf = NumberFormat.getPercentInstance(Locale.ENGLISH);
    private static final NumberFormat df = NumberFormat.getInstance(Locale.ENGLISH);

    static {
        pf.setMaximumFractionDigits(4);
        df.setMinimumFractionDigits(2);
    }

    static void showInfo(Player player, NpcInstance npc) {
        double mod = npc.calcStat(Stats.REWARD_MULTIPLIER, 1.0, player, null);

        showInfo(player, npc.getTemplate(), npc instanceof RaidBossInstance, npc instanceof SiegeGuardInstance, mod);
    }

    public static void showInfo(Player player, NpcTemplate npcTemplate, boolean isBoss, boolean isSiegeGuard, double mod) {
        if (!Config.ALLOW_DROP_CALCULATOR)
            return;

        final int diff = NpcInstance.calculateLevelDiffForDrop(npcTemplate.level, player.isInParty() ? player.getParty().getLevel() : player.getLevel(), isBoss);
        mod *= Experience.penaltyModifier(diff, 9);

        NpcHtmlMessage htmlMessage = new NpcHtmlMessage(5);
        htmlMessage.replace("%npc_name%", npcTemplate.name());

        //@SuppressWarnings("unused")
        //boolean icons = player.isVarSet("DroplistIcons");

        if (mod <= 0) {
            htmlMessage.setFile("actions/rewardlist_to_weak.htm");
            player.sendPacket(htmlMessage);
            return;
        }

        if (npcTemplate.getRewards().isEmpty()) {
            htmlMessage.setFile("actions/rewardlist_empty.htm");
            player.sendPacket(htmlMessage);
            return;
        }

        htmlMessage.setFile("actions/rewardlist_info.htm");

        StringBuilder builder = new StringBuilder(100);
        if (npcTemplate.getRewards().containsKey(RewardType.SWEEP)) {
            builder.append("<font name=\"hs12\" color=127b21>Spoil:</font><br>");
            showListedRewards(builder, RewardType.SWEEP, npcTemplate.getRewardList(RewardType.SWEEP), player, npcTemplate);
            builder.append("<br>");
        }
        if (npcTemplate.getRewards().containsKey(RewardType.RATED_GROUPED)) {
            builder.append("<font name=\"hs12\" color=127b21>Drop:</font><br>");
            showListedRewards(builder, RewardType.RATED_GROUPED, npcTemplate.getRewardList(RewardType.RATED_GROUPED), player, npcTemplate);
            builder.append("<br>");
        }
        RewardType[] rest = {RewardType.NOT_RATED_GROUPED, RewardType.NOT_RATED_NOT_GROUPED};
        for (RewardType type : rest) {
            if (npcTemplate.getRewards().containsKey(type)) {
                showListedRewards(builder, type, npcTemplate.getRewardList(type), player, npcTemplate);
            }
        }
        htmlMessage.replace("%info%", builder.toString());
        player.sendPacket(htmlMessage);
    }

    private static void showListedRewards(StringBuilder tmp, RewardType type, RewardList rewardList, Player player, NpcTemplate template) {
        for (RewardGroup g : rewardList) {
            List<RewardData> items = g.getItems();

            tmp.append("<table>");
            for (RewardData d : items) {
                String icon = d.getItem().getIcon();
                if (icon == null || icon.equals(""))
                    icon = "icon.etc_question_mark_i00";
                tmp.append("<tr><td width=32><img src=").append(icon).append(" width=32 height=32></td><td width=238><font color=a47a3e>").append(HtmlUtils.htmlItemName(d.getItemId())).append("</font><br1>");

                long[] counts = CalculateRewardChances.getDropCounts(player, template, type != RewardType.SWEEP,
                        d.getItemId());
                String chance = CalculateRewardChances.getDropChance(player, template, type != RewardType.SWEEP, d.getItemId());
                tmp.append("<font color=\"b09979\">[").append(counts[0]).append("...").append(counts[1]).append("]&nbsp;");
                tmp.append(CommunityDropCalculator.formatDropChance(chance)).append("</font></td></tr>");
            }
            tmp.append("</table>");
        }
    }

}