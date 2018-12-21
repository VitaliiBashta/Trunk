package l2trunk.gameserver;

import l2trunk.commons.lang.FileUtils;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public enum Announcements {
    INSTANCE;
    private static final Logger _log = LoggerFactory.getLogger(Announcements.class);
    private final List<Announce> announcements = new ArrayList<>();
    private int lastAnnounceId = 5000000;

    Announcements() {
        loadAnnouncements();
    }

    private static String getQuestionMark(int announceId) {
        return "\b\tType=1 \tID=" + announceId + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b";
    }


    public static void shout(Creature activeChar, String text, ChatType type) {
        Say2 cs = new Say2(activeChar.getObjectId(), type, activeChar.getName(), text);

        int rx = MapUtils.regionX(activeChar);
        int ry = MapUtils.regionY(activeChar);
        int offset = Config.SHOUT_OFFSET;

        for (Player player : GameObjectsStorage.getAllPlayers()) {
            if (player == activeChar || activeChar.getReflection() != player.getReflection())
                continue;

            int tx = MapUtils.regionX(player);
            int ty = MapUtils.regionY(player);

            if (tx >= rx - offset && tx <= rx + offset && ty >= ry - offset && ty <= ry + offset || activeChar.isInRangeZ(player, Config.CHAT_RANGE))
                player.sendPacket(cs);
        }

        activeChar.sendPacket(cs);
    }

    public List<Announce> getAnnouncements() {
        return announcements;
    }

    public void loadAnnouncements() {
        announcements.clear();

        String[] lines = FileUtils.readFileToString(Config.CONFIG.resolve("announcements.txt")).split("\n");
        for (String line : lines) {
            StringTokenizer token = new StringTokenizer(line, "\t");
            if (token.countTokens() > 1)
                addAnnouncement(Integer.parseInt(token.nextToken()), token.nextToken(), false);
            else
                addAnnouncement(0, line, false);
        }
    }

    public void showAnnouncements(Player activeChar) {
        announcements.forEach(a -> a.showAnnounce(activeChar));
    }

    public void addAnnouncement(int val, String text, boolean save) {
        Announce announce = new Announce(val, text);
        announce.start();

        announcements.add(announce);
        if (save)
            saveToDisk();
    }

    public void delAnnouncement(int line) {
        Announce announce = announcements.remove(line);
        if (announce != null)
            announce.stop();

        saveToDisk();
    }

    private void saveToDisk() {
        Path f = Config.CONFIG.resolve("announcements.txt");
        List<String> lines = announcements.stream().map(a -> a.time + "\t" + a.getAnnounce() + "\n").collect(Collectors.toList());
        try {
            Files.write(f, lines);
        } catch (IOException e) {
            _log.error("Error while saving config/announcements.txt!", e);
        }
    }

    public void announceToAll(SystemMessage sm) {
        GameObjectsStorage.getAllPlayers().forEach(pl -> pl.sendPacket(sm));
    }

    public void announceToAll(String text) {
        announceToAll(text, ChatType.ANNOUNCEMENT);
    }

    public void announceToAll(String text, ChatType type) {
        Say2 cs = new Say2(0, type, "", text);
        GameObjectsStorage.getAllPlayers().forEach(pl -> pl.sendPacket(cs));
    }

    /**
     * Отправляет анонсом CustomMessage, приминимо к примеру в шатдауне.
     *
     * @param address адрес в {@link l2trunk.gameserver.network.serverpackets.components.CustomMessage}
     */
    public void announceByCustomMessage(String address) {
        GameObjectsStorage.getAllPlayers().forEach(player ->
                announceToPlayerByCustomMessage(player, address));
    }

    public void announceByCustomMessage(String address, String[] replacements, ChatType type) {
        GameObjectsStorage.getAllPlayers().forEach(player ->
                announceToPlayerByCustomMessage(player, address, replacements, type));
    }

    public void announceToPlayerByCustomMessage(Player player, String address) {
        CustomMessage cm = new CustomMessage(address, player);
        player.sendPacket(new Say2(0, ChatType.ANNOUNCEMENT, "", cm.toString()));
    }

    public void announceToPlayerByCustomMessage(Player player, String address, String[] replacements, ChatType type) {
        CustomMessage cm = new CustomMessage(address, player);
        if (replacements != null)
            for (String s : replacements)
                cm.addString(s);
        player.sendPacket(new Say2(0, type, "", cm.toString()));
    }

    public void announceToAll(SystemMessage2 sm) {
        GameObjectsStorage.getAllPlayers().forEach(player -> player.sendPacket(sm));
    }

    public class Announce extends RunnableImpl {
        private final int time;
        private final String announce;
        private final int id;
        private Future<?> _task;

        Announce(int time, String announce) {
            this.time = time;
            this.announce = announce;

            lastAnnounceId++;
            id = lastAnnounceId;
        }

        @Override
        public void runImpl() {
            IStaticPacket csNoQuestion = new Say2(0, ChatType.CRITICAL_ANNOUNCE, "", announce);
            IStaticPacket csQuestion = new Say2(0, ChatType.CRITICAL_ANNOUNCE, "", announce + getQuestionMark(id));
            for (Player player : GameObjectsStorage.getAllPlayers()) {
                if (player.containsQuickVar("DisabledAnnounce" + id))
                    continue;
                int newValue = player.getQuickVarI("Announce: " + id, 0) + 1;
                if (newValue >= 3)
                    player.sendPacket(csQuestion);
                else {
                    player.sendPacket(csNoQuestion);
                    player.addQuickVar("Announce: " + id, newValue);
                }
            }
        }

        void showAnnounce(Player player) {
            IStaticPacket cs = new Say2(0, ChatType.CRITICAL_ANNOUNCE, player.getName(), announce);
            player.sendPacket(cs);
        }

        void start() {
            if (time > 0)
                _task = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(this, time * 1000L, time * 1000L);
        }

        void stop() {
            if (_task != null) {
                _task.cancel(false);
                _task = null;
            }
        }

        public String getAnnounce() {
            return announce;
        }
    }
}