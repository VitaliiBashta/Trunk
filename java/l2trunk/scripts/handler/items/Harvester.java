package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;

import java.util.List;

public final class Harvester extends SimpleItemHandler implements ScriptFile {
    private static final int ITEM_IDS = 5125;

    @Override
    public List<Integer> getItemIds() {
        return List.of(ITEM_IDS);
    }


    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    protected boolean useItemImpl(Player player, ItemInstance item, boolean ctrl) {
        GameObject target = player.getTarget();
        if (target == null || !target.isMonster()) {
            player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        MonsterInstance monster = (MonsterInstance) player.getTarget();

        if (!monster.isDead()) {
            player.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
            return false;
        }

        Skill skill = SkillTable.INSTANCE.getInfo(2098);
        if (skill != null && skill.checkCondition(player, monster, false, false, true)) {
            player.getAI().Cast(skill, monster);
            return true;
        }
        return false;
    }
}
