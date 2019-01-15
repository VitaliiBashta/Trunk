package l2trunk.gameserver.utils;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;

public final class SiegeUtils {
    final private static int SealofRuler = 246;
    final private static int BuildHeadquarters = 247;
    final private static int BuildAdvancedHeadquarters = 326;
    final private static int OutpostConstruction = 844;
    final private static int OutpostDemolition = 845;

    public static void addSiegeSkills(Player character) {
        character.addSkill(SealofRuler, false);
        character.addSkill(BuildHeadquarters, false);
        if (character.isNoble())
            character.addSkill(BuildAdvancedHeadquarters, false);

        if (character.getClan() != null && character.getClan().getCastle() > 0) {
            character.addSkill(OutpostConstruction, false);
            character.addSkill(OutpostDemolition, false);
        }
    }

    public static void removeSiegeSkills(Player character) {
        character.removeSkill(SealofRuler, false);
        character.removeSkill(BuildHeadquarters, false);
        character.removeSkill(BuildAdvancedHeadquarters, false);

        if (character.getClan() != null && character.getClan().getCastle() > 0) {
            character.removeSkill(OutpostConstruction, false);
            character.removeSkill(OutpostDemolition, false);
        }
    }
}
