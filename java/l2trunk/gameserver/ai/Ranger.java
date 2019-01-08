package l2trunk.gameserver.ai;

import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public class Ranger extends DefaultAI {
    public Ranger(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return super.thinkActive() || defaultThinkBuff();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        super.onEvtAttacked(attacker, damage);
        NpcInstance actor = getActor();
        if (actor.isDead() || attacker == null || actor.getDistance(attacker) > 200)
            return;

        if (actor.isMoving)
            return;
        Location oldLocation = new Location(actor);
        int posX = actor.getX();
        int posY = actor.getY();
        int posZ = actor.getZ();

        int signx = posX < attacker.getX() ? -1 : 1;
        int signy = posY < attacker.getY() ? -1 : 1;

        int range = (int) (0.71 * actor.calculateAttackDelay() / 1000 * actor.getMoveSpeed());

        posX += signx * range;
        posY += signy * range;
        posZ = GeoEngine.getHeight(posX, posY, posZ, actor.getGeoIndex());
        Location newLocation = new Location(posX, posY, posZ);

        if (GeoEngine.canMoveToCoord(oldLocation, newLocation, actor.getGeoIndex())) {
            addTaskMove(newLocation, false);
            addTaskAttack(attacker);
        }
    }

    @Override
    public boolean createNewTask() {
        return defaultFightTask();
    }

    @Override
    public int getRatePHYS() {
        return 25;
    }

    @Override
    public int getRateDOT() {
        return 40;
    }

    @Override
    public int getRateDEBUFF() {
        return 25;
    }

    @Override
    public int getRateDAM() {
        return 50;
    }

    @Override
    public int getRateSTUN() {
        return 50;
    }

    @Override
    public int getRateBUFF() {
        return 5;
    }

    @Override
    public int getRateHEAL() {
        return 50;
    }
}