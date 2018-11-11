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

public class SkillsEngine {
    private static final Logger _log = LoggerFactory.getLogger(SkillsEngine.class);

    private static final SkillsEngine _instance = new SkillsEngine();

    public static SkillsEngine getInstance() {
        return _instance;
    }

    private List<Skill> loadSkills(Path file) {
        if (file == null) {
            _log.warn("SkillsEngine: File not found!");
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
            _log.info("Dir " + dir.toAbsolutePath() + " not exists");
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

        _log.info("SkillsEngine: Loaded " + result.size() + " skill templates from XML files. Max id: " + maxId + ", max level: " + maxLvl);
        return result;
    }
}