package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.dao.EffectsDAO;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectTasks;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.instances.MerchantInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.model.instances.TrapInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncAdd;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Summon extends Skill {
    private final SummonType _summonType;

    private final double expPenalty;
    private final int itemConsumeIdInTime;
    private final int itemConsumeCountInTime;
    private final int itemConsumeDelay;
    private final int lifeTime;

    public Summon(StatsSet set) {
        super(set);

        _summonType = Enum.valueOf(SummonType.class, set.getString("summonType", "PET").toUpperCase());
        expPenalty = set.getDouble("expPenalty");
        itemConsumeIdInTime = set.getInteger("itemConsumeIdInTime");
        itemConsumeCountInTime = set.getInteger("itemConsumeCountInTime");
        itemConsumeDelay = set.getInteger("itemConsumeDelay", 240) * 1000;
        lifeTime = set.getInteger("lifeTime", 1200) * 1000;
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (player.isProcessingRequest()) {
            player.sendPacket(SystemMsg.PETS_AND_SERVITORS_ARE_NOT_AVAILABLE_AT_THIS_TIME);
            return false;
        }

        switch (_summonType) {
            case TRAP:
                if (player.isInZonePeace()) {
                    player.sendPacket(SystemMsg.A_MALICIOUS_SKILL_CANNOT_BE_USED_IN_A_PEACE_ZONE);
                    return false;
                }
                break;
            case PET:
            case SIEGE_SUMMON:
                if (player.getPet() != null || player.isMounted()) {
                    player.sendPacket(SystemMsg.YOU_ALREADY_HAVE_A_PET);
                    return false;
                }
                break;
            case AGATHION:
                if (player.getAgathionId() > 0 && npcId != 0) {
                    player.sendPacket(SystemMsg.AN_AGATHION_HAS_ALREADY_BEEN_SUMMONED);
                    return false;
                }
        }

        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature caster, List<Creature> targets) {
        if (!(caster instanceof Player))
            return;
        Player player = (Player)caster;

        switch (_summonType) {
            case AGATHION:
                player.setAgathion(npcId);
                break;
            case TRAP:
                int trapSkillId = getFirstAddedSkill().id;

                if (player.getTrapsCount() >= 5)
                    player.destroyFirstTrap();
                TrapInstance trap = new TrapInstance(IdFactory.getInstance().getNextId(), NpcHolder.getTemplate(npcId), player, trapSkillId);
                player.addTrap(trap);
                trap.spawnMe();
                break;
            case PET:
            case SIEGE_SUMMON:
                // Removal of the corpse if it summon from a corpse.
                Location loc = null;
                if (targetType == SkillTargetType.TARGET_CORPSE)
                    for (Creature target : targets)
                        if (target != null && target.isDead()) {
                            player.getAI().setAttackTarget(null);
                            loc = target.getLoc();
                            if (target instanceof NpcInstance)
                                ((NpcInstance) target).endDecayTask();
                            else if (target instanceof SummonInstance)
                                ((SummonInstance) target).endDecayTask();
                            else
                                return; // кто труп ?
                        }

                if (player.getPet() != null || player.isMounted())
                    return;

                NpcTemplate summonTemplate = NpcHolder.getTemplate(npcId);
                SummonInstance summon = new SummonInstance(IdFactory.getInstance().getNextId(), summonTemplate, player, lifeTime, itemConsumeIdInTime, itemConsumeCountInTime, itemConsumeDelay, this);
                player.setPet(summon);

                summon.setTitle(player.getName());
                summon.setExpPenalty(expPenalty);
                summon.setExp(Experience.LEVEL[Math.min(summon.getLevel(), Experience.LEVEL.length - 1)]);
                summon.setHeading(player.getHeading());
                summon.setReflection(player.getReflection());
                summon.spawnMe(loc == null ? Location.findAroundPosition(player, 50, 70) : loc);
                summon.setRunning();
                summon.setFollowMode(true);

                if (summon.getSkillLevel(4140) > 0)
                    summon.altUseSkill(4140, summon.getSkillLevel(4140), player);

                if ("Shadow".equalsIgnoreCase(summon.getName()))//FIXME [G1ta0] идиотский хардкод
                    summon.addStatFunc(new FuncAdd(Stats.ABSORB_DAMAGE_PERCENT, 0x40, this, 15));

                EffectsDAO.INSTANCE.restoreEffects(summon, true, summon.getMaxHp(), summon.getMaxCp(), summon.getMaxMp());
                if (player.isInOlympiadMode())
                    summon.getEffectList().stopAllEffects();

                summon.setFullHpMp();

                if (_summonType == SummonType.SIEGE_SUMMON) {
                    SiegeEvent siegeEvent = player.getEvent(SiegeEvent.class);

                    siegeEvent.addSiegeSummon(summon);
                }
                break;
            case MERCHANT:
                if (player.getPet() != null || player.isMounted())
                    return;

                NpcTemplate merchantTemplate = NpcHolder.getTemplate(npcId);
                MerchantInstance merchant = new MerchantInstance(IdFactory.getInstance().getNextId(), merchantTemplate);

                merchant.setCurrentHp(merchant.getMaxHp(), false);
                merchant.setCurrentMp(merchant.getMaxMp());
                merchant.setHeading(player.getHeading());
                merchant.setReflection(player.getReflection());
                merchant.spawnMe(player.getLoc());

                ThreadPoolManager.INSTANCE.schedule(new GameObjectTasks.DeleteTask(merchant), lifeTime);
                break;
        }


        if (isSSPossible())
            caster.unChargeShots(isMagic());
    }

    private enum SummonType {
        PET,
        SIEGE_SUMMON,
        AGATHION,
        TRAP,
        MERCHANT
    }
}