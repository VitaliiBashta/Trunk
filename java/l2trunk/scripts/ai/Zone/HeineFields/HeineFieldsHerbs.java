package l2trunk.scripts.ai.Zone.HeineFields;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledFuture;

public final class HeineFieldsHerbs implements ScriptFile {

    private static final int[] HERBS = {14824, 14825, 14826, 14827};
    private static ScheduledFuture<?> DropAncientHerbTask;
    private static ScheduledFuture<?> DeleteAncientHerbTask;
    private final List<ItemInstance> herbs = new CopyOnWriteArrayList<>();

    @Override
    public void onLoad() {
        DropAncientHerbTask = ThreadPoolManager.INSTANCE.scheduleAtFixedDelay(new DropAncientHerbTask(), 10000L, Config.ANCIENT_HERB_RESPAWN_TIME);
        DeleteAncientHerbTask = ThreadPoolManager.INSTANCE.scheduleAtFixedDelay(() -> {
            herbs.forEach(GameObject::deleteMe);
            herbs.clear();
        }, 70000L, Config.ANCIENT_HERB_DESPAWN_TIME);

    }

    @Override
    public void onReload() {
        DropAncientHerbTask.cancel(false);
        DeleteAncientHerbTask.cancel(false);
    }

    @Override
    public void onShutdown() {
    }

    public class DropAncientHerbTask extends RunnableImpl {
        @Override
        public void runImpl() {
            Config.HEIN_FIELDS_LOCATIONS.forEach(loc -> {
                if (Rnd.chance(herbs.isEmpty() ? 100 : Config.ANCIENT_HERB_SPAWN_CHANCE)) { // Herb first spawn 100% to avoid NPE in Tax divides.
                    for (int x = 0; x < Config.ANCIENT_HERB_SPAWN_COUNT; x++) {
                        ItemInstance item = new ItemInstance(IdFactory.getInstance().getNextId(), Rnd.get(HERBS));
                        item.setCount(1);
                        Location pos = Location.findPointToStay(loc, Config.ANCIENT_HERB_SPAWN_RADIUS, 120);
                        item.dropMe(null, pos);
                        herbs.add(item);
                    }
                }
            });
        }
    }
    }