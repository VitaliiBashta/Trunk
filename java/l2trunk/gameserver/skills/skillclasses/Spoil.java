package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Formulas.AttackInfo;

import java.util.List;
import java.util.Objects;

public final class Spoil extends Skill {
    public Spoil(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (!activeChar.isPlayer())
            return;

        int ss = isSSPossible() ? (isMagic() ? activeChar.getChargedSpiritShot() : activeChar.getChargedSoulShot() ? 2 : 0) : 0;
        if (ss > 0 && getPower() > 0)
            activeChar.unChargeShots(false);

        targets.stream()
                .filter(Objects::nonNull)
                .filter(t -> !t.isDead())
                .forEach(t -> {
                    if (t.isMonster()) {
                        if (isSpoilUse(t)) {
                            if (((MonsterInstance) t).isSpoiled())
                                activeChar.sendPacket(SystemMsg.IT_HAS_ALREADY_BEEN_SPOILED);
                            else {
                                MonsterInstance monster = (MonsterInstance) t;
                                boolean success;
                                if (!Config.ALT_SPOIL_FORMULA) {
                                    int monsterLevel = monster.getLevel();
                                    int modifier = Math.abs(monsterLevel - activeChar.getLevel());
                                    double rateOfSpoil = Config.BASE_SPOIL_RATE;
                                    if (modifier > 8)
                                        rateOfSpoil = rateOfSpoil - rateOfSpoil * (modifier - 8) * 9 / 100;

                                    rateOfSpoil = rateOfSpoil * getMagicLevel() / monsterLevel;
                                    if (rateOfSpoil < Config.MINIMUM_SPOIL_RATE)
                                        rateOfSpoil = Config.MINIMUM_SPOIL_RATE;
                                    else if (rateOfSpoil > 99.)
                                        rateOfSpoil = 99.;

                                    if (((Player) activeChar).isGM())
                                        activeChar.sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Spoil.Chance", (Player) activeChar).addNumber((long) rateOfSpoil));
                                    success = Rnd.chance(rateOfSpoil);

                                } else
                                    success = Formulas.calcSkillSuccess(activeChar, t, this, getActivateRate());
                                if (success && monster.setSpoiled(activeChar.getPlayer()))
                                    activeChar.sendPacket(SystemMsg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
                                else
                                    activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_FAILED).addSkillName(id, getDisplayLevel()));
                            }
                        } else
                            activeChar.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_FAILED).addSkillName(id, getDisplayLevel()));
                    }

                    if (getPower() > 0) {
                        double damage;
                        if (isMagic())
                            damage = Formulas.calcMagicDam(activeChar, t, this, ss);
                        else {
                            AttackInfo info = Formulas.calcPhysDam(activeChar, t, this, false, false, ss > 0, false);
                            damage = info.damage;

                            if (info.lethal_dmg > 0)
                                t.reduceCurrentHp(info.lethal_dmg, activeChar, this, true, true, false, false, false, false, false);
                        }

                        t.reduceCurrentHp(damage, activeChar, this, true, true, false, true, false, false, true);
                        t.doCounterAttack(this, activeChar, false);
                    }

                    getEffects(activeChar, t);

                    t.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, Math.max(_effectPoint, 1));
                });
    }

    private boolean isSpoilUse(Creature target) {
        return getLevel() != 1 || target.getLevel() <= 22 || getId() != 254;
    }
}