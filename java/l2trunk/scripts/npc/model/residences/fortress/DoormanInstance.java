package l2trunk.scripts.npc.model.residences.fortress;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class DoormanInstance extends l2trunk.scripts.npc.model.residences.DoormanInstance {
    private Location loc;

    public DoormanInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        String loc = template.getAiParams().getString("tele_loc");
        if (loc != null)
            this.loc = Location.of(loc);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;
        int cond = getCond(player);
        switch (cond) {
            case COND_OWNER:
                if ("openDoors".equalsIgnoreCase(command))
                    doors.forEach(d -> ReflectionUtils.getDoor(d).openMe());
                else if ("closeDoors".equalsIgnoreCase(command))
                    doors.forEach(d -> ReflectionUtils.getDoor(d).closeMe());
                break;
            case COND_SIEGE:
                if (command.equalsIgnoreCase("tele"))
                    player.teleToLocation(loc);
                break;
            case COND_FAIL:
                player.sendPacket(new NpcHtmlMessage(player, this, _failDialog, 0));
                break;
        }
    }

    @Override
    public void setDialogs() {
        _mainDialog = "residence2/fortress/fortress_doorkeeper001.htm";
        _failDialog = "residence2/fortress/fortress_doorkeeper002.htm";
        _siegeDialog = "residence2/fortress/fortress_doorkeeper003.htm";
    }

    @Override
    public int getOpenPriv() {
        return Clan.CP_CS_ENTRY_EXIT;
    }

    @Override
    public Residence getResidence() {
        return getFortress();
    }
}
