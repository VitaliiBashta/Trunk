package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;

import java.util.List;

public class NihilInvaderChest extends DefaultAI {
    private static final List<Integer> _firstLevelItems = List.of(4039, 4040, 4041, 4042, 4043, 4044);
    private static final List<Integer> _secondLevelItems =List.of(9628, 9629, 9630);

    public NihilInvaderChest(NpcInstance actor) {
        super(actor);
        actor.startImmobilized();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor.getNpcId() == 18820) {
            if (Rnd.chance(40)) {
                actor.broadcastPacket(new MagicSkillUse(actor,  2025,  1, 10));
                actor.dropItem(attacker.getPlayer(),Rnd.get(_firstLevelItems), Rnd.get(10, 20));
                actor.doDie(null);
            }
        } else if (actor.getNpcId() == 18823) {
            if (Rnd.chance(40)) {
                actor.broadcastPacket(new MagicSkillUse(actor,  2025,  1, 10));
                actor.dropItem(attacker.getPlayer(),Rnd.get(_secondLevelItems), Rnd.get(10, 20));
                actor.doDie(null);
            }
        }
        for (NpcInstance npc : actor.getReflection().getNpcs())
            if (npc.getNpcId() == actor.getNpcId())
                npc.deleteMe();

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }

}