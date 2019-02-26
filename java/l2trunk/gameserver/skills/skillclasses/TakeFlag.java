package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.model.entity.events.objects.TerritoryWardObject;
import l2trunk.gameserver.model.entity.residence.Dominion;
import l2trunk.gameserver.model.instances.residences.SiegeFlagInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

public final class TakeFlag extends Skill {
    public TakeFlag(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!super.checkCondition(player, target, forceUse, dontMove, first))
            return false;

        if (player.getClan() == null)
            return false;

        DominionSiegeEvent siegeEvent1 = player.getEvent(DominionSiegeEvent.class);
        if (siegeEvent1 == null)
            return false;

        if (!(player.getActiveWeaponFlagAttachment() instanceof TerritoryWardObject)) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (player.isMounted()) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (!(target instanceof SiegeFlagInstance) || target.getNpcId() != 36590 || (((SiegeFlagInstance)target).getClan() != player.getClan())) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        DominionSiegeEvent siegeEvent2 = target.getEvent(DominionSiegeEvent.class);
        if (siegeEvent2 == null || siegeEvent1 != siegeEvent2) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        return true;
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
            if (target != null) {
                Player player = (Player) activeChar;
                DominionSiegeEvent siegeEvent1 = player.getEvent(DominionSiegeEvent.class);
                if (siegeEvent1 == null)
                    return;
                if (!(target instanceof SiegeFlagInstance) || target.getNpcId() != 36590 || ((SiegeFlagInstance)target).getClan() != player.getClan())
                    return;
                if (!(player.getActiveWeaponFlagAttachment() instanceof TerritoryWardObject))
                    return;
                DominionSiegeEvent siegeEvent2 = target.getEvent(DominionSiegeEvent.class);
                if (siegeEvent2 == null || siegeEvent1 != siegeEvent2)
                    return;

                // текущая територия, к которой пойдет Вард
                Dominion dominion = siegeEvent1.getResidence();
                // вард с вражеской територии
                TerritoryWardObject wardObject = (TerritoryWardObject) player.getActiveWeaponFlagAttachment();
                // територия с которой уйдет Вард
                DominionSiegeEvent siegeEvent3 = wardObject.getEvent();
                Dominion dominion3 = siegeEvent3.getResidence();
                // айди територии к которой относится Вард
                int wardDominionId = wardObject.getDominionId();

                // удаляем с инвентарями вард, и освободжаем ресурсы
                wardObject.despawnObject(siegeEvent3);
                // удаляем Вард
                dominion3.removeFlag(wardDominionId);
                // добавляем Вард
                dominion.addFlag(wardDominionId);
                // позиции вардов с текущей територии
                // спавним Варда, уже в новой територии
                siegeEvent1.spawnAction("ward_" + wardDominionId, true);

                DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
                runnerEvent.broadcastTo(new SystemMessage2(SystemMsg.CLAN_S1_HAS_SUCCEEDED_IN_CAPTURING_S2S_TERRITORY_WARD).addString(dominion.getOwner().getName()).addResidenceName(wardDominionId));
            }
    }
}