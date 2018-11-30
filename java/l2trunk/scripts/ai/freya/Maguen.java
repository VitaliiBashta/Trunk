package l2trunk.scripts.ai.freya;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.tables.SkillTable;

public final class Maguen extends Fighter {
    private static final int[] maguenStatsSkills = {6343, 6365, 6366};
    private static final int[] maguenRaceSkills = {6367, 6368, 6369};

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
            getActor().getAroundCharacters(800, 300)
                    .stream()
                    .filter(GameObject::isPlayer)
                    .forEach(a -> a.sendPacket(sm));
        }
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        if (skill.getId() != 9060)
            return;
        NpcInstance actor = getActor();
        if (actor.isInZone(ZoneType.dummy)) {
            switch (actor.getNpcState()) {
                case 1:
                    if (Rnd.chance(80))
                        actor.doCast(SkillTable.INSTANCE.getInfo(maguenRaceSkills[0], Rnd.get(2, 3)), caster, true);
                    else
                        actor.doCast(SkillTable.INSTANCE.getInfo(maguenStatsSkills[0], Rnd.get(1, 2)), caster, true);
                    break;
                case 2:
                    if (Rnd.chance(80))
                        actor.doCast(SkillTable.INSTANCE.getInfo(maguenRaceSkills[1], Rnd.get(2, 3)), caster, true);
                    else
                        actor.doCast(SkillTable.INSTANCE.getInfo(maguenStatsSkills[1], Rnd.get(1, 2)), caster, true);
                    break;
                case 3:
                    if (Rnd.chance(80))
                        actor.doCast(SkillTable.INSTANCE.getInfo(maguenRaceSkills[2], Rnd.get(2, 3)), caster, true);
                    else
                        actor.doCast(SkillTable.INSTANCE.getInfo(maguenStatsSkills[2], Rnd.get(1, 2)), caster, true);
                    break;
                default:
                    break;
            }
        } else {
            switch (actor.getNpcState()) {
                case 1:
                    actor.doCast(SkillTable.INSTANCE.getInfo(maguenRaceSkills[0], 1), caster, true);
                    break;
                case 2:
                    actor.doCast(SkillTable.INSTANCE.getInfo(maguenRaceSkills[1], 1), caster, true);
                    break;
                case 3:
                    actor.doCast(SkillTable.INSTANCE.getInfo(maguenRaceSkills[2], 1), caster, true);
                    break;
                default:
                    break;
            }
        }
        getActor().setNpcState(4);
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        if (attacker == null)
            return;

        if (attacker.isPlayable())
            return;

        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean checkAggression(Creature target, boolean avoidAttack) {
        if (target.isPlayable())
            return false;

        return super.checkAggression(target, avoidAttack);
    }
}