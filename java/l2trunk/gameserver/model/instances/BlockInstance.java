package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public class BlockInstance extends NpcInstance {
    private boolean _isRed;

    public BlockInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    public boolean isRed() {
        return _isRed;
    }

    public void setRed(boolean red) {
        _isRed = red;
        broadcastCharInfo();
    }

    public void changeColor() {
        setRed(!_isRed);
    }

    @Override
    public void showChatWindow(Player player, int val) {
    }

    @Override
    public boolean isNameAbove() {
        return false;
    }

    @Override
    public int getFormId() {
        return _isRed ? 0x53 : 0;
    }

    @Override
    public boolean isInvul() {
        return true;
    }
}
