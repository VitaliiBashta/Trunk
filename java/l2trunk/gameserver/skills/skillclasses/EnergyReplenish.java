package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.List;

public final class EnergyReplenish extends Skill {
    private final int addEnergy;

    public EnergyReplenish(StatsSet set) {
        super(set);
        addEnergy = set.getInteger("addEnergy");
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        if (!super.checkCondition(activeChar, target, forceUse, dontMove, first))
            return false;

        if (!activeChar.isPlayer())
            return false;

        Player player = (Player) activeChar;
        ItemInstance item = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LBRACELET);
        if (item == null || (item.getTemplate().getAgathionEnergy() - item.getAgathionEnergy()) < addEnergy) {
            player.sendPacket(SystemMsg.YOUR_ENERGY_CANNOT_BE_REPLENISHED_BECAUSE_CONDITIONS_ARE_NOT_MET);
            return false;
        }

        return true;
    }

    @Override
    public void useSkill(Creature activeChar, List<Creature> targets) {
        targets.forEach(cha -> {
            cha.setAgathionEnergy(cha.getAgathionEnergy() + addEnergy);
            cha.sendPacket(new SystemMessage2(SystemMsg.ENERGY_S1_REPLENISHED).addInteger(addEnergy));
        });
    }
}
