package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.entity.events.objects.ZoneObject;
import l2trunk.gameserver.model.instances.residences.SiegeFlagInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncMul;

import java.util.List;

public final class SummonSiegeFlag extends Skill {
    private final FlagType flagType;
    private final double advancedMult;

    public SummonSiegeFlag(StatsSet set) {
        super(set);
        flagType = set.getEnum("flagType", FlagType.class);
        double temp = set.getDouble("advancedMultiplier");
        if (temp == 0) advancedMult = 1;
        else advancedMult = temp;
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!super.checkCondition(player, target, forceUse, dontMove, first))
            return false;

        if (player.getClan() == null || !player.isClanLeader())
            return false;


        switch (flagType) {
            case DESTROY:
                //
                break;
            case OUTPOST:
            case NORMAL:
            case ADVANCED:
                if (player.isInZone(Zone.ZoneType.RESIDENCE)) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
                    return false;
                }

                SiegeEvent siegeEvent = player.getEvent(SiegeEvent.class);
                if (siegeEvent == null) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
                    return false;
                }

                boolean inZone = false;
                List<ZoneObject> zones = siegeEvent.getObjects(SiegeEvent.FLAG_ZONES);
                for (ZoneObject zone : zones) {
                    if (player.isInZone(zone.getZone()))
                        inZone = true;
                }

                if (!inZone) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_SET_UP_A_BASE_HERE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
                    return false;
                }

                SiegeClanObject siegeClan = siegeEvent.getSiegeClan(siegeEvent.getClass() == DominionSiegeEvent.class ? SiegeEvent.DEFENDERS : SiegeEvent.ATTACKERS, player.getClan());
                if (siegeClan == null) {
                    player.sendPacket(SystemMsg.YOU_CANNOT_SUMMON_THE_ENCAMPMENT_BECAUSE_YOU_ARE_NOT_A_MEMBER_OF_THE_SIEGE_CLAN_INVOLVED_IN_THE_CASTLE__FORTRESS__HIDEOUT_SIEGE, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
                    return false;
                }

                if (siegeClan.getFlag() != null) {
                    player.sendPacket(SystemMsg.AN_OUTPOST_OR_HEADQUARTERS_CANNOT_BE_BUILT_BECAUSE_ONE_ALREADY_EXISTS, new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addSkillName(this));
                    return false;
                }
                break;
        }
        return true;
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        Player player = (Player) activeChar;

        Clan clan = player.getClan();
        if (clan == null || !player.isClanLeader())
            return;

        SiegeEvent siegeEvent = activeChar.getEvent(SiegeEvent.class);
        if (siegeEvent == null)
            return;

        SiegeClanObject siegeClan = siegeEvent.getSiegeClan(siegeEvent.getClass() == DominionSiegeEvent.class ? SiegeEvent.DEFENDERS : SiegeEvent.ATTACKERS, clan);
        if (siegeClan == null)
            return;

        if (flagType == FlagType.DESTROY) {
            siegeClan.deleteFlag();
        } else {
            if (siegeClan.getFlag() != null)
                return;

            // 35062/36590
            SiegeFlagInstance flag = (SiegeFlagInstance) NpcHolder.getTemplate(flagType == FlagType.OUTPOST ? 36590 : 35062).getNewInstance();
            flag.setClan(siegeClan);
            flag.addEvent(siegeEvent);

            if (flagType == FlagType.ADVANCED)
                flag.addStatFunc(new FuncMul(Stats.MAX_HP, 0x50, flag, advancedMult));

            flag.setFullHpMp();
            flag.setHeading(player.getHeading());

            // Ставим флаг перед чаром
            int x = (int) (player.getX() + 100 * Math.cos(player.headingToRadians(player.getHeading() - 32768)));
            int y = (int) (player.getY() + 100 * Math.sin(player.headingToRadians(player.getHeading() - 32768)));
            flag.spawnMe(GeoEngine.moveCheck(player.getX(), player.getY(), player.getZ(), x, y, player.getGeoIndex()));

            siegeClan.setFlag(flag);
        }
    }

    public enum FlagType {
        DESTROY,
        NORMAL,
        ADVANCED,
        OUTPOST
    }
}