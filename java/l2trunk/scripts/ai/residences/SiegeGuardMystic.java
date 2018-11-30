package l2trunk.scripts.ai.residences;

import l2trunk.gameserver.model.instances.NpcInstance;

public class SiegeGuardMystic extends SiegeGuard {
    protected SiegeGuardMystic(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean createNewTask() {
        return defaultFightTask();
    }

    @Override
    public int getRatePHYS() {
        return _damSkills.size() == 0 ? 25 : 0;
    }

    @Override
    public int getRateDOT() {
        return 25;
    }

    @Override
    public int getRateDEBUFF() {
        return 25;
    }

    @Override
    public int getRateDAM() {
        return 100;
    }

    @Override
    public int getRateSTUN() {
        return 10;
    }

    @Override
    public int getRateBUFF() {
        return 10;
    }

    @Override
    public int getRateHEAL() {
        return 20;
    }
}