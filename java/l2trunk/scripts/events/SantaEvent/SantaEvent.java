package l2trunk.scripts.events.SantaEvent;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;

import java.util.Map;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class SantaEvent extends Functions implements ScriptFile, OnDeathListener {
    private static final Map<Integer, Double> RANDOM_MONSTER_REWARD_CHANCE = Map.of(
            5557, 0.3,
            5556, 0.3,
            5558, 1.0,
            5559, 0.09);
    private static final Map<Integer, Integer> SANTA_REWARD_REQUIRED_ITEM_AMOUNT = Map.of(
            5557, 4,
            5556, 4,
            5558, 10,
            5559, 1);
    private static final int SANTA_TREE = 5560;
    private static final int SANTA_HAT = 7836;
    private static final int MAX_LEVEL_DIFFERENCE = 5;

    private static final int SANTA_NPC_ID = 105;
    private static final Location SANTA_LOC = new Location(83608, 149192, -3400, 49524);

    private static boolean isActive() {
        return Config.EVENT_SANTA_ALLOW;
    }

    private static void onMonsterKilled(Player player) {
        RANDOM_MONSTER_REWARD_CHANCE.entrySet().stream()
                .filter(e -> Rnd.chance(e.getValue() * Config.EVENT_SANTA_CHANCE_MULT))
                .findFirst().ifPresent(e -> giveMonsterReward(player, e.getKey()));
    }

    private static void giveMonsterReward(Player player, int itemId) {
        addItem(player, itemId, 1L);
        player.sendMessage("Hey! Santa needs it! Tell him what you have found!");
    }

    private static boolean checkRequiredItems(Player player) {
        return SANTA_REWARD_REQUIRED_ITEM_AMOUNT.entrySet().stream()
                .allMatch(e -> player.haveItem(e.getKey(), e.getValue()));
    }

    private static void removeRequiredItems(Player player) {
        SANTA_REWARD_REQUIRED_ITEM_AMOUNT.forEach((k, v) ->
                removeItem(player, k, v, "SantaEventReward"));
    }

    @Override
    public void onLoad() {
        if (isActive()) {
            CharListenerList.addGlobal(this);
            NpcUtils.spawnSingle(SANTA_NPC_ID,SANTA_LOC );
        }
    }

    @Override
    public void onDeath(Creature actor, Creature killer) {
        if (killer instanceof Playable && isActive() && Math.abs(actor.getLevel() - killer.getLevel()) <= MAX_LEVEL_DIFFERENCE) {
            onMonsterKilled(((Playable)killer).getPlayer());
        }
    }

    public void getRewardFromSanta() {//Method run from Santa Npc
        if (!checkRequiredItems(player)) {
            player.sendMessage("Sorry but you don't have required Items!");
            return;
        }
        removeRequiredItems(player);
        addItem(player, SANTA_TREE, 1);
        addItem(player, SANTA_HAT, 1);
        player.sendMessage("Santa is really grateful! You can still bring him more!");
    }

}
