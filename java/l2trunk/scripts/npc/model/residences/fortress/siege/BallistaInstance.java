package l2trunk.scripts.npc.model.residences.fortress.siege;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class BallistaInstance extends NpcInstance {
    public BallistaInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);

        if (killer instanceof Player) {
            Player player = (Player)killer;
            if (player.getClan() != null) {
                player.getClan().incReputation(30, false, "Ballista " + getTitle());
                player.sendPacket(new SystemMessage2(SystemMsg.THE_BALLISTA_HAS_BEEN_SUCCESSFULLY_DESTROYED));
            }

        }

    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return true;
    }

    @Override
    public void showChatWindow(Player player, int val) {
    }

    @Override
    public boolean isInvul() {
        return false;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }
}