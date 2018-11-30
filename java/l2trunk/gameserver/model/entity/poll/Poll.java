package l2trunk.gameserver.model.entity.poll;

import l2trunk.gameserver.model.Player;

import java.util.List;

public class Poll {
    private String _question;
    private PollAnswer[] _answers;
    private long _endTime;
    private int lastId = 1;

    Poll(String question) {
        _question = question;
        _answers = new PollAnswer[0];
    }

    Poll(String question, List<PollAnswer> answers, long endTime) {
        _question = question;
        _answers = convertAnswers(answers);
        _endTime = endTime;
    }

    private static PollAnswer[] convertAnswers(List<PollAnswer> answers) {
        PollAnswer[] convertedAnswers = new PollAnswer[answers.size()];
        for (int i = 0; i < answers.size(); i++)
            convertedAnswers[i] = answers.get(i);
        return convertedAnswers;
    }

    public String getQuestion() {
        return _question;
    }

    public void setQuestion(String question) {
        _question = question;
    }

    public PollAnswer[] getAnswers() {
        return _answers;
    }

    public long getEndTime() {
        return _endTime;
    }

    /**
     * @param time in MILISECONDS
     */
    public void setEndTime(long time) {
        _endTime = time;
        if (PollEngine.INSTANCE.isActive()) {
            _endTime = System.currentTimeMillis() + time;
            PollEngine.INSTANCE.startThread();
        }
    }

    public void addVote(Player player, int answerId) {
        PollAnswer newAnswer = getAnswerById(answerId);
        if (newAnswer != null) {
            newAnswer.increaseVotes();
        }
        player.sendMessage("Thank You!");
    }

    private PollAnswer getAnswerById(int answerId) {
        for (PollAnswer answer : getAnswers())
            if (answer.getId() == answerId) {
                return answer;
            }
        return null;
    }

    public String getPollEndDate() {
        //If poll didnt start yet, _endTime returns value of total poll time, not currentTime + totalPollTime
        long pollTime = _endTime < (System.currentTimeMillis() - 100 * 60 * 60 * 1000) ? System.currentTimeMillis() + _endTime : _endTime;

        //Difference between poll ending time and current time
        long timeDifference = pollTime - System.currentTimeMillis();

        //removing miliseconds
        timeDifference /= 1000;

        if (timeDifference < 0)
            return "";

        //Getting time left
        int days = (int) Math.floor(timeDifference / 24 / 60 / 60);
        timeDifference -= days * 24 * 60 * 60;
        int hours = (int) Math.floor(timeDifference / 60 / 60);
        timeDifference -= hours * 60 * 60;
        int minutes = (int) Math.floor(timeDifference / 60);

        StringBuilder builder = new StringBuilder();
        if (days > 0)
            builder.append(days).append(" day").append(days > 1 ? "s" : "");
        if (hours > 0)
            builder.append(builder.length() == 0 ? "" : ", ").append(hours).append(" hour").append(hours > 1 ? "s" : "");
        if (minutes > 0)
            builder.append(builder.length() == 0 ? "" : ", ").append(minutes).append(" minute").append(minutes > 1 ? "s" : "");

        return builder.toString();
    }

    private void addAnswers(PollAnswer[] answers) {
        _answers = answers;
    }

    public void addAnswers(List<PollAnswer> answers) {
        addAnswers(convertAnswers(answers));
        ;
    }

    public void addAnswer(String answerTitle) {
        int id = getNewAnswerId();
        PollAnswer newAnswer = new PollAnswer(id, answerTitle, 0);
        addNewAnswerToAnwers(newAnswer);
    }

    public void deleteAnswer(int id) {
        PollAnswer[] leftAnswers = new PollAnswer[_answers.length - 1];
        int count = 0;
        for (PollAnswer _answer : _answers) {
            if (_answer.getId() != id) {
                leftAnswers[count] = _answer;
                count++;
            }
        }
        _answers = leftAnswers;
    }

    int getNewAnswerId() {
        return lastId++;
    }

    private void addNewAnswerToAnwers(PollAnswer answer) {
        PollAnswer[] newAnswers = new PollAnswer[_answers.length + 1];
        for (int i = 0; i < _answers.length; i++)
            newAnswers[i] = _answers[i];
        newAnswers[newAnswers.length - 1] = answer;
        _answers = newAnswers;
    }
}
