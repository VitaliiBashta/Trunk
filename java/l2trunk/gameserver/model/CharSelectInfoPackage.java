package l2trunk.gameserver.model;

import l2trunk.gameserver.dao.ItemsDAO;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;

public final class CharSelectInfoPackage {
    private final ItemInstance[] paperdoll;
    private String name;
    private final int objectId;
    private int charId = 0x00030b7a;
    private long exp = 0;
    private int sp = 0;
    private int clanId = 0;
    private int race = 0;
    private int classId = 0;
    private int baseClassId = 0;
    private int deleteTimer = 0;
    private long lastAccess = 0L;
    private int face = 0;
    private int _hairStyle = 0;
    private int _hairColor = 0;
    private int _sex = 0;
    private int _level = 1;
    private int _karma = 0, _pk = 0, _pvp = 0;
    private int _maxHp = 0;
    private double _currentHp = 0;
    private int _maxMp = 0;
    private double _currentMp = 0;
    private int _accesslevel = 0;
    private int _x = 0, _y = 0, _z = 0;
    private int _vitalityPoints = 20000;

    public CharSelectInfoPackage(int objectId, String name) {
        this.objectId = objectId;
        this.name = name;
        paperdoll = new ItemInstance[Inventory.PAPERDOLL_MAX];
        ItemsDAO.INSTANCE.getItemsByOwnerIdAndLoc(objectId, ItemLocation.PAPERDOLL)
                .filter(item -> item.getEquipSlot() < Inventory.PAPERDOLL_MAX)
                .forEach(item ->
                        paperdoll[item.getEquipSlot()] = item);
    }

    public int getObjectId() {
        return objectId;
    }

    public int getCharId() {
        return charId;
    }

    public void setCharId(int charId) {
        this.charId = charId;
    }

    public int getClanId() {
        return clanId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getBaseClassId() {
        return baseClassId;
    }

    public void setBaseClassId(int baseClassId) {
        this.baseClassId = baseClassId;
    }

    public double getCurrentHp() {
        return _currentHp;
    }

    public void setCurrentHp(double currentHp) {
        _currentHp = currentHp;
    }

    public double getCurrentMp() {
        return _currentMp;
    }

    public void setCurrentMp(double currentMp) {
        _currentMp = currentMp;
    }

    public int getDeleteTimer() {
        return deleteTimer;
    }

    public void setDeleteTimer(int deleteTimer) {
        this.deleteTimer = deleteTimer;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(long lastAccess) {
        this.lastAccess = lastAccess;
    }

    public long getExp() {
        return exp;
    }

    public void setExp(long exp) {
        this.exp = exp;
    }

    public int getFace() {
        return face;
    }

    public void setFace(int face) {
        this.face = face;
    }

    public int getHairColor() {
        return _hairColor;
    }

    public void setHairColor(int hairColor) {
        _hairColor = hairColor;
    }

    public int getHairStyle() {
        return _hairStyle;
    }

    public void setHairStyle(int hairStyle) {
        _hairStyle = hairStyle;
    }

    public int getPaperdollAugmentationId(int slot) {
        ItemInstance item = paperdoll[slot];
        if (item != null && item.isAugmented())
            return item.getAugmentationId();
        return 0;
    }

    public int getPaperdollItemId(int slot) {
        ItemInstance item = paperdoll[slot];
        if (item != null) {
			/*
			int visualItemId = item.getVisualItemId();
			if (visualItemId != 0)
				return visualItemId;
			*/
            return item.getItemId();
        }
        return 0;
    }

    public int getPaperdollEnchantEffect(int slot) {
        ItemInstance item = paperdoll[slot];
        if (item != null)
            return item.getEnchantLevel();
        return 0;
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(int level) {
        _level = level;
    }

    public int getMaxHp() {
        return _maxHp;
    }

    public void setMaxHp(int maxHp) {
        _maxHp = maxHp;
    }

    public int getMaxMp() {
        return _maxMp;
    }

    public void setMaxMp(int maxMp) {
        _maxMp = maxMp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = race;
    }

    public int getSex() {
        return _sex;
    }

    public void setSex(int sex) {
        _sex = sex;
    }

    public int getSp() {
        return sp;
    }

    public void setSp(int sp) {
        this.sp = sp;
    }

    public int getKarma() {
        return _karma;
    }

    public void setKarma(int karma) {
        _karma = karma;
    }

    public int getAccessLevel() {
        return _accesslevel;
    }

    public void setAccessLevel(int accesslevel) {
        _accesslevel = accesslevel;
    }

    public int getX() {
        return _x;
    }

    public void setX(int x) {
        _x = x;
    }

    public int getY() {
        return _y;
    }

    public void setY(int y) {
        _y = y;
    }

    public int getZ() {
        return _z;
    }

    public void setZ(int z) {
        _z = z;
    }

    public int getPk() {
        return _pk;
    }

    public void setPk(int pk) {
        _pk = pk;
    }

    public int getPvP() {
        return _pvp;
    }

    public void setPvP(int pvp) {
        _pvp = pvp;
    }

    public int getVitalityPoints() {
        return _vitalityPoints;
    }

    public void setVitalityPoints(int points) {
        _vitalityPoints = points;
    }
}