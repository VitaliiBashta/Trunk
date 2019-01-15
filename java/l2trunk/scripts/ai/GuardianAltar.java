package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

/**
 * AI 18811	Guardian of the Altar - Спавит рандомных охранников если атакован
 * - если у игрока есть Protection Souls Pendant 14848 - спавнит мини-рб
 * - не использует random walk
 * - не отвечает на атаки
 */
public final class GuardianAltar extends DefaultAI {
    private static final int DarkShamanVarangka = 18808;

    public GuardianAltar(NpcInstance actor) {
        super(actor);
        actor.setInvul(true);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker == null)
            return;

        Player player = attacker.getPlayer();

        if (Rnd.chance(40) && player.getInventory().destroyItemByItemId(14848, 1L, "GuardianAltar")) {
            if (actor.getAroundNpc(1500, 300)
                    .filter(npc -> npc.getNpcId() == 18808)
                    .peek(npc -> Functions.npcSay(actor, "I can sense the presence of Dark Shaman already!"))
                    .findFirst().isPresent())
                return;


            SimpleSpawner sp = new SimpleSpawner(DarkShamanVarangka);
            sp.setLoc(Location.findPointToStay(actor, 400, 420));
            NpcInstance npc = sp.doSpawn(true);
            if (attacker.isPet() || attacker.isSummon())
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(2, 100));
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker.getPlayer(), Rnd.get(1, 100));

        } else if (Rnd.chance(5)) {
            if (actor.getAroundNpc(1000, 300)
                    .anyMatch(npc -> npc.getNpcId() == 22702))
                return;

            for (int i = 0; i < 2; i++) {
                SimpleSpawner sp = new SimpleSpawner(22702);
                sp.setLoc(Location.findPointToStay(actor, 150, 160));
                NpcInstance npc = sp.doSpawn(true);
                if (attacker.isPet() || attacker.isSummon())
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(2, 100));
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker.getPlayer(), Rnd.get(1, 100));
            }
        }
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}