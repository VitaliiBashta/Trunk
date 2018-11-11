package l2trunk.gameserver.skills.effects;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.gameserver.listener.actor.OnCurrentHpDamageListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class EffectCurseOfLifeFlow extends Effect {
    private CurseOfLifeFlowListener _listener;

    private final Map<Integer, HardReference<? extends Creature>> _damageList = new HashMap<>();

    public EffectCurseOfLifeFlow(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();
        _listener = new CurseOfLifeFlowListener();
        _effected.addListener(_listener);
    }

    @Override
    public void onExit() {
        super.onExit();
        _effected.removeListener(_listener);
        _listener = null;
    }

    @Override
    public boolean onActionTime() {
        if (_effected.isDead())
            return false;

        for (Map.Entry<Integer, HardReference<? extends Creature>> dmg : _damageList.entrySet()) {
            Creature damager = dmg.getValue().get();
            if (damager == null || damager.isDead() || damager.isCurrentHpFull())
                continue;

            int damage = dmg.getKey();
            if (damage <= 0)
                continue;

            double max_heal = calc();
            double heal = Math.min(damage, max_heal);
            double newHp = Math.min(damager.getCurrentHp() + heal, damager.getMaxHp());

            damager.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger((long) (newHp - damager.getCurrentHp())));
            damager.setCurrentHp(newHp, false);
        }

        _damageList.clear();

        return true;
    }

    private class CurseOfLifeFlowListener implements OnCurrentHpDamageListener {
        @Override
        public void onCurrentHpDamage(Creature actor, double damage, Creature attacker, Skill skill) {
            if (attacker == actor || attacker == _effected)
                return;
            HardReference<? extends Creature> ref = attacker.getRef();
            Optional<Map.Entry<Integer, HardReference<? extends Creature>>> findDamager = _damageList.entrySet().stream().filter(entry -> entry.getValue() != ref).findAny();
            if (findDamager.isPresent()) {
                Integer old_damage = findDamager.get().getKey();
                _damageList.remove(old_damage);
                _damageList.put(old_damage == 0 ? (int) damage : old_damage + (int) damage, ref);
            }
        }
    }
}