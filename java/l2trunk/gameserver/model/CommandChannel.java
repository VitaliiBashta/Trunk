package l2trunk.gameserver.model;

import l2trunk.commons.collections.JoinedIterator;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.matching.MatchingRoom;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;


public final class CommandChannel implements PlayerGroup {
    public static final int STRATEGY_GUIDE_ID = 8871;
    public static final int CLAN_IMPERIUM_ID = 391;

    private final List<Party> commandChannelParties = new CopyOnWriteArrayList<>();
    private Player commandChannelLeader;
    private int commandChannelLvl;
    private Reflection reflection;

    private MatchingRoom matchingRoom;

    /**
     * Creates a New Command Channel and Add the Leaders party to the CC
     */
    public CommandChannel(Player leader) {
        commandChannelLeader = leader;
        commandChannelParties.add(leader.getParty());
        commandChannelLvl = leader.getParty().getLevel();
        leader.getParty().setCommandChannel(this);
        sendPacket(ExMPCCOpen.STATIC);
    }

    /**
     * Проверяет возможность создания командного канала
     */
    public static boolean checkAuthority(Player creator) {
        // CC могут создавать только лидеры партий, состоящие в клане ранком не ниже барона
        if (creator.getClan() == null || !creator.isInParty() || !creator.getParty().isLeader(creator) || creator.getPledgeClass() < Player.RANK_BARON) {
            creator.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
            return false;
        }

        // CC можно создать, если есть клановый скилл Clan Imperium
        boolean haveSkill = creator.getSkillLevel(CLAN_IMPERIUM_ID) > 0;

        // Ищем Strategy Guide в инвентаре
        boolean haveItem = creator.getInventory().getItemByItemId(STRATEGY_GUIDE_ID) != null;

        if (!haveSkill && !haveItem) {
            creator.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_THE_AUTHORITY_TO_USE_THE_COMMAND_CHANNEL);
            return false;
        }

        return true;
    }

    /**
     * Adds a Party to the Command Channel
     */
    public void addParty(Party party) {
        sendPacket(new ExMPCCPartyInfoUpdate(party, 1));
        commandChannelParties.add(party);
        refreshLevel();
        party.setCommandChannel(this);

        for (Player $member : party) {
            $member.sendPacket(ExMPCCOpen.STATIC);
            if (matchingRoom != null)
                matchingRoom.broadcastPlayerUpdate($member);
        }
    }

    /**
     * Removes a Party from the Command Channel
     */
    public void removeParty(Party party) {
        commandChannelParties.remove(party);
        refreshLevel();
        party.setCommandChannel(null);
        party.sendPacket(ExMPCCClose.STATIC);
        if (reflection != null)
            party.forEach(player -> player.teleToLocation(reflection.getReturnLoc(), 0));

        if (commandChannelParties.size() < 2)
            disbandChannel();
        else {
            for (Player $member : party) {
                $member.sendPacket(new ExMPCCPartyInfoUpdate(party, 0));
                if (matchingRoom != null)
                    matchingRoom.broadcastPlayerUpdate($member);
            }
        }
    }

    /**
     * Распускает Command Channel
     */
    public void disbandChannel() {
        sendPacket(new SystemMessage2(SystemMsg.THE_COMMAND_CHANNEL_HAS_BEEN_DISBANDED));
        for (Party party : commandChannelParties) {
            party.setCommandChannel(null);
            party.sendPacket(ExMPCCClose.STATIC);
            if (isInReflection())
                party.sendPacket(new SystemMessage2(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(1));
        }
        Reflection reflection = getReflection();
        if (reflection != null) {
            reflection.startCollapseTimer(60000L);
            setReflection(null);
        }

        if (matchingRoom != null)
            matchingRoom.disband();
        commandChannelParties.clear();
        commandChannelLeader = null;
    }

    @Override
    public void sendPacket(IStaticPacket... gsp) {
        commandChannelParties.forEach(party -> party.sendPacket(gsp));
    }

    @Override
    public void sendMessage(String message) {
        commandChannelParties.forEach(party -> party.sendMessage(message));
    }

    @Override
    public void sendChatMessage(int objectId, int messageType, String charName, String text) {
        commandChannelParties.forEach(party -> party.sendChatMessage(objectId, messageType, charName, text));
    }

    public void broadcastToChannelPartyLeaders(L2GameServerPacket gsp) {
        commandChannelParties.forEach(party -> party.getLeader().sendPacket(gsp));
    }


    public List<Party> getParties() {
        return commandChannelParties;
    }

    @Override
    public Iterator<Player> iterator() {
        List<Iterator<Player>> iterators = new ArrayList<>(commandChannelParties.size());
        for (Party p : commandChannelParties)
            iterators.add(p.getMembers().iterator());
        return new JoinedIterator<>(iterators);
    }

    @Override
    public int size() {
        return commandChannelParties.stream()
                .mapToInt(Party::size).sum();
    }

    @Override
    public Player getLeader() {
        return commandChannelLeader;
    }

    @Override
    public List<Player> getMembers() {
        return commandChannelParties.stream()
                .flatMap(party -> party.getMembers().stream())
                .collect(Collectors.toList());
    }

    @Override
    public boolean containsMember(Player player) {
        return commandChannelParties.stream().anyMatch(party -> party.containsMember(player));
    }

    @Override
    public int getLevel() {
        return commandChannelLvl;
    }

    /**
     * @param newLeader the leader of the Command Channel
     */
    void setChannelLeader(Player newLeader) {
        commandChannelLeader = newLeader;
        sendPacket(new SystemMessage2(SystemMsg.COMMAND_CHANNEL_AUTHORITY_HAS_BEEN_TRANSFERRED_TO_C1).addString(newLeader.getName()));
    }

    private void refreshLevel() {
        commandChannelLvl = commandChannelParties.stream()
                .mapToInt(Party::getLevel)
                .max().orElse(0);

    }

    boolean isInReflection() {
        return reflection != null;
    }

    public Reflection getReflection() {
        return reflection;
    }

    @Override
    public CommandChannel setReflection(Reflection reflection) {
        this.reflection = reflection;
        return this;
    }

    public MatchingRoom getMatchingRoom() {
        return matchingRoom;
    }

    public void setMatchingRoom(MatchingRoom matchingRoom) {
        this.matchingRoom = matchingRoom;
    }
}