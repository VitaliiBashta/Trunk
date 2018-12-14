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

/**
 * AI NPC для SSQ Dungeon
 * - Если находят чара в радиусе 120, то кричат в чат и отправляют на точку старта
 * - Видят и цепляют тех, кто находится в хайде
 * - Никогда и никого не атакуют
 */
public final class GuardofDawn extends DefaultAI {
    private static final int _aggrorange = 150;
    private static final int _skill = 5978;
    private Location _locStart = null;
    private Location _locEnd = null;
    private Location _locTele = null;
    private boolean moveToEnd = true;
    private boolean noCheckPlayers = false;

    public GuardofDawn(NpcInstance actor, Location locationEnd, Location telePoint) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 200;
        setStartPoint(actor.getSpawnedLoc()); // точка старта, по сути место спавна.
        setEndPoint(locationEnd);
        setTelePoint(telePoint);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();

        // проверяем игроков вокруг
        if (!noCheckPlayers)
            checkAroundPlayers(actor);

        // если есть задания - делаем их
        if (_def_think) {
            doTask();
            return true;
        }

        // заданий нет, значит можно давать новое, для этого ставим moveToEnd обратное значение
        moveToEnd = !moveToEnd;

        // добавляем задачу на движение
        if (!moveToEnd)
            addTaskMove(getEndPoint(), true);
        else
            addTaskMove(getStartPoint(), true);
        doTask();

        return true;
    }

    private boolean checkAroundPlayers(NpcInstance actor) {
        for (Playable target : World.getAroundPlayables(actor, _aggrorange, _aggrorange)) {
            if (!canSeeInSilentMove(target) || !canSeeInHide(target))
                continue;

            if (target != null && target.isPlayer() && !target.isInvul() && GeoEngine.canSeeTarget(actor, target, false)) {
                actor.doCast(_skill, target, true);
                Functions.npcSay(actor, "Intruder! Protect the Priests of Dawn!");
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

    private Location getStartPoint() {
        return _locStart;
    }

    private void setStartPoint(Location loc) {
        _locStart = loc;
    }

    private Location getEndPoint() {
        return _locEnd;
    }

    private void setEndPoint(Location loc) {
        _locEnd = loc;
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