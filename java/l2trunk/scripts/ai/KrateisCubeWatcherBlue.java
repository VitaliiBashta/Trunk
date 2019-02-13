package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class KrateisCubeWatcherBlue extends DefaultAI {
    private static final int RESTORE_CHANCE = 60;

    public KrateisCubeWatcherBlue(NpcInstance actor) {
        super(actor);
        AI_TASK_ACTIVE_DELAY = 3000;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtThink() {
        NpcInstance actor = getActor();

        World.getAroundPlayers(actor, 600, 300)
                .filter(cha -> !cha.isDead())
                .filter(cha -> Rnd.chance(RESTORE_CHANCE))
                .forEach(cha -> {
                    double valCP = cha.getMaxCp() - cha.getCurrentCp();
                    if (valCP > 0) {
                        cha.addCp(valCP);
                        cha.sendPacket(new SystemMessage2(SystemMsg.S1_CP_HAS_BEEN_RESTORED).addInteger(Math.round(valCP)));
                    }

                    double valHP = cha.getMaxHp() - cha.getCurrentHp();
                    if (valHP > 0) {
                        cha.setCurrentHp(valHP + cha.getCurrentHp(), false);
                        cha.sendPacket(new SystemMessage2(SystemMsg.S1_HP_HAS_BEEN_RESTORED).addInteger(Math.round(valHP)));
                    }

                    double valMP = cha.getMaxMp() - cha.getCurrentMp();
                    if (valMP > 0) {
                        cha.setCurrentMp(valMP + cha.getCurrentMp());
                        cha.sendPacket(new SystemMessage2(SystemMsg.S1_MP_HAS_BEEN_RESTORED).addInteger(Math.round(valMP)));
                    }
                });
    }

    @Override
    public void onEvtDead(Creature killer) {
        final NpcInstance actor = getActor();
        super.onEvtDead(killer);

        actor.deleteMe();
        ThreadPoolManager.INSTANCE.schedule(() -> {
            NpcInstance a = NpcHolder.getTemplate(18601).getNewInstance();
            a.setFullHpMp()
                    .spawnMe(actor.getLoc());
        }, 10000L);
    }
}
