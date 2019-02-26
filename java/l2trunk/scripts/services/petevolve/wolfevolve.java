package l2trunk.scripts.services.petevolve;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.PetDataTable.L2Pet;

public final class wolfevolve extends Functions {
    private static final int WOLF_COLLAR = L2Pet.WOLF.getControlItemId(); // Ошейник Wolf
    private static final int GREAT_WOLF_NECKLACE = L2Pet.GREAT_WOLF.getControlItemId(); // Ожерелье Great Wolf

    public void evolve() {
        if (player == null || npc == null)
            return;
        Summon pl_pet = player.getPet();
        if (!player.haveItem(WOLF_COLLAR)) {
            show("scripts/services/petevolve/no_item.htm", player, npc);
            return;
        }
        if (pl_pet == null || pl_pet.isDead()) {
            show("scripts/services/petevolve/evolve_no.htm", player, npc);
            return;
        }
        if (pl_pet.getNpcId() != PetDataTable.PET_WOLF_ID) {
            show("scripts/services/petevolve/no_wolf.htm", player, npc);
            return;
        }
        if (pl_pet.getLevel() < 55) {
            show("scripts/services/petevolve/no_level.htm", player, npc);
            return;
        }

        int controlItemId = player.getPet().getControlItemObjId();
        player.getPet().unSummon();

        ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
        control.setItemId(GREAT_WOLF_NECKLACE);
        control.setEnchantLevel(L2Pet.GREAT_WOLF.getMinLevel());
        control.setJdbcState(JdbcEntityState.UPDATED);
        control.update();
        player.sendItemList(false);

        show("scripts/services/petevolve/yes_wolf.htm", player, npc);
    }
}