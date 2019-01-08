package l2trunk.gameserver.listener.game;

import l2trunk.gameserver.listener.GameListener;

public interface OnSSPeriodListener extends GameListener {
    void onPeriodChange(int val);
}
