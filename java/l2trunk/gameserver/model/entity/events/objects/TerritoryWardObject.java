package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.EventHolder;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.entity.events.EventType;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.DominionSiegeRunnerEvent;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.TerritoryWardInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.attachment.FlagItemAttachment;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class TerritoryWardObject implements SpawnableObject, FlagItemAttachment {
    private static final long RETURN_FLAG_DELAY = 120000L;
    private final Location location;
    private final int itemId;
    private final NpcTemplate template;
    private NpcInstance wardNpcInstance;
    private ItemInstance wardItemInstance;
    private ScheduledFuture<?> startTimerTask;
    private ScheduledFuture<?> teleportBackTask;

    public TerritoryWardObject(int itemId, int npcId, Location location) {
        this.itemId = itemId;
        template = NpcHolder.getTemplate(npcId);
        this.location = location;
    }

    @Override
    public void spawnObject(GlobalEvent event) {
        wardItemInstance = ItemFunctions.createItem(itemId);
        wardItemInstance.setAttachment(this);

        wardNpcInstance = new TerritoryWardInstance(IdFactory.getInstance().getNextId(), template, this);
        wardNpcInstance.addEvent(event);
        wardNpcInstance.setFullHpMp();
        wardNpcInstance.spawnMe(location);
        startTimerTask = null;

        if (wardNpcInstance.getZone(ZoneType.SIEGE) != null) {
            wardNpcInstance.getZone(ZoneType.SIEGE).addListener(new OnZoneEnterLeaveListenerImpl());
        }

        ThreadPoolManager.INSTANCE.schedule(() -> {
            if (wardNpcInstance.getZone(ZoneType.SIEGE) != null) {
                wardNpcInstance.getZone(ZoneType.SIEGE).addListener(new OnZoneEnterLeaveListenerImpl());
            }
        }, 1000L);
    }

    private void stopTerrFlagCountDown() {
        if (startTimerTask == null)
            return;
        startTimerTask.cancel(false);
        startTimerTask = null;
    }

    @Override
    public void despawnObject(GlobalEvent event) {
        if (wardItemInstance == null || wardNpcInstance == null)
            return;

        Player owner = GameObjectsStorage.getPlayer(wardItemInstance.getOwnerId());
        owner.getInventory().destroyItem(wardItemInstance, "Territory Ward");
        owner.sendDisarmMessage(wardItemInstance);

        if (teleportBackTask != null)
            teleportBackTask.cancel(true);

        wardItemInstance.setAttachment(null);
        wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
        wardItemInstance.delete();
        wardItemInstance.deleteMe();
        wardItemInstance = null;

        wardNpcInstance.deleteMe();
        wardNpcInstance = null;

        stopTerrFlagCountDown();
    }

    @Override
    public void refreshObject(GlobalEvent event) {
        //
    }

    @Override
    public void onLogout(Player player) {
        if (player.getActiveWeaponInstance() != null) {
            player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
            player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, null);
        }
        player.getInventory().removeItem(wardItemInstance, "Territory Ward");

        wardItemInstance.setOwnerId(0);
        wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
        wardItemInstance.update();

        wardNpcInstance.setFullHpMp();
        wardNpcInstance.spawnMe(location);


        DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.broadcastTo(new ExShowScreenMessage("Territory Ward returned to the castle!"));

        stopTerrFlagCountDown();
    }

    @Override
    public void onDeath(Player owner, Creature killer) {
        Location loc = owner.getLoc();

        if (owner.getActiveWeaponInstance() != null) {
            owner.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
            owner.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, null);
        }
        owner.getInventory().removeItem(wardItemInstance, "Territory Ward");
        owner.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_DROPPED_S1).addName(wardItemInstance));

        wardItemInstance.setOwnerId(0);
        wardItemInstance.setJdbcState(JdbcEntityState.UPDATED);
        wardItemInstance.update();

        DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);

        wardNpcInstance.setFullHpMp();
        if (owner.isInZone(ZoneType.SIEGE)) {
            wardNpcInstance.spawnMe(loc);
            teleportBackTask = ThreadPoolManager.INSTANCE.schedule(new ReturnFlagThread(), RETURN_FLAG_DELAY);
        } else {
            wardNpcInstance.spawnMe(location);
            runnerEvent.broadcastTo(new ExShowScreenMessage("Territory Ward returned to the castle!", 3000, false));
        }

        runnerEvent.broadcastTo(new SystemMessage2(SystemMsg.THE_CHARACTER_THAT_ACQUIRED_S1S_WARD_HAS_BEEN_KILLED).addResidenceName(getDominionId()));
        stopTerrFlagCountDown();
    }

    @Override
    public boolean canPickUp(Player player) {
        return player.getActiveWeaponFlagAttachment() == null;
    }

    @Override
    public void pickUp(Player player) {
        player.getInventory().addItem(wardItemInstance, "Territory Ward");
        player.getInventory().equipItem(wardItemInstance);

        player.sendPacket(SystemMsg.YOUVE_ACQUIRED_THE_WARD);

        DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
        runnerEvent.broadcastTo(new SystemMessage2(SystemMsg.THE_S1_WARD_HAS_BEEN_DESTROYED_C2_NOW_HAS_THE_TERRITORY_WARD).addResidenceName(getDominionId()).addName(player));
        checkZoneForTerr(player);

        if (teleportBackTask != null)
            teleportBackTask.cancel(true);
    }

    private void checkZoneForTerr(Player player) {
        if (!player.isInZone(ZoneType.SIEGE)) {
            startTerrFlagCountDown(player);
        }
    }

    private void startTerrFlagCountDown(Player player) {
        if (startTimerTask != null) {
            startTimerTask.cancel(false);
            startTimerTask = null;
        }
        startTimerTask = ThreadPoolManager.INSTANCE.schedule(() -> {
            if (!player.isInZone(ZoneType.SIEGE))
                onLogout(player);
        }, Config.INTERVAL_FLAG_DROP * 1000);

        player.sendMessage("You've leaved the battle zone! The flag will dissapear in " + Config.INTERVAL_FLAG_DROP + " seconds!");

    }


    @Override
    public boolean canAttack(Player player) {
        player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS);
        return false;
    }

    @Override
    public boolean canCast(Player player, Skill skill) {
        List<Skill> skills = player.getActiveWeaponItem().getAttachedSkills();
        if (player.getActiveWeaponItem().getAttachedSkills() == null) {
            player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL);
            return false;
        }

        if (!skills.contains(skill)) {
            player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL);
            return false;
        }

        return true;
    }

    @Override
    public boolean canBeLost() {
        return true;
    }


    public Location getWardLocation() {
        if (wardItemInstance == null || wardNpcInstance == null)
            return null;

        if (wardItemInstance.getOwnerId() > 0) {
            Player player = GameObjectsStorage.getPlayer(wardItemInstance.getOwnerId());
            if (player != null)
                return player.getLoc();
        }

        return wardNpcInstance.getLoc();
    }

    public int getDominionId() {
        return itemId - 13479;
    }

    public DominionSiegeEvent getEvent() {
        return wardNpcInstance.getEvent(DominionSiegeEvent.class);
    }

    private class OnZoneEnterLeaveListenerImpl implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Player actor) {
        }

        @Override
        public void onZoneLeave(Zone zone, Player player) {
            if (wardItemInstance != null && wardItemInstance.getOwnerId() == player.objectId()) {
                checkZoneForTerr(player);
            }
        }
    }

    private class ReturnFlagThread implements Runnable {
        @Override
        public void run() {
            if (wardNpcInstance != null) {
                wardNpcInstance.teleToLocation(location);
                DominionSiegeRunnerEvent runnerEvent = EventHolder.getEvent(EventType.MAIN_EVENT, 1);
                runnerEvent.broadcastTo(new ExShowScreenMessage("Territory Ward returned to the castle!", 3000, false));
            }
        }
    }
}
