package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.base.InvisibleType;
import l2trunk.gameserver.stats.Env;

public final class EffectInvisible extends Effect {
    private InvisibleType _invisibleType = InvisibleType.NONE;

    public EffectInvisible(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public boolean checkCondition() {
        if (effected instanceof Player) {
            Player player = (Player) effected;
            if (player.isInvisible())
                return false;
            return player.getActiveWeaponFlagAttachment() == null;
        } else return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        Player player = (Player) effected;

        _invisibleType = player.getInvisibleType();

        player.setInvisibleType(InvisibleType.EFFECT);

        World.removeObjectFromPlayers(player);

        World.getAroundNpc(player, 500, 100)
                .filter(cr -> player.equals(cr.getCastingTarget()))
                .forEach(cr -> cr.abortCast(true, true));
    }

    @Override
    public void onExit() {
        super.onExit();
        Player player = (Player) effected;
        if (!player.isInvisible())
            return;

        player.setInvisibleType(_invisibleType);

        player.broadcastUserInfo(true);
        if (player.getPet() != null)
            player.getPet().broadcastCharInfo();
    }

}