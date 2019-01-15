package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class SupportPower extends ScriptItemHandler implements ScriptFile {
    private static final Integer ITEM_IDS = 24001;
    private static final Map<Integer, Integer> classSkills = Map.of(
            97, 24001,//Cardinal
            98, 24002,//Hierophant
            100, 24003,//SwordMuse
            105, 24004,//EvaSaint
            107, 24005,//SpectralDancer
            112, 24006,//ShillienSaint
            115, 24007,//Dominator
            116, 24008);//Doomcryer

    @Override
    public boolean pickupItem(Playable playable, ItemInstance item) {
        return true;
    }

    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public List<Integer> getItemIds() {
        return Collections.singletonList(ITEM_IDS);
    }

    public boolean useItem(Playable playable, ItemInstance item, boolean ctrl) {
        if (playable == null || !playable.isPlayer())
            return false;

        Player player = playable.getPlayer();

        int itemId = item.getItemId();
        int classId = player.getBaseClassId();

        if (player.isInOlympiadMode()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_CANNOT_BE_USED_DUE_TO_UNSUITABLE_TERMS).addItemName(itemId));
            return false;
        }

        if (player.getLevel() < 76) {
            player.sendMessage("Use only a third profession!");
            player.sendMessage("Use only on the main class!");
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