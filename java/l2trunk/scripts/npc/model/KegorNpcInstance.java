package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class KegorNpcInstance extends NpcInstance {
    public KegorNpcInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String htmlpath;
        if (getReflection().isDefault())
            htmlpath = "default/32761-default.htm";
        else
            htmlpath = "default/32761.htm";
        return htmlpath;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("request_stone".equalsIgnoreCase(command)) {
            if (player.getInventory().getCountOf(15469) == 0 && player.getInventory().getCountOf(15470) == 0)
                Functions.addItem(player, 15469, 1, "KegorNpcInstance");
            else
                player.sendMessage("You can't take more than 1 Frozen Core.");
        } else
            super.onBypassFeedback(player, command);
    }
}