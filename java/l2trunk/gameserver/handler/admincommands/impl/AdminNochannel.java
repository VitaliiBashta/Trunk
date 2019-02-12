package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.AdminFunctions;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Util;

public class AdminNochannel implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanBanChat)
            return false;

        int banChatCount = 0;
        int penaltyCount;
        int banChatCountPerDay = activeChar.getPlayerAccess().BanChatCountPerDay;
        if (banChatCountPerDay > 0) {
            banChatCount = activeChar.getVarInt("banChatCount");

            penaltyCount = activeChar.getVarInt("penaltyChatCount");

            long LastBanChatDayTime =  activeChar.getVarLong("LastBanChatDayTime");

            if (LastBanChatDayTime != 0) {
                if (System.currentTimeMillis() - LastBanChatDayTime < 1000 * 60 * 60 * 24) {
                    if (banChatCount >= banChatCountPerDay) {
                        activeChar.sendMessage("At night, you can not give more " + banChatCount + " bans chat.");
                        return false;
                    }
                } else {
                    int bonus_mod = 1; // Убрать, если потребуется сделать зависимость бонуса от количества банов
                    if (activeChar.getPlayerAccess().BanChatBonusId > 0 && activeChar.getPlayerAccess().BanChatBonusCount > 0) {
                        int add_count = activeChar.getPlayerAccess().BanChatBonusCount * bonus_mod;

                        ItemTemplate item = ItemHolder.getTemplate(activeChar.getPlayerAccess().BanChatBonusId);
                        activeChar.sendMessage("Bonus for moderation: " + add_count + " " + item.getName());

                        if (penaltyCount > 0) // У модератора был штраф за нарушения
                        {
                            activeChar.sendMessage("Fine for violation: " + penaltyCount + " " + item.getName());
                            activeChar.setVar("penaltyChatCount",  Math.max(0, penaltyCount - add_count)); // Уменьшаем штраф
                            add_count -= penaltyCount; // Вычитаем штраф из бонуса
                        }

                        if (add_count > 0)
                            ItemFunctions.addItem(activeChar, activeChar.getPlayerAccess().BanChatBonusId, add_count, "AdminNoChannel");
                    }
                    activeChar.setVar("LastBanChatDayTime", System.currentTimeMillis());
                    activeChar.setVar("banChatCount", 0);
                    banChatCount = 0;
                }
            } else
                activeChar.setVar("LastBanChatDayTime", System.currentTimeMillis());
        }

        switch (command) {
            case admin_nochannel:
            case admin_nc: {
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //nochannel charName [period] [reason]");
                    return false;
                }
                int timeval = 30; // if no args, then 30 min default.
                if (wordList.length > 2)
                    try {
                        timeval = Integer.parseInt(wordList[2]);
                    } catch (Exception E) {
                        timeval = 30;
                    }

                String msg = AdminFunctions.banChat(activeChar, null, wordList[1], timeval, wordList.length > 3 ? Util.joinStrings(" ", wordList, 3) : null);
                activeChar.sendMessage(msg);

                if (banChatCountPerDay > -1 && msg.startsWith("You are banned from chat")) {
                    banChatCount++;
                    activeChar.setVar("banChatCount",  banChatCount);
                    activeChar.sendMessage("You have left " + (banChatCountPerDay - banChatCount) + " Bans chat.");
                }
            }
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_nochannel,
        admin_nc
    }
}