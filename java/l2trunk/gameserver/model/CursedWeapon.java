package l2trunk.gameserver.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Skill.AddedSkill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CursedWeapon {
    private final String _name;
    private final int _itemId, _skillMaxLevel;
    private final int _skillId;
    private String _transformationName;
    private int _dropRate, _disapearChance;
    private int _durationMin, _durationMax, _durationLost;
    private int _transformationId, _transformationTemplateId;
    private int _stageKills, _nbKills = 0, _playerKarma = 0, _playerPkKills = 0;

    private CursedWeaponState _state = CursedWeaponState.NONE;
    private Location _loc = null;
    private long _endTime = 0, owner = 0;
    private ItemInstance _item = null;

    public CursedWeapon(int itemId, int skillId, String name) {
        _name = name;
        _itemId = itemId;
        _skillId = skillId;
        _skillMaxLevel = SkillTable.INSTANCE().getMaxLevel(_skillId);
    }

    public void initWeapon() {
        zeroOwner();
        setState(CursedWeaponState.NONE);
        _endTime = 0;
        _item = null;
        _nbKills = 0;
    }

    /**
     * Выпадение оружия из монстра
     */
    public void create(NpcInstance attackable, Player killer) {
        _item = ItemFunctions.createItem(_itemId);
        if (_item != null) {
            zeroOwner();
            setState(CursedWeaponState.DROPPED);

            if (_endTime == 0)
                _endTime = System.currentTimeMillis() + getRndDuration() * 60000;

            _item.dropToTheGround(attackable, Location.findPointToStay(attackable, 100));
            _loc = _item.getLoc();
            _item.setTimeToDeleteAfterDrop(0);

            // RedSky and Earthquake
            L2GameServerPacket redSky = new ExRedSky(10);
            L2GameServerPacket eq = new Earthquake(killer.getLoc(), 30, 12);
            for (Player player : GameObjectsStorage.getAllPlayersForIterate())
                player.sendPacket(redSky, eq);
        }
    }

    /**
     * Выпадение оружия из владельца, или исчезновение с определенной вероятностью.
     * Вызывается при смерти игрока.
     */
    public boolean dropIt(NpcInstance attackable, Player killer, Player owner) {
        if (Rnd.chance(_disapearChance))
            return false;

        Player player = getOnlineOwner();
        if (player == null) {
            if (owner == null)
                return false;
            player = owner;
        }

        ItemInstance oldItem;
        if ((oldItem = player.getInventory().removeItemByItemId(_itemId, 1L, "CursedWeaponDrop")) == null)
            return false;

        player.setKarma(_playerKarma);
        player.setPkKills(_playerPkKills);
        player.setCursedWeaponEquippedId(0);
        player.setTransformation(0);
        player.setTransformationName(null);
        player.validateLocation(0);

        Skill skill = SkillTable.INSTANCE().getInfo(_skillId, player.getSkillLevel(_skillId));
        if (skill != null)
            for (AddedSkill s : skill.getAddedSkills())
                player.removeSkillById(s.id);

        player.removeSkillById(_skillId);

        player.abortAttack(true, false);

        zeroOwner();
        setState(CursedWeaponState.DROPPED);

        oldItem.dropToTheGround(player, Location.findPointToStay(player, 100));
        _loc = oldItem.getLoc();

        oldItem.setTimeToDeleteAfterDrop(0);
        _item = oldItem;

        player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_DROPPED_S1).addItemName(oldItem.getItemId()));
        player.broadcastUserInfo(true);
        player.broadcastPacket(new Earthquake(player.getLoc(), 30, 12));

        return true;
    }

    private void giveSkill(Player player) {
        for (Skill s : getSkills(player)) {
            player.addSkill(s, false);
            player._transformationSkills.put(s.getId(), s);
        }
        player.sendPacket(new SkillList(player));
    }

    private Collection<Skill> getSkills(Player player) {
        int level = 1 + _nbKills / _stageKills;
        if (level > _skillMaxLevel)
            level = _skillMaxLevel;

        Skill skill = SkillTable.INSTANCE().getInfo(_skillId, level);
        List<Skill> ret = new ArrayList<>();
        ret.add(skill);
        for (AddedSkill s : skill.getAddedSkills())
            ret.add(SkillTable.INSTANCE().getInfo(s.id, s.level));
        return ret;
    }

    /**
     * вызывается при загрузке оружия
     */
    public boolean reActivate() {
        if (getTimeLeft() <= 0) {
            if (getPlayerId() != 0) // to be sure, that cursed weapon will deleted in right way
                setState(CursedWeaponState.ACTIVATED);
            return false;
        }

        if (getPlayerId() == 0) {
            if (_loc == null || (_item = ItemFunctions.createItem(_itemId)) == null)
                return false;

            _item.dropMe(null, _loc);
            _item.setTimeToDeleteAfterDrop(0);

            setState(CursedWeaponState.DROPPED);
        } else
            setState(CursedWeaponState.ACTIVATED);
        return true;
    }

    public void activate(Player player, ItemInstance item) {
        if (isDropped() || getPlayerId() != player.getObjectId()) // оружие уже в руках игрока или новый игрок
        {
            _playerKarma = player.getKarma();
            _playerPkKills = player.getPkKills();
        }

        setPlayer(player);
        setState(CursedWeaponState.ACTIVATED);

        player.leaveParty();
        if (player.isMounted())
            player.setMount(0, 0, 0);

        _item = item;

        player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
        player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, null);
        player.getInventory().setPaperdollItem(Inventory.PAPERDOLL_RHAND, _item);

        player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_EQUIPPED_YOUR_S1).addItemName(_item.getItemId()));

        player.setTransformation(0);
        player.setCursedWeaponEquippedId(_itemId);
        player.setTransformation(_transformationId);
        player.setTransformationName(_transformationName);
        player.setTransformationTemplate(_transformationTemplateId);
        player.setKarma(9999999);
        player.setPkKills(_nbKills);

        if (_endTime == 0)
            _endTime = System.currentTimeMillis() + getRndDuration() * 60000;

        giveSkill(player);

        player.setCurrentHpMp(player.getMaxHp(), player.getMaxMp());
        player.setCurrentCp(player.getMaxCp());
        player.broadcastUserInfo(true);
    }

    public void increaseKills() {
        Player player = getOnlineOwner();
        if (player == null)
            return;

        _nbKills++;
        player.setPkKills(_nbKills);
        player.updateStats();
        if (_nbKills % _stageKills == 0 && _nbKills <= _stageKills * (_skillMaxLevel - 1))
            giveSkill(player);
        _endTime -= _durationLost * 60000; // Reduce time-to-live
    }

    public void setDisapearChance(int disapearChance) {
        _disapearChance = disapearChance;
    }

    public void setDurationMin(int duration) {
        _durationMin = duration;
    }

    public void setDurationMax(int duration) {
        _durationMax = duration;
    }

    public void setDurationLost(int durationLost) {
        _durationLost = durationLost;
    }

    public int getTransformationId() {
        return _transformationId;
    }

    public void setTransformationId(int transformationId) {
        _transformationId = transformationId;
    }

    public void setTransformationTemplateId(int transformationTemplateId) {
        _transformationTemplateId = transformationTemplateId;
    }

    public void setTransformationName(String name) {
        _transformationName = name;
    }

    private void zeroOwner() {
        owner = 0;
        _playerKarma = 0;
        _playerPkKills = 0;
    }

    private CursedWeaponState getState() {
        return _state;
    }

    private void setState(CursedWeaponState state) {
        _state = state;
    }

    public boolean isActivated() {
        return getState() == CursedWeaponState.ACTIVATED;
    }

    public boolean isDropped() {
        return getState() == CursedWeaponState.DROPPED;
    }

    public long getEndTime() {
        return _endTime;
    }

    public void setEndTime(long endTime) {
        _endTime = endTime;
    }

    public String getName() {
        return _name;
    }

    public int getItemId() {
        return _itemId;
    }

    public ItemInstance getItem() {
        return _item;
    }

    public void setItem(ItemInstance item) {
        _item = item;
    }

    public int getSkillId() {
        return _skillId;
    }

    public int getDropRate() {
        return _dropRate;
    }

    public void setDropRate(int dropRate) {
        _dropRate = dropRate;
    }

    public int getPlayerId() {
        return owner == 0 ? 0 : GameObjectsStorage.getStoredObjectId(owner);
    }

    public void setPlayerId(int playerId) {
        owner = playerId == 0 ? 0 : GameObjectsStorage.objIdNoStore(playerId);
    }

    public Player getPlayer() {
        return owner == 0 ? null : GameObjectsStorage.getAsPlayer(owner);
    }

    public void setPlayer(Player player) {
        if (player != null)
            owner = player.getStoredId();
        else if (owner != 0)
            setPlayerId(getPlayerId()); // для того что бы сохранить objId, но не искать игрока в хранилище
    }

    public int getPlayerKarma() {
        return _playerKarma;
    }

    public void setPlayerKarma(int playerKarma) {
        _playerKarma = playerKarma;
    }

    public int getPlayerPkKills() {
        return _playerPkKills;
    }

    public void setPlayerPkKills(int playerPkKills) {
        _playerPkKills = playerPkKills;
    }

    public int getNbKills() {
        return _nbKills;
    }

    public void setNbKills(int nbKills) {
        _nbKills = nbKills;
    }

    public int getStageKills() {
        return _stageKills;
    }

    public void setStageKills(int stageKills) {
        _stageKills = stageKills;
    }

    /**
     * Возвращает позицию (x, y, z)
     *
     * @return Location
     */
    public Location getLoc() {
        return _loc;
    }

    public void setLoc(Location loc) {
        _loc = loc;
    }

    public int getRndDuration() {
        if (_durationMin > _durationMax)
            _durationMax = 2 * _durationMin;
        return Rnd.get(_durationMin, _durationMax);
    }

    public boolean isActive() {
        return isActivated() || isDropped();
    }

    public int getLevel() {
        return Math.min(1 + (_nbKills / _stageKills), _skillMaxLevel);
    }

    public long getTimeLeft() {
        return _endTime - System.currentTimeMillis();
    }

    public Location getWorldPosition() {
        if (isActivated()) {
            Player player = getOnlineOwner();
            if (player != null)
                return player.getLoc();
        } else if (isDropped())
            if (_item != null)
                return _item.getLoc();

        return null;
    }

    public Player getOnlineOwner() {
        Player player = getPlayer();
        return player != null && player.isOnline() ? player : null;
    }

    public boolean isOwned() {
        return owner != 0;
    }

    public enum CursedWeaponState {
        NONE,
        ACTIVATED,
        DROPPED,
    }
}