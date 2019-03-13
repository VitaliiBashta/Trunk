package l2trunk.gameserver.dao;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.entity.residence.Castle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public enum CastleDAO {
    INSTANCE;
    private static final String UPDATE_SQL_QUERY = "UPDATE castle SET tax_percent=?, treasury=?, reward_count=?, siege_date=?, last_siege_date=?, own_date=? WHERE id=?";
    private static final Logger _log = LoggerFactory.getLogger(CastleDAO.class);

    public void update(Castle castle) {
        if (!castle.getJdbcState().isUpdatable())
            return;

        castle.setJdbcState(JdbcEntityState.STORED);
        update0(castle);
    }

    private void update0(Castle castle) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_SQL_QUERY)) {
            statement.setInt(1, castle.getTaxPercent0());
            statement.setLong(2, castle.getTreasury());
            statement.setInt(3, castle.getRewardCount());
            statement.setLong(4, castle.getSiegeDate().getTimeInMillis());
            statement.setLong(5, castle.getLastSiegeDate().getTimeInMillis());
            statement.setLong(6, castle.getOwnDate().getTimeInMillis());
            statement.setInt(7, castle.getId());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("CastleDAO#update0(Castle): ", e);
        }
    }
}
