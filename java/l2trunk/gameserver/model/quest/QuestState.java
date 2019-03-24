package l2trunk.gameserver.model.quest;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.ImagesCache;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.instancemanager.SpawnManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.OnKillListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.Element;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.AddonsConfig;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.quests._255_Tutorial;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.commons.lang.NumberUtils.toLong;
import static l2trunk.gameserver.model.quest.Quest.SOUND_ITEMGET;

public final class QuestState {
    public static final String VAR_COND = "cond";
    private static final int RESTART_HOUR = 6;
    private static final int RESTART_MINUTES = 30;
    private static final Logger _log = LoggerFactory.getLogger(QuestState.class);
    public final Player player;
    public final Quest quest;
    private final Map<String, String> vars = new ConcurrentHashMap<>();
    private final Map<String, QuestTimer> timers = new ConcurrentHashMap<>();
    private int state;
    private Integer cond = null;
    private OnKillListener onKillListener = null;

    /**
     * Constructor<?> of the QuestState : save the quest in the list of quests of the getPlayer.<BR/><BR/>
     * <p/>
     * <U><I>Actions :</U></I><BR/>
     * <LI>Save informations in the object QuestState created (Quest, Player, Completion, State)</LI>
     * <LI>Add the QuestState in the getPlayer's list of quests by using setQuestState()</LI>
     * <LI>Add drops gotten by the quest</LI>
     * <BR/>
     *
     * @param quest  : quest associated with the QuestState
     * @param player : L2Player pointing out the getPlayer
     * @param state  : state of the quest
     */
    public QuestState(Quest quest, Player player, int state) {
        this.quest = quest;
        this.player = player;

        // Save the state of the quest for the getPlayer in the getPlayer's list of quest onwed
        player.setQuestState(this);

        // set the state of the quest
        this.state = state;
        quest.notifyCreate(this);
    }


    /**
     * Add XP and SP as quest reward
     * <br><br>
     * Метод учитывает рейты!
     */
    public void addExpAndSp(long exp, long sp) {
        if (player == null)
            return;
        if (exp > 0)
            player.addExpAndSp((long) (exp * getRateQuestsReward()), 0);
        if (sp > 0)
            player.addExpAndSp(0, (long) (sp * getRateQuestsReward()));
    }

    public void addNotifyOfDeath(Player player, boolean withPet) {
        OnDeathListenerImpl listener = new OnDeathListenerImpl();
        player.addListener(listener);
        if (withPet) {
            Summon summon = player.getPet();
            if (summon != null)
                summon.addListener(listener);
        }
    }

    public void addPlayerOnKillListener() {
        if (onKillListener != null)
            throw new IllegalArgumentException("Cant add twice kill listener to getPlayer");

        onKillListener = new PlayerOnKillListenerImpl();
        player.addListener(onKillListener);
    }

    public void removePlayerOnKillListener() {
        player.removeListener(onKillListener);
    }

    public void addRadar(Location loc) {
        player.addRadar(loc);
    }

    public void addRadarWithMap(Location location) {
        if (player != null)
            player.addRadarWithMap(location);
    }

    /**
     * Используется для однодневных квестов
     */
    public void exitCurrentQuest(Quest quest) {
        exitCurrentQuest();
        quest.newQuestState(player, Quest.DELAYED);
        QuestState qs = player.getQuestState(quest);
        qs.setRestartTime();
    }

    public void exitCurrentQuest() {
        exitCurrentQuest(true);
    }

    public void finish() {
        exitCurrentQuest(false);
    }

    /**
     * Destroy element used by quest when quest is exited
     */
    private void exitCurrentQuest(boolean repeatable) {
        if (player == null)
            return;

        removePlayerOnKillListener();
        // Clean drops
        quest.getItems().stream()
                .filter(itemId -> itemId != 57)
                .map(player.inventory::getItemByItemId)
                .filter(Objects::nonNull)
                .forEach(itemId ->
                        player.inventory.destroyItemByItemId(itemId.getItemId(), player.inventory.getItemByItemId(itemId.getItemId()).getCount(), "Exiting Quest " + quest.name));


        // If quest is repeatable, delete quest from list of quest of the getPlayer and from database (quest CAN be created again => repeatable)
        if (repeatable) {
            player.removeQuestState(quest.name);
            Quest.deleteQuestInDb(this);
            vars.clear();
        } else { // Otherwise, delete variables for quest and update database (quest CANNOT be created again => not repeatable)
            vars.keySet().stream()
                    .filter(Objects::nonNull)
                    .forEach(this::unset);
            setState(Quest.COMPLETED);
            Quest.updateQuestInDb(this);
        }
        player.sendPacket(new QuestList(player));
    }

    public void abortQuest() {
        quest.onAbort(this);
        exitCurrentQuest();
    }

    /**
     * <font color=red>Не использовать для получения кондов!</font><br><br>
     * <p/>
     * Return the value of the variable of quest represented by "var"
     *
     * @param var : name of the variable of quest
     * @return Object
     */
    public String get(String var) {
        return vars.get(var);
    }

    public Map<String, String> getVars() {
        return vars;
    }

    /**
     * Возвращает переменную в виде целого числа.
     *
     * @param var : String designating the variable for the quest
     * @return int
     */
    public int getInt(String var) {
        String val = get(var);
        if (val == null)
            return 0;
        return toInt(val);
    }

    public long getLong(String var) {
        String val = get(var);
        if (val == null)
            return 0;
        return toLong(val);
    }


    public int getItemEquipped(int loc) {
        return player.getInventory().getPaperdollItemId(loc);
    }

    public boolean haveAllItems(List<Integer> itemIds) {
        if (player == null)
            return false;
        return itemIds.stream().allMatch(player::haveItem);
    }

    public long getSumQuestItemsCount(Collection<Integer> itemIds) {
        if (player == null)
            return 0;
        return itemIds.stream()
                .map(itemId -> player.getInventory().getCountOf(itemId))
                .count();
    }

    public long getQuestItemsCount(int itemId) {
        return player == null ? 0 : player.getInventory().getCountOf(itemId);
    }

    public boolean giveItemIfNotHave(int itemId) {
        return giveItemIfNotHave(itemId, 1);
    }

    public boolean giveItemIfNotHave(int itemId, int limit) {
        if (getQuestItemsCount(itemId) < limit) {
            giveItems(itemId);
            playSound(SOUND_ITEMGET);
            return false;
        }
        return true;
    }

    public long getQuestItemsCount(List<Integer> itemsIds) {
        return itemsIds.stream().mapToLong(this::getQuestItemsCount).sum();
    }

    public long getQuestItemsCount(int... itemsIds) {
        return Stream.of(itemsIds)
                .mapToLong(this::getQuestItemsCount)
                .sum();
    }

    public boolean haveQuestItem(int itemId, int count) {
        return getQuestItemsCount(itemId) >= count;
    }

    public boolean haveAllQuestItems(Collection<Integer> itemIds) {
        return itemIds.stream().allMatch(this::haveQuestItem);
    }
    public boolean haveAnyQuestItems(Collection<Integer> itemIds) {
        return itemIds.stream().anyMatch(this::haveQuestItem);
    }

    public boolean haveAllQuestItems(int... itemIds) {
        return IntStream.of(itemIds)
                .mapToObj(this::haveQuestItem)
                .allMatch(i -> i);
    }

    public boolean haveAnyQuestItems(int... itemIds) {
        return IntStream.of(itemIds)
                .mapToObj(this::haveQuestItem)
                .anyMatch(i -> i);
    }


    public boolean haveQuestItem(int itemId) {
        return player.haveItem(itemId);
    }

    public int getState() {
        return state == Quest.DELAYED ? Quest.CREATED : state;
    }

    /**
     * Return state of the quest after its initialization.<BR><BR>
     * <U><I>Actions :</I></U>
     * <LI>Remove drops from previous state</LI>
     * <LI>Set new state of the quest</LI>
     * <LI>Add drop for new state</LI>
     * <LI>Update information in database</LI>
     * <LI>Send packet QuestList to client</LI>
     */
    public void setState(int state) {
        if (player == null)
            return;

        this.state = state;

        if (quest.isVisible() && isStarted())
            player.sendPacket(new ExShowQuestMark(quest.id));

        Quest.updateQuestInDb(this);
        player.sendPacket(new QuestList(player));
    }

    public void start() {
        setState(Quest.STARTED);
    }

    public void complete() {
        setState(Quest.COMPLETED);
    }

    public String getStateName() {
        switch (state) {
            case Quest.CREATED:
                return "Start";
            case Quest.STARTED:
                return "Started";
            case Quest.COMPLETED:
                return "Completed";
            case Quest.DELAYED:
                return "Delayed";
        }
        return "Start";
    }

    public void giveAdena(long count) {
        player.addAdena(count, "");
    }
    public void giveItems(int itemId) {
        giveItems(itemId, 1);
    }

    public void giveItems(int itemId, long count) {
        if (itemId == ItemTemplate.ITEM_ID_ADENA)
            giveItems(itemId, count, true);
        else
            giveItems(itemId, count, false);
    }

    public void giveItems(int itemId, long count, boolean rate) {
        if (player == null)
            return;

        if (count <= 0)
            count = 1;

        if (rate)
            count = (long) (count * getRateQuestsReward());

        ItemFunctions.addItem(player, itemId, count, "Quest " + quest.name);
        player.sendChanges();
    }

    public void giveItems(int itemId, long count, Element element, int power) {
        if (player == null)
            return;

        if (count <= 0)
            count = 1;

        // Get template of item
        ItemTemplate template = ItemHolder.getTemplate(itemId);
        if (template == null)
            return;

        for (int i = 0; i < count; i++) {
            ItemInstance item = ItemFunctions.createItem(itemId);

            if (element != Element.NONE)
                item.setAttributeElement(element, power);

            // Add items to getPlayer's inventory
            player.inventory.addItem(item, "Quest " + quest.name);
        }

        player.sendPacket(SystemMessage2.obtainItems(template.itemId(), count, 0));
        player.sendChanges();
    }

    public void dropItem(NpcInstance npc, int itemId) {
        dropItem(npc, itemId, 1);
    }

    public void dropItem(NpcInstance npc, int itemId, long count) {
        if (player == null)
            return;

        ItemInstance item = ItemFunctions.createItem(itemId);
        item.setCount(count);
        item.dropToTheGround(player, npc);
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов.
     * <br><br>
     * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
     * нужно набить определенное количество предметов не осуществляется.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     *
     * @param count      количество при рейтах 1х
     * @param calcChance шанс при рейтах 1х, в процентах
     * @return количество вещей для дропа, может быть 0
     */
    private int rollDrop(int count, double calcChance) {
        if (calcChance <= 0 || count <= 0)
            return 0;
        return rollDrop(count, count, calcChance);
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов.
     * <br><br>
     * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
     * нужно набить определенное количество предметов не осуществляется.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     *
     * @param min        минимальное количество при рейтах 1х
     * @param max        максимальное количество при рейтах 1х
     * @param calcChance шанс при рейтах 1х, в процентах
     * @return количество вещей для дропа, может быть 0
     */
    private int rollDrop(int min, int max, double calcChance) {
        if (calcChance <= 0 || min <= 0 || max <= 0)
            return 0;
        int dropmult = 1;
        calcChance *= getRateQuestsDrop();
        if (quest.getParty() > Quest.PARTY_NONE) {
            if (player.getParty() != null)
                calcChance *= Config.ALT_PARTY_BONUS.get(player.getParty().getMemberCountInRange(player, Config.ALT_PARTY_DISTRIBUTION_RANGE) - 1) / 100.;
        }
        if (calcChance > 100) {
            if ((int) Math.ceil(calcChance / 100) <= calcChance / 100)
                calcChance = Math.nextUp(calcChance);
            dropmult = (int) Math.ceil(calcChance / 100);
            calcChance = calcChance / dropmult;
        }
        return Rnd.chance(calcChance) ? Rnd.get(min * dropmult, max * dropmult) : 0;
    }

    private double getRateQuestsDrop() {
        if (Config.ALLOW_ADDONS_CONFIG)
            return Config.RATE_QUESTS_DROP * AddonsConfig.getQuestDropRates(quest);
        return Config.RATE_QUESTS_DROP;
    }

    public double getRateQuestsReward() {
        double bonus = 1.;
        if (Config.ALLOW_ADDONS_CONFIG)
            return Config.RATE_QUESTS_REWARD * bonus * AddonsConfig.getQuestRewardRates(quest);
        return Config.RATE_QUESTS_REWARD * bonus;
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
     * проверяет максимум, а так же проигрывает звук получения вещи.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     *
     * @param itemId id вещи
     * @param min    минимальное количество при рейтах 1х
     * @param max    максимальное количество при рейтах 1х
     * @param limit  максимум таких вещей
     * @return true если после выполнения количество достигло лимита
     */
    public boolean rollAndGive(int itemId, int min, int max, int limit, double calcChance) {
        if (calcChance <= 0 || min <= 0 || max <= 0 || limit <= 0 || itemId <= 0)
            return false;
        long count = rollDrop(min, max, calcChance);
        if (count > 0) {
            long alreadyCount = getQuestItemsCount(itemId);
            if (alreadyCount + count > limit)
                count = limit - alreadyCount;
            if (count > 0) {
                giveItems(itemId, count, false);
                if (count + alreadyCount < limit)
                    playSound(SOUND_ITEMGET);
                else {
                    playSound(Quest.SOUND_MIDDLE);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
     * а так же проигрывает звук получения вещи.
     * <br><br>
     * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
     * нужно набить определенное количество предметов не осуществляется.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     */
    public void rollAndGive(int itemId, int min, int max, double calcChance) {
        if (calcChance <= 0 || min <= 0 || max <= 0 || itemId <= 0)
            return;
        int count = rollDrop(min, max, calcChance);
        if (count > 0) {
            giveItems(itemId, count, false);
            playSound(SOUND_ITEMGET);
        }
    }

    /**
     * Этот метод рассчитывает количество дропнутых вещей в зависимости от рейтов и дает их,
     * а так же проигрывает звук получения вещи.
     * <br><br>
     * Следует учесть, что контроль за верхним пределом вещей в квестах, в которых
     * нужно набить определенное количество предметов не осуществляется.
     * <br><br>
     * Ни один из передаваемых параметров не должен быть равен 0
     *
     * @param itemId id вещи
     * @param count  количество при рейтах 1х
     */
    public void rollAndGive(int itemId, int count, double calcChance) {
        if (calcChance <= 0 || count <= 0 || itemId <= 0)
            return;
        int countToDrop = rollDrop(count, calcChance);
        if (countToDrop > 0) {
            giveItems(itemId, countToDrop, false);
            playSound(SOUND_ITEMGET);
        }
    }

    public boolean isCompleted() {
        return getState() == Quest.COMPLETED;
    }

    public boolean isStarted() {
        return getState() == Quest.STARTED;
    }

    public boolean isCreated() {
        return getState() == Quest.CREATED;
    }

    public void killNpcByObjectId(int _objId) {
        NpcInstance npc = GameObjectsStorage.getNpc(_objId);
        if (npc != null)
            npc.doDie(null);
        else
            _log.warn("Attemp to kill object that is not npc in quest " + quest.id);
    }

    public boolean isSet(String var) {
        return getInt(var) == 1;
    }

    public void inc(String var) {
        set(var, getInt(var) + 1);
    }

    public void set(String var) {
        set(var, 1);
    }

    public void set(String var, long intval) {
        set(var, "" + intval);
    }

    public void set(String var, String val) {
        set(var, val, true);
    }


    public void set(String var, String val, boolean store) {
        if (val == null)
            val = "";

        vars.put(var, val);

        if (store)
            Quest.updateQuestVarInDb(this, var, val);

    }

    public void setStateAndNotSave(int state) {
        if (player == null)
            return;

        this.state = state;

        if (quest.isVisible() && isStarted())
            player.sendPacket(new ExShowQuestMark(quest.id));

        player.sendPacket(new QuestList(player));
    }

    public void playSound(String sound) {
        if (player != null)
            player.sendPacket(new PlaySound(sound));
    }

    public void playTutorialVoice(String voice) {
        if (player != null)
            player.sendPacket(new PlaySound(PlaySound.Type.VOICE, voice, 0, 0, player.getLoc()));
    }

    private void onTutorialClientEvent() {
        if (player != null)
            player.sendPacket(new TutorialEnableClientEvent(0));
    }

    public void showQuestionMark(int number) {
        if (player != null)
            player.sendPacket(new TutorialShowQuestionMark(number));
    }

    public void showTutorialPage(String html) {
        if (player == null)
            return;
        String text = HtmCache.INSTANCE.getNotNull("quests/_255_Tutorial/" + html, player);
        player.sendPacket(new TutorialShowHtml(text));
    }

    public void closeTutorial() {
        onTutorialClientEvent();
        if (player != null) {
            player.sendPacket(TutorialCloseHtml.STATIC);
            player.deleteQuickVar("watchingTutorial");
            Quest q = QuestManager.getQuest(_255_Tutorial.class);
            player.processQuestEvent(q, "onTutorialClose", null);
        }
    }

    public void showTutorialHTML(String html) {
        if (player != null) {
            // Alexander - Added support for showing crest images on tutorial windows
            html = ImagesCache.sendUsedImages(html, player);

            // Alexander - If the html has crests then we should delay the tutorial html so the images reach their destination before the htm
            if (html.startsWith("CREST")) {
                ThreadPoolManager.INSTANCE.schedule(new TutorialShowThread(html.substring(5)), 200);
            } else {
                player.sendPacket(new TutorialShowHtml(html));
                player.addQuickVar("watchingTutorial", true);
            }
        }
    }

    /**
     * Start a timer for quest.<BR><BR>
     *
     * @param name<BR> The name of the timer. Will also be the value for event of onEvent
     * @param time<BR> The milisecond value the timer will elapse
     */
    public void startQuestTimer(String name, long time) {
        startQuestTimer(name, time, null);
    }

    /**
     * Add a timer to the quest.<BR><BR>
     *
     * @param name: name of the timer (also passed back as "event" in notifyEventClanAttack)
     * @param time: time in ms for when to fire the timer
     * @param npc:  npc associated with this timer (can be null)
     */
    public void startQuestTimer(String name, long time, NpcInstance npc) {
        QuestTimer timer = new QuestTimer(name, time, npc);
        timer.setQuestState(this);
        QuestTimer oldTimer = timers.put(name, timer);
        if (oldTimer != null)
            oldTimer.stop();
        timer.start();
    }

    public boolean isRunningQuestTimer(String name) {
        return timers.get(name) != null;
    }

    public boolean cancelQuestTimer(String name) {
        QuestTimer timer = removeQuestTimer(name);
        if (timer != null)
            timer.stop();
        return timer != null;
    }

    QuestTimer removeQuestTimer(String name) {
        QuestTimer timer = timers.remove(name);
        if (timer != null)
            timer.setQuestState(null);
        return timer;
    }

    public void pauseQuestTimers() {
        quest.pauseQuestTimers(this);
    }

    public void stopQuestTimers() {
        timers.values().forEach(timer -> {
            timer.setQuestState(null);
            timer.stop();
        });
        timers.clear();
    }

    public void resumeQuestTimers() {
        quest.resumeQuestTimers(this);
    }

    Map<String, QuestTimer> getTimers() {
        return timers;
    }

    public void takeItems(Collection<Integer> itemsIds) {
        itemsIds.forEach(this::takeItems);
    }

    public void takeItems(int itemId) {
        takeItems(itemId, -1);
    }

    /**
     * Удаляет указанные предметы из инвентаря игрока, и обновляет инвентарь
     *
     * @param itemId : id удаляемого предмета
     * @param count  : число удаляемых предметов<br>
     *               Если count передать -1, то будут удалены все указанные предметы.
     */
    public void takeItems(int itemId, long count) {
        if (player == null || count == 0)
            return;
        // Get object item from getPlayer's inventory list
        ItemInstance item = player.getInventory().getItemByItemId(itemId);
        if (item == null)
            return;
        // Tests on count value in order not to have negative value
        if (count < 0 || count > item.getCount())
            count = item.getCount();

        // Destroy the quantity of items wanted
        player.getInventory().destroyItemByItemId(itemId, count, "Quest " + quest.name);
        // Send message of destruction to client
        player.sendPacket(SystemMessage2.removeItems(itemId, count));
    }

    public void takeAllItems(int... itemsIds) {
        Arrays.stream(itemsIds).forEach(itemId -> takeItems(itemId, -1));
    }

    public void takeAllItems(Collection<Integer> itemsIds) {
        itemsIds.forEach(itemId -> takeItems(itemId, -1));
    }

    public void unset(String var) {
        if (var == null)
            return;
        String old = vars.remove(var);
        if (old != null)
            Quest.deleteQuestVarInDb(this, var);
    }

    private boolean checkPartyMember(Player member, int state, int maxrange, GameObject rangefrom) {
        if (member == null)
            return false;
        if (rangefrom != null && maxrange > 0 && !member.isInRange(rangefrom, maxrange))
            return false;
        QuestState qs = member.getQuestState(quest);
        return qs != null && qs.getState() == state;
    }

    private List<Player> getPartyMembers(int state, int maxrange, GameObject rangefrom) {
        Party party = player.getParty();
        if (party == null) {
            if (checkPartyMember(player, state, maxrange, rangefrom))
                return List.of(player);
            return List.of();
        }

        return party.getMembersStream().filter(m -> checkPartyMember(m, state, maxrange, rangefrom))
                .collect(Collectors.toList());
    }

    public Player getRandomPartyMember(int state, int maxrangefromplayer) {
        List<Player> list = getPartyMembers(state, maxrangefromplayer, player);
        if (list.size() == 0)
            return null;
        return Rnd.get(list);
    }


    /**
     * Add spawn for getPlayer instance
     * Return object id of newly spawned npc
     */
    public NpcInstance addSpawn(int npcId) {
        return addSpawn(npcId, player.getLoc(), 0, 0);
    }

    public NpcInstance addSpawn(int npcId, int despawnDelay) {
        return addSpawn(npcId, player.getLoc(), 0, despawnDelay);
    }

    public NpcInstance addSpawn(int npcId, Location loc) {
        return addSpawn(npcId, loc, 0, 0);
    }

    public NpcInstance addSpawn(int npcId, Location loc, int randomOffset, int despawnDelay) {
        return quest.addSpawn(npcId, loc, randomOffset, despawnDelay);
    }

    public NpcInstance findTemplate(int npcId) {
        return SpawnManager.INSTANCE.getSpawners("NONE").stream()
                .filter(Objects::nonNull)
                .filter(spawn -> spawn.getCurrentNpcId() == npcId)
                .map(Spawner::getLastSpawn)
                .findFirst().orElse(null);
    }

    public int calculateLevelDiffForDrop(int mobLevel, int player) {
        if (!Config.DEEPBLUE_DROP_RULES)
            return 0;
        return Math.max(player - mobLevel - Config.DEEPBLUE_DROP_MAXDIFF, 0);
    }

    public int getCond() {
        if (cond == null) {
            int val = getInt(VAR_COND);
            if ((val & 0x80000000) != 0) {
                val &= 0x7fffffff;
                for (int i = 1; i < 32; i++) {
                    val = (val >> 1);
                    if (val == 0) {
                        val = i;
                        break;
                    }
                }
            }
            cond = val;
        }

        return cond;
    }

    public void setCond(int newCond) {
        setCond(newCond, true);
    }

    public void setCond(int newCond, boolean store) {
        if (newCond == getCond())
            return;

        int oldCond = getInt(VAR_COND);
        cond = newCond;

        if ((oldCond & 0x80000000) != 0) {
            // уже используется второй формат
            if (newCond > 2) // Если этап меньше 3 то возвращаемся к первому варианту.
            {
                oldCond &= 0x80000001 | ((1 << newCond) - 1);
                newCond = oldCond | (1 << (newCond - 1));
            }
        } else {
            // Второй вариант теперь используется всегда если этап больше 2
            if (newCond > 2)
                newCond = 0x80000001 | (1 << (newCond - 1)) | ((1 << oldCond) - 1);
        }

        final String sVal = String.valueOf(newCond);
        set(VAR_COND, sVal, false);
        if (store)
            Quest.updateQuestVarInDb(this, VAR_COND, sVal);

        if (player != null) {
            player.sendPacket(new QuestList(player));
            if (newCond != 0 && quest.isVisible() && isStarted())
                player.sendPacket(new ExShowQuestMark(quest.id));
        }
    }

    /**
     * Устанавлевает время, когда квест будет доступен персонажу.
     * Метод используется для квестов, которые проходятся один раз в день.
     */
    private void setRestartTime() {
        Calendar reDo = Calendar.getInstance();
        if (reDo.get(Calendar.HOUR_OF_DAY) >= RESTART_HOUR)
            reDo.add(Calendar.DATE, 1);
        reDo.set(Calendar.HOUR_OF_DAY, RESTART_HOUR);
        reDo.set(Calendar.MINUTE, RESTART_MINUTES);
        set("restartTime", reDo.getTimeInMillis());
    }

    /**
     * Проверяет, наступило ли время для выполнения квеста.
     * Метод используется для квестов, которые проходятся один раз в день.
     *
     * @return boolean
     */
    public boolean isNowAvailable() {
        return getLong("restartTime") <= System.currentTimeMillis();
    }

    public class OnDeathListenerImpl implements OnDeathListener {
        @Override
        public void onDeath(Creature actor, Creature killer) {
            Player player = actor.getPlayer();
            if (player == null)
                return;

            player.removeListener(this);

            quest.notifyDeath(killer, actor, QuestState.this);
        }
    }

    private class PlayerOnKillListenerImpl implements OnKillListener {
        @Override
        public void onKill(Creature actor, Creature victim) {
            if (!(victim instanceof Player))
                return;

            Player actorPlayer = (Player) actor;
            Stream<Player> players;
            switch (quest.getParty()) {
                case Quest.PARTY_NONE:
                    players = Stream.of(actorPlayer);
                    break;
                case Quest.PARTY_ALL:
                    if (actorPlayer.getParty() == null)
                        players = Stream.of(actorPlayer);
                    else {
                        players = actorPlayer.getParty().getMembersStream()
                                .filter(m -> m.isInRange(actorPlayer, Creature.INTERACTION_DISTANCE));

                    }
                    break;
                default:
                    players = Stream.empty();
                    break;
            }

            players.map(player1 -> player.getQuestState(quest))
                    .filter(Objects::nonNull)
                    .filter(questState -> !questState.isCompleted())
                    .forEach(questState ->
                            quest.notifyKill((Player) victim, questState));

        }

    }

    private class TutorialShowThread implements Runnable {
        private final String html;

        TutorialShowThread(String html) {
            this.html = html;
        }

        @Override
        public void run() {
            if (player == null)
                return;

            player.sendPacket(new TutorialShowHtml(html));
            player.addQuickVar("watchingTutorial", true);
        }
    }
}