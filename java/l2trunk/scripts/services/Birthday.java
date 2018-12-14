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

import java.util.Calendar;


public final class Birthday extends Functions {
    private static final int EXPLORERHAT = 10250;
    private static final int HAT = 13488; // Birthday Hat
    private static final int NPC_ALEGRIA = 32600; // Alegria

    private static final String msgSpawned = "scripts/services/Birthday-spawned.htm";

    /**
     * Вызывается у гейткиперов
     */
    public void summonAlegria() {
        Player player = getSelf();
        NpcInstance npc = getNpc();

        if (player == null || npc == null || !NpcInstance.canBypassCheck(player, player.getLastNpc()))
            return;

        //TODO: На оффе можно вызвать до 3х нпсов. Но зачем? о.0
        for (NpcInstance n : World.getAroundNpc(npc))
            if (n.getNpcId() == NPC_ALEGRIA) {
                show(msgSpawned, player, npc);
                return;
            }

        player.sendPacket(PlaySound.HB01);

        try {
            //Спаним Аллегрию где-то спереди от ГК
            int x = (int) (npc.getX() + 40 * Math.cos(npc.headingToRadians(npc.getHeading() - 32768 + 8000)));
            int y = (int) (npc.getY() + 40 * Math.sin(npc.headingToRadians(npc.getHeading() - 32768 + 8000)));

            NpcInstance alegria = NpcUtils.spawnSingle(NPC_ALEGRIA, x, y, npc.getZ(), 180000);
            alegria.setHeading(PositionUtils.calculateHeadingFrom(alegria, player));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Вызывается у NPC Alegria
     */
    public void exchangeHat() {
        Player player = getSelf();
        final NpcInstance npc = getNpc();

        if (player == null || npc == null || !NpcInstance.canBypassCheck(player, player.getLastNpc()) || npc.isBusy())
            return;

        if (ItemFunctions.getItemCount(player, EXPLORERHAT) < 1) {
            show("default/32600-nohat.htm", player, npc);
            return;
        }
        ItemFunctions.removeItem(player, EXPLORERHAT, 1, true, "exchangeHat");
        ItemFunctions.addItem(player, HAT, 1, true, "exchangeHat");
        show("default/32600-successful.htm", player, npc);

        long now = System.currentTimeMillis() / 1000;
        player.setVar("Birthday", String.valueOf(now), -1);

        npc.setBusy(true);

        ThreadPoolManager.INSTANCE.execute(new GameObjectTasks.DeleteTask(npc));
    }

}