package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;

import java.io.Serializable;
import java.util.Comparator;

public class SiegeClanObject implements Serializable {
    private final long _date;
    private final Clan _clan;
    private String _type;
    private NpcInstance _flag;

    public SiegeClanObject(String type, Clan clan, long param) {
        this(type, clan, 0, System.currentTimeMillis());
    }

    public SiegeClanObject(String type, Clan clan, long param, long date) {
        _type = type;
        _clan = clan;
        _date = date;
    }

    public int getObjectId() {
        return _clan.clanId();
    }

    public Clan getClan() {
        return _clan;
    }

    public NpcInstance getFlag() {
        return _flag;
    }

    public void setFlag(NpcInstance npc) {
        _flag = npc;
    }

    public void deleteFlag() {
        if (_flag != null) {
            _flag.deleteMe();
            _flag = null;
        }
    }

    public String getType() {
        return _type;
    }

    public void setType(String type) {
        _type = type;
    }

    public void broadcast(IStaticPacket... packet) {
        getClan().broadcastToOnlineMembers(packet);
    }

    public void broadcast(L2GameServerPacket... packet) {
        getClan().broadcastToOnlineMembers(packet);
    }

    public void setEvent(boolean start, SiegeEvent event) {
        if (start) {
            for (Player player : _clan.getOnlineMembers()) {
                player.addEvent(event);
                player.broadcastCharInfo();
            }
        } else {
            for (Player player : _clan.getOnlineMembers()) {
                player.removeEvent(event);
                player.getEffectList().stopEffect(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                player.broadcastCharInfo();
            }
        }
    }

    public boolean isParticle(Player player) {
        return true;
    }

    public long getParam() {
        return 0;
    }

    public long getDate() {
        return _date;
    }

    public static class SiegeClanComparatorImpl implements Comparator<SiegeClanObject>, Serializable {
        private static final SiegeClanComparatorImpl _instance = new SiegeClanComparatorImpl();

        public static SiegeClanComparatorImpl getInstance() {
            return _instance;
        }

        @Override
        public int compare(SiegeClanObject o1, SiegeClanObject o2) {
            return Long.compare(o2.getParam(), o1.getParam());
        }
    }
}
