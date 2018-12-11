package l2trunk.scripts.handler.items;

import l2trunk.gameserver.data.xml.holder.SkillAcquireHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Spellbooks extends ScriptItemHandler implements ScriptFile {
    private final Set<Integer> _itemIds = new HashSet<>();

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

    public Spellbooks() {
        List<SkillLearn> l = SkillAcquireHolder.getAllNormalSkillTreeWithForgottenScrolls();
        for (SkillLearn learn : l)
            _itemIds.add(learn.getItemId());
    }

    @Override
    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (!playable.isPlayer())
            return false;

        Player player = (Player) playable;

        if (item.getCount() < 1) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return false;
        }

        List<SkillLearn> list = SkillAcquireHolder.getSkillLearnListByItemId(player, item.getItemId());
        if (list.isEmpty()) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return false;
        }

        // проверяем ли есть нужные скилы
        boolean alreadyHas = true;
        boolean good = true;
        for (SkillLearn learn : list) {
            if (player.getSkillLevel(learn.getId()) != learn.getLevel()) {
                alreadyHas = false;
                break;
            }
        }
        for (SkillLearn learn2 : list) {
            if (item.getItemId() == 13728 && learn2.getItemId() != 13728) {
                good = false;
                break;
            }
        }
        if (!good) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return false;
        }
        if (alreadyHas) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return false;
        }

        // проверка по уровне
        boolean wrongLvl = false;
        for (SkillLearn learn : list) {
            if (player.getLevel() < learn.getMinLevel())
                wrongLvl = true;
        }

        if (wrongLvl) {
            player.sendPacket(new SystemMessage2(SystemMsg.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(item.getItemId()));
            return false;
        }

        if (!player.consumeItem(item.getItemId(), 1L))
            return false;

        for (SkillLearn skillLearn : list) {
            Skill skill = SkillTable.INSTANCE.getInfo(skillLearn.getId(), skillLearn.getLevel());
            if (skill == null)
                continue;
            player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.getId(), skill.getLevel()));

            player.addSkill(skill, true);
        }

        player.updateStats();
        player.sendPacket(new SkillList(player));
        // Анимация изучения книги над головой чара (на самом деле, для каждой книги своя анимация, но они одинаковые)
        player.broadcastPacket(new MagicSkillUse(player, player, 2790, 1, 1, 0));
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return new ArrayList<>(_itemIds);
    }
}