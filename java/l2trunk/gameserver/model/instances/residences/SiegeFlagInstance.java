package l2trunk.gameserver.model.instances.residences;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public class SiegeFlagInstance extends NpcInstance {
    private SiegeClanObject owner;
    private long _lastAnnouncedAttackedTime = 0;

    public SiegeFlagInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(false);
    }

    @Override
    public String getName() {
        return owner.getClan().getName();
    }

    @Override
    public Clan getClan() {
        return owner.getClan();
    }

    public void setClan(SiegeClanObject owner) {
        this.owner = owner;
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        Player player = attacker.getPlayer();
        if (player == null || isInvul())
            return false;
        Clan clan = player.getClan();
        return clan == null || owner.getClan() != clan;
    }

    @Override
    public boolean isAttackable(Creature attacker) {
        return true;
    }

    @Override
    protected void onDeath(Creature killer) {
        owner.setFlag(null);
        super.onDeath(killer);
    }

    @Override
    protected void onReduceCurrentHp(final double damage, final Creature attacker, Skill skill, final boolean awake, final boolean standUp, boolean directHp) {
        if (System.currentTimeMillis() - _lastAnnouncedAttackedTime > 120000) {
            _lastAnnouncedAttackedTime = System.currentTimeMillis();
            owner.getClan().broadcastToOnlineMembers(SystemMsg.YOUR_BASE_IS_BEING_ATTACKED);
        }

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp);
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }

    @Override
    public boolean isInvul() {
        return invul;
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
    public boolean isHealBlocked() {
        return true;
    }

    @Override
    public boolean isEffectImmune() {
        return true;
    }
}