package l2trunk.scripts.events.Christmas;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.skills.SkillsEngine;
import l2trunk.gameserver.skills.effects.EffectTemplate;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncTemplate;

import java.nio.file.Path;

public final class ctreeAI extends DefaultAI {
    private  final Path ORIGINAL_EFFECT_FILE = Config.DATAPACK_ROOT.resolve("data/stats/skills/2100-2199.xml");
    private static final int ORIGINAL_EFFECT_ID = 2139;
    private static final int RANGE = 200;
    private final Skill treeEffect;

    ctreeAI(NpcInstance actor) {
        super(actor);
        treeEffect = getRandomTreeEffect();
    }

    private  Skill getRandomTreeEffect() {
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

    private  Skill createRandomSkillEffect(Stats stat, double mult) {
        Skill copiedSkill = SkillsEngine.INSTANCE.loadSkill(ORIGINAL_EFFECT_ID, ORIGINAL_EFFECT_FILE);
        changeSkillEffect(copiedSkill, stat, mult);
        return copiedSkill;
    }

    private static void changeSkillEffect(Skill skill, Stats stat, double mult) {
        FuncTemplate func = new FuncTemplate(null, "Mul", stat, 0x30, mult);
        for (EffectTemplate template : skill.getEffectTemplates()) {
            template.clearAttachedFuncs();
            template.attachFunc(func);
        }
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        for (Player player : World.getAroundPlayers(actor, RANGE, RANGE))
            if (player != null && player.getEffectList().getEffectsBySkillId(ORIGINAL_EFFECT_ID) == null) {
                actor.doCast(treeEffect, player, true);
            }
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