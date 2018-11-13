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
        for (Player p : AntharasManager.getZone().getInsidePlayers())
            notifyEvent(CtrlEvent.EVT_AGGRESSION, p, 5000);
    }

    @Override
    public void onEvtDead(Creature killer) {
        getActor().doCast(SkillTable.getInstance().getInfo(5097, 1), getActor(), true);
        super.onEvtDead(killer);
    }
}