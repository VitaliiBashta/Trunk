package l2trunk.scripts.services;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.GameObjectTasks;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.PlaySound;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.NpcUtils;
import l2trunk.gameserver.utils.PositionUtils;


public final class Birthday extends Functions {
    private static final int EXPLORERHAT = 10250;
    private static final int HAT = 13488; // Birthday Hat
    private static final int NPC_ALEGRIA = 32600; // Alegria

    private static final String msgSpawned = "scripts/services/Birthday-spawned.htm";

    public void summonAlegria() {
        if (player == null || npc == null || !NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        if (World.getAroundNpc(npc)
                .filter(n -> n.getNpcId() == NPC_ALEGRIA)
                .peek(n -> show(msgSpawned, player, npc))
                .findFirst().isPresent())
            return;


        player.sendPacket(PlaySound.HB01);

        try {

            NpcInstance alegria = NpcUtils.spawnSingle(NPC_ALEGRIA, npc.getLoc().randomOffset(40), 180000);
            alegria.setHeading(PositionUtils.calculateHeadingFrom(alegria, player));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Вызывается у NPC Alegria
     */
    public void exchangeHat() {

        if (player == null || npc == null || !NpcInstance.canBypassCheck(player, player.getLastNpc()) || npc.isBusy())
            return;

        if (!player.haveItem(EXPLORERHAT)) {
            show("default/32600-nohat.htm", player, npc);
            return;
        }
        ItemFunctions.removeItem(player, EXPLORERHAT, 1, "exchangeHat");
        ItemFunctions.addItem(player, HAT, 1, "exchangeHat");
        show("default/32600-successful.htm", player, npc);

        long now = System.currentTimeMillis() / 1000;
        player.setVar("Birthday",now);

        npc.setBusy(true);

        ThreadPoolManager.INSTANCE.execute(new GameObjectTasks.DeleteTask(npc));
    }

}