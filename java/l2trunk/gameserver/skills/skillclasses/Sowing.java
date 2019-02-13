package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Manor;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

public final class Sowing extends Skill {
    public Sowing(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (!(activeChar instanceof Player))
            return;

        Player player = (Player) activeChar;
        int seedId = player.getUseSeed();
        boolean altSeed = ItemHolder.getTemplate(seedId).isAltSeed();

        // remove seed from inventory
        if (!player.getInventory().destroyItemByItemId(seedId, "Sowing")) {
            player.sendActionFailed();
            return;
        }

        player.sendPacket(SystemMessage2.removeItems(seedId, 1L));

        if (target instanceof MonsterInstance) {
            MonsterInstance monster = (MonsterInstance) target;
            if (!monster.isSeeded()) {// обработка
                double successRate = Config.MANOR_SOWING_BASIC_SUCCESS;

                double diffPlayerTarget = Math.abs(player.getLevel() - monster.getLevel());
                double diffSeedTarget = Math.abs(Manor.INSTANCE.getSeedLevel(seedId) - monster.getLevel());

                // Штраф, на разницу уровней между мобом и игроком
                // 5% на каждый уровень при разнице >5 - по умолчанию
                if (diffPlayerTarget > Config.MANOR_DIFF_PLAYER_TARGET)
                    successRate -= (diffPlayerTarget - Config.MANOR_DIFF_PLAYER_TARGET) * Config.MANOR_DIFF_PLAYER_TARGET_PENALTY;

                // Штраф, на разницу уровней между семечкой и мобом
                // 5% на каждый уровень при разнице >5 - по умолчанию
                if (diffSeedTarget > Config.MANOR_DIFF_SEED_TARGET)
                    successRate -= (diffSeedTarget - Config.MANOR_DIFF_SEED_TARGET) * Config.MANOR_DIFF_SEED_TARGET_PENALTY;

                if (altSeed)
                    successRate *= Config.MANOR_SOWING_ALT_BASIC_SUCCESS / Config.MANOR_SOWING_BASIC_SUCCESS;

                // Минимальный шанс успеха всегда 1%
                if (successRate < 1)
                    successRate = 1;

                if (player.isGM())
                    player.sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Sowing.Chance").addNumber((long) successRate));

                if (Rnd.chance(successRate) && monster.setSeeded(player, seedId, altSeed))
                    player.sendPacket(SystemMsg.THE_SEED_WAS_SUCCESSFULLY_SOWN);
                else
                    player.sendPacket(SystemMsg.THE_SEED_WAS_NOT_SOWN);
            }

        }
    }
}