package l2trunk.gameserver.utils;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.tables.SkillTable;

public class SiegeUtils {
    public static void addSiegeSkills(Player character) {
        character.addSkill(SkillTable.INSTANCE().getInfo(246, 1), false);
        character.addSkill(SkillTable.INSTANCE().getInfo(247, 1), false);
        if (character.isNoble())
            character.addSkill(SkillTable.INSTANCE().getInfo(326, 1), false);

        if (character.getClan() != null && character.getClan().getCastle() > 0) {
            character.addSkill(SkillTable.INSTANCE().getInfo(844, 1), false);
            character.addSkill(SkillTable.INSTANCE().getInfo(845, 1), false);
        }
    }

    public static void removeSiegeSkills(Player character) {
        character.removeSkill(SkillTable.INSTANCE().getInfo(246, 1), false);
        character.removeSkill(SkillTable.INSTANCE().getInfo(247, 1), false);
        character.removeSkill(SkillTable.INSTANCE().getInfo(326, 1), false);

        if (character.getClan() != null && character.getClan().getCastle() > 0) {
            character.removeSkill(SkillTable.INSTANCE().getInfo(844, 1), false);
            character.removeSkill(SkillTable.INSTANCE().getInfo(845, 1), false);
        }
    }

    public static boolean getCanRide() {
        for (Residence residence : ResidenceHolder.getInstance().getResidences())
            if (residence != null && residence.getSiegeEvent().isInProgress())
                return false;
        return true;
    }
}
