package l2trunk.scripts.ai;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

import java.util.Objects;

public final class GuardofDawnStat extends DefaultAI {
    private static final int AGGRORANGE = 120;
    private final int skill = 5978;
    private final Location loc;
    private boolean noCheckPlayers = false;

    public GuardofDawnStat(NpcInstance actor, Location loc) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 200;
        this.loc = loc;
    }

    @Override
    public boolean thinkActive() {
        // проверяем игроков вокруг
        if (!noCheckPlayers)
            checkAroundPlayers(getActor());
        return true;
    }

    private boolean checkAroundPlayers(NpcInstance actor) {
        return World.getAroundPlayables(actor, AGGRORANGE, AGGRORANGE).stream()
                .filter(Objects::nonNull)
                .filter(this::canSeeInSilentMove)
                .filter(this::canSeeInHide)
                .filter(GameObject::isPlayer)
                .filter(Playable::isSilentMoving)
                .filter(target -> !target.isInvul())
                .filter(target -> GeoEngine.canSeeTarget(actor, target, false))
                .peek(target -> {
                    actor.doCast(skill, target, true);
                    Functions.npcSay(actor, "Intruder alert!! We have been infiltrated!");
                    noCheckPlayers = true;
                    ThreadPoolManager.INSTANCE.schedule(() -> {
                        target.teleToLocation(loc);
                        noCheckPlayers = false;
                    }, 3000);
                }).findFirst().isPresent();
    }

    @Override
    public void thinkAttack() {
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public void onIntentionAttack(Creature target) {
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature attacker, int aggro) {
    }

    @Override
    public void onEvtClanAttacked(Creature attacked_member, Creature attacker, int damage) {
    }

}