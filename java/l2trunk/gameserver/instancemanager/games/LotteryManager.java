package l2trunk.gameserver.instancemanager.games;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

public enum LotteryManager {
    INSTANCE;
    public static final long SECOND = 1000;
    private static final long MINUTE = 60000;
    private static final Logger _log = LoggerFactory.getLogger(LotteryManager.class);
    private static final String INSERT_LOTTERY = "INSERT INTO games(id, idnr, enddate, prize, newprize) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_PRICE = "UPDATE games SET prize=?, newprize=? WHERE id = 1 AND idnr = ?";
    private static final String UPDATE_LOTTERY = "UPDATE games SET finished=1, prize=?, newprize=?, number1=?, number2=?, prize1=?, prize2=?, prize3=? WHERE id=1 AND idnr=?";
    private static final String SELECT_LAST_LOTTERY = "SELECT idnr, prize, newprize, enddate, finished FROM games WHERE id = 1 ORDER BY idnr DESC LIMIT 1";
    private static final String SELECT_LOTTERY_ITEM = "SELECT enchant_level, custom_type2 FROM items WHERE item_id = 4442 AND custom_type1 = ?";
    private static final String SELECT_LOTTERY_TICKET = "SELECT number1, number2, prize1, prize2, prize3 FROM games WHERE id = 1 AND idnr = ?";
    private int _number;
    private int _prize;
    private boolean _isSellingTickets;
    private boolean _isStarted;
    private long _enddate;

    public void init() {
        _number = 1;
        _prize = Config.SERVICES_LOTTERY_PRIZE;
        _isSellingTickets = false;
        _isStarted = false;
        _enddate = System.currentTimeMillis();

        if (Config.SERVICES_ALLOW_LOTTERY)
            new startLottery().run();
    }


    public void increasePrize(int count) {
        _prize += count;
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(UPDATE_PRICE)) {
            statement.setInt(1, getPrize());
            statement.setInt(2, getPrize());
            statement.setInt(3, getId());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("Lottery: Could not increase current lottery prize: ", e);
        }
    }

    private boolean restoreLotteryData() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_LAST_LOTTERY);
             ResultSet rset = statement.executeQuery()) {

            if (rset.next()) {
                _number = rset.getInt("idnr");

                if (rset.getInt("finished") == 1) {
                    _number++;
                    _prize = rset.getInt("newprize");
                } else {
                    _prize = rset.getInt("prize");
                    _enddate = rset.getLong("enddate");

                    if (_enddate <= System.currentTimeMillis() + 2 * MINUTE) {
                        new finishLottery().run();
                        return false;
                    }

                    if (_enddate > System.currentTimeMillis()) {

                        _isStarted = true;
                        ThreadPoolManager.INSTANCE.schedule(new finishLottery(), _enddate - System.currentTimeMillis());

                        if (_enddate > System.currentTimeMillis() + 12 * MINUTE) {
                            _isSellingTickets = true;
                            ThreadPoolManager.INSTANCE.schedule(new stopSellingTickets(), _enddate - System.currentTimeMillis() - 10 * MINUTE);
                        }

                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            _log.warn("Lottery: Could not restore lottery data: ", e);
        }
        return true;
    }

    private void announceLottery() {
        if (Config.SERVICES_ALLOW_LOTTERY)
            _log.info("Lottery: Starting ticket sell for lottery #" + getId() + ".");
        _isSellingTickets = true;
        _isStarted = true;

        Announcements.INSTANCE.announceToAll("Lottery tickets are now available for Lucky Lottery #" + getId() + ".");
    }

    private void scheduleEndOfLottery() {
        //Connection con = null;
        //PreparedStatement statement;
        /** Calendar finishtime = Calendar.INSTANCE();
         finishtime.setTimeInMillis(_enddate);
         finishtime.set(Calendar.MINUTE, 0);
         finishtime.set(Calendar.SECOND, 0);
         finishtime.add(Calendar.DAY_OF_MONTH, 7);
         finishtime.set(Calendar.DAY_OF_WEEK, 6);
         finishtime.set(Calendar.HOUR_OF_DAY, 7);
         _enddate = finishtime.getTimeInMillis();

         ThreadPoolManager.INSTANCE().scheduleGeneral(new stopSellingTickets(), _enddate - System.currentTimeMillis() - 10 * MINUTE);
         ThreadPoolManager.INSTANCE().scheduleGeneral(new finishLottery(), _enddate - System.currentTimeMillis());
         **/

        Calendar finishtime = Calendar.getInstance();
        finishtime.setTimeInMillis(_enddate);
        finishtime.set(Calendar.MINUTE, 0);
        finishtime.set(Calendar.SECOND, 0);

        if (finishtime.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            finishtime.set(Calendar.HOUR_OF_DAY, 19);
            _enddate = finishtime.getTimeInMillis();
            _enddate += 604800000;
        } else {
            finishtime.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            finishtime.set(Calendar.HOUR_OF_DAY, 19);
            _enddate = finishtime.getTimeInMillis();
        }

        ThreadPoolManager.INSTANCE.schedule(new stopSellingTickets(), _enddate - System.currentTimeMillis() - 10 * MINUTE);
        ThreadPoolManager.INSTANCE.schedule(new finishLottery(), _enddate - System.currentTimeMillis());
    }

    private void createNewLottery() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_LOTTERY)) {
            statement.setInt(1, 1);
            statement.setInt(2, getId());
            statement.setLong(3, getEndDate());
            statement.setInt(4, getPrize());
            statement.setInt(5, getPrize());
            statement.execute();
        } catch (SQLException e) {
            _log.warn("Lottery: Could not store new lottery data: ", e);
        }
    }

    public int[] decodeNumbers(int enchant, int type2) {
        int res[] = new int[5];
        int id = 0;
        int nr = 1;

        while (enchant > 0) {
            int val = enchant / 2;
            if (val != (double) enchant / 2)
                res[id++] = nr;
            enchant /= 2;
            nr++;
        }

        nr = 17;

        while (type2 > 0) {
            int val = type2 / 2;
            if (val != (double) type2 / 2)
                res[id++] = nr;
            type2 /= 2;
            nr++;
        }

        return res;
    }

    private int[] checkTicket(int id, int enchant, int type2) {
        int res[] = {0, 0};
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(SELECT_LOTTERY_TICKET)) {
            statement.setInt(1, id);
            ResultSet rset = statement.executeQuery();

            if (rset.next()) {
                int curenchant = rset.getInt("number1") & enchant;
                int curtype2 = rset.getInt("number2") & type2;

                if (curenchant == 0 && curtype2 == 0)
                    return res;

                int count = 0;

                for (int i = 1; i <= 16; i++) {
                    int val = curenchant / 2;
                    if (val != (double) curenchant / 2)
                        count++;
                    int val2 = curtype2 / 2;
                    if (val2 != (double) curtype2 / 2)
                        count++;
                    curenchant = val;
                    curtype2 = val2;
                }

                switch (count) {
                    case 0:
                        break;
                    case 5:
                        res[0] = 1;
                        res[1] = rset.getInt("prize1");
                        break;
                    case 4:
                        res[0] = 2;
                        res[1] = rset.getInt("prize2");
                        break;
                    case 3:
                        res[0] = 3;
                        res[1] = rset.getInt("prize3");
                        break;
                    default:
                        res[0] = 4;
                        res[1] = 200;
                }
            }
        } catch (SQLException e) {
            _log.warn("Lottery: Could not check lottery ticket #" + id + ": ", e);
        }

        return res;
    }

    public int[] checkTicket(ItemInstance item) {
        return checkTicket(item.getCustomType1(), item.getEnchantLevel(), item.getCustomType2());
    }

    public boolean isSellableTickets() {
        return _isSellingTickets;
    }

    public boolean isStarted() {
        return _isStarted;
    }

    public int getId() {
        return _number;
    }

    public int getPrize() {
        return _prize;
    }

    public long getEndDate() {
        return _enddate;
    }

    private class startLottery extends RunnableImpl {
        startLottery() {
            // Do nothing
        }

        @Override
        public void runImpl() {
            if (restoreLotteryData()) {
                announceLottery();
                scheduleEndOfLottery();
                createNewLottery();
            }
        }
    }

    private class stopSellingTickets extends RunnableImpl {
        stopSellingTickets() {
            // Do nothing
        }

        @Override
        public void runImpl() {
            if (Config.SERVICES_ALLOW_LOTTERY)
                LOG.info("Lottery: Stopping ticket sell for lottery #" + getId() + ".");
            _isSellingTickets = false;

            Announcements.INSTANCE.announceToAll(new SystemMessage2(SystemMsg.LOTTERY_TICKET_SALES_HAVE_BEEN_TEMPORARILY_SUSPENDED));
        }
    }

    private class finishLottery extends RunnableImpl {
        finishLottery() {
            // Do nothing
        }

        @Override
        public void runImpl() {
            if (Config.SERVICES_ALLOW_LOTTERY)
                LOG.info("Lottery: Ending lottery #" + getId() + ".");

            int[] luckynums = new int[5];
            int luckynum = 0;

            for (int i = 0; i < 5; i++) {
                boolean found = true;

                while (found) {
                    luckynum = Rnd.get(20) + 1;
                    found = false;

                    for (int j = 0; j < i; j++)
                        if (luckynums[j] == luckynum)
                            found = true;
                }

                luckynums[i] = luckynum;
            }

            if (Config.SERVICES_ALLOW_LOTTERY)
                LOG.info("Lottery: The lucky numbers are " + luckynums[0] + ", " + luckynums[1] + ", " + luckynums[2] + ", " + luckynums[3] + ", " + luckynums[4] + ".");

            int enchant = 0;
            int type2 = 0;

            for (int i = 0; i < 5; i++)
                if (luckynums[i] < 17)
                    enchant += Math.pow(2, luckynums[i] - 1);
                else
                    type2 += Math.pow(2, luckynums[i] - 17);

            if (Config.SERVICES_ALLOW_LOTTERY)
                LOG.info("Lottery: Encoded lucky numbers are " + enchant + ", " + type2);

            int count1 = 0;
            int count2 = 0;
            int count3 = 0;
            int count4 = 0;

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(SELECT_LOTTERY_ITEM)) {
                statement.setInt(1, getId());
                ResultSet rset = statement.executeQuery();

                while (rset.next()) {
                    int curenchant = rset.getInt("enchant_level") & enchant;
                    int curtype2 = rset.getInt("custom_type2") & type2;

                    if (curenchant == 0 && curtype2 == 0)
                        continue;

                    int count = 0;

                    for (int i = 1; i <= 16; i++) {
                        int val = curenchant / 2;

                        if (val != (double) curenchant / 2)
                            count++;

                        int val2 = curtype2 / 2;

                        if (val2 != (double) curtype2 / 2)
                            count++;

                        curenchant = val;
                        curtype2 = val2;
                    }

                    if (count == 5)
                        count1++;
                    else if (count == 4)
                        count2++;
                    else if (count == 3)
                        count3++;
                    else if (count > 0)
                        count4++;
                }
            } catch (SQLException e) {
                LOG.warn("Lottery: Could restore lottery data: ", e);
            }

            int prize4 = count4 * Config.SERVICES_LOTTERY_2_AND_1_NUMBER_PRIZE;
            int prize1 = 0;
            int prize2 = 0;
            int prize3 = 0;
            int newprize;

            if (count1 > 0)
                prize1 = (int) ((getPrize() - prize4) * Config.SERVICES_LOTTERY_5_NUMBER_RATE / count1);

            if (count2 > 0)
                prize2 = (int) ((getPrize() - prize4) * Config.SERVICES_LOTTERY_4_NUMBER_RATE / count2);

            if (count3 > 0)
                prize3 = (int) ((getPrize() - prize4) * Config.SERVICES_LOTTERY_3_NUMBER_RATE / count3);

            //TODO: Уточнить что происходит с джекпотом на оффе. Если с проигрышем всех участников джекпот уменьшается то до каких приделов.
            if (prize1 == 0 && prize2 == 0 && prize3 == 0)
                newprize = getPrize();
            else
                newprize = getPrize() + prize1 + prize2 + prize3;

            if (Config.SERVICES_ALLOW_LOTTERY)
                LOG.info("Lottery: Jackpot for next lottery is " + newprize + ".");

            SystemMessage2 sm;
            if (count1 > 0) {
                // There are winners.
                sm = new SystemMessage2(SystemMsg.THE_PRIZE_AMOUNT_FOR_THE_WINNER_OF_LOTTERY__S1__IS_S2_ADENA_WE_HAVE_S3_FIRST_PRIZE_WINNERS);
                sm.addInteger(getId());
                sm.addInteger(getPrize());
                sm.addInteger(count1);
                Announcements.INSTANCE.announceToAll(sm);
            } else {
                // There are no winners.
                sm = new SystemMessage2(SystemMsg.THE_PRIZE_AMOUNT_FOR_LUCKY_LOTTERY__S1__IS_S2_ADENA_THERE_WAS_NO_FIRST_PRIZE_WINNER_IN_THIS_DRAWING_THEREFORE_THE_JACKPOT_WILL_BE_ADDED_TO_THE_NEXT_DRAWING);
                sm.addInteger(getId());
                sm.addInteger(getPrize());
                Announcements.INSTANCE.announceToAll(sm);
            }

            try (Connection con = DatabaseFactory.getInstance().getConnection();
                 PreparedStatement statement = con.prepareStatement(UPDATE_LOTTERY)) {
                statement.setInt(1, getPrize());
                statement.setInt(2, newprize);
                statement.setInt(3, enchant);
                statement.setInt(4, type2);
                statement.setInt(5, prize1);
                statement.setInt(6, prize2);
                statement.setInt(7, prize3);
                statement.setInt(8, getId());
                statement.execute();
            } catch (SQLException e) {
                LOG.warn("Lottery: Could not store finished lottery data: ", e);
            }

            ThreadPoolManager.INSTANCE.schedule(new startLottery(), MINUTE);
            _number++;

            _isStarted = false;
        }
    }
}