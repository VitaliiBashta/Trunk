package l2trunk.gameserver.model.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.AutoAttackStart;
import l2trunk.gameserver.network.serverpackets.CharInfo;
import l2trunk.gameserver.network.serverpackets.L2GameServerPacket;
import l2trunk.gameserver.network.serverpackets.MyTargetSelected;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class DecoyInstance extends NpcInstance {

    private final Player player;
    private int _lifeTime, timeRemaining;
    private ScheduledFuture<?> _decoyLifeTask, _hateSpam;

    public DecoyInstance(int objectId, NpcTemplate template, Player player, int lifeTime) {
        super(objectId, template);

        this.player = player;
        _lifeTime = lifeTime;
        timeRemaining = _lifeTime;
        int skilllevel = getNpcId() < 13257 ? getNpcId() - 13070 : getNpcId() - 13250;
        _decoyLifeTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new DecoyLifetime(), 1000, 1000);
        _hateSpam = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new HateSpam(5272, skilllevel), 1000, 3000);
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);
        if (_hateSpam != null) {
            _hateSpam.cancel(false);
            _hateSpam = null;
        }
        _lifeTime = 0;
    }

    public void unSummon() {
        if (_decoyLifeTask != null) {
            _decoyLifeTask.cancel(false);
            _decoyLifeTask = null;
        }
        if (_hateSpam != null) {
            _hateSpam.cancel(false);
            _hateSpam = null;
        }
        deleteMe();
    }

    private void decTimeRemaining() {
        timeRemaining -= 1000;
    }

    private int getTimeRemaining() {
        return timeRemaining;
    }

    public int getLifeTime() {
        return _lifeTime;
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return player.isAutoAttackable(attacker);
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return player.isAttackable(attacker);
    }

    @Override
    protected void onDelete() {
        player.setDecoy(null);
        super.onDelete();
    }

    @Override
    public void onAction(Player player, boolean shift) {
        if (player.getTarget() != this) {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelected(objectId(), 0));
        } else if (isAutoAttackable(player))
            player.getAI().Attack(this, false, shift);
    }

    @Override
    public double getColRadius() {
        if (player.isTrasformed()  && player.getTransformationTemplate() != 0)
            return NpcHolder.getTemplate(player.getTransformationTemplate()).collisionRadius;
        return player.getBaseTemplate().collisionRadius;
    }

    @Override
    public double getColHeight() {
        if (player.isTrasformed() && player.getTransformationTemplate() != 0)
            return NpcHolder.getTemplate(player.getTransformationTemplate()).collisionHeight;
        return player.getBaseTemplate().collisionHeight;
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper) {
        if (!isInCombat())
            return List.of(new CharInfo(this));
        else {
            List<L2GameServerPacket> list = new ArrayList<>(2);
            list.add(new CharInfo(this));
            list.add(new AutoAttackStart(objectId));
            return list;
        }
    }

    @Override
    public boolean isInvul() {
        return invul;
    }

    class DecoyLifetime extends RunnableImpl {
        @Override
        public void runImpl() {
            try {
                double newTimeRemaining;
                decTimeRemaining();
                newTimeRemaining = getTimeRemaining();
                if (newTimeRemaining < 0)
                    unSummon();
            } catch (RuntimeException e) {
                LOG.error("Error on Decoy Lifetime End", e);
            }
        }
    }

    class HateSpam extends RunnableImpl {
        private final int skillId;
        private final int skillLvl;


        HateSpam(int skillId, int skillLvl) {
            this.skillId = skillId;
            this.skillLvl = skillLvl;
        }

        @Override
        public void runImpl() {
            try {
                setTarget(DecoyInstance.this);
                doCast(skillId, skillLvl, DecoyInstance.this, true);
            } catch (RuntimeException e) {
                LOG.error("Error while Changing Target to DecoyInstance", e);
            }
        }
    }
}