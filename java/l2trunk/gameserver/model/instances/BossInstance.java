package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.HeroDiary;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public class BossInstance extends RaidBossInstance {
    private boolean teleportedToNest;

    public BossInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isBoss() {
        return true;
    }

    @Override
    public final boolean isMovementDisabled() {
        // Core should stay anyway
        return getNpcId() == 29006 || super.isMovementDisabled();
    }

    @Override
    protected void onDeath(Creature killer) {
        if (killer instanceof Playable) {
            Player player = ((Playable)killer).getPlayer();
            if (player.isInParty()) {
                player.getParty().getMembersStream()
                        .filter(Player::isNoble)
                        .forEach(member -> Hero.INSTANCE.addHeroDiary(member.objectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId()));
            } else if (player.isNoble())
                Hero.INSTANCE.addHeroDiary(player.objectId(), HeroDiary.ACTION_RAID_KILLED, getNpcId());
        }
        super.onDeath(killer);
    }

    public boolean isTeleported() {
        return teleportedToNest;
    }

    public void setTeleported(boolean flag) {
        teleportedToNest = flag;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }
}
