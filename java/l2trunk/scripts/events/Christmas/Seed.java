package l2trunk.scripts.events.Christmas;

import l2trunk.gameserver.ThreadPoolManager;
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
import l2trunk.scripts.handler.items.ScriptItemHandler;

import java.util.Arrays;
import java.util.List;

public final class Seed extends ScriptItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(5560, // Christmas Tree
            5561 // Special Christmas Tree
    );
    private static final List<Integer> NPC_IDS = List.of(13006, // Christmas Tree
            13007 // Special Christmas Tree
    );
    private static final int DESPAWN_TIME = 3600000; //60 min

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }


    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        Player activeChar = (Player) playable;

        if (activeChar.isInOlympiadMode() || Olympiad.isRegistered(activeChar)) return false;

        int itemId = item.getItemId();
        int npcId = NPC_IDS.get(0);
        if (itemId == ITEM_IDS.get(1)) npcId = NPC_IDS.get(1);

        if (World.getAroundNpc(activeChar, 300, 200)
                .filter(npc -> NPC_IDS.contains(npc.getNpcId()))
                .peek(npc -> activeChar.sendPacket(new SystemMessage2(SystemMsg.SINCE_S1_ALREADY_EXISTS_NEARBY_YOU_CANNOT_SUMMON_IT_AGAIN).addName(npc)))
                .findFirst().isPresent())
            return false;

        if (!activeChar.getInventory().destroyItem(item, 1L, "Seed"))
            return false;

        SimpleSpawner spawn = new SimpleSpawner(npcId)
                .setLoc(activeChar.getLoc());
        NpcInstance npc = spawn.doSpawn(false);
        npc.setTitle(activeChar.getName()); //FIXME Почему-то не устанавливается
        spawn.respawnNpc(npc);

        // АИ вещающее бафф регена устанавливается только для большой елки
        if (itemId == 5560)
            npc.setAI(new ctreeAI(npc));

        ThreadPoolManager.INSTANCE.schedule(spawn::deleteAll, DESPAWN_TIME);
        playable.sendMessage("Christmas Tree will stay here for 1 Hour!");
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}