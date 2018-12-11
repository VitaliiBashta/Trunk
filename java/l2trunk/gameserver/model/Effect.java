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
    protected final Creature _effector;
    /**
     * One on whom the effect is applied
     */
    protected final Creature effected;

    protected final Skill _skill;
    protected final EffectTemplate template;
    private final int _displayId;
    private final int _displayLevel;
    // the value of an update
    private final double _value;
    // the current state
    private final AtomicInteger _state;
    // counter
    private int _count;
    // period, milliseconds
    private long _period;
    private long _startTimeMillis;
    private long _duration;
    private boolean _inUse = false;
    private Effect next = null;
    private boolean _active = false;
    private Future<?> _effectTask;
    private ActionDispelListener _listener;

    protected Effect(Env env, EffectTemplate template) {
        _skill = env.skill;
        _effector = env.character;
        effected = env.target;

        this.template = template;
        _value = template._value;
        _count = template.getCount();
        _period = template.getPeriod();

        _duration = _period * _count;

        _displayId = template._displayId != 0 ? template._displayId : _skill.getDisplayId();
        _displayLevel = template._displayLevel != 0 ? template._displayLevel : _skill.getDisplayLevel();

        _state = new AtomicInteger(STARTING);
    }

    public long getPeriod() {
        return _period;
    }

    public void setPeriod(long time) {
        _period = time;
        _duration = _period * _count;
    }

    public int getCount() {
        return _count;
    }

    public void setCount(int count) {
        _count = count;
        _duration = _period * _count;
    }

    public boolean isOneTime() {
        return _period == 0;
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
        return _duration;
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

    public boolean isInUse() {
        return _inUse;
    }

    public void setInUse(boolean inUse) {
        _inUse = inUse;
    }

    protected boolean isActive() {
        return _active;
    }

    /**
     * For inactive effect it is not called onActionTime.
     */
    private void setActive(boolean set) {
        _active = set;
    }

    public EffectTemplate getTemplate() {
        return template;
    }

    public String getStackType() {
        return getTemplate()._stackType;
    }

    public String getStackType2() {
        return getTemplate()._stackType2;
    }

    public boolean checkStackType(String param) {
        return getStackType().equalsIgnoreCase(param) || getStackType2().equalsIgnoreCase(param);
    }

    public boolean checkStackType(Effect param) {
        return checkStackType(param.getStackType()) || checkStackType(param.getStackType2());
    }

    public int getStackOrder() {
        return getTemplate()._stackOrder;
    }

    public Skill getSkill() {
        return _skill;
    }

    public Creature getEffector() {
        return _effector;
    }

    public Creature getEffected() {
        return effected;
    }

    public double calc() {
        return _value;
    }

    private boolean isEnded() {
        return isFinished() || isFinishing();
    }

    private boolean isFinishing() {
        return getState() == FINISHING;
    }

    private boolean isFinished() {
        return getState() == FINISHED;
    }

    private int getState() {
        return _state.get();
    }

    private boolean setState(int oldState, int newState) {
        return _state.compareAndSet(oldState, newState);
    }

    public boolean checkCondition() {
        return true;
    }

    /**
     * Notify started
     */
    protected void onStart() {
        getEffected().addStatFuncs(getStatFuncs());
        getEffected().addTriggers(getTemplate());
        if (getTemplate()._abnormalEffect != AbnormalEffect.NULL)
            getEffected().startAbnormalEffect(getTemplate()._abnormalEffect);
        else if (getEffectType().getAbnormal() != null)
            getEffected().startAbnormalEffect(getEffectType().getAbnormal());
        if (getTemplate()._abnormalEffect2 != AbnormalEffect.NULL)
            getEffected().startAbnormalEffect(getTemplate()._abnormalEffect2);
        if (getTemplate()._abnormalEffect3 != AbnormalEffect.NULL)
            getEffected().startAbnormalEffect(getTemplate()._abnormalEffect3);
        if (template._cancelOnAction)
            getEffected().addListener(_listener = new ActionDispelListener());
        if (getEffected().isPlayer() && !getSkill().canUseTeleport())
            getEffected().getPlayer().getPlayerAccess().UseTeleport = false;
    }

    /**
     * Return true for continuation of this effect
     */
    protected abstract boolean onActionTime();

    /**
     * Cancel the effect in the the abnormal effect map of the effected L2Character.<BR><BR>
     */
    protected void onExit() {
        getEffected().removeStatsOwner(this);
        getEffected().removeStatFuncs(getStatFuncs());
        getEffected().removeTriggers(getTemplate());
        if (getTemplate()._abnormalEffect != AbnormalEffect.NULL)
            getEffected().stopAbnormalEffect(getTemplate()._abnormalEffect);
        else if (getEffectType().getAbnormal() != null)
            getEffected().stopAbnormalEffect(getEffectType().getAbnormal());
        if (getTemplate()._abnormalEffect2 != AbnormalEffect.NULL)
            getEffected().stopAbnormalEffect(getTemplate()._abnormalEffect2);
        if (getTemplate()._abnormalEffect3 != AbnormalEffect.NULL)
            getEffected().stopAbnormalEffect(getTemplate()._abnormalEffect3);
        if (template._cancelOnAction)
            getEffected().removeListener(_listener);
        if (getEffected().isPlayer() && getStackType().equals(EffectTemplate.HP_RECOVER_CAST))
            getEffected().sendPacket(new ShortBuffStatusUpdate());
        if (getEffected().isPlayer() && !getSkill().canUseTeleport() && !getEffected().getPlayer().getPlayerAccess().UseTeleport)
            getEffected().getPlayer().getPlayerAccess().UseTeleport = true;
    }

    private void stopEffectTask() {
        if (_effectTask != null)
            _effectTask.cancel(false);
    }

    private void startEffectTask() {
        if (_effectTask == null) {
            _startTimeMillis = System.currentTimeMillis();
            _effectTask = EffectTaskManager.getInstance().scheduleAtFixedRate(this, _period, _period);
        }
    }

    /**
     * Adds a list of effects in the event of the success of start method is called
     */
    public final void schedule() {
        Creature effected = getEffected();
        if (effected == null)
            return;

        if (!checkCondition())
            return;

        getEffected().getEffectList().addEffect(this);
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
                if (isInUse()) {
                    setInUse(false);
                    setActive(false);
                    onExit();
                }
            }
            getEffected().getEffectList().removeEffect(this);
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
            if (!getSkill().isHideStartMessage() && getEffected().getEffectList().getEffectsCountForSkill(getSkill().getId()) == 1)
                getEffected().sendPacket(new SystemMessage2(SystemMsg.S1S_EFFECT_CAN_BE_FELT).addSkillName(_displayId, _displayLevel));

            return;
        }

        if (getState() == SUSPENDED) {
            if (isTimeLeft()) {
                _count--;
                if (isTimeLeft())
                    return;
            }

            exit();
            return;
        }

        if (getState() == ACTING)
            if (isTimeLeft()) {
                _count--;
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

            if (getSkill().getDelayedEffect() > 0) {
                Skill delayErrects = SkillTable.INSTANCE.getInfo(getSkill().getDelayedEffect(), 1);
                if (delayErrects != null) {
                    delayErrects.getEffects(_effector, effected, false, false);
                }
            }
            boolean msg = !isHidden() && getEffected().getEffectList().getEffectsCountForSkill(getSkill().getId()) == 1;

            getEffected().getEffectList().removeEffect(this);

            // Display a message only for the last remaining effect of the skill
            if (msg)
                getEffected().sendPacket(new SystemMessage2(SystemMsg.S1_HAS_WORN_OFF).addSkillName(_displayId, _displayLevel));
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
            getEffected().getEffectList().removeEffect(this);
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
            getEffected().getEffectList().removeEffect(this);
        }
    }

    /**
     * Queued effect
     *
     * @param e
     * @return true, if the effect is queued
     */
    private boolean scheduleNext(Effect e) {
        if (e == null || e.isEnded())
            return false;

        Effect next = getNext();
        if (next != null && !next.maybeScheduleNext(e))
            return false;

        this.next = e;

        return true;
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
    public boolean maybeScheduleNext(Effect newEffect) {
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
            } else
            // если новый короче то зашедулить старый
            {
                suspend();
                newEffect.scheduleNext(this);
            }

        return true;
    }

    protected List<Func> getStatFuncs() {
        return getTemplate().getStatFuncs(this);
    }

    public void addIcon(AbnormalStatusUpdate mi) {
        if (!isActive() || isHidden())
            return;
        int duration = _skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        mi.addEffect(_displayId, _displayLevel, duration);
    }

    public void addPartySpelledIcon(PartySpelled ps) {
        if (!isActive() || isHidden())
            return;
        int duration = _skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        ps.addPartySpelledEffect(_displayId, _displayLevel, duration);
    }

    public void addOlympiadSpelledIcon(Player player, ExOlympiadSpelledInfo os) {
        if (!isActive() || isHidden())
            return;
        int duration = _skill.isToggle() ? AbnormalStatusUpdate.INFINITIVE_EFFECT : getTimeLeft();
        os.addSpellRecivedPlayer(player);
        os.addEffect(_displayId, _displayLevel, duration);
    }

    protected int getLevel() {
        return _skill.getLevel();
    }

    public EffectType getEffectType() {
        return getTemplate()._effectType;
    }

    protected boolean isHidden() {
        return _displayId < 0;
    }

    @Override
    public int compareTo(Effect obj) {
        if (obj.equals(this))
            return 0;
        return 1;
    }

    public boolean isSaveable() {
        return template.isSaveable(getSkill().isSaveable()) && getTimeLeft() >= Config.ALT_SAVE_EFFECTS_REMAINING_TIME;
    }

    public int getDisplayId() {
        return _displayId;
    }

    public int getDisplayLevel() {
        return _displayLevel;
    }

    public boolean isCancelable() {
        return template.isCancelable(getSkill().isCancelable());
    }

    @Override
    public String toString() {
        return "Skill: " + _skill + ", state: " + getState() + ", inUse: " + _inUse + ", active : " + _active;
    }

    @Override
    public boolean isFuncEnabled() {
        return isInUse();
    }

    @Override
    public boolean overrideLimits() {
        return false;
    }

    public boolean isOffensive() {
        return template.isOffensive(getSkill().isOffensive());
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