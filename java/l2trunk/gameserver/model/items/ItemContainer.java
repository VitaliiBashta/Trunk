package l2trunk.gameserver.model.items;

import l2trunk.commons.math.SafeMath;
import l2trunk.gameserver.dao.ItemsDAO;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public abstract class ItemContainer {
    static final ItemsDAO ITEMS_DAO = ItemsDAO.INSTANCE;

    final List<ItemInstance> items = new CopyOnWriteArrayList<>();
    /**
     * Блокировка для чтения/записи вещей из списка и внешних операций
     */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    ItemContainer() {

    }

    public int getSize() {
        return items.size();
    }

    public synchronized List<ItemInstance> getItems() {
        return items;
    }

    public synchronized void clear() {
        items.clear();
    }

    public final void writeLock() {
        writeLock.lock();
    }

    public final void writeUnlock() {
        writeLock.unlock();
    }

    final void readLock() {
        readLock.lock();
    }

    final void readUnlock() {
        readLock.unlock();
    }

    public synchronized ItemInstance getItemByObjectId(int objectId) {
        return items.stream()
                .filter(i -> i.getObjectId() == objectId)
                .findFirst().orElse(null);
    }

    /**
     * Найти первую вещь по itemId
     *
     * @param itemId
     * @return вещь, если найдена, либо null если не найдена
     */
    public synchronized ItemInstance getItemByItemId(int itemId) {
        return items.stream()
                .filter(item -> item.getItemId() == itemId)
                .findFirst().orElse(null);
    }

    public synchronized List<ItemInstance> getItemsByItemId(int itemId) {
        return items.stream()
                .filter(item -> item.getItemId() == itemId)
                .collect(Collectors.toList());
    }

    public synchronized long getCountOf(int itemId) {
        return items.stream()
                .filter(i -> i.getItemId() == itemId)
                .count();
    }

    /**
     * Создать вещь и добавить в список, либо увеличить количество вещи в инвентаре
     *
     * @param itemId - идентификатор itemId вещи
     * @param count  - количество для создания, либо увеличения
     * @return созданная вещь
     */
    ItemInstance addItem(int itemId, long count, String owner, String log) {
        if (count < 1)
            return null;

        ItemInstance item;
        writeLock();
        try {
            item = getItemByItemId(itemId);

            if (item != null && item.isStackable()) {
                item.setCount(SafeMath.addAndLimit(item.getCount(), count));
                onModifyItem(item);
                if (owner != null)
                    Log.LogAddItem(owner, log, item, count);
            } else {
                item = ItemFunctions.createItem(itemId);
                item.setCount(count);

                items.add(item);
                onAddItem(item);

                if (owner != null)
                    Log.LogAddItem(owner, log, item, count);
            }
        } finally {
            writeUnlock();
        }
        return item;
    }

    /**
     * Добавить вещь в список.<br>
     * При добавлении нескольких вещей подряд, список должен быть заблокирован с writeLock() и разблокирован после добавления с writeUnlock()<br>
     * <br>
     * <buffPrice><font color="red">Должно выполнятся в блоке synchronized(item)</font></buffPrice>
     *
     * @return вещь, полученая в результате добавления, null если не найдена
     */
    public ItemInstance addItem(ItemInstance item, String owner, String log) {
        if (item == null)
            return null;

        if (item.getCount() < 1)
            return null;

        ItemInstance result = null;

        writeLock();
        try {
            if (getItemByObjectId(item.getObjectId()) != null)
                return null;

            long countToAdd = item.getCount();
            if (item.isStackable()) {
                int itemId = item.getItemId();
                result = getItemByItemId(itemId);
                if (result != null)
                    synchronized (result) {
                        // увеличить количество в стопке
                        result.setCount(SafeMath.addAndLimit(item.getCount(), result.getCount()));
                        onModifyItem(result);
                        onDestroyItem(item);

                    }
            }

            if (result == null) {
                items.add(item);
                result = item;

                onAddItem(result);
            }

            if (owner != null)
                Log.LogAddItem(owner, log, result, countToAdd);
        } finally {
            writeUnlock();
        }

        return result;
    }

    /**
     * Удаляет вещь из списка, либо уменьшает количество вещи по objectId
     *
     * @param objectId - идентификатор objectId вещи
     * @param count    - на какое количество уменьшить, если количество равно количество вещи, то вещь удаляется из списка
     * @return вещь, полученая в результате удаления, null если не найдена
     */
    public ItemInstance removeItemByObjectId(int objectId, long count, String owner, String log) {
        if (count < 1)
            return null;

        ItemInstance result;

        writeLock();
        try {
            ItemInstance item;
            if ((item = getItemByObjectId(objectId)) == null)
                return null;

            synchronized (item) {
                result = removeItem(item, count, owner, log);
            }
        } finally {
            writeUnlock();
        }

        return result;
    }

    /**
     * Удаляет вещь из списка, либо уменьшает количество первой найденной вещи по itemId
     *
     * @param itemId - идентификатор itemId
     * @param count  - на какое количество уменьшить, если количество равно количество вещи, то вещь удаляется из списка
     * @return вещь, полученая в результате удаления, null если не найдена
     */
    ItemInstance removeItemByItemId(int itemId, long count, String owner, String log) {
        if (count < 1)
            return null;

        ItemInstance result;

        writeLock();
        try {
            ItemInstance item;
            if ((item = getItemByItemId(itemId)) == null)
                return null;

            synchronized (item) {
                result = removeItem(item, count, owner, log);
            }
        } finally {
            writeUnlock();
        }

        return result;
    }

    /**
     * Удаляет вещь из списка, либо уменьшает количество вещи.<br>
     * При удалении нескольких вещей подряд, список должен быть заблокирован с writeLock() и разблокирован после добавления с writeUnlock()<br>
     * <br>
     * <buffPrice><font color="red">Должно выполнятся в блоке synchronized(item)</font></buffPrice>
     *
     * @param item  - вещь для удаления
     * @param count - на какое количество уменьшить, если количество равно количество вещи, то вещь удаляется из списка
     * @return вещь, полученая в результате удаления
     */
    public ItemInstance removeItem(ItemInstance item, long count, String owner, String log) {
        if (item == null)
            return null;

        if (count < 1)
            return null;

        if (item.getCount() < count)
            return null;

        writeLock();
        try {
            if (!items.contains(item))
                return null;

            if (item.getCount() > count) {
                if (owner != null)
                    Log.LogRemoveItem(owner, log, item, count);

                item.setCount(item.getCount() - count);
                onModifyItem(item);

                ItemInstance newItem = new ItemInstance(IdFactory.getInstance().getNextId(), item.getItemId());
                newItem.setCount(count);

                return newItem;
            } else
                return removeItem(item, owner, log);
        } finally {
            writeUnlock();
        }
    }

    /**
     * Удаляет вещь из списка.<br>
     * При удалении нескольких вещей подряд, список должен быть заблокирован с writeLock() и разблокирован после добавления с writeUnlock()<br>
     * <br>
     * <buffPrice><font color="red">Должно выполнятся в блоке synchronized(item)</font></buffPrice>
     *
     * @param item - вещь для удаления
     * @return вещь, полученая в результате удаления
     */
    public ItemInstance removeItem(ItemInstance item, String owner, String log) {
        if (item == null)
            return null;

        writeLock();
        try {
            if (!items.remove(item))
                return null;

            onRemoveItem(item);

            if (owner != null)
                Log.LogRemoveItem(owner, log, item, item.getCount());

            return item;
        } finally {
            writeUnlock();
        }
    }

    /**
     * Уничтожить вещь из списка, либо снизить количество по идентификатору objectId
     *
     * @param objectId
     * @param count    - количество для удаления
     * @return true, если количество было снижено или вещь была уничтожена
     */
    public boolean destroyItemByObjectId(int objectId, long count, String owner, String log) {
        writeLock();
        try {
            ItemInstance item;
            if ((item = getItemByObjectId(objectId)) == null)
                return false;

            synchronized (item) {
                return destroyItem(item, count, owner, log);
            }
        } finally {
            writeUnlock();
        }
    }

    /**
     * Уничтожить вещь из списка, либо снизить количество по идентификатору itemId
     *
     * @param itemId
     * @param count  - количество для удаления
     * @return true, если количество было снижено или вещь была уничтожена
     */
    public boolean destroyItemByItemId(int itemId, long count, String owner, String log) {
        writeLock();
        try {
            ItemInstance item;
            if ((item = getItemByItemId(itemId)) == null)
                return false;

            synchronized (item) {
                return destroyItem(item, count, owner, log);
            }
        } finally {
            writeUnlock();
        }
    }

    /**
     * Уничтожить вещь из списка, либо снизить количество<br>
     * <br>
     * <buffPrice><font color="red">Должно выполнятся в блоке synchronized(item)</font></buffPrice>
     *
     * @param count - количество для удаления
     * @return true, если количество было снижено или вещь была уничтожена
     */
    boolean destroyItem(ItemInstance item, long count, String owner, String log) {
        if (item == null)
            return false;

        if (count < 1)
            return false;

        if (item.getCount() < count)
            return false;

        writeLock();
        try {
            if (!items.contains(item))
                return false;

            if (item.getCount() > count) {
                if (owner != null)
                    Log.LogDestroyItem(owner, log, item, count);

                item.setCount(item.getCount() - count);
                onModifyItem(item);

                return true;
            } else
                return destroyItem(item, owner, log);
        } finally {
            writeUnlock();
        }
    }

    /**
     * Удаляет вещь из списка.<br>
     * <br>
     * <buffPrice><font color="red">Должно выполнятся в блоке synchronized(item)</font></buffPrice>
     *
     * @param item - вещь для удаления
     * @return вещь, полученая в результате удаления
     */
    boolean destroyItem(ItemInstance item, String owner, String log) {
        if (item == null)
            return false;

        writeLock();
        try {
            if (!items.remove(item))
                return false;

            if (owner != null)
                Log.LogDestroyItem(owner, log, item, item.getCount());
            onRemoveItem(item);
            onDestroyItem(item);

            return true;
        } finally {
            writeUnlock();
        }
    }

    protected abstract void onAddItem(ItemInstance item);

    protected abstract void onModifyItem(ItemInstance item);

    protected abstract void onRemoveItem(ItemInstance item);

    protected abstract void onDestroyItem(ItemInstance item);
}
