package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class SteelCitadelTeleporterInstance extends NpcInstance {
    public SteelCitadelTeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (!player.isInParty()) {
            showChatWindow(player, "default/32745-1.htm");
            return;
        }
        if (!player.isPartyLeader()) {
            showChatWindow(player, "default/32745-2.htm");
            return;
        }
        if (!rangeCheck(player)) {
            showChatWindow(player, "default/32745-2.htm");
            return;
        }
        switch (command.toLowerCase()) {
            case "01_up":
                player.getParty().teleport(new Location(-22208, 277122, -13376));
                break;
            case "02_up":
                player.getParty().teleport(new Location(-22208, 277106, -11648));
                break;
            case "02_down":
                player.getParty().teleport(new Location(-22208, 277074, -15040));
                break;
            case "03_up":
                player.getParty().teleport(new Location(-22208, 277120, -9920));
                break;
            case "03_down":
                player.getParty().teleport(new Location(-22208, 277120, -13376));
                break;
            case "04_up":
                player.getParty().teleport(new Location(-19024, 277126, -8256));
                break;
            case "04_down":
                player.getParty().teleport(new Location(-22208, 277106, -11648));
                break;
            case "06_up":
                player.getParty().teleport(new Location(-19024, 277106, -9920));
                break;
            case "06_down":
                player.getParty().teleport(new Location(-22208, 277122, -9920));
                break;
            case "07_up":
                player.getParty().teleport(new Location(-19008, 277100, -11648));
                break;
            case "07_down":
                player.getParty().teleport(new Location(-19024, 277122, -8256));
                break;
            case "08_up":
                player.getParty().teleport(new Location(-19008, 277100, -13376));
                break;
            case "08_down":
                player.getParty().teleport(new Location(-19008, 277106, -9920));
                break;
            case "09_up":
                player.getParty().teleport(new Location(14602, 283179, -7500));
                break;
            case "09_down":
                player.getParty().teleport(new Location(-19008, 277100, -11648));
                break;
            case "facedemon":
                enterInstance(player, 5);
                break;
            case "faceranku":
                enterInstance(player, 6);
                break;
            case "leave":
                player.getReflection().collapse();
                break;
            default:
                super.onBypassFeedback(player, command);
                break;
        }
    }

    private boolean rangeCheck(Player pl) {
        return pl.getParty().getMembers().stream().allMatch(m -> pl.isInRange(m, 400));
    }

    private int getIz(int floor) {
        if (floor == 5)
            return 3;
        else
            return 4;
    }

    private void enterInstance(Player player, int floor) {
        Reflection r = player.getActiveReflection();
        if (r != null) {
            if (player.canReenterInstance(getIz(floor)))
                player.teleToLocation(r.getTeleportLoc(), r);
        } else if (player.canEnterInstance(getIz(floor))) {
            ReflectionUtils.enterReflection(player, getIz(floor));
        }
    }
}