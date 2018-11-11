package l2trunk.gameserver.listener.actor.player;

import l2trunk.gameserver.listener.PlayerListener;

public interface OnAnswerListener extends PlayerListener {
    void sayYes();

    void sayNo();
}
