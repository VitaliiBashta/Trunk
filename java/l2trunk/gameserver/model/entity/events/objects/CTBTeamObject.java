package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.residences.clanhall.CTBBossInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

public class CTBTeamObject implements SpawnableObject {
    private final NpcTemplate _mobTemplate;
    private final NpcTemplate _flagTemplate;
    private final Location _flagLoc;
    private CTBSiegeClanObject _siegeClan;
    private NpcInstance flag;
    private CTBBossInstance mob;

    public CTBTeamObject(int mobTemplate, int flagTemplate, Location flagLoc) {
        _mobTemplate = NpcHolder.getTemplate(mobTemplate);
        _flagTemplate = NpcHolder.getTemplate(flagTemplate);
        _flagLoc = flagLoc;
    }

    @Override
    public void spawnObject(GlobalEvent event) {
        if (flag == null) {
            flag = new NpcInstance(IdFactory.getInstance().getNextId(), _flagTemplate);
            flag.setFullHpMp();
            ;
            flag.setHasChatWindow(false);
            flag.spawnMe(_flagLoc);
        } else if (mob == null) {
            NpcTemplate template = _siegeClan == null || _siegeClan.getParam() == 0 ? _mobTemplate : NpcHolder.getTemplate((int) _siegeClan.getParam());

            mob = (CTBBossInstance) template.getNewInstance();
            mob.setFullHpMp();
            mob.setMatchTeamObject(this);
            mob.addEvent(event);

            int x = (int) (_flagLoc.x + 300 * Math.cos(mob.headingToRadians(flag.getHeading() - 32768)));
            int y = (int) (_flagLoc.y + 300 * Math.sin(mob.headingToRadians(flag.getHeading() - 32768)));

            Location loc = new Location(x, y, flag.getZ(), flag.getHeading());
            mob.setSpawnedLoc(loc);
            mob.spawnMe(loc);
        } else
            throw new IllegalArgumentException("Cant spawn twice");
    }

    @Override
    public void despawnObject(GlobalEvent event) {
        if (mob != null) {
            mob.deleteMe();
            mob = null;
        }
        if (flag != null) {
            flag.deleteMe();
            flag = null;
        }
        _siegeClan = null;
    }

    @Override
    public void refreshObject(GlobalEvent event) {

    }

    public CTBSiegeClanObject getSiegeClan() {
        return _siegeClan;
    }

    public void setSiegeClan(CTBSiegeClanObject siegeClan) {
        _siegeClan = siegeClan;
    }

    public boolean isParticle() {
        return flag != null && mob != null;
    }

    public NpcInstance getFlag() {
        return flag;
    }
}
