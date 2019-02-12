package l2trunk.scripts.services.petevolve;

import l2trunk.commons.dao.JdbcEntityState;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Summon;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.PetDataTable;
import l2trunk.gameserver.tables.PetDataTable.L2Pet;

public final class ibcougar extends Functions {
    private static final int BABY_COUGAR_CHIME = L2Pet.BABY_COUGAR.getControlItemId();
    private static final int IN_COUGAR_CHIME = L2Pet.IMPROVED_BABY_COUGAR.getControlItemId();

    public void evolve() {
        if (player == null || npc == null)
            return;
        Summon pl_pet = player.getPet();
        if (player.haveItem(BABY_COUGAR_CHIME)) {
            if (pl_pet == null || pl_pet.isDead()) {
                show("scripts/services/petevolve/evolve_no.htm", player, npc);
                return;
            }
            if (pl_pet.getNpcId() != PetDataTable.BABY_COUGAR_ID) {
                show("scripts/services/petevolve/no_pet.htm", player, npc);
                return;
            }
            if (pl_pet.getLevel() < 55) {
                show("scripts/services/petevolve/no_level.htm", player, npc);
                return;
            }

            int controlItemId = player.getPet().getControlItemObjId();
            player.getPet().unSummon();

            ItemInstance control = player.getInventory().getItemByObjectId(controlItemId);
            control.setItemId(IN_COUGAR_CHIME);
            control.setEnchantLevel(L2Pet.IMPROVED_BABY_COUGAR.getMinLevel());
            control.setJdbcState(JdbcEntityState.UPDATED);
            control.update();
            player.sendItemList(false);

            show("scripts/services/petevolve/yes_pet.htm", player, npc);
        } else {
            show("scripts/services/petevolve/no_item.htm", player, npc);
        }
    }
}