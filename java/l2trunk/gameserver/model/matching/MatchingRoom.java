package l2trunk.gameserver.model.matching;

import l2trunk.gameserver.instancemanager.MatchingRoomManager;
import l2trunk.gameserver.listener.actor.player.OnPlayerPartyInviteListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerPartyLeaveListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.PlayerGroup;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class MatchingRoom implements PlayerGroup {
    public static final int PARTY_MATCHING = 0;
    public static final int CC_MATCHING = 1;
    //
    static final int WAIT_PLAYER = 0;
    static final int ROOM_MASTER = 1;
    static final int PARTY_MEMBER = 2;
    static final int UNION_LEADER = 3;
    static final int UNION_PARTY = 4;
    static final int WAIT_PARTY = 5;
    static final int WAIT_NORMAL = 6;
    final Player _leader;
    private final int _id;
    private final PartyListenerImpl _listener = new PartyListenerImpl();
    private final List<Player> members = new CopyOnWriteArrayList<>();
    private int _minLevel;
    private int _maxLevel;
    private int _maxMemberSize;
    private int _lootType;
    private String _topic;

    MatchingRoom(Player leader, int minLevel, int maxLevel, int maxMemberSize, int lootType, String topic) {
        _leader = leader;
        _id = MatchingRoomManager.INSTANCE.addMatchingRoom(this);
        _minLevel = minLevel;
        _maxLevel = maxLevel;
        _maxMemberSize = maxMemberSize;
        _lootType = lootType;
        _topic = topic;

        addMember0(leader, null);
    }

    public boolean addMember(Player player) {
        if (members.contains(player))
            return true;

        if (player.getLevel() < getMinLevel() || player.getLevel() > getMaxLevel() || getPlayers().size() >= getMaxMembersSize()) {
            player.sendPacket(notValidMessage());
            return false;
        }

        return addMember0(player, new SystemMessage2(enterMessage()).addName(player));
    }

    private boolean addMember0(Player player, L2GameServerPacket p) {
        if (!members.isEmpty())
            player.addListener(_listener);

        members.add(player);

        player.setMatchingRoom(this);

        for (Player $member : this)
            if ($member != player)
                $member.sendPacket(p, addMemberPacket($member, player));

        MatchingRoomManager.INSTANCE.removeFromWaitingList(player);
        player.sendPacket(infoRoomPacket(), membersPacket(player));
        player.sendChanges();
        return true;
    }

    public void removeMember(Player member, boolean oust) {
        if (!members.remove(member))
            return;

        member.removeListener(_listener);
        member.setMatchingRoom(null);
        if (members.isEmpty())
            disband();
        else {
            L2GameServerPacket infoPacket = infoRoomPacket();
            SystemMsg exitMessage0 = exitMessage(true, oust);
            L2GameServerPacket exitMessage = exitMessage0 != null ? new SystemMessage2(exitMessage0).addName(member) : null;
            for (Player player : this)
                player.sendPacket(infoPacket, removeMemberPacket(player, member), exitMessage);
        }

        member.sendPacket(closeRoomPacket(), exitMessage(false, oust));
        MatchingRoomManager.INSTANCE.addToWaitingList(member);
        member.sendChanges();
    }

    public void broadcastPlayerUpdate(Player player) {
        for (Player $member : MatchingRoom.this)
            $member.sendPacket(updateMemberPacket($member, player));
    }

    public void disband() {
        for (Player player : this) {
            player.removeListener(_listener);
            player.sendPacket(closeRoomMessage());
            player.sendPacket(closeRoomPacket());
            player.setMatchingRoom(null);
            player.sendChanges();

            MatchingRoomManager.INSTANCE.addToWaitingList(player);
        }

        members.clear();

        MatchingRoomManager.INSTANCE.removeMatchingRoom(this);
    }

    //===============================================================================================================================================
    //                                                            Abstracts
    //===============================================================================================================================================
    protected abstract SystemMsg notValidMessage();

    protected abstract SystemMsg enterMessage();

    protected abstract SystemMsg exitMessage(boolean toOthers, boolean kick);

    protected abstract SystemMsg closeRoomMessage();

    protected abstract L2GameServerPacket closeRoomPacket();

    public abstract L2GameServerPacket infoRoomPacket();

    protected abstract L2GameServerPacket addMemberPacket(Player $member, Player active);

    protected abstract L2GameServerPacket removeMemberPacket(Player $member, Player active);

    protected abstract L2GameServerPacket updateMemberPacket(Player $member, Player active);

    protected abstract L2GameServerPacket membersPacket(Player active);

    public abstract int getType();

    public abstract int getMemberType(Player member);

    //===============================================================================================================================================
    //                                                            Broadcast
    //===============================================================================================================================================
    @Override
    public void sendPacket(IStaticPacket... arg) {
        for (Player player : this)
            player.sendPacket(arg);
    }

    @Override
    public void sendMessage(String message) {
        for (Player member : members)
            member.sendMessage(message);
    }

    @Override
    public void sendChatMessage(int objectId, int messageType, String charName, String text) {
        for (Player member : members)
            member.sendChatMessage(objectId, messageType, charName, text);
    }

    //===============================================================================================================================================
    //                                                            Getters
    //===============================================================================================================================================
    public int getId() {
        return _id;
    }

    public int getMinLevel() {
        return _minLevel;
    }

    //===============================================================================================================================================
    //                                                            Setters
    //===============================================================================================================================================
    public void setMinLevel(int minLevel) {
        _minLevel = minLevel;
    }

    public int getMaxLevel() {
        return _maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        _maxLevel = maxLevel;
    }

    public String getTopic() {
        return _topic;
    }

    public void setTopic(String topic) {
        _topic = topic;
    }

    public int getMaxMembersSize() {
        return _maxMemberSize;
    }

    public int getLocationId() {
        return MatchingRoomManager.INSTANCE.getLocation(_leader);
    }

    public Collection<Player> getPlayers() {
        return members;
    }

    public int getLootType() {
        return _lootType;
    }

    public void setLootType(int lootType) {
        _lootType = lootType;
    }

    @Override
    public Iterator<Player> iterator() {
        return members.iterator();
    }

    @Override
    public int size() {
        return members.size();
    }

    @Override
    public Player getLeader() {
        return _leader;
    }

    @Override
    public boolean isLeader(Player player) {
        return _leader == player;
    }

    @Override
    public List<Player> getMembers() {
        return members;
    }

    @Override
    public boolean containsMember(Player player) {
        return members.contains(player);
    }

    public void setMaxMemberSize(int maxMemberSize) {
        _maxMemberSize = maxMemberSize;
    }

    private class PartyListenerImpl implements OnPlayerPartyInviteListener, OnPlayerPartyLeaveListener {
        @Override
        public void onPartyInvite(Player player) {
            broadcastPlayerUpdate(player);
        }

        @Override
        public void onPartyLeave(Player player) {
            broadcastPlayerUpdate(player);
        }
    }
}