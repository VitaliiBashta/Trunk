package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class Battleground extends SimpleItemHandler implements ScriptFile {
    @Override
    public List<Integer> getItemIds() {
        return List.of(10143, 10144, 10145, 10146, 10147, 10148, 10411);
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();

        if (!player.isInZone(ZoneType.SIEGE)) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
            return false;
        }

        if (!useItem(player, item, 1))
            return false;

        switch (itemId) {
            //Battleground Spell - Shield Master
            case 10143:
                List.of(2379, 2380, 2381, 2382, 2383).forEach(skill -> {
                    player.broadcastPacket(new MagicSkillUse(player, skill));
                    player.altOnMagicUseTimer(player, skill);
                });
                break;
            // Battleground Spell - Wizard
            case 10144:
                List.of(2379, 2380, 2381, 2384, 2385).forEach(skill -> {
                    player.broadcastPacket(new MagicSkillUse(player, skill));
                    player.altOnMagicUseTimer(player, skill);
                });
                break;
            // Battleground Spell - Healer
            case 10145:
                List.of(2379, 2380, 2381, 2384, 2386).forEach(skill -> {
                    player.broadcastPacket(new MagicSkillUse(player, skill));
                    player.altOnMagicUseTimer(player, skill);
                });
                break;
            // Battleground Spell - Dagger Master
            case 10146:
                List.of(2379, 2380, 2381, 2388, 2383).forEach(skill -> {
                    player.broadcastPacket(new MagicSkillUse(player, skill));
                    player.altOnMagicUseTimer(player, skill);
                });
                break;
            // Battleground Spell - Bow Master
            case 10147:
                List.of(2379, 2380, 2381, 2389, 2383).forEach(skill -> {
                    player.broadcastPacket(new MagicSkillUse(player, skill));
                    player.altOnMagicUseTimer(player, skill);
                });
                break;
            // Battleground Spell - Bow Master
            case 10148:
                List.of(2390, 2391).forEach(skill -> {
                    player.broadcastPacket(new MagicSkillUse(player, skill));
                    player.altOnMagicUseTimer(player, skill);
                });
                break;
            //Full Bottle of Souls - 5 Souls (For Combat)
            case 10411:
                player.broadcastPacket(new MagicSkillUse(player, 2499));
                player.altOnMagicUseTimer(player, 2499);
                break;
            default:
                return false;
        }

        return true;
    }

    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }
}
