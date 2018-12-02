package l2trunk.gameserver.skills.skillclasses;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.geodata.GeoEngine;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.items.Inventory;
import l2trunk.gameserver.model.items.ItemInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.FishTable;
import l2trunk.gameserver.templates.FishTemplate;
import l2trunk.gameserver.templates.StatsSet;
import l2trunk.gameserver.templates.item.WeaponTemplate;
import l2trunk.gameserver.templates.item.WeaponTemplate.WeaponType;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.PositionUtils;

import java.util.ArrayList;
import java.util.List;

public class FishingSkill extends Skill {
    public FishingSkill(StatsSet set) {
        super(set);
    }

    @Override
    public boolean checkCondition(Creature activeChar, Creature target, boolean forceUse, boolean dontMove, boolean first) {
        Player player = (Player) activeChar;

        if (player.getSkillLevel(SKILL_FISHING_MASTERY) == -1)
            return false;

        if (player.isFishing()) {
            player.stopFishing();
            player.sendPacket(SystemMsg.CANCELS_FISHING);
            return false;
        }

        if (player.isInBoat()) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_RIDING_AS_A_PASSENGER_OF_A_BOAT__ITS_AGAINST_THE_RULES);
            return false;
        }

        if (player.getPrivateStoreType() != Player.STORE_PRIVATE_NONE) {
            activeChar.sendPacket(SystemMsg.YOU_CANNOT_FISH_WHILE_USING_A_RECIPE_BOOK_PRIVATE_MANUFACTURE_OR_PRIVATE_STORE);
            return false;
        }

        if (!player.isInZone(ZoneType.FISHING) || player.isInWater()) {
            player.sendPacket(SystemMsg.YOU_CANT_FISH_HERE);
            return false;
        }

        WeaponTemplate weaponItem = player.getActiveWeaponItem();
        if (weaponItem == null || weaponItem.getItemType() != WeaponType.ROD) {
            //Fishing poles are not installed
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_A_FISHING_POLE_EQUIPPED);
            return false;
        }

        ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
        if (lure == null || lure.getCount() < 1) {
            player.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
            return false;
        }

        //Вычисляем координаты поплавка
        int rnd = Rnd.get(50) + 150;
        double angle = PositionUtils.convertHeadingToDegree(player.getHeading());
        double radian = Math.toRadians(angle - 90);
        double sin = Math.sin(radian);
        double cos = Math.cos(radian);
        int x1 = -(int) (sin * rnd);
        int y1 = (int) (cos * rnd);
        int x = player.getX() + x1;
        int y = player.getY() + y1;
        //z - уровень карты
        int z = GeoEngine.getHeight(x, y, player.getZ(), player.getGeoIndex()) + 1;

        //Проверяем, что поплавок оказался в воде
        boolean isInWater = false;
        List<Zone> zones = new ArrayList<>();
        World.getZones(zones, new Location(x, y, z), player.getReflection());
        for (Zone zone : zones)
            if (zone.getType() == ZoneType.water) {
                //z - уровень воды
                z = zone.getTerritory().getZmax();
                isInWater = true;
                break;
            }

        if (!isInWater) {
            player.sendPacket(SystemMsg.YOU_CANT_FISH_HERE);
            return false;
        }

        player.getFishing().setFishLoc(new Location(x, y, z));

        return super.checkCondition(activeChar, target, forceUse, dontMove, first);
    }

    @SuppressWarnings("unused")
    @Override
    public void useSkill(Creature caster, List<Creature> targets) {
        if (caster == null || !caster.isPlayer())
            return;

        Player player = (Player) caster;

        ItemInstance lure = player.getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
        if (lure == null || lure.getCount() < 1) {
            player.sendPacket(SystemMsg.YOU_MUST_PUT_BAIT_ON_YOUR_HOOK_BEFORE_YOU_CAN_FISH);
            return;
        }
        Zone zone = player.getZone(ZoneType.FISHING);
        if (zone == null)
            return;

        int distributionId = zone.getParams().getInteger("distribution_id");

        int lureId = lure.getItemId();

        int group = l2trunk.gameserver.model.Fishing.getFishGroup(lure.getItemId());
        int type = l2trunk.gameserver.model.Fishing.getRandomFishType(lureId);
        int lvl = l2trunk.gameserver.model.Fishing.getRandomFishLvl(player);

        List<FishTemplate> fishs = FishTable.INSTANCE.getFish(group, type, lvl);
        if (fishs == null || fishs.size() == 0) {
            player.sendPacket(SystemMsg.SYSTEM_ERROR);
            return;
        }

        if (!player.getInventory().destroyItemByObjectId(player.getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L, "FishingSkill")) {
            player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_BAIT);
            return;
        }

        int check = Rnd.get(fishs.size());
        FishTemplate fish = fishs.get(check);

        player.startFishing(fish, lureId);
    }
}