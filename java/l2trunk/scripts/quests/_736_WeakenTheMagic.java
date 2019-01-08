package l2trunk.scripts.quests;

import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.network.serverpackets.components.NpcString;

import java.util.Arrays;
import java.util.List;

public final class _736_WeakenTheMagic extends Dominion_KillSpecialUnitQuest {
    public _736_WeakenTheMagic() {
        super();
    }

    @Override
    protected NpcString startNpcString() {
        return NpcString.DEFEAT_S1_WIZARDS_AND_SUMMONERS;
    }

    @Override
    protected NpcString progressNpcString() {
        return NpcString.YOU_HAVE_DEFEATED_S2_OF_S1_ENEMIES;
    }

    @Override
    protected NpcString doneNpcString() {
        return NpcString.YOU_WEAKENED_THE_ENEMYS_MAGIC;
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
                ClassId.sorceror,
                ClassId.warlock,
                ClassId.spellsinger,
                ClassId.elementalSummoner,
                ClassId.spellhowler,
                ClassId.phantomSummoner,
                ClassId.archmage,
                ClassId.arcanaLord,
                ClassId.mysticMuse,
                ClassId.elementalMaster,
                ClassId.stormScreamer,
                ClassId.spectralMaster
        );
    }
}
