package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.network.serverpackets.components.NpcString;

import java.util.Arrays;
import java.util.List;

public final class _734_PierceThroughAShield extends Dominion_KillSpecialUnitQuest {
    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_ENEMY_KNIGHTS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_KNIGHTS;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_WEAKENED_THE_ENEMYS_DEFENSE;
    }

    @Override
    protected int getRandomMin() {
        return 10;
    }

    @Override
    protected int getRandomMax() {
        return 15;
    }

    @Override
    protected List<ClassId> getTargetClassIds() {
        return List.of(
                ClassId.darkAvenger,
                ClassId.hellKnight,
                ClassId.paladin,
                ClassId.phoenixKnight,
                ClassId.templeKnight,
                ClassId.evaTemplar,
                ClassId.shillienKnight,
                ClassId.shillienTemplar
        );
    }
}
