package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class Glacier extends Fighter {
    private final int skill = 6301;
    public Glacier(NpcInstance actor) {
        super(actor);
        actor.setBlock(true);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        getActor().setNpcState(1);
        ThreadPoolManager.INSTANCE.schedule(() -> getActor().setNpcState(2), 800);
        ThreadPoolManager.INSTANCE.schedule(() -> getActor().deleteMe(), 30000L);
    }

    @Override
    public void onEvtDead(Creature killer) {
        getActor().getAroundCharacters(350, 100)
                .filter(GameObject::isPlayer)
                .forEach(cha -> cha.altOnMagicUseTimer(cha, skill));

        super.onEvtDead(killer);
    }
}