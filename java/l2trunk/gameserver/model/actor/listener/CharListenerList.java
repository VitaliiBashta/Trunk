package l2trunk.gameserver.model.actor.listener;

import l2trunk.commons.listener.Listener;
import l2trunk.commons.listener.ListenerList;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.listener.actor.*;
import l2trunk.gameserver.listener.actor.ai.OnAiEventListener;
import l2trunk.gameserver.listener.actor.ai.OnAiIntentionListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;

public class CharListenerList extends ListenerList<Creature> {
    final static ListenerList<Creature> global = new ListenerList<>();

    final Creature actor;

    public CharListenerList(Creature actor) {
        this.actor = actor;
    }

    public static boolean addGlobal(Listener<Creature> listener) {
        return global.add(listener);
    }

    public static void removeGlobal(Listener<Creature> listener) {
        global.remove(listener);
    }

    Creature getActor() {
        return actor;
    }

    public void onAiIntention(CtrlIntention intention, Object arg0, Object arg1) {
        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnAiIntentionListener)
                    ((OnAiIntentionListener) listener).onAiIntention(getActor(), intention, arg0, arg1);
    }

    public void onAiEvent(CtrlEvent evt, Object[] args) {
        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnAiEventListener)
                    ((OnAiEventListener) listener).onAiEvent(getActor(), evt, args);
    }

    public void onAttack(Creature target) {
        if (!global.getListeners().isEmpty())
            for (Listener<Creature> listener : global.getListeners())
                if (listener instanceof OnAttackListener)
                    ((OnAttackListener) listener).onAttack(getActor(), target);

        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnAttackListener)
                    ((OnAttackListener) listener).onAttack(getActor(), target);
    }

    public void onAttackHit(Creature attacker) {
        if (!global.getListeners().isEmpty())
            for (Listener<Creature> listener : global.getListeners())
                if (listener instanceof OnAttackHitListener)
                    ((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);

        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnAttackHitListener)
                    ((OnAttackHitListener) listener).onAttackHit(getActor(), attacker);
    }

    public void onMagicUse(Skill skill, Creature target, boolean alt) {
        if (!global.getListeners().isEmpty())
            for (Listener<Creature> listener : global.getListeners())
                if (listener instanceof OnMagicUseListener)
                    ((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);

        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnMagicUseListener)
                    ((OnMagicUseListener) listener).onMagicUse(getActor(), skill, target, alt);
    }

    public void onMagicHit(Skill skill, Creature caster) {
        if (!global.getListeners().isEmpty())
            for (Listener<Creature> listener : global.getListeners())
                if (listener instanceof OnMagicHitListener)
                    ((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);

        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnMagicHitListener)
                    ((OnMagicHitListener) listener).onMagicHit(getActor(), skill, caster);
    }

    public void onDeath(Creature killer) {
        if (!global.getListeners().isEmpty())
            for (Listener<Creature> listener : global.getListeners())
                if (listener instanceof OnDeathListener)
                    ((OnDeathListener) listener).onDeath(getActor(), killer);

        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnDeathListener)
                    ((OnDeathListener) listener).onDeath(getActor(), killer);
    }

    public void onKill(Creature victim) {
        if (!global.getListeners().isEmpty())
            for (Listener<Creature> listener : global.getListeners())
                if (listener instanceof OnKillListener && !((OnKillListener) listener).ignorePetOrSummon())
                    ((OnKillListener) listener).onKill(getActor(), victim);

        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnKillListener && !((OnKillListener) listener).ignorePetOrSummon())
                    ((OnKillListener) listener).onKill(getActor(), victim);
    }

    public void onKillIgnorePetOrSummon(Creature victim) {
        if (!global.getListeners().isEmpty())
            for (Listener<Creature> listener : global.getListeners())
                if (listener instanceof OnKillListener && ((OnKillListener) listener).ignorePetOrSummon())
                    ((OnKillListener) listener).onKill(getActor(), victim);

        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnKillListener && ((OnKillListener) listener).ignorePetOrSummon())
                    ((OnKillListener) listener).onKill(getActor(), victim);
    }

    public void onCurrentHpDamage(double damage, Creature attacker, Skill skill) {
        if (!global.getListeners().isEmpty())
            for (Listener<Creature> listener : global.getListeners())
                if (listener instanceof OnCurrentHpDamageListener)
                    ((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill);

        if (!getListeners().isEmpty())
            for (Listener<Creature> listener : getListeners())
                if (listener instanceof OnCurrentHpDamageListener)
                    ((OnCurrentHpDamageListener) listener).onCurrentHpDamage(getActor(), damage, attacker, skill);
    }
}
