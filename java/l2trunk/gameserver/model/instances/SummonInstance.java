package l2trunk.gameserver.model.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SetSummonRemainTime;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.concurrent.Future;

public final class SummonInstance extends Summon {
    private final int CYCLE = 5000; // in millis
    private final int _summonSkillId;
    private final int _itemConsumeIdInTime;
    private final int _itemConsumeCountInTime;
    private final int _itemConsumeDelay;
    private final int _maxLifetime;
    private double _expPenalty = 0;
    private Future<?> disappearTask;
    private int _consumeCountdown;
    private int _lifetimeCountdown;

    public SummonInstance(int objectId, NpcTemplate template, Player owner, int lifetime, int consumeid, int consumecount, int consumedelay, Skill skill) {
        super(objectId, template, owner);
        setName(template.name);
        _lifetimeCountdown = _maxLifetime = lifetime;
        _itemConsumeIdInTime = consumeid;
        _itemConsumeCountInTime = consumecount;
        _consumeCountdown = _itemConsumeDelay = consumedelay;
        _summonSkillId = skill.displayId;
        disappearTask = ThreadPoolManager.INSTANCE.schedule(new Lifetime(), CYCLE);
    }

    @Override
    public final int getLevel() {
        return getTemplate() != null ? getTemplate().level : 0;
    }

    @Override
    public int getSummonType() {
        return 1;
    }

    @Override
    public int getCurrentFed() {
        return _lifetimeCountdown;
    }

    @Override
    public int getMaxFed() {
        return _maxLifetime;
    }

    @Override
    public double getExpPenalty() {
        return _expPenalty;
    }

    public void setExpPenalty(double expPenalty) {
        _expPenalty = expPenalty;
    }

    @Override
    protected void onDeath(Creature killer) {
        super.onDeath(killer);

        saveEffects();

        if (disappearTask != null) {
            disappearTask.cancel(false);
            disappearTask = null;
        }
    }

    private int getItemConsumeIdInTime() {
        return _itemConsumeIdInTime;
    }

    private int getItemConsumeCountInTime() {
        return _itemConsumeCountInTime;
    }

    private synchronized void stopDisappear() {
        if (disappearTask != null) {
            disappearTask.cancel(false);
            disappearTask = null;
        }
    }

    @Override
    public void unSummon() {
        stopDisappear();
        super.unSummon();
    }

    @Override
    public void displayGiveDamageMessage(Creature target, int damage, boolean crit, boolean miss, boolean shld, boolean magic) {
        if (owner == null)
            return;
        if (crit)
            owner.sendPacket(SystemMsg.SUMMONED_MONSTERS_CRITICAL_HIT);
        if (miss)
            owner.sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
        else if (!target.isInvul())
            owner.sendPacket(new SystemMessage(SystemMessage.C1_HAS_GIVEN_C2_DAMAGE_OF_S3).addName(this).addName(target).addNumber(damage));
    }

    @Override
    public void displayReceiveDamageMessage(Creature attacker, int damage) {
        owner.sendPacket(new SystemMessage(SystemMessage.C1_HAS_RECEIVED_DAMAGE_OF_S3_FROM_C2).addName(this).addName(attacker).addNumber((long) damage));
    }

    @Override
    public int getEffectIdentifier() {
        return _summonSkillId;
    }

    @Override
    public void onAction(Player player, boolean shift) {
        super.onAction(player, shift);
        if (shift) {
            if (!player.getPlayerAccess().CanViewChar)
                return;

            String dialog;
            dialog = HtmCache.INSTANCE.getNotNull("scripts/actions/admin.L2SummonInstance.onActionShift.htm", player);
            dialog = dialog.replaceFirst("%name%", String.valueOf(getName()));
            dialog = dialog.replaceFirst("%level%", String.valueOf(getLevel()));
            dialog = dialog.replaceFirst("%class%", getClass().getSimpleName().replaceFirst("L2", "").replaceFirst("Instance", ""));
            dialog = dialog.replaceFirst("%xyz%", getLoc().x + " " + getLoc().y + " " + getLoc().z);
            dialog = dialog.replaceFirst("%heading%", String.valueOf(getLoc().h));

            dialog = dialog.replaceFirst("%owner%", owner.getName());
            dialog = dialog.replaceFirst("%ownerId%", String.valueOf(owner.objectId()));

            dialog = dialog.replaceFirst("%npcId%", String.valueOf(getNpcId()));
            dialog = dialog.replaceFirst("%expPenalty%", String.valueOf(getExpPenalty()));

            dialog = dialog.replaceFirst("%maxHp%", String.valueOf(getMaxHp()));
            dialog = dialog.replaceFirst("%maxMp%", String.valueOf(getMaxMp()));
            dialog = dialog.replaceFirst("%currHp%", String.valueOf((int) getCurrentHp()));
            dialog = dialog.replaceFirst("%currMp%", String.valueOf((int) getCurrentMp()));

            dialog = dialog.replaceFirst("%pDef%", String.valueOf(getPDef(null)));
            dialog = dialog.replaceFirst("%mDef%", String.valueOf(getMDef(null, null)));
            dialog = dialog.replaceFirst("%pAtk%", String.valueOf(getPAtk(null)));
            dialog = dialog.replaceFirst("%mAtk%", String.valueOf(getMAtk(null, null)));
            dialog = dialog.replaceFirst("%accuracy%", String.valueOf(getAccuracy()));
            dialog = dialog.replaceFirst("%evasionRate%", String.valueOf(getEvasionRate(null)));
            dialog = dialog.replaceFirst("%crt%", String.valueOf(getCriticalHit(null, null)));
            dialog = dialog.replaceFirst("%runSpeed%", String.valueOf(getRunSpeed()));
            dialog = dialog.replaceFirst("%walkSpeed%", String.valueOf(getWalkSpeed()));
            dialog = dialog.replaceFirst("%pAtkSpd%", String.valueOf(getPAtkSpd()));
            dialog = dialog.replaceFirst("%mAtkSpd%", String.valueOf(getMAtkSpd()));
            dialog = dialog.replaceFirst("%dist%", String.valueOf((int) getRealDistance(player)));

            dialog = dialog.replaceFirst("%STR%", String.valueOf(getSTR()));
            dialog = dialog.replaceFirst("%DEX%", String.valueOf(getDEX()));
            dialog = dialog.replaceFirst("%CON%", String.valueOf(getCON()));
            dialog = dialog.replaceFirst("%INT%", String.valueOf(getINT()));
            dialog = dialog.replaceFirst("%WIT%", String.valueOf(getWIT()));
            dialog = dialog.replaceFirst("%MEN%", String.valueOf(getMEN()));

            NpcHtmlMessage msg = new NpcHtmlMessage(5);
            msg.setHtml(dialog);
            player.sendPacket(msg);
        }
    }

    @Override
    public long getWearedMask() {
        return WeaponType.SWORD.mask(); // TODO: читать пассивки и смотреть тип оружия и брони там
    }

    class Lifetime extends RunnableImpl {
        @Override
        public void runImpl() {
            if (owner == null) {
                disappearTask = null;
                unSummon();
                return;
            }

            int usedtime = isInCombat() ? CYCLE : CYCLE / 4;
            _lifetimeCountdown -= usedtime;

            if (_lifetimeCountdown <= 0) {
                owner.sendPacket(Msg.SERVITOR_DISAPPEASR_BECAUSE_THE_SUMMONING_TIME_IS_OVER);
                disappearTask = null;
                unSummon();
                return;
            }

            _consumeCountdown -= usedtime;
            if (_itemConsumeIdInTime > 0 && _itemConsumeCountInTime > 0 && _consumeCountdown <= 0)
                if (owner.getInventory().destroyItemByItemId(getItemConsumeIdInTime(), getItemConsumeCountInTime(), "Life End")) {
                    _consumeCountdown = _itemConsumeDelay;
                    owner.sendPacket(new SystemMessage(SystemMessage.A_SUMMONED_MONSTER_USES_S1).addItemName(getItemConsumeIdInTime()));
                } else {
                    owner.sendPacket(Msg.SINCE_YOU_DO_NOT_HAVE_ENOUGH_ITEMS_TO_MAINTAIN_THE_SERVITORS_STAY_THE_SERVITOR_WILL_DISAPPEAR);
                    unSummon();
                }

            owner.sendPacket(new SetSummonRemainTime(SummonInstance.this));

            disappearTask = ThreadPoolManager.INSTANCE.schedule(this, CYCLE);
        }
    }
}