package l2trunk.gameserver.ai;

import l2trunk.gameserver.model.instances.NpcInstance;

public class Fighter extends DefaultAI {
    public Fighter(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        return super.thinkActive() || defaultThinkBuff();
    }

    @Override
    public boolean createNewTask() {
        return defaultFightTask();
    }

    @Override
    public int getRatePHYS() {
        return 30;
    }

    @Override
    public int getRateDOT() {
        return 20;
    }

    @Override
    public int getRateDEBUFF() {
        return 20;
    }

    @Override
    public int getRateDAM() {
        return 15;
    }

    @Override
    public int getRateSTUN() {
        return 30;
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