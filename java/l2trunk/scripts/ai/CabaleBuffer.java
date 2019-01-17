package l2trunk.scripts.ai;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;
import java.util.Objects;

public final class CabaleBuffer extends DefaultAI {
    private static final int PREACHER_FIGHTER_SKILL_ID = 4361;
    private static final int PREACHER_MAGE_SKILL_ID = 4362;
    private static final int ORATOR_FIGHTER_SKILL_ID = 4364;
    private static final int ORATOR_MAGE_SKILL_ID = 4365;
    private static final long castDelay = 60 * 1000L;
    private static final long buffDelay = 1000L;
    private static final List<NpcString> preacherText = List.of(
            NpcString.THIS_WORLD_WILL_SOON_BE_ANNIHILATED,
            NpcString.ALL_IS_LOST__PREPARE_TO_MEET_THE_GODDESS_OF_DEATH,
            NpcString.ALL_IS_LOST__THE_PROPHECY_OF_DESTRUCTION_HAS_BEEN_FULFILLED,
            NpcString.THE_END_OF_TIME_HAS_COME__THE_PROPHECY_OF_DESTRUCTION_HAS_BEEN_FULFILLED);
    private static final List<NpcString> oratorText = List.of(
            NpcString.THE_DAY_OF_JUDGMENT_IS_NEAR,
            NpcString.THE_PROPHECY_OF_DARKNESS_HAS_BEEN_FULFILLED,
            NpcString.AS_FORETOLD_IN_THE_PROPHECY_OF_DARKNESS__THE_ERA_OF_CHAOS_HAS_BEGUN,
            NpcString.THE_PROPHECY_OF_DARKNESS_HAS_COME_TO_PASS);
    private long _castVar = 0;
    private long _buffVar = 0;

    public CabaleBuffer(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        int winningCabal = SevenSigns.INSTANCE.getCabalHighestScore();

        if (winningCabal == SevenSigns.CABAL_NULL)
            return true;

        int losingCabal = SevenSigns.CABAL_NULL;

        if (winningCabal == SevenSigns.CABAL_DAWN)
            losingCabal = SevenSigns.CABAL_DUSK;
        else if (winningCabal == SevenSigns.CABAL_DUSK)
            losingCabal = SevenSigns.CABAL_DAWN;

        if (_castVar + castDelay < System.currentTimeMillis()) {
            _castVar = System.currentTimeMillis();
            Functions.npcSay(actor, actor.getNpcId() == SevenSigns.ORATOR_NPC_ID ? Rnd.get(oratorText) : Rnd.get(preacherText));
        }
        /*
         * For each known player in range, cast either the positive or negative buff.
         * <BR>
         * The stats affected depend on the player type, either a fighter or a mystic.
         * <BR>
         * Curse of Destruction (Loser)
         *  - Fighters: -25% Accuracy, -25% Effect Resistance
         *  - Mystics: -25% Casting Speed, -25% Effect Resistance
         *
         * Blessing of Prophecy (Winner)
         *  - Fighters: +25% Max Load, +25% Effect Resistance
         *  - Mystics: +25% Magic Cancel Resist, +25% Effect Resistance
         */
        int lostCabal = losingCabal;
        if (_buffVar + buffDelay < System.currentTimeMillis()) {
            _buffVar = System.currentTimeMillis();
            World.getAroundPlayers(actor, 300, 200)
                    .filter(Objects::nonNull)
                    .forEach(player -> {
                        int playerCabal = SevenSigns.INSTANCE.getPlayerCabal(player);
                        int i0 = Rnd.get(100);
                        int i1 = Rnd.get(10000);
                        if (playerCabal == winningCabal && actor.getNpcId() == SevenSigns.ORATOR_NPC_ID) {
                            if (player.isMageClass()) {
                                if (player.getEffectList().getEffectsBySkillId(ORATOR_MAGE_SKILL_ID).count() <= 0) {
                                    if (i1 < 1)
                                        Functions.npcSay(actor, NpcString.I_BESTOW_UPON_YOU_A_BLESSING);
                                    actor.altUseSkill(ORATOR_MAGE_SKILL_ID, player);
                                } else if (i0 < 5) {
                                    if (i1 < 500)
                                        Functions.npcSay(actor, NpcString.S1__I_GIVE_YOU_THE_BLESSING_OF_PROPHECY, player.getName());
                                    actor.altUseSkill(ORATOR_MAGE_SKILL_ID, 2, player);
                                }
                            } else {
                                if (player.getEffectList().getEffectsBySkillId(ORATOR_FIGHTER_SKILL_ID).count() <= 0) {
                                    if (i1 < 1)
                                        Functions.npcSay(actor, NpcString.HERALD_OF_THE_NEW_ERA__OPEN_YOUR_EYES);
                                    actor.altUseSkill(ORATOR_FIGHTER_SKILL_ID, player);
                                } else if (i0 < 5) {
                                    if (i1 < 500)
                                        Functions.npcSay(actor, NpcString.S1__I_BESTOW_UPON_YOU_THE_AUTHORITY_OF_THE_ABYSS, player.getName());
                                    actor.altUseSkill(ORATOR_FIGHTER_SKILL_ID, 2, player);
                                }
                            }
                        } else if (playerCabal == lostCabal && actor.getNpcId() == SevenSigns.PREACHER_NPC_ID) {
                            if (player.isMageClass()) {
                                if (player.getEffectList().getEffectsBySkillId(PREACHER_MAGE_SKILL_ID).count() <= 0) {
                                    if (i1 < 1)
                                        Functions.npcSay(actor, NpcString.YOU_DONT_HAVE_ANY_HOPE__YOUR_END_HAS_COME);
                                    actor.altUseSkill(PREACHER_MAGE_SKILL_ID, player);
                                } else if (i0 < 5) {
                                    if (i1 < 500)
                                        Functions.npcSay(actor, NpcString.A_CURSE_UPON_YOU);

                                    actor.altUseSkill(PREACHER_MAGE_SKILL_ID, 2, player);
                                }
                            } else {
                                if (player.getEffectList().getEffectsBySkillId(PREACHER_FIGHTER_SKILL_ID).count() <= 0) {
                                    if (i1 < 1)
                                        Functions.npcSay(actor, NpcString.S1__YOU_BRING_AN_ILL_WIND, player.getName());

                                    actor.altUseSkill(PREACHER_FIGHTER_SKILL_ID, player);
                                } else if (i0 < 5) {
                                    if (i1 < 500)
                                        Functions.npcSay(actor, NpcString.S1__YOU_MIGHT_AS_WELL_GIVE_UP, player.getName());

                                    actor.altUseSkill(PREACHER_FIGHTER_SKILL_ID, 2, player);
                                }
                            }
                        }
                    });
        }

        return false;
    }
}