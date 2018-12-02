package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * AI 18811	Guardian of the Altar - Спавит рандомных охранников если атакован
 * - если у игрока есть Protection Souls Pendant 14848 - спавнит мини-рб
 * - не использует random walk
 * - не отвечает на атаки
 *
 * @author pchayka
 */
public final class GuardianAltar extends DefaultAI {
    private static final Logger LOG = LoggerFactory.getLogger(GuardianAltar.class);
    private static final int DarkShamanVarangka = 18808;

    private GuardianAltar(NpcInstance actor) {
        super(actor);
        actor.setIsInvul(true);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker == null)
            return;

        Player player = attacker.getPlayer();

        if (Rnd.chance(40) && player.getInventory().destroyItemByItemId(14848, 1L, "GuardianAltar")) {
            List<NpcInstance> around = actor.getAroundNpc(1500, 300);
            if (around != null && !around.isEmpty())
                for (NpcInstance npc : around)
                    if (npc.getNpcId() == 18808) {
                        Functions.npcSay(actor, "I can sense the presence of Dark Shaman already!");
                        return;
                    }

            try {
                SimpleSpawner sp = new SimpleSpawner(NpcHolder.getTemplate(DarkShamanVarangka));
                sp.setLoc(Location.findPointToStay(actor, 400, 420));
                NpcInstance npc = sp.doSpawn(true);
                if (attacker.isPet() || attacker.isSummon())
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(2, 100));
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker.getPlayer(), Rnd.get(1, 100));
            } catch (RuntimeException e) {
                LOG.error("Error while Spawning Monsters of Guardian Altar", e);
            }

        } else if (Rnd.chance(5)) {
            List<NpcInstance> around = actor.getAroundNpc(1000, 300);
            if (around != null && !around.isEmpty())
                for (NpcInstance npc : around)
                    if (npc.getNpcId() == 22702)
                        return;

            for (int i = 0; i < 2; i++)
                try {
                    SimpleSpawner sp = new SimpleSpawner(NpcHolder.getTemplate(22702));
                    sp.setLoc(Location.findPointToStay(actor, 150, 160));
                    NpcInstance npc = sp.doSpawn(true);
                    if (attacker.isPet() || attacker.isSummon())
                        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(2, 100));
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker.getPlayer(), Rnd.get(1, 100));
                } catch (RuntimeException e) {
                    LOG.error("Error while spawning Rare Monsters of Guardian Altar", e);
                }
        }
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}