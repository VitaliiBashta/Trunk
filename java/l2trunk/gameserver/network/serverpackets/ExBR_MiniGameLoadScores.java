package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.instancemanager.games.MiniGameScoreManager;
import l2trunk.gameserver.model.Player;

import java.util.*;


public final class ExBR_MiniGameLoadScores extends L2GameServerPacket {
    private final Map<Integer, List<Map.Entry<String, Integer>>> _entries = new TreeMap<>();
    private int _place;
    private int _score;
    private int _lastScore;

    public ExBR_MiniGameLoadScores(Player player) {
        int lastBig = 0;
        int i = 1;

        for (Map.Entry<Integer, Set<String>> entry : MiniGameScoreManager.INSTANCE.getScores().entrySet()) {
            for (String name : entry.getValue()) {
                List<Map.Entry<String, Integer>> set = _entries.computeIfAbsent(i, k -> new ArrayList<>());

                if (name.equalsIgnoreCase(player.getName()))
                    if (entry.getKey() > lastBig) {
                        _place = i;
                        _score = (lastBig = entry.getKey());
                    }

                set.add(new AbstractMap.SimpleImmutableEntry<>(name, entry.getKey()));

                i++;

                _lastScore = entry.getKey();

                if (i > 100)
                    break;
            }
        }
    }

    @Override
    protected void writeImpl() {
        writeEx(0xDD);
        writeD(_place); // place of last big score of player
        writeD(_score); // last big score of player
        writeD(0x00); //?
        writeD(_lastScore); //last score of list
        for (Map.Entry<Integer, List<Map.Entry<String, Integer>>> entry : _entries.entrySet())
            for (Map.Entry<String, Integer> scoreEntry : entry.getValue()) {
                writeD(entry.getKey());
                writeS(scoreEntry.getKey());
                writeD(scoreEntry.getValue());
            }
    }
}