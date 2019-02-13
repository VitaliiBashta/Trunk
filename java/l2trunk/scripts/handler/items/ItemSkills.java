package l2trunk.scripts.handler.items;

import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.templates.item.ItemTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class ItemSkills extends ScriptItemHandler implements ScriptFile {
    private static List<Integer> itemIds;


    public ItemSkills() {
        Set<Integer> set = new HashSet<>();
        for (ItemTemplate template : ItemHolder.getAllTemplates()) {
            if (template == null)
                continue;

            for (Skill skill : template.getAttachedSkills())
                if (skill.isItemHandler)
                    set.add(template.itemId());
        }
        itemIds = new ArrayList<>(set);
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();

        if (ctrl && (itemId == Player.autoHp || itemId == Player.autoCp || itemId == Player.autoMp)) {

            if (itemId == Player.autoCp) {
                if (player._autoCp) {
                    player.AutoCp(false);
                    player.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addString(item.getName()));
                } else {
                    player.AutoCp(true);
                    player.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(item.getName()));
                }
            } else if (itemId == Player.autoHp) {
                if (player._autoHp) {
                    player.AutoHp(false);
                    player.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addString(item.getName()));
                } else {
                    player.AutoHp(true);
                    player.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(item.getName()));
                }
            } else {
                if (player._autoMp) {
                    player.AutoMp(false);
                    player.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addString(item.getName()));
                } else {
                    player.AutoMp(true);
                    player.sendPacket(new SystemMessage2(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addString(item.getName()));
                }
            }

            return false; // Avoid the use of potion when enabling/disabling feature
        } else {
            List<Skill> skills = item.getTemplate().getAttachedSkills();

            for (int i = 0; i < skills.size(); i++) {
                Skill skill = skills.get(i);
                Creature aimingTarget = skill.getAimingTarget(player, player.getTarget());
                if (skill.checkCondition(player, aimingTarget, ctrl, false, true))
                    player.getAI().cast(skill, aimingTarget, ctrl, false);
                else if (i == 0)  //FIXME [VISTALL] всегда первый скил идет вместо конда?
                    return false;
            }
        }

        return true;
    }

    @Override
    public List<Integer> getItemIds() {
        return itemIds;
    }
}
