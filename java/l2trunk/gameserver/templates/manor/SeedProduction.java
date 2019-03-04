package l2trunk.gameserver.templates.manor;

public final class SeedProduction {
    private final int seedId;
    private final long price;
    private long residual;
    private long sales;

    public SeedProduction(int id) {
        seedId = id;
        sales = 0;
        price = 0;
        sales = 0;
    }

    public SeedProduction(int id, long amount, long price, long sales) {
        seedId = id;
        residual = amount;
        this.price = price;
        this.sales = sales;
    }

    public int getId() {
        return seedId;
    }

    public long getCanProduce() {
        return residual;
    }

    public void setCanProduce(long amount) {
        residual = amount;
    }

    public long getPrice() {
        return price;
    }

    public long getStartProduce() {
        return sales;
    }
}
