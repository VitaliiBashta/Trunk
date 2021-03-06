package l2trunk.scripts.ai.groups;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ForgeoftheGods extends Fighter {
    private static final List<Integer> RANDOM_SPAWN_MOBS = List.of(18799, 18800, 18801, 18802, 18803);
    private static final List<Integer> FOG_MOBS = List.of(
            22634, 22635, 22636, 22637, 22638, 22639, 22640, 22641,
            22642, 22643, 22644, 22645, 22646, 22647, 22648, 22649);
    private static final int TAR_BEETLE = 18804;

    private static final int TAR_BEETLE_ACTIVATE_SKILL_CHANGE = 2; // chance for activate skill
    private static final int TAR_BEETLE_SEARCH_RADIUS = 500; // search around players

    public ForgeoftheGods(NpcInstance actor) {
        super(actor);

        if (actor.getNpcId() == TAR_BEETLE) {
            AI_TASK_ATTACK_DELAY = 1250;
            actor.setInvul(true);
            actor.setHasChatWindow(false);
        } else if (RANDOM_SPAWN_MOBS.contains(actor.getNpcId()))
            actor.startImmobilized();
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();

        if (actor.getNpcId() != TAR_BEETLE)
            return super.thinkActive();

        if (actor.isDead() || !Rnd.chance(TAR_BEETLE_ACTIVATE_SKILL_CHANGE))
            return false;

        return  !World.getAroundPlayers(actor, TAR_BEETLE_SEARCH_RADIUS, 200)
                .filter(p -> Rnd.chance(50))
                .peek(p -> actor.doCast(6142, Rnd.get(1, 3), p, false))
                .findFirst().isPresent();
    }

    @Override
    public void onEvtDead(Creature killer) {
        NpcInstance actor = getActor();

        if (FOG_MOBS.contains(actor.getNpcId())) {
            NpcInstance npc = NpcHolder.getTemplate(Rnd.get(RANDOM_SPAWN_MOBS)).getNewInstance();
            npc.setSpawnedLoc(actor.getLoc());
            npc.setReflection(actor.getReflection());
            npc.setFullHpMp();
            npc.spawnMe(npc.getSpawnedLoc());
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
        }

        super.onEvtDead(killer);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (getActor().getNpcId() == TAR_BEETLE)
            return;
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
        if (getActor().getNpcId() == TAR_BEETLE)
            return;
        super.onEvtAggression(target, aggro);
    }

    @Override
    public boolean checkTarget(Creature target, int range) {
        NpcInstance actor = getActor();
        if (RANDOM_SPAWN_MOBS.contains(getActor().getNpcId()) && target != null && !actor.isInRange(target, actor.getAggroRange())) {
            actor.getAggroList().remove(target, true);
            return false;
        }
        return super.checkTarget(target, range);
    }

    @Override
    public boolean randomWalk() {
        return !RANDOM_SPAWN_MOBS.contains(getActor().getNpcId()) && getActor().getNpcId() != TAR_BEETLE;
    }
}