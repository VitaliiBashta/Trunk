package l2trunk.gameserver.model.items.listeners;

import l2trunk.gameserver.listener.inventory.OnEquipListener;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SkillCoolTime;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.stats.Formulas;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.item.ItemTemplate;

import java.util.List;

public final class ItemSkillsListener implements OnEquipListener {
    private static final ItemSkillsListener _instance = new ItemSkillsListener();

    public static ItemSkillsListener getInstance() {
        return _instance;
    }

    @Override
    public void onUnequip(int slot, ItemInstance item, Playable actor) {
        Player player = (Player) actor;

        ItemTemplate it = item.getTemplate();

        List<Skill> itemSkills = it.getAttachedSkills();

        Skill enchant4Skill = it.getEnchant4Skill();

        player.removeTriggers(it);

        if (itemSkills != null && itemSkills.size() > 0)
            for (Skill itemSkill : itemSkills)
                if (itemSkill.getId() >= 26046 && itemSkill.getId() <= 26048) {
                    int level = player.getSkillLevel(itemSkill.getId());
                    int newlevel = level - 1;
                    if (newlevel > 0)
                        player.addSkill(SkillTable.INSTANCE.getInfo(itemSkill.getId(), newlevel), false);
                    else
                        player.removeSkill(itemSkill.getId());
                } else {
                    player.removeSkill(itemSkill, false);
                }

        if (enchant4Skill != null)
            player.removeSkill(enchant4Skill, false);

        if (itemSkills.size() > 0 || enchant4Skill != null) {
            player.sendPacket(new SkillList(player));
            player.updateStats();
        }
    }

    @Override
    public void onEquip(int slot, ItemInstance item, Playable actor) {
        Player player = (Player) actor;

        ItemTemplate it = item.getTemplate();
        List<Skill> itemSkills = it.getAttachedSkills();;
        Skill enchant4Skill = null;


        if (item.getEnchantLevel() >= 4)
            enchant4Skill = it.getEnchant4Skill();

        // Для оружия при несоотвествии грейда скилы не выдаем
        if (it.getType2() == ItemTemplate.TYPE2_WEAPON && player.getWeaponsExpertisePenalty() > 0)
            return;

        player.addTriggers(it);

        boolean needSendInfo = false;
        if (itemSkills.size() > 0)
            for (Skill itemSkill : itemSkills)
                if (itemSkill.getId() >= 26046 && itemSkill.getId() <= 26048) {
                    int level = player.getSkillLevel(itemSkill.getId());
                    int newlevel = level;
                    if (level > 0) {
                        if (SkillTable.INSTANCE.getInfo(itemSkill.getId(), level + 1) != null)
                            newlevel = level + 1;
                    } else
                        newlevel = 1;
                    if (newlevel != level) {
                        player.addSkill(SkillTable.INSTANCE.getInfo(itemSkill.getId(), newlevel), false);
                    }
                } else if (player.getSkillLevel(itemSkill.getId()) < itemSkill.getLevel()) {
                    player.addSkill(itemSkill, false);

                    if (itemSkill.isActive()) {
                        long reuseDelay = Formulas.calcSkillReuseDelay(player, itemSkill);
                        reuseDelay = Math.min(reuseDelay, 30000);

                        if (reuseDelay > 0 && !player.isSkillDisabled(itemSkill)) {
                            player.disableSkill(itemSkill, reuseDelay);
                            needSendInfo = true;
                        }
                    }
                }

        if (enchant4Skill != null)
            player.addSkill(enchant4Skill, false);

        if (itemSkills.size() > 0 || enchant4Skill != null) {
            player.sendPacket(new SkillList(player));
            player.updateStats();
            if (needSendInfo)
                player.sendPacket(new SkillCoolTime(player));
        }
    }
}