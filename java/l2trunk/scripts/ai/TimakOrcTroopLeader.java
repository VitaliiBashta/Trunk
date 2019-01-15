package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class TimakOrcTroopLeader extends Fighter {
    private static final List<Integer> BROTHERS = List.of(20768, // Timak Orc Troop Shaman
            20769, // Timak Orc Troop Warrior
            20770); // Timak Orc Troop Archer

    private boolean firstTimeAttacked = true;

    public TimakOrcTroopLeader(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (!actor.isDead() && firstTimeAttacked) {
            firstTimeAttacked = false;
            Functions.npcSay(actor, NpcString.SHOW_YOURSELVES);
            BROTHERS.forEach(bro -> {
                NpcInstance npc = NpcHolder.getTemplate(bro).getNewInstance();
                npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
                npc.setReflection(actor.getReflection());
                npc.setFullHpMp();
                npc.spawnMe(npc.getSpawnedLoc());
                npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
            });
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        firstTimeAttacked = true;
        super.onEvtDead(killer);
    }
}