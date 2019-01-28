package l2trunk.scripts.handler.items;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.instancemanager.MapRegionHolder;
import l2trunk.gameserver.model.Manor;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.ChestInstance;
import l2trunk.gameserver.model.instances.MinionInstance;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.RaidBossInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.mapregion.DomainArea;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Seed extends ScriptItemHandler implements ScriptFile {
    private static Set<Integer> _itemIds;

    public Seed() {
        _itemIds = Manor.INSTANCE.getAllSeeds().keySet();
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
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer())
            return false;
        Player player = (Player) playable;

        // Цель не выбрана
        if (playable.getTarget() == null) {
            player.sendActionFailed();
            return false;
        }

        // Цель не моб, РБ или миньон
        if (!player.getTarget().isMonster() || player.getTarget() instanceof RaidBossInstance || player.getTarget() instanceof MinionInstance && ((MinionInstance) player.getTarget()).getLeader() instanceof RaidBossInstance || player.getTarget() instanceof ChestInstance || ((MonsterInstance) playable.getTarget()).getChampion() > 0 && !item.isAltSeed()) {
            player.sendPacket(SystemMsg.THE_TARGET_IS_UNAVAILABLE_FOR_SEEDING);
            return false;
        }

        MonsterInstance target = (MonsterInstance) playable.getTarget();

        if (target == null) {
            player.sendPacket(Msg.INVALID_TARGET);
            return false;
        }

        // Моб мертв
        if (target.isDead()) {
            player.sendPacket(Msg.INVALID_TARGET);
            return false;
        }

        // Уже посеяно
        if (target.isSeeded()) {
            player.sendPacket(SystemMsg.THE_SEED_HAS_BEEN_SOWN);
            return false;
        }

        int seedId = item.getItemId();
        if (seedId == 0 || player.getInventory().getItemByItemId(item.getItemId()) == null) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
        }

        DomainArea domain = MapRegionHolder.getInstance().getRegionData(DomainArea.class, player);
        int castleId = domain == null ? 0 : domain.getId();
        // Несовпадение зоны
        if (Manor.INSTANCE.getCastleIdForSeed(seedId) != castleId) {
            player.sendPacket(SystemMsg.THIS_SEED_MAY_NOT_BE_SOWN_HERE);
            return false;
        }

        // use Sowing skill, id 2097
        Skill skill = SkillTable.INSTANCE.getInfo(2097);
        if (skill == null) {
            player.sendActionFailed();
            return false;
        }

        if (skill.checkCondition(player, target, false, false, true)) {
            player.setUseSeed(seedId);
            player.getAI().cast(skill, target);
        }
        return true;
    }

    @Override
    public final List<Integer> getItemIds() {
        return new ArrayList<>(_itemIds);
    }
}