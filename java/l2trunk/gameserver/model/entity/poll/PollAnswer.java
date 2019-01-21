package l2trunk.gameserver.model.entity.poll;

public final class PollAnswer {
    private final int id;
    private final String answer;
    private int votes;

    public PollAnswer(String answer) {
        id = PollEngine.INSTANCE.getPoll().getNewAnswerId();
        this.answer = answer;
        votes = 0;
    }

    PollAnswer(int id, String answer, int votes) {
        this.id = id;
        this.answer = answer;
        this.votes = votes;
    }

    public int getId() {
        return id;
    }

    public String getAnswer() {
        return answer;
    }

    public int getVotes() {
        return votes;
    }

    void increaseVotes() {
        votes++;
    }

}
