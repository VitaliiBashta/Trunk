package l2trunk.scripts.ai.crypts_of_disgrace;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class ContaminatedBaturCommander extends Fighter {
    private static final int TurkaCommanderChief = 22707; // Turka Commander in Chief
    private static final int CHANCE = 10; // Шанс спауна Turka Commander in Chief и миньенов

    public ContaminatedBaturCommander(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (Rnd.chance(CHANCE)) {
            // Спауним
            NpcInstance actor = getActor();
            SimpleSpawner sp = new SimpleSpawner(NpcHolder.getInstance().getTemplate(TurkaCommanderChief));
            sp.setLoc(Location.findPointToStay(actor, 100, 120));
            NpcInstance npc = sp.doSpawn(true);

            // Натравливаем на атакующего
            if (killer.isPet() || killer.isSummon())
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(2, 100));
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer.getPlayer(), Rnd.get(1, 100));
        }

        super.onEvtDead(killer);
    }
}
