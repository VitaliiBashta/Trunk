package l2trunk.scripts.npc.model.events;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class FurnfaceInstance extends NpcInstance {
    public FurnfaceInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setTargetable(false);
    }

    void setActive2114001(int i) {
        setTargetable(false);
        if (getAISpawnParam() == i) {
            changeState();
        }
    }

    void setActive2114002() {
        setTargetable(false);
        changeState();
    }

    void setSCE_GAME_PLAYER_START() {
        changeState();
        setTargetable(true);
    }

    void setSCE_GAME_END() {
        changeState();
        setTargetable(false);
    }

    void setSCE_GAME_FAILURE() {
        setTargetable(false);
        changeState();
    }
    private void changeState(){
        setNpcState(1);
        ThreadPoolManager.INSTANCE.schedule(() -> setNpcState(2), 2 * 1000);
    }

}