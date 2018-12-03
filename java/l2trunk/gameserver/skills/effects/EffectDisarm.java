package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.stats.Env;

public final class EffectDisarm extends Effect {
    public EffectDisarm(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        if (!effected.isPlayer())
            return false;
        Player player = effected.getPlayer();
        // Нельзя снимать/одевать проклятое оружие и флаги
        if (player.isCursedWeaponEquipped() || player.getActiveWeaponFlagAttachment() != null)
            return false;
        return super.checkCondition();
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = (Player) effected;

        ItemInstance wpn = player.getActiveWeaponInstance();
        if (wpn != null) {
            player.getInventory().unEquipItem(wpn);
            player.sendDisarmMessage(wpn);
        }
        player.startWeaponEquipBlocked();
    }

    @Override
    public void onExit() {
        super.onExit();
        effected.stopWeaponEquipBlocked();
    }

    @Override
    public boolean onActionTime() {
        // just stop this effect
        return false;
    }
}