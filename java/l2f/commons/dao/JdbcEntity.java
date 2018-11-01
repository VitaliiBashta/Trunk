package l2f.commons.dao;

import java.io.Serializable;

public interface JdbcEntity extends Serializable
{
	public JdbcEntityState getJdbcState();

	public void setJdbcState(JdbcEntityState state);

	public void save();
	
	public void update();
	
	public void delete();
}
