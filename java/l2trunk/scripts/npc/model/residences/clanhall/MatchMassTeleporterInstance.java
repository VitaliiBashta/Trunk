package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.events.impl.ClanHallTeamBattleEvent;
import l2trunk.gameserver.model.entity.events.objects.CTBTeamObject;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class MatchMassTeleporterInstance extends NpcInstance {
    private final int _flagId;
    private long _timeout;

    public MatchMassTeleporterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        _flagId = template.getAiParams().getInteger("flag");
    }

    @Override
    public void showChatWindow(Player player, int val) {
        ClanHall clanHall = getClanHall();
        ClanHallTeamBattleEvent siegeEvent = clanHall.getSiegeEvent();

        if (_timeout > System.currentTimeMillis()) {
            showChatWindow(player, "residence2/clanhall/agit_mass_teleporter001.htm");
            return;
        }

        if (isInRange(player, INTERACTION_DISTANCE)) {
            _timeout = System.currentTimeMillis() + 60000L;

            List<CTBTeamObject> locs = siegeEvent.getObjects(ClanHallTeamBattleEvent.TRYOUT_PART);

            CTBTeamObject object = locs.get(_flagId);
            if (object.getFlag() != null) {
                World.getAroundPlayers(this, 400, 100)
                        .forEach(p ->
                                p.teleToLocation(Location.findPointToStay(object.getFlag(), 100, 125)));
            }
        }
    }
}
