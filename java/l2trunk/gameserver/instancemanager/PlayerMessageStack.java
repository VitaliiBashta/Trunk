package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerMessageStack {
    private static PlayerMessageStack _instance;

    private final Map<Integer, List<L2GameServerPacket>> _stack = new HashMap<>();

    private PlayerMessageStack() {
        //TODO: загрузка из БД
    }

    public static PlayerMessageStack getInstance() {
        if (_instance == null)
            _instance = new PlayerMessageStack();
        return _instance;
    }

    public void mailto(int char_obj_id, L2GameServerPacket message) {
        Player cha = GameObjectsStorage.getPlayer(char_obj_id);
        if (cha != null) {
            cha.sendPacket(message);
            return;
        }

        synchronized (_stack) {
            List<L2GameServerPacket> messages;
            if (_stack.containsKey(char_obj_id))
                messages = _stack.remove(char_obj_id);
            else
                messages = new ArrayList<>();
            messages.add(message);
            //TODO: сохранение в БД
            _stack.put(char_obj_id, messages);
        }
    }

    public void CheckMessages(Player cha) {
        List<L2GameServerPacket> messages;
        synchronized (_stack) {
            if (!_stack.containsKey(cha.objectId()))
                return;
            messages = _stack.remove(cha.objectId());
        }
        if (messages == null || messages.size() == 0)
            return;
        //TODO: удаление из БД
        for (L2GameServerPacket message : messages)
            cha.sendPacket(message);
    }
}