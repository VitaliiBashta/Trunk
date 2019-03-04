package l2trunk.scripts.npc.model.residences;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.List;

public abstract class DoormanInstance extends NpcInstance {
    protected static final int COND_OWNER = 0;
    protected static final int COND_SIEGE = 1;
    protected static final int COND_FAIL = 2;

    protected String _siegeDialog;
    protected String _mainDialog;
    protected String _failDialog;

    protected final List<Integer> doors;

    protected DoormanInstance(int objectId, NpcTemplate template) {
        super(objectId, template);

        setDialogs();

        doors = template.getAiParams().getIntegerList("doors");
    }

    protected void setDialogs() {
        _siegeDialog = getTemplate().getAiParams().getString("siege_dialog");
        _mainDialog = getTemplate().getAiParams().getString("main_dialog");
        _failDialog = getTemplate().getAiParams().getString("fail_dialog");
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;
        int cond = getCond(player);
        switch (cond) {
            case COND_OWNER:
                if (command.equalsIgnoreCase("openDoors"))
                    for (int i : doors)
                        ReflectionUtils.getDoor(i).openMe();
                else if (command.equalsIgnoreCase("closeDoors"))
                    for (int i : doors)
                        ReflectionUtils.getDoor(i).closeMe();
                break;
            case COND_SIEGE:
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
                filename = _mainDialog;
                break;
            case COND_SIEGE:
                filename = _siegeDialog;
                break;
            case COND_FAIL:
                filename = _failDialog;
                break;
        }
        player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
    }

    protected int getCond(Player player) {
        Residence residence = getResidence();
        Clan residenceOwner = residence.getOwner();
        if (residenceOwner != null && player.getClan() == residenceOwner && (player.getClanPrivileges() & getOpenPriv()) == getOpenPriv()) {
            if (residence.getSiegeEvent().isInProgress())
                return COND_SIEGE;
            else
                return COND_OWNER;
        } else
            return COND_FAIL;
    }

    protected abstract int getOpenPriv();

    protected abstract Residence getResidence();
}
