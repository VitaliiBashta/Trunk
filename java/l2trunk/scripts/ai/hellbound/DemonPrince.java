package l2trunk.scripts.ai.hellbound;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

public final class DemonPrince extends Fighter {
    private final Skill ULTIMATE_DEFENSE_SKILL = SkillTable.INSTANCE().getInfo(5044, 3);
    private static final int TELEPORTATION_CUBIC_ID = 32375;
    private static final Location CUBIC_POSITION = new Location(-22144, 278744, -8239, 0);
    private boolean _notUsedUltimateDefense = true;

    public DemonPrince(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        if (_notUsedUltimateDefense && actor.getCurrentHpPercents() < 10) {
            _notUsedUltimateDefense = false;

            // FIXME Скилл использует, но эффект скила не накладывается.
            clearTasks();
            addTaskBuff(actor, ULTIMATE_DEFENSE_SKILL);
        }

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        _notUsedUltimateDefense = true;

        actor.getReflection().setReenterTime(System.currentTimeMillis());
        actor.getReflection().addSpawnWithoutRespawn(TELEPORTATION_CUBIC_ID, CUBIC_POSITION, 0);

        super.onEvtDead(killer);
    }
}