package l2trunk.gameserver.ai;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.PlayableAI.nextAction;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.Die;
import l2trunk.gameserver.utils.Location;

public class CharacterAI extends AbstractAI {
    public CharacterAI(Creature actor) {
        super(actor);
    }

    @Override
    public void onIntentionIdle() {
        clientStopMoving();
        changeIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    public void onIntentionActive() {
        clientStopMoving();
        changeIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        onEvtThink();
    }

    @Override
    public void onIntentionAttack(Creature target) {
        setAttackTarget(target);
        clientStopMoving();
        changeIntention(CtrlIntention.AI_INTENTION_ATTACK);
        onEvtThink();
    }

    @Override
    public void onIntentionCast(Skill skill, Creature target) {
        setAttackTarget(target);
        changeIntention(CtrlIntention.AI_INTENTION_CAST);
        onEvtThink();
    }

    @Override
    public void onIntentionFollow(Creature target, Integer offset) {
        changeIntention(CtrlIntention.AI_INTENTION_FOLLOW);
        onEvtThink();
    }

    @Override
    public void onIntentionInteract(GameObject object) {
    }

    @Override
    public void onIntentionPickUp(GameObject item) {
    }

    @Override
    public void onIntentionRest() {
    }

    @Override
    public void onIntentionCoupleAction(Player player, Integer socialId) {
    }

    @Override
    public void onEvtArrivedBlocked(Location blocked_at_pos) {
        Creature actor = getActor();
        if (actor.isPlayer()) {
            // Приводит к застреванию в стенах:
            //if (actor.isInRange(blocked_at_pos, 1000))
            //	actor.setLoc(blocked_at_pos, true);
            // Этот способ надежнее:
            Location loc = ((Player) actor).getLastServerPosition();
            if (loc != null)
                actor.setLoc(loc, true);
            actor.stopMove();
        }
        onEvtThink();
    }

    @Override
    public void onEvtForgetObject(GameObject object) {
        if (object == null)
            return;

        Creature actor = getActor();

        if (actor.isAttackingNow() && getAttackTarget() == object)
            actor.abortAttack(true, true);

        if (actor.isCastingNow() && getAttackTarget() == object)
            actor.abortCast(true, true);

        if (getAttackTarget() == object)
            setAttackTarget(null);

        if (actor.getTargetId() == object.getObjectId())
            actor.setTarget(null);

        if (actor.getFollowTarget() == object)
            actor.setFollowTarget(null);

        if (actor.getPet() != null)
            actor.getPet().getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
    }

    @Override
    public void onEvtDead(Creature killer) {
        Creature actor = getActor();

        actor.abortAttack(true, true);
        actor.abortCast(true, true);
        actor.stopMove();
        actor.broadcastPacket(new Die(actor));

        setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    public void onEvtFakeDeath() {
        clientStopMoving();
        setIntention(CtrlIntention.AI_INTENTION_IDLE);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (damage > 0) {
            notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 2);
        }


    }

    @Override
    public void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage) {
    }

    public void Attack(Creature target, boolean forceUse, boolean dontMove) {
        setIntentionAttack(CtrlIntention.AI_INTENTION_ATTACK, target);
    }

    public void Cast(Skill skill, Creature target) {
        Cast(skill, target, false, false);
    }

    void Cast(Skill skill, Creature target, boolean forceUse, boolean dontMove) {
        setIntentionAttack(CtrlIntention.AI_INTENTION_ATTACK, target);
    }

    @Override
    public void onEvtThink() {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }

    @Override
    public void onEvtFinishCasting() {
    }

    @Override
    public void onEvtReadyToAct() {
    }

    @Override
    public void onEvtArrived() {
    }

    @Override
    public void onEvtArrivedTarget() {
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
    }

    @Override
    public void onEvtSpawn() {
    }

    @Override
    public void onEvtDeSpawn() {
    }

    public void stopAITask() {
    }

    public void startAITask() {
    }

    public void setNextAction(nextAction action, Object arg0, Object arg1, boolean arg2, boolean arg3) {
    }

    public void clearNextAction() {
    }

    public boolean isActive() {
        return true;
    }

    @Override
    public void onEvtTimer(int timerId) {
    }

    protected void addTimer(int timerId, long delay) {
        addTimer(timerId, null, null, delay);
    }

    public void addTimer(int timerId, Object arg1, long delay) {
        addTimer(timerId, arg1, null, delay);
    }

    protected void addTimer(int timerId, Object arg1, Object arg2, long delay) {
        ThreadPoolManager.INSTANCE.schedule(new Timer(timerId), delay);
    }

    protected class Timer extends RunnableImpl {
        private final int timerId;

        Timer(int timerId) {
            this.timerId = timerId;
        }

        @Override
        public void runImpl() {
            notifyEvent(CtrlEvent.EVT_TIMER, timerId);
        }
    }
}