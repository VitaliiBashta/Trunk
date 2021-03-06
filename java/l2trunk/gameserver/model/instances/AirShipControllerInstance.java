package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.boat.AirShip;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

public class AirShipControllerInstance extends NpcInstance {
    AirShipControllerInstance(int objectID, NpcTemplate template) {
        super(objectID, template);
    }

    private static SystemMsg canBoard(Player player) {
        if (player.isTrasformed() )
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_TRANSFORMED;
        if (player.isParalyzed())
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_PETRIFIED;
        if (player.isDead() || player.isFakeDeath())
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_DEAD;
        if (player.isFishing())
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_FISHING;
        if (player.isInCombat())
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_BATTLE;
        if (player.isInDuel())
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_IN_A_DUEL;
        if (player.isSitting())
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_SITTING;
        if (player.isCastingNow())
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_CASTING;
        if (player.isCursedWeaponEquipped())
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHEN_A_CURSED_WEAPON_IS_EQUIPPED;
        if (player.getActiveWeaponFlagAttachment() != null)
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_HOLDING_A_FLAG;
        if (player.getPet() != null || player.isMounted())
            return SystemMsg.YOU_CANNOT_BOARD_AN_AIRSHIP_WHILE_A_PET_OR_A_SERVITOR_IS_SUMMONED;

        return null;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("board".equalsIgnoreCase(command)) {
            SystemMsg msg = canBoard(player);
            if (msg != null) {
                player.sendPacket(msg);
                return;
            }

            Boat boat = getDockedAirShip();
            if (boat == null) {
                player.sendActionFailed();
                return;
            }

            if (player.getBoat() != null && player.getBoat().objectId() != boat.objectId()) {
                player.sendPacket(SystemMsg.YOU_HAVE_ALREADY_BOARDED_ANOTHER_AIRSHIP);
                return;
            }

            player.stablePoint = player.getLoc().setH(0);
            boat.addPlayer(player, new Location());
        } else
            super.onBypassFeedback(player, command);
    }

    Boat getDockedAirShip() {
        return World.getAroundCharacters(this, 1000, 500)
                .filter(creature -> creature instanceof AirShip)
                .map(cha -> (Boat) cha)
                .filter(Boat::isDocked)
                .findFirst().orElse(null);
    }
}