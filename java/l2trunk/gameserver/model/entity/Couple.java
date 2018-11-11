package l2trunk.gameserver.model.entity;

import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.instancemanager.CoupleManager;
import l2trunk.gameserver.model.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public final class Couple {
    private static final Logger _log = LoggerFactory.getLogger(Couple.class);

    private final int id;
    private int player1Id = 0;
    private int player2Id = 0;
    private boolean maried = false;
    private long affiancedDate;
    private long weddingDate;
    private boolean isChanged;

    public Couple(int coupleId) {
        id = coupleId;
    }

    public Couple(Player player1, Player player2) {
        id = IdFactory.getInstance().getNextId();
        player1Id = player1.getObjectId();
        player2Id = player2.getObjectId();
        long time = System.currentTimeMillis();
        affiancedDate = time;
        weddingDate = time;
        player1.setCoupleId(id);
        player1.setPartnerId(player2Id);
        player2.setCoupleId(id);
        player2.setPartnerId(player1Id);
    }

    public void marry() {
        weddingDate = System.currentTimeMillis();
        maried = true;
        setChanged(true);
    }

    public void divorce() {
        CoupleManager.getInstance().getCouples().remove(this);
        CoupleManager.getInstance().getDeletedCouples().add(this);
    }

    public void store(Connection con) {
        try (PreparedStatement statement = con.prepareStatement("REPLACE INTO couples (id, player1Id, player2Id, maried, affiancedDate, weddingDate) VALUES (?, ?, ?, ?, ?, ?)")) {
            statement.setInt(1, id);
            statement.setInt(2, player1Id);
            statement.setInt(3, player2Id);
            statement.setBoolean(4, maried);
            statement.setLong(5, affiancedDate);
            statement.setLong(6, weddingDate);
            statement.execute();
        } catch (SQLException e) {
            _log.error("Error while Storing Couple! ", e);
        }
    }

    public final int getId() {
        return id;
    }

    public final int getPlayer1Id() {
        return player1Id;
    }

    public void setPlayer1Id(int _player1Id) {
        this.player1Id = _player1Id;
    }

    public final int getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(int _player2Id) {
        this.player2Id = _player2Id;
    }

    public final boolean getMaried() {
        return maried;
    }

    public void setMaried(boolean _maried) {
        this.maried = _maried;
    }

    public final long getAffiancedDate() {
        return affiancedDate;
    }

    public void setAffiancedDate(long _affiancedDate) {
        this.affiancedDate = _affiancedDate;
    }

    public final long getWeddingDate() {
        return weddingDate;
    }

    public void setWeddingDate(long _weddingDate) {
        this.weddingDate = _weddingDate;
    }

    /**
     * Требует ли изминений свадьба в базе даных
     *
     * @return true если требует
     */
    public boolean isChanged() {
        return isChanged;
    }

    /**
     * Устанавливает состояние изменения свадьбы
     *
     * @param val изменена или нет
     */
    public void setChanged(boolean val) {
        isChanged = val;
    }
}
