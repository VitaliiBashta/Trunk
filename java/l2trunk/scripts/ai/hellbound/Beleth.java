package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Mystic;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.scripts.bosses.BelethManager;

public final class Beleth extends Mystic {
    private static final int CLONE = 29119;
    private long _lastFactionNotifyTime = 0;

    public Beleth(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        BelethManager.getInstance().setBelethDead();
        super.onEvtDead(killer);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();

        if (System.currentTimeMillis() - _lastFactionNotifyTime > _minFactionNotifyInterval) {
            _lastFactionNotifyTime = System.currentTimeMillis();

            World.getAroundNpc(actor).stream()
                    .filter(npc -> npc.getNpcId() == CLONE)
                    .forEach(npc -> npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100)));
        }

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public boolean randomAnimation() {
        return false;
    }

    @Override
    public boolean canSeeInSilentMove(Playable target) {
        return true;
    }

    @Override
    public boolean canSeeInHide(Playable target) {
        return true;
    }

    @Override
    public void addTaskAttack(Creature target) {
        return;
    }

}