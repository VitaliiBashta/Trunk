package l2trunk.gameserver.ai;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public abstract class AbstractAI extends RunnableImpl {
    protected static final Logger _log = LoggerFactory.getLogger(AbstractAI.class);

    protected final Creature actor;
    private Creature attackTarget = null;

    private CtrlIntention intention = CtrlIntention.AI_INTENTION_IDLE;

    AbstractAI(Creature actor) {
        this.actor = actor;
    }

    public void runImpl() {
    }

    public void changeIntention(CtrlIntention intention) {
        this.intention = intention;
        if (intention != CtrlIntention.AI_INTENTION_CAST && intention != CtrlIntention.AI_INTENTION_ATTACK)
            setAttackTarget(null);
    }

    public final void setIntention(CtrlIntention intention, ItemInstance gameObject) {
        setAttackTarget(null);
        Creature actor = getActor();
        if (!actor.isVisible()) {
            if (this.intention == CtrlIntention.AI_INTENTION_IDLE)
                return;
            intention = CtrlIntention.AI_INTENTION_IDLE;
        }
        actor.getListeners().onAiIntention(intention, gameObject, null);
        if (intention == CtrlIntention.AI_INTENTION_PICK_UP) {
            onIntentionPickUp(gameObject);
        }
    }

    public final void setIntentionAttack(Creature gameObject) {
        Creature actor = getActor();

        if (!actor.isVisible()) {
            if (this.intention == CtrlIntention.AI_INTENTION_IDLE)
                return;
        }

        actor.getListeners().onAiIntention(CtrlIntention.AI_INTENTION_ATTACK, gameObject, null);

        onIntentionAttack(gameObject);

    }

    public final void setIntentionInteract(CtrlIntention intention, GameObject gameObject) {
        if (intention != CtrlIntention.AI_INTENTION_ATTACK)
            setAttackTarget(null);

        Creature actor = getActor();

        if (!actor.isVisible()) {
            if (this.intention == CtrlIntention.AI_INTENTION_IDLE)
                return;
            intention = CtrlIntention.AI_INTENTION_IDLE;
        }

        actor.getListeners().onAiIntention(intention, gameObject, null);

        if (intention == CtrlIntention.AI_INTENTION_ATTACK) {
            onIntentionAttack((Creature) gameObject);
        } else if (intention == CtrlIntention.AI_INTENTION_INTERACT) {
            onIntentionInteract(gameObject);
        }
    }

    public final void setIntention(CtrlIntention intention, Creature follower, Integer arg1) {
        setAttackTarget(null);
        Creature actor = getActor();
        if (!actor.isVisible()) {
            if (this.intention == CtrlIntention.AI_INTENTION_IDLE)
                return;
            intention = CtrlIntention.AI_INTENTION_IDLE;
        }
        actor.getListeners().onAiIntention(intention, follower, arg1);
        onIntentionFollow(follower, arg1);
    }

    public final void setIntention(CtrlIntention intention, Player player, Integer offset) {
        setAttackTarget(null);

        Creature actor = getActor();

        if (!actor.isVisible()) {
            if (this.intention == CtrlIntention.AI_INTENTION_IDLE)
                return;
            intention = CtrlIntention.AI_INTENTION_IDLE;
        }

        actor.getListeners().onAiIntention(intention, player, offset);

        if (intention == CtrlIntention.AI_INTENTION_FOLLOW) {
            onIntentionFollow(player, offset);
        } else if (intention == CtrlIntention.AI_INTENTION_COUPLE_ACTION) {
            onIntentionCoupleAction(player, offset);
        }
    }

    public void setIntention(CtrlIntention intention, Object arg0, Object arg1) {
        Creature actor = getActor();

        if (!actor.isVisible()) {
            if (this.intention == CtrlIntention.AI_INTENTION_IDLE)
                return;
            intention = CtrlIntention.AI_INTENTION_IDLE;
        }

        actor.getListeners().onAiIntention(intention, arg0, arg1);

        if (intention == CtrlIntention.AI_INTENTION_CAST) {
            onIntentionCast((Skill) arg0, (Creature) arg1);
        }
    }

    public final void notifyEvent(CtrlEvent evt) {
        Creature actor = getActor();
        if (actor == null || !actor.isVisible())
            return;

        switch (evt) {
            case EVT_THINK:
                onEvtThink();
                break;
            case EVT_READY_TO_ACT:
                onEvtReadyToAct();
                break;
            case EVT_ARRIVED:
                onEvtArrived();
                break;
            case EVT_ARRIVED_TARGET:
                onEvtArrivedTarget();
                break;
            case EVT_FAKE_DEATH:
                onEvtFakeDeath();
                break;
            case EVT_FINISH_CASTING:
                onEvtFinishCasting();
                break;
            case EVT_SPAWN:
                onEvtSpawn();
                break;
            case EVT_DESPAWN:
                onEvtDeSpawn();
                break;
        }
    }


    public final void notifyEvent(CtrlEvent evt, Object arg0) {
        Creature actor = getActor();
        if (actor == null || !actor.isVisible())
            return;

//        actor.getListeners().onAiEvent(evt, List.of(arg0));
        switch (evt) {
            case EVT_ARRIVED_BLOCKED:
                onEvtArrivedBlocked((Location) arg0);
                break;
            case EVT_FORGET_OBJECT:
                onEvtForgetObject((GameObject) arg0);
                break;
            case EVT_DEAD:
                onEvtDead((Creature) arg0);
                break;
            case EVT_TIMER:
                onEvtTimer(((Number) arg0).intValue());
                break;
        }
    }

    public final void notifySeeSpell(Skill skill, Creature creature) {
        Creature actor = getActor();
        if (actor == null || !actor.isVisible())
            return;
        onEvtSeeSpell(skill, creature);
    }

    public final void notifyEvent(CtrlEvent evt, Creature creature, int dmg) {
        Creature actor = getActor();
        if (actor == null || !actor.isVisible())
            return;
        if (creature != null)
            actor.getListeners().onAiEvent(evt, List.of(creature, dmg));

        if (evt == CtrlEvent.EVT_ATTACKED) {
            onEvtAttacked(creature, dmg);
        } else if (evt == CtrlEvent.EVT_AGGRESSION) {
            onEvtAggression(creature, dmg);
        }
    }

    void notifyEventClanAttack(NpcInstance act, Creature attacker, int damage) {
        Creature actor = getActor();
        if (actor == null || !actor.isVisible())
            return;

        actor.getListeners().onAiEvent(CtrlEvent.EVT_CLAN_ATTACKED, List.of(act, attacker, damage));

        onEvtClanAttacked(act, attacker, damage);
    }

    protected void clientActionFailed() {
        Creature actor = getActor();
        if (actor instanceof Player)
            actor.sendActionFailed();
    }

    /**
     * Останавливает движение и рассылает ValidateLocation
     */
    protected void clientStopMoving() {
        Creature actor = getActor();
        actor.stopMove();
    }

    public Creature getActor() {
        return actor;
    }

    public CtrlIntention getIntention() {
        return intention;
    }

    public final void setIntention(CtrlIntention intention) {
        setAttackTarget(null);
        Creature actor = getActor();
        if (!actor.isVisible()) {
            if (this.intention == CtrlIntention.AI_INTENTION_IDLE)
                return;
            intention = CtrlIntention.AI_INTENTION_IDLE;
        }
        actor.getListeners().onAiIntention(intention, null, null);
        switch (intention) {
            case AI_INTENTION_IDLE:
                onIntentionIdle();
                break;
            case AI_INTENTION_ACTIVE:
                onIntentionActive();
                break;
            case AI_INTENTION_REST:
                onIntentionRest();
                break;
        }
    }

    public Creature getAttackTarget() {
        return attackTarget;
    }

    public void setAttackTarget(Creature target) {
        attackTarget = target;
    }

    public boolean isGlobalAI() {
        return false;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " for " + getActor();
    }

    public abstract void onIntentionIdle();

    public abstract void onIntentionActive();

    public abstract void onIntentionRest();

    public abstract void onIntentionAttack(Creature target);

    public abstract void onIntentionCast(Skill skill, Creature target);

    public abstract void onIntentionPickUp(GameObject item);

    public abstract void onIntentionInteract(GameObject object);

    public abstract void onIntentionCoupleAction(Player player, Integer socialId);

    public abstract void onEvtThink();

    public abstract void onEvtAttacked(Creature attacker, int damage);

    public abstract void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage);

    public abstract void onEvtAggression(Creature target, int aggro);

    public abstract void onEvtReadyToAct();

    public abstract void onEvtArrived();

    public abstract void onEvtArrivedTarget();

    public abstract void onEvtArrivedBlocked(Location blocked_at_pos);

    public abstract void onEvtForgetObject(GameObject object);

    public abstract void onEvtDead(Creature killer);

    public abstract void onEvtFakeDeath();

    public abstract void onEvtFinishCasting();

    public abstract void onEvtSeeSpell(Skill skill, Creature caster);

    public abstract void onEvtSpawn();

    public abstract void onEvtDeSpawn();

    public abstract void onIntentionFollow(Creature target, Integer offset);

    public abstract void onEvtTimer(int timerId);
}