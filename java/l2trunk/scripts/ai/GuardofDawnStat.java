package l2trunk.scripts.ai;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

public final class GuardofDawnStat extends DefaultAI {
    private static final int _aggrorange = 120;
    private final Skill skill = SkillTable.INSTANCE.getInfo(5978, 1);
    private Location _locTele = null;
    private boolean noCheckPlayers = false;

    public GuardofDawnStat(NpcInstance actor, Location telePoint) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 200;
        setTelePoint(telePoint);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();

        // проверяем игроков вокруг
        if (!noCheckPlayers)
            checkAroundPlayers(actor);

        return true;
    }

    private boolean checkAroundPlayers(NpcInstance actor) {
        for (Playable target : World.getAroundPlayables(actor, _aggrorange, _aggrorange)) {
            if (!canSeeInSilentMove(target) || !canSeeInHide(target))
                continue;

            if (target != null && target.isPlayer() && !target.isInvul() && GeoEngine.canSeeTarget(actor, target, false)) {
                actor.doCast(skill, target, true);
                Functions.npcSay(actor, "Intruder alert!! We have been infiltrated!");
                noCheckPlayers = true;
                ThreadPoolManager.INSTANCE.schedule(() -> {
                    target.teleToLocation(getTelePoint());
                    noCheckPlayers = false;
                }, 3000);
                return true;
            }
        }
        return false;
    }

    private Location getTelePoint() {
        return _locTele;
    }

    private void setTelePoint(Location loc) {
        _locTele = loc;
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