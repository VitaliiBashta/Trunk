package l2trunk.scripts.ai.residences.castle;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.SiegeClanObject;
import l2trunk.gameserver.model.instances.NpcInstance;

public final class ArtefactAI extends CharacterAI {
    public ArtefactAI(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtAggression(Creature attacker, int aggro) {
        NpcInstance actor;
        Player player;
        if (attacker == null || (player = attacker.getPlayer()) == null || (actor = (NpcInstance) getActor()) == null)
            return;

        SiegeEvent<?, ?> siegeEvent1 = actor.getEvent(SiegeEvent.class);
        SiegeEvent<?, ?> siegeEvent2 = player.getEvent(SiegeEvent.class);
        SiegeClanObject siegeClan = siegeEvent1.getSiegeClan(SiegeEvent.ATTACKERS, player.getClan());

        if (siegeEvent2 == null || siegeEvent1 == siegeEvent2 && siegeClan != null)
            ThreadPoolManager.INSTANCE.schedule(new notifyGuard(player), 1000);
    }

    private class notifyGuard extends RunnableImpl {
        private final HardReference<Player> _playerRef;

        notifyGuard(Player attacker) {
            _playerRef = attacker.getRef();
        }

        @Override
        public void runImpl() {
            NpcInstance actor;
            Player attacker = _playerRef.get();
            if (attacker == null || (actor = (NpcInstance) getActor()) == null)
                return;

            actor.getAroundNpc(1500, 200)
                    .filter(GameObject::isSiegeGuard)
                    .filter(npc -> Rnd.chance(20))
                    .forEach(npc -> npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 5000));

            if (attacker.getCastingSkill() != null && attacker.getCastingSkill().targetType == Skill.SkillTargetType.TARGET_HOLY)
                ThreadPoolManager.INSTANCE.schedule(this, 10000);
        }
    }
}
