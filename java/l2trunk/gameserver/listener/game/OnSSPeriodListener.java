package l2trunk.gameserver.listener.game;

import l2trunk.gameserver.listener.GameListener;

/**
 * @author VISTALL
 * @date 7:12/19.05.2011
 */
public interface OnSSPeriodListener extends GameListener {
    void onPeriodChange(int val);
}
