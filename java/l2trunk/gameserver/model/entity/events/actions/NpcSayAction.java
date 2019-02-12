package l2trunk.gameserver.model.entity.events.actions;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.events.EventAction;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcSay;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.utils.MapUtils;

public final class NpcSayAction implements EventAction {
    private final int _npcId;
    private final int _range;
    private final ChatType _chatType;
    private final NpcString _text;

    public NpcSayAction(int npcId, int range, ChatType type, NpcString string) {
        _npcId = npcId;
        _range = range;
        _chatType = type;
        _text = string;
    }

    @Override
    public void call(GlobalEvent event) {
        NpcInstance npc = GameObjectsStorage.getByNpcId(_npcId);
        if (npc == null)
            return;

        if (_range <= 0) {
            int rx = MapUtils.regionX(npc);
            int ry = MapUtils.regionY(npc);
            int offset = Config.SHOUT_OFFSET;

            GameObjectsStorage.getAllPlayersStream()
                    .filter(player -> npc.getReflection() == player.getReflection())
                    .forEach(player -> {
                        int tx = MapUtils.regionX(player);
                        int ty = MapUtils.regionY(player);

                        if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset)
                            packet(npc, player);
                    });
        } else World.getAroundPlayers(npc, _range, Math.max(_range / 2, 200))
                .forEach(player -> packet(npc, player));
    }

    private void packet(NpcInstance npc, Player player) {
        player.sendPacket(new NpcSay(npc, _chatType, _text,""));
    }
}
