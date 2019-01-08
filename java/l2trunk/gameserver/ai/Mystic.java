package l2trunk.gameserver.ai;

import l2trunk.gameserver.model.instances.NpcInstance;

public class Mystic extends DefaultAI {
    public Mystic(NpcInstance actor) {
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
        return _damSkills.size() == 0 ? 25 : 0;
    }

    @Override
    public int getRateDOT() {
        return 25;
    }

    @Override
    public int getRateDEBUFF() {
        return 20;
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