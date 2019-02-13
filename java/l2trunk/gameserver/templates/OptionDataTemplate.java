package l2trunk.gameserver.templates;

import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.stats.StatTemplate;

import java.util.ArrayList;
import java.util.List;

public final class OptionDataTemplate extends StatTemplate {
    private final List<Skill> skills = new ArrayList<>(0);
    public final int id;

    public OptionDataTemplate(int id) {
        this.id = id;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public List<Skill> getSkills() {
        return skills;
    }

    public int id() {
        return id;
    }
}
