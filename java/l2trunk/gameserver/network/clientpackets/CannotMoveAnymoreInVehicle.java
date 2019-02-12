/*	*/
package l2trunk.gameserver.network.clientpackets;
/*	*/
/*	*/

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.boat.Boat;
import l2trunk.gameserver.network.GameClient;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.utils.Location;

/*	*/
/*	*/
/*	*/
/*	*/

/*	*/
/*	*/ public class CannotMoveAnymoreInVehicle extends L2GameClientPacket
        /*	*/ {
    /*	*/   private final Location _loc;
    /*	*/   private int _boatid;

    /*	*/
    /*	*/
    public CannotMoveAnymoreInVehicle()
    /*	*/ {
        /* 10 */
        this._loc = new Location();
        /*	*/
    }

    /*	*/
    /*	*/
    protected void readImpl()
    /*	*/ {
        /* 16 */
        this._boatid = readD();
        /* 17 */
        this._loc.x = readD();
        /* 18 */
        this._loc.y = readD();
        /* 19 */
        this._loc.z = readD();
        /* 20 */
        this._loc.h = readD();
        /*	*/
    }

    /*	*/
    /*	*/
    protected void runImpl()
    /*	*/ {
        /* 26 */
        Player player = ((GameClient) getClient()).getActiveChar();
        /* 27 */
        if (player == null) {
            /* 28 */
            return;
            /*	*/
        }
        /* 30 */
        Boat boat = player.getBoat();
        /* 31 */
        if ((boat == null) || (boat.objectId() != this._boatid))
            /*	*/ return;
        /* 33 */
        player.setInBoatPosition(this._loc);
        /* 34 */
        player.setHeading(this._loc.h);
        /* 35 */
        player.broadcastPacket(new L2GameServerPacket[]{boat.inStopMovePacket(player)});
        /*	*/
    }
    /*	*/
}