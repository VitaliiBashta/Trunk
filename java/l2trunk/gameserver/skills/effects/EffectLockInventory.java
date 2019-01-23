package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.LockType;
import l2trunk.gameserver.stats.Env;

import java.util.List;

public final class EffectLockInventory extends Effect {
    private final LockType lockType;
    private final List<Integer> lockItems;

    public EffectLockInventory(Env env, EffectTemplate template) {
        super(env, template);
        lockType = template.getParam().getEnum("lockType", LockType.class);
        lockItems = template.getParam().getIntegerList("lockItems");
    }

    @Override
    public void onStart() {
        super.onStart();

        Player player = effector.getPlayer();

        player.getInventory().lockItems(lockType, lockItems);
    }

    @Override
    public void onExit() {
        super.onExit();

        Player player = effector.getPlayer();

        player.getInventory().unlock();
    }

    @Override
    protected boolean onActionTime() {
        return false;
    }
}
