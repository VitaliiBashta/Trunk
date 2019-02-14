package l2trunk.gameserver.model;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.listener.actor.OnAttackListener;
import l2trunk.gameserver.listener.actor.OnMagicUseListener;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.skills.AbnormalEffect;
import l2trunk.gameserver.skills.EffectType;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.stats.funcs.FuncOwner;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.taskmanager.EffectTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class Effect extends RunnableImpl implements Comparable<Effect>, FuncOwner {
    protected static final Logger _log = LoggerFactory.getLogger(Effect.class);
    // A condition in which the scheduled task runs effect
    private static final int SUSPENDED = -1;

    private static final int STARTING = 0;
    private static final int STARTED = 1;
    private static final int ACTING = 2;
    private static final int FINISHING = 3;
    private static final int FINISHED = 4;

    /**
     * Applies
     */
    public final Creature effector;
    /**
     * One on whom the effect is applied
     */
    public final Creature effected;

    public final Skill skill;
    public final int displayId;
    public final int displayLevel;
    protected final EffectTemplate template;
    // the value of an update
    private final double value;
    // the current state
    private final AtomicInteger state;
    // counter
    private int count;
    // period, milliseconds
    private long period;
    private long _startTimeMillis;
    private long duration;
    private boolean inUse = false;
    private Effect next = null;
    private boolean active = false;
    private Future<?> _effectTask;
    private ActionDispelListener _listener;

    protected Effect(Env env, EffectTemplate template) {
        skill = env.skill;
        effector = env.character;
        effected = env.target;

        this.template = template;
        value = template.value;
        count = template.getCount();
        period = template.getPeriod();

        duration = period * count;

        displayId = template.displayId != 0 ? template.displayId : skill.displayId;
        displayLevel = template.displayLevel != 0 ? template.displayLevel : skill.getDisplayLevel();

        state = new AtomicInteger(STARTING);
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long time) {
        period = time;
        duration = period * count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
        duration = period * this.count;
    }

    public boolean isOneTime() {
        return period == 0;
    }

    /**
     * Returns the start time of the effect, if the time is not set, it returns the current
     */
    public long getStartTime() {
        if (_startTimeMillis == 0L)
            return System.currentTimeMillis();
        return _startTimeMillis;
    }

    /**
     * Returns the total duration of the effect in milliseconds.
     */
    public long getTime() {
        return System.currentTimeMillis() - getStartTime();
    }

    /**
     * Returns the length of the effect in milliseconds.
     */
    private long getDuration() {
        return duration;
    }

    /**
     * Returns the remaining time in seconds.
     */
    public int getTimeLeft() {
        return (int) ((getDuration() - getTime()) / 1000L);
    }

    /**
     * Returns true, if there is time for the effect
     */
    private boolean isTimeLeft() {
        return getDuration() - getTime() > 0L;
    }

    public final boolean isInUse() {
        return inUse;
    }

    final void setInUse(boolean inUse) {
        this.inUse = inUse;
    }

    protected final boolean isActive() {
        return active;
    }

    /**
     * For inactive effect it is not called onActionTime.
     */
    private void setActive(boolean set) {
        active = set;
    }

    public EffectTemplate getTemplate() {
        return template;
    }

    public String getStackType() {
        return template.stackType;
    }

    public String getStackType2() {
        return template.stackType2;
    }

    public boolean checkStackType(String param) {
        return template.stackType.equalsIgnoreCase(param) || template.stackType2.equalsIgnoreCase(param);
    }

    public int getStackOrder() {
        return template.stackOrder;
    }

//    public Skill skill() {
//        return skill;
//    }

    public final double calc() {
        return value;
    }

    private boolean isEnded() {
        return state.get() == FINISHED || state.get() == FINISHING;
    }

    private boolean setState(int oldState, int newState) {
        return state.compareAndSet(oldState, newState);
    }

    public boolean checkCondition() {
        return true;
    }

    /**
     * Notify started
     */
    protected void onStart() {
        effected.addStatFuncs(getStatFuncs());
        effected.addTriggers(getTemplate());
        if (getTemplate()._abnormalEffect != AbnormalEffect.NULL)
            effected.startAbnormalEffect(getTemplate()._abnormalEffect);
        else if (getEffectType().getAbnormal() != null)
            effected.startAbnormalEffect(getEffectType().getAbnormal());
        if (getTemplate()._abnormalEffect2 != AbnormalEffect.NULL)
            effected.startAbnormalEffect(getTemplate()._abnormalEffect2);
        if (getTemplate()._abnormalEffect3 != AbnormalEffect.NULL)
            effected.startAbnormalEffect(getTemplate()._abnormalEffect3);
        if (template._cancelOnAction)
            effected.addListener(_listener = new ActionDispelListener());
        if (effected instanceof Player && !skill.canUseTeleport)
            ((Player) effected).getPlayerAccess().UseTeleport = false;
    }

    /**
     * Return true for continuation of this effect
     */
    protected boolean onActionTime() {
        return false;
    }

    /**
     * Cancel the effect in the the abnormal effect map of the effected L2Character.<BR><BR>
     */
    protected void onExit() {
        effected.removeStatsOwner(this);
        effected.removeStatFuncs(getStatFuncs());
        effected.removeTriggers(getTemplate());
        if (getTemplate()._abnormalEffect != AbnormalEffect.NULL)
            effected.stopAbnormalEffect(getTemplate()._abnormalEffect);
        else if (getEffectType().getAbnormal() != null)
            effected.stopAbnormalEffect(getEffectType().getAbnormal());
        if (getTemplate()._abnormalEffect2 != AbnormalEffect.NULL)
            effected.stopAbnormalEffect(getTemplate()._abnormalEffect2);
        if (getTemplate()._abnormalEffect3 != AbnormalEffect.NULL)
            effected.stopAbnormalEffect(getTemplate()._abnormalEffect3);
        if (template._cancelOnAction)
            effected.removeListener(_listener);
        if (effected instanceof Player && getStackType().equals(EffectTemplate.HP_RECOVER_CAST))
            effected.sendPacket(new ShortBuffStatusUpdate());
        if (effected instanceof Player && !skill.canUseTeleport && !((Player) effected).getPlayerAccess().UseTeleport)
            ((Player) effected).getPlayerAccess().UseTeleport = true;
    }

    private void stopEffectTask() {
        if (_effectTask != null)
            _effectTask.cancel(false);
    }

    private void startEffectTask() {
        if (_effectTask == null) {
            _startTimeMillis = System.currentTimeMillis();
            _effectTask = EffectTaskManager.getInstance().scheduleAtFixedRate(this, period, period);
        }
    }

    /**
     * Adds a list of effects in the event of the success of start method is called
     */
    public final void schedule() {
        if (effected == null)
            return;

        if (!checkCondition())
            return;

        effected.getEffectList().addEffect(this);
    }

    /**
     * Transfer Effect in "background" mode, the effect can be started by schedule
     */
    private void suspend() {
        // The effect is created, run the task in the background
        if (setState(STARTING, SUSPENDED))
            startEffectTask();
        else if (setState(STARTED, SUSPENDED) || setState(ACTING, SUSPENDED)) {
            synchronized (this) {
                if (inUse) {
                    inUse = false;
                    active = false;
                    onExit();
                }
            }
            effected.getEffectList().removeEffect(this);
        }
    }

    /**
     * Starts the task effect, if the effect was successfully added to the list
     */
    public final void start() {
        if (setState(STARTING, STARTED)) {
            synchronized (this) {
                if (isInUse()) {
                    setActive(true);
                    onStart();
                    startEffectTask();
                }
            }
        }

        run();
    }

    @Override
    public final void runImpl() {
        if (setState(STARTED, ACTING)) {
            // Display a message only for the first effect of the skill
            if (!skill.hideStartMessage && effected.getEffectList().getEffectsCountForSkill(skill.id) == 1)
                effected.sendPacket(new SystemMessage2(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(displayId, displayLevel));

            return;
        }

        if (state.get() == SUSPENDED) {
            if (isTimeLeft()) {
                count--;
                if (isTimeLeft())
                    return;
            }

            exit();
            return;
        }

        if (state.get() == ACTING)
            if (isTimeLeft()) {
                count--;
                if ((!isActive() || onActionTime()) && isTimeLeft())
                    return;
            }

        if (setState(ACTING, FINISHING))
            setInUse(false);

        if (setState(FINISHING, FINISHED)) {
            synchronized (this) {
                setActive(false);
                stopEffectTask();
                onExit();
            }

            // Adding the effect of the next scheduled
            Effect next = getNext();
            if (next != null)
                if (next.setState(SUSPENDED, STARTING))
                    next.schedule();

            if (skill.delayedEffect > 0) {
                Skill delayErrects = SkillTable.INSTANCE.getInfo(skill.delayedEffect);
                if (delayErrects != null) {
                    delayErrects.getEffects(effector, effected);
                }
            }
            boolean msg = !isHidden() && effected.getEffectList().getEffectsCountForSkill(skill.id) == 1;

            effected.getEffectList().removeEffect(this);

            // Display a message only for the last remaining effect of the skill
            if (msg)
                effected.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_WORN_OFF).addSkillName(displayId, displayLevel));
        }
    }

    /**
     * Completes the effect and all related removes the effect from the effects list
     */
    public final void exit() {
        Effect next = getNext();
        if (next != null)
            next.exit();
        removeNext();

        // The effect is scheduled to start, remove
        if (setState(STARTING, FINISHED))
            effected.getEffectList().removeEffect(this);
            // The effect of working in the "background", stop the task scheduler
        else if (setState(SUSPENDED, FINISHED))
            stopEffectTask();
        else if (setState(STARTED, FINISHED) || setState(ACTING, FINISHED)) {
            synchronized (this) {
                if (isInUse()) {
                    setInUse(false);
                    setActive(false);
                    stopEffectTask();
                    onExit();
                }
            }
            effected.getEffectList().removeEffect(this);
        }
    }

    /**
     * Queued effect
     */
    private void scheduleNext(Effect e) {
        if (e == null || e.isEnded())
            return;

        Effect next = getNext();
        if (next != null && !next.maybeScheduleNext(e))
            return;
        this.next = e;
    }

    public Effect getNext() {
        return next;
    }

    private void removeNext() {
        next = null;
    }

    /**
     * @return false - ignore the effect of a new, true - to use the new effect
     */
    boolean maybeScheduleNext(Effect newEffect) {
        if (newEffect.getStackOrder() < getStackOrder()) // new effect is weaker
        {
            if (newEffect.getTimeLeft() > getTimeLeft()) // new effect is longer
            {
                newEffect.suspend();
                scheduleNext(newEffect); // try to attach a new effect in all
            }

            return false; // weaker effect is always ignored, even if you do not hit the turn
        } else // if the old one is not long, just stop it
            if (newEffect.getTimeLeft() >= getTimeLeft()) {
                // inherit the old green, if it's worth
                if (getNext() != null && getNext().getTimeLeft() > newEffect.getTimeLeft()) {
                    newEffect.scheduleNext(getNext());
                    // Schendule disconnect from the current
                    removeNext();
                }
                exit();
            } else {
                // если новый короче то зашедулить старый
                suspend();
                newEffect.scheduleNext(this);
            }

        return true;
    }

    protected List<Func> getStatFuncs() {
        return getTemplate().getStatFuncs(this);
    }

    void addIcon(AbnormalStatusUpdate mi) {
        if (!isActive() || isHidden())
            return;
        int duration = skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        mi.addEffect(displayId, displayLevel, duration);
    }

    public void addPartySpelledIcon(PartySpelled ps) {
        if (!isActive() || isHidden())
            return;
        int duration = skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        ps.addPartySpelledEffect(displayId, displayLevel, duration);
    }

    public void addOlympiadSpelledIcon(Player player, ExOlympiadSpelledInfo os) {
        if (!isActive() || isHidden())
            return;
        int duration = skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        os.addSpellRecivedPlayer(player);
        os.addEffect(displayId, displayLevel, duration);
    }

    protected int getLevel() {
        return skill.level;
    }

    public EffectType getEffectType() {
        return getTemplate().effecttype;
    }

    protected boolean isHidden() {
        return displayId < 0;
    }

    @Override
    public int compareTo(Effect obj) {
        if (obj.equals(this))
            return 0;
        return 1;
    }

    public boolean isSaveable() {
        return template.isSaveable(skill.isSaveable()) && getTimeLeft() >= Config.ALT_SAVE_EFFECTS_REMAINING_TIME;
    }


    public boolean isCancelable() {
        return template.isCancelable(skill.isCancelable());
    }

    @Override
    public String toString() {
        return "Skill: " + skill + ", state: " + state.get() + ", inUse: " + inUse + ", active : " + active;
    }

    @Override
    public boolean isFuncEnabled() {
        return inUse;
    }


    public boolean isOffensive() {
        return template.isOffensive(skill.isOffensive);
    }

    private class ActionDispelListener implements OnAttackListener, OnMagicUseListener {
        @Override
        public void onMagicUse(Creature actor, Skill skill, Creature target, boolean alt) {
            exit();
        }

        @Override
        public void onAttack(Creature actor, Creature target) {
            exit();
        }
    }
}