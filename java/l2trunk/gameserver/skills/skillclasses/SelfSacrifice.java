package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class SelfSacrifice extends Skill {
    private final int _effRadius;

    public SelfSacrifice(StatsSet set) {
        super(set);
        this._effRadius = set.getInteger("effRadius", 1000);
        this._lethal1 = set.getInteger("lethal1", 0);
        this._lethal2 = set.getInteger("lethal2", 0);
    }

    @Override
    public List<Creature> getTargets(final Creature activeChar, final Creature aimingTarget, final boolean forceUse) {
        return activeChar.getAroundCharacters(this._effRadius, 1000)
                .filter(Objects::nonNull)
                .filter(GameObject::isPlayer)
                .filter(target -> !target.isAutoAttackable(activeChar))
                .collect(Collectors.toList());
    }

    @Override
    public void useSkill(final Creature activeChar, final List<Creature> targets) {
        for (Creature target : targets) {
            if (target != null) {
                if ((getSkillType() != Skill.SkillType.BUFF) || (target == activeChar) || ((!target.isCursedWeaponEquipped()) && (!activeChar.isCursedWeaponEquipped()))) {
                    boolean reflected = target.checkReflectSkill(activeChar, this);
                    getEffects(activeChar, target, getActivateRate() > 0, false, reflected);
                }
            }
        }
        if ((isSSPossible()) && ((this.skillType != Skill.SkillType.SELF_SACRIFICE))) {
            activeChar.unChargeShots(isMagic());
        }
    }
}