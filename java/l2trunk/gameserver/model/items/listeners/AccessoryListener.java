package l2trunk.gameserver.model.items.listeners;

import l2trunk.gameserver.listener.inventory.OnEquipListener;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.Skill.SkillType;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.templates.item.ItemTemplate;

public final class AccessoryListener implements OnEquipListener {
    private static final AccessoryListener _instance = new AccessoryListener();

    public static AccessoryListener getInstance() {
        return _instance;
    }

    @Override
    public void onUnequip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable())
            return;

        Player player = (Player) actor;

        if (item.getBodyPart() == ItemTemplate.SLOT_L_BRACELET && item.getTemplate().getAttachedSkills().size() > 0) {
            int agathionId = player.getAgathionId();
            int transformNpcId = player.getTransformationTemplate();
            for (Skill skill : item.getTemplate().getAttachedSkills()) {
                if (agathionId > 0 && skill.npcId == agathionId)
                    player.setAgathion(0);
                if (skill.npcId == transformNpcId && skill.skillType == SkillType.TRANSFORMATION)
                    player.setTransformation(0);
            }
        }

        if (item.isAccessory() || item.getTemplate().isTalisman() || item.getTemplate().isBracelet())
            player.sendUserInfo(true);
            // TODO [G1ta0] отладить отображение аксессуаров
            //getPlayer.sendPacket(new ItemList(getPlayer, false));
        else
            player.broadcastCharInfo();
    }

    @Override
    public void onEquip(int slot, ItemInstance item, Playable actor) {
        if (!item.isEquipable())
            return;

        Player player = (Player) actor;

        if (item.isAccessory() || item.getTemplate().isTalisman() || item.getTemplate().isBracelet())
            player.sendUserInfo(true);
            // TODO [G1ta0] отладить отображение аксессуаров
            //getPlayer.sendPacket(new ItemList(getPlayer, false));
        else
            player.broadcastCharInfo();
    }
}