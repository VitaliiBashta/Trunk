package l2trunk.gameserver.model.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectTasks;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Skill.SkillTargetType;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.MyTargetSelected;
import l2trunk.gameserver.network.serverpackets.NpcInfo;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.taskmanager.EffectTaskManager;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public final class TrapInstance extends NpcInstance {
    private final Creature owner;
    private final Skill skill;
    private ScheduledFuture<?> _targetTask;
    private ScheduledFuture<?> _destroyTask;
    private boolean detected;

    public TrapInstance(int objectId, NpcTemplate template, Creature owner, int skillId) {
        this(objectId, template, owner, skillId, owner.getLoc());
    }

    public TrapInstance(int objectId, NpcTemplate template, Creature owner, int skillId, Location loc) {
        super(objectId, template);
        this.owner = owner;
        this.skill = SkillTable.INSTANCE.getInfo(skillId);

        setReflection(owner.getReflection());
        setLevel(owner.getLevel());
        setTitle(owner.getName());
        setLoc(loc);
    }

    private Creature getOwner() {
        return owner;
    }

    @Override
    protected void onSpawn() {
        super.onSpawn();

        _destroyTask = ThreadPoolManager.INSTANCE.schedule(new GameObjectTasks.DeleteTask(this), 120000L);

        _targetTask = EffectTaskManager.getInstance().scheduleAtFixedRate(new CastTask(this), 250L, 250L);
    }

    @Override
    public void broadcastCharInfo() {
        if (detected) super.broadcastCharInfo();
    }

    @Override
    protected void onDelete() {
        Creature owner = getOwner();
        if (owner instanceof Player)
            ((Player) owner).removeTrap(this);
        if (_destroyTask != null)
            _destroyTask.cancel(false);
        _destroyTask = null;
        if (_targetTask != null)
            _targetTask.cancel(false);
        _targetTask = null;
        super.onDelete();
    }

    public void setDetected(boolean detected) {
        this.detected = detected;
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
    public void showChatWindow(Player player, String filename, Object... replace) {
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (player.getTarget() != this) {
            player.setTarget(this);
            if (player.getTarget() == this)
                player.sendPacket(new MyTargetSelected(objectId(), player.getLevel()));
        }
        player.sendActionFailed();
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        // если не обезврежена и не овнер, ниче не показываем
        if (!detected && getOwner() != forPlayer)
            return List.of();

        return List.of(new NpcInfo(this, forPlayer));
    }

    private static class CastTask extends RunnableImpl {
        private final TrapInstance trap;

        CastTask(TrapInstance trap) {
            this.trap = trap;
        }

        @Override
        public void runImpl() {
            if (trap == null)
                return;

            Creature owner = trap.getOwner();
            if (owner == null)
                return;

            trap.getAroundCharacters(200, 200)
                    .filter(target -> target != owner)
                    .findFirst().ifPresent(target -> {
                if (trap.skill.checkTarget(owner, target, null, false, false) == null) {
                    List<Creature> targets;
                    if (trap.skill.targetType != SkillTargetType.TARGET_AREA)
                        targets = List.of(target);
                    else
                        targets = trap.getAroundCharacters(trap.skill.skillRadius, 128)
                                .filter(t -> trap.skill.checkTarget(owner, t, null, false, false) == null)
                                .collect(Collectors.toList());

                    trap.skill.useSkill(trap, targets);
                    if (target instanceof Player)
                        ((Player)target).sendMessage(new CustomMessage("common.Trap"));
                    trap.deleteMe();
                }
            });
        }
    }
}