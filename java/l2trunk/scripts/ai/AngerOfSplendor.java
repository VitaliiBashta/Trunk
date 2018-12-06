package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.StatusUpdate;

public final class AngerOfSplendor extends Fighter {

    public AngerOfSplendor(NpcInstance actor) {
        super(actor);
        AI_TASK_ATTACK_DELAY = 1000;
        AI_TASK_ACTIVE_DELAY = 1000;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker == null || actor.isDead())
            return;

        int transformer = 21528;
        int chance = actor.getParameter("transformChance", 90);
        if (chance == 100 || ((MonsterInstance) actor).getChampion() == 0 && actor.getCurrentHpPercents() > 50 && Rnd.chance(chance)) {
            MonsterInstance npc = (MonsterInstance) NpcHolder.getTemplate(transformer).getNewInstance();
            npc.setSpawnedLoc(actor.getLoc());
            npc.setReflection(actor.getReflection());
            npc.setChampion(((MonsterInstance) actor).getChampion());
            npc.setFullHpMp();
            npc.spawnMe(npc.getSpawnedLoc());
            npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 100);
            actor.doDie(actor);
            actor.decayMe();
            attacker.setTarget(npc);
            attacker.sendPacket(npc.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP));
            return;
        }
        super.onEvtAttacked(attacker, damage);
    }
}