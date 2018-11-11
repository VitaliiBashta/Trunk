package l2trunk.scripts.events.Christmas;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.handler.items.ScriptItemHandler;

import java.util.Arrays;
import java.util.List;

public final class Seed extends ScriptItemHandler implements ScriptFile {
    @Override
    public void onLoad() {
        ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    public class DeSpawnScheduleTimerTask extends RunnableImpl {
        final SimpleSpawner spawnedTree;

        DeSpawnScheduleTimerTask(SimpleSpawner spawn) {
            spawnedTree = spawn;
        }

        @Override
        public void runImpl() {
            spawnedTree.deleteAll();
        }
    }

    private static final Integer[] _itemIds = {5560, // Christmas Tree
            5561 // Special Christmas Tree
    };

    private static final int[] _npcIds = {13006, // Christmas Tree
            13007 // Special Christmas Tree
    };

    private static final int DESPAWN_TIME = 3600000; //60 min

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        Player activeChar = (Player) playable;
        NpcTemplate template = null;

        if (activeChar.isInOlympiadMode() || Olympiad.isRegistered(activeChar)) {
            return false;
        }

        int itemId = item.getItemId();
        for (int i = 0; i < _itemIds.length; i++)
            if (_itemIds[i] == itemId) {
                template = NpcHolder.getInstance().getTemplate(_npcIds[i]);
                break;
            }

        for (NpcInstance npc : World.getAroundNpc(activeChar, 300, 200))
            if (npc.getNpcId() == _npcIds[0] || npc.getNpcId() == _npcIds[1]) {
                activeChar.sendPacket(new SystemMessage2(SystemMsg.SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN).addName(npc));
                return false;
            }

        if (template == null)
            return false;

        if (!activeChar.getInventory().destroyItem(item, 1L, "Seed"))
            return false;

        SimpleSpawner spawn = new SimpleSpawner(template);
        spawn.setLoc(activeChar.getLoc());
        NpcInstance npc = spawn.doSpawn(false);
        npc.setTitle(activeChar.getName()); //FIXME Почему-то не устанавливается
        spawn.respawnNpc(npc);

        // АИ вещающее бафф регена устанавливается только для большой елки
        if (itemId == 5560)
            npc.setAI(new ctreeAI(npc));

        ThreadPoolManager.getInstance().schedule(new DeSpawnScheduleTimerTask(spawn), (activeChar.isInPeaceZone() ? DESPAWN_TIME / 3 : DESPAWN_TIME));
        playable.sendMessage("Christmas Tree will stay here for 1 Hour!");
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return Arrays.asList(_itemIds);
    }
}