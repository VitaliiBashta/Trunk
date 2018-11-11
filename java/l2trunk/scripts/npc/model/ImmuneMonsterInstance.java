package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public class ImmuneMonsterInstance extends MonsterInstance {
    public ImmuneMonsterInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }
}