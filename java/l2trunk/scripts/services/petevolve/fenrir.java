package l2trunk.scripts.services.petevolve;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.PetDataTable.L2Pet;

public final class fenrir extends Functions {
    private static final int GREAT_WOLF = PetDataTable.GREAT_WOLF_ID;
    private static final int GREAT_WOLF_NECKLACE = L2Pet.GREAT_WOLF.getControlItemId();
    private static final int FENRIR_NECKLACE = L2Pet.FENRIR_WOLF.getControlItemId();

    public void evolve() {
        if (player == null || npc == null)
            return;
        if (!player.haveItem(GREAT_WOLF_NECKLACE) ) {
            show("scripts/services/petevolve/no_item.htm", player, npc);
            return;
        }
        Summon pl_pet = player.getPet();
        if (pl_pet == null || pl_pet.isDead()) {
            show("scripts/services/petevolve/evolve_no.htm", player, npc);
            return;
        }
        if (pl_pet.getNpcId() != GREAT_WOLF) {
            show("scripts/services/petevolve/no_wolf.htm", player, npc);
            return;
        }
        if (pl_pet.getLevel() < 70) {
            show("scripts/services/petevolve/no_level_gw.htm", player, npc);
            return;
        }

        int controlItemId = player.getPet().getControlItemObjId();
        player.getPet().unSummon();

        ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
        control.setItemId(FENRIR_NECKLACE);
        control.setEnchantLevel(L2Pet.FENRIR_WOLF.getMinLevel());
        control.setJdbcState(JdbcEntityState.UPDATED);
        control.update();
        player.sendItemList(false);

        show("scripts/services/petevolve/yes_wolf.htm", player, npc);
    }
}