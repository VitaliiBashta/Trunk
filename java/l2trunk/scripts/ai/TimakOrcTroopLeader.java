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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TimakOrcTroopLeader extends Fighter {
    private static final Logger LOG = LoggerFactory.getLogger(TimakOrcTroopLeader.class);

    private static final int[] BROTHERS = {20768, // Timak Orc Troop Shaman
            20769, // Timak Orc Troop Warrior
            20770 // Timak Orc Troop Archer
    };

    private boolean _firstTimeAttacked = true;

    private TimakOrcTroopLeader(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (!actor.isDead() && _firstTimeAttacked) {
            _firstTimeAttacked = false;
            Functions.npcSay(actor, NpcString.SHOW_YOURSELVES);
            for (int bro : BROTHERS)
                try {
                    NpcInstance npc = NpcHolder.getInstance().getTemplate(bro).getNewInstance();
                    npc.setSpawnedLoc(((MonsterInstance) actor).getMinionPosition());
                    npc.setReflection(actor.getReflection());
                    npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp(), true);
                    npc.spawnMe(npc.getSpawnedLoc());
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));
                } catch (RuntimeException e) {
                    LOG.error("Error while spawning brothers of Timak Orc Troop Leader", e);
                }
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }
}