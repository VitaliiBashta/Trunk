package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.data.xml.holder.AirshipDockHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.ClanAirShip;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.AirshipDock;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class ClanAirShipControllerInstance extends AirShipControllerInstance {
    private static final int ENERGY_STAR_STONE = 13277;
    private static final int AIRSHIP_SUMMON_LICENSE = 13559;
    private final AirshipDock airshipDock;
    private final AirshipDock.AirshipPlatform _platform;
    private ClanAirShip _dockedShipRef = null;

    public ClanAirShipControllerInstance(int objectID, NpcTemplate template) {
        super(objectID, template);
        int dockId = template.getAiParams().getInteger("dockId");
        int platformId = template.getAiParams().getInteger("platformId");
        airshipDock = AirshipDockHolder.getDock(dockId);
        _platform = airshipDock.getPlatform(platformId);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("summon".equalsIgnoreCase(command)) {
            if (player.getClan() == null || player.getClan().getLevel() < 5) {
                player.sendPacket(SystemMsg.IN_ORDER_TO_ACQUIRE_AN_AIRSHIP_THE_CLANS_LEVEL_MUST_BE_LEVEL_5_OR_HIGHER);
                return;
            }

            if ((player.getClanPrivileges() & Clan.CP_CL_SUMMON_AIRSHIP) != Clan.CP_CL_SUMMON_AIRSHIP) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }

            if (!player.getClan().isHaveAirshipLicense()) {
                player.sendPacket(SystemMsg.AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_EITHER_YOU_HAVE_NOT_REGISTERED_YOUR_AIRSHIP_LICENSE_OR_THE_AIRSHIP_HAS_NOT_YET_BEEN_SUMMONED);
                return;
            }

            ClanAirShip dockedAirShip = getDockedAirShip();
            ClanAirShip clanAirship = player.getClan().getAirship();

            if (clanAirship != null) {
                if (clanAirship == dockedAirShip)
                    player.sendPacket(SystemMsg.THE_CLAN_OWNED_AIRSHIP_ALREADY_EXISTS);
                else
                    player.sendPacket(SystemMsg.YOUR_CLANS_AIRSHIP_IS_ALREADY_BEING_USED_BY_ANOTHER_CLAN_MEMBER);
                return;
            }

            if (dockedAirShip != null) {
                Functions.npcSay(this, NpcString.IN_AIR_HARBOR_ALREADY_AIRSHIP_DOCKED_PLEASE_WAIT_AND_TRY_AGAIN, ChatType.SHOUT, 5000);
                return;
            }

            if (removeItem(player, ENERGY_STAR_STONE, 5, "Clan Airship") != 5) {
                player.sendPacket(new SystemMessage2(SystemMsg.AN_AIRSHIP_CANNOT_BE_SUMMONED_BECAUSE_YOU_DONT_HAVE_ENOUGH_S1).addItemName(ENERGY_STAR_STONE));
                return;
            }

            ClanAirShip dockedShip = new ClanAirShip(player.getClan());
            dockedShip.setDock(airshipDock);
            dockedShip.setPlatform(_platform);

            dockedShip.setHeading(0);
            dockedShip.spawnMe(_platform.getSpawnLoc());
            dockedShip.startDepartTask();

            Functions.npcSay(this, NpcString.AIRSHIP_IS_SUMMONED_IS_DEPART_IN_5_MINUTES, ChatType.SHOUT, 5000);
        } else if ("register".equalsIgnoreCase(command)) {
            if (player.getClan() == null || !player.isClanLeader() || player.getClan().getLevel() < 5) {
                player.sendPacket(SystemMsg.IN_ORDER_TO_ACQUIRE_AN_AIRSHIP_THE_CLANS_LEVEL_MUST_BE_LEVEL_5_OR_HIGHER);
                return;
            }

            if (player.getClan().isHaveAirshipLicense()) {
                player.sendPacket(SystemMsg.THE_AIRSHIP_SUMMON_LICENSE_HAS_ALREADY_BEEN_ACQUIRED);
                return;
            }

            if (!player.haveItem(AIRSHIP_SUMMON_LICENSE)) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
                return;
            }

            removeItem(player, AIRSHIP_SUMMON_LICENSE, 1, "Clan Airship");
            player.getClan().setAirshipLicense(true);
            player.getClan().setAirshipFuel(ClanAirShip.MAX_FUEL);
            player.getClan().updateClanInDB();
            player.sendPacket(SystemMsg.THE_AIRSHIP_SUMMON_LICENSE_HAS_BEEN_ENTERED);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    protected ClanAirShip getDockedAirShip() {
        ClanAirShip ship = _dockedShipRef;
        if (ship != null && ship.isDocked())
            return ship;
        else
            return null;
    }

    public void setDockedShip(ClanAirShip dockedShip) {
        if (_dockedShipRef != null) {
            _dockedShipRef.setDock(null);
            _dockedShipRef.setPlatform(null);
        }

        if (dockedShip != null) {
            boolean alreadyEnter = dockedShip.getDock() != null;
            dockedShip.setDock(airshipDock);
            dockedShip.setPlatform(_platform);
            if (!alreadyEnter)
                dockedShip.startArrivalTask();
        }

        _dockedShipRef = dockedShip;
    }
}
