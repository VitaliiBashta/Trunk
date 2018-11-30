package l2trunk.scripts.ai.residences;

import l2trunk.gameserver.model.instances.NpcInstance;

public final class SiegeGuardPriest extends SiegeGuard {
    public SiegeGuardPriest(NpcInstance actor) {
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
        return 35;
    }

    @Override
    public int getRateDEBUFF() {
        return 50;
    }

    @Override
    public int getRateDAM() {
        return 60;
    }

    @Override
    public int getRateSTUN() {
        return 10;
    }

    @Override
    public int getRateBUFF() {
        return 25;
    }

    @Override
    public int getRateHEAL() {
        return 90;
    }
}