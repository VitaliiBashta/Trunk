package l2trunk.scripts.ai;

import l2trunk.commons.threading.RunnableImpl;
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
import l2trunk.gameserver.utils.PositionUtils;

/**
 * AI NPC для SSQ Dungeon
 * - Если находят чара в радиусе 300, то кричат в чат и отправляют на точку старта
 * - Не видят, кто находится в хайде
 * - Никогда и никого не атакуют
 *
 * @author pchayka, n0nam3
 */
public class GuardofDawnFemale extends DefaultAI {
    private static final int _aggrorange = 300;
    private final Skill _skill = SkillTable.INSTANCE().getInfo(5978, 1);
    private Location _locTele = null;
    private boolean noCheckPlayers = false;

    public GuardofDawnFemale(NpcInstance actor, Location telePoint) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 200;
        setTelePoint(telePoint);
    }

    public class Teleportation extends RunnableImpl {

        Location _telePoint = null;
        Playable _target = null;

        Teleportation(Location telePoint, Playable target) {
            _telePoint = telePoint;
            _target = target;
        }

        @Override
        public void runImpl() {
            _target.teleToLocation(_telePoint);
            noCheckPlayers = false;
        }
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

            if (target != null && target.isPlayer() && !target.isSilentMoving() && !target.isInvul() && GeoEngine.canSeeTarget(actor, target, false) && PositionUtils.isFacing(actor, target, 150)) {
                actor.doCast(_skill, target, true);
                Functions.npcSay(actor, "Who are you?! A new face like you can't approach this place!");
                noCheckPlayers = true;
                ThreadPoolManager.INSTANCE().schedule(new Teleportation(getTelePoint(), target), 3000);
                return true;
            }
        }
        return false;
    }

    private void setTelePoint(Location loc) {
        _locTele = loc;
    }

    private Location getTelePoint() {
        return _locTele;
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