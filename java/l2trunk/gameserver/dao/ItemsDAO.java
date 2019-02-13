package l2trunk.gameserver.dao;

import l2trunk.commons.dao.JdbcDAO;
import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.items.ItemInstance.ItemLocation;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public enum ItemsDAO implements JdbcDAO<Integer, ItemInstance> {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(ItemsDAO.class);

    private final static String RESTORE_ITEM = "SELECT object_id, owner_id, item_id, count, enchant_level, loc, loc_data, custom_type1, custom_type2, life_time, custom_flags, augmentation_id, attribute_fire, attribute_water, attribute_wind, attribute_earth, attribute_holy, attribute_unholy, agathion_energy, visual_item_id FROM items WHERE object_id = ?";
    private final static String RESTORE_ITEMS = "SELECT object_id FROM items WHERE loc = ?";
    private final static String RESTORE_OWNER_ITEMS = "SELECT object_id FROM items WHERE owner_id = ? AND loc = ?";
    private final static String STORE_ITEM = "INSERT INTO items (object_id, owner_id, item_id, count, enchant_level, loc, loc_data, custom_type1, custom_type2, life_time, custom_flags, augmentation_id, attribute_fire, attribute_water, attribute_wind, attribute_earth, attribute_holy, attribute_unholy, agathion_energy, visual_item_id) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final static String UPDATE_ITEM = "UPDATE items SET owner_id = ?, item_id = ?, count = ?, enchant_level = ?, loc = ?, loc_data = ?, custom_type1 = ?, custom_type2 = ?, life_time = ?, custom_flags = ?, augmentation_id = ?, attribute_fire = ?, attribute_water = ?, attribute_wind = ?, attribute_earth = ?, attribute_holy = ?, attribute_unholy = ?, agathion_energy=?, visual_item_id=? WHERE object_id = ?";
    private final static String REMOVE_ITEM = "DELETE FROM items WHERE object_id = ?";
    private final AtomicLong load = new AtomicLong();
    private final AtomicLong insert = new AtomicLong();
    private final AtomicLong update = new AtomicLong();
    private final AtomicLong delete = new AtomicLong();
    //    private final static ItemsDAO instance = new ItemsDAO();
    private Cache cache;

    public void init() {
        cache = CacheManager.getInstance().getCache(ItemInstance.class.getName());
    }


    public Cache getCache() {
        return cache;
    }

    private ItemInstance load0(int objectId) throws SQLException {
        ItemInstance item;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_ITEM)) {
            statement.setInt(1, objectId);
            ResultSet rset = statement.executeQuery();
            item = load0(rset);
        }
        load.incrementAndGet();
        return item;
    }

    private ItemInstance load0(ResultSet rset) throws SQLException {
        ItemInstance item = null;

        if (rset.next()) {
            int objectId = rset.getInt(1);
            item = new ItemInstance(objectId);
            item.setOwnerId(rset.getInt(2));
            item.setItemId(rset.getInt(3));
            item.setCount(rset.getLong(4));
            item.setEnchantLevel(rset.getInt(5));
            item.setLocName(rset.getString(6));
            item.setLocData(rset.getInt(7));
            item.setCustomType1(rset.getInt(8));
            item.setCustomType2(rset.getInt(9));
            item.setLifeTime(rset.getInt(10));
            item.setCustomFlags(rset.getInt(11));
            item.setAugmentationId(rset.getInt(12));
            item.getAttributes().setFire(rset.getInt(13));
            item.getAttributes().setWater(rset.getInt(14));
            item.getAttributes().setWind(rset.getInt(15));
            item.getAttributes().setEarth(rset.getInt(16));
            item.getAttributes().setHoly(rset.getInt(17));
            item.getAttributes().setUnholy(rset.getInt(18));
            item.setAgathionEnergy(rset.getInt(19));
            item.setVisualItemId(rset.getInt(20));
        }

        return item;
    }

    private void save0(ItemInstance item, PreparedStatement statement) throws SQLException {
        statement.setInt(1, item.objectId());
        statement.setInt(2, item.getOwnerId());
        statement.setInt(3, item.getItemId());
        statement.setLong(4, item.getCount());
        statement.setInt(5, item.getEnchantLevel());
        statement.setString(6, item.getLocName());
        statement.setInt(7, item.getLocData());
        statement.setInt(8, item.getCustomType1());
        statement.setInt(9, item.getCustomType2());
        statement.setInt(10, item.getLifeTime());
        statement.setInt(11, item.getCustomFlags());
        statement.setInt(12, item.getAugmentationId());
        statement.setInt(13, item.getAttributes().getFire());
        statement.setInt(14, item.getAttributes().getWater());
        statement.setInt(15, item.getAttributes().getWind());
        statement.setInt(16, item.getAttributes().getEarth());
        statement.setInt(17, item.getAttributes().getHoly());
        statement.setInt(18, item.getAttributes().getUnholy());
        statement.setInt(19, item.getAgathionEnergy());
        statement.setInt(20, item.getVisualItemId());
    }

    private void save0(ItemInstance item) throws SQLException {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(STORE_ITEM)) {
            save0(item, statement);
            statement.execute();
        }
        insert.incrementAndGet();
    }

    private void delete0(ItemInstance item, PreparedStatement statement) throws SQLException {
        statement.setInt(1, item.objectId());
    }

    private void delete0(ItemInstance item) throws SQLException {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(REMOVE_ITEM)) {
            delete0(item, statement);
            statement.execute();
        }
        delete.incrementAndGet();
    }

    private void update0(ItemInstance item, PreparedStatement statement) throws SQLException {
        statement.setInt(20, item.objectId());
        statement.setInt(1, item.getOwnerId());
        statement.setInt(2, item.getItemId());
        statement.setLong(3, item.getCount());
        statement.setInt(4, item.getEnchantLevel());
        statement.setString(5, item.getLocName());
        statement.setInt(6, item.getLocData());
        statement.setInt(7, item.getCustomType1());
        statement.setInt(8, item.getCustomType2());
        statement.setInt(9, item.getLifeTime());
        statement.setInt(10, item.getCustomFlags());
        statement.setInt(11, item.getAugmentationId());
        statement.setInt(12, item.getAttributes().getFire());
        statement.setInt(13, item.getAttributes().getWater());
        statement.setInt(14, item.getAttributes().getWind());
        statement.setInt(15, item.getAttributes().getEarth());
        statement.setInt(16, item.getAttributes().getHoly());
        statement.setInt(17, item.getAttributes().getUnholy());
        statement.setInt(18, item.getAgathionEnergy());
        statement.setInt(19, item.getVisualItemId());
    }

    private void update0(ItemInstance item) throws SQLException {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_ITEM)) {
            update0(item, statement);
            statement.execute();
        }
        update.incrementAndGet();
    }

    @Override
    public ItemInstance load(Integer objectId) {
        ItemInstance item;

        Element ce = cache.get(objectId);
        if (ce != null) {
            item = (ItemInstance) ce.getObjectValue();
            return item;
        }

        try {
            item = load0(objectId);
            if (item == null)
                return null;

            item.setJdbcState(JdbcEntityState.STORED);
        } catch (SQLException e) {
            LOG.error("Error while restoring item : " + objectId, e);
            return null;
        }

        cache.put(new Element(item.objectId(), item));

        return item;
    }

    private Stream<ItemInstance> load(Collection<Integer> objectIds) {
        if (objectIds.isEmpty())
            return Stream.empty();

        return objectIds.stream()
                .map(this::load)
                .filter(Objects::nonNull);

    }

    @Override
    public void save(ItemInstance item) {
        if (!item.getJdbcState().isSavable())
            return;

        try {
            save0(item);
            item.setJdbcState(JdbcEntityState.STORED);
        } catch (SQLException e) {
            LOG.error("Error while saving item : " + item, e);
            return;
        }

        cache.put(new Element(item.objectId(), item));
    }

    public void save(Collection<ItemInstance> items) {
        items.forEach(this::save);
    }

    @Override
    public void update(ItemInstance item) {
        if (!item.getJdbcState().isUpdatable())
            return;

        try {
            update0(item);
            item.setJdbcState(JdbcEntityState.STORED);
        } catch (SQLException e) {
            LOG.error("Error while updating item : " + item, e);
            return;
        }

        cache.putIfAbsent(new Element(item.objectId(), item));
    }

    public void update(Collection<ItemInstance> items) {
        items.forEach(this::update);
    }

    @Override
    public void saveOrUpdate(ItemInstance item) {
        if (item.getJdbcState().isSavable())
            save(item);
        else if (item.getJdbcState().isUpdatable())
            update(item);
    }

    @Override
    public void delete(ItemInstance item) {
        if (!item.getJdbcState().isDeletable())
            return;

        try {
            delete0(item);
            item.setJdbcState(JdbcEntityState.DELETED);
        } catch (SQLException e) {
            LOG.error("Error while deleting item : " + item, e);
            return;
        }

        cache.remove(item.objectId());
    }

    public void delete(Collection<ItemInstance> items) {
        items.forEach(this::delete);
    }

    public Stream<ItemInstance> getItemsByOwnerIdAndLoc(int ownerId, ItemLocation loc) {
        Collection<Integer> objectIds = new ArrayList<>();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_OWNER_ITEMS)) {
            statement.setInt(1, ownerId);
            statement.setString(2, loc.name());
            ResultSet rset = statement.executeQuery();
            while (rset.next())
                objectIds.add(rset.getInt(1));
        } catch (SQLException e) {
            LOG.error("Error while restore items of owner : " + ownerId, e);
            objectIds.clear();
        }

        return load(objectIds);
    }

    public Stream<ItemInstance> getItemsByLocation(ItemLocation loc) {
        Collection<Integer> objectIds = new ArrayList<>();

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(RESTORE_ITEMS)) {
            statement.setString(1, loc.name());

            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next())
                    objectIds.add(rset.getInt(1));
            }
        } catch (SQLException e) {
            LOG.error("Error while restore items from loc:" + loc.toString(), e);
            objectIds.clear();
        }

        return load(objectIds);
    }
}
