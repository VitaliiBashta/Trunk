package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;

import java.util.List;

public final class Aggression extends Skill {
    private final boolean unaggring;
    private final boolean silent;
    private final boolean ignorePlayables;
    private final boolean autoAttack;

    public Aggression(StatsSet set) {
        super(set);
        unaggring = set.isSet("unaggroing");
        silent = set.isSet("silent");
        ignorePlayables = set.isSet("ignorePlayables");
        autoAttack = set.isSet("autoAttack");
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        int effect = effectPoint;

        if (isSSPossible() && (activeChar.getChargedSoulShot() || activeChar.getChargedSpiritShot() > 0))
            effect *= 2;

        for (Creature target : targets)
            if (target != null) {
                if (target.isAutoAttackable(activeChar)) {
                    if (target instanceof NpcInstance)
                        if (unaggring) {
                            if (activeChar instanceof Playable)
                                ((NpcInstance) target).getAggroList().addDamageHate(activeChar, 0, -effect);
                        } else {
                            target.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, activeChar, effect);
                            if (!silent)
                                target.getAI().notifyEvent(CtrlEvent.EVT_ATTACKED, activeChar, 0);
                        }
                    else if (!ignorePlayables && target instanceof Playable && !target.isDebuffImmune()) {
                        target.setTarget(activeChar);
                        if (autoAttack)
                            target.getAI().Attack(activeChar, false, false);
                    }
                    getEffects(activeChar, target, activateRate > 0, false);
                }
            }

        if (isSSPossible())
            activeChar.unChargeShots(isMagic());
    }
}