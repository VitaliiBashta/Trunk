package l2trunk.scripts.ai.freya;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.network.serverpackets.components.NpcString;

import java.util.List;
import java.util.Objects;


public final class Maguen extends Fighter {
    private static final List<Integer> maguenStatsSkills = List.of(6343, 6365, 6366);
    private static final List<Integer> maguenRaceSkills = List.of(6367, 6368, 6369);

    public Maguen(NpcInstance actor) {
        super(actor);
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.INSTANCE.schedule(() -> getActor().setNpcState(Rnd.get(1, 3)), 2000L);
        ThreadPoolManager.INSTANCE.schedule(() -> {
            getActor().setNpcState(4);
            getActor().doDie(null);
        }, 10000L);
        ExShowScreenMessage sm = new ExShowScreenMessage(NpcString.MAGUEN_APPEARANCE, 5000, ScreenMessageAlign.TOP_CENTER, true, 1, -1, true);
        if (!getActor().isInZone(ZoneType.dummy)) {
            getActor().getAroundPlayers(800, 300)
                    .forEach(a -> a.sendPacket(sm));
        }
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        if (skill.id != 9060)
            return;
        NpcInstance actor = getActor();
        if (actor.isInZone(ZoneType.dummy)) {
            switch (actor.getNpcState()) {
                case 1:
                    if (Rnd.chance(80))
                        actor.doCast(maguenRaceSkills.get(0), Rnd.get(2, 3), caster, true);
                    else
                        actor.doCast(maguenStatsSkills.get(0), Rnd.get(1, 2), caster, true);
                    break;
                case 2:
                    if (Rnd.chance(80))
                        actor.doCast(maguenRaceSkills.get(1), Rnd.get(2, 3), caster, true);
                    else
                        actor.doCast(maguenStatsSkills.get(1), Rnd.get(1, 2), caster, true);
                    break;
                case 3:
                    if (Rnd.chance(80))
                        actor.doCast(maguenRaceSkills.get(2), Rnd.get(2, 3), caster, true);
                    else
                        actor.doCast(maguenStatsSkills.get(2), Rnd.get(1, 2), caster, true);
                    break;
                default:
                    break;
            }
        } else {
            switch (actor.getNpcState()) {
                case 1:
                    actor.doCast(maguenRaceSkills.get(0), caster, true);
                    break;
                case 2:
                    actor.doCast(maguenRaceSkills.get(1), caster, true);
                    break;
                case 3:
                    actor.doCast(maguenRaceSkills.get(2), caster, true);
                    break;
                default:
                    break;
            }
        }
        getActor().setNpcState(4);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (attacker == null || attacker instanceof Playable)
            return;

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean checkAggression(Playable target, boolean avoidAttack) {
        return false;
    }
}