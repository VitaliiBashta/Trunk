package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class Cocktails extends SimpleItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(10178, 15356, 20393, 10179, 15357, 20394, 14739);
    // Sweet Fruit Cocktail
    private static final List<Integer> sweet_list = List.of(2404, // Might
            2405, // Shield
            2406, // Wind Walk
            2407, // Focus
            2408, // Death Whisper
            2409, // Guidance
            2410, // Bless Shield
            2411, // Bless Body
            2412, // Haste
            2413); // Vampiric Rage
    // Fresh Fruit Cocktail
    private static final List<Integer> fresh_list = List.of(2414, // Berserker Spirit
            2411, // Bless Body
            2415, // Magic Barrier
            2405, // Shield
            2406, // Wind Walk
            2416, // Bless Soul
            2417, // Empower
            2418, // Acumen
            2419); // Clarity
    //Event - Fresh Milk
    private static final List<Integer> milk_list = List.of(
            2873, 2874, 2875, 2876, 2877, 2878, 2879, 2885, 2886, 2887, 2888, 2889, 2890);

    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();

        if (player.isInOlympiadMode()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
            return false;
        }

        if (!useItem(player, item, 1))
            return false;

        switch (itemId) {
            // Sweet Fruit Cocktail
            case 10178:
            case 15356:
            case 20393:
                sweet_list.forEach(skill -> {
                    player.broadcastPacket(new MagicSkillUse(player, skill));
                    player.altOnMagicUseTimer(player, skill);
                });
                break;
            // Fresh Fruit Cocktail
            case 10179:
            case 15357:
            case 20394:
                for (int skill : fresh_list) {
                    player.broadcastPacket(new MagicSkillUse(player, skill));
                    player.altOnMagicUseTimer(player, skill);
                }
                break;
            //Event - Fresh Milk
            case 14739:
                player.broadcastPacket(new MagicSkillUse(player, 2873));
                player.altOnMagicUseTimer(player, 2891, 6);
                milk_list.forEach(skill -> {
                    player.broadcastPacket(new MagicSkillUse(player, skill));
                    player.altOnMagicUseTimer(player, skill);
                });
            default:
                return false;
        }

        return true;
    }
}