package l2trunk.scripts.ai;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.List;

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
        List<Creature> around = World.getAroundCharacters(actor, 600, 300);
        if (around.isEmpty())
            return;

        for (Creature cha : around)
            if (cha.isPlayer() && !cha.isDead() && Rnd.chance(RESTORE_CHANCE)) {
                double valCP = cha.getMaxCp() - cha.getCurrentCp();
                if (valCP > 0) {
                    cha.setCurrentCp(valCP + cha.getCurrentCp());
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
            }
    }

    @Override
    public void onEvtDead(Creature killer) {
        final NpcInstance actor = getActor();
        super.onEvtDead(killer);

        actor.deleteMe();
        ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                NpcTemplate template = NpcHolder.getInstance().getTemplate(18601);
                if (template != null) {
                    NpcInstance a = template.getNewInstance();
                    a.setCurrentHpMp(a.getMaxHp(), a.getMaxMp());
                    a.spawnMe(actor.getLoc());
                }
            }
        }, 10000L);
    }
}
