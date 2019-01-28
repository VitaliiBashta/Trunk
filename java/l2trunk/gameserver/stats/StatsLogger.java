package l2trunk.gameserver.stats;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.utils.TimeUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public enum StatsLogger {
    INSTANCE;

    private final Collection<SkillStat> statsToAdd = new CopyOnWriteArrayList<>();

    StatsLogger() {
        ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new LogStatThread(), TimeUtils.MINUTE_IN_MILLIS, TimeUtils.MINUTE_IN_MILLIS);
    }

    public void addNewStat(Skill skill, Player attacker, Player defender, double base, double finalChance, double statMod, double mAtkMod, double deltaMod, double debuffMod, double resMod, double elementMod) {
        statsToAdd.add(new SkillStat(skill, attacker, defender, base, finalChance, statMod, mAtkMod, deltaMod, debuffMod, resMod, elementMod));
    }

    private void clearStats() {
        statsToAdd.clear();
    }

    private Collection<SkillStat> getStatsToAdd() {
        return statsToAdd;
    }

    private static class SkillStat {
        final String skillName;
        final int skillId;
        final int attackerLevel;
        final int defenderLevel;
        final String attackerClass;
        final String defenderClass;
        final double base;
        final double finalChance;
        final double statMod;
        final double mAtkMod;
        final double deltaMod;
        final double debuffMod;
        final double resMod;
        final double elementMod;

        private SkillStat(Skill skill, Player attacker, Player defender, double base, double finalChance, double statMod, double mAtkMod, double deltaMod, double debuffMod, double resMod, double elementMod) {
            skillName = skill.name;
            skillId = skill.id;
            attackerLevel = attacker.getLevel();
            defenderLevel = defender.getLevel();
            attackerClass = attacker.getClassId().name();
            defenderClass = defender.getClassId().name();
            this.base = base;
            this.finalChance = finalChance;
            this.statMod = statMod;
            this.mAtkMod = mAtkMod;
            this.deltaMod = deltaMod;
            this.debuffMod = debuffMod;
            this.resMod = resMod;
            this.elementMod = elementMod;
        }
    }

    private static class LogStatThread implements Runnable {
        @Override
        public void run() {
            Collection<SkillStat> stats = INSTANCE.getStatsToAdd();
            if (Config.ALLOW_SKILLS_STATS_LOGGER && stats.size() > 1) {
                StringBuilder query = new StringBuilder();
                query.append("INSERT INTO skill_chance_logger VALUES ");
                for (SkillStat stat : stats) {
                    query.append("('").append(stat.skillName).append("',");
                    query.append(stat.skillId).append(',');
                    query.append(stat.attackerLevel).append(',');
                    query.append(stat.defenderLevel).append(',');
                    query.append('\'').append(stat.attackerClass).append("',");
                    query.append('\'').append(stat.defenderClass).append("',");
                    query.append(stat.base).append(',');
                    query.append(stat.finalChance).append(',');
                    query.append(stat.statMod).append(',');
                    query.append(stat.mAtkMod).append(',');
                    query.append(stat.deltaMod).append(',');
                    query.append(stat.debuffMod).append(',');
                    query.append(stat.resMod).append(',');
                    query.append(stat.elementMod).append("),");
                }
                String finalQuery = query.substring(0, query.length() - 1);

                try (Connection con = DatabaseFactory.getInstance().getConnection();
                     PreparedStatement statement = con.prepareStatement(finalQuery)) {
                    statement.executeUpdate();
                } catch (SQLException e) {
                    //LOGGER.error("Error while logging Skill Chance Stats. Query: "+finalQuery+" Error: ", e);
                }

                INSTANCE.clearStats();
            }
        }
    }
}
