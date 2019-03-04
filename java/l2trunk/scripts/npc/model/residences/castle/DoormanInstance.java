package l2trunk.scripts.npc.model.residences.castle;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

public final class DoormanInstance extends l2trunk.scripts.npc.model.residences.DoormanInstance {
    private final Location[] locs = new Location[2];

    public DoormanInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        for (int i = 0; i < locs.length; i++) {
            String loc = template.getAiParams().getString("tele_loc" + i);
            if (loc != null)
                locs[i] = Location.of(loc);
        }
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
                else if (command.startsWith("tele")) {
                    int id = Integer.parseInt(command.substring(4, 5));
                    Location loc = locs[id];
                    if (loc != null)
                        player.teleToLocation(loc);
                }
                break;
            case COND_SIEGE:
                if (command.startsWith("tele")) {
                    int id = Integer.parseInt(command.substring(4, 5));
                    Location loc = locs[id];
                    if (loc != null)
                        player.teleToLocation(loc);
                } else
                    player.sendPacket(new NpcHtmlMessage(player, this, _siegeDialog, 0));
                break;
            case COND_FAIL:
                player.sendPacket(new NpcHtmlMessage(player, this, _failDialog, 0));
                break;
        }
    }

    @Override
    public void showChatWindow(Player player, int val) {
        String filename = null;
        int cond = getCond(player);
        switch (cond) {
            case COND_OWNER:
            case COND_SIEGE:
                filename = _mainDialog;
                break;
            case COND_FAIL:
                filename = _failDialog;
                break;
        }
        player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
    }

    @Override
    protected int getCond(Player player) {
        Castle residence = getCastle();
        Clan residenceOwner = residence.getOwner();
        if (residenceOwner != null && player.getClan() == residenceOwner && (player.getClanPrivileges() & getOpenPriv()) == getOpenPriv()) {
            if (residence.getSiegeEvent().isInProgress() || residence.getDominion().getSiegeEvent().isInProgress())
                return COND_SIEGE;
            else
                return COND_OWNER;
        } else
            return COND_FAIL;
    }

    @Override
    public int getOpenPriv() {
        return Clan.CP_CS_ENTRY_EXIT;
    }

    @Override
    public Residence getResidence() {
        return getCastle();
    }
}
