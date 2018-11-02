package l2f.gameserver.model.entity.events.objects;

import l2f.gameserver.model.GameObjectsStorage;
import l2f.gameserver.model.Player;
import l2f.gameserver.model.entity.events.impl.SiegeEvent;
import l2f.gameserver.model.pledge.Clan;

import java.util.HashSet;
import java.util.Set;

public class CMGSiegeClanObject extends SiegeClanObject {
    private Set<Integer> players = new HashSet<>();
    private long _param;

    public CMGSiegeClanObject(String type, Clan clan, long param, long date) {
        super(type, clan, param, date);
        _param = param;
    }

    public CMGSiegeClanObject(String type, Clan clan, long param) {
        super(type, clan, param);
        _param = param;
    }

    public void addPlayer(int objectId) {
        players.add(objectId);
    }

    @Override
    public long getParam() {
        return _param;
    }

    public void setParam(long param) {
        _param = param;
    }

    @Override
    public boolean isParticle(Player player) {
        return players.contains(player.getObjectId());
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
