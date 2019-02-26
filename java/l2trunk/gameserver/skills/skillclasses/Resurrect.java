package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.lang.Pair;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.BaseStats;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

public final class Resurrect extends Skill {
    private final boolean _canPet;

    public Resurrect(StatsSet set) {
        super(set);
        _canPet = set.getBool("canPet", false);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (player != null) {
            if ((target == null) || ((target != player) && (!target.isDead()))) {
                player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }

            Player pcTarget = target.getPlayer();

            if (pcTarget == null) {
                player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }

            if ((pcTarget.getTeam() != TeamType.NONE) && (player.getTeam() == TeamType.NONE))
                return false;
            if ((player.getTeam() != TeamType.NONE) && (pcTarget.getTeam() == TeamType.NONE))
                return false;
            if ((player.getTeam() != TeamType.NONE) && (pcTarget.getTeam() != TeamType.NONE) && (player.getTeam() != pcTarget.getTeam())) {
                return false;
            }
            if ((player.isInOlympiadMode()) || (pcTarget.isInOlympiadMode())) {
                player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                return false;
            }

            for (GlobalEvent e : player.getEvents()) {
                if (!e.canRessurect(player, target, forceUse)) {
                    player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
                    return false;
                }
            }
            boolean playerInSiegeZone = player.isInZone(Zone.ZoneType.SIEGE);
            boolean targetInSiegeZone = target.isInZone(Zone.ZoneType.SIEGE);
            boolean playerClan = player.getClan() != null;
            boolean targetClan = pcTarget.getClan() != null;

            if ((playerInSiegeZone) || (targetInSiegeZone)) {
                if ((!targetClan) || (!playerClan)) {
                    player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
                    return false;
                }
                SiegeEvent event = target.getEvent(SiegeEvent.class);
                if (event == null) {
                    target.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
                    return false;
                }
            }

            if (oneTarget()) {
                if (target instanceof PetInstance) {
                    Pair ask = pcTarget.getAskListener(false);
                    ReviveAnswerListener reviveAsk = (ask != null) && ((ask.getValue() instanceof ReviveAnswerListener)) ? (ReviveAnswerListener) ask.getValue() : null;
                    if (reviveAsk != null) {
                        if (reviveAsk.isForPet())
                            player.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
                        else
                            player.sendPacket(Msg.SINCE_THE_MASTER_WAS_IN_THE_PROCESS_OF_BEING_RESURRECTED_THE_ATTEMPT_TO_RESURRECT_THE_PET_HAS_BEEN_CANCELLED);
                        return false;
                    }
                    if ((!this._canPet) && (this.targetType != SkillTargetType.TARGET_PET)) {
                        player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                        return false;
                    }
                } else if (target instanceof Player) {
                    Pair ask = pcTarget.getAskListener(false);
                    ReviveAnswerListener reviveAsk = (ask != null) && ((ask.getValue() instanceof ReviveAnswerListener)) ? (ReviveAnswerListener) ask.getValue() : null;

                    if (reviveAsk != null) {
                        if (reviveAsk.isForPet())
                            player.sendPacket(Msg.WHILE_A_PET_IS_ATTEMPTING_TO_RESURRECT_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
                        else
                            player.sendPacket(Msg.BETTER_RESURRECTION_HAS_BEEN_ALREADY_PROPOSED);
                        return false;
                    }
                    if (this.targetType == SkillTargetType.TARGET_PET) {
                        player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                        return false;
                    }

                    if (pcTarget.isFestivalParticipant()) {
                        player.sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Resurrect"));
                        return false;
                    }
                }
            }
            return super.checkCondition(player, target, forceUse, dontMove, first);
        } else {
            return false;
        }
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        double percent = power;

        if (percent < 100 && !isItemHandler) {
            double wit_bonus = power * (BaseStats.WIT.calcBonus(activeChar) - 1);
            percent += wit_bonus > 20 ? 20 : wit_bonus;
            if (percent > 90)
                percent = 90;
        }

        for (Creature target : targets) {
            Loop:
            if (target != null) {
                if (target.getPlayer() == null)
                    continue;

                for (GlobalEvent e : target.getEvents())
                    if (!e.canRessurect((Player) activeChar, target, true))
                        break Loop;

                if (target instanceof PetInstance && _canPet) {
                    if (((PetInstance)target).owner == activeChar)
                        ((PetInstance) target).doRevive(percent);
                    else
                        target.getPlayer().reviveRequest((Player) activeChar, percent, true);
                } else if (target instanceof Player) {
                    if (targetType == SkillTargetType.TARGET_PET)
                        continue;

                    Player targetPlayer = (Player) target;

                    Pair<Integer, OnAnswerListener> ask = targetPlayer.getAskListener(false);
                    ReviveAnswerListener reviveAsk = ask != null && ask.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) ask.getValue() : null;
                    if (reviveAsk != null)
                        continue;

                    if (targetPlayer.isFestivalParticipant())
                        continue;

                    targetPlayer.reviveRequest((Player) activeChar, percent, false);
                } else
                    continue;

                getEffects(activeChar, target, activateRate > 0, false);
            }
        }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}