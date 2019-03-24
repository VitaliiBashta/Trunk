package l2trunk.gameserver.model;

import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface PlayerGroup extends Iterable<Player> {

    default int size(){
        return getMembers().size();
    }

    default Player getLeader(){
        return getMembers().get(0);
    };

    default Stream<Player> getMembersStream() {
        return getMembers().stream();
    }

    List<Player> getMembers();

//    boolean containsMember(Player player);

    default void sendPacket(IStaticPacket... packets) {
        getMembersStream().forEach(p -> p.sendPacket(packets));
    }

    default void sendPacket(Player exclude, IStaticPacket... packets) {
        getMembersStream().filter(p -> p != exclude).forEach(p -> p.sendPacket(packets));
    }

    default void sendMessage(CustomMessage string) {
        getMembersStream().forEach(p -> p.sendMessage(string));
    }

    default void sendChatMessage(int objectId, int messageType, String charName, String text) {
        getMembersStream().forEach(p -> p.sendChatMessage(objectId, messageType, charName, text));
    }

    default boolean isLeader(Player player) {
        if (getLeader() == null)
            return false;

        return getLeader() == player;
    }

    default List<Player> getMembersInRange(GameObject obj, int range) {
        return getMembersStream().filter(member -> member.isInRangeZ(obj, range)).collect(Collectors.toList());
    }

    default int getMemberCountInRange(GameObject obj, int range) {
        return (int) getMembersStream().filter(member -> member.isInRangeZ(obj, range)).count();
    }

    default List<Integer> getMembersObjIds() {
        return getMembersStream().map(Player::objectId).collect(Collectors.toList());
    }

    default Player getPlayerByName(String name) {
        if (name == null)
            return null;

        return getMembersStream().filter(member -> name.equalsIgnoreCase(member.getName())).findAny().orElse(null);
    }

}
