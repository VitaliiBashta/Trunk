package l2trunk.scripts.npc.model;

import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.bosses.BaylorManager;
import l2trunk.scripts.instances.CrystalCaverns;

public final class CrystalCavernControllerInstance extends NpcInstance {
    public CrystalCavernControllerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String htmlpath = null;
        if (val == 0) {
            if (player.isPartyLeader()) {
                if (getNpcId() == 32280)
                    htmlpath = "default/32280-2.htm";
                else if (getNpcId() == 32278)
                    htmlpath = "default/32278.htm";
                else if (getNpcId() == 32276)
                    htmlpath = "default/32276.htm";
                else if (getNpcId() == 32279)
                    htmlpath = "default/32279.htm";
                else if (getNpcId() == 32277)
                    htmlpath = "default/32277.htm";
            } else
                htmlpath = "default/32280-1.htm";
        } else
            htmlpath = "default/32280-1.htm";
        return htmlpath;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("request_emerald".equalsIgnoreCase(command)) {
            ((CrystalCaverns) getReflection()).notifyEmeraldRequest();
        } else if ("request_coral".equalsIgnoreCase(command)) {
            ((CrystalCaverns) getReflection()).notifyCoralRequest();
        } else if ("request_baylor".equalsIgnoreCase(command)) {
            int state = BaylorManager.canIntoBaylorLair(player);
            if (state == 1 || state == 2) {
                showChatWindow(player, "default/32276-1.htm");
                return;
            } else if (state == 4) {
                showChatWindow(player, "default/32276-2.htm");
                return;
            } else if (state == 3) {
                showChatWindow(player, "default/32276-3.htm");
                return;
            }
            if (player.isInParty()) {
                for (Player p : player.getParty().getMembers()) {
                    if (!p.haveItem(9695)) {
                        Functions.npcSay(this, NpcString.S1___________________, p.getName());
                        return;
                    }
                    if (!p.haveItem(9696) ) {
                        Functions.npcSay(this, NpcString.S1__________________, p.getName());
                        return;
                    }
                    if (!p.haveItem(9697)) {
                        Functions.npcSay(this, NpcString.YOU_DONT_HAVE_CLEAR_CRYSTAL, p.getName());
                        return;
                    }
                    if (!isInRange(p, 400)) {
                        Functions.npcSay(this, NpcString.FAR_AWAY, p.getName());
                        return;
                    }
                }
                ItemFunctions.addItem(player, 10015, 1, "CrystalCavernControllerInstance");
                player.getParty().getMembersStream().forEach(p -> {
                    ItemFunctions.removeItem(p, 9695, 1, "CrystalCavernControllerInstance");
                    ItemFunctions.removeItem(p, 9696, 1, "CrystalCavernControllerInstance");
                    ItemFunctions.removeItem(p, 9697, 1, "CrystalCavernControllerInstance");
                    p.teleToLocation(Location.of(153526, 142172, -12736));
                });
                BaylorManager.entryToBaylorLair(player);
                deleteMe();
            }
        } else if ("request_parme".equalsIgnoreCase(command)) {
            player.teleToLocation(new Location(153736, 142008, -9744));
        } else if ("request_exit".equalsIgnoreCase(command)) {
            if (getReflection().getInstancedZoneId() == 10)
                player.teleToLocation(getReflection().getInstancedZone().getReturnCoords(), ReflectionManager.DEFAULT);
        } else
            super.onBypassFeedback(player, command);
    }
}
