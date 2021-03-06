package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2trunk.gameserver.model.instances.ArtefactInstance;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

public final class TakeCastle extends Skill {
    public TakeCastle(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        Zone siegeZone = target.getZone(ZoneType.SIEGE);

        if (!super.checkCondition(player, target, forceUse, dontMove, first))
            return false;

        if (player.getClan() == null || !player.isClanLeader()) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        CastleSiegeEvent siegeEvent = player.getEvent(CastleSiegeEvent.class);
        if (siegeEvent == null) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (siegeEvent.getSiegeClan(CastleSiegeEvent.ATTACKERS, player.getClan()) == null || siegeEvent.getResidence().getId() != siegeZone.getParams().getInteger("residence")) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (player.isMounted()) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (!player.isInRangeZ(target, 185)) {
            player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return false;
        }

        if (first)
            siegeEvent.broadcastTo(SystemMsg.THE_OPPOSING_CLAN_HAS_STARTED_TO_ENGRAVE_THE_HOLY_ARTIFACT, CastleSiegeEvent.DEFENDERS);

        return true;
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
            if (target instanceof ArtefactInstance) {
                Player player = (Player) activeChar;

                CastleSiegeEvent siegeEvent = player.getEvent(CastleSiegeEvent.class);
                if (siegeEvent != null) {
                    IStaticPacket lostPacket = siegeEvent.getResidence().getOwner() != null ? new Say2(player.objectId, ChatType.CRITICAL_ANNOUNCE, siegeEvent.getResidence().getName() + " Castle", "Clan " + siegeEvent.getResidence().getOwner().getName() + " has lost " + siegeEvent.getResidence().getName() + " Castle") : null;
                    IStaticPacket winPacket = new Say2(player.objectId, ChatType.CRITICAL_ANNOUNCE, siegeEvent.getResidence().getName() + " Castle", "Clan " + player.getClan().getName() + " has taken " + siegeEvent.getResidence().getName() + " Castle");
                    GameObjectsStorage.getAllPlayersStream().forEach(pl -> {
                        if (lostPacket != null)
                            pl.sendPacket(lostPacket);
                        pl.sendPacket(winPacket);
                    });
                    siegeEvent.processStep(player.getClan());
                }
            }
    }
}