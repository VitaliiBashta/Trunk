package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.scripts.quests._234_FatesWhisper;
import l2trunk.scripts.quests._235_MimirsElixir;
import l2trunk.scripts.quests._236_SeedsOfChaos;

public final class NoblessSell extends Functions {
    public void get() {
        Player player = getSelf();

        if (player.isNoble()) {
            player.sendMessage("You are already Nobless!");
            return;
        }
        if (!Config.SERVICES_NOBLESS_SELL_ENABLED) {
            show("Service is disabled.", player);
            return;
        }//setleve
        if ((player.getLevel() < 75) && (player.getActiveClass().isBase())) {
            player.sendMessage("You need to be over 75 level to purchase noblesse!");
            return;
        }

        if (player.getInventory().destroyItemByItemId(Config.SERVICES_NOBLESS_SELL_ITEM, Config.SERVICES_NOBLESS_SELL_PRICE, "NoblessSell")) {
            makeSubQuests();
            becomeNoble();
        } else if (Config.SERVICES_NOBLESS_SELL_ITEM == 37000)
            player.sendMessage("You don't have 10 Donator Coins!");
        else
            player.sendMessage("You don't have 10 Donator Coins!");
    }

    private void makeSubQuests() {
        Player player = getSelf();
        if (player == null)
            return;
        QuestState qs = player.getQuestState(_234_FatesWhisper.class);
        if (qs != null)
            qs.exitCurrentQuest(true);
        QuestManager.getQuest(_234_FatesWhisper.class).newQuestState(player, Quest.COMPLETED);

        if (player.getRace() == Race.kamael) {
            qs = player.getQuestState(_236_SeedsOfChaos.class);
            if (qs != null)
                qs.exitCurrentQuest(true);
            QuestManager.getQuest(_236_SeedsOfChaos.class).newQuestState(player, Quest.COMPLETED);
        } else {
            qs = player.getQuestState(_235_MimirsElixir.class);
            if (qs != null)
                qs.exitCurrentQuest(true);
            QuestManager.getQuest(_235_MimirsElixir.class).newQuestState(player, Quest.COMPLETED);
        }
    }

    private void becomeNoble() {
        Player player = getSelf();
        if (player == null || player.isNoble())
            return;

        Olympiad.addNoble(player);
        player.setNoble(true);
        player.updatePledgeClass();
        player.updateNobleSkills();
        player.sendPacket(new SkillList(player));
        player.getInventory().addItem(7694, 1L, "nobleTiara");
        player.sendMessage("Congratulations! You gained noblesse rank.");
        player.broadcastUserInfo(true);
        player.broadcastPacket(new MagicSkillUse(player,  6696,  1000));
    }
}