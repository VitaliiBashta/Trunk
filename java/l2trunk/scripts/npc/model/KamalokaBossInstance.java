package l2trunk.scripts.npc.model;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.concurrent.ScheduledFuture;

public abstract class KamalokaBossInstance extends LostCaptainInstance {
    private ScheduledFuture<?> manaRegen;

    KamalokaBossInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        manaRegen = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new ManaRegen(), 20000, 20000);
    }

    @Override
    public boolean isRaid() {
        return false;
    }

    @Override
    protected void onDeath(Creature killer) {
        if (manaRegen != null) {
            manaRegen.cancel(false);
            manaRegen = null;
        }

        super.onDeath(killer);
    }

    private int getAddMp() {
        switch (getLevel()) {
            case 23:
            case 26:
                return 6;
            case 33:
            case 36:
                return 10;
            case 43:
            case 46:
                return 13;
            case 53:
            case 56:
                return 16; // С потолка
            case 63:
            case 66:
                return 19; // С потолка
            case 73:
                return 22; // С потолка
            default:
                return 0;
        }
    }

    private class ManaRegen implements Runnable {
        @Override
        public void run() {
            World.getAroundPlayers(KamalokaBossInstance.this).stream()
                    .filter(p -> !p.isHealBlocked())
                    .forEach(p -> {
                        int newMp = getNewMp(p.getCurrentMp(), p.getMaxMp());
                        p.setCurrentMp(newMp);
                        p.sendPacket(new SystemMessage(SystemMessage.S1_MPS_HAVE_BEEN_RESTORED).addNumber(newMp));
                    });

        }

        private int getNewMp(double currentMp, double maxMp) {
            double newMp = Math.min(Math.max(0, maxMp - currentMp), getAddMp());
            return (int) (newMp + currentMp);
        }
    }
}
