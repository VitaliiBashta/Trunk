package l2trunk.gameserver.ai;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.lang.reference.HardReferences;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractAI extends RunnableImpl {
    protected static final Logger _log = LoggerFactory.getLogger(AbstractAI.class);

    protected final Creature actor;
    private HardReference<? extends Creature> _attackTarget = HardReferences.emptyRef();

    private CtrlIntention _intention = CtrlIntention.AI_INTENTION_IDLE;

    AbstractAI(Creature actor) {
        this.actor = actor;
    }

    public void runImpl() {

    }

    public void changeIntention(CtrlIntention intention, Object arg0, Object arg1) {
        _intention = intention;
        if (intention != CtrlIntention.AI_INTENTION_CAST && intention != CtrlIntention.AI_INTENTION_ATTACK)
            setAttackTarget(null);
    }

    public final void setIntention(CtrlIntention intention, Object arg0) {
        setIntention(intention, arg0, null);
    }

    public void setIntention(CtrlIntention intention, Object arg0, Object arg1) {
        if (intention != CtrlIntention.AI_INTENTION_CAST && intention != CtrlIntention.AI_INTENTION_ATTACK)
            setAttackTarget(null);

        Creature actor = getActor();

        if (!actor.isVisible()) {
            if (_intention == CtrlIntention.AI_INTENTION_IDLE)
                return;

            intention = CtrlIntention.AI_INTENTION_IDLE;
        }

        actor.getListeners().onAiIntention(intention, arg0, arg1);

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
            case AI_INTENTION_ATTACK:
                onIntentionAttack((Creature) arg0);
                break;
            case AI_INTENTION_CAST:
                onIntentionCast((Skill) arg0, (Creature) arg1);
                break;
            case AI_INTENTION_PICK_UP:
                onIntentionPickUp((GameObject) arg0);
                break;
            case AI_INTENTION_INTERACT:
                onIntentionInteract((GameObject) arg0);
                break;
            case AI_INTENTION_FOLLOW:
                onIntentionFollow((Creature) arg0, (Integer) arg1);
                break;
            case AI_INTENTION_COUPLE_ACTION:
                onIntentionCoupleAction((Player) arg0, (Integer) arg1);
                break;
        }
    }

    public final void notifyEvent(CtrlEvent evt) {
        notifyEvent(evt, new Object[]{});
    }

    public final void notifyEvent(CtrlEvent evt, Object arg0) {
        notifyEvent(evt, new Object[]{arg0});
    }

    public final void notifyEvent(CtrlEvent evt, Object arg0, Object arg1) {
        notifyEvent(evt, new Object[]{arg0, arg1});
    }

    final void notifyEvent(CtrlEvent evt, Object arg0, Object arg1, Object arg2) {
        notifyEvent(evt, new Object[]{arg0, arg1, arg2});
    }

    @SuppressWarnings("incomplete-switch")
    public void notifyEvent(CtrlEvent evt, Object[] args) {
        Creature actor = getActor();
        if (actor == null || !actor.isVisible())
            return;

        actor.getListeners().onAiEvent(evt, args);

        switch (evt) {
            case EVT_THINK:
                onEvtThink();
                break;
            case EVT_ATTACKED:
                onEvtAttacked((Creature) args[0], ((Number) args[1]).intValue());
                break;
            case EVT_CLAN_ATTACKED:
                onEvtClanAttacked((Creature) args[0], (Creature) args[1], ((Number) args[2]).intValue());
                break;
            case EVT_AGGRESSION:
                onEvtAggression((Creature) args[0], ((Number) args[1]).intValue());
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
            case EVT_ARRIVED_BLOCKED:
                onEvtArrivedBlocked((Location) args[0]);
                break;
            case EVT_FORGET_OBJECT:
                onEvtForgetObject((GameObject) args[0]);
                break;
            case EVT_DEAD:
                onEvtDead((Creature) args[0]);
                break;
            case EVT_FAKE_DEATH:
                onEvtFakeDeath();
                break;
            case EVT_FINISH_CASTING:
                onEvtFinishCasting();
                break;
            case EVT_SEE_SPELL:
                onEvtSeeSpell((Skill) args[0], (Creature) args[1]);
                break;
            case EVT_SPAWN:
                onEvtSpawn();
                break;
            case EVT_DESPAWN:
                onEvtDeSpawn();
                break;
            case EVT_TIMER:
                onEvtTimer(((Number) args[0]).intValue(), args[1], args[2]);
                break;
        }
    }

    protected void clientActionFailed() {
        Creature actor = getActor();
        if (actor != null && actor.isPlayer())
            actor.sendActionFailed();
    }

    /**
     * Останавливает движение
     *
     * @param validate - рассылать ли ValidateLocation
     */
    void clientStopMoving(boolean validate) {
        Creature actor = getActor();
        actor.stopMove(validate);
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
        return _intention;
    }

    public final void setIntention(CtrlIntention intention) {
        setIntention(intention, null, null);
    }

    public Creature getAttackTarget() {
        return _attackTarget.get();
    }

    public void setAttackTarget(Creature target) {
        _attackTarget = target == null ? HardReferences.<Creature>emptyRef() : target.getRef();
    }

    /**
     * Означает, что AI всегда включен, независимо от состояния региона
     */
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

    public abstract void onEvtTimer(int timerId, Object arg1, Object arg2);
}