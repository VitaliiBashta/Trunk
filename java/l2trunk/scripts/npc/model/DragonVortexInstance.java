package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;


public final class DragonVortexInstance extends NpcInstance {
    private final int[] bosses = {25718, 25719, 25720, 25721, 25722, 25723, 25724};
    private NpcInstance boss;

    public DragonVortexInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("request_boss")) {
            if (getBoss() != null && !getBoss().isDead()) {
                showChatWindow(player, "default/32871-3.htm");
                return;
            }

            if (ItemFunctions.getItemCount(player, 17248) > 0) {
                ItemFunctions.removeItem(player, 17248, 1, true, "DragonVortex");
                boss = NpcUtils.spawnSingle(bosses[Rnd.get(bosses.length)], Location.coordsRandomize(getLoc(), 300, 600), getReflection());
                ThreadPoolManager.INSTANCE.schedule(() -> {
                    if (getBoss() != null && !getBoss().isDead())
                        setBoss(null);
                }, 1800000);
                showChatWindow(player, "default/32871-1.htm");
            } else
                showChatWindow(player, "default/32871-2.htm");
        } else
            super.onBypassFeedback(player, command);
    }

    private NpcInstance getBoss() {
        return boss;
    }

    private void setBoss(NpcInstance _boss) {
        boss = _boss;
    }
}