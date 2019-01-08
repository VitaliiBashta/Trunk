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

public class CharListenerList extends ListenerList {
    final static ListenerList global = new ListenerList();

    final Creature actor;

    public CharListenerList(Creature actor) {
        this.actor = actor;
    }

    public static boolean addGlobal(Listener listener) {
        return global.add(listener);
    }

    public static void removeGlobal(Listener listener) {
        global.remove(listener);
    }

    Creature getActor() {
        return actor;
    }

    public void onAiIntention(CtrlIntention intention, Object arg0, Object arg1) {
        getListeners().stream()
                .filter(l -> l instanceof OnAiIntentionListener)
                .map(l -> (OnAiIntentionListener) l)
                .forEach(l -> l.onAiIntention(getActor(), intention, arg0, arg1));
    }

    public void onAiEvent(CtrlEvent evt, Object[] args) {
        getListeners().stream()
                .filter(l -> l instanceof OnAiEventListener)
                .map(l -> (OnAiEventListener) l)
                .forEach(l -> l.onAiEvent(getActor(), evt, args));
    }

    public void onAttack(Creature target) {
        onAttack(global, target);
        onAttack(this, target);
    }

    public void onAttackHit(Creature attacker) {
        onAttackHit(global,attacker);
        onAttackHit(this,attacker);
    }
    public void onMagicUse(Skill skill, Creature target, boolean alt) {
        onMagicUse(global, skill, target, alt);
        onMagicUse(this, skill, target, alt);
    }

    public void onMagicHit(Skill skill, Creature caster) {
        onMagicHit(global, skill, caster);
        onMagicHit(this, skill, caster);
    }

    public void onDeath(Creature killer) {
        onDeath(global, killer);
        onDeath(this, killer);
    }

    public void onKill(Creature victim) {
        onKill(global, victim);
        onKill(this, victim);
    }

    public void onKillIgnorePetOrSummon(Creature victim) {
        onKillIgnorePetOrSummon(global, victim);
        onKillIgnorePetOrSummon(this, victim);
    }

    public void onCurrentHpDamage(double damage, Creature attacker, Skill skill) {
        onCurrentHpDamage(global, damage, attacker, skill);
        onCurrentHpDamage(this, damage, attacker, skill);
    }

    private void onAttackHit(ListenerList list,Creature attacker) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnAttackHitListener)
                .map( l ->(OnAttackHitListener) l)
                .forEach(l ->l.onAttackHit(getActor(), attacker));
    }

    private void onMagicUse(ListenerList list, Skill skill, Creature target, boolean alt) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnMagicUseListener)
                .map(l -> (OnMagicUseListener) l)
                .forEach(l -> l.onMagicUse(getActor(), skill, target, alt));
    }

    private void onAttack(ListenerList list, Creature target) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnAttackListener)
                .map(l -> (OnAttackListener) l)
                .forEach(l -> l.onAttack(getActor(), target));

    }

    private void onMagicHit(ListenerList list, Skill skill, Creature caster) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnMagicHitListener)
                .map(l -> (OnMagicHitListener) l)
                .forEach(l -> l.onMagicHit(getActor(), skill, caster));
    }

    private void onDeath(ListenerList list, Creature killer) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnDeathListener)
                .map(l -> (OnDeathListener) l)
                .forEach(l -> l.onDeath(getActor(), killer));
    }

    private void onKill(ListenerList list, Creature victim) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnKillListener)
                .map(l -> (OnKillListener) l)
                .filter(l -> !l.ignorePetOrSummon())
                .forEach(l -> l.onKill(getActor(), victim));
    }

    private void onCurrentHpDamage(ListenerList list, double damage, Creature attacker, Skill skill) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnCurrentHpDamageListener)
                .map(l -> (OnCurrentHpDamageListener) l)
                .forEach(l -> l.onCurrentHpDamage(getActor(), damage, attacker, skill));
    }

    private void onKillIgnorePetOrSummon(ListenerList list, Creature victim) {
        list.getListeners().stream()
                .filter(l -> l instanceof OnKillListener)
                .map(l -> (OnKillListener) l)
                .filter(OnKillListener::ignorePetOrSummon)
                .forEach(l -> l.onKill(getActor(), victim));
    }
}
