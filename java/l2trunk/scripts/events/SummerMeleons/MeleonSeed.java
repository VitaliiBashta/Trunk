package l2trunk.scripts.events.SummerMeleons;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.handler.items.ScriptItemHandler;
import l2trunk.scripts.npc.model.MeleonInstance;

import java.util.Arrays;
import java.util.List;

public final class MeleonSeed extends ScriptItemHandler implements ScriptFile {
    private static final Integer[] ITEM_IDS = {15366, // Watermelon seed
            15367 // Honey Watermelon Seed
    };

    private static final int[] _npcIds = {13271, // Young Watermelon
            13275 // Young Honey Watermelon
    };

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        Player activeChar = (Player) playable;
        if (activeChar.isInZone(ZoneType.RESIDENCE)) {
            return false;
        }
        if (activeChar.isInOlympiadMode()) {
            activeChar.sendMessage("You can not cultivate a watermelon at the stadium.");
            return false;
        }
        if (!activeChar.getReflection().isDefault()) {
            activeChar.sendMessage("You can not cultivate a watermelon in an instance.");
            return false;
        }
        NpcTemplate template = null;

        int itemId = item.getItemId();
        for (int i = 0; i < ITEM_IDS.length; i++)
            if (ITEM_IDS[i] == itemId) {
                template = NpcHolder.getTemplate(_npcIds[i]);
                break;
            }

        if (template == null)
            return false;

        if (!activeChar.getInventory().destroyItem(item, 1L, "MeleonSeed"))
            return false;

        SimpleSpawner spawn = new SimpleSpawner(template);
        spawn.setLoc(Location.findPointToStay(activeChar, 30, 70));
        NpcInstance npc = spawn.doSpawn(true);
        npc.setAI(new MeleonAI(npc));
        ((MeleonInstance) npc).setSpawner(activeChar);

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
        return Arrays.asList(ITEM_IDS);
    }

}