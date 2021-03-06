package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.items.IItemHandler;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;

import java.util.HashMap;
import java.util.List;

public final class ACP implements IVoicedCommandHandler {
    private static final List<String> COMMANDS = List.of("acpon", "acpoff");

    // @VoicedCommand "ACP": items id's
    private static final int ID_HEAL_CP = 5592;
    private static final int ID_HEAL_MP = 728;
    private static final int ID_HEAL_HP = 1539;

    // ACP system requirements of occupation for character
    // Can be relocated into Config file in order to avoid compiling
    private static final int ACP_MIN_LVL = 0;
    private static final int ACP_HP_LVL = 1;
    private static final int ACP_CP_LVL = 1;
    private static final int ACP_MP_LVL = 1;
    // ACP system re-use time in mili-seconds
    private static final int ACP_MILI_SECONDS_FOR_LOOP = 1000;

    // ACP system CP/HP/MP
    private static final boolean ACP_CP = true;
    private static final boolean ACP_MP = true;
    private static final boolean ACP_HP = true;
    private static final HashMap<String, Thread> userAcpMap = new HashMap<>();

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        // Check if getPlayer exists in the world
        if (activeChar == null) {
            return false;
        }

        if (command.equals("acpon")) {
            if (userAcpMap.containsKey(activeChar.toString())) {
                activeChar.sendMessage("[ACP]: Already enabled!");
            } else {
                activeChar.sendMessage("[ACP]: System has been enabled!");
                Thread t = new Thread(new AcpHealer(activeChar));
                userAcpMap.put(activeChar.toString(), t);
                t.start();
                return true;
            }
        } else if (command.equals("acpoff")) {
            if (!userAcpMap.containsKey(activeChar.toString())) {
                activeChar.sendMessage("[ACP]: System has not been enabled!");
            } else {
                userAcpMap.remove(activeChar.toString()) // here we get thread and remove it from map
                        .interrupt(); // and interrupt it
                activeChar.sendMessage("[ACP]: System has been disabled!");
            }
        }
        return false;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMANDS;
    }

    private class AcpHealer implements Runnable {
        private final Player player;

        AcpHealer(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // Check for occupation requirements
                    if (player.getLevel() >= ACP_MIN_LVL) {
                        ItemInstance cpBottle = player.inventory.getItemByItemId(ID_HEAL_CP);
                        ItemInstance hpBottle = player.inventory.getItemByItemId(ID_HEAL_HP);
                        ItemInstance mpBottle = player.inventory.getItemByItemId(ID_HEAL_MP);

                        if (hpBottle != null && hpBottle.getCount() > 0) {
                            // Check condition of stats(HP)
                            if ((player.getCurrentHp() / player.getMaxHp()) * 100 < ACP_HP_LVL && ACP_HP) {
                                IItemHandler handlerHP = hpBottle.getTemplate().getHandler();
                                if (handlerHP != null) {
                                    handlerHP.useItem(player, hpBottle, false);
                                    player.sendMessage("[ACP]: HP has been restored.");
                                }
                            }
                            // Check condition of stats(CP)
                            if (cpBottle != null && cpBottle.getCount() > 0) {
                                if ((player.getCurrentCp() / player.getMaxCp()) * 100 < ACP_CP_LVL && ACP_CP) {
                                    IItemHandler handlerCP = cpBottle.getTemplate().getHandler();
                                    if (handlerCP != null) {
                                        handlerCP.useItem(player, cpBottle, false);
                                        player.sendMessage("[ACP]: CP has been restored.");
                                    }
                                }
                            }
                            // Check condition of stats(MP)
                            if (mpBottle != null && mpBottle.getCount() > 0) {
                                if ((player.getCurrentMp() / player.getMaxMp()) * 100 < ACP_MP_LVL && ACP_MP) {
                                    IItemHandler handlerMP = mpBottle.getTemplate().getHandler();
                                    if (handlerMP != null) {
                                        handlerMP.useItem(player, mpBottle, false);
                                        player.sendMessage("[ACP]: MP has been restored.");
                                    }
                                }
                            }
                        } else {
                            player.sendMessage("You don't have nothing to regenerate.");
                            return;
                        }
                    }
                    Thread.sleep(ACP_MILI_SECONDS_FOR_LOOP);
                }
            } catch (InterruptedException e) {
                // nothing
            } catch (Exception e) {
                Thread.currentThread().interrupt();
            } finally {
                userAcpMap.remove(player.toString());
            }
        }
    }
}
