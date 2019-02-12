package l2trunk.scripts.ai.crypts_of_disgrace;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.Location;

public final class TurkaCommanderChief extends Fighter {
    private static final int TurkaCommanderMinion = 22706; // Миньен
    private static final int MinionCount = 2; // Количество миньенов
    private static final int Guardian = 18815; // Guardian of the Burial Grounds
    private static final int CHANCE = 10;

    public TurkaCommanderChief(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();

        for (int i = 0; i < MinionCount; i++) // При спауне главного спауним и миньенов
            npcSpawn(TurkaCommanderMinion);
    }

    @Override
    public void onEvtDead(Creature killer) {
        if (Rnd.chance(CHANCE)) // Если повезло
        {
            // Спауним гварда
            NpcInstance npc = npcSpawn(Guardian);

            // И натравливаем его
            if (killer instanceof Summon)
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(2, 100));
            if (killer instanceof Player)
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, killer, Rnd.get(1, 100));
        }

        super.onEvtDead(killer);
    }

    private NpcInstance npcSpawn(int template) {
        NpcInstance actor = getActor();
        SimpleSpawner sp = new SimpleSpawner(template)
                .setLoc(Location.findPointToStay(actor, 100, 120));
        return sp.doSpawn(true);
    }
}
