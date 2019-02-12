package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.model.reward.RewardItem;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.utils.ItemFunctions;

import java.util.List;

public final class Sweep extends Skill {
    public Sweep(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Player player, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (isNotTargetAoE())
            return super.checkCondition(player, target, forceUse, dontMove, first);

        if (target == null)
            return false;

        if (!(target instanceof MonsterInstance) || !target.isDead()) {
            player.sendPacket(SystemMsg.INVALID_TARGET);
            return false;
        }

        if (!((MonsterInstance) target).isSpoiled()) {
            player.sendPacket(SystemMsg.SWEEPER_FAILED_TARGET_NOT_SPOILED);
            return false;
        }

        if (!((MonsterInstance) target).isSpoiled(player)) {
            player.sendPacket(SystemMsg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
            return false;
        }

        return super.checkCondition(player, target, forceUse, dontMove, first);
    }

    @Override
    public void useSkill(Creature activeChar, Creature target) {
        if (!(activeChar instanceof Player))
            return;

        Player player = (Player) activeChar;

        if (target instanceof MonsterInstance && target.isDead() && ((MonsterInstance) target).isSpoiled()) {
            MonsterInstance monster = (MonsterInstance) target;

            if (monster.isSpoiled(player)) {
                List<RewardItem> items = monster.takeSweep();

                if (items != null) {
                    for (RewardItem item : items) {
                        ItemInstance sweep = ItemFunctions.createItem(item.itemId);
                        sweep.setCount(item.count);

                        if (player.isInParty() && player.getParty().isDistributeSpoilLoot()) {
                            player.getParty().distributeItem(player, sweep, null);
                        } else {
                            if (player.getInventory().validateCapacity(sweep) && player.getInventory().validateWeight(sweep)) {
                                player.getInventory().addItem(sweep, "Sweep");

                                SystemMessage2 smsg = getMessage(item, false);
                                player.sendPacket(smsg);
                                if (player.isInParty()) {
                                    smsg = getMessage(item, true);
                                    player.getParty().sendPacket(player, smsg);
                                }
                            }
                        }

                    }
                }
                player.getAI().setAttackTarget(null);
                monster.endDecayTask();
            }
        } else {
            player.sendPacket(SystemMsg.THERE_ARE_NO_PRIORITY_RIGHTS_ON_A_SWEEPER);
        }

    }


    private SystemMessage2 getMessage(RewardItem item, boolean party) {
        SystemMessage2 smsg;
        if (!party) {
            if (item.count == 1) {
                smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S1);
                smsg.addItemName(item.itemId);
            } else {
                smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S2_S1);
                smsg.addItemName(item.itemId);
                smsg.addInteger(item.count);
            }
        } else {
            if (item.count == 1) {
                smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S1);
                smsg.addItemName(item.itemId);
            } else {
                smsg = new SystemMessage2(SystemMsg.YOU_HAVE_OBTAINED_S2_S1);
                smsg.addItemName(item.itemId);
                smsg.addInteger(item.count);
            }

        }
        return smsg;
    }
}