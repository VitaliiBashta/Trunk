/*	*/
package l2trunk.gameserver.model.actor.recorder;
/*	*/
/*	*/

import l2trunk.gameserver.model.Summon;

/*	*/
/*	*/ public class SummonStatsChangeRecorder extends CharStatsChangeRecorder<Summon>
        /*	*/ {
    /*	*/
    public SummonStatsChangeRecorder(Summon actor)
    /*	*/ {
        /*  9 */
        super(actor);
        /*	*/
    }

    /*	*/
    /*	*/
    protected void onSendChanges()
    /*	*/ {
        /* 15 */
        super.onSendChanges();
        /*	*/
        /* 17 */
        if ((this._changes & 0x2) == 2)
            /* 18 */ ((Summon) this.activeChar).sendPetInfo();
            /* 19 */
        else if ((this._changes & 0x1) == 1)
            /* 20 */ ((Summon) this.activeChar).broadcastCharInfo();
        /*	*/
    }
    /*	*/
}