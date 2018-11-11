package l2trunk.gameserver.templates.manor;

public class SeedProduction
{
	private final int _seedId;
	private long _residual;
	private final long _price;
	private long _sales;

	public SeedProduction(int id)
	{
		_seedId = id;
		_sales = 0;
		_price = 0;
		_sales = 0;
	}

	public SeedProduction(int id, long amount, long price, long sales)
	{
		_seedId = id;
		_residual = amount;
		_price = price;
		_sales = sales;
	}

	public int getId()
	{
		return _seedId;
	}

	public long getCanProduce()
	{
		return _residual;
	}

	public void setCanProduce(long amount)
	{
		_residual = amount;
	}

	public long getPrice()
	{
		return _price;
	}

	public long getStartProduce()
	{
		return _sales;
	}
}
