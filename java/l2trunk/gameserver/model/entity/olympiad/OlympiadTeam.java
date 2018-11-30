package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExOlympiadUserInfo;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.templates.StatsSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OlympiadTeam {
    private final OlympiadGame _game;
    private final Map<Integer, TeamMember> _members;
    private final int _side;
    private String _name = "";
    private double _damage;

    public OlympiadTeam(OlympiadGame game, int side) {
        _game = game;
        _side = side;
        _members = new ConcurrentHashMap<>();
    }

    public void addMember(int obj_id) {
        String player_name = "";
        Player player = GameObjectsStorage.getPlayer(obj_id);
        if (player != null)
            player_name = player.getName();
        else {
            StatsSet noble = Olympiad._nobles.get(obj_id);
            if (noble != null)
                player_name = noble.getString(Olympiad.CHAR_NAME, "");
        }

        _members.put(obj_id, new TeamMember(obj_id, player_name, player, _game, _side));

        _name = player_name;
    }

    public void addDamage(Player player, double damage) {
        _damage += damage;

        TeamMember member = _members.get(player.getObjectId());
        member.addDamage(damage);
    }

    public double getDamage() {
        return _damage;
    }

    public String getName() {
        return _name;
    }

    public void portPlayersToArena() {
        for (TeamMember member : _members.values())
            member.portPlayerToArena();
    }

    public void portPlayersBack() {
        for (TeamMember member : _members.values())
            member.portPlayerBack();
    }

    public void heal() {
        for (TeamMember member : _members.values())
            member.heal();
    }

    public void removeBuffs(boolean fromSummon) {
        for (TeamMember member : _members.values())
            member.removeBuffs(fromSummon);
    }

    public void preparePlayers() {
        for (TeamMember member : _members.values())
            member.preparePlayer();

        if (_members.size() <= 1)
            return;

        List<Player> list = new ArrayList<>();
        for (TeamMember member : _members.values()) {
            Player player = member.getPlayer();
            if (player != null) {
                list.add(player);
                player.leaveParty();
            }
        }

        if (list.size() <= 1)
            return;

        Player leader = list.get(0);
        if (leader == null)
            return;

        Party party = new Party(leader, 0);
        leader.setParty(party);

        for (Player player : list)
            if (player != leader)
                player.joinParty(party);
    }

    public void startComp() {
        for (TeamMember member : _members.values())
            member.startComp();
    }

    public void stopComp() {
        for (TeamMember member : _members.values())
            member.stopComp();
    }

    public void takePointsForCrash() {
        for (TeamMember member : _members.values())
            member.takePointsForCrash();
    }

    public boolean checkPlayers() {
        for (TeamMember member : _members.values())
            if (member.checkPlayer())
                return true;
        return false;
    }

    private boolean isAllDead() {
        for (TeamMember member : _members.values())
            if (!member.isDead() && member.checkPlayer())
                return false;
        return true;
    }

    public boolean contains(int objId) {
        return _members.containsKey(objId);
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(_members.size());
        for (TeamMember member : _members.values()) {
            Player player = member.getPlayer();
            if (player != null)
                players.add(player);
        }
        return players;
    }

    public Collection<TeamMember> getMembers() {
        return _members.values();
    }

    public void broadcast(L2GameServerPacket p) {
        for (TeamMember member : _members.values()) {
            Player player = member.getPlayer();
            if (player != null)
                player.sendPacket(p);
        }
    }

    public void broadcast(IStaticPacket p) {
        for (TeamMember member : _members.values()) {
            Player player = member.getPlayer();
            if (player != null)
                player.sendPacket(p);
        }
    }

    public void broadcastInfo() {
        for (TeamMember member : _members.values()) {
            Player player = member.getPlayer();
            if (player != null)
                player.broadcastPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()));
        }
    }

    public boolean logout(Player player) {
        if (player != null) {
            for (TeamMember member : _members.values()) {
                Player pl = member.getPlayer();
                if (pl != null && pl == player)
                    member.logout();
            }
        }
        return checkPlayers();
    }

    public boolean doDie(Player player) {
        if (player != null)
            for (TeamMember member : _members.values()) {
                Player pl = member.getPlayer();
                if (pl != null && pl == player)
                    member.doDie();
            }
        return isAllDead();
    }

    public void saveNobleData() {
        for (TeamMember member : _members.values())
            member.saveNobleData();
    }
}