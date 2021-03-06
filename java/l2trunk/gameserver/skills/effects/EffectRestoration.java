package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.utils.ItemFunctions;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.commons.lang.NumberUtils.toLong;

public final class EffectRestoration extends Effect {
    private final int itemId;
    private final long count;

    public EffectRestoration(Env env, EffectTemplate template) {
        super(env, template);
        String[] item = getTemplate().getParam().getString("Item").split(":");
        itemId = toInt(item[0]);
        count = toLong(item[1]);
    }

    @Override
    public void onStart() {
        super.onStart();
        ItemFunctions.addItem((Player) effected, itemId, count, "EffectRestoration");
    }

}
