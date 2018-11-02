package l2f.gameserver.hwid;

import l2f.commons.threading.RunnableImpl;
import l2f.gameserver.model.Player;
import l2f.gameserver.network.clientpackets.EnterWorld;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClickersDetector {
    private static final Logger _log = LoggerFactory.getLogger(EnterWorld.class);

    public ClickersDetector(Player player) {
    }


    private static class Kick extends RunnableImpl {
        private String HWID;

        private Kick(String hwid) {
            HWID = hwid;
        }

        @Override
        public void runImpl() {
            for (Player player : HwidEngine.getInstance().getGamerByHwid(HWID).getOnlineChars()) {
                if (player.getNetConnection() != null)
                    player.getNetConnection().closeNow(false);
            }
        }

    }
}
