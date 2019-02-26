package l2trunk.gameserver.templates.manor;

public final class CropProcure {
    private final int rewardType;
    public final int cropId;
    private final long buy;
    public final long price;
    private long buyResidual;

    public CropProcure(int id) {
        cropId = id;
        buyResidual = 0;
        rewardType = 0;
        buy = 0;
        price = 0;
    }

    public CropProcure(int id, long amount, int type, long buy, long price) {
        cropId = id;
        buyResidual = amount;
        rewardType = type;
        this.buy = buy;
        this.price = price < 0 ? 0 : price;
    }

    public int getReward() {
        return rewardType;
    }

    public long getAmount() {
        return buyResidual;
    }

    public void setAmount(long amount) {
        buyResidual = amount;
    }

    public long getStartAmount() {
        return buy;
    }

    public long price() {
        return price;
    }
}
