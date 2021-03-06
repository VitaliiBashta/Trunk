package l2trunk.scripts.events.SummerMeleons;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.handler.items.ScriptItemHandler;
import l2trunk.scripts.npc.model.MeleonInstance;

import java.util.Arrays;
import java.util.List;

public final class MeleonSeed extends ScriptItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(15366, // Watermelon seed
            15367);// Honey Watermelon Seed

    private static final List<Integer> NPC_IDS = List.of(13271, // Young Watermelon
            13275);// Young Honey Watermelon

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        if (player.isInZone(ZoneType.RESIDENCE)) {
            return false;
        }
        if (player.isInOlympiadMode()) {
            player.sendMessage("You can not cultivate a watermelon at the stadium.");
            return false;
        }
        if (!player.getReflection().isDefault()) {
            player.sendMessage("You can not cultivate a watermelon in an instance.");
            return false;
        }
        int templateId = NPC_IDS.get(1);
        if (item.getItemId() == ITEM_IDS.get(0)) templateId = NPC_IDS.get(0);

        if (!player.getInventory().destroyItem(item, 1L, "MeleonSeed"))
            return false;

        SimpleSpawner spawn = new SimpleSpawner(templateId);
        spawn.setLoc(Location.findPointToStay(player, 30, 70));
        NpcInstance npc = spawn.doSpawn(true);
        npc.setAI(new MeleonAI(npc));
        ((MeleonInstance) npc).setSpawner(player);

        ThreadPoolManager.INSTANCE.schedule(spawn::deleteAll, 180000);

        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }


    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
    }

}