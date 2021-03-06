package l2trunk.scripts.npc.model.residences.fortress.peace;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.DoorObject;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.npc.model.residences.fortress.FacilityManagerInstance;

import java.util.List;
import java.util.Map;

public final class GuardCaptionInstance extends FacilityManagerInstance {
    public GuardCaptionInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        Fortress fortress = getFortress();
        if (command.equalsIgnoreCase("defenceInfo")) {
            if ((player.getClanPrivileges() & Clan.CP_CS_MANAGE_SIEGE) != Clan.CP_CS_MANAGE_SIEGE) {
                showChatWindow(player, "residence2/fortress/fortress_not_authorized.htm");
                return;
            }

            if (fortress.getContractState() != Fortress.CONTRACT_WITH_CASTLE) {
                showChatWindow(player, "residence2/fortress/fortress_supply_officer005.htm");
                return;
            }

            showChatWindow(player, "residence2/fortress/fortress_garrison002.htm",
                    Map.of("%facility_0%", String.valueOf(fortress.getFacilityLevel(Fortress.REINFORCE)),
                    "%facility_2%", String.valueOf(fortress.getFacilityLevel(Fortress.DOOR_UPGRADE))
                    , "%facility_3%", String.valueOf(fortress.getFacilityLevel(Fortress.DWARVENS)),
                    "%facility_4%", String.valueOf(fortress.getFacilityLevel(Fortress.SCOUT))));
        } else if (command.equalsIgnoreCase("defenceUp1") || command.equalsIgnoreCase("defenceUp2"))
            buyFacility(player, Fortress.REINFORCE, Integer.parseInt(command.substring(9, 10)), 100000);
        else if (command.equalsIgnoreCase("deployScouts"))
            buyFacility(player, Fortress.SCOUT, 1, 150000);
        else if (command.equalsIgnoreCase("doorUpgrade")) {
            boolean buy = buyFacility(player, Fortress.DOOR_UPGRADE, 1, 200000);
            if (buy) {
                List<DoorObject> doorObjects = fortress.getSiegeEvent().getObjects(FortressSiegeEvent.UPGRADEABLE_DOORS);
                for (DoorObject d : doorObjects)
                    d.setUpgradeValue(fortress.getSiegeEvent(), d.getDoor().getMaxHp() * fortress.getFacilityLevel(Fortress.DOOR_UPGRADE));
            }
        } else if (command.equalsIgnoreCase("hireDwarves"))
            buyFacility(player, Fortress.DWARVENS, 1, 100000);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        showChatWindow(player, "residence2/fortress/fortress_garrison001.htm");
    }
}
