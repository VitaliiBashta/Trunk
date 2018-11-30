package l2trunk.gameserver.model.entity.poll;

public class PollAnswer {
    private final int _id;
    private final String _answer;
    private int _votes;

    public PollAnswer(String answer) {
        _id = PollEngine.INSTANCE.getPoll().getNewAnswerId();
        _answer = answer;
        _votes = 0;
    }

    PollAnswer(int id, String answer, int votes) {
        _id = id;
        _answer = answer;
        _votes = votes;
    }

    public int getId() {
        return _id;
    }

    public String getAnswer() {
        return _answer;
    }

    public int getVotes() {
        return _votes;
    }

    public void setVotes(int votes) {
        _votes = votes;
    }

    public void increaseVotes() {
        _votes++;
    }

    public void decreaseVotes() {
        if (_votes > 0)
            _votes--;

    }
}
