package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.dao.SiegePlayerDAO;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.pledge.Clan;

import java.util.ArrayList;
import java.util.List;

public class CTBSiegeClanObject extends SiegeClanObject {
    private final List<Integer> _players = new ArrayList<>();
    private long _npcId;

    public CTBSiegeClanObject(String type, Clan clan, long param, long date) {
        super(type, clan, param, date);
        _npcId = param;
    }

    public CTBSiegeClanObject(String type, Clan clan, long param) {
        this(type, clan, param, System.currentTimeMillis());
    }

    public void select(Residence r) {
        _players.addAll(SiegePlayerDAO.INSTANCE.select(r, getObjectId()));
    }

    public List<Integer> getPlayers() {
        return _players;
    }

    @Override
    public void setEvent(boolean start, SiegeEvent event) {
        for (int i : getPlayers()) {
            Player player = GameObjectsStorage.getPlayer(i);
            if (start)
                player.addEvent(event);
            else
                player.removeEvent(event);
            player.broadcastCharInfo();
        }
    }

    @Override
    public boolean isParticle(Player player) {
        return _players.contains(player.objectId());
    }

    @Override
    public long getParam() {
        return _npcId;
    }

    public void setParam(int npcId) {
        _npcId = npcId;
    }
}
