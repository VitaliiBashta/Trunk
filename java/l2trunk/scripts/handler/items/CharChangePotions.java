package l2trunk.scripts.handler.items;

import l2trunk.gameserver.handler.items.ItemHandler;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

public final class CharChangePotions extends ScriptItemHandler implements ScriptFile {
    private static final List<Integer> ITEM_IDS = List.of(5235, 5236, 5237, // Face
            5238,
            5239,
            5240,
            5241, // Hair Color
            5242,
            5243,
            5244,
            5245,
            5246,
            5247,
            5248); // Hair Style


    @Override
    public void onLoad() {
        ItemHandler.INSTANCE.registerItemHandler(this);
    }

    @Override
    public boolean useItem(Player player, ItemInstance item, boolean ctrl) {
        int itemId = item.getItemId();

        if (!player.getInventory().destroyItem(item, 1, "CharChangePotions")) {
            player.sendActionFailed();
            return false;
        }

        int face = player.getFace();
        int hairStyle = player.getHairStyle();
        int hairColor = player.getHairColor();
        switch (itemId) {
            case 5235:
                player.setFace(0);
                break;
            case 5236:
                player.setFace(1);
                break;
            case 5237:
                player.setFace(2);
                break;
            case 5238:
                player.setHairColor(0);
                break;
            case 5239:
                player.setHairColor(1);
                break;
            case 5240:
                player.setHairColor(2);
                break;
            case 5241:
                player.setHairColor(3);
                break;
            case 5242:
                player.setHairStyle(0);
                break;
            case 5243:
                player.setHairStyle(1);
                break;
            case 5244:
                player.setHairStyle(2);
                break;
            case 5245:
                player.setHairStyle(3);
                break;
            case 5246:
                player.setHairStyle(4);
                break;
            case 5247:
                player.setHairStyle(5);
                break;
            case 5248:
                player.setHairStyle(6);
                break;
        }

        player.broadcastPacket(new MagicSkillUse(player, 2003));
        if (face != player.getFace() || hairColor != player.getHairColor() || hairStyle != player.getHairStyle())
            player.broadcastUserInfo(true);
        return true;
    }

    @Override
    public final List<Integer> getItemIds() {
        return ITEM_IDS;
    }
}