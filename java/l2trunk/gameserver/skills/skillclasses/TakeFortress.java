package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.FortressCombatFlagObject;
import l2trunk.gameserver.model.entity.events.objects.StaticObjectObject;
import l2trunk.gameserver.model.instances.StaticObjectInstance;
import l2trunk.gameserver.model.items.attachment.ItemAttachment;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

public final class TakeFortress extends Skill {
    public TakeFortress(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!super.checkCondition(player, target, forceUse, dontMove, first))
            return false;

        GameObject flagPole = player.getTarget();
        if (!(flagPole instanceof StaticObjectInstance) || ((StaticObjectInstance) flagPole).getType() != 3) {
            player.sendPacket(SystemMsg.THE_TARGET_IS_NOT_A_FLAGPOLE_SO_A_FLAG_CANNOT_BE_DISPLAYED);
            return false;
        }

        if (first) {
            if (!World.getAroundCharacters(flagPole, skillRadius * 2, 100)
                    .filter(Creature::isCastingNow)
                    .filter(ch -> ch.getCastingSkill() == this)
                    .peek(ch ->
                            player.sendPacket(SystemMsg.A_FLAG_IS_ALREADY_BEING_DISPLAYED_ANOTHER_FLAG_CANNOT_BE_DISPLAYED))
                    .findFirst().isPresent())
                return false;
        }

        if (player.getClan() == null) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        FortressSiegeEvent siegeEvent = player.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (player.isMounted()) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        ItemAttachment attach = player.getActiveWeaponFlagAttachment();
        if (!(attach instanceof FortressCombatFlagObject) || ((FortressCombatFlagObject) attach).getEvent() != siegeEvent) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
            return false;
        }

        if (!player.isInRangeZ(target, castRange)) {
            player.sendPacket(SystemMsg.YOUR_TARGET_IS_OUT_OF_RANGE);
            return false;
        }

        if (first)
            siegeEvent.broadcastTo(new SystemMessage2(SystemMsg.S1_CLAN_IS_TRYING_TO_DISPLAY_A_FLAG).addString(player.getClan().getName()), CastleSiegeEvent.DEFENDERS);

        return true;
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        GameObject flagPole = activeChar.getTarget();
        if (!(flagPole instanceof StaticObjectInstance) || ((StaticObjectInstance) flagPole).getType() != 3)
            return;
        Player player = (Player) activeChar;
        FortressSiegeEvent siegeEvent = player.getEvent(FortressSiegeEvent.class);
        if (siegeEvent == null)
            return;

        StaticObjectObject object = siegeEvent.getFirstObject(FortressSiegeEvent.FLAG_POLE);
        if (((StaticObjectInstance) flagPole).getUId() != object.getUId())
            return;

        siegeEvent.processStep(player.getClan());
    }
}