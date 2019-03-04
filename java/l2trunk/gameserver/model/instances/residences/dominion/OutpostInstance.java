package l2trunk.gameserver.model.instances.residences.dominion;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.geometry.Circle;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.instances.residences.SiegeFlagInstance;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncMul;
import l2trunk.gameserver.templates.ZoneTemplate;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class OutpostInstance extends SiegeFlagInstance {
    private Zone zone;

    public OutpostInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        Circle c = new Circle(getLoc(), 250);
        c.setZmax(World.MAP_MAX_Z);
        c.setZmin(World.MAP_MIN_Z);

        StatsSet set = new StatsSet();
        set.set("name", "");
        set.set("type", Zone.ZoneType.dummy);
        set.set("territory", new Territory().add(c));

        zone = new Zone(new ZoneTemplate(set));
        zone.setReflection(ReflectionManager.DEFAULT);
        zone.addListener(new OnZoneEnterLeaveListenerImpl());
        zone.setActive(true);
    }

    @Override
    public void onDelete() {
        super.onDelete();

        zone.setActive(false);
        zone = null;
    }

    private class OnZoneEnterLeaveListenerImpl implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Player actor) {
            DominionSiegeEvent siegeEvent = OutpostInstance.this.getEvent(DominionSiegeEvent.class);
            if (siegeEvent == null)
                return;

            if (actor.getEvent(DominionSiegeEvent.class) != siegeEvent)
                return;

            actor.addStatFunc(new FuncMul(Stats.REGENERATE_HP_RATE, 0x40, OutpostInstance.this, 2.));
            actor.addStatFunc(new FuncMul(Stats.REGENERATE_MP_RATE, 0x40, OutpostInstance.this, 2.));
            actor.addStatFunc(new FuncMul(Stats.REGENERATE_CP_RATE, 0x40, OutpostInstance.this, 2.));
        }

        @Override
        public void onZoneLeave(Zone zone, Player actor) {
            actor.removeStatsOwner(OutpostInstance.this);
        }
    }
}
