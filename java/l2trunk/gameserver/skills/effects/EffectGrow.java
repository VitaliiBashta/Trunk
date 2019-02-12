package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.stats.Env;

public final class EffectGrow extends Effect {
    public EffectGrow(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (effected instanceof NpcInstance) {
            NpcInstance npc = (NpcInstance) effected;
            npc.setCollisionHeight(npc.getCollisionHeight() * 1.24);
            npc.setCollisionRadius(npc.getCollisionRadius() * 1.19);
        }
    }

    @Override
    public void onExit() {
        super.onExit();
        if (effected instanceof NpcInstance) {
            NpcInstance npc = (NpcInstance) effected;
            npc.setCollisionHeight(npc.getTemplate().collisionHeight);
            npc.setCollisionRadius(npc.getTemplate().collisionRadius);
        }
    }

}