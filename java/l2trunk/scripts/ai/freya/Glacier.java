package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

public final class Glacier extends Fighter {
    private final Skill skill = SkillTable.getInstance().getInfo(6301, 1);
    public Glacier(NpcInstance actor) {
        super(actor);
        actor.block();
    }

    @Override
    protected void onEvtSpawn() {
        super.onEvtSpawn();
        getActor().setNpcState(1);
        ThreadPoolManager.getInstance().schedule(() -> getActor().setNpcState(2), 800);
        ThreadPoolManager.getInstance().schedule(() -> getActor().deleteMe(), 30000L);
    }

    @Override
    protected void onEvtDead(Creature killer) {
        getActor().getAroundCharacters(350, 100).stream()
                .filter(GameObject::isPlayer)
                .forEach(cha -> cha.altOnMagicUseTimer(cha, skill));

        super.onEvtDead(killer);
    }
}