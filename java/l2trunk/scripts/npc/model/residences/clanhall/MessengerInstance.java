package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.ClanHallSiegeEvent;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.CastleSiegeInfo;
import l2trunk.gameserver.templates.npc.NpcTemplate;

/**
 * @author VISTALL
 * @date 15:54/07.05.2011
 * 35420
 */
public class MessengerInstance extends NpcInstance {
    private final String _siegeDialog;
    private final String _ownerDialog;

    public MessengerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);

        _siegeDialog = template.getAiParams().getString("siege_dialog");
        _ownerDialog = template.getAiParams().getString("owner_dialog");
    }

    @Override
    public void showChatWindow(Player player, int val) {
        ClanHall clanHall = getClanHall();
        ClanHallSiegeEvent siegeEvent = clanHall.getSiegeEvent();
        if (clanHall.getOwner() != null && clanHall.getOwner() == player.getClan())
            showChatWindow(player, _ownerDialog);
        else if (siegeEvent.isInProgress())
            showChatWindow(player, _siegeDialog);
        else
            player.sendPacket(new CastleSiegeInfo(clanHall, player));
    }
}
