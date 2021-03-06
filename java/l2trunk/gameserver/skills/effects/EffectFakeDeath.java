package l2trunk.gameserver.skills.effects;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.ChangeWaitType;
import l2trunk.gameserver.network.serverpackets.Revive;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Env;

public final class EffectFakeDeath extends Effect {
    public EffectFakeDeath(Env env, EffectTemplate template) {
        super(env, template);
    }

    @Override
    public void onStart() {
        super.onStart();

        Player player = (Player) effected;
        player.setFakeDeath(true);
        player.getAI().notifyEvent(CtrlEvent.EVT_FAKE_DEATH);
        player.broadcastPacket(new ChangeWaitType(player, 2));
        player.abortCast(true, false);
        player.abortAttack(true, false);
        player.broadcastCharInfo();
    }

    @Override
    public void onExit() {
        super.onExit();

        Player player = (Player) effected;
        player.setNonAggroTime(System.currentTimeMillis() + 5000L);
        player.setFakeDeath(false);
        player.broadcastPacket(new ChangeWaitType(player, 3));
        player.broadcastPacket(new Revive(player));
        player.broadcastCharInfo();
    }

    @Override
    public boolean onActionTime() {
        if (effected.isDead())
            return false;

        double manaDam = calc();

        if (manaDam > effected.getCurrentMp() && skill.isToggle()) {
            effected.sendPacket(SystemMsg.NOT_ENOUGH_MP);
            effected.sendPacket(new SystemMessage(SystemMessage.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(skill.id, skill.getDisplayLevel()));
            return false;
        }

        effected.reduceCurrentMp(manaDam, null);
        return true;
    }
}