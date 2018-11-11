package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

public class MinionInstance extends MonsterInstance {
    private MonsterInstance _master;

    public MinionInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    public MonsterInstance getLeader() {
        return _master;
    }

    public void setLeader(MonsterInstance leader) {
        _master = leader;
    }

    private boolean isRaidFighter() {
        return getLeader() != null && getLeader().isRaid();
    }

    @Override
    protected void onDeath(Creature killer) {
        if (getLeader() != null)
            getLeader().notifyMinionDied(this);

        super.onDeath(killer);
    }

    @Override
    protected void onDecay() {
        decayMe();

        _spawnAnimation = 2;
    }

    @Override
    public boolean isFearImmune() {
        return isRaidFighter();
    }

    @Override
    public Location getSpawnedLoc() {
        return getLeader() != null ? getLeader().getLoc() : getLoc();
    }

    @Override
    public boolean canChampion() {
        return false;
    }

    @Override
    public boolean isMinion() {
        return true;
    }
}