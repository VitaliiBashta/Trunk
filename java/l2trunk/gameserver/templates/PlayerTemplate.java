package l2trunk.gameserver.templates;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.templates.item.CreateItem;
import l2trunk.gameserver.utils.Location;

import java.util.List;


public final class PlayerTemplate extends CharTemplate {
    public final ClassId classId;

    public final Race race;

    public final Location spawnLoc = new Location();

    public final boolean isMale;

    public final int classBaseLevel;
    public final double lvlHpAdd;
    public final double lvlHpMod;
    public final double lvlCpAdd;
    public final double lvlCpMod;
    public final double lvlMpAdd;
    public final double lvlMpMod;

    private final List<CreateItem> items;

    public PlayerTemplate(int id, StatsSet set, boolean isMale, List<CreateItem> items) {
        super(set);
        classId = ClassId.getById(id);
        race = Race.of(set.getInteger("raceId"));

        spawnLoc.set(Location.of(set.getInteger("spawnX"), set.getInteger("spawnY"), set.getInteger("spawnZ")));

        this.isMale = isMale;

        classBaseLevel = set.getInteger("classBaseLevel");
        lvlHpAdd = set.getDouble("lvlHpAdd");
        lvlHpMod = set.getDouble("lvlHpMod");
        lvlCpAdd = set.getDouble("lvlCpAdd");
        lvlCpMod = set.getDouble("lvlCpMod");
        lvlMpAdd = set.getDouble("lvlMpAdd");
        lvlMpMod = set.getDouble("lvlMpMod");

        this.items = items;
    }

    public List<CreateItem> getItems() {
        return items;
    }
}