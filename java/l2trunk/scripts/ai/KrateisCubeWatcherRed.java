package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class KrateisCubeWatcherRed extends DefaultAI {
    private static final Map<Integer, Integer> SKILLS = Map.of(
            1064, 14,
            1160, 15,
            1164, 19,
            1167, 6,
            1168, 7);
    private static final int SKILL_CHANCE = 25;

    public KrateisCubeWatcherRed(NpcInstance actor) {
        super(actor);
        AI_TASK_ACTIVE_DELAY = 3000;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtThink() {
        NpcInstance actor = getActor();

        World.getAroundCharacters(actor, 600, 300)
                .filter(GameObject::isPlayer)
                .filter(cha -> !cha.isDead())
                .filter(cha -> Rnd.chance(SKILL_CHANCE))
                .forEach(cha -> {
                    int rnd = Rnd.get(new ArrayList<>(SKILLS.keySet()));
                    SkillTable.INSTANCE.getInfo(rnd, SKILLS.get(rnd)).getEffects(cha);
                });
    }

    @Override
    public void onEvtDead(Creature killer) {
        final NpcInstance actor = getActor();
        super.onEvtDead(killer);

        actor.deleteMe();
        ThreadPoolManager.INSTANCE.schedule(() -> {
            NpcInstance a = NpcHolder.getTemplate(18602).getNewInstance();
            a.setFullHpMp();
            a.spawnMe(actor.getLoc());
        }, 10000L);
    }
}
