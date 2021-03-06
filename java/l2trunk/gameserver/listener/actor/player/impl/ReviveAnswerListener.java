package l2trunk.gameserver.listener.actor.player.impl;

import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.PetInstance;

public final class ReviveAnswerListener implements OnAnswerListener {
    private final Player player;
    private final double _power;
    private final boolean _forPet;

    public ReviveAnswerListener(Player player, double power, boolean forPet) {
        this.player = player;
        _forPet = forPet;
        _power = power;
    }

    @Override
    public void sayYes() {
        if (player == null)
            return;
        if (!player.isDead() && !_forPet || _forPet && player.getPet() != null && !player.getPet().isDead())
            return;

        // Ady - If the request for resurrection was sent more than 5 minutes ago, then don't do nothing when its accepted. Only for players
        if (!_forPet && player.getResurrectionMaxTime() < System.currentTimeMillis())
            return;

        if (!_forPet)
            player.doRevive(_power);
        else if (player.getPet() != null)
            ((PetInstance) player.getPet()).doRevive(_power);
    }

    public double getPower() {
        return _power;
    }

    public boolean isForPet() {
        return _forPet;
    }
}
