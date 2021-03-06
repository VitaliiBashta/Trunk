package l2trunk.scripts.events.TheFallHarvest;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.handler.items.ScriptItemHandler;
import l2trunk.scripts.npc.model.SquashInstance;

import java.util.List;

public final class Seed extends ScriptItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(6389, // small seed
            6390 // large seed
    );
    private static final List<Integer> NPC_IDS = List.of(12774, // Young Pumpkin
            12777 // Large Young Pumpkin
    );

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        if (player.isInZone(ZoneType.RESIDENCE)) {
            return false;
        }
        if (player.isInOlympiadMode()) {
            player.sendMessage("You can not cultivate a pumpkin at the stadium.");
            return false;
        }
        if (!player.getReflection().isDefault()) {
            player.sendMessage("You can not cultivate a pumpkin in an instance.");
            return false;
        }

        int npcId = NPC_IDS.get(0);
        if (item.getItemId() == ITEM_IDS.get(1)) npcId = NPC_IDS.get(1);

        if (!player.getInventory().destroyItem(item, 1L, "useSeed"))
            return false;

        SimpleSpawner spawn = new SimpleSpawner(npcId)
                .setLoc(Location.findPointToStay(player, 30, 70));
        NpcInstance npc = spawn.doSpawn(true);
        npc.setAI(new SquashAI(npc));
        ((SquashInstance) npc).setSpawner(player);

        ThreadPoolManager.INSTANCE.schedule(spawn::deleteAll, 180000);

        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
    }

}