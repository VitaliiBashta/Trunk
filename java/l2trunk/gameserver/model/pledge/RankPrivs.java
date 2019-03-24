package l2trunk.gameserver.model.pledge;

public final class RankPrivs {
    private final int rank;
    private int party;
    private int privs;

    public RankPrivs(int rank, int party, int privs) {
        this.rank = rank;
        this.party = party;
        this.privs = privs;
    }

    public int getRank() {
        return rank;
    }

    public int getParty() {
        return party;
    }

    public void setParty(int party) {
        this.party = party;
    }

    public int getPrivs() {
        return privs;
    }

    public void setPrivs(int privs) {
        this.privs = privs;
    }
}
