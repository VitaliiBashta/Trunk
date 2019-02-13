package l2trunk.scripts.npc.model.birthday;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.concurrent.Future;

public final class BirthDayCakeInstance extends NpcInstance {
    private static final int SKILL = 22035;
    private Future<?> _castTask;

    public BirthDayCakeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setTargetable(false);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        _castTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(() ->
                World.getAroundPlayers(this, 500, 100)
                .filter(player -> player.getEffectList().getEffectsBySkillId(SKILL) == null)
                .forEach(player -> SkillTable.INSTANCE.getInfo(SKILL).getEffects(this, player)), 1000L, 1000L);
    }

    @Override
    public void onDespawn() {
        super.onDespawn();

        _castTask.cancel(false);
        _castTask = null;
    }
}
