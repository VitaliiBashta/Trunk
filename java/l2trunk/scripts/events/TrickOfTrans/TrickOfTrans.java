package l2trunk.scripts.events.TrickOfTrans;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.actor.player.OnPlayerEnterListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class TrickOfTrans extends Functions implements ScriptFile, OnDeathListener, OnPlayerEnterListener {
        private static final Logger LOG = LoggerFactory.getLogger(TrickOfTrans.class);
        // Эвент Менеджеры
        private static final int EVENT_MANAGER_ID = 32132; // Alchemist\'s Servitor
        private static final int CHESTS_ID = 13036; // Alchemist\'s Chest

        // Рецепты
        private static final int RED_PSTC = 9162; // Red Philosopher''s Stone Transmutation Circle
        private static final int BLUE_PSTC = 9163; // Blue Philosopher''s Stone Transmutation Circle
        private static final int ORANGE_PSTC = 9164; // Orange Philosopher''s Stone Transmutation Circle
        private static final int BLACK_PSTC = 9165; // Black Philosopher''s Stone Transmutation Circle
        private static final int WHITE_PSTC = 9166; // White Philosopher''s Stone Transmutation Circle
        private static final int GREEN_PSTC = 9167; // Green Philosopher''s Stone Transmutation Circle

        // Награды
        private static final int RED_PSTC_R = 9171; // Red Philosopher''s Stone
        private static final int BLUE_PSTC_R = 9172; // Blue Philosopher''s Stone
        private static final int ORANGE_PSTC_R = 9173; // Orange Philosopher''s Stone
        private static final int BLACK_PSTC_R = 9174; // Black Philosopher''s Stone
        private static final int WHITE_PSTC_R = 9175; // White Philosopher''s Stone
        private static final int GREEN_PSTC_R = 9176; // Green Philosopher''s Stone

        // Ключ
        private static final int A_CHEST_KEY = 9205; // Alchemist''s Chest Key

        private static boolean active = false;

        private static List<SimpleSpawner> EM_SPAWNS = new ArrayList<>();
        private static  List<SimpleSpawner> _ch_spawns = new ArrayList<>();

        // Ингридиенты
        private static final int PhilosophersStoneOre = 9168; // Philosopher''s Stone Ore
        private static final int PhilosophersStoneOreMax = 17; // Максимальное Кол-во
        private static final int PhilosophersStoneConversionFormula = 9169; // Philosopher''s Stone Conversion Formula
        private static final int MagicReagents = 9170; // Magic Reagents
        private static final int MagicReagentsMax = 30; // Максимальное Кол-во

        @Override
        public void onLoad() {
            CharListenerList.addGlobal(this);
            if (isActive()) {
                active = true;
                spawnEventManagers();
                LOG.info("Loaded Event: Trick of Trnasmutation [state: activated]");
            } else
                LOG.info("Loaded Event: Trick of Trnasmutation [state: deactivated]");
        }

        private static boolean isActive() {
            return isActive("trickoftrans");
        }

        public void startEvent() {
            if (setActive("trickoftrans", true)) {
                spawnEventManagers();
                System.out.println("Event 'Trick of Transmutation' started.");
                Announcements.INSTANCE.announceByCustomMessage("scripts.events.TrickOfTrans.AnnounceEventStarted");
            } else
                player.sendMessage("Event 'Trick of Transmutation' already started.");

            active = true;

            show("admin/events/events.htm", player);
        }

        public void stopEvent() {
            if (!player.getPlayerAccess().IsEventGm)
                return;
            if (setActive("trickoftrans", false)) {
                unSpawnEventManagers();
                System.out.println("Event 'Trick of Transmutation' stopped.");
                Announcements.INSTANCE.announceByCustomMessage("scripts.events.TrickOfTrans.AnnounceEventStoped");
            } else
                player.sendMessage("Event 'Trick of Transmutation' not started.");

            active = false;

            show("admin/events/events.htm", player);
        }

        @Override
        public void onPlayerEnter(final Player player) {
            if (active)
                Announcements.INSTANCE.announceToPlayerByCustomMessage(player, "scripts.events.TrickOfTrans.AnnounceEventStarted");
        }

        @Override
        public void onReload() {
            unSpawnEventManagers();
        }

        @Override
        public void onShutdown() {
            unSpawnEventManagers();
        }

        private void spawnEventManagers() {
            // Эвент Менеджер
            final List<Location> EVENT_MANAGERS = List.of(
                    new Location(147992, 28616, -2295, 0), // Aden
                    new Location(81919, 148290, -3472, 51432), // Giran
                    new Location(18293, 145208, -3081, 6470), // Dion
                    new Location(-14694, 122699, -3122, 0), // Gludio
                    new Location(-81634, 150275, -3155, 15863)); // Gludin

            // Сундуки
            final List<Location> CHESTS = List.of(
                    new Location(148081, 28614, -2274, 2059), // Aden
                    new Location(147918, 28615, -2295, 31471), // Aden
                    new Location(147998, 28534, -2274, 49152), // Aden
                    new Location(148053, 28550, -2274, 55621), // Aden
                    new Location(147945, 28563, -2274, 40159), // Aden
                    new Location(82012, 148286, -3472, 61567), // Giran
                    new Location(81822, 148287, -3493, 29413), // Giran
                    new Location(81917, 148207, -3493, 49152), // Giran
                    new Location(81978, 148228, -3472, 53988), // Giran
                    new Location(81851, 148238, -3472, 40960), // Giran
                    new Location(18343, 145253, -3096, 7449), // Dion
                    new Location(18284, 145274, -3090, 19740), // Dion
                    new Location(18351, 145186, -3089, 61312), // Dion
                    new Location(18228, 145265, -3079, 21674), // Dion
                    new Location(18317, 145140, -3078, 55285), // Dion
                    new Location(-14584, 122694, -3122, 65082), // Gludio
                    new Location(-14610, 122756, -3143, 13029), // Gludio
                    new Location(-14628, 122627, -3122, 50632), // Gludio
                    new Location(-14697, 122607, -3143, 48408), // Gludio
                    new Location(-14686, 122787, -3122, 12416), // Gludio
                    new Location(-81745, 150275, -3134, 32768), // Gludin
                    new Location(-81520, 150275, -3134, 0), // Gludin
                    new Location(-81628, 150379, -3134, 16025), // Gludin
                    new Location(-81696, 150347, -3155, 22854), // Gludin
                    new Location(-81559, 150332, -3134, 3356)); // Gludin

            EM_SPAWNS = SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS);
            SpawnNPCs(CHESTS_ID, CHESTS, _ch_spawns, 300);
        }

        private void unSpawnEventManagers() {
            deSpawnNPCs(EM_SPAWNS);
            deSpawnNPCs(_ch_spawns);
        }

    @Override
    public void onDeath(final Creature cha, final Creature killer) {
        if (killer instanceof Playable) {
            Playable playable = (Playable) killer;
            if (active && simpleCheckDrop(cha, playable) && Rnd.chance(Config.EVENT_TRICK_OF_TRANS_CHANCE * playable.getPlayer().getRateItems() * Config.RATE_DROP_ITEMS * ((NpcInstance) cha).getTemplate().rateHp))
                ((NpcInstance) cha).dropItem(playable.getPlayer(), A_CHEST_KEY, 1);
        }
    }

    public void accept() {
        if (!player.isQuestContinuationPossible(true))
            return;

        if (!player.findRecipe(RED_PSTC_R))
            addItem(player, RED_PSTC, 1);
        if (!player.findRecipe(BLACK_PSTC_R))
            addItem(player, BLACK_PSTC, 1);
        if (!player.findRecipe(BLUE_PSTC_R))
            addItem(player, BLUE_PSTC, 1);
        if (!player.findRecipe(GREEN_PSTC_R))
            addItem(player, GREEN_PSTC, 1);
        if (!player.findRecipe(ORANGE_PSTC_R))
            addItem(player, ORANGE_PSTC, 1);
        if (!player.findRecipe(WHITE_PSTC_R))
            addItem(player, WHITE_PSTC, 1);

        show("scripts/events/TrickOfTrans/TrickOfTrans_01.htm", player);
    }

    public void open() {
        if (player.haveItem(A_CHEST_KEY)) {
            removeItem(player, A_CHEST_KEY, 1, "TrickOrTrans");
            addItem(player, PhilosophersStoneOre, Rnd.get(1, PhilosophersStoneOreMax));
            addItem(player, MagicReagents, Rnd.get(1, MagicReagentsMax));
            if (Rnd.chance(80))
                addItem(player, PhilosophersStoneConversionFormula, 1);

            show("scripts/events/TrickOfTrans/TrickOfTrans_02.htm", player);
        } else
            show("scripts/events/TrickOfTrans/TrickOfTrans_03.htm", player);
    }
}