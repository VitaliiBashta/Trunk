package l2trunk.gameserver.skills;

import l2trunk.commons.lang.FileUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public enum SkillsEngine {
    INSTANCE;
    private static final Logger LOG = LoggerFactory.getLogger(SkillsEngine.class);

    private List<Skill> loadSkills(Path file) {
        if (file == null) {
            LOG.warn("SkillsEngine: File not found!");
            return null;
        }
        DocumentSkill doc = new DocumentSkill(file);
        doc.parse();
        return doc.getSkills();
    }

    public Skill loadSkill(int skillId, Path file) {
        DocumentSkill doc = new DocumentSkill(file, skillId);
        doc.parse();
        List<Skill> parsedSkills = doc.getSkills();

        if (parsedSkills.isEmpty())
            return null;
        else
            return parsedSkills.get(0);
    }

    public Map<Integer, Skill> loadAllSkills() {
        Path dir = Config.DATAPACK_ROOT.resolve("data/stats/skills");
        if (!Files.exists(dir)) {
            LOG.info("Dir " + dir.toAbsolutePath() + " not exists");
            return Collections.emptyMap();
        }

        Collection<Path> files = FileUtils.getAllFiles(dir, true, ".xml");
        Map<Integer, Skill> result = new HashMap<>();
        int maxId = 0;
        int maxLvl = 0;

        for (Path file : files) {
            List<Skill> s = loadSkills(file);
            if (s != null) {
                for (Skill skill : s) {
                    result.put(SkillTable.getSkillHashCode(skill), skill);
                    if (skill.getId() > maxId)
                        maxId = skill.getId();
                    if (skill.getLevel() > maxLvl)
                        maxLvl = skill.getLevel();
                }
            }
        }

        LOG.info("SkillsEngine: Loaded " + result.size() + " skill templates from XML files. Max id: " + maxId + ", max level: " + maxLvl);
        return result;
    }
}