package l2trunk.gameserver.model;

import l2trunk.commons.lang.reference.HardReference;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public class DeathPenalty {
    private static final int _skillId = 5076;
    private static final int _fortuneOfNobleseSkillId = 1325;
    private static final int _charmOfLuckSkillId = 2168;

    private final HardReference<Player> _playerRef;
    private int _level;
    private boolean hasCharmOfLuck;

    public DeathPenalty(Player player, int level) {
        _playerRef = player.getRef();
        _level = level;
    }

    private Player getPlayer() {
        return _playerRef.get();
    }

    /*
     * For common usage
     */
    public int getLevel() {
        // Some checks if admin set incorrect value at database
        if (_level > 15)
            _level = 15;

        if (_level < 0)
            _level = 0;

        return Config.ALLOW_DEATH_PENALTY_C5 ? _level : 0;
    }

    /*
     * Used only when saving DB if admin for some reasons disabled it in config after it was enabled.
     * In if we will use getLevel() it will be reseted to 0
     */
    public int getLevelOnSaveDB() {
        if (_level > 15)
            _level = 15;

        if (_level < 0)
            _level = 0;

        return _level;
    }

    public void notifyDead(Creature killer) {
        if (!Config.ALLOW_DEATH_PENALTY_C5)
            return;

        if (hasCharmOfLuck) {
            hasCharmOfLuck = false;
            return;
        }

        if (killer == null || killer.isPlayable())
            return;

        Player player = getPlayer();
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
            player.removeSkill(remove.getId(), true);

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
        Player player = getPlayer();
        if (player == null || getLevel() >= 15 || player.isGM())
            return;

        if (getLevel() != 0) {
            Skill remove = player.getKnownSkill(_skillId);
            if (remove != null)
                player.removeSkill(remove.getId(), true);
        }

        _level++;

        player.addSkill(_skillId, getLevel(), false);
        player.sendPacket(new SystemMessage2(SystemMsg.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addInteger(getLevel()));
        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    public void reduceLevel() {
        Player player = getPlayer();
        if (player == null || getLevel() <= 0)
            return;

        player.removeSkill(_skillId, true);

        _level--;

        if (getLevel() > 0) {
            player.addSkill(_skillId, getLevel(), false);
            player.sendPacket(new SystemMessage2(SystemMsg.THE_LEVEL_S1_DEATH_PENALTY_WILL_BE_ASSESSED).addInteger(getLevel()));
        } else
            player.sendPacket(SystemMsg.THE_DEATH_PENALTY_HAS_BEEN_LIFTED);

        player.sendEtcStatusUpdate();
        player.updateStats();
    }

    void checkCharmOfLuck() {
        Player player = getPlayer();
        if (player != null)
            hasCharmOfLuck = player.getEffectList().getAllEffects()
                    .map(e -> e.getSkill().getId())
                    .anyMatch(e -> e == _charmOfLuckSkillId || e == _fortuneOfNobleseSkillId);
        hasCharmOfLuck = false;
    }
}