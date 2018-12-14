package l2trunk.scripts.ai.events;

import l2trunk.commons.math.random.RndSelector;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.tables.SkillTable;

public final class SpecialTree extends DefaultAI {
    private static final RndSelector<Integer> SOUNDS;

    static {
        SOUNDS = new RndSelector<>(5);
        SOUNDS.add(2140, 20);
        SOUNDS.add(2142, 20);
        SOUNDS.add(2145, 20);
        SOUNDS.add(2147, 20);
        SOUNDS.add(2149, 20);
    }

    private boolean _buffsEnabled = false;
    private int _timer = 0;

    public SpecialTree(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        if (_buffsEnabled) {
            _timer++;
            if (_timer >= 180) {
                _timer = 0;

                final NpcInstance actor = getActor();
                if (actor == null)
                    return false;

                addTaskBuff(actor, 2139);

                if (Rnd.chance(33))
                    actor.broadcastPacketToOthers(new MagicSkillUse(actor,  SOUNDS.select(),  500));
            }
        }

        return super.thinkActive();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        _buffsEnabled = !getActor().isInZonePeace();
        _timer = 0;
    }
}