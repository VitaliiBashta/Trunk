package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.Experience;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.PetDataTable;

import static l2trunk.commons.lang.NumberUtils.toInt;

public class AdminLevel implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditChar)
            return false;

        GameObject target = activeChar.getTarget();
        if (!(target instanceof Player || target instanceof PetInstance)) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }
        int level;

        switch (command) {
            case admin_add_level:
            case admin_addLevel:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //addLevel occupation");
                    return false;
                }
                level = toInt(wordList[1], 1);

                setLevel(activeChar, target, level + ((Creature) target).getLevel());
                break;
            case admin_set_level:
            case admin_setLevel:
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //setLevel occupation");
                    return false;
                }
                level = toInt(wordList[1], 1);
                setLevel(activeChar, target, level);
                break;
        }

        return true;
    }

    private void setLevel(Player activeChar, GameObject target, int level) {
        if (target instanceof Player || target instanceof PetInstance) {
            if (level < 1 || level > Experience.getMaxLevel() + 1) {
                activeChar.sendMessage("You must specify occupation 1 - " + Experience.getMaxLevel() + 1);
                return;
            }
            if (target instanceof Player) {
                Player pTarget = (Player) target;
                long expToAdd = Experience.LEVEL[level] - pTarget.getExp();
                if (level > Experience.getMaxLevel())
                    expToAdd -= 1000L;

                int oldLvl = pTarget.getActiveClass().getLevel();

                pTarget.getActiveClass().addExp(expToAdd);
                pTarget.getActiveClass().addSp((long) Integer.MAX_VALUE);
                pTarget.sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE).addNumber(expToAdd));
                pTarget.levelSet(level - oldLvl);
                pTarget.updateStats();
                return;
            }
            long exp_add = PetDataTable.INSTANCE.getInfo(((PetInstance) target).getNpcId(), level).getExp() - ((PetInstance) target).getExp();
            ((PetInstance) target).addExpAndSp(exp_add, 0);
        } else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
        }
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_add_level,
        admin_addLevel,
        admin_set_level,
        admin_setLevel,
    }
}