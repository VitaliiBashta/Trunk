package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.entity.events.impl.FortressSiegeEvent;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.attachment.FlagItemAttachment;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class FortressCombatFlagObject implements SpawnableObject, FlagItemAttachment {
    private static final Logger _log = LoggerFactory.getLogger(FortressCombatFlagObject.class);
    private final Location _location;
    private ItemInstance item;
    private GlobalEvent event;

    public FortressCombatFlagObject(Location location) {
        _location = location;
    }

    @Override
    public void spawnObject(GlobalEvent event) {
        if (item != null) {
            _log.info("FortressCombatFlagObject: can't spawn twice: " + event);
            return;
        }
        item = ItemFunctions.createItem(9819);
        item.setAttachment(this);
        item.dropMe(null, _location);
        item.setTimeToDeleteAfterDrop(0);

        this.event = event;
    }

    @Override
    public void despawnObject(GlobalEvent event) {
        if (item == null)
            return;

        Player owner = GameObjectsStorage.getPlayer(item.getOwnerId());
        if (owner != null) {
            owner.getInventory().destroyItem(item, "Fortress Combat Flag");
            owner.sendDisarmMessage(item);
        }

        item.setAttachment(null);
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.delete();

        item.deleteMe();
        item = null;

        this.event = null;
    }

    @Override
    public void refreshObject(GlobalEvent event) {

    }

    @Override
    public void onLogout(Player player) {
        onDeath(player, null);
    }

    @Override
    public void onDeath(Player owner, Creature killer) {
        owner.getInventory().removeItem(item, "Fortress Combat Flag");

        item.setOwnerId(0);
        item.setJdbcState(JdbcEntityState.UPDATED);
        item.update();

        owner.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));

        item.dropMe(null, _location);
        item.setTimeToDeleteAfterDrop(0);
    }

    @Override
    public boolean canPickUp(Player player) {
        if (player.getActiveWeaponFlagAttachment() != null)
            return false;
        FortressSiegeEvent event = player.getEvent(FortressSiegeEvent.class);
        if (event == null)
            return false;
        SiegeClanObject object = event.getSiegeClan(FortressSiegeEvent.ATTACKERS, player.getClan());
        return object != null;
    }

    @Override
    public void pickUp(Player player) {
        player.getInventory().equipItem(item);

        FortressSiegeEvent event = player.getEvent(FortressSiegeEvent.class);
        event.broadcastTo(new SystemMessage2(SystemMsg.C1_HAS_ACQUIRED_THE_FLAG).addName(player), FortressSiegeEvent.ATTACKERS, FortressSiegeEvent.DEFENDERS);
    }

    @Override
    public boolean canAttack(Player player) {
        player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_PERFORM_ANY_ATTACKS);
        return false;
    }

    @Override
    public boolean canCast(Player player, Skill skill) {
        List<Skill> skills = player.getActiveWeaponItem().getAttachedSkills();
        if (!skills.contains(skill)) {
            player.sendPacket(SystemMsg.THAT_WEAPON_CANNOT_USE_ANY_OTHER_SKILL_EXCEPT_THE_WEAPONS_SKILL);
            return false;
        } else
            return true;
    }

    @Override
    public boolean canBeLost() {
        return true;
    }


    public GlobalEvent getEvent() {
        return event;
    }
}
