package l2trunk.gameserver.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class DeathPenalty {
    private static final int _skillId = 5076;
    private static final int _fortuneOfNobleseSkillId = 1325;
    private static final int _charmOfLuckSkillId = 2168;

    private final Player player;
    private int level;
    private boolean hasCharmOfLuck;

    public DeathPenalty(Player player, int level) {
        this.player = player;
        this.level = level;
    }

    /*
     * For common usage
     */
    public int getLevel() {
        // Some checks if admin set incorrect value at database
        if (level > 15)
            level = 15;

        if (level < 0)
            level = 0;

        return Config.ALLOW_DEATH_PENALTY_C5 ? level : 0;
    }

    /*
     * Used only when saving DB if admin for some reasons disabled it in config after it was enabled.
     * In if we will use level() it will be reseted to 0
     */
    int getLevelOnSaveDB() {
        if (level > 15)
            level = 15;

        if (level < 0)
            level = 0;

        return level;
    }

    public void notifyDead(Creature killer) {
        if (!Config.ALLOW_DEATH_PENALTY_C5)
            return;

        if (hasCharmOfLuck) {
            hasCharmOfLuck = false;
            return;
        }

        if (killer == null || killer instanceof Playable)
            return;

        if (player == null || player.getLevel() <= 9)
            return;

        int karmaBonus = player.getKarma() / Config.ALT_DEATH_PENALTY_C5_KARMA_PENALTY;
        if (karmaBonus < 0)
            karmaBonus = 0;

        if (Rnd.chance(Config.ALT_DEATH_PENALTY_C5_CHANCE + karmaBonus))
            addLevel();
    }

    public void restore(Player player) {
        Skill remove = player.getKnownSkill(_skillId);
        if (remove != null)
            player.removeSkill(remove.id, true);

        if (!Config.ALLOW_DEATH_PENALTY_C5)
            return;

        if (getLevel() > 0) {
            player.addSkill(_skillId, getLevel(), false);
            player.sendPacket(new SystemMessage2(SystemMsg.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addInteger(getLevel()));
        }
        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    private void addLevel() {
        if (player == null || getLevel() >= 15 || player.isGM())
            return;

        if (getLevel() != 0) {
            Skill remove = player.getKnownSkill(_skillId);
            if (remove != null)
                player.removeSkill(remove.id, true);
        }

        level++;

        player.addSkill(_skillId, getLevel(), false);
        player.sendPacket(new SystemMessage2(SystemMsg.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addInteger(getLevel()));
        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    public void reduceLevel() {
        if (player == null || getLevel() <= 0)
            return;

        player.removeSkill(_skillId, true);

        level--;

        if (getLevel() > 0) {
            player.addSkill(_skillId, getLevel(), false);
            player.sendPacket(new SystemMessage2(SystemMsg.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addInteger(getLevel()));
        } else
            player.sendPacket(SystemMsg.THE_DEATH_PENALTY_HAS_BEEN_LIFTED);

        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    void checkCharmOfLuck() {
        if (player != null)
            hasCharmOfLuck = player.getEffectList().getAllEffects().stream()
                    .map(e -> e.skill.id)
                    .anyMatch(e -> e == _charmOfLuckSkillId || e == _fortuneOfNobleseSkillId);
        hasCharmOfLuck = false;
    }
}