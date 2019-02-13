package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.instances.residences.SiegeFlagInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;

import java.util.List;

public final class Heal extends Skill {
    private final boolean ignoreHpEff;
    private final boolean staticPower;

    public Heal(StatsSet set) {
        super(set);
        ignoreHpEff = set.getBool("ignoreHpEff", false);
        staticPower = set.getBool("staticPower", isItemHandler);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if ((target == null) || target instanceof DoorInstance || target.isRaid() || target.isBoss() || (target instanceof SiegeFlagInstance)) {
            return false;
        }
        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        // Надо уточнить формулу.
        double hp = power;
        if (!staticPower) {
            hp += 0.1 * power * Math.sqrt(activeChar.getMAtk(null, this) / 333.);
        }

        int sps = isSSPossible() && (hpConsume == 0) ? activeChar.getChargedSpiritShot() : 0;

        if (sps == 2) {
            hp *= 1.5;
        } else if (sps == 1) {
            hp *= 1.3;
        }

        if ((activeChar.getSkillMastery(id) == 3) && !staticPower) {
            activeChar.removeSkillMastery(id);
            hp *= 3.;
        }

        for (Creature target : targets) {
            if (target != null) {
                if (target.isHealBlocked()) {
                    continue;
                }

                // Player holding a cursed weapon can't be healed and can't heal
                if (target != activeChar) {
                    if (target instanceof Player && ((Player)target).isCursedWeaponEquipped()) {
                        continue;
                    } else if (activeChar instanceof Player && ((Player)activeChar).isCursedWeaponEquipped()) {
                        continue;
                    }
                }

                double addToHp;
                if (staticPower) {
                    addToHp = power;
                } else {
                    addToHp = (hp * (!ignoreHpEff ? target.calcStat(Stats.HEAL_EFFECTIVNESS, 100., activeChar, this) : 100.)) / 100.;
                    addToHp = activeChar.calcStat(Stats.HEAL_POWER, addToHp, target, this);
                }

                addToHp = Math.max(0, Math.min(addToHp, ((target.calcStat(Stats.HP_LIMIT, null, null) * target.getMaxHp()) / 100.) - target.getCurrentHp()));

                if (addToHp > 0) {
                    target.setCurrentHp(addToHp + target.getCurrentHp(), false);
                }
                if (id == 4051) {
                    target.sendPacket(SystemMsg.REJUVENATING_HP);
                } else if (target instanceof Player) {
                    if (activeChar == target) {
                        activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(addToHp)));
                    } else {
                        // FIXME show a healer, as he restored the HP goal?
                        target.sendPacket(new SystemMessage2(SystemMsg.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(activeChar.getName()).addInteger(Math.round(addToHp)));
                    }
                } else if (target instanceof l2trunk.gameserver.model.Summon) {
                    Player owner = ((Summon)target).owner;
                    if (owner != null) {
                        if (activeChar == target) {
                            owner.sendMessage(new CustomMessage("YOU_HAVE_RESTORED_S1_HP_OF_YOUR_PET").addNumber(Math.round(addToHp)));
                        } else if (owner == activeChar) {
                            owner.sendMessage(new CustomMessage("YOU_HAVE_RESTORED_S1_HP_OF_YOUR_PET").addNumber(Math.round(addToHp)));
                        } else {
                            // Пета лечит кто-то другой
                            owner.sendMessage(new CustomMessage("S1_HAS_BEEN_RESTORED_S2_HP_OF_YOUR_PET").addString(activeChar.getName()).addNumber(Math.round(addToHp)));
                        }
                    }
                }
                getEffects(activeChar, target, activateRate > 0, false);
            }
        }

        if (isSSPossible()) {
            activeChar.unChargeShots(isMagic());
        }
    }
}