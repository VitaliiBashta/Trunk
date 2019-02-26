package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.pledge.Clan;

import java.util.HashSet;
import java.util.Set;

public final class CMGSiegeClanObject extends SiegeClanObject {
    private final Set<Integer> players = new HashSet<>();
    private long param;

    public CMGSiegeClanObject(String type, Clan clan, long param, long date) {
        super(type, clan, param, date);
        this.param = param;
    }

    public CMGSiegeClanObject(String type, Clan clan, long param) {
        super(type, clan, param);
        this.param = param;
    }

    public void addPlayer(int objectId) {
        players.add(objectId);
    }

    @Override
    public long getParam() {
        return param;
    }

    public void setParam(long param) {
        this.param = param;
    }

    @Override
    public boolean isParticle(Player player) {
        return players.contains(player.objectId());
    }

    @Override
    public void setEvent(boolean start, SiegeEvent event) {
        for (int i : players) {
            Player player = GameObjectsStorage.getPlayer(i);
            if (player != null) {
                if (start)
                    player.addEvent(event);
                else
                    player.removeEvent(event);
                player.broadcastCharInfo();
            }
        }
    }

    public Set<Integer> getPlayers() {
        return players;
    }
}
