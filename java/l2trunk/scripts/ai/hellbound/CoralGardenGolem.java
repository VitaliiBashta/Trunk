package l2trunk.scripts.ai.hellbound;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.scripts.instances.CrystalCaverns;

import java.util.List;

public final class CoralGardenGolem extends DefaultAI {
    private static final List<NpcString> phrases_idle = List.of(
            NpcString.HELLO_IS_ANYONE_THERE,
            NpcString.IS_NO_ONE_THERE_HOW_LONG_HAVE_I_BEEN_HIDING_I_HAVE_BEEN_STARVING_FOR_DAYS_AND_CANNOT_HOLD_OUT_ANYMORE,
            NpcString.IF_SOMEONE_WOULD_GIVE_ME_SOME_OF_THOSE_TASTY_CRYSTAL_FRAGMENTS_I_WOULD_GLADLY_TELL_THEM_WHERE_TEARS_IS_HIDING_YUMMY_YUMMY,
            NpcString.HEY_YOU_FROM_ABOVE_THE_GROUND_LETS_SHARE_SOME_CRYSTAL_FRAGMENTS_IF_YOU_HAVE_ANY);
    private static final List<NpcString> phrases_eat = List.of(
            NpcString.CRISPY_AND_COLD_FEELING_TEEHEE_DELICIOUS,
            NpcString.YUMMY_THIS_IS_SO_TASTY,
            NpcString.HOW_INSENSITIVE_ITS_NOT_NICE_TO_GIVE_ME_JUST_A_PIECE_CANT_YOU_GIVE_ME_MORE,
            NpcString.SNIFF_SNIFF_GIVE_ME_MORE_CRYSTAL_FRAGMENTS,
            NpcString.AH__IM_HUNGRY);
    private boolean fedCrystal = false;
    private boolean trapped = false;

    public CoralGardenGolem(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (!fedCrystal && Rnd.chance(1))
            Functions.npcShout(actor, Rnd.get(phrases_idle));
        if (!actor.isMoving && !trapped) {
            World.getAroundItems(actor, 200, 200)
                    .filter(i -> i.getItemId() == 9693) //Crystal Fragment
                    .findFirst().ifPresent(closestItem -> actor.moveToLocation(closestItem.getLoc(), 0, true));
        }
        return false;
    }

    @Override
    public void onEvtArrived() {
        super.onEvtArrived();
        NpcInstance actor = getActor();
        World.getAroundItems(actor, 20, 200)
                .forEach(obj -> {
                    if (obj.getItemId() == 9693) {
                        fedCrystal = true;
                        obj.deleteMe();
                        Functions.npcShout(actor, Rnd.get(phrases_eat));
                    } else
                        actor.moveToLocation(actor.getSpawnedLoc(), 0, true);
                });

        if (!trapped && (actor.isInZone("[cry_cav_cor_gar_golem_trap_1]") || actor.isInZone("[cry_cav_cor_gar_golem_trap_2]"))) {
            trapped = true;
            actor.broadcastPacket(new MagicSkillUse(actor, 5441, 3000));
            if (!actor.getReflection().isDefault() && actor.getReflection().getInstancedZoneId() == 10)
                ((CrystalCaverns) actor.getReflection()).notifyGolemTrapped();
        }
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
    }

    @Override
    public void onEvtAggression(Creature target, int aggro) {
    }

    @Override
    public boolean randomWalk() {
        return false;
    }
}