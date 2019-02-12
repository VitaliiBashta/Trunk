package l2trunk.gameserver.model.entity.events.impl;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.events.GlobalEvent;

public class UndergroundColiseumBattleEvent extends GlobalEvent {
    public UndergroundColiseumBattleEvent(Player player1, Player player2) {
        super(0, player1.objectId() + "_" + player2.objectId());
    }

    @Override
    public void announce(int val) {
        switch (val) {
            case -180:
            case -120:
            case -60:
                break;
        }
    }

    @Override
    public void reCalcNextTime(boolean onInit) {
        registerActions();
    }

    @Override
    protected long startTimeMillis() {
        return System.currentTimeMillis() + 180000L;
    }
}
