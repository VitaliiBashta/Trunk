package l2trunk.gameserver.model;

import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface PlayerGroup extends Iterable<Player> {

    int size();

    Player getLeader();

    List<Player> getMembers();

    boolean containsMember(Player player);

    default void sendPacket(IStaticPacket... packets) {
        stream().forEach(p -> p.sendPacket(packets));
    }

    default void sendPacket(Player exclude, IStaticPacket... packets) {
        stream().filter(p -> p != exclude).forEach(p -> p.sendPacket(packets));
    }

//    default void sendMessage(String message) {
//        stream().forEach(p -> p.sendMessage(message));
//    }

    default void sendMessage(CustomMessage string) {
        stream().forEach(p -> p.sendMessage(string));
    }

    default void sendMessage(Predicate<Player> condition, String message) {
        stream().filter(condition).forEach(p -> p.sendMessage(message));
    }

    default void sendChatMessage(int objectId, int messageType, String charName, String text) {
        stream().forEach(p -> p.sendChatMessage(objectId, messageType, charName, text));
    }

    private Stream<Player> stream() {
        return getMembers().stream();
    }

    default boolean isLeader(Player player) {
        if (getLeader() == null)
            return false;

        return getLeader() == player;
    }

    default List<Player> getMembersInRange(GameObject obj, int range) {
        return stream().filter(member -> member.isInRangeZ(obj, range)).collect(Collectors.toList());
    }

    default int getMemberCountInRange(GameObject obj, int range) {
        return (int) stream().filter(member -> member.isInRangeZ(obj, range)).count();
    }

    default List<Integer> getMembersObjIds() {
        return getMembers().stream().map(Player::objectId).collect(Collectors.toList());
    }

    default Player getPlayerByName(String name) {
        if (name == null)
            return null;

        return stream().filter(member -> name.equalsIgnoreCase(member.getName())).findAny().orElse(null);
    }

}
