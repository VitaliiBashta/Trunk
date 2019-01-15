package l2trunk.scripts.ai;

import l2trunk.commons.text.PrintfFormat;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.npc.model.OrfenInstance;

import java.util.Arrays;
import java.util.List;

public final class Orfen extends Fighter {
    private static final List<PrintfFormat> MsgOnRecall = List.of(
            new PrintfFormat("%s. Stop kidding yourself about your own powerlessness!"),
            new PrintfFormat("%s. I'll make you feel what true fear is!"),
            new PrintfFormat("You're really stupid to have challenged me. %s! Get ready!"),
            new PrintfFormat("%s. Do you think that's going to work?!"));

    private final List<Skill> _paralyze;

    public Orfen(NpcInstance actor) {
        super(actor);
        _paralyze = getActor().getTemplate().getDebuffSkills();
    }

    @Override
    public boolean thinkActive() {
        if (super.thinkActive())
            return true;
        OrfenInstance actor = getActor();

        if (actor.isTeleported() && actor.getCurrentHpPercents() > 95) {
            actor.setTeleported(false);
            return true;
        }

        return false;
    }

    @Override
    public boolean createNewTask() {
        return defaultNewTask();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        super.onEvtAttacked(attacker, damage);
        OrfenInstance actor = getActor();
        if (actor.isCastingNow())
            return;

        double distance = actor.getDistance(attacker);

        // if(attacker.isMuted() &&)
        if (distance > 300 && distance < 1000 && _damSkills.size() > 0 && Rnd.chance(10)) {
            Functions.npcSay(actor, Rnd.get(MsgOnRecall).sprintf(attacker.getName()));
            teleToLocation(attacker, Location.findFrontPosition(actor, attacker, 0, 50));
            Skill r_skill = _damSkills.get(Rnd.get(_damSkills.size()));
            if (canUseSkill(r_skill, attacker, -1))
                addTaskAttack(attacker, r_skill.getId(), r_skill.getLevel());
        } else if (_paralyze.size() > 0 && Rnd.chance(20)) {
            Skill r_skill = _paralyze.get(Rnd.get(_paralyze.size()));
            if (canUseSkill(r_skill, attacker, -1))
                addTaskAttack(attacker, r_skill.getId(), r_skill.getLevel());
        }
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        super.onEvtSeeSpell(skill, caster);
        OrfenInstance actor = getActor();
        if (actor.isCastingNow())
            return;

        double distance = actor.getDistance(caster);
        if (_damSkills.size() > 0 && skill.getEffectPoint() > 0 && distance < 1000 && Rnd.chance(20)) {
            Functions.npcSay(actor, Rnd.get(MsgOnRecall).sprintf(caster.getName()));
            teleToLocation(caster, Location.findFrontPosition(actor, caster, 0, 50));
            Skill r_skill = Rnd.get(_damSkills);
            if (canUseSkill(r_skill, caster, -1))
                addTaskAttack(caster, r_skill.getId(), r_skill.getLevel());
        }
    }

    @Override
    public OrfenInstance getActor() {
        NpcInstance actor = super.getActor();
        return (OrfenInstance) actor;
    }

    private void teleToLocation(Creature attacker, Location loc) {
        attacker.teleToLocation(loc);
    }
}