package l2trunk.scripts.handler.items;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.SoulCrystalHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.ActionFail;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SetupGauge;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.SoulCrystal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class SoulCrystals extends ScriptItemHandler implements ScriptFile {
    private final Set<Integer> itemIds = new HashSet<>();

    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.getInstance().registerItemHandler(this);
    }

    @Override
    public void onReload() {

    }

    @Override
    public void onShutdown() {

    }

    public SoulCrystals() {
        for (SoulCrystal crystal : SoulCrystalHolder.getInstance().getCrystals()) {
            itemIds.add(crystal.getItemId());
            itemIds.add(crystal.getNextItemId());
        }
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer())
            return false;
        Player player = playable.getPlayer();

        if (player.getTarget() == null || !player.getTarget().isMonster()) {
            player.sendPacket(Msg.INVALID_TARGET, ActionFail.STATIC);
            return false;
        }

        if (playable.isActionsDisabled()) {
            player.sendActionFailed();
            return false;
        }

        MonsterInstance target = (MonsterInstance) player.getTarget();

        // u can use soul crystal only when target hp goes to <50%
        if (target.getCurrentHpPercents() >= 50) {
            player.sendPacket(Msg.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_A_SOUL, ActionFail.STATIC);
            return false;
        }

        // Soul Crystal Casting section
        int skillHitTime = SkillTable.INSTANCE().getInfo(2096, 1).getHitTime();
        player.broadcastPacket(new MagicSkillUse(player, 2096, 1, skillHitTime, 0));
        player.sendPacket(new SetupGauge(player, SetupGauge.BLUE, skillHitTime));
        // End Soul Crystal Casting section

        // Continue execution later
        player._skillTask = ThreadPoolManager.INSTANCE().schedule(new CrystalFinalizer(player, target), skillHitTime);
        return true;
    }

    static class CrystalFinalizer extends RunnableImpl {
        private final Player _activeChar;
        private final MonsterInstance _target;

        CrystalFinalizer(Player activeChar, MonsterInstance target) {
            _activeChar = activeChar;
            _target = target;
        }

        @Override
        public void runImpl() {
            _activeChar.sendActionFailed();
            _activeChar.clearCastVars();
            if (_activeChar.isDead() || _target.isDead())
                return;
            _target.addAbsorber(_activeChar);
        }
    }

    @Override
    public final List<Integer> getItemIds() {
        return new ArrayList<>(itemIds);
    }
}