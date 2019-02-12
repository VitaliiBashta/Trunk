package l2trunk.gameserver.cache;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.items.ItemInfo;
import l2trunk.gameserver.model.items.ItemInstance;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public enum ItemInfoCache {
    INSTANCE;
    private final Cache cache;

    ItemInfoCache() {
        cache = CacheManager.getInstance().getCache(getClass().getName());
    }

    public void put(ItemInstance item) {
        cache.put(new Element(item.objectId(), new ItemInfo(item)));
    }

    /**
     * Получить информацию из кеша, по objecId предмета. Если игрок онлайн и все еще владеет этим предметом
     * информация будет обновлена.
     *
     * @param objectId - идентификатор предмета
     * @return возвращает описание вещи, или null если описания нет, или уже удалено из кеша
     */
    public ItemInfo get(int objectId) {
        Element element = cache.get(objectId);

        ItemInfo info = null;
        if (element != null)
            info = (ItemInfo) element.getObjectValue();

        Player player;

        if (info != null) {
            player = World.getPlayer(info.getOwnerId());

            ItemInstance item = null;

            if (player != null)
                item = player.getInventory().getItemByObjectId(objectId);

            if (item != null)
                if (item.getItemId() == info.getItemId())
                    cache.put(new Element(item.objectId(), info = new ItemInfo(item)));
        }

        return info;
    }
}
