package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class LekonInstance extends NpcInstance {
    private static final int ENERGY_STAR_STONE = 13277;
    private static final int AIRSHIP_SUMMON_LICENSE = 13559;

    public LekonInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("get_license".equals(command)) {
            if (player.getClan() == null || !player.isClanLeader() || player.getClan().getLevel() < 5) {
                showChatWindow(player, 2);
                return;
            }

            if (player.getClan().isHaveAirshipLicense() || player.haveItem(AIRSHIP_SUMMON_LICENSE)) {
                showChatWindow(player, 4);
                return;
            }

            if (removeItem(player, ENERGY_STAR_STONE, 10, "LekonInstance") != 10) {
                showChatWindow(player, 3);
                return;
            }

            addItem(player, AIRSHIP_SUMMON_LICENSE, 1);
        } else
            super.onBypassFeedback(player, command);
    }
}
