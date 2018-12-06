package l2trunk.gameserver.model.entity.CCPHelpers;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.InventoryUpdate;
import l2trunk.gameserver.network.serverpackets.NetPingPacket;
import l2trunk.gameserver.scripts.Functions;

import java.util.ArrayList;
import java.util.List;

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
                    Functions.addItem(activeChar, 9600, a, "Opening TOAD");
                }
                if (b > 0) {
                    Functions.addItem(activeChar, 9601, b, "Opening TOAD");
                }
                if (c > 0) {
                    Functions.addItem(activeChar, 9602, c, "Opening TOAD");
                }
            } else {
                activeChar.sendMessage("You do not have enough Ancient Tomes of the Demon.");
            }
        } else {
            activeChar.sendMessage("You do not have enough Ancient Tomes of the Demon.");
        }
    }

    public static void setAntiGrief(Player activeChar) {
        if (!activeChar.getVarB("antigrief", false)) {
            activeChar.setVar("antigrief", "true", -1);
            activeChar.sendMessage("You are now PROTECTED from unwanted buffs!");
        } else {
            activeChar.unsetVar("antigrief");
            activeChar.sendMessage("You are NO LONGER protected from unwanted buffs!");
        }
    }

    public static String showOnlineCount() {
        return "Players Online: " + GameObjectsStorage.getAllPlayers().size();
    }

    public static boolean getPing(Player activeChar) {
        activeChar.sendMessage("Processing request...");
        activeChar.sendPacket(new NetPingPacket(activeChar));
        ThreadPoolManager.INSTANCE.schedule(new AnswerTask(activeChar), 3000L);
        return true;
    }

    public static void combineTalismans(Player activeChar) {
        List<int[]> sameIds = new ArrayList<>();

        for (ItemInstance item : activeChar.getInventory().getItems()) {
            // Getting talisman
            if (item.getLifeTime() > 0 && item.getName().contains("Talisman")) {
                talismanAddToCurrent(sameIds, item.getItemId());
            }
        }

        int allCount = 0;
        int newCount = 0;
        for (int[] idCount : sameIds) {
            // Item Count > 1
            if (idCount[1] > 1) {
                int lifeTime = 0;
                List<ItemInstance> existingTalismans = activeChar.getInventory().getItemsByItemId(idCount[0]);
                for (ItemInstance existingTalisman : existingTalismans) {
                    lifeTime += existingTalisman.getLifeTime();
                    activeChar.getInventory().destroyItem(existingTalisman, "Combine Talismans");
                }

                ItemInstance newTalisman = activeChar.getInventory().addItem(idCount[0], 1L, "Combine Talismans");
                newTalisman.setLifeTime(lifeTime);
                newTalisman.setJdbcState(JdbcEntityState.UPDATED);
                newTalisman.update();
                activeChar.sendPacket(new InventoryUpdate().addModifiedItem(newTalisman));

                allCount += idCount[0];
                newCount++;
            }
        }

        if (allCount > 0)
            activeChar.sendMessage(allCount + " Talismans were combined into " + newCount);
        else
            activeChar.sendMessage("You don't have Talismans to combine!");
    }

    private static void talismanAddToCurrent(List<int[]> sameIds, int itemId) {
        for (int[] sameId : sameIds)
            if (sameId[0] == itemId) {
                sameId[1] = sameId[1] + 1;
                return;
            }
        sameIds.add(new int[]
                {
                        itemId,
                        1
                });
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
