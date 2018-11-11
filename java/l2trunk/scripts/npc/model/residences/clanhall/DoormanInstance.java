package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 13:19/31.03.2011
 */
public class DoormanInstance extends l2trunk.scripts.npc.model.residences.DoormanInstance {
    public DoormanInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public int getOpenPriv() {
        return Clan.CP_CH_ENTRY_EXIT;
    }

    @Override
    public Residence getResidence() {
        return getClanHall();
    }
}
