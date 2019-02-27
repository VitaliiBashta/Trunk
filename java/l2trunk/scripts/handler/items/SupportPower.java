package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static l2trunk.gameserver.model.base.ClassId.*;

public final class SupportPower extends ScriptItemHandler implements ScriptFile {
    private static final Integer ITEM_IDS = 24001;
    private static final Map<ClassId, Integer> classSkills = Map.of(
            cardinal, 24001,
            hierophant, 24002,
            swordMuse, 24003,
            evaSaint, 24004,
            spectralDancer, 24005,
            shillienSaint, 24006,
            dominator, 24007,
            doomcryer, 24008);

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public List<Integer> getItemIds() {
        return List.of(ITEM_IDS);
    }

    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();
        ClassId classId = player.getBaseClassId();

        if (player.isInOlympiadMode()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
            return false;
        }

        if (player.getLevel() < 76) {
            player.sendMessage("Use only a third profession!");
            return false;
        }

        if (player.getActiveClassId() != player.getBaseClassId()) {
            player.sendMessage("Use only on the main class!");
            return false;
        }
        Integer skillID = classSkills.get(classId);
        if (skillID == null) return false;

        player.addSkill(skillID, false);
        player.updateStats();
        player.sendPacket(new SkillList(player));
        return true;
    }
}