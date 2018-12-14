package l2trunk.scripts.events.Christmas;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncTemplate;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Objects;

public final class ctreeAI extends DefaultAI {
    private static final int ORIGINAL_EFFECT_ID = 2139;
    private static final int RANGE = 200;
    private final Skill treeEffect;

    ctreeAI(NpcInstance actor) {
        super(actor);
        treeEffect = getRandomTreeEffect();
    }

    private static void changeSkillEffect(Stats stat, double mult) {
        FuncTemplate func = new FuncTemplate(null, "Mul", stat, 0x30, mult);
        for (EffectTemplate template : SkillTable.INSTANCE.getInfo(ctreeAI.ORIGINAL_EFFECT_ID).getEffectTemplates()) {
            template.clearAttachedFuncs();
            template.attachFunc(func);
        }
    }

    private Skill getRandomTreeEffect() {
        int random = Rnd.get(7);
        switch (random) {
            case 0:
                return createRandomSkillEffect(Stats.POWER_DEFENCE, 1.25);
            case 1:
                return createRandomSkillEffect(Stats.POWER_ATTACK, 1.25);
            case 2:
                return createRandomSkillEffect(Stats.MAGIC_ATTACK, 1.25);
            case 3:
                return createRandomSkillEffect(Stats.CRITICAL_DAMAGE, 1.15);
            case 4:
                return createRandomSkillEffect(Stats.MAGIC_ATTACK_SPEED, 1.15);
            case 5:
                return createRandomSkillEffect(Stats.POWER_ATTACK_SPEED, 1.15);
            case 6:
                return createRandomSkillEffect(Stats.CRITICAL_RATE, 1.15);
            default:
                return createRandomSkillEffect(Stats.CRITICAL_RATE, 1.15);
        }
    }

    private Skill createRandomSkillEffect(Stats stat, double mult) {
        Skill copiedSkill = SkillTable.INSTANCE.getInfo(ORIGINAL_EFFECT_ID);
        changeSkillEffect(stat, mult);
        return copiedSkill;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;
        World.getAroundPlayers(actor, RANGE, RANGE).stream()
                .filter(Objects::nonNull)
                .filter(player -> player.getEffectList().getEffectsBySkillId(ORIGINAL_EFFECT_ID) == null)
                .forEach(player -> actor.doCast(treeEffect, player, true));
        return false;
    }

    @Override
    public boolean randomAnimation() {
        return false;
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}