package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.stats.Formulas.AttackInfo;

import java.util.List;

public final class ChargeSoul extends Skill {
    private final int numSouls;

    public ChargeSoul(StatsSet set) {
        super(set);
        numSouls = set.getInteger("numSouls", level);
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (!(activeChar instanceof Player))
            return;
        Player player = (Player)activeChar;

        for (Creature target : targets)
            if (target != null && !target.isDead()) {
                    boolean reflected = target != player && target.checkReflectSkill(player, this);
                    Creature realTarget = reflected ? player : target;

                    if (power > 0) {// Если == 0 значит скилл "отключен"
                        AttackInfo info = Formulas.calcPhysDam(player, player, this, false, false, false, false);

                        realTarget.reduceCurrentHp(info.damage, player, this, true, true, false, true, false, false, true);
                        if (!reflected)
                            realTarget.doCounterAttack(this, player, false);
                    }

                    if (realTarget instanceof Playable || realTarget instanceof MonsterInstance)
                        player.setConsumedSouls(player.getConsumedSouls() + numSouls, null);

                    getEffects(player, target, activateRate > 0, false, reflected);
                }

        if (isSSPossible())
            player.unChargeShots(isMagic());
    }
}