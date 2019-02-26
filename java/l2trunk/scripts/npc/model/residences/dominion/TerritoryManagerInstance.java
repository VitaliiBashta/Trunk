package l2trunk.scripts.npc.model.residences.dominion;

import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.entity.residence.Dominion;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.HtmlUtils;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.scripts.quests._234_FatesWhisper;
import l2trunk.scripts.quests._235_MimirsElixir;
import l2trunk.scripts.quests._236_SeedsOfChaos;

public final class TerritoryManagerInstance extends NpcInstance {
    public TerritoryManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        Dominion dominion = getDominion();
        DominionSiegeEvent siegeEvent = dominion.getSiegeEvent();
        int npcId = getNpcId();
        int badgeId = 13676 + dominion.getId();

        if ("buyspecial".equalsIgnoreCase(command)) {
            if (player.haveItem( badgeId) ) {
                MultiSellHolder.INSTANCE.SeparateAndSend(npcId, player, 0);
            } else {
                showChatWindow(player, 1);
            }
        } else if ("buyNobless".equalsIgnoreCase(command)) {
            if (player.isNoble()) {
                showChatWindow(player, 9);
                return;
            }
            if (player.consumeItem(badgeId, 100L)) {
                QuestState st = player.getQuestState(_234_FatesWhisper.class);
                if (st != null) {
                    st.exitCurrentQuest();
                    st.quest.newQuestState(player, Quest.COMPLETED);
                }

                if (player.getRace() == Race.kamael) {
                    st = player.getQuestState(_236_SeedsOfChaos.class);
                    if (st != null) {
                        st.exitCurrentQuest();
                        st.quest.newQuestState(player, Quest.COMPLETED);
                    }
                } else {
                    st = player.getQuestState(_235_MimirsElixir.class);
                    if (st != null) {
                        st.exitCurrentQuest();
                        st.quest.newQuestState(player, Quest.COMPLETED);
                    }
                }

                Olympiad.addNoble(player);
                player.setNoble(true);
                player.updatePledgeClass();
                player.updateNobleSkills();
                player.sendPacket(new SkillList(player));
                player.broadcastUserInfo(true);
                showChatWindow(player, 10);
            } else
                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
        } else if ("calculate".equalsIgnoreCase(command)) {
            if (!player.isQuestContinuationPossible(true))
                return;
            int[] rewards = siegeEvent.calculateReward(player);
            if (rewards == null || rewards[0] == 0) {
                showChatWindow(player, 4);
                return;
            }

            NpcHtmlMessage html = new NpcHtmlMessage(player, this, getHtmlPath(npcId, 5, player), 5);
            html.replace("%territory%", HtmlUtils.htmlResidenceName(dominion.getId()));
            html.replace("%badges%", rewards[0]);
            html.replace("%adena%", rewards[1]);
            html.replace("%fame%", rewards[2]);
            player.sendPacket(html);
        } else if ("recivelater".equalsIgnoreCase(command))
            showChatWindow(player, getHtmlPath(npcId, 6, player));
        else if ("recive".equalsIgnoreCase(command)) {
            int[] rewards = siegeEvent.calculateReward(player);
            if (rewards == null || rewards[0] == 0) {
                showChatWindow(player, 4);
                return;
            }

            ItemFunctions.addItem(player, badgeId, rewards[0], "TerritoryManager");
            ItemFunctions.addItem(player, ItemTemplate.ITEM_ID_ADENA, rewards[1], "TerritoryManager");
            if (rewards[2] > 0)
                player.addFame(rewards[2], "CalcBadges:" + dominion.getId());

            siegeEvent.clearReward(player.objectId());
            showChatWindow(player, 7);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        if (player.getLevel() < 40 || player.getClassId().occupation() < 2)
            val = 8;
        return val == 0 ? "residence2/dominion/TerritoryManager.htm" : "residence2/dominion/TerritoryManager-" + val + ".htm";
    }
}