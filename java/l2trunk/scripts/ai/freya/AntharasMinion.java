package l2trunk.scripts.ai.freya;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.scripts.bosses.AntharasManager;

public final class AntharasMinion extends Fighter {
    public AntharasMinion(NpcInstance actor) {
        super(actor);
        actor.startDebuffImmunity();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        AntharasManager.getZone().getInsidePlayers().forEach(p ->
            notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000));
    }

    @Override
    public void onEvtDead(Creature killer) {
        getActor().doCast(5097, getActor(), true);
        super.onEvtDead(killer);
    }
}