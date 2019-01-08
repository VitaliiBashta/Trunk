package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

public final class BladeOfSplendor extends RndTeleportFighter {
    private static final int CLONE = 21525;

    private boolean _firstTimeAttacked = true;

    public BladeOfSplendor(NpcInstance actor) {
        super(actor);
        this.AI_TASK_ATTACK_DELAY = 1000;
        this.AI_TASK_ACTIVE_DELAY = 100000;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor == null)
            return;
        if ((!actor.isDead()) && (this._firstTimeAttacked)) {
            this._firstTimeAttacked = false;
            Functions.npcSay(actor, "Now I Know Why You Wanna Hate Me");
            MonsterInstance npc = (MonsterInstance) NpcHolder.getTemplate(CLONE).getNewInstance();
            npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
            npc.setReflection(actor.getReflection());
            npc.setFullHpMp();
            npc.spawnMe(npc.getSpawnedLoc());
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 1000));
        }
        super.onEvtAttacked(attacker, damage);
    }

    protected void onEvtDead(Player killer) {
        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }
}