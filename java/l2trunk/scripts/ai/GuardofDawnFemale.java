package l2trunk.scripts.ai;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.PositionUtils;

import java.util.Objects;

public final class GuardofDawnFemale extends DefaultAI {
    private static final int AGGRORANGE = 300;
    private static final int deathStrike = 5978;
    private final Location location;
    private boolean noCheckPlayers = false;

    public GuardofDawnFemale(NpcInstance actor, Location telePoint) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 200;
        location = telePoint;
    }

    @Override
    public boolean thinkActive() {
        // проверяем игроков вокруг
        if (!noCheckPlayers)
            checkAroundPlayers(getActor());
        return true;
    }

    private void checkAroundPlayers(NpcInstance actor) {
        World.getAroundPlayers(actor, AGGRORANGE, AGGRORANGE)
                .filter(this::canSeeInSilentMove)
                .filter(this::canSeeInHide)
                .filter(Playable::isSilentMoving)
                .filter(target -> !target.isInvul())
                .filter(target -> GeoEngine.canSeeTarget(actor, target, false))
                .filter(target -> PositionUtils.isFacing(actor, target, 150))
                .findFirst()
                .ifPresent(target -> {
                    actor.doCast(deathStrike, target, true);
                    Functions.npcSay(actor, "Who are you?! A new face like you can't approach this place!");
                    noCheckPlayers = true;
                    ThreadPoolManager.INSTANCE.schedule(() -> {
                        target.teleToLocation(location);
                        noCheckPlayers = false;
                    }, 3000);
                });
    }

    @Override
    public boolean randomWalk() {
        return false;
    }

    @Override
    public void thinkAttack() {
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