package l2trunk.gameserver.listener.reflection;

import l2trunk.commons.listener.Listener;
import l2trunk.gameserver.model.entity.Reflection;

public interface OnReflectionCollapseListener extends Listener<Reflection>
{
	void onReflectionCollapse(Reflection reflection);
}
