package l2trunk.gameserver.model.entity.CCPHelpers;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.network.serverpackets.NetPingPacket;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class CCPSmallCommands {
    private static final int DECREASE_LEVEL_REQUIREMENT_ID = 6673;
    private static final long DECREASE_LEVEL_REQUIREMENT_COUNT = 1L;

    public static void openToad(Player activeChar, long count) {
        if (activeChar.getInventory().getItemByItemId(9599) == null) {
            activeChar.sendMessage("You do not have enough Ancient Tomes of the Demon.");
            return;
        }

        if (count <= 0)
            count = activeChar.getInventory().getItemByItemId(9599).getCount();

        if (activeChar.getInventory().getItemByItemId(9599).getCount() >= count) {
            int a = 0, b = 0, c = 0, rnd;
            for (int i = 0; i < count; i++) {
                rnd = Rnd.get(100);

                if ((rnd <= 100) && (rnd >= 66)) {
                    a++;
                } else if ((rnd <= 65) && (rnd >= 31)) {
                    b++;
                } else if (rnd <= 30) {
                    c++;
                } else {
                    activeChar.sendMessage("You do not have enough Ancient Tomes of the Demon.");
                }
            }

            if (activeChar.getInventory().destroyItemByItemId(9599, a + b + c, "Opening TOAD")) {
                if (a > 0) {
                    addItem(activeChar, 9600, a);
                }
                if (b > 0) {
                    addItem(activeChar, 9601, b);
                }
                if (c > 0) {
                    addItem(activeChar, 9602, c);
                }
            } else {
                activeChar.sendMessage("You do not have enough Ancient Tomes of the Demon.");
            }
        } else {
            activeChar.sendMessage("You do not have enough Ancient Tomes of the Demon.");
        }
    }

    public static String showOnlineCount() {
        return "Players Online: " + GameObjectsStorage.getAllPlayersStream().count();
    }

    public static boolean getPing(Player activeChar) {
        activeChar.sendMessage("Processing request...");
        activeChar.sendPacket(new NetPingPacket(activeChar));
        ThreadPoolManager.INSTANCE.schedule(new AnswerTask(activeChar), 3000L);
        return true;
    }

    public static boolean decreaseLevel(Player activeChar, int levelsToRemove) {
        if (levelsToRemove <= 0) {
            activeChar.sendMessage("You need to write value above 0");
            return false;
        }

        if (levelsToRemove >= activeChar.getLevel()) {
            activeChar.sendMessage("Level to decrease cannot be bigger than " + (activeChar.getLevel() - 1));
            return false;
        }

        if (!activeChar.getInventory().destroyItemByItemId(DECREASE_LEVEL_REQUIREMENT_ID, DECREASE_LEVEL_REQUIREMENT_COUNT, "Decreasing Levels")) {
            activeChar.sendMessage("You don't have enough Festival Coins!");
            return false;
        }

        int oldLevel = activeChar.getLevel();
        int newLevel = oldLevel - levelsToRemove;
        long expToRemove = Experience.LEVEL[newLevel] - activeChar.getExp();
        expToRemove -= 1000L;

        activeChar.getActiveClass().addExp(expToRemove);
        activeChar.levelSet(newLevel - oldLevel);
        activeChar.updateStats();
        activeChar.sendMessage(levelsToRemove + " levels were decreased!");
        return true;
    }

    private static final class AnswerTask implements Runnable {
        private final Player player;

        AnswerTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            int ping = player.getPing();
            if (ping != -1) {
                player.sendMessage("Current ping: " + ping + " ms.");
            } else {
                player.sendMessage("The data from the client was not received.");
            }
        }
    }
}
