package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;

public final class EvilNpc extends DefaultAI {
    private long _lastAction;
    private static final String[] _txt = {
            "Leave me alone!",
            "Calm down!",
            "I will avenge you, then you will ask for forgiveness!",
            "you will be in trouble!",
            "I complain to you, have you arrested!"};

    public EvilNpc(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (attacker == null || attacker.getPlayer() == null)
            return;

        // Ругаемся и кастуем скилл не чаще, чем раз в 3 секунды
        if (System.currentTimeMillis() - _lastAction > 3000) {
            int chance = Rnd.get(0, 100);
            if (chance < 2)
                attacker.getPlayer().setKarma(attacker.getPlayer().getKarma() + 5);
            else if (chance < 4)
                actor.doCast(4578, attacker, true); // Petrification
            else
                actor.doCast(4185, 7, attacker, true); // Sleep

            Functions.npcSay(actor, attacker.getName() + ", " + Rnd.get(_txt));
            _lastAction = System.currentTimeMillis();
        }
    }
}