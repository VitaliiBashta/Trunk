package l2trunk.gameserver.model.items.listeners;

import l2trunk.gameserver.data.xml.holder.OptionDataHolder;
import l2trunk.gameserver.listener.inventory.OnEquipListener;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.stats.triggers.TriggerInfo;
import l2trunk.gameserver.templates.OptionDataTemplate;

public final class ItemEnchantOptionsListener implements OnEquipListener {
    private static final ItemEnchantOptionsListener _instance = new ItemEnchantOptionsListener();

    public static ItemEnchantOptionsListener getInstance() {
        return _instance;
    }

    @Override
    public void onEquip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable())
            return;
        Player player = actor.getPlayer();

        boolean needSendInfo = false;
        for (int i : item.getEnchantOptions()) {
            OptionDataTemplate template = OptionDataHolder.getTemplate(i);
            if (template == null)
                continue;

            player.addStatFuncs(template.getStatFuncs(template));
            for (Skill skill : template.getSkills()) {
                player.addSkill(skill, false);
                needSendInfo = true;
            }
            for (TriggerInfo triggerInfo : template.getTriggerList())
                player.addTrigger(triggerInfo);
        }

        if (needSendInfo)
            player.sendPacket(new SkillList(player));
        player.sendChanges();
    }

    @Override
    public void onUnequip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable())
            return;

        Player player = actor.getPlayer();

        boolean needSendInfo = false;
        for (int i : item.getEnchantOptions()) {
            OptionDataTemplate template = OptionDataHolder.getTemplate(i);
            if (template == null)
                continue;

            player.removeStatsOwner(template);
            for (Skill skill : template.getSkills()) {
                player.removeSkill(skill.id, false);
                needSendInfo = true;
            }
            for (TriggerInfo triggerInfo : template.getTriggerList())
                player.removeTrigger(triggerInfo);
        }

        if (needSendInfo)
            player.sendPacket(new SkillList(player));
        player.sendChanges();
    }
}
