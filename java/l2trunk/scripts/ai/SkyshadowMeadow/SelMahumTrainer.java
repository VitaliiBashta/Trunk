package l2trunk.scripts.ai.SkyshadowMeadow;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Grivesky
 * - AI for mobs Sel Mahum Drill Sergeant (22775), Sel Mahum Training Officer (22,776), Sel Mahum Drill Sergeant (22778).
 * - If the attack swears to chat, Agrita Coaches mobs.
 * - He uses a random Social Networks and makes Coaches mobs to repeat after him three times.
 * - AI is tested and works.
 */
public final class SelMahumTrainer extends Fighter {
    private static final List<NpcString> TEXT = List.of(NpcString.SCHOOL7, NpcString.SCHOOL8);
    private List<NpcInstance> _arm = new ArrayList<>();
    private long _wait_timeout = System.currentTimeMillis() + 20000;
    private boolean _firstTimeAttacked = true;

    public SelMahumTrainer(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        final int social = Rnd.get(4, 7);
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        if (_wait_timeout < System.currentTimeMillis()) {
            if (_arm == null || _arm.isEmpty()) {
                _arm = getActor().getAroundNpc(750, 750).collect(Collectors.toList());
            }

            _wait_timeout = (System.currentTimeMillis() + Rnd.get(20, 30) * 1000);

            actor.broadcastPacket(new SocialAction(actor.getObjectId(), social));
            actor.setHeading(actor.getSpawnedLoc().h);

            int time = 2000;
            for (int i = 0; i <= 2; i++) {
                ThreadPoolManager.INSTANCE.schedule(() -> {
                    _arm.forEach(voin -> {
                        voin.setHeading(voin.getSpawnedLoc().h);
                        voin.broadcastPacket(new SocialAction(voin.getObjectId(), social));
                    });
                }, time);
                time += 2000;
            }
        }
        return true;
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance npc = _arm.get(_arm.size() - 1);
        NpcInstance actor = getActor();
        if (actor == null)
            return;

        npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, Rnd.get(1, 100));

        if (attacker.isDead())
            actor.moveToLocation(actor.getSpawnedLoc(), 0, true);

        if (_firstTimeAttacked) {
            _firstTimeAttacked = false;
            Functions.npcSay(actor, Rnd.get(TEXT));
        }

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public void onEvtDead(Creature killer) {
        _firstTimeAttacked = true;
        super.onEvtDead(killer);
    }
}