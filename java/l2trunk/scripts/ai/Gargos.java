package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;

public final class Gargos extends Fighter {
    private long _lastFire;

    public Gargos(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return super.thinkActive() || thinkFire();
    }

    private boolean thinkFire() {
        if (System.currentTimeMillis() - _lastFire > 60000L) {
            NpcInstance actor = getActor();
            Functions.npcSayCustomMessage(actor, "scripts.ai.Gargos.fire");
            actor.doCast(SkillTable.INSTANCE.getInfo(5705), actor, false);
            _lastFire = System.currentTimeMillis();
            return true;
        }

        return false;
    }
}