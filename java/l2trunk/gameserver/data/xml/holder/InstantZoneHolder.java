package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.time.cron.SchedulingPattern;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.templates.InstantZone;

import java.util.*;
import java.util.stream.Collectors;

public final class InstantZoneHolder {
    private InstantZoneHolder() {
    }

    private static final Map<Integer, InstantZone> ZONES = new HashMap<>();

    public static void addInstantZone(InstantZone zone) {
        ZONES.put(zone.getId(), zone);
    }

    public static InstantZone getInstantZone(int id) {
        return ZONES.get(id);
    }

    private static SchedulingPattern getResetReuseById(int id) {
        InstantZone zone = getInstantZone(id);
        return zone == null ? null : zone.getResetReuse();
    }

    public static int getMinutesToNextEntrance(int id, Player player) {
        SchedulingPattern resetReuse = getResetReuseById(id);
        if (resetReuse == null)
            return 0;

        Long time = null;
        if (getSharedReuseInstanceIds(id) != null && !getSharedReuseInstanceIds(id).isEmpty()) {
            List<Long> reuses = new ArrayList<>();
            for (int i : getSharedReuseInstanceIds(id))
                if (player.getInstanceReuse(i) != null)
                    reuses.add(player.getInstanceReuse(i));
            if (!reuses.isEmpty()) {
                Collections.sort(reuses);
                time = reuses.get(reuses.size() - 1);
            }
        } else
            time = player.getInstanceReuse(id);
        if (time == null)
            return 0;
        return (int) Math.max((resetReuse.next(time) - System.currentTimeMillis()) / 60000L, 0);
    }


    private static List<Integer> getSharedReuseInstanceIds(int id) {
        return ZONES.values().stream()
                .filter(iz -> iz.getSharedReuseGroup() > 0)
                .filter(iz -> getInstantZone(id).getSharedReuseGroup() > 0)
                .filter(iz -> iz.getSharedReuseGroup() == getInstantZone(id).getSharedReuseGroup())
                .map(InstantZone::getId)
                .collect(Collectors.toList());

    }

    public static List<Integer> getSharedReuseInstanceIdsByGroup(int groupId) {
        if (groupId < 1)
            return Collections.emptyList();
        List<Integer> sharedInstanceIds = new ArrayList<>();
        for (InstantZone iz : ZONES.values())
            if (iz.getSharedReuseGroup() > 0 && iz.getSharedReuseGroup() == groupId)
                sharedInstanceIds.add(iz.getId());
        return sharedInstanceIds;
    }

    public static int size() {
        return ZONES.size();
    }

}
