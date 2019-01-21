package l2trunk.gameserver.model.entity.poll;

import l2trunk.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;

public final class Poll {
    private String question;
    private List<PollAnswer> answers;
    private long endTime;
    private int lastId = 1;

    Poll(String question) {
        this.question = question;
        answers = new ArrayList<>();
    }

    Poll(String question, List<PollAnswer> answers, long endTime) {
        this.question = question;
        this.answers = answers;
        this.endTime = endTime;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public List<PollAnswer> getAnswers() {
        return answers;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long time) {
        endTime = time;
        if (PollEngine.INSTANCE.isActive()) {
            endTime = System.currentTimeMillis() + time;
            PollEngine.INSTANCE.startThread();
        }
    }

    public void addVote(Player player, int answerId) {
        PollAnswer newAnswer = getAnswerById(answerId);
        if (newAnswer != null) newAnswer.increaseVotes();
        player.sendMessage("Thank You!");
    }

    private PollAnswer getAnswerById(int answerId) {
        return answers.stream()
                .filter(answer -> answer.getId() == answerId)
                .findFirst().orElse(null);
    }

    public String getPollEndDate() {
        //If poll didnt start yet, endTime returns value of total poll time, not currentTime + totalPollTime
        long pollTime = endTime < (System.currentTimeMillis() - 100 * 60 * 60 * 1000) ? System.currentTimeMillis() + endTime : endTime;

        //Difference between poll ending time and current time
        long timeDifference = pollTime - System.currentTimeMillis();

        //removing miliseconds
        timeDifference /= 1000;

        if (timeDifference < 0)
            return "";

        //Getting time left
        int days = (int) Math.floor(timeDifference / 24. / 60 / 60);
        timeDifference -= days * 24 * 60 * 60;
        int hours = (int) Math.floor(timeDifference / 60. / 60);
        timeDifference -= hours * 60 * 60;
        int minutes = (int) Math.floor(timeDifference / 60.);

        StringBuilder builder = new StringBuilder();
        if (days > 0)
            builder.append(days).append(" day").append(days > 1 ? "s" : "");
        if (hours > 0)
            builder.append(builder.length() == 0 ? "" : ", ").append(hours).append(" hour").append(hours > 1 ? "s" : "");
        if (minutes > 0)
            builder.append(builder.length() == 0 ? "" : ", ").append(minutes).append(" minute").append(minutes > 1 ? "s" : "");

        return builder.toString();
    }

    public void addAnswers(List<PollAnswer> answers) {
        this.answers = answers;
    }

    public void addAnswer(String answerTitle) {
        int id = getNewAnswerId();
        PollAnswer newAnswer = new PollAnswer(id, answerTitle, 0);
        addNewAnswerToAnwers(newAnswer);
    }

    public void deleteAnswer(int id) {
        answers.remove(id);
    }

    int getNewAnswerId() {
        return lastId++;
    }

    private void addNewAnswerToAnwers(PollAnswer answer) {
        answers.add(answer);
    }
}
