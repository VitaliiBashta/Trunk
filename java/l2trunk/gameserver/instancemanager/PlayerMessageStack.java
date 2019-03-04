package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PlayerMessageStack {
    private static PlayerMessageStack instance;

    private final Map<Integer, List<L2GameServerPacket>> stack = new HashMap<>();

    private PlayerMessageStack() {
        //TODO: загрузка из БД
    }

    public static PlayerMessageStack getInstance() {
        if (instance == null)
            instance = new PlayerMessageStack();
        return instance;
    }

    public void mailto(int char_obj_id, L2GameServerPacket message) {
        Player cha = GameObjectsStorage.getPlayer(char_obj_id);
        if (cha != null) {
            cha.sendPacket(message);
            return;
        }

        synchronized (stack) {
            List<L2GameServerPacket> messages;
            if (stack.containsKey(char_obj_id))
                messages = stack.remove(char_obj_id);
            else
                messages = new ArrayList<>();
            messages.add(message);
            //TODO: сохранение в БД
            stack.put(char_obj_id, messages);
        }
    }

    public void checkMessages(Player cha) {
        List<L2GameServerPacket> messages;
        synchronized (stack) {
            if (!stack.containsKey(cha.objectId()))
                return;
            messages = stack.remove(cha.objectId());
        }
        if (messages == null || messages.size() == 0)
            return;
        //TODO: удаление из БД
        messages.forEach(cha::sendPacket);
    }
}