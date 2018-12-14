package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.AgitDecoInfo;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.npc.model.residences.ResidenceManager;

public class ManagerInstance extends ResidenceManager {
    public ManagerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected Residence getResidence() {
        return getClanHall();
    }

    @Override
    public L2GameServerPacket decoPacket() {
        ClanHall clanHall = getClanHall();
        if (clanHall != null)
            return new AgitDecoInfo(clanHall);
        else
            return null;
    }

    @Override
    protected int getPrivUseFunctions() {
        return Clan.CP_CH_USE_FUNCTIONS;
    }

    @Override
    protected int getPrivSetFunctions() {
        return Clan.CP_CH_SET_FUNCTIONS;
    }

    @Override
    protected int getPrivDismiss() {
        return Clan.CP_CH_DISMISS;
    }

    @Override
    protected int getPrivDoors() {
        return Clan.CP_CH_ENTRY_EXIT;
    }
}