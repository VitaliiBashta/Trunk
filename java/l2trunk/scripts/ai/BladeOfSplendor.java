package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public final class BladeOfSplendor extends RndTeleportFighter {
    private static final Logger LOG = LoggerFactory.getLogger(BladeOfSplendor.class);
    private static final int[] CLONES = {21525};

    private boolean _firstTimeAttacked = true;

    private BladeOfSplendor(NpcInstance actor) {
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
            for (int bro : CLONES)
                try {
                    MonsterInstance npc = (MonsterInstance) NpcHolder.getTemplate(bro).getNewInstance();
                    npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
                    npc.setReflection(actor.getReflection());
                    npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
                    npc.spawnMe(npc.getSpawnedLoc());
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 1000));
                } catch (RuntimeException e) {
                    LOG.error("Error while creating BladeOfSplendor", e);
                }
        }
        super.onEvtAttacked(attacker, damage);
    }

    protected void onEvtDead(Player killer) {
        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }
}