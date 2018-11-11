package l2trunk.gameserver.handler.chat;

import l2trunk.gameserver.network.serverpackets.components.ChatType;

public interface IChatHandler {
    void say();

    ChatType getType();
}
