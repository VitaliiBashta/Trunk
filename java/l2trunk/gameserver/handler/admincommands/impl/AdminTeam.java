package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class AdminTeam implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        TeamType team = TeamType.NONE;
        int range = -1;
        if (wordList.length >= 2) {
            for (TeamType t : TeamType.values()) {
                if (wordList[1].equalsIgnoreCase(t.name()))
                    team = t;
            }
            if (wordList.length >= 3)
                range = Integer.parseInt(wordList[2]);
        }

        if (range > 0) {
            TeamType team1 = team;
            World.getAroundPlayers(activeChar, range, 500)
                    .forEach(p -> p.setTeam(team1));
            activeChar.sendMessage("You have changed Team");
        } else {
            GameObject object = activeChar.getTarget();
            if (!(object instanceof Creature)) {
                activeChar.sendPacket(SystemMsg.INVALID_TARGET);
                return false;
            }

            ((Creature) object).setTeam(team);

            if (object instanceof Player) {
                Player pObject = (Player)object;
                if (pObject.isHero())
                    if (team != TeamType.NONE)
                        Hero.removeSkills(pObject);
                    else
                        Hero.addSkills(pObject);

                pObject.broadcastRelationChanged();
            }
        }
        return true;
    }

    @Override
    public  String getAdminCommand() {
        return "admin_setteam";
    }
}
