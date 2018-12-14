package l2trunk.gameserver.model.entity.events.objects;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.events.GlobalEvent;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.attachment.FlagItemAttachment;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ItemFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CTFCombatFlagObject implements SpawnableObject, FlagItemAttachment {
    private static final Logger _log = LoggerFactory.getLogger(CTFCombatFlagObject.class);
    private ItemInstance _item;

    private GlobalEvent _event;

    @Override
    public void spawnObject(GlobalEvent event) {
        if (_item != null) {
            _log.info("FortressCombatFlagObject: can't spawn twice: " + event);
            return;
        }
        _item = ItemFunctions.createItem(9819);
        _item.setAttachment(this);

        _event = event;
    }

    public ItemInstance getItem() {
        return _item;
    }

    @Override
    public void setItem(ItemInstance item) {
        // ignored
    }

    @Override
    public void despawnObject(GlobalEvent event) {
        if (_item == null)
            return;

        Player owner = GameObjectsStorage.getPlayer(_item.getOwnerId());
        if (owner != null) {
            owner.getInventory().destroyItem(_item, "CTF Combat Flag");
            owner.sendDisarmMessage(_item);
        }

        _item.setAttachment(null);
        _item.setJdbcState(JdbcEntityState.UPDATED);
        _item.delete();

        _item.deleteMe();
        _item = null;

        _event = null;
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
        despawnObject(_event);
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

    public GlobalEvent getEvent() {
        return _event;
    }

    @Override
    public boolean canPickUp(Player player) {
        return false;
    }

    @Override
    public void pickUp(Player player) {

    }

    @Override
    public boolean canBeLost() {
        return false;
    }

    @Override
    public boolean canBeUnEquiped() {
        return false;
    }
}
