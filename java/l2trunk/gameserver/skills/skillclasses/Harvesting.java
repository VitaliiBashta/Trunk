package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.reward.RewardItem;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ItemFunctions;

public final class Harvesting extends Skill {
    public Harvesting(StatsSet set) {
        super(set);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (!(activeChar instanceof Player))
            return;

        Player player = (Player) activeChar;

            if (target instanceof MonsterInstance) {
                MonsterInstance monster = (MonsterInstance) target;

                // Не посеяно
                if (monster.isSeeded()) {
                    if (monster.isSeeded(player)) {
                        double SuccessRate = Config.MANOR_HARVESTING_BASIC_SUCCESS;
                        int diffPlayerTarget = Math.abs(activeChar.getLevel() - monster.getLevel());

                        // Штраф, на разницу уровней между мобом и игроком
                        // 5% на каждый уровень при разнице >5 - по умолчанию
                        if (diffPlayerTarget > Config.MANOR_DIFF_PLAYER_TARGET)
                            SuccessRate -= (diffPlayerTarget - Config.MANOR_DIFF_PLAYER_TARGET) * Config.MANOR_DIFF_PLAYER_TARGET_PENALTY;

                        // Минимальный шанс успеха всегда 1%
                        if (SuccessRate < 1)
                            SuccessRate = 1;

                        if (player.isGM())
                            player.sendMessage(new CustomMessage("l2trunk.gameserver.skills.skillclasses.Harvesting.Chance").addNumber((long) SuccessRate));

                        if (Rnd.chance(SuccessRate)) {
                            RewardItem item = monster.takeHarvest();
                            if (item != null) {
                                if (player.getInventory().validateCapacity(item.itemId, item.count) && player.getInventory().validateWeight(item.itemId, item.count)) {
                                    player.getInventory().addItem(item.itemId, item.count, "Harvesting");

                                    player.getCounters().manorSeedsSow++;

                                    player.sendPacket(new SystemMessage2(SystemMsg.C1_HARVESTED_S3_S2S).addName(player).addInteger(item.count).addItemName(item.itemId));
                                    if (player.isInParty()) {
                                        SystemMessage2 smsg = new SystemMessage2(SystemMsg.C1_HARVESTED_S3_S2S).addString(player.getName()).addInteger(item.count).addItemName(item.itemId);
                                        player.getParty().sendPacket(player, smsg);
                                    }
                                } else {
                                    ItemInstance harvest = ItemFunctions.createItem(item.itemId);
                                    harvest.setCount(item.count);
                                    harvest.dropToTheGround(player, monster);
                                }

                            }
                        } else {
                            activeChar.sendPacket(SystemMsg.THE_HARVEST_HAS_FAILED);
                            monster.clearHarvest();
                        }

                    } else {
                        activeChar.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_HARVEST);
                    }

                } else {
                    activeChar.sendPacket(SystemMsg.THE_HARVEST_FAILED_BECAUSE_THE_SEED_WAS_NOT_SOWN);
                }

            }
    }
}