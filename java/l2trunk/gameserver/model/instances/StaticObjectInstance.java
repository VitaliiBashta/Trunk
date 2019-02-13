package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.scripts.Events;
import l2trunk.gameserver.templates.StaticObjectTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class StaticObjectInstance extends GameObject {
    private final StaticObjectTemplate template;
    private int meshIndex;

    public StaticObjectInstance(int objectId, StaticObjectTemplate template) {
        super(objectId);

        this.template = template;
    }

    public int getUId() {
        return template.uid;
    }

    public int getType() {
        return template.type;
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (Events.onAction(player, this, shift))
            return;

        if (player.getTarget() != this) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(objectId(), 0));
            return;
        }

        MyTargetSelected my = new MyTargetSelected(objectId(), 0);
        player.sendPacket(my);

        if (!isInRange(player, 150)) {
            if (player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
                player.getAI().setIntentionInteract(CtrlIntention.AI_INTENTION_INTERACT, this);
            return;
        }

        if (template.type == 0) // Arena Board
            player.sendPacket(new NpcHtmlMessage(player, getUId(), "newspaper/arena.htm", 0));
        else if (template.type == 2) { // Village map
            player.sendPacket(new ShowTownMap(template.filePath, template.mapX, template.mapY));
            player.sendActionFailed();
        }
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        return List.of(new StaticObject(this));
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return false;
    }

    public void broadcastInfo() {
        StaticObject p = new StaticObject(this);
        World.getAroundPlayers(this)
                .forEach(pl -> pl.sendPacket(p));
    }

    @Override
    public int getGeoZ(Location loc) {  //FIXME [VISTALL] нужно ли?
        return loc.z;
    }

    public int getMeshIndex() {
        return meshIndex;
    }

    public void setMeshIndex(int meshIndex) {
        this.meshIndex = meshIndex;
    }
}