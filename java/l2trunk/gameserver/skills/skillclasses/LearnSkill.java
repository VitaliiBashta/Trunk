package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.StatsSet;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class LearnSkill extends Skill {
    private final List<Integer> _learnSkillId;
    private final List<Integer> _learnSkillLvl;

    public LearnSkill(StatsSet set) {
        super(set);

        _learnSkillId = Arrays.stream(set.getString("learnSkillId", "0").split(","))
                .map(NumberUtils::toInt)
                .collect(Collectors.toList());


        _learnSkillLvl = Arrays.stream(set.getString("learnSkillLvl", "1").split(","))
                .map(NumberUtils::toInt)
                .collect(Collectors.toList());
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        if (!(activeChar instanceof Player))
            return;

        final Player player = ((Player) activeChar);
        Skill newSkill;

        for (int i = 0; i < _learnSkillId.size(); i++) {
            if (player.getSkillLevel(_learnSkillId.get(i)) < _learnSkillLvl.get(i) && _learnSkillId.get(i) != 0) {
                newSkill = SkillTable.INSTANCE.getInfo(_learnSkillId.get(i), _learnSkillLvl.get(i));
                if (newSkill != null)
                    player.addSkill(newSkill, true);
            }
        }
    }
}