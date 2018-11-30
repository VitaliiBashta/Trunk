package l2trunk.scripts.npc.model.events;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public class FurnfaceInstance extends NpcInstance {
    public FurnfaceInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setTargetable(false);
    }

    public void setActive2114001(int i) {
        setTargetable(false);
        if (getAISpawnParam() == i) {
            setNpcState(1);
            ThreadPoolManager.INSTANCE.schedule(new OFF_TIMER(), 2 * 1000);
        }
    }

    public void setActive2114002() {
        setTargetable(false);
        setNpcState(1);
        ThreadPoolManager.INSTANCE.schedule(new OFF_TIMER(), 2 * 1000);
    }

    public void setSCE_GAME_PLAYER_START() {
        setNpcState(1);
        ThreadPoolManager.INSTANCE.schedule(new OFF_TIMER(), 2 * 1000);
        setTargetable(true);
    }

    public void setSCE_GAME_END() {
        setNpcState(1);
        ThreadPoolManager.INSTANCE.schedule(new OFF_TIMER(), 2 * 1000);
        setTargetable(false);
    }

    public void setSCE_GAME_FAILURE() {
        setTargetable(false);
        setNpcState(1);
        ThreadPoolManager.INSTANCE.schedule(new OFF_TIMER(), 2 * 1000);
    }

    private class OFF_TIMER extends RunnableImpl {
        @Override
        public void runImpl() {
            setNpcState(2);
        }
    }
}