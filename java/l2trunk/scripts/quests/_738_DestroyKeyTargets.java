package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.network.serverpackets.components.NpcString;

import java.util.List;

public final class _738_DestroyKeyTargets extends Dominion_KillSpecialUnitQuest {
    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_WARSMITHS_AND_OVERLORDS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_WARSMITHS_AND_OVERLORDS;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_DESTROYED_THE_ENEMYS_PROFESSIONALS;
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
                ClassId.necromancer,
                ClassId.swordSinger,
                ClassId.bladedancer,
                ClassId.overlord,
                ClassId.warsmith,
                ClassId.soultaker,
                ClassId.swordMuse,
                ClassId.spectralDancer,
                ClassId.dominator,
                ClassId.maestro,
                ClassId.inspector,
                ClassId.judicator
        );
    }
}
