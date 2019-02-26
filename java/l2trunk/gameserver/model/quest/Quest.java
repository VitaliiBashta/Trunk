package l2trunk.gameserver.model.quest;

import l2trunk.commons.logging.LogUtils;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.olympiad.OlympiadGame;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExNpcQuestHtmlMessage;
import l2trunk.gameserver.network.serverpackets.ExQuestNpcLogList;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.HtmlUtils;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.NpcUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static l2trunk.commons.lang.NumberUtils.toInt;

public class Quest {
    public static final String SOUND_ITEMGET = "ItemSound.quest_itemget";
    public static final String SOUND_MIDDLE = "ItemSound.quest_middle";
    public static final int PARTY_NONE = 0;
    public static final int PARTY_ALL = 2;
    public final static int CREATED = 1;
    public final static int STARTED = 2;
    public final static int COMPLETED = 3;
    public static final int ADENA_ID = 57;
    protected static final String SOUND_ACCEPT = "ItemSound.quest_accept";
    protected static final String SOUND_FINISH = "ItemSound.quest_finish";
    protected static final String SOUND_GIVEUP = "ItemSound.quest_giveup";
    protected static final String SOUND_TUTORIAL = "ItemSound.quest_tutorial";
    protected static final String SOUND_JACKPOT = "ItemSound.quest_jackpot";
    protected static final String SOUND_HORROR2 = "SkillSound5.horror_02";
    protected static final String SOUND_BEFORE_BATTLE = "Itemsound.quest_before_battle";
    protected static final String SOUND_FANFARE_MIDDLE = "ItemSound.quest_fanfare_middle";
    protected static final String SOUND_FANFARE2 = "ItemSound.quest_fanfare_2";
    protected static final String SOUND_BROKEN_KEY = "ItemSound2.broken_key";
    protected static final String SOUND_ENCHANT_SUCESS = "ItemSound3.sys_enchant_sucess";
    protected static final String SOUND_ENCHANT_FAILED = "ItemSound3.sys_enchant_failed";
    protected static final String SOUND_ED_CHIMES05 = "AmdSound.ed_chimes_05";
    protected static final String SOUND_ARMOR_WOOD_3 = "ItemSound.armor_wood_3";
    protected static final String SOUND_ITEM_DROP_EQUIP_ARMOR_CLOTH = "ItemSound.item_drop_equip_armor_cloth";
    protected static final String NO_QUEST_DIALOG = "no-quest";
    protected static final int PARTY_ONE = 1;
    final static int DELAYED = 4;
    private static final Logger LOG = LoggerFactory.getLogger(Quest.class);
    public final String name;
    public final int id;
    private final int party;
    //карта с приостановленными квестовыми таймерами для каждого игрока
    private final Map<Integer, Map<String, QuestTimer>> pausedQuestTimers = new ConcurrentHashMap<>();
    private Set<Integer> questItems = new HashSet<>();
    private Map<Integer, List<QuestNpcLogInfo>> npcLogList = new HashMap<>();

    public Quest(boolean party) {
        this(party ? 1 : 0);
    }

    /**
     * 0 - по ластхиту, 1 - случайно по пати, 2 - всей пати.
     */
    public Quest(int party) {
        name = getClass().getSimpleName();
        id = toInt(name.split("_")[1]);
        this.party = party;
        QuestManager.addQuest(this);
    }

    /**
     * Update informations regarding quest in database.<BR>
     * <U><I>Actions :</I></U><BR>
     * <LI>Get ID state of the quest recorded in object qs</LI>
     * <LI>Save in database the ID state (with or without the star) for the variable called "&lt;state&gt;" of the quest</LI>
     *
     * @param qs : QuestState
     */
    static void updateQuestInDb(QuestState qs) {
        updateQuestVarInDb(qs, "<state>", qs.getStateName());
    }

    /**
     * Insert in the database the quest for the getPlayer.
     *
     * @param qs    : QuestState pointing out the state of the quest
     * @param var   : String designating the name of the variable for the quest
     * @param value : String designating the value of the variable for the quest
     */
    static void updateQuestVarInDb(QuestState qs, String var, String value) {
        Player player = qs.player;
        if (player == null)
            return;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("REPLACE INTO character_quests (char_id,name,var,value) VALUES (?,?,?,?)")) {
            statement.setInt(1, qs.player.objectId());
            statement.setString(2, qs.quest.name);
            statement.setString(3, var);
            statement.setString(4, value);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Could not insert char quest:", e);
        }
    }

    /**
     * Delete the getPlayer's quest from database.
     *
     * @param qs : QuestState pointing out the getPlayer's quest
     */
    static void deleteQuestInDb(QuestState qs) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=?")) {
            statement.setInt(1, qs.player.objectId());
            statement.setString(2, qs.quest.name);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("could not delete char quest", e);
        }
    }

    static void deleteQuestVarInDb(QuestState qs, String var) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND name=? AND var=?")) {
            statement.setInt(1, qs.player.objectId());
            statement.setString(2, qs.quest.name);
            statement.setString(3, var);
            statement.executeUpdate();
        } catch (SQLException e) {
            LOG.error("Could not delete char quest", e);
        }
    }

    /**
     * Add quests to the L2Player.<BR><BR>
     * <U><I>Action : </U></I><BR>
     * Add state of quests, drops and variables for quests in the HashMap _quest of L2Player
     *
     * @param player : Player who is entering the world
     */
    public static void restoreQuestStates(Player player, Connection con) {
        try (PreparedStatement invalidQuestData = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? and name=?");
             PreparedStatement statement = con.prepareStatement("SELECT name,value FROM character_quests WHERE char_id=? AND var=?")) {
            statement.setInt(1, player.objectId());
            statement.setString(2, "<state>");
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    String questName = rset.getString("name");
                    String state = rset.getString("value");

                    if ("Start".equalsIgnoreCase(state)) {// невзятый квест
                        invalidQuestData.setInt(1, player.objectId());
                        invalidQuestData.setString(2, questName);
                        invalidQuestData.executeUpdate();
                        continue;
                    }

                    // Search quest associated with the ID
                    Quest q = QuestManager.getQuest(questName);
                    if (q == null) {
                        if (!Config.DONTLOADQUEST)
                            LOG.warn("Unknown quest " + questName + " for getPlayer " + player.getName());
                        continue;
                    }

                    // Create a new QuestState for the getPlayer that will be added to the getPlayer's list of quests
                    new QuestState(q, player, getStateId(state));
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while restoring Quest States ", e);
        }
        // Get list of quests owned by the getPlayer from the DB in order to add variables used in the quest.
        try (PreparedStatement statement = con.prepareStatement("SELECT name,var,value FROM character_quests WHERE char_id=?")) {
            statement.setInt(1, player.objectId());
            try (ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    String questId = rset.getString("name");
                    String var = rset.getString("var");
                    String value = rset.getString("value");
                    // Get the QuestState saved in the loop before
                    QuestState qs = player.getQuestState(questId);
                    if (qs == null)
                        continue;
                    // затычка на пропущенный первый конд
                    if (var.equals("cond") && Integer.parseInt(value) < 0)
                        value = String.valueOf(Integer.parseInt(value) | 1);
                    // Add parameter to the quest
                    qs.set(var, value, false);
                }
            }
        } catch (SQLException e) {
            LOG.error("Error while restoring Quest States ", e);
        }
    }

    private static int getStateId(String state) {
        if ("Start".equalsIgnoreCase(state))
            return CREATED;
        else if ("Started".equalsIgnoreCase(state))
            return STARTED;
        else if ("Completed".equalsIgnoreCase(state))
            return COMPLETED;
        else if ("Delayed".equalsIgnoreCase(state))
            return DELAYED;
        return CREATED;
    }

    protected static NpcInstance addSpawnToInstance(int npcId, Location loc, int refId) {
        NpcInstance npc = NpcHolder.getTemplate(npcId).getNewInstance();
        npc.setReflection(refId);
        npc.setSpawnedLoc(Location.findPointToStay(loc, 50, 50, npc.getGeoIndex()));
        npc.spawnMe(npc.getSpawnedLoc());
        return npc;
    }

    protected void addQuestItem(Collection<Integer> ids) {
        ids.forEach(this::addQuestItem);
    }

    protected void addQuestItem(int... ids) {
        Arrays.stream(ids).forEach(this::addQuestItem);
    }

    protected void addQuestItem(int id) {
        if (id < 1) return;
        if (questItems.contains(id))
            LOG.warn("Item " + ItemHolder.getTemplate(id) + " multiple times in quest drop in " + name);
        questItems.add(id);
    }

    public Set<Integer> getItems() {
        return questItems;
    }

    protected boolean isQuestItem(int id) {
        return questItems.contains(id);
    }

    public List<QuestNpcLogInfo> getNpcLogList(int cond) {
        return npcLogList.get(cond);
    }

    protected void addAttackId(Collection<Integer> attackIds) {
        attackIds.forEach(id -> addEventId(id, QuestEventType.ATTACKED_WITH_QUEST));
    }

    protected void addAttackId(Integer... attackIds) {
        Stream.of(attackIds)
                .forEach(id -> addEventId(id, QuestEventType.ATTACKED_WITH_QUEST));
    }

    /**
     * Add this quest to the list of quests that the passed mob will respond to
     * for the specified Event type.<BR>
     * <BR>
     *
     * @param npcId     : id of the NPC to register
     * @param eventType : type of event being registered
     */
    private void addEventId(int npcId, QuestEventType eventType) {
        try {
            NpcTemplate t = NpcHolder.getTemplate(npcId);
            if (t != null)
                t.addQuestEvent(eventType, this);
        } catch (RuntimeException e) {
            LOG.error("Error while adding Event Id. Npc ID:" + npcId + " event Type:" + eventType, e);
        }
    }

    protected void addKillId(Collection<Integer> killIds) {
        killIds.forEach(this::addKillId);
    }

    protected void addKillId(int... killIds) {
        Arrays.stream(killIds).forEach(this::addKillId);

    }

    protected void addKillId(int killid) {
        addEventId(killid, QuestEventType.MOB_KILLED_WITH_QUEST);
    }


    protected void addKillNpcWithLog(int cond, String varName, int max, int... killIds) {
        addKillNpcWithLog(cond, varName, max, Arrays.stream(killIds).boxed().collect(Collectors.toList()));
    }

    protected void addKillNpcWithLog(int cond, String varName, int max, List<Integer> killIds) {
        if (killIds.size() == 0)
            throw new IllegalArgumentException("Npc list cant be empty!");

        addKillId(killIds);
        if (npcLogList.isEmpty())
            npcLogList = new HashMap<>(5);

        List<QuestNpcLogInfo> vars = npcLogList.computeIfAbsent(cond, k -> new ArrayList<>(5));

        vars.add(new QuestNpcLogInfo(killIds, varName, max));
    }

    protected boolean updateKill(NpcInstance npc, QuestState st) {
        Player player = st.player;
        if (player == null)
            return false;
        List<QuestNpcLogInfo> vars = getNpcLogList(st.getCond());
        if (vars == null)
            return false;
        boolean done = true;
        boolean find = false;
        for (QuestNpcLogInfo info : vars) {
            int count = st.getInt(info.getVarName());
            if (!find && info.getNpcIds().contains(npc.getNpcId())) {
                find = true;
                if (count < info.getMaxCount()) {
                    st.set(info.getVarName(), ++count);
                    player.sendPacket(new ExQuestNpcLogList(st));
                }
            }

            if (count != info.getMaxCount())
                done = false;
        }

        return done;
    }


    protected void addSkillUseId(int... npcIds) {
        Arrays.stream(npcIds).forEach(npcId -> addEventId(npcId, QuestEventType.MOB_TARGETED_BY_SKILL));
    }

    protected void addStartNpc(int... npcIds) {
        Arrays.stream(npcIds).forEach(this::addStartNpc);
    }

    protected void addStartNpc(Collection<Integer> npcIds) {
        npcIds.forEach(this::addStartNpc);
    }

    protected void addStartNpc(int npcId) {
        addTalkId(npcId);
        addEventId(npcId, QuestEventType.QUEST_START);
    }

    /**
     * Add the quest to the NPC's first-talk (default action dialog)
     */
    protected void addFirstTalkId(int... npcIds) {
        Arrays.stream(npcIds).forEach(npcId -> addEventId(npcId, QuestEventType.NPC_FIRST_TALK));
    }

    public void addTalkId(int talkId) {
        addEventId(talkId, QuestEventType.QUEST_TALK);
    }

    public void addTalkId(int... talkIds) {
        Arrays.stream(talkIds).forEach(this::addTalkId);
    }

    public void addTalkId(Collection<Integer> talkIds) {
        talkIds.forEach(this::addTalkId);
    }

    /**
     * Возвращает название квеста (Берется с npcstring-*.dat)
     * state 1 = ""
     * state 2 = "In Progress"
     * state 3 = "Done"
     */
    public String getDescr(Player player) {
        if (!isVisible())
            return null;

        QuestState qs = player.getQuestState(this);
        int state = 2;
        if (qs == null || qs.isCreated() && qs.isNowAvailable())
            state = 1;
        else if (qs.isCompleted() || !qs.isNowAvailable())
            state = 3;

        int fStringId = id;
        if (fStringId >= 10000)
            fStringId -= 5000;
        fStringId = fStringId * 100 + state;
        return HtmlUtils.htmlNpcString(fStringId);
    }

    public final int getParty() {
        return party;
    }

    /**
     * Add a new QuestState to the database and return it.
     *
     * @return QuestState : QuestState created
     */
    public QuestState newQuestState(Player player, int state) {
        QuestState qs = new QuestState(this, player, state);
        updateQuestInDb(qs);
        return qs;
    }

    public QuestState newQuestStateAndNotSave(Player player, int state) {
        return new QuestState(this, player, state);
    }

    public void notifyAttack(NpcInstance npc, QuestState qs) {
        try {
            onAttack(npc, qs);
        } catch (RuntimeException e) {
            showError(qs.player, e);
            return;
        }
        showResult(npc, qs.player, null);
    }

    void notifyDeath(Creature killer, Creature victim, QuestState qs) {
        try {
            onDeath(killer, victim, qs);
        } catch (RuntimeException e) {
            showError(qs.player, e);
            return;
        }
        showResult(null, qs.player, null);
    }

    public void notifyEvent(String event, QuestState qs, NpcInstance npc) {
        String res;
        try {
            res = onEvent(event, qs, npc);
        } catch (RuntimeException e) {
            showError(qs.player, e);
            return;
        }
        showResult(npc, qs.player, res);
    }

    public void notifyKill(NpcInstance npc, QuestState qs) {
        try {
            onKill(npc, qs);
        } catch (RuntimeException e) {
            showError(qs.player, e);
            return;
        }
        showResult(npc, qs.player, null);
    }

    public void notifyKill(Player target, QuestState qs) {
        try {
            onKill(target, qs);
        } catch (RuntimeException e) {
            showError(qs.player, e);
            return;
        }
        showResult(null, qs.player, null);
    }

    /**
     * Override the default NPC dialogs when a quest defines this for the given NPC
     */
    public final boolean notifyFirstTalk(NpcInstance npc, Player player) {
        String res;
        try {
            res = onFirstTalk(npc, player);
        } catch (RuntimeException e) {
            showError(player, e);
            return true;
        }
        // if the quest returns text to display, display it. Otherwise, use the default npc text.
        return showResult(npc, player, res, true);
    }

    public boolean notifyTalk(NpcInstance npc, QuestState qs) {
        String res;
        try {
            res = onTalk(npc, qs);
        } catch (RuntimeException e) {
            showError(qs.player, e);
            return true;
        }
        return showResult(npc, qs.player, res);
    }

    public void notifySkillUse(NpcInstance npc, Skill skill, QuestState qs) {
        try {
            onSkillUse(npc, skill, qs);
        } catch (RuntimeException e) {
            showError(qs.player, e);
            return;
        }
        showResult(npc, qs.player, null);
    }

    final void notifyCreate(QuestState qs) {
        try {
            onCreate(qs);
        } catch (RuntimeException e) {
            showError(qs.player, e);
        }
    }

    public void onCreate(QuestState qs) {
    }

    protected void onAttack(NpcInstance npc, QuestState qs) {
    }

    protected void onDeath(Creature killer, Creature victim, QuestState qs) {
    }

    protected String onEvent(String event, QuestState qs, NpcInstance npc) {
        return null;
    }

    public void onKill(NpcInstance npc, QuestState qs) {
    }

    protected void onKill(Player killed, QuestState st) {
    }

    protected String onFirstTalk(NpcInstance npc, Player player) {
        return null;
    }

    protected String onTalk(NpcInstance npc, QuestState qs) {
        return null;
    }

    protected void onSkillUse(NpcInstance npc, Skill skill, QuestState qs) {
    }

    public void onOlympiadEnd(OlympiadGame og, QuestState qs) {
    }

    public void onAbort(QuestState qs) {
    }

    public boolean canAbortByPacket() {
        return true;
    }

    private void showError(Player player, Throwable t) {
        LOG.error("Quest Error!", t);
        if (player.isGM()) {
            String res = "<html><body><title>Script error</title>"
                    + LogUtils.dumpStack(t).replace("\n", "<br>") + "</body></html>";
            showResult(null, player, res);
        }
    }

    private void showHtmlFile(Player player, String fileName, boolean showQuestInfo) {
        if (player == null)
            return;

        GameObject target = player.getTarget();
        NpcHtmlMessage npcReply = showQuestInfo
                ? new ExNpcQuestHtmlMessage(target == null ? 5 : target.objectId(), id)
                : new NpcHtmlMessage(target == null ? 5 : target.objectId());
        npcReply.setFile("quests/" + getClass().getSimpleName() + "/" + fileName);

        player.sendPacket(npcReply);
    }

    protected void showHtmlFile(Player player, String fileName, String rep2) {
        if (player == null)
            return;

        GameObject target = player.getTarget();
        NpcHtmlMessage npcReply = new NpcHtmlMessage(target == null ? 5 : target.objectId());
        npcReply.setFile("quests/" + getClass().getSimpleName() + "/" + fileName);

        npcReply.replace("%siege_time%", rep2);

        player.sendPacket(npcReply);
    }

    private void showSimpleHtmFile(Player player, String fileName) {
        if (player == null)
            return;

        NpcHtmlMessage npcReply = new NpcHtmlMessage(5);
        npcReply.setFile(fileName);
        player.sendPacket(npcReply);
    }

    /**
     * Show a message to getPlayer.<BR><BR>
     * <U><I>Concept : </I></U><BR>
     * 3 cases are managed according to the value of the parameter "res" :<BR>
     * <LI><U>"res" ends with string ".html" :</U> an HTML is opened in order to be shown in a dialog box</LI>
     * <LI><U>"res" starts with tag "html" :</U> the message hold in "res" is shown in a dialog box</LI>
     * <LI><U>"res" is null :</U> do not show any message</LI>
     * <LI><U>"res" is empty string :</U> show default message</LI>
     * <LI><U>otherwise :</U> the message hold in "res" is shown in chat box</LI>
     *
     * @param res : String pointing out the message to show at the getPlayer
     */
    private boolean showResult(NpcInstance npc, Player player, String res) {
        return showResult(npc, player, res, false);
    }

    private boolean showResult(NpcInstance npc, Player player, String res, boolean isFirstTalk) {
        boolean showQuestInfo = showQuestInfo(player);
        if (isFirstTalk)
            showQuestInfo = false;
        if (res == null) // do not show message
            return true;
        if (res.isEmpty()) // show default npc message
            return false;
        if (res.startsWith("no_quest") || "noquest".equalsIgnoreCase(res) || "no-quest".equalsIgnoreCase(res))
            showSimpleHtmFile(player, "no-quest.htm");
        else if ("completed".equalsIgnoreCase(res))
            showSimpleHtmFile(player, "completed-quest.htm");
        else if (res.endsWith(".htm"))
            showHtmlFile(player, res, showQuestInfo);
        else {
            NpcHtmlMessage npcReply = showQuestInfo
                    ? new ExNpcQuestHtmlMessage(npc == null ? 5 : npc.objectId(), id)
                    : new NpcHtmlMessage(npc == null ? 5 : npc.objectId());
            npcReply.setHtml(res);
            player.sendPacket(npcReply);
        }
        return true;
    }

    // Проверяем, показывать ли информацию о квесте в диалоге.
    private boolean showQuestInfo(Player player) {
        QuestState qs = player.getQuestState(this);
        if (qs != null && qs.getState() != CREATED)
            return false;
        return isVisible();
    }

    // Останавливаем и сохраняем таймеры (при выходе из игры)
    void pauseQuestTimers(QuestState qs) {
        if (qs.getTimers().isEmpty())
            return;

        qs.getTimers().values().forEach(timer -> {
            timer.setQuestState(null);
            timer.pause();
        });

        pausedQuestTimers.put(qs.player.objectId(), qs.getTimers());
    }

    // =========================================================
    //  QUEST SPAWNS
    // =========================================================

    // Восстанавливаем таймеры (при входе в игру)
    void resumeQuestTimers(QuestState qs) {
        Map<String, QuestTimer> timers = pausedQuestTimers.remove(qs.player.objectId());
        if (timers == null)
            return;

        qs.getTimers().putAll(timers);

        qs.getTimers().values().forEach(timer -> {
            timer.setQuestState(qs);
            timer.start();
        });
    }

    protected NpcInstance addSpawn(int npcId, Location loc, int randomOffset, int despawnDelay) {
        NpcInstance result = NpcUtils.spawnSingle(npcId, randomOffset > 50
                ? Location.findPointToStay(loc, 0, randomOffset, ReflectionManager.DEFAULT.getGeoIndex())
                : loc);
        if (despawnDelay > 0 && result != null)
            ThreadPoolManager.INSTANCE.schedule(new DeSpawnScheduleTimerTask(result), despawnDelay);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Quest)) return false;
        Quest quest = (Quest) o;
        return id == quest.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isVisible() {
        return true;
    }

    private class DeSpawnScheduleTimerTask extends RunnableImpl {
        NpcInstance npc;

        DeSpawnScheduleTimerTask(NpcInstance npc) {
            this.npc = npc;
        }

        @Override
        public void runImpl() {
            if (npc != null)
                if (npc.getSpawn() != null)
                    npc.getSpawn().deleteAll();
                else
                    npc.deleteMe();
        }
    }
}