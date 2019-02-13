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

import java.util.List;

public final class Spellbooks extends ScriptItemHandler implements ScriptFile {
    private final List<Integer> itemIds;

    public Spellbooks() {
        itemIds = SkillAcquireHolder.getAllSpellbookIds();
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
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
            if (player.getSkillLevel(learn.id()) != learn.getLevel()) {
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
            Skill skill = SkillTable.INSTANCE.getInfo(skillLearn.id(), skillLearn.getLevel());
            if (skill == null)
                continue;
            player.sendPacket(new SystemMessage2(SystemMsg.YOU_HAVE_EARNED_S1_SKILL).addSkillName(skill.id, skill.level));

            player.addSkill(skill, true);
        }

        player.updateStats();
        player.sendPacket(new SkillList(player));
        // Анимация изучения книги над головой чара (на самом деле, для каждой книги своя анимация, но они одинаковые)
        player.broadcastPacket(new MagicSkillUse(player, 2790));
        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return itemIds;
    }
}