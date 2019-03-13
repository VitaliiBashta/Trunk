package l2trunk.gameserver.skills.effects;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.data.xml.holder.CubicHolder;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillLaunched;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.templates.CubicTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public final class EffectCubic extends Effect {
    private final CubicTemplate _template;
    private Future<?> _task = null;

    public EffectCubic(Env env, EffectTemplate template) {
        super(env, template);
        _template = CubicHolder.getTemplate(getTemplate().getParam().getInteger("cubicId"), getTemplate().getParam().getInteger("cubicLevel"));
    }

    private static boolean canBeAttacked(Player attacker, Creature target, CubicTemplate.SkillInfo info) {
        if (target == null || target.isDead())
            return false;

        if (target instanceof DoorInstance && !info.isCanAttackDoor())
            return false;

        if (!attacker.isInRangeZ(target, info.getSkill().castRange))
            return false;

        return target.isAutoAttackable(attacker);
    }

    private static void doHeal(final Player player, CubicTemplate.SkillInfo info, final int delay) {
        final Skill skill = info.getSkill();
        Player target = null;
        if (player.getParty() == null) {
            if (!player.isCurrentHpFull() && !player.isDead())
                target = player;
        } else {
            double currentHp = Integer.MAX_VALUE;
            List<Player> members = player.getParty().getMembersStream()
                    .filter(Objects::nonNull).collect(Collectors.toList());
            for (Player member : members) {
                if (player.isInRange(member, info.getSkill().castRange) && !member.isCurrentHpFull() && !member.isDead() && member.getCurrentHp() < currentHp) {
                    currentHp = member.getCurrentHp();
                    target = member;
                }
            }
        }

        if (target == null)
            return;

        int chance = info.getChance((int) target.getCurrentHpPercents());

        if (!Rnd.chance(chance))
            return;

        final Creature aimTarget = target;
        player.broadcastPacket(new MagicSkillUse(player, aimTarget, skill.id));
        player.disableSkill(skill, delay * 1000L);
        ThreadPoolManager.INSTANCE.schedule(() -> {
            final List<Creature> targets = List.of(aimTarget);
            player.broadcastPacket(new MagicSkillLaunched(player.objectId(), skill.displayId, skill.getDisplayLevel(), targets));
            player.callSkill(skill, targets, false);
        }, skill.hitTime);
    }

    private static void doAttack(final Player player, final CubicTemplate.SkillInfo info, final int delay) {
        if (!Rnd.chance(info.getChance()))
            return;

        final Skill skill = info.getSkill();
        Creature target = null;
        if (player.isInCombat()) {
            GameObject object = player.getTarget();
            target = object instanceof Creature ? (Creature) object : null;
        }
        if (!canBeAttacked(player, target, info))
            return;

        final Creature aimTarget = target;
        player.broadcastPacket(new MagicSkillUse(player, target, skill.displayId, skill.getDisplayLevel(), skill.hitTime, 0));
        player.disableSkill(skill, delay * 1000L);
        ThreadPoolManager.INSTANCE.schedule(() -> {
            final List<Creature> targets = List.of(aimTarget);

            player.broadcastPacket(new MagicSkillLaunched(player.objectId(), skill.displayId, skill.getDisplayLevel(), targets));
            player.callSkill(skill, targets, false);

            if (aimTarget instanceof NpcInstance)
                if (aimTarget.paralizeOnAttack(player)) {
                    if (Config.PARALIZE_ON_RAID_DIFF)
                        player.paralizeMe(aimTarget);
                } else {
                    int damage = skill.effectPoint != 0 ? skill.effectPoint : (int) skill.power;
                    aimTarget.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, damage);
                }
        }, skill.hitTime);
    }

    private static void doDebuff(final Player player, final CubicTemplate.SkillInfo info, final int delay) {
        if (!Rnd.chance(info.getChance()))
            return;

        final Skill skill = info.getSkill();
        Creature target = null;
        if (player.isInCombat()) {
            GameObject object = player.getTarget();
            target = object instanceof Creature ? (Creature) object : null;
        }
        if (!canBeAttacked(player, target, info))
            return;

        final Creature aimTarget = target;
        player.broadcastPacket(new MagicSkillUse(player, target, skill.displayId, skill.getDisplayLevel(), skill.hitTime, 0));
        player.disableSkill(skill, delay * 1000L);
        ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                final List<Creature> targets = List.of(aimTarget);
                player.broadcastPacket(new MagicSkillLaunched(player.objectId(), skill.displayId, skill.getDisplayLevel(), targets));
                final boolean succ = Formulas.calcSkillSuccess(player, aimTarget, skill, info.getChance());
                if (succ)
                    player.callSkill(skill, targets, false);

                if (aimTarget instanceof NpcInstance)
                    if (aimTarget.paralizeOnAttack(player)) {
                        if (Config.PARALIZE_ON_RAID_DIFF)
                            player.paralizeMe(aimTarget);
                    } else {
                        int damage = skill.effectPoint != 0 ? skill.effectPoint : (int) skill.power;
                        aimTarget.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, player, damage);
                    }
            }
        }, skill.hitTime);
    }

    private static void doCancel(final Player player, final CubicTemplate.SkillInfo info, final int delay) {
        if (!Rnd.chance(info.getChance()))
            return;

        if (player.getEffectList().getAllEffects().stream()
                .filter(Effect::isOffensive)
                .filter(Effect::isCancelable)
                .allMatch(e -> e.getTemplate().applyOnCaster))
            return;

        final Skill skill = info.getSkill();

        player.broadcastPacket(new MagicSkillUse(player, skill.displayId, skill.getDisplayLevel(), skill.hitTime));
        player.disableSkill(skill, delay * 1000L);
        ThreadPoolManager.INSTANCE.schedule(() -> {
            final List<Creature> targets = List.of(player);
            player.broadcastPacket(new MagicSkillLaunched(player.objectId(), skill.displayId, skill.getDisplayLevel(), targets));
            player.callSkill(skill, targets, false);
        }, skill.hitTime);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!(effected instanceof Playable)) {
            return;
        }
        Player player = ((Playable) effected).getPlayer();

        player.addCubic(this);
        _task = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new ActionTask(), 1000L, 1000L);
    }

    @Override
    public void onExit() {
        super.onExit();
        if (!(effected instanceof Playable)) {
            return;
        }

        Player player = ((Playable) effected).getPlayer();
        player.removeCubic(getId());
        _task.cancel(true);
        _task = null;
    }

    private void doAction(Player player) {
        for (Map.Entry<Integer, List<CubicTemplate.SkillInfo>> entry : _template.getSkills())
            if (Rnd.chance(entry.getKey())) // TODO: Должен выбирать один из списка, а не перебирать список шансов
            {
                for (CubicTemplate.SkillInfo skillInfo : entry.getValue()) {
                    if (player.isSkillDisabled(skillInfo.getSkill()))
                        continue;
                    switch (skillInfo.getActionType()) {
                        case ATTACK:
                            if (canAttack())
                                doAttack(player, skillInfo, _template.getDelay());
                            break;
                        case DEBUFF:
                            if (canAttack())
                                doDebuff(player, skillInfo, _template.getDelay());
                            break;
                        case HEAL:
                            doHeal(player, skillInfo, _template.getDelay());
                            break;
                        case CANCEL:
                            doCancel(player, skillInfo, _template.getDelay());
                            break;
                    }
                }
                break;
            }
    }

    private boolean canAttack() {
        if (!(effected instanceof Playable))
            return true;

        Player effectedPlayer = ((Playable) effected).getPlayer();

        if (effectedPlayer.isInCombat() || effectedPlayer.getPvpFlag() > 0)
            return true;

        if (effectedPlayer.isInZone(Zone.ZoneType.battle_zone) || effectedPlayer.isInZone(Zone.ZoneType.SIEGE))
            return true;

        if (effectedPlayer.isAttackingNow())
            return true;

        return effectedPlayer.isInOlympiadMode() || effectedPlayer.isInDuel();

    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public boolean isCancelable() {
        return false;
    }

    public int getId() {
        return _template.getId();
    }

    private class ActionTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (!isActive())
                return;
            if (effected instanceof Player)
                doAction((Player) effected);
        }
    }
}