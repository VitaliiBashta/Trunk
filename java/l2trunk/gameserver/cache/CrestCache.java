package l2trunk.gameserver.cache;

import l2trunk.gameserver.database.DatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public final class CrestCache {
    public static final int ALLY_CREST_SIZE = 192;
    public static final int CREST_SIZE = 256;
    public static final int LARGE_CREST_SIZE = 2176;

    private static final Logger _log = LoggerFactory.getLogger(CrestCache.class);

    /**
     * Требуется для получения ID значка по ID клана
     */
    private static final Map<Integer, Integer> _pledgeCrestId = new HashMap<>();
    private static final Map<Integer, Integer> _pledgeCrestLargeId = new HashMap<>();
    private static final Map<Integer, Integer> _allyCrestId = new HashMap<>();
    /**
     * Получение значка по ID
     */
    private static final Map<Integer, byte[]> _pledgeCrest = new HashMap<>();
    private static final Map<Integer, byte[]> _pledgeCrestLarge = new HashMap<>();
    private static final Map<Integer, byte[]> _allyCrest = new HashMap<>();

    public static void init() {
        load();
    }


    /**
     * Генерирует уникальный положительный ID на основе данных: ID клана/альянса и значка
     *
     * @param pledgeId ID клана или альянса
     * @param crest    данные значка
     * @return ID значка в "кэше"
     */
    private static int getCrestId(int pledgeId, byte[] crest) {
        return pledgeId;
    }

    private static void load() {
        int count = 0;
        int pledgeId, crestId;
        byte[] crest;

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {

            try (PreparedStatement statement = con.prepareStatement("SELECT clan_id, crest FROM clan_data WHERE crest IS NOT NULL");
                 ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    count++;

                    pledgeId = rset.getInt("clan_id");
                    crest = rset.getBytes("crest");

                    crestId = getCrestId(pledgeId, crest);

                    _pledgeCrestId.put(pledgeId, crestId);
                    _pledgeCrest.put(crestId, crest);
                }
            }

            try (PreparedStatement statement = con.prepareStatement("SELECT clan_id, largecrest FROM clan_data WHERE largecrest IS NOT NULL");
                 ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    count++;

                    pledgeId = rset.getInt("clan_id");
                    crest = rset.getBytes("largecrest");

                    crestId = getCrestId(pledgeId, crest);

                    _pledgeCrestLargeId.put(pledgeId, crestId);
                    _pledgeCrestLarge.put(crestId, crest);
                }
            }

            try (PreparedStatement statement = con.prepareStatement("SELECT ally_id, crest FROM ally_data WHERE crest IS NOT NULL");
                 ResultSet rset = statement.executeQuery()) {
                while (rset.next()) {
                    count++;
                    pledgeId = rset.getInt("ally_id");
                    crest = rset.getBytes("crest");

                    crestId = getCrestId(pledgeId, crest);

                    _allyCrestId.put(pledgeId, crestId);
                    _allyCrest.put(crestId, crest);
                }
            }
        } catch (SQLException e) {
            _log.error("Error while loading CrestCache! ", e);
        }
        _log.info("CrestCache: Loaded " + count + " crests");
    }

    public static synchronized byte[] getPledgeCrest(int crestId) {
        return _pledgeCrest.get(crestId);
    }

    public static synchronized byte[] getPledgeCrestLarge(int crestId) {
        return _pledgeCrestLarge.get(crestId);
    }

    public static byte[] getAllyCrest(int crestId) {
        return _allyCrest.get(crestId);
    }

    public static synchronized int getPledgeCrestId(int pledgeId) {
        if (_pledgeCrest.containsKey(pledgeId))
            return _pledgeCrestId.get(pledgeId);
        return 0;
    }

    public static synchronized int getPledgeCrestLargeId(int pledgeId) {
        if (_pledgeCrestLargeId.containsKey(pledgeId))
        return _pledgeCrestLargeId.get(pledgeId);
        return 0;
    }

    public static synchronized int getAllyCrestId(int pledgeId) {
        return _allyCrestId.get(pledgeId);
    }

    public static synchronized void removePledgeCrest(int pledgeId) {
        _pledgeCrest.remove(_pledgeCrestId.remove(pledgeId));
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest=? WHERE clan_id=?")) {
            statement.setNull(1, Types.VARBINARY);
            statement.setInt(2, pledgeId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while removing Pledge Crest!", e);
        }
    }

    public static void removePledgeCrestLarge(int pledgeId) {
        _pledgeCrestLarge.remove(_pledgeCrestLargeId.remove(pledgeId));
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET largecrest=? WHERE clan_id=?")) {
            statement.setNull(1, Types.VARBINARY);
            statement.setInt(2, pledgeId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while removing Pledge Crest Large! ", e);
        }
    }

    public static synchronized void removeAllyCrest(int pledgeId) {
        _allyCrest.remove(_allyCrestId.remove(pledgeId));
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE ally_data SET crest=? WHERE ally_id=?")) {
            statement.setNull(1, Types.VARBINARY);
            statement.setInt(2, pledgeId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while Removing Ally Crest ", e);
        }
    }

    public static synchronized int savePledgeCrest(int pledgeId, byte[] crest) {
        int crestId = getCrestId(pledgeId, crest);
        _pledgeCrestId.put(pledgeId, crestId);
        _pledgeCrest.put(crestId, crest);
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET crest=? WHERE clan_id=?")) {
            statement.setBytes(1, crest);
            statement.setInt(2, pledgeId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while saving Pledge Crest! ", e);
        }

        return crestId;
    }

    public static synchronized int savePledgeCrestLarge(int pledgeId, byte[] crest) {
        int crestId = getCrestId(pledgeId, crest);
        _pledgeCrestLargeId.put(pledgeId, crestId);
        _pledgeCrestLarge.put(crestId, crest);

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE clan_data SET largecrest=? WHERE clan_id=?")) {
            statement.setBytes(1, crest);
            statement.setInt(2, pledgeId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while saving Pledge Crest Large! ", e);
        }

        return crestId;
    }

    public static synchronized int saveAllyCrest(int pledgeId, byte[] crest) {
        int crestId = getCrestId(pledgeId, crest);
        _allyCrestId.put(pledgeId, crestId);
        _allyCrest.put(crestId, crest);
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE ally_data SET crest=? WHERE ally_id=?")) {
            statement.setBytes(1, crest);
            statement.setInt(2, pledgeId);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while saving Ally Crest! ", e);
        }
        return crestId;
    }
}
