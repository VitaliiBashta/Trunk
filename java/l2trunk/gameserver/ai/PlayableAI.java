package l2trunk.gameserver.ai;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.Skill.NextAction;
import l2trunk.gameserver.model.Skill.SkillType;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MyTargetSelected;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.Location;

import java.util.concurrent.ScheduledFuture;

import static l2trunk.gameserver.ai.CtrlIntention.*;

public class PlayableAI extends CharacterAI {
    Skill skill;
    boolean _forceUse;
    private Object _intention_arg0 = null;
    private Object _intention_arg1 = null;
    private volatile int thinking = 0; // to prevent recursive thinking
    private nextAction _nextAction;
    private Object _nextAction_arg0;
    private Object _nextAction_arg1;
    private boolean _nextAction_arg2;
    private boolean _nextAction_arg3;
    private boolean _dontMove;

    private ScheduledFuture<?> _followTask;

    PlayableAI(Playable actor) {
        super(actor);
    }


    public void changeIntention(CtrlIntention intention, Object arg0, Object arg1) {
        super.changeIntention(intention);
        _intention_arg0 = arg0;
        _intention_arg1 = arg1;
    }

    @Override
    public void setIntention(CtrlIntention intention, Object arg0, Object arg1) {
        _intention_arg0 = null;
        _intention_arg1 = null;
        super.setIntention(intention, arg0, arg1);
    }

    @Override
    public void onIntentionCast(Skill skill, Creature target) {
        this.skill = skill;
        super.onIntentionCast(skill, target);
    }

    @Override
    public void setNextAction(nextAction action, Object arg0, Object arg1, boolean arg2, boolean arg3) {
        _nextAction = action;
        _nextAction_arg0 = arg0;
        _nextAction_arg1 = arg1;
        _nextAction_arg2 = arg2;
        _nextAction_arg3 = arg3;
    }

    public boolean setNextIntention() {
        nextAction nextAction = _nextAction;
        Object nextAction_arg0 = _nextAction_arg0;
        Object nextAction_arg1 = _nextAction_arg1;
        boolean nextAction_arg2 = _nextAction_arg2;
        boolean nextAction_arg3 = _nextAction_arg3;

        Playable actor = getActor();
        if ((nextAction == null) || actor.isActionsDisabled()) {
            return false;
        }

        Skill skill;
        Creature target;
        GameObject object;

        switch (nextAction) {
            case ATTACK:
                if (nextAction_arg0 == null) {
                    return false;
                }
                target = (Creature) nextAction_arg0;
                _forceUse = nextAction_arg2;
                _dontMove = nextAction_arg3;
                clearNextAction();
                setIntentionAttack(target);
                break;
            case CAST:
                if ((nextAction_arg0 == null) || (nextAction_arg1 == null)) {
                    return false;
                }
                skill = (Skill) nextAction_arg0;
                target = (Creature) nextAction_arg1;
                _forceUse = nextAction_arg2;
                _dontMove = nextAction_arg3;
                clearNextAction();
                if (!skill.checkCondition(actor.getPlayer(), target, _forceUse, _dontMove, true)) {
                    if ((skill.nextAction == NextAction.ATTACK) && !actor.equals(target)) {
                        setNextAction(PlayableAI.nextAction.ATTACK, target, null, _forceUse, false);
                        return setNextIntention();
                    }
                    return false;
                }
                setIntention(AI_INTENTION_CAST, skill, target);
                break;
            case MOVE:
                if ((nextAction_arg0 == null) || (nextAction_arg1 == null)) {
                    return false;
                }
                Location loc = (Location) nextAction_arg0;
                Integer offset = (Integer) nextAction_arg1;
                clearNextAction();
                actor.moveToLocation(loc, offset, nextAction_arg2);
                break;
            case REST:
                actor.sitDown(null);
                break;
            case INTERACT:
                if (nextAction_arg0 == null) {
                    return false;
                }
                object = (GameObject) nextAction_arg0;
                clearNextAction();
                onIntentionInteract(object);
                break;
            case PICKUP:
                if (nextAction_arg0 == null) {
                    return false;
                }
                object = (GameObject) nextAction_arg0;
                clearNextAction();
                onIntentionPickUp(object);
                break;
            case EQIP:
                if ((actor instanceof Player) && (nextAction_arg0 instanceof ItemInstance)) {
                    ItemInstance item = (ItemInstance) nextAction_arg0;
                    item.getTemplate().getHandler().useItem((Player)actor, item, _nextAction_arg2);
                    break;
                } else {
                    return false;
                }
            case COUPLE_ACTION:
                if ((nextAction_arg0 == null) || (nextAction_arg1 == null)) {
                    return false;
                }
                target = (Creature) nextAction_arg0;
                Integer socialId = (Integer) nextAction_arg1;
                _forceUse = nextAction_arg2;
                _nextAction = null;
                clearNextAction();
                onIntentionCoupleAction((Player) target, socialId);
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public void clearNextAction() {
        _nextAction = null;
        _nextAction_arg0 = null;
        _nextAction_arg1 = null;
        _nextAction_arg2 = false;
        _nextAction_arg3 = false;
    }

    @Override
    public void onEvtFinishCasting() {
        if (!setNextIntention()) {
            setIntention(AI_INTENTION_ACTIVE);
        }
    }

    @Override
    public void onEvtReadyToAct() {
        if (!setNextIntention()) {
            onEvtThink();
        }
    }

    @Override
    public void onEvtArrived() {
        if (!setNextIntention()) {
            if ((getIntention() == AI_INTENTION_INTERACT) || (getIntention() == AI_INTENTION_PICK_UP)) {
                onEvtThink();
            } else {
                changeIntention(AI_INTENTION_ACTIVE);
            }
        }
    }

    @Override
    public void onEvtArrivedTarget() {
        switch (getIntention()) {
            case AI_INTENTION_ATTACK:
                thinkAttack(false);
                break;
            case AI_INTENTION_CAST:
                thinkCast(false);
                break;
            case AI_INTENTION_FOLLOW:
                thinkFollow();
                break;
            default:
                onEvtThink();
                break;
        }
    }

    @Override
    public final void onEvtThink() {
        Playable actor = getActor();
        if (actor.isActionsDisabled()) {
            return;
        }

        try {
            if (thinking > 1) {
                thinking++;
                return;
            }

            switch (getIntention()) {
                case AI_INTENTION_ACTIVE:
                    thinkActive();
                    break;
                case AI_INTENTION_ATTACK:
                    thinkAttack(true);
                    break;
                case AI_INTENTION_CAST:
                    if (actor instanceof Player) {
                        Player player = (Player)actor;
                        if (player.isCastingNow() || (player.getCastingSkill() != null)) return;
                    }
                    thinkCast(true);
                    break;
                case AI_INTENTION_PICK_UP:
                    thinkPickUp();
                    break;
                case AI_INTENTION_INTERACT:
                    thinkInteract();
                    break;
                case AI_INTENTION_FOLLOW:
                    thinkFollow();
                    break;
                case AI_INTENTION_COUPLE_ACTION:
                    thinkCoupleAction((Player) _intention_arg0, (Integer) _intention_arg1, false);
                    break;
                default:
                    break;
            }
        } catch (RuntimeException e) {
            //_log.error("Error while Thinking", e);
        } finally {
            thinking--;
        }
    }

    public void thinkActive() {

    }

    public void thinkFollow() {
        Playable actor = getActor();

        Creature target = (Creature) _intention_arg0;
        Integer offset = (Integer) _intention_arg1;

        // Are too far away goal or target is not suitable for the following
        if (target == null || target.isAlikeDead() || actor.getDistance(target) > 4000 || offset == null) {
            clientActionFailed();
            return;
        }

        // Already follow this end
        if (actor.isFollow && (actor.getFollowTarget() == target)) {
            clientActionFailed();
            return;
        }

        // Are close enough or can not get around - then flee?
        if (actor.isInRange(target, offset + 20) || actor.isMovementDisabled()) {
            clientActionFailed();
        }

        if (_followTask != null) {
            _followTask.cancel(false);
            _followTask = null;
        }

        _followTask = ThreadPoolManager.INSTANCE.schedule(new ThinkFollow(), 250L);
    }

    @Override
    public void onIntentionInteract(GameObject object) {
        Playable actor = getActor();

        if (actor.isActionsDisabled()) {
            setNextAction(nextAction.INTERACT, object, null, false, false);
            clientActionFailed();
            return;
        }

        clearNextAction();
        changeIntention(AI_INTENTION_INTERACT, object, null);
        onEvtThink();
    }

    @Override
    public void onIntentionCoupleAction(Player player, Integer socialId) {
        clearNextAction();
        changeIntention(CtrlIntention.AI_INTENTION_COUPLE_ACTION, player, socialId);
        onEvtThink();
    }

    private void thinkInteract() {
        Playable actor = getActor();

        GameObject target = (GameObject) _intention_arg0;

        if (target == null) {
            setIntention(AI_INTENTION_ACTIVE);
            return;
        }

        int range = (int) (Math.max(30, actor.getMinDistance(target)) + 20);

        if (actor.isInRangeZ(target, range)) {
            if (actor instanceof Player) {
                ((Player) actor).doInteract(target);
            }
            setIntention(AI_INTENTION_ACTIVE);
        } else {
            actor.moveToLocation(target.getLoc(), 40, true);
            setNextAction(nextAction.INTERACT, target, null, false, false);
        }
    }

    @Override
    public void onIntentionPickUp(GameObject object) {
        Playable actor = getActor();

        if (actor.isActionsDisabled()) {
            setNextAction(nextAction.PICKUP, object, null, false, false);
            clientActionFailed();
            return;
        }

        clearNextAction();
        changeIntention(AI_INTENTION_PICK_UP, object,null);
        onEvtThink();
    }

    private void thinkPickUp() {
        final Playable actor = getActor();

        final GameObject target = (GameObject) _intention_arg0;

        if (target == null) {
            setIntention(AI_INTENTION_ACTIVE);
            return;
        }

        if (actor.isInRange(target, 30) && (Math.abs(actor.getZ() - target.getZ()) < 50)) {
            if (actor instanceof Player || actor instanceof PetInstance ) {
                if (target instanceof ItemInstance)
                actor.doPickupItem((ItemInstance)target);
            }
            setIntention(AI_INTENTION_ACTIVE);
        } else {
            ThreadPoolManager.INSTANCE.execute(new RunnableImpl() {
                @Override
                public void runImpl() {
                    actor.moveToLocation(target.getLoc(), 10, true);
                    setNextAction(nextAction.PICKUP, target, null, false, false);
                }
            });
        }
    }

    public void thinkAttack(boolean checkRange) {
        Playable actor = getActor();

        Player player = actor.getPlayer();
        if (player == null) {
            setIntention(AI_INTENTION_ACTIVE);
            return;
        }

        if (actor.isActionsDisabled() || actor.isAttackingDisabled()) {
            actor.sendActionFailed();
            return;
        }

        boolean isPosessed = (actor instanceof Summon) && ((Summon) actor).isDepressed();

        Creature attack_target = getAttackTarget();
        if ((attack_target == null) || attack_target.isDead() || (!isPosessed && !(_forceUse ? attack_target.isAttackable(actor) : attack_target.isAutoAttackable(actor)))) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        if (!checkRange) {
            clientStopMoving();
            actor.doAttack(attack_target);
            return;
        }

        int range = actor.getPhysicalAttackRange();
        if (range < 10) {
            range = 10;
        }

        boolean canSee = GeoEngine.canSeeTarget(actor, attack_target, false);

        if (!canSee && ((range > 200) || (Math.abs(actor.getZ() - attack_target.getZ()) > 200))) {
            actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        range += actor.getMinDistance(attack_target);

        if (actor.isFakeDeath()) {
            actor.breakFakeDeath();
        }

        if (actor.isInRangeZ(attack_target, range)) {
            if (!canSee) {
                actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                setIntention(AI_INTENTION_ACTIVE);
                actor.sendActionFailed();
                return;
            }

            clientStopMoving();
            actor.doAttack(attack_target);
        } else if (!_dontMove) {
            ThreadPoolManager.INSTANCE.execute(new ExecuteFollow(attack_target, range - 20));
        } else {
            actor.sendActionFailed();
        }
    }

    public void thinkCast(boolean checkRange) {
        Playable actor = getActor();

        Creature target = getAttackTarget();

        if ((skill.skillType == SkillType.CRAFT) || skill.isToggle()) {
            if (skill.checkCondition(actor.getPlayer(), target, _forceUse, _dontMove, true)) {
                actor.doCast(skill, target, _forceUse);
            }
            return;
        }

        if ((target == null) || ((target.isDead() != skill.isCorpse) && !skill.isNotTargetAoE())) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        if (!checkRange) {
            // If the skill is the next step, assign this action after the expiry of skill
            if ((skill.nextAction == NextAction.ATTACK) && !actor.equals(target) && (!_forceUse)) {
                setNextAction(nextAction.ATTACK, target, null, _forceUse, false);
            } else {
                clearNextAction();
            }

            clientStopMoving();

            if (skill.checkCondition(actor.getPlayer(), target, _forceUse, _dontMove, true)) {
                actor.doCast(skill, target, _forceUse);
            } else {
                setNextIntention();
                if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                    thinkAttack(true);
                }
            }

            return;
        }

        int range = actor.getMagicalAttackRange(skill);
        if (range < 10) {
            range = 10;
        }

        boolean canSee = (skill.skillType == SkillType.TAKECASTLE) || (skill.skillType == SkillType.TAKEFORTRESS) || GeoEngine.canSeeTarget(actor, target, actor.isFlying());
        boolean noRangeSkill = skill.castRange == 32767;

        if (!noRangeSkill && !canSee && ((range > 200) || (Math.abs(actor.getZ() - target.getZ()) > 200))) {
            actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        range += actor.getMinDistance(target);

        if (actor.isFakeDeath()) {
            actor.breakFakeDeath();
        }

        if (actor.isInRangeZ(target, range) || noRangeSkill) {
            if (!noRangeSkill && !canSee) {
                actor.sendPacket(SystemMsg.CANNOT_SEE_TARGET);
                setIntention(AI_INTENTION_ACTIVE);
                actor.sendActionFailed();
                return;
            }

            // If the skill is the next step, assign this action after the expiry of skill
            if ((skill.nextAction == NextAction.ATTACK) && !actor.equals(target) && (!_forceUse)) {
                setNextAction(nextAction.ATTACK, target, null, _forceUse, false);
            } else {
                clearNextAction();
            }

            if (skill.checkCondition(actor.getPlayer(), target, _forceUse, _dontMove, true)) {
                clientStopMoving();
                actor.doCast(skill, target, _forceUse);
            } else {
                setNextIntention();
                if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) {
                    thinkAttack(true);
                }
            }
        } else if (!_dontMove) {
            ThreadPoolManager.INSTANCE.execute(new ExecuteFollow(target, range - 20));
        } else {
            actor.sendPacket(Msg.YOUR_TARGET_IS_OUT_OF_RANGE);
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
        }
    }

    public void thinkCoupleAction(Player target, Integer socialId, boolean cancel) {
        //
    }

    @Override
    public void onEvtDead(Creature killer) {
        clearNextAction();
        super.onEvtDead(killer);
    }

    @Override
    public void onEvtFakeDeath() {
        clearNextAction();
        super.onEvtFakeDeath();
    }

    public void lockTarget(Creature target) {
        Playable actor = getActor();

        if ((target == null) || target.isDead()) {
            actor.setAggressionTarget(null);
        } else if (actor.getAggressionTarget() == null) {
            GameObject actorStoredTarget = actor.getTarget();
            actor.setAggressionTarget(target);
            actor.setTarget(target);

            clearNextAction();
            // DS: aggression only throws an invisible target, but the current is not interrupted the attack / cast
            /*
             * if (getIntention() == CtrlIntention.AI_INTENTION_ATTACK) setAttackTarget(target); switch(getIntention())
             * {
             * 		case AI_INTENTION_ATTACK:
             * 			setAttackTarget(target);
             * 			break;
             * 		case AI_INTENTION_CAST:
             * 			L2Skill skill = actor.getCastingSkill();
             * 				if (skill == null) skill = skill;
             * 				if (skill != null && !skill.isUsingWhileCasting()) switch(skill.targetType())
             * 				{
             * 					case TARGET_ONE:
             * 					case TARGET_AREA:
             * 					case TARGET_MULTIFACE:
             * 					case TARGET_TUNNEL:
             * 						setAttackTarget(target);
             * 						actor.setCastingTarget(target);
             * 					break;
             * 				}
             * 			break;
             * }
             */

            if (actorStoredTarget != target) {
                actor.sendPacket(new MyTargetSelected(target.objectId(), 0));
            }
        }
    }

    @Override
    public void Attack(Creature target, boolean forceUse, boolean dontMove) {
        Playable actor = getActor();

        if (actor.isActionsDisabled() || actor.isAttackingDisabled()) {
            // Если не можем атаковать, то атаковать позже
            setNextAction(nextAction.ATTACK, target, null, forceUse, false);
            actor.sendActionFailed();
            return;
        }

        _dontMove = dontMove;
        _forceUse = forceUse;
        clearNextAction();
        setIntentionAttack(target);
    }

    @Override
    public void cast(Skill skill, Creature target, boolean forceUse, boolean dontMove) {
        Playable actor = getActor();

        // Если скилл альтернативного типа (например, бутылка на хп),
        // то он может использоваться во время каста других скиллов, или во время атаки, или на бегу.
        // Поэтому пропускаем дополнительные проверки.
        if (actor instanceof Player) {
            Player player = (Player)actor;
            if ((player.getCastingSkill() != null) && (player.getCastingSkill().skillType == SkillType.TRANSFORMATION)) {
                clientActionFailed();
                return;
            }
        }
        if (skill.isAltUse || skill.isToggle()) {
            if ((skill.isToggle() || skill.isItemHandler) && (actor.isOutOfControl() || actor.isStunned() || actor.isSleeping() || actor.isParalyzed() || actor.isAlikeDead())) {
                clientActionFailed();
            } else {
                actor.altUseSkill(skill.id, target);
            }
            return;
        }

        // Если не можем кастовать, то использовать скилл позже
        if (actor.isActionsDisabled()) {
            // if (!actor.isSkillDisabled(skill.id()))
            setNextAction(nextAction.CAST, skill, target, forceUse, dontMove);
            clientActionFailed();
            return;
        }

        // actor.stopMove(null);
        _forceUse = forceUse;
        _dontMove = dontMove;
        clearNextAction();
        setIntention(CtrlIntention.AI_INTENTION_CAST, skill, target);
    }

    @Override
    public Playable getActor() {
        return (Playable) super.getActor();
    }

    public enum nextAction {
        ATTACK,
        CAST,
        MOVE,
        REST,
        PICKUP,
        EQIP,
        INTERACT,
        COUPLE_ACTION
    }

    protected class ThinkFollow extends RunnableImpl {
        @Override
        public void runImpl() {
            Playable actor = getActor();

            if (getIntention() != AI_INTENTION_FOLLOW) {
                // If the pet has stopped the persecution, change status, it was not necessary to click on the Follow button 2 times.
                if ((actor instanceof Summon) && (getIntention() == AI_INTENTION_ACTIVE)) {
                    ((Summon) actor).setFollowMode(false);
                }
                return;
            }

            Creature target = (Creature) _intention_arg0;
            int offset = _intention_arg1 instanceof Integer ? (Integer) _intention_arg1 : 0;

            if (target == null || !target.isVisible() || target.isAlikeDead() || actor.getDistance(target) > 4000) {
                setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                ThreadPoolManager.INSTANCE.schedule(new FollowRecover(), 250L);
                return;
            }

            Player player = actor.getPlayer();

            if (player == null || player.isLogoutStarted() || ( actor instanceof Summon ) && player.getPet() != actor) {
                setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                return;
            }

            if (!actor.isInRange(target, offset + 20) && (!actor.isFollow || (actor.getFollowTarget() != target)))
                actor.followToCharacter(target, offset, false);
            _followTask = ThreadPoolManager.INSTANCE.schedule(this, 250L);
        }
    }

    private class ExecuteFollow extends RunnableImpl {
        private final Creature target;
        private final int range;

        ExecuteFollow(Creature target, int range) {
            this.target = target;
            this.range = range;
        }

        @Override
        public void runImpl() {
            if (target instanceof DoorInstance) {
                actor.moveToLocation(target.getLoc(), 40, true);
            } else {
                actor.followToCharacter(target, range, true);
            }
        }
    }

    protected class FollowRecover extends RunnableImpl {
        @Override
        public void runImpl() {
            Playable actor = getActor();
            Creature target = (_intention_arg0 instanceof Creature) ? (Creature) _intention_arg0 : null;

            if (getIntention() == AI_INTENTION_FOLLOW) {
                if (target == null || !(actor instanceof SummonInstance)) {
                    return;
                }

                if (!target.isAlikeDead() && (actor.getDistance(target) <= 4000)) {
                    ((Summon) actor).setFollowMode(true);
                    int offset = (_intention_arg1 instanceof Integer) ? (Integer) _intention_arg1 : 0;
                    actor.followToCharacter(target, offset, false);
                    return;
                }
            }

            ThreadPoolManager.INSTANCE.schedule(this, 250L);
        }
    }
}