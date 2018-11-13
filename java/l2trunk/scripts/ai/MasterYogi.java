package l2trunk.scripts.ai;

import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;


public final class MasterYogi extends DefaultAI {
    private long wait_timeout1 = 0;
    private long wait_timeout2 = 0;
    private int range = 0;

    public MasterYogi(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean isGlobalAI() {
        return true;
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();

        //Calculate the radius at which NPCs will talk
        if (range <= 0) {
            List<NpcInstance> around = actor.getAroundNpc(6000, 300);
            if (around != null && !around.isEmpty()) {
                double distance;
                for (NpcInstance npc : around)
                    if (npc.getNpcId() == 32599) {
                        distance = actor.getDistance(npc) * 0.50;
                        if (range > 0 && distance < range || range == 0)
                            range = (int) distance;
                    }
            } else
                range = 3000;
        }

        if (System.currentTimeMillis() > wait_timeout1) {
            wait_timeout1 = System.currentTimeMillis() + 998988;
            Functions.npcSayInRangeCustomMessage(actor, range, "scripts.ai.MasterYogi.Hey");
            return true;
        }
        if (System.currentTimeMillis() > wait_timeout2) {
            wait_timeout2 = System.currentTimeMillis() + 1010009;
            Functions.npcSayInRangeCustomMessage(actor, range, "scripts.ai.MasterYogi.Hohoho");
            return true;
        }

        return randomAnimation();

    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }
}