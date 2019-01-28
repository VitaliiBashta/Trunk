package l2trunk.gameserver.ai;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Skill.SkillType;
import l2trunk.gameserver.model.items.attachment.FlagItemAttachment;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.network.serverpackets.ExRotation;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import static l2trunk.gameserver.ai.CtrlIntention.AI_INTENTION_ACTIVE;

public final class PlayerAI extends PlayableAI {
    public PlayerAI(Player actor) {
        super(actor);
    }

    @Override
    public void onIntentionRest() {
        changeIntention(CtrlIntention.AI_INTENTION_REST);
        setAttackTarget(null);
        clientStopMoving();
    }

    @Override
    public void onIntentionActive() {
        clearNextAction();
        changeIntention(CtrlIntention.AI_INTENTION_ACTIVE);
    }

    @Override
    public void onIntentionInteract(GameObject object) {
        Player actor = getActor();

        if (actor.getSittingTask()) {
            setNextAction(nextAction.INTERACT, object, null, false, false);
            return;
        } else if (actor.isSitting()) {
            actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
            clientActionFailed();
            return;
        }
        super.onIntentionInteract(object);
    }

    @Override
    public void onIntentionPickUp(GameObject object) {
        Player actor = getActor();

        if (actor.getSittingTask()) {
            setNextAction(nextAction.PICKUP, object, null, false, false);
            return;
        } else if (actor.isSitting()) {
            actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
            clientActionFailed();
            return;
        }
        super.onIntentionPickUp(object);
    }

    @Override
    public void thinkAttack(boolean checkRange) {
        Player actor = getActor();

        if (actor.isInFlyingTransform()) {
            setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
            return;
        }

        FlagItemAttachment attachment = actor.getActiveWeaponFlagAttachment();
        if (attachment != null && !attachment.canAttack(actor)) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        if (actor.isFrozen()) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }

        super.thinkAttack(checkRange);
    }

    @Override
    public void thinkCast(boolean checkRange) {
        Player actor = getActor();

        FlagItemAttachment attachment = actor.getActiveWeaponFlagAttachment();
        if (attachment != null && !attachment.canCast(actor, skill)) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendActionFailed();
            return;
        }

        if (actor.isFrozen()) {
            setIntention(AI_INTENTION_ACTIVE);
            actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_FROZEN, ActionFail.STATIC);
            return;
        }

        super.thinkCast(checkRange);
    }

    @Override
    public void thinkCoupleAction(Player target, Integer socialId, boolean cancel) {
        Player actor = getActor();
        if (target == null || !target.isOnline()) {
            actor.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
            return;
        }

        if (cancel || !actor.isInRange(target, 50) || actor.isInRange(target, 20) || actor.getReflection() != target.getReflection() || !GeoEngine.canSeeTarget(actor, target, false)) {
            target.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
            actor.sendPacket(SystemMsg.THE_COUPLE_ACTION_WAS_CANCELLED);
            return;
        }
        if (_forceUse) // служит только для флага что б активировать у другого игрока социалку
            target.getAI().setIntention(CtrlIntention.AI_INTENTION_COUPLE_ACTION, actor, socialId);
        //
        int heading = actor.calcHeading(target.getX(), target.getY());
        actor.setHeading(heading);
        actor.broadcastPacket(new ExRotation(actor.getObjectId(), heading));
        //
        actor.broadcastPacket(new SocialAction(actor.getObjectId(), socialId));
    }

    @Override
    public void Attack(Creature target, boolean forceUse, boolean dontMove) {
        Player actor = getActor();

        if (actor.isInFlyingTransform()) {
            actor.sendActionFailed();
            return;
        }

        if (System.currentTimeMillis() - actor.getLastAttackPacket() < Config.ATTACK_PACKET_DELAY) {
            actor.sendActionFailed();
            return;
        }

        actor.setLastAttackPacket();

        if (actor.getSittingTask()) {
            setNextAction(nextAction.ATTACK, target, null, forceUse, false);
            return;
        } else if (actor.isSitting()) {
            actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);
            clientActionFailed();
            return;
        }

        super.Attack(target, forceUse, dontMove);
    }

    @Override
    public void cast(Skill skill, Creature target, boolean forceUse, boolean dontMove) {
        Player actor = getActor();

        if (!skill.altUse() && !skill.isToggle() && !(skill.skillType == SkillType.CRAFT && Config.ALLOW_TALK_WHILE_SITTING))
            // Если в этот момент встаем, то использовать скилл когда встанем
            if (actor.getSittingTask()) {
                setNextAction(nextAction.CAST, skill, target, forceUse, dontMove);
                clientActionFailed();
                return;
            } else if (skill.skillType == SkillType.SUMMON) {
                if (actor.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
                    actor.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_DURING_A_TRADE_OR_WHILE_USING_A_PRIVATE_STORE);
                    clientActionFailed();
                    return;
                }
                //   else if (actor.getPetSummonBlockedTime() > System.currentTimeMillis())
                //   {
                //      int time = (int) (actor.getPetSummonBlockedTime() - System.currentTimeMillis()) / 60000;
                //    if (time == 0)
                //      actor.sendMessage("You must wait 1 minute after your firt summon." );
                //      else
                //          actor.sendMessage("You must wait " + time + " minutes after your firt summon." );
                //      clientActionFailed();
                //      return;
                //  }
            }
            // char is sitting
            else if (actor.isSitting()) {
                if (skill.skillType == SkillType.TRANSFORMATION)
                    actor.sendPacket(SystemMsg.YOU_CANNOT_TRANSFORM_WHILE_SITTING);
                else
                    actor.sendPacket(SystemMsg.YOU_CANNOT_MOVE_WHILE_SITTING);

                clientActionFailed();
                return;
            }

        // setBlock the player for 10 minutes to summon a pet after the resurrection.
        //actor.setPetSummonBlockedTime(System.currentTimeMillis() + 600 * 1000);

        super.cast(skill, target, forceUse, dontMove);
    }

    @Override
    public Player getActor() {
        return (Player) super.getActor();
    }
}