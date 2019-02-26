package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ExOlympiadUserInfo;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class OlympiadTeam {
    private final OlympiadGame game;
    private final Map<Integer, TeamMember> members;
    private final int side;
    private String _name = "";
    private double _damage;

    public OlympiadTeam(OlympiadGame game, int side) {
        this.game = game;
        this.side = side;
        members = new ConcurrentHashMap<>();
    }

    public void addMember(int obj_id) {
        String player_name = "";
        Player player = GameObjectsStorage.getPlayer(obj_id);
        if (player != null)
            player_name = player.getName();
        else {
            StatsSet noble = Olympiad.nobles.get(obj_id);
            if (noble != null)
                player_name = noble.getString(Olympiad.CHAR_NAME, "");
        }

        members.put(obj_id, new TeamMember(obj_id, player_name, player, game, side));

        _name = player_name;
    }

    public void addDamage(Player player, double damage) {
        _damage += damage;

        TeamMember member = members.get(player.objectId());
        member.addDamage(damage);
    }

    public double getDamage() {
        return _damage;
    }

    public String getName() {
        return _name;
    }

    public void portPlayersToArena() {
        for (TeamMember member : members.values())
            member.portPlayerToArena();
    }

    public void portPlayersBack() {
        for (TeamMember member : members.values())
            member.portPlayerBack();
    }

    public void heal() {
        for (TeamMember member : members.values())
            member.heal();
    }

    public void removeBuffs(boolean fromSummon) {
        for (TeamMember member : members.values())
            member.removeBuffs(fromSummon);
    }

    public void preparePlayers() {
        for (TeamMember member : members.values())
            member.preparePlayer();

        if (members.size() <= 1)
            return;

        List<Player> list = new ArrayList<>();
        for (TeamMember member : members.values()) {
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
        for (TeamMember member : members.values())
            member.startComp();
    }

    public void stopComp() {
        for (TeamMember member : members.values())
            member.stopComp();
    }

    public void takePointsForCrash() {
        for (TeamMember member : members.values())
            member.takePointsForCrash();
    }

    public boolean checkPlayers() {
        for (TeamMember member : members.values())
            if (member.checkPlayer())
                return true;
        return false;
    }

    private boolean isAllDead() {
        for (TeamMember member : members.values())
            if (!member.isDead() && member.checkPlayer())
                return false;
        return true;
    }

    public boolean contains(int objId) {
        return members.containsKey(objId);
    }

    public Stream<Player> getPlayers() {
        return members.values().stream()
                .filter(Objects::nonNull)
                .map(TeamMember::getPlayer);
    }

    public Collection<TeamMember> getMembers() {
        return members.values();
    }

    public void broadcast(L2GameServerPacket p) {
        members.values().forEach(member -> member.getPlayer().sendPacket(p));
    }

    public void broadcast(IStaticPacket p) {
        for (TeamMember member : members.values()) {
            Player player = member.getPlayer();
            if (player != null)
                player.sendPacket(p);
        }
    }

    public void broadcastInfo() {
        for (TeamMember member : members.values()) {
            Player player = member.getPlayer();
            if (player != null)
                player.broadcastPacket(new ExOlympiadUserInfo(player, player.getOlympiadSide()));
        }
    }

    public boolean logout(Player player) {
        if (player != null) {
            for (TeamMember member : members.values()) {
                Player pl = member.getPlayer();
                if (pl != null && pl == player)
                    member.logout();
            }
        }
        return checkPlayers();
    }

    public boolean doDie(Player player) {
        if (player != null)
            for (TeamMember member : members.values()) {
                Player pl = member.getPlayer();
                if (pl != null && pl == player)
                    member.doDie();
            }
        return isAllDead();
    }

    void saveNobleData() {
        members.values().forEach(TeamMember::saveNobleData);
    }
}