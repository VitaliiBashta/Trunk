package l2trunk.gameserver.utils;

import l2trunk.gameserver.data.xml.holder.InstantZoneHolder;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.CommandChannel;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.templates.InstantZone;
import l2trunk.gameserver.templates.InstantZoneEntryType;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class ReflectionUtils {
    public static DoorInstance getDoor(int id) {
        return ReflectionManager.DEFAULT.getDoor(id);
    }

    public static Zone getZone(String name) {
        return ReflectionManager.DEFAULT.getZone(name);
    }

    public static List<Zone> getZonesByType(Zone.ZoneType zoneType) {
        Collection<Zone> zones = ReflectionManager.DEFAULT.getZones();
        return zones.stream()
                .filter(z -> z.getType() == zoneType)
                .collect(Collectors.toList());
    }

    public static Reflection enterReflection(Player invoker, int instancedZoneId) {
        InstantZone iz = InstantZoneHolder.getInstantZone(instancedZoneId);
        return enterReflection(invoker, new Reflection(), iz);
    }

    public static void enterReflection(Player invoker, Reflection r, int instancedZoneId) {
        InstantZone iz = InstantZoneHolder.getInstantZone(instancedZoneId);
        enterReflection(invoker, r, iz);
    }

    private static Reflection enterReflection(Player invoker, Reflection r, InstantZone iz) {
        r.init(iz);

        if (r.getReturnLoc() == null)
            r.setReturnLoc(invoker.getLoc());

        InstantZoneEntryType type = iz.getEntryType();
        //If type is command channel and can be also one Party(Impossible to make Command Channel)
        if (type == InstantZoneEntryType.COMMAND_CHANNEL && iz.getMinParty() <= 9)
            //If has only party, without command channel
            if (invoker.getParty() != null && !invoker.getParty().isInCommandChannel())
                type = InstantZoneEntryType.PARTY;

        switch (type) {
            case SOLO:
                if (iz.getRemovedItemId() > 0)
                    ItemFunctions.removeItem(invoker, iz.getRemovedItemId(), iz.getRemovedItemCount(),  "ReflectionUtils");
                if (iz.getGiveItemId() > 0)
                    ItemFunctions.addItem(invoker, iz.getGiveItemId(), iz.getGiveItemCount(), "ReflectionUtils");
                if (iz.isDispelBuffs())
                    invoker.dispelBuffs();
                if (iz.getSetReuseUponEntry() && iz.getResetReuse().next(System.currentTimeMillis()) > System.currentTimeMillis())
                    invoker.setInstanceReuse(iz.getId(), System.currentTimeMillis());
                invoker.setVar("backCoords", invoker.getLoc().toXYZString());
                if (iz.getTeleportCoord() != null)
                    invoker.teleToLocation(iz.getTeleportCoord(), r);
                break;
            case PARTY:
                Party party = invoker.getParty();

                party.setReflection(r);
                r.setParty(party);

                party.getMembersStream().forEach(member -> {
                    if (iz.getRemovedItemId() > 0)
                        ItemFunctions.removeItem(member, iz.getRemovedItemId(), iz.getRemovedItemCount(), "ReflectionUtils");
                    if (iz.getGiveItemId() > 0)
                        ItemFunctions.addItem(member, iz.getGiveItemId(), iz.getGiveItemCount(), "ReflectionUtils");
                    if (iz.isDispelBuffs())
                        member.dispelBuffs();
                    if (iz.getSetReuseUponEntry())
                        member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
                    member.setVar("backCoords", invoker.getLoc().toXYZString());
                    if (iz.getTeleportCoord() != null)
                        member.teleToLocation(iz.getTeleportCoord(), r);
                });
                break;
            case COMMAND_CHANNEL:
                Party commparty = invoker.getParty();
                CommandChannel cc = commparty.getCommandChannel();

                cc.setReflection(r);
                r.setCommandChannel(cc);

                for (Player member : cc) {
                    if (iz.getRemovedItemId() > 0)
                        ItemFunctions.removeItem(member, iz.getRemovedItemId(), iz.getRemovedItemCount(), "ReflectionUtils");
                    if (iz.getGiveItemId() > 0)
                        ItemFunctions.addItem(member, iz.getGiveItemId(), iz.getGiveItemCount(), "ReflectionUtils");
                    if (iz.isDispelBuffs())
                        member.dispelBuffs();
                    if (iz.getSetReuseUponEntry())
                        member.setInstanceReuse(iz.getId(), System.currentTimeMillis());
                    member.setVar("backCoords", invoker.getLoc().toXYZString());
                    member.teleToLocation(iz.getTeleportCoord(), r);
                }

                break;
        }

        return r;
    }
}
