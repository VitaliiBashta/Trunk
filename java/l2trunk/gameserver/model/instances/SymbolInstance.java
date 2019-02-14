package l2trunk.gameserver.model.instances;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectTasks;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.taskmanager.EffectTaskManager;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public final class SymbolInstance extends NpcInstance {
    private final Creature _owner;
    private final Skill skill;
    private ScheduledFuture<?> _targetTask;
    private ScheduledFuture<?> _destroyTask;

    public SymbolInstance(int objectId, NpcTemplate template, Creature owner, Skill skill) {
        super(objectId, template);
        _owner = owner;
        this.skill = skill;

        setReflection(owner.getReflection());
        setLevel(owner.getLevel());
        setTitle(owner.getName());
    }

    private Creature getOwner() {
        return _owner;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        _destroyTask = ThreadPoolManager.INSTANCE.schedule(new GameObjectTasks.DeleteTask(this), 120000L);

        _targetTask = EffectTaskManager.getInstance().scheduleAtFixedRate(() ->
                getAroundCharacters(200, 200).forEach(target -> {
                    if (skill.checkTarget(_owner, target, null, false, false) == null) {
                        List<Creature> targets = new ArrayList<>();

                        if (!skill.isAoE())
                            targets.add(target);
                        else
                            targets.addAll(getAroundCharacters(skill.skillRadius, 128)
                                    .filter(t -> skill.checkTarget(_owner, t, null, false, false) == null)
                                    .collect(Collectors.toList()));
                        targets.add(target);

                        skill.useSkill(SymbolInstance.this, targets);
                    }
                }), 1000, Rnd.get(4000, 7000));
    }

    @Override
    protected void onDelete() {
        if (_destroyTask != null)
            _destroyTask.cancel(false);
        _destroyTask = null;
        if (_targetTask != null)
            _targetTask.cancel(false);
        _targetTask = null;
        super.onDelete();
    }

    @Override
    public int getPAtk(Creature target) {
        Creature owner = getOwner();
        return owner == null ? 0 : owner.getPAtk(target);
    }

    @Override
    public int getMAtk(Creature target, Skill skill) {
        Creature owner = getOwner();
        return owner == null ? 0 : owner.getMAtk(target, skill);
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return false;
    }

    @Override
    public boolean isInvul() {
        return true;
    }

    @Override
    public boolean isFearImmune() {
        return true;
    }

    @Override
    public boolean isParalyzeImmune() {
        return true;
    }

    @Override
    public boolean isLethalImmune() {
        return true;
    }

    @Override
    public void showChatWindow(Player player, int val) {
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
    }

    @Override
    public void onAction(Player player, boolean shift) {
        player.sendActionFailed();
    }

    @Deprecated
    @Override
    public Clan getClan() {
        return null;
    }
}
