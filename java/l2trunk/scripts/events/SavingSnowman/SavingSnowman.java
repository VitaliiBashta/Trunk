package l2trunk.scripts.events.SavingSnowman;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.reward.RewardList;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.PositionUtils;
import l2trunk.gameserver.utils.Util;
import l2trunk.scripts.events.EventsConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class SavingSnowman extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
    private static final Logger _log = LoggerFactory.getLogger(SavingSnowman.class);
    private static final List<SimpleSpawner> SPAWNS = new ArrayList<>();
    private static final int INITIAL_SAVE_DELAY = 10 * 60 * 1000; // 10 мин
    private static final int SAVE_INTERVAL = 60 * 60 * 1000; // 60 мин
    private static final int SNOWMAN_SHOUT_INTERVAL = 60 * 1000; // 1 мин
    private static final int THOMAS_EAT_DELAY = 10 * 60 * 1000; // 10 мин
    private static final int SATNA_SAY_INTERVAL = 5 * 60 * 1000; // 5 мин
    private static final int EVENT_MANAGER_ID = 13184;
    private static final int CTREE_ID = 13006;
    private static final int EVENT_REWARDER_ID = 13186;
    private static final int SNOWMAN_ID = 13160;
    private static final int THOMAS_ID = 13183;
    private static final int SANTA_BUFF_REUSE = 12 * 3600 * 1000; // 12 hours
    private static final int SANTA_LOTTERY_REUSE = 3 * 3600 * 1000; // 3 hours
    // Оружие для обмена купонов
    private static final int WEAPONS[][] = {{20109, 20110, 20111, 20112, 20113, 20114, 20115, 20116, 20117, 20118, 20119, 20120, 20121, 20122}, // D
            {20123, 20124, 20125, 20126, 20127, 20128, 20129, 20130, 20131, 20132, 20133, 20134, 20135, 20136}, // C
            {20137, 20138, 20139, 20140, 20141, 20142, 20143, 20144, 20145, 20146, 20147, 20148, 20149, 20150}, // B
            {20151, 20152, 20153, 20154, 20155, 20156, 20157, 20158, 20159, 20160, 20161, 20162, 20163, 20164}, // A
            {20165, 20166, 20167, 20168, 20169, 20170, 20171, 20172, 20173, 20174, 20175, 20176, 20177, 20178} // S
    };
    private static ScheduledFuture<?> _snowmanShoutTask;
    private static ScheduledFuture<?> _saveTask;
    private static ScheduledFuture<?> _sayTask;
    private static ScheduledFuture<?> _eatTask;
    private static SnowmanState _snowmanState;
    private static NpcInstance _snowman;
    private static Creature _thomas;
    private static boolean _active = false;


    private static boolean isActive() {
        return isActive("SavingSnowman");
    }

    private static void spawnRewarder(Player rewarded) {
        // Два санты рядом не должно быть
        for (NpcInstance npc : rewarded.getAroundNpc(1500, 300))
            if (npc.getNpcId() == EVENT_REWARDER_ID)
                return;

        // Санта появляется в зоне прямой видимости
        Location spawnLoc = Location.findPointToStay(rewarded, 300, 400);
        for (int i = 0; i < 20 && !GeoEngine.canSeeCoord(rewarded, spawnLoc.x, spawnLoc.y, spawnLoc.z, false); i++)
            spawnLoc = Location.findPointToStay(rewarded, 300, 400);

        // Спауним
        NpcTemplate template = NpcHolder.getTemplate(EVENT_REWARDER_ID);
        if (template == null) {
            System.out.println("WARNING! events.SavingSnowman.spawnRewarder template is null for npc: " + EVENT_REWARDER_ID);
            Thread.dumpStack();
            return;
        }

        NpcInstance rewarder = new NpcInstance(IdFactory.getInstance().getNextId(), template);
        rewarder.setLoc(spawnLoc);
        rewarder.setHeading((int) (Math.atan2(spawnLoc.y - rewarded.getY(), spawnLoc.x - rewarded.getX()) * Creature.HEADINGS_IN_PI) + 32768); // Лицом к игроку
        rewarder.spawnMe();

        Functions.npcSayCustomMessage(rewarder, "scripts.events.SavingSnowman.RewarderPhrase1");

        Location targetLoc = Location.findFrontPosition(rewarded, rewarded, 40, 50);
        rewarder.setSpawnedLoc(targetLoc);
        rewarder.broadcastPacket(new CharMoveToLocation(rewarder.getObjectId(), rewarder.getLoc(), targetLoc));

        ThreadPoolManager.INSTANCE.schedule(() -> reward(rewarder, rewarded), 5000);
    }

    public static void reward(NpcInstance rewarder, Player rewarded) {
        if (!_active || rewarder == null || rewarded == null)
            return;
        Functions.npcSayCustomMessage(rewarder, "scripts.events.SavingSnowman.RewarderPhrase2", rewarded.getName());
        Functions.addItem(rewarded, 14616, 1, "SavingSnowman"); // Gift from Santa Claus
        ThreadPoolManager.INSTANCE.schedule(() -> removeRewarder(rewarder), 5000);
    }

    private static void removeRewarder(NpcInstance rewarder) {
        if (!_active || rewarder == null)
            return;

        Functions.npcSayCustomMessage(rewarder, "scripts.events.SavingSnowman.RewarderPhrase3");

        Location loc = rewarder.getSpawnedLoc();

        double radian = PositionUtils.convertHeadingToRadian(rewarder.getHeading());
        int x = loc.x - (int) (Math.sin(radian) * 300);
        int y = loc.y + (int) (Math.cos(radian) * 300);
        int z = loc.z;

        rewarder.broadcastPacket(new CharMoveToLocation(rewarder.getObjectId(), loc, new Location(x, y, z)));

        ThreadPoolManager.INSTANCE.schedule(() -> unspawnRewarder(rewarder), 2000);
    }

    private static void unspawnRewarder(NpcInstance rewarder) {
        if (!_active || rewarder == null)
            return;
        rewarder.deleteMe();
    }

    private static Location getRandomSpawnPoint() {
        //L2Territory[] locs = TerritoryTable.INSTANCE().getLocations();
        //L2Territory terr = locs[Rnd.get(locs.length)];
        //return new Location(terr.getRandomPoint());
        return new Location(0, 0, 0);
    }

    // Индюк захавывает снеговика
    private static void eatSnowman() {
        if (_snowman == null || _thomas == null)
            return;

        GameObjectsStorage.getAllPlayersStream().forEach(player ->
                Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceSnowmanKilled", null, ChatType.CRITICAL_ANNOUNCE));

        _snowmanState = SnowmanState.KILLED;

        if (_snowmanShoutTask != null) {
            _snowmanShoutTask.cancel(false);
            _snowmanShoutTask = null;
        }

        _snowman.deleteMe();
        _thomas.deleteMe();
    }

    // Индюк умер, освобождаем снеговика
    public static void freeSnowman(Creature topDamager) {
        if (_snowman == null || topDamager == null || !topDamager.isPlayable())
            return;

        GameObjectsStorage.getAllPlayersStream().forEach(player ->
                Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceSnowmanSaved", null, ChatType.CRITICAL_ANNOUNCE));

        _snowmanState = SnowmanState.SAVED;

        if (_snowmanShoutTask != null) {
            _snowmanShoutTask.cancel(false);
            _snowmanShoutTask = null;
        }
        if (_eatTask != null) {
            _eatTask.cancel(false);
            _eatTask = null;
        }

        Player player = topDamager.getPlayer();
        Functions.npcSayCustomMessage(_snowman, "scripts.events.SavingSnowman.SnowmanSayTnx", player.getName());
        addItem(player, 20034, 3, "SavingSnowman"); // Revita-Pop
        addItem(player, 20338, 1, "SavingSnowman"); // Rune of Experience Points 50%	10 Hour Expiration Period
        addItem(player, 20344, 1, "SavingSnowman"); // Rune of SP 50% 10 Hour Expiration Period

        ThreadPoolManager.INSTANCE.execute(() -> _snowman.deleteMe());
    }

    @Override
    public void onLoad() {
        CharListenerList.addGlobal(this);
        if (isActive()) {
            _active = true;
            spawnEventManagers();
            _log.info("Loaded Event: SavingSnowman [state: activated]");
            _saveTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new SaveTask(), INITIAL_SAVE_DELAY, SAVE_INTERVAL);
            _sayTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new SayTask(), SATNA_SAY_INTERVAL, SATNA_SAY_INTERVAL);
            _snowmanState = SnowmanState.SAVED;
        } else
            _log.info("Loaded Event: SavingSnowman [state: deactivated]");
    }

    public void startEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (SetActive("SavingSnowman", true)) {
            spawnEventManagers();
            System.out.println("Event 'SavingSnowman' started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.SavingSnowman.AnnounceEventStarted");
            if (_saveTask == null)
                _saveTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new SaveTask(), INITIAL_SAVE_DELAY, SAVE_INTERVAL);
            if (_sayTask == null)
                _sayTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new SayTask(), SATNA_SAY_INTERVAL, SATNA_SAY_INTERVAL);
            _snowmanState = SnowmanState.SAVED;
        } else
            player.sendMessage("Event 'SavingSnowman' already started.");

        _active = true;

        show("admin/events/events.htm", player);
    }

    /**
     * Останавливает эвент
     */
    public void stopEvent() {
        Player player = getSelf();
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (SetActive("SavingSnowman", false)) {
            unSpawnEventManagers();
            if (_snowman != null)
                _snowman.deleteMe();
            if (_thomas != null)
                _thomas.deleteMe();
            System.out.println("Event 'SavingSnowman' stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.SavingSnowman.AnnounceEventStoped");
            if (_saveTask != null) {
                _saveTask.cancel(false);
                _saveTask = null;
            }
            if (_sayTask != null) {
                _sayTask.cancel(false);
                _sayTask = null;
            }
            if (_eatTask != null) {
                _eatTask.cancel(false);
                _eatTask = null;
            }
            _snowmanState = SnowmanState.SAVED;
        } else
            player.sendMessage("Event 'SavingSnowman' not started.");

        _active = false;

        show("admin/events/events.htm", player);
    }

    private void spawnEventManagers() {
        SpawnNPCs(EVENT_MANAGER_ID, EventsConfig.EVENT_MANAGERS, SPAWNS);
        SpawnNPCs(CTREE_ID, EventsConfig.CTREES, SPAWNS);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(SPAWNS);
    }

    @Override
    public void onReload() {
        unSpawnEventManagers();
        if (_saveTask != null)
            _saveTask.cancel(false);
        _saveTask = null;
        if (_sayTask != null)
            _sayTask.cancel(false);
        _sayTask = null;
        _snowmanState = SnowmanState.SAVED;
    }

    @Override
    public void onShutdown() {
        unSpawnEventManagers();
    }

    @Override
    public void onDeath(Creature cha, Creature killer) {
        if (_active && killer != null) {
            Player pKiller = killer.getPlayer();
            if (pKiller != null && SimpleCheckDrop(cha, killer) && Rnd.get(1000) < Config.EVENT_SAVING_SNOWMAN_REWARDER_CHANCE) {
                List<Player> players = new ArrayList<>();
                if (pKiller.isInParty())
                    players = pKiller.getParty().getMembers();
                else
                    players.add(pKiller);

                spawnRewarder(players.get(Rnd.get(players.size())));
            }
        }
    }

    public void buff() {
        Player player = getSelf();
        if (!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
            return;

        if (!player.isQuestContinuationPossible(true))
            return;

        String var = player.getVar("santaEventTime");
        if (var != null && Long.parseLong(var) > System.currentTimeMillis()) {
            show("default/13184-4.htm", player);
            return;
        }

        if (_snowmanState != SnowmanState.SAVED) {
            show("default/13184-3.htm", player);
            return;
        }

        player.broadcastPacket(new MagicSkillUse(player, 23017));
        player.altOnMagicUseTimer(player, 23017);
        player.setVar("santaEventTime", String.valueOf(System.currentTimeMillis() + SANTA_BUFF_REUSE), -1);

        Summon pet = player.getPet();
        if (pet != null) {
            pet.broadcastPacket(new MagicSkillUse(pet, 23017));
            pet.altOnMagicUseTimer(pet, 23017);
        }
    }

    public void locateSnowman() {
        Player player = getSelf();
        if (!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
            return;

        if (_snowman != null) {
            // Убираем и ставим флажок на карте и стрелку на компасе
            player.sendPacket(new RadarControl(2, 2, _snowman.getLoc()), new RadarControl(0, 1, _snowman.getLoc()));
            player.sendPacket(new SystemMessage(SystemMessage.S2_S1).addZoneName(_snowman.getLoc()).addString("Look snowman in "));
        } else
            player.sendPacket(Msg.YOUR_TARGET_CANNOT_BE_FOUND);
    }

    public void coupon(String[] var) {
        Player player = getSelf();
        if (!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
            return;

        if (!player.isQuestContinuationPossible(true))
            return;

        if (getItemCount(player, 20107) < 1) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_REQUIRED_ITEMS);
            return;
        }

        int num = Integer.parseInt(var[0]);
        if (num < 0 || num > 13)
            return;

        int expertise = Math.min(player.expertiseIndex, 5);
        expertise = Math.max(expertise, 1);
        expertise--;

        removeItem(player, 20107, 1, "SavingSnowman");

        int item_id = WEAPONS[expertise][num];
        int enchant = Rnd.get(4, 16);
        ItemInstance item = ItemFunctions.createItem(item_id);
        item.setEnchantLevel(enchant);
        player.getInventory().addItem(item, "SavingSnowman");
        player.sendPacket(SystemMessage2.obtainItems(item_id, 1, enchant));
    }

    public void lotery() {
        Player player = getSelf();
        if (!_active || player.isActionsDisabled() || player.isSitting() || player.getLastNpc() == null || player.getLastNpc().getDistance(player) > 300)
            return;

        if (!player.isQuestContinuationPossible(true))
            return;

        if (getItemCount(player, 57) < Config.EVENT_SAVING_SNOWMAN_LOTERY_PRICE) {
            player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            return;
        }

        String var = player.getVar("santaLotteryTime");
        if (var != null && Long.parseLong(var) > System.currentTimeMillis()) {
            show("default/13184-5.htm", player);
            return;
        }

        removeItem(player, 57, Config.EVENT_SAVING_SNOWMAN_LOTERY_PRICE, "SavingSnowman");
        player.setVar("santaLotteryTime", String.valueOf(System.currentTimeMillis() + SANTA_LOTTERY_REUSE), -1);

        int chance = Rnd.get(RewardList.MAX_CHANCE);

        // Special Christmas Tree            30%
        if (chance < 300000)
            addItem(player, 5561, 1, "SavingSnowman");
            // Christmas Red Sock                18%
        else if (chance < 480000)
            addItem(player, 14612, 1, "SavingSnowman");
            // Santa Claus' Weapon Exchange Ticket - 12 Hour Expiration Period      15%
        else if (chance < 630000)
            addItem(player, 20107, 1, "SavingSnowman");
            // Gift from Santa Claus             5%
        else if (chance < 680000)
            addItem(player, 14616, 1, "SavingSnowman");
            // Rudolph's Nose                    5%
        else if (chance < 730000 && getItemCount(player, 14611) == 0)
            addItem(player, 14611, 1, "SavingSnowman");
            // Santa's Hat                       5%
        else if (chance < 780000 && getItemCount(player, 7836) == 0)
            addItem(player, 7836, 1, "SavingSnowman");
            // Santa's Antlers                   5%
        else if (chance < 830000 && getItemCount(player, 8936) == 0)
            addItem(player, 8936, 1, "SavingSnowman");
            // Agathion Seal Bracelet - Rudolph (постоянный предмет)                5%
        else if (chance < 880000 && getItemCount(player, 10606) == 0)
            addItem(player, 10606, 1, "SavingSnowman");
            // Agathion Seal Bracelet: Rudolph - 30 дней со скилом на виталити      5%
        else if (chance < 930000 && getItemCount(player, 20094) == 0)
            addItem(player, 20094, 1, "SavingSnowman");
            // Chest of Experience (Event)       3%
        else if (chance < 960000)
            addItem(player, 20575, 1, "SavingSnowman");
            // Призрачные аксессуары             2.5%
        else if (chance < 985000)
            addItem(player, Rnd.get(9177, 9204), 1, "SavingSnowman");
            // BOSE или BRES                     1.2%
        else if (chance < 997000)
            addItem(player, Rnd.get(9156, 9157), 1, "SavingSnowman");
    }

    public String DialogAppend_13184(Integer val) {
        if (val != 0)
            return "";

        return " (" + Util.formatAdena(Config.EVENT_SAVING_SNOWMAN_LOTERY_PRICE) + " adena)";
    }

    @Override
    public void onPlayerEnter(Player player) {
        if (_active)
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceEventStarted");
    }

    // Индюк захватывает снеговика
    private void captureSnowman() {
        Location spawnPoint = getRandomSpawnPoint();

        GameObjectsStorage.getAllPlayersStream().forEach(player -> {
            Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.SavingSnowman.AnnounceSnowmanCaptured", null, ChatType.CRITICAL_ANNOUNCE);
            player.sendPacket(new SystemMessage(SystemMessage.S2_S1).addZoneName(spawnPoint).addString("Look snowman in "));
            // Убираем и ставим флажок на карте и стрелку на компасе
            player.sendPacket(new RadarControl(2, 2, spawnPoint), new RadarControl(0, 1, spawnPoint));
        });

        // Спауним снеговика

        SimpleSpawner sp = (SimpleSpawner) new SimpleSpawner(SNOWMAN_ID)
                .setLoc(spawnPoint)
                .setAmount(1)
                .setRespawnDelay(0);
        _snowman = sp.doSpawn(true);

        if (_snowman == null)
            return;

        // Спауним Томаса
        Location pos = Location.findPointToStay(_snowman, 100, 120);

        sp = (SimpleSpawner) new SimpleSpawner(THOMAS_ID)
                .setLoc(pos)
                .setAmount(1)
                .setRespawnDelay(0);
        _thomas = sp.doSpawn(true);

        _snowmanState = SnowmanState.CAPTURED;

        // Если по каким-то причинам таск существует, останавливаем его
        if (_snowmanShoutTask != null) {
            _snowmanShoutTask.cancel(false);
            _snowmanShoutTask = null;
        }
        _snowmanShoutTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new ShoutTask(), 1, SNOWMAN_SHOUT_INTERVAL);

        if (_eatTask != null) {
            _eatTask.cancel(false);
            _eatTask = null;
        }
        _eatTask = ThreadPoolManager.INSTANCE.schedule(SavingSnowman::eatSnowman, THOMAS_EAT_DELAY);
    }

    public enum SnowmanState {
        CAPTURED,
        KILLED,
        SAVED
    }

    public class SayTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (!_active)
                return;
            SPAWNS.stream()
                    .filter(s -> s.getCurrentNpcId() == EVENT_MANAGER_ID)
                    .forEach(s -> Functions.npcSayCustomMessage(s.getLastSpawn(), "scripts.events.SavingSnowman.SantaSay"));
        }
    }

    private class ShoutTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (!_active || _snowman == null || _snowmanState != SnowmanState.CAPTURED)
                return;
            Functions.npcShoutCustomMessage(_snowman, "scripts.events.SavingSnowman.SnowmanShout");
        }
    }

    public class SaveTask extends RunnableImpl {
        @Override
        public void runImpl() {
            if (!_active || _snowmanState == SnowmanState.CAPTURED)
                return;
            captureSnowman();
        }
    }
}