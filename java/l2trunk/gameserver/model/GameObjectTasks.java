package l2trunk.gameserver.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.network.serverpackets.ExVoteSystemInfo;
import l2trunk.gameserver.network.serverpackets.MagicSkillLaunched;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.Collections;
import java.util.List;

public final class GameObjectTasks {
    public static class DeleteTask extends RunnableImpl {
        private final Creature creature;

        public DeleteTask(Creature creature) {
            this.creature = creature;
        }

        @Override
        public void runImpl() {
            if (creature != null)
                creature.deleteMe();
        }
    }

    // ============================ Таски для L2Player ==============================
    public static class SoulConsumeTask extends RunnableImpl {
        private final Player player;

        public SoulConsumeTask(Player player) {
            this.player = player;
        }

        @Override
        public void runImpl() {
            Player player = this.player;
            if (player == null)
                return;
            player.setConsumedSouls(player.getConsumedSouls() + 1, null);
        }
    }

    /**
     * PvPFlagTask
     */
    public static class PvPFlagTask extends RunnableImpl {
        private final Player player;

        public PvPFlagTask(Player player) {
            this.player = player;
        }

        @Override
        public void runImpl() {
            if (player == null)
                return;

            long diff = Math.abs(System.currentTimeMillis() - player.getlastPvpAttack());
            if (diff > Config.PVP_TIME)
                player.stopPvPFlag();
            else if (diff > Config.PVP_TIME - 20000)
                player.updatePvPFlag(2);
            else
                player.updatePvPFlag(1);
        }
    }

    /**
     * HourlyTask
     */
    public static class HourlyTask extends RunnableImpl {
        private final Player player;

        HourlyTask(Player player) {
            this.player = player;
        }

        @Override
        public void runImpl() {
            if (player == null)
                return;
            // Каждый час в игре оповещаем персонажу сколько часов он играет.
            int hoursInGame = player.getHoursInGame();
            player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_BEEN_PLAYING_FOR_AN_EXTENDED_PERIOD_OF_TIME_S1).addInteger(hoursInGame));
            player.sendPacket(new SystemMessage2(SystemMsg.YOU_OBTAINED_S1_RECOMMENDS).addInteger(player.addRecomLeft()));
        }
    }

    /**
     * RecomBonusTask
     */
    public static class RecomBonusTask extends RunnableImpl {
        private final Player player;

        RecomBonusTask(Player player) {
            this.player = player;
        }

        @Override
        public void runImpl() {
            if (player == null)
                return;
            player.setRecomBonusTime(0);
            player.sendPacket(new ExVoteSystemInfo(player));
        }
    }

    public static class WaterTask extends RunnableImpl {
        private final Player player;

        public WaterTask(Player player) {
            this.player = player;
        }

        @Override
        public void runImpl() {
            if (player == null)
                return;
            if (player.isDead() || !player.isInWater()) {
                player.stopWaterTask();
                return;
            }

            double reduceHp = player.getMaxHp() < 100 ? 1 : player.getMaxHp() / 100.;
            player.reduceCurrentHp(reduceHp, player, null, false, false, true, false, false, false, false);
            player.sendPacket(new SystemMessage2(SystemMsg.YOU_RECEIVED_S1_DAMAGE_BECAUSE_YOU_WERE_UNABLE_TO_BREATHE).addInteger((long) reduceHp));
        }
    }

    public static class KickTask extends RunnableImpl {
        private final Player player;

        public KickTask(Player player) {
            this.player = player;
        }

        @Override
        public void runImpl() {
            if (player == null)
                return;
            player.kick();
        }
    }

    public static class UnJailTask extends RunnableImpl {
        private final Player player;
        private final boolean _msg;

        public UnJailTask(Player player, boolean msg) {
            this.player = player;
            _msg = msg;
        }

        @Override
        public void runImpl() {
            if (player == null) {
                return;
            }

            String[] re = player.getVar("jailedFrom").split(";");
            player.unsetVar("jailedFrom");
            player.unsetVar("jailed");

            player.teleToLocation(17144, 170152, -3504, 0);
            player.setReflection(re.length > 3 ? Integer.parseInt(re[3]) : 0);

            if (_msg) {
                player.sendPacket(new Say2(0, ChatType.TELL, "Jail", "You are now free to go!"));
            }

            if (player.isBlocked()) {// prevent locks
                player.setBlock();
            }
            player.standUp();
        }
    }

    /**
     * EndSitDownTask
     */
    public static class EndSitDownTask extends RunnableImpl {
        private final Player player;

        EndSitDownTask(Player player) {
            this.player = player;
        }

        @Override
        public void runImpl() {
            if (player == null)
                return;
            player.sittingTaskLaunched = false;
            player.getAI().clearNextAction();
        }
    }

    /**
     * EndStandUpTask
     */
    public static class EndStandUpTask extends RunnableImpl {
        private final Player player;

        EndStandUpTask(Player player) {
            this.player = player;
        }

        @Override
        public void runImpl() {
            if (player == null)
                return;
            player.sittingTaskLaunched = false;
            player.setSitting(false);
            if (!player.getAI().setNextIntention())
                player.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
        }
    }

    public static class ReturnTask extends RunnableImpl {
        private final Creature creature;
        private final List<Skill> list;
        private final List<Integer> timeLeft;

        public ReturnTask(Creature creature, List<Skill> list, List<Integer> timeLeft) {
            this.creature = creature;
            this.list = list;
            this.timeLeft = timeLeft;
        }

        @Override
        public void runImpl() {
            int i;
            if ((creature != null) && (list != null) && (!creature.isDead()) && creature instanceof Player && (!((Player)creature).isInOlympiadMode()) && (!((Player)creature).isInObserverMode())) {
                i = 0;
                for (Skill skill : list) {
                    if (skill != null)
                        skill.getEffects(creature, creature, false, false, timeLeft.get(i) * 1000, 1.0, false);
                    i++;
                }
            }
        }
    }
    // ============================ Таски для L2Character ==============================

    /**
     * AltMagicUseTask
     */
    public static class AltMagicUseTask extends RunnableImpl {
        final int skillId;
        private final Creature creature, target;

        public AltMagicUseTask(Creature character, Creature target, int skillId) {
            creature = character;
            this.target = target;
            this.skillId = skillId;
        }

        @Override
        public void runImpl() {
            Creature cha, target;
            if ((cha = creature) == null || (target = this.target) == null)
                return;
            cha.altOnMagicUseTimer(target, skillId);
        }
    }

    public static class CastEndTimeTask extends RunnableImpl {
        private final  Creature character;

        public CastEndTimeTask(Creature character) {
            this.character = character;
        }

        @Override
        public void runImpl() {
            if (character == null)
                return;
            character.onCastEndTime();
        }
    }

    /**
     * HitTask
     */
    public static class HitTask extends RunnableImpl {
        final boolean crit;
        final boolean miss;
        final boolean shld;
        final boolean soulshot;
        final boolean unchargeSS;
        final boolean notify;
        final int damage;
        private final Creature creature, target;

        public HitTask(Creature cha, Creature target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld, boolean unchargeSS, boolean notify) {
            creature = cha;
            this.target = target;
            this.damage = damage;
            this.crit = crit;
            this.shld = shld;
            this.miss = miss;
            this.soulshot = soulshot;
            this.unchargeSS = unchargeSS;
            this.notify = notify;
        }

        @Override
        public void runImpl() {
            Creature character, target;
            if ((character = creature) == null || (target = this.target) == null)
                return;

            if (character.isAttackAborted())
                return;
            // if (GeoEngine.canSeeTarget(character, target, false))
            character.onHitTimer(target, damage, crit, miss, soulshot, shld, unchargeSS);

            if (notify)
                character.getAI().notifyEvent(CtrlEvent.EVT_READY_TO_ACT);
        }
    }

    /**
     * Task launching the function onMagicUseTimer()
     */
    public static class MagicUseTask extends RunnableImpl {
        final boolean _forceUse;
        private final Creature creature;

        public MagicUseTask(Creature cha, boolean forceUse) {
            creature = cha;
            _forceUse = forceUse;
        }

        @Override
        public void runImpl() {
            Creature character = creature;
            if (character == null)
                return;
            Skill castingSkill = character.getCastingSkill();
            Creature castingTarget = character.getCastingTarget();
            if (castingSkill == null || castingTarget == null) {
                character.clearCastVars();
                return;
            }
            character.onMagicUseTimer(castingTarget, castingSkill, _forceUse);
        }
    }

    public static class MagicLaunchedTask extends RunnableImpl {
        final boolean forceUse;
        private final Creature charRef;

        public MagicLaunchedTask(Creature cha, boolean forceUse) {
            charRef = cha;
            this.forceUse = forceUse;
        }

        @Override
        public void runImpl() {
            Creature character = charRef;
            if (character == null)
                return;
            Skill castingSkill = character.getCastingSkill();
            Creature castingTarget = character.getCastingTarget();
            if (castingSkill == null || castingTarget == null) {
                character.clearCastVars();
                return;
            }
            List<Creature> targets = castingSkill.getTargets(character, castingTarget, forceUse);
            character.broadcastPacket(new MagicSkillLaunched(character.objectId(), castingSkill.displayId, castingSkill.getDisplayLevel(), Collections.unmodifiableList(targets)));
        }
    }

    public static class NotifyAITask extends RunnableImpl {
        private final CtrlEvent evt;
        private final Creature creature;
        private final Integer damage;
        private final Creature charRef;

        public NotifyAITask(Creature cha, CtrlEvent evt, Creature creature) {
            this(cha, evt, creature, null);
        }

        public NotifyAITask(Creature cha, CtrlEvent evt, Creature creature, Integer damage) {
            charRef = cha;
            this.evt = evt;
            this.creature = creature;
            this.damage = damage;
        }

        public NotifyAITask(Creature cha, CtrlEvent evt) {
            this(cha, evt, null, null);
        }

        @Override
        public void runImpl() {
            Creature character = charRef;
            if (character == null || !character.hasAI() || !Config.ALLOW_NPC_AIS)
                return;

            if (creature != null)
                if (damage != null)
                    character.getAI().notifyEvent(evt, creature, damage);
                else
                    character.getAI().notifyEvent(evt, creature);
            else character.getAI().notifyEvent(evt);


        }
    }

    /**
     * Task of Checking Skill cast Landing
     **/
    public static class MagicGeoCheckTask extends RunnableImpl {
        private final Creature charRef;

        public MagicGeoCheckTask(Creature cha) {
            charRef = cha;
        }

        @Override
        public void runImpl() {
            Creature character = charRef;
            if (character == null) {
                return;
            }
            Creature castingTarget = character.getCastingTarget();
            if (castingTarget == null) {
                return;
            }
            if (!GeoEngine.canSeeTarget(character, castingTarget, character.isFlying())) {
                return;
            }

            character.skillGeoCheckTask = null;
        }
    }
}