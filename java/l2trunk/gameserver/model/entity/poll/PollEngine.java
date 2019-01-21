package l2trunk.gameserver.model.entity.poll;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public enum PollEngine {
    INSTANCE;
    private static final Logger _log = LoggerFactory.getLogger(PollEngine.class);

    private Poll poll;
    private boolean active = false;
    private ScheduledFuture<?> _endPollThread = null;

    PollEngine() {
        if (!Config.ENABLE_POLL_SYSTEM)
            return;

        loadPoll();
        startAnnounceThread();
    }

    public void addNewPollQuestion(String question) {
        poll = new Poll(question);
    }

    public Poll getPoll() {
        return poll;
    }

    public Poll getActivePoll() {
        if (poll == null)
            return null;
        if (!isActive())
            return null;
        return poll;
    }

    public void startPoll(boolean announce, boolean firstTime) {
        if (getPoll().getEndTime() < System.currentTimeMillis())
            if (firstTime)
                getPoll().setEndTime(System.currentTimeMillis() + getPoll().getEndTime());
            else
                return;

        active = true;
        if (announce)
            announcePoll(true);
        startThread();
    }

    public void deleteCurrentPoll() {
        poll = null;
        deleteAllPlayerVotes();
        savePoll();
    }

    public void stopPoll(boolean announce) {
        active = false;
        if (announce)
            announcePoll(false);
        deleteAllPlayerVotes();
    }

    public boolean isActive() {
        return active;
    }

    private void announcePoll(boolean active) {
        if (active) {
            Announcements.INSTANCE.announceToAll("New poll has been opened! Use .poll to Vote!");
        } else {
            Announcements.INSTANCE.announceToAll("Voting on the poll is now finished!");

            sortAnswers(getPoll().getAnswers());

            getPoll().getAnswers().forEach(answer ->
                    Announcements.INSTANCE.announceToAll(getAnswerProcentage(answer) + "% players voted on \"" + answer.getAnswer() + "\""));
        }
    }

    void startThread() {
        if (_endPollThread != null) {
            _endPollThread.cancel(false);
            _endPollThread = null;
        }
        _endPollThread = ThreadPoolManager.INSTANCE.schedule(() -> {
            if (getPoll() != null) {
                stopPoll(true);
            }
        }, getPoll().getEndTime() - System.currentTimeMillis());
    }

    private void startAnnounceThread() {
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(() -> {
            if (getActivePoll() != null) {
                Say2 say = new Say2(0, ChatType.ANNOUNCEMENT, "", "You didn't vote on the poll yet! Write .poll to vote!");
                GameObjectsStorage.getAllPlayersStream()
                        .filter(Player::isOnline)
                        .forEach(p -> p.sendPacket(say));
            }
        }, Config.ANNOUNCE_POLL_EVERY_X_MIN * 60000, Config.ANNOUNCE_POLL_EVERY_X_MIN * 60000);

    }

    public int getAnswerProcentage(PollAnswer choosenAnswer) {
        if (choosenAnswer.getVotes() == 0)
            return 0;

        int totalVotes = 0;
        for (PollAnswer singleAnswer : getPoll().getAnswers())
            totalVotes += singleAnswer.getVotes();
        return (int) (((double) choosenAnswer.getVotes() / totalVotes) * 100);
    }

    public void sortAnswers(List<PollAnswer> answers) {
        answers.sort((o1, o2) -> Integer.compare(o2.getVotes(), o1.getVotes()));
    }

    private void loadPoll() {
        String question = null;
        List<PollAnswer> answers = new ArrayList<>();
        long endTime = 0;

        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("SELECT * FROM poll")) {
                statement.execute();
                ResultSet rset = statement.getResultSet();

                while (rset.next()) {
                    question = rset.getString("question");
                    endTime = rset.getLong("end_time") * 1000;

                    int answerId = rset.getInt("answer_id");
                    String answerText = rset.getString("answer_text");
                    int answerVotes = rset.getInt("answer_votes");

                    PollAnswer answer = new PollAnswer(answerId, answerText, answerVotes);
                    answers.add(answer);
                }

                if (question != null) {
                    poll = new Poll(question, answers, endTime);
                    startPoll(true, false);
                }

                rset.close();
            }
        } catch (Exception e) {
            _log.error("error in loadPoll:", e);
        }
    }

    private void savePoll() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            try (PreparedStatement statement = con.prepareStatement("DELETE FROM poll")) {
                //Deleting everything from poll
                statement.execute();
            }

            if (getPoll() == null)
                return;
            //inserting data
            try (PreparedStatement statement = con.prepareStatement("INSERT INTO poll VALUES (?,?,?,?,?)")) {
                for (PollAnswer answer : getPoll().getAnswers()) {
                    statement.setString(1, getPoll().getQuestion());
                    statement.setInt(2, answer.getId());
                    statement.setString(3, answer.getAnswer());
                    statement.setInt(4, answer.getVotes());
                    statement.setLong(5, getPoll().getEndTime() / 1000);
                    statement.execute();
                }
            }
        } catch (Exception e) {
            _log.error("could not save Poll:", e);
        }
    }

    private void deleteAllPlayerVotes() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("UPDATE hwid SET poll_answer=-1")) {
            statement.execute();
        } catch (SQLException e) {
            _log.error("could not deleteAllPlayerVotes:", e);
        }
    }
}
