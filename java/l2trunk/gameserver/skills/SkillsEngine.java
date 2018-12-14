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
import java.util.stream.Collectors;

public enum SkillsEngine {
    INSTANCE;
    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private List<Skill> loadSkills(Path file) {
        DocumentSkill doc = new DocumentSkill(file);
        doc.parse();
        return doc.getSkills();
    }

    public Map<Integer, Skill> loadAllSkills() {
        Path dir = Config.DATAPACK_ROOT.resolve("data/stats/skills");
        if (!Files.exists(dir)) {
            LOG.info("Dir " + dir.toAbsolutePath() + " not exists");
            return Collections.emptyMap();
        }

        Collection<Path> files = FileUtils.getAllFiles(dir, true, ".xml");
        Map<Integer, Skill> result = new HashMap<>();
        files.forEach(file ->
                result.putAll(loadSkills(file).stream()
                        .collect(Collectors.toMap(SkillTable::getSkillHashCode, s -> s))));
        return result;
    }
}