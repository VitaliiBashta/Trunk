package l2trunk.scripts.handler.items;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.SoulCrystalHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
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

    public SoulCrystals() {
        for (SoulCrystal crystal : SoulCrystalHolder.getInstance().getCrystals()) {
            itemIds.add(crystal.getItemId());
            itemIds.add(crystal.getNextItemId());
        }
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
            if (player.isActionsDisabled()) {
                player.sendActionFailed();
                return false;
            }
            GameObject target = player.getTarget();
            if (target instanceof MonsterInstance) {
                MonsterInstance monster = (MonsterInstance) target;

                // u can use soul crystal only when monster hp goes to <50%
                if (monster.getCurrentHpPercents() >= 50) {
                    player.sendPacket(Msg.THE_SOUL_CRYSTAL_WAS_NOT_ABLE_TO_ABSORB_A_SOUL, ActionFail.STATIC);
                    return false;
                }

                // Soul Crystal Casting section
                Skill soulCrystal = SkillTable.INSTANCE.getInfo(2096);
                player.broadcastPacket(new MagicSkillUse(player, soulCrystal));
                player.sendPacket(new SetupGauge(player, SetupGauge.BLUE, soulCrystal.hitTime));
                // End Soul Crystal Casting section

                // Continue execution later
                player.skillTask = ThreadPoolManager.INSTANCE.schedule(() -> {
                    player.sendActionFailed();
                    player.clearCastVars();
                    if (player.isDead())
                        return;
                    monster.addAbsorber(player);
                }, soulCrystal.hitTime);
                return true;
            } else {
                player.sendPacket(Msg.INVALID_TARGET, ActionFail.STATIC);
                return false;
            }
    }

    @Override
    public final List<Integer> getItemIds() {
        return new ArrayList<>(itemIds);
    }

}