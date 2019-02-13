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
        if (!(activeChar instanceof Player))
            return;
        Player player = (Player) activeChar;
        int ss = isSSPossible() ? (isMagic() ? player.getChargedSpiritShot() : player.getChargedSoulShot() ? 2 : 0) : 0;
        if (ss > 0 && power > 0)
            player.unChargeShots(false);

        targets.stream()
                .filter(Objects::nonNull)
                .filter(t -> !t.isDead())
                .filter(t -> t instanceof MonsterInstance)
                .map(t -> (MonsterInstance)t)
                .forEach(monster -> {
                    if (isSpoilUse(monster)) {
                        if (monster.isSpoiled())
                            player.sendPacket(SystemMsg.IT_HAS_ALREADY_BEEN_SPOILED);
                        else {
                            boolean success;
                            if (!Config.ALT_SPOIL_FORMULA) {
                                int monsterLevel = monster.getLevel();
                                int modifier = Math.abs(monsterLevel - player.getLevel());
                                double rateOfSpoil = Config.BASE_SPOIL_RATE;
                                if (modifier > 8)
                                    rateOfSpoil = rateOfSpoil - rateOfSpoil * (modifier - 8) * 9 / 100;

                                rateOfSpoil = rateOfSpoil * magicLevel / monsterLevel;
                                if (rateOfSpoil < Config.MINIMUM_SPOIL_RATE)
                                    rateOfSpoil = Config.MINIMUM_SPOIL_RATE;
                                else if (rateOfSpoil > 99.)
                                    rateOfSpoil = 99.;

                                if (player.isGM())
                                    player.sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Spoil.Chance").addNumber((long) rateOfSpoil));
                                success = Rnd.chance(rateOfSpoil);

                            } else
                                success = Formulas.calcSkillSuccess(player, monster, this, activateRate);
                            if (success && monster.setSpoiled(player))
                                player.sendPacket(SystemMsg.THE_SPOIL_CONDITION_HAS_BEEN_ACTIVATED);
                            else
                                player.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_FAILED).addSkillName(id, getDisplayLevel()));
                        }
                    } else
                        player.sendPacket(new SystemMessage2(SystemMsg.S1_HAS_FAILED).addSkillName(id, getDisplayLevel()));

                    if (power > 0) {
                        double damage;
                        if (isMagic())
                            damage = Formulas.calcMagicDam(player, monster, this, ss);
                        else {
                            AttackInfo info = Formulas.calcPhysDam(player, monster, this, false, false, ss > 0, false);
                            damage = info.damage;

                            if (info.lethal_dmg > 0)
                                monster.reduceCurrentHp(info.lethal_dmg, player, this, true, true, false, false, false, false, false);
                        }

                        monster.reduceCurrentHp(damage, player, this, true, true, false, true, false, false, true);
                        monster.doCounterAttack(this, player, false);
                    }

                    getEffects(player, monster);

                    monster.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, Math.max(effectPoint, 1));
                });
    }

    private boolean isSpoilUse(Creature target) {
        return level != 1 || target.getLevel() <= 22 || id != 254;
    }
}