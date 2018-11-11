package l2trunk.scripts.npc.model.residences.castle;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import l2trunk.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.List;
import java.util.Set;

/**
 * @author VISTALL
 * @date 8:58/17.03.2011
 */
public class CastleFlameTowerInstance extends SiegeToggleNpcInstance {
    private Set<String> _zoneList;

    public CastleFlameTowerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onDeathImpl(Creature killer) {
        CastleSiegeEvent event = getEvent(CastleSiegeEvent.class);
        if (event == null || !event.isInProgress())
            return;

        for (String s : _zoneList) {
            List<CastleDamageZoneObject> objects = event.getObjects(s);
            for (CastleDamageZoneObject zone : objects)
                zone.getZone().setActive(false);
        }
    }

    @Override
    public void setZoneList(Set<String> set) {
        _zoneList = set;
    }
}
