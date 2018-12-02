package l2trunk.gameserver.taskmanager;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.items.ItemInstance;

import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum ItemsAutoDestroy {
    INSTANCE;
    private static final long MILLIS_TO_CHECK_DESTROY_THREAD = 1000L;
    private static final long MILLIS_TO_DELETE_HERB = 60000L;

    private final Queue<ItemInstance> itemsToDelete = new ConcurrentLinkedQueue<>();

    public void init() {
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new CheckItemsForDestroy(), MILLIS_TO_CHECK_DESTROY_THREAD, MILLIS_TO_CHECK_DESTROY_THREAD);

    }

    public void addItem(ItemInstance item, long destroyTime) {
        item.setTimeToDeleteAfterDrop(System.currentTimeMillis() + destroyTime);
        itemsToDelete.add(item);
    }

    public void addHerb(ItemInstance herb) {
        herb.setTimeToDeleteAfterDrop(System.currentTimeMillis() + MILLIS_TO_DELETE_HERB);
        itemsToDelete.add(herb);
    }

    private Collection<ItemInstance> getItemsToDelete() {
        return itemsToDelete;
    }


    private static class CheckItemsForDestroy implements Runnable {
        @Override
        public void run() {
            long currentTime = System.currentTimeMillis();
            for (ItemInstance item : INSTANCE.getItemsToDelete())
                if (item == null || item.getTimeToDeleteAfterDrop() == 0 || item.getLocation() != ItemInstance.ItemLocation.VOID)
                    INSTANCE.getItemsToDelete().remove(item);
                else if (item.getTimeToDeleteAfterDrop() < currentTime) {
                    item.deleteMe();
                    INSTANCE.getItemsToDelete().remove(item);
                }
        }
    }
}