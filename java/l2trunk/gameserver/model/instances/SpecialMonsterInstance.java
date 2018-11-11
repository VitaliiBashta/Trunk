package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.templates.npc.NpcTemplate;

public abstract class SpecialMonsterInstance extends MonsterInstance {
    protected SpecialMonsterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean canChampion() {
        return false;
    }

    @Override
    public long getRegenTick() {
        return 0L;
    }
}