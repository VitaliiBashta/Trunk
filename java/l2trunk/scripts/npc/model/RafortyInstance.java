package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;

public final class RafortyInstance extends NpcInstance {
    private static final int FREYA_NECKLACE = 16025;
    private static final int BLESSED_FREYA_NECKLACE = 16026;
    private static final int BOTTLE_OF_FREYAS_SOUL = 16027;

    public RafortyInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("exchange_necklace_1".equalsIgnoreCase(command)) {
            if (ItemFunctions.getItemCount(player, FREYA_NECKLACE) > 0)
                showChatWindow(player, "default/" + getNpcId() + "-ex4.htm");
            else
                showChatWindow(player, "default/" + getNpcId() + "-ex6.htm");
        } else if ("exchange_necklace_2".equalsIgnoreCase(command)) {
            if (ItemFunctions.getItemCount(player, BOTTLE_OF_FREYAS_SOUL) > 0)
                showChatWindow(player, "default/" + getNpcId() + "-ex8.htm");
            else
                showChatWindow(player, "default/" + getNpcId() + "-ex7.htm");
        } else if ("exchange_necklace_3".equalsIgnoreCase(command)) {
            if (ItemFunctions.getItemCount(player, FREYA_NECKLACE) > 0 && ItemFunctions.getItemCount(player, BOTTLE_OF_FREYAS_SOUL) > 0) {
                ItemFunctions.removeItem(player, FREYA_NECKLACE, 1, true, "RafortyInstance");
                ItemFunctions.removeItem(player, BOTTLE_OF_FREYAS_SOUL, 1, true, "RafortyInstance");
                ItemFunctions.addItem(player, BLESSED_FREYA_NECKLACE, 1, true, "RafortyInstance");
                showChatWindow(player, "default/" + getNpcId() + "-ex9.htm");
            } else
                showChatWindow(player, "default/" + getNpcId() + "-ex11.htm");
        } else
            super.onBypassFeedback(player, command);
    }
}