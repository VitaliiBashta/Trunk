package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.network.serverpackets.components.NpcString;

import java.util.List;

public final class _737_DenyBlessings extends Dominion_KillSpecialUnitQuest {
    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_HEALERS_AND_BUFFERS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_HEALERS_AND_BUFFERS;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_HAVE_WEAKENED_THE_ENEMYS_SUPPORT;
    }

    @Override
    protected int getRandomMin() {
        return 3;
    }

    @Override
    protected int getRandomMax() {
        return 8;
    }

    @Override
    protected List<ClassId> getTargetClassIds() {
        return List.of(
                ClassId.bishop,
                ClassId.prophet,
                ClassId.elder,
                ClassId.shillienElder,
                ClassId.cardinal,
                ClassId.hierophant,
                ClassId.evaSaint,
                ClassId.shillienSaint,
                ClassId.doomcryer
        );
    }
}
