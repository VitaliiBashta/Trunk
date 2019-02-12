package l2trunk.scripts.events.Hitman;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerExitListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.instances.GuardInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.scripts.npc.model.events.HitmanInstance.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class Hitman extends Functions implements ScriptFile, OnDeathListener, OnPlayerExitListener {

    private static final Logger _log = LoggerFactory.getLogger(Hitman.class);

    private static final String DELETE_TERGET_FROM_DATABASE = "DELETE FROM event_hitman WHERE target=?";
    private static final String SAVE_TO_DATABASE = "INSERT INTO event_hitman VALUES (?,?,?,?,?,?)";
    private static final String LOAD_FROM_DATABASE = "SELECT * FROM event_hitman";
    private static final String DELETE_STOREID_FROM_DATABASE = "DELETE FROM event_hitman WHERE storedId=?";

    private static Map<Integer, Order> orderMap;
    private static StringBuilder _itemsList;
    private static Map<String, Integer> allowedItems;
    private static List<Integer> _inList;

    private static boolean checkPlayer(Player player) {
        if (player.isDead()) {
            show(new CustomMessage("scripts.events.Hitman.CancelledDead"), player);
            return false;
        }

        if (player.getTeam() != TeamType.NONE) {
            show(new CustomMessage("scripts.events.Hitman.CancelledOtherEvent"), player);
            return false;
        }

        if (player.isMounted()) {
            show(new CustomMessage("scripts.events.Hitman.Cancelled"), player);
            return false;
        }

        if (player.isCursedWeaponEquipped()) {
            show(new CustomMessage("scripts.events.Hitman.Cancelled"), player);
            return false;
        }

        if (player.isInDuel()) {
            show(new CustomMessage("scripts.events.Hitman.CancelledDuel"), player);
            return false;
        }

        if (player.getOlympiadGame() != null || Olympiad.isRegistered(player)) {
            show(new CustomMessage("scripts.events.Hitman.CancelledOlympiad"), player);
            return false;
        }

        if (player.isInParty() && player.getParty().isInDimensionalRift()) {
            show(new CustomMessage("scripts.events.Hitman.CancelledOtherEvent"), player);
            return false;
        }

        if (player.isInObserverMode()) {
            show(new CustomMessage("scripts.events.Hitman.CancelledObserver"), player);
            return false;
        }

        if (player.isTeleporting()) {
            show(new CustomMessage("scripts.events.Hitman.CancelledTeleport"), player);
            return false;
        }

        return true;
    }

    private static void deleteFromDatabase(String target) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_TERGET_FROM_DATABASE)) {
            statement.setString(1, target);
            statement.execute();
        } catch (SQLException ignored) {
        }
    }

    public static String getItemsList() {
        return _itemsList.toString();
    }

    public static Order getOrderById(int index) {
        return orderMap.get(_inList.get(index));
    }

    public static int addOrder(Player player, String name, int killsCount, int itemcount, String itemname) {
        if (player == null)
            return 6;

        if (!checkPlayer(player)) {
            return -1;
        }

        if (killsCount > 10) {
            return 4;
        }

        if (!player.haveItem(Config.EVENT_HITMAN_COST_ITEM_ID, Config.EVENT_HITMAN_COST_ITEM_COUNT)) {
            return 0;
        }

        if (!player.haveItem(allowedItems.get(itemname), (itemcount * killsCount))) {
            return 0;
        }

        if (player.getAdena() < (itemcount * killsCount + Config.EVENT_HITMAN_COST_ITEM_COUNT)) {
            return 0;
        }

        if (isRegistered(player)) {
            return 1;
        }

        if (World.getPlayer(name) == null) {
            return 2;
        }

        if (World.getPlayer(name).objectId() == player.objectId()) {
            return 3;
        }

        final Order order = new Order(player.getName(), name, allowedItems.get(itemname), itemcount, killsCount);

        orderMap.put(player.objectId(), order);
        _inList.add(0, player.objectId());
        saveToDatabase(player.objectId(), player.getName(), name, allowedItems.get(itemname), itemcount, killsCount);
        removeItem(player, Config.EVENT_HITMAN_COST_ITEM_ID, Config.EVENT_HITMAN_COST_ITEM_COUNT, "RemovedHitItem");
        removeItem(player, allowedItems.get(itemname), itemcount * killsCount, "Removed Hit Event");

        Announcements.INSTANCE.announceToAll(new CustomMessage("scripts.events.Hitman.Announce", player.getName(), itemcount, itemname, name).toString());

        World.getPlayer(name).setOrdered(player.objectId());

        return 5;
    }

    public static int getOrdersCount() {
        return orderMap.size();
    }

    private static void saveToDatabase(int objectId, String ownerName, String targetName, int itemId, int itemCount, int killsCount) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SAVE_TO_DATABASE)) {
            statement.setInt(1, objectId);
            statement.setString(2, ownerName);
            statement.setString(3, targetName);
            statement.setInt(4, itemId);
            statement.setInt(5, itemCount);
            statement.setInt(6, killsCount);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadFromDatabase() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(LOAD_FROM_DATABASE);
             ResultSet rset = statement.executeQuery()) {
            while (rset.next()) {
                orderMap.put(rset.getInt(1), new Order(rset.getString(2), rset.getString(3), rset.getInt(4), rset.getInt(5), rset.getInt(6)));
                _inList.add(0, rset.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void deleteFromDatabase(int storedId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(DELETE_STOREID_FROM_DATABASE)) {
            statement.setInt(1, storedId);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteOrder(int storedId) {
        if (!orderMap.containsKey(storedId)) {
            return false;
        }
        if (GameObjectsStorage.getPlayer(orderMap.get(storedId).getTargetName()) != null) {
            GameObjectsStorage.getPlayer(orderMap.get(storedId).getTargetName()).setOrdered(0);
        }
        addItem(World.getPlayer(storedId), orderMap.get(storedId).getItemId(), orderMap.get(storedId).getItemCount() * orderMap.get(storedId).getKillsCount());
        orderMap.remove(storedId);
        _inList.remove((Object) storedId);
        deleteFromDatabase(storedId);

        return true;
    }

    private static boolean isRegistered(Player player) {
        return orderMap.containsKey(player.objectId());
    }

    @Override
    public void onLoad() {
        orderMap = new HashMap<>();
        _itemsList = new StringBuilder();
        allowedItems = new HashMap<>();
        _inList = new ArrayList<>();

        CharListenerList.addGlobal(this);

        for (Integer EVENT_HITMAN_ALLOWED_ITEM_LIST : Config.EVENT_HITMAN_ALLOWED_ITEM_LIST) {
            final String itemName = ItemFunctions.createItem(EVENT_HITMAN_ALLOWED_ITEM_LIST).getTemplate().getName();
            _itemsList.append(itemName).append(";");
            allowedItems.put(itemName, EVENT_HITMAN_ALLOWED_ITEM_LIST);
        }
        loadFromDatabase();
        _log.info("Loaded Event: Hitman");
    }

    @Override
    public void onDeath(Creature actor, Creature killer) {
        if (killer instanceof MonsterInstance || killer instanceof GuardInstance)
            return;
        if (getOrderByTargetName(actor.getName()) != null && !actor.getName().equals(killer.getName())) {
            final Order order = getOrderByTargetName(actor.getName());
            addItem(killer.getPlayer(), order.getItemId(), order.getItemCount());
            Announcements.INSTANCE.announceToAll(new CustomMessage("scripts.events.Hitman.AnnounceKill", killer.getPlayer(), killer.getName(), actor.getName(), order.getItemCount(), ItemFunctions.createItem(order.getItemId()).getTemplate().getName()).toString());

            if (order.getKillsCount() > 1) {
                order.decrementKillsCount();
            } else {
                orderMap.remove(World.getPlayer(order.getOwner()).objectId());
                _inList.remove((Object) World.getPlayer(order.getOwner()).objectId());
                deleteFromDatabase(actor.getName());
            }
        }
    }

    @Override
    public void onReload() {
        orderMap.clear();
        allowedItems.clear();
    }

    @Override
    public void onShutdown() {
        orderMap.clear();
        allowedItems.clear();
    }

    private Order getOrderByTargetName(String name) {
        return orderMap.values().stream()
                .filter(e -> name.equals(e.getTargetName()))
                .findFirst().orElse(null);
    }

    @Override
    public void onPlayerExit(Player player) {
        // /Выходит тот за кого назначили награду
        if (orderMap.containsKey(player.getOrdered())) {
            Player gamer = GameObjectsStorage.getPlayer(player.getOrdered());
            gamer.sendMessage("Event Hitman :" + " Reward for murder returned");
            deleteOrder(player.getOrdered());
        }
        // /Выходит тот кто назначил награду
        else if (orderMap.containsKey(player.objectId())) {
            Player gamer = GameObjectsStorage.getPlayer(player.objectId());
            gamer.sendMessage("Event Hitman :" + " Reward for murder returned");
            deleteOrder(player.getOrdered());
        }
    }
}
