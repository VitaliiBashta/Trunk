package l2trunk.scripts.npc.model.residences.castle;

import l2trunk.gameserver.dao.CastleDamageZoneDAO;
import l2trunk.gameserver.dao.CastleDoorUpgradeDAO;
import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.instancemanager.CastleManorManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.events.impl.CastleSiegeEvent;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.events.objects.CastleDamageZoneObject;
import l2trunk.gameserver.model.entity.events.objects.DoorObject;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.model.entity.residence.Fortress;
import l2trunk.gameserver.model.entity.residence.Residence;
import l2trunk.gameserver.model.instances.DoorInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.Privilege;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.HtmlUtils;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.scripts.npc.model.residences.ResidenceManager;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.commons.lang.NumberUtils.toLong;

public final class ChamberlainInstance extends ResidenceManager {

    public ChamberlainInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    private static long modifyPrice(long price) {
        int i = SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_STRIFE);
        if (i == SevenSigns.CABAL_DUSK) price *= 3;
        if (i == SevenSigns.CABAL_DAWN) price *= (long) 0.8;
        return price;
    }

    @Override
    protected void setDialogs() {
        _mainDialog = "castle/chamberlain/chamberlain.htm";
        _failDialog = "castle/chamberlain/chamberlain-notlord.htm";
        _siegeDialog = _mainDialog;
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        int condition = getCond(player);
        if (condition != COND_OWNER)
            return;

        StringTokenizer st = new StringTokenizer(command, " ");
        String actualCommand = st.nextToken();
        String val = "";
        if (st.countTokens() >= 1)
            val = st.nextToken();

        Castle castle = getCastle();
        if ("viewSiegeInfo".equalsIgnoreCase(actualCommand)) {
            if (!isHaveRigths(player, Clan.CP_CS_MANAGE_SIEGE)) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            player.sendPacket(new CastleSiegeInfo(castle, player));
        } else if ("ManageTreasure".equalsIgnoreCase(actualCommand)) {
            if (!player.isClanLeader()) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/chamberlain-castlevault.htm");
            html.replace("%Treasure%", castle.getTreasury());
            html.replace("%CollectedShops%", castle.getCollectedShops());
            html.replace("%CollectedSeed%", castle.getCollectedSeed());
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("TakeTreasure")) {
            if (!player.isClanLeader()) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            if (!"".equals(val)) {
                long treasure = toLong(val);
                if (castle.getTreasury() < treasure) {
                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setFile("castle/chamberlain/chamberlain-havenottreasure.htm");
                    html.replace("%Treasure%", castle.getTreasury());
                    html.replace("%Requested%", treasure);
                    player.sendPacket(html);
                    return;
                }
                if (treasure > 0) {
                    castle.addToTreasuryNoTax(-treasure, false, false);
                    player.addAdena(treasure, "ChamberlainTakeTreasure");
                }
            }

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/chamberlain-castlevault.htm");
            html.replace("%Treasure%", castle.getTreasury());
            html.replace("%CollectedShops%", castle.getCollectedShops());
            html.replace("%CollectedSeed%", castle.getCollectedSeed());
            player.sendPacket(html);
        } else if ("PutTreasure".equalsIgnoreCase(actualCommand)) {
            if (!val.equals("")) {
                long treasure = toLong(val);
                if (treasure > player.getAdena()) {
                    player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                    return;
                }
                if (treasure > 0) {
                    castle.addToTreasuryNoTax(treasure, false, false);
                    player.reduceAdena(treasure, true, "ChamberlainPutTreasure");
                }
            }

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/chamberlain-castlevault.htm");
            html.replace("%Treasure%", castle.getTreasury());
            html.replace("%CollectedShops%", castle.getCollectedShops());
            html.replace("%CollectedSeed%", castle.getCollectedSeed());
            player.sendPacket(html);
        } else if ("manor".equalsIgnoreCase(actualCommand)) {
            if (!isHaveRigths(player, Clan.CP_CS_MANOR_ADMIN)) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            String filename;
            if (CastleManorManager.INSTANCE.isDisabled())
                filename = "npcdefault.htm";
            else {
                int cmd = toInt(val);
                switch (cmd) {
                    case 0:
                        filename = "castle/chamberlain/manor/manor.htm";
                        break;
                    // TODO: correct in html's to 1
                    case 4:
                        filename = "castle/chamberlain/manor/manor_help00" + st.nextToken() + ".htm";
                        break;
                    default:
                        filename = "castle/chamberlain/chamberlain-no.htm";
                        break;
                }
            }

            if (filename.length() > 0) {
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile(filename);
                player.sendPacket(html);
            }
        } else if (actualCommand.startsWith("manor_menu_select")) {
            if (!isHaveRigths(player, Clan.CP_CS_MANOR_ADMIN)) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            // input string format:
            // manor_menu_select?ask=X&state=Y&time=X
            if (CastleManorManager.INSTANCE.isUnderMaintenance()) {
                player.sendPacket(SystemMsg.THE_MANOR_SYSTEM_IS_CURRENTLY_UNDER_MAINTENANCE);
                player.sendActionFailed();
                return;
            }

            String params = actualCommand.substring(actualCommand.indexOf("?") + 1);
            StringTokenizer str = new StringTokenizer(params, "&");
            int ask = toInt(str.nextToken().split("=")[1]);
            int state = toInt(str.nextToken().split("=")[1]);
            int time = toInt(str.nextToken().split("=")[1]);

            int castleId;
            if (state == -1) // info for current manor
                castleId = castle.getId();
            else
                // info for requested manor
                castleId = state;

            switch (ask) { // Main action
                case 3: // Current seeds (Manor info)
                    if (time == 1 && !ResidenceHolder.getResidence(Castle.class, castleId).isNextPeriodApproved())
                        player.sendPacket(new ExShowSeedInfo(castleId, List.of()));
                    else
                        player.sendPacket(new ExShowSeedInfo(castleId, ResidenceHolder.getResidence(Castle.class, castleId).getSeedProduction(time)));
                    break;
                case 4: // Current crops (Manor info)
                    if (time == 1 && !ResidenceHolder.getResidence(Castle.class, castleId).isNextPeriodApproved())
                        player.sendPacket(new ExShowCropInfo(castleId, List.of()));
                    else
                        player.sendPacket(new ExShowCropInfo(castleId, ResidenceHolder.getResidence(Castle.class, castleId).getCropProcure(time)));
                    break;
                case 5: // Basic info (Manor info)
                    player.sendPacket(new ExShowManorDefaultInfo());
                    break;
                case 7: // Edit seed setup
                    if (castle.isNextPeriodApproved())
                        player.sendPacket(SystemMsg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM);
                    else
                        player.sendPacket(new ExShowSeedSetting(castle.getId()));
                    break;
                case 8: // Edit crop setup
                    if (castle.isNextPeriodApproved())
                        player.sendPacket(SystemMsg.A_MANOR_CANNOT_BE_SET_UP_BETWEEN_430_AM_AND_8_PM);
                    else
                        player.sendPacket(new ExShowCropSetting(castle.getId()));
                    break;
            }
        } else if ("operate_door".equalsIgnoreCase(actualCommand)) // door control
        {
            if (!isHaveRigths(player, Clan.CP_CS_ENTRY_EXIT)) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            if (castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress()) {
                showChatWindow(player, "residence2/castle/chamberlain_saius021.htm");
                return;
            }
            if (!val.equals("")) {
                boolean open = toInt(val) == 1;
                while (st.hasMoreTokens()) {
                    DoorInstance door = ReflectionUtils.getDoor(toInt(st.nextToken()));
                    if (open)
                        door.openMe();
                    else
                        door.closeMe();
                }
            }

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/" + getTemplate().npcId + "-d.htm");
            player.sendPacket(html);
        } else if ("tax_set".equalsIgnoreCase(actualCommand)) // tax rates control
        {
            if (!isHaveRigths(player, Clan.CP_CS_TAXES)) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            if (!val.equals("")) {
                // По умолчанию налог не более 15%
                int maxTax = 15;
                // Если печатью SEAL_STRIFE владеют DUSK то налог можно выставлять не более 5%
                if (SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DUSK)
                    maxTax = 5;
                    // Если печатью SEAL_STRIFE владеют DAWN то налог можно выставлять не более 25%
                else if (SevenSigns.INSTANCE.getSealOwner(SevenSigns.SEAL_STRIFE) == SevenSigns.CABAL_DAWN)
                    maxTax = 25;

                int tax = toInt(val);
                if (tax < 0 || tax > maxTax) {
                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setFile("castle/chamberlain/chamberlain-hightax.htm");
                    html.replace("%CurrentTax%", castle.getTaxPercent());
                    player.sendPacket(html);
                    return;
                }
                castle.setTaxPercent(player, tax);
            }

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/chamberlain-settax.htm");
            html.replace("%CurrentTax%", castle.getTaxPercent());
            player.sendPacket(html);
        } else if (actualCommand.equalsIgnoreCase("upgrade_castle")) {
            if (!checkSiegeFunctions(player))
                return;

            showChatWindow(player, "castle/chamberlain/chamberlain-upgrades.htm");
        } else if (actualCommand.equalsIgnoreCase("reinforce")) {
            if (!checkSiegeFunctions(player))
                return;

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/doorStrengthen-" + castle.getName() + ".htm");
            player.sendPacket(html);
        } else if ("trap_select".equalsIgnoreCase(actualCommand)) {
            if (!checkSiegeFunctions(player))
                return;

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/trap_select-" + castle.getName() + ".htm");
            player.sendPacket(html);
        } else if ("buy_trap".equalsIgnoreCase(actualCommand)) {
            if (!checkSiegeFunctions(player))
                return;

            if (castle.getSiegeEvent().getObjects(CastleSiegeEvent.BOUGHT_ZONES).contains(val)) {
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile("castle/chamberlain/trapAlready.htm");
                player.sendPacket(html);
                return;
            }

            List<CastleDamageZoneObject> objects = castle.getSiegeEvent().getObjects(val);
            long price = modifyPrice(objects.stream()
                    .mapToLong(CastleDamageZoneObject::getPrice).sum());


            if (player.getClan().getAdenaCount() < price) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }

            player.getClan().getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, price, "ChamberlainBuyTrap");
            castle.getSiegeEvent().addObject(CastleSiegeEvent.BOUGHT_ZONES, val);
            CastleDamageZoneDAO.INSTANCE.insert(castle, val);

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/trapSuccess.htm");
            player.sendPacket(html);
        } else if ("door_manage".equalsIgnoreCase(actualCommand)) {
            if (!isHaveRigths(player, Clan.CP_CS_ENTRY_EXIT)) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            if (castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress()) {
                showChatWindow(player, "residence2/castle/chamberlain_saius021.htm");
                return;
            }

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/doorManage.htm");
            html.replace("%id%", val);
            html.replace("%type%", st.nextToken());
            player.sendPacket(html);
        } else if ("upgrade_door_confirm".equalsIgnoreCase(actualCommand)) {
            if (!isHaveRigths(player, Clan.CP_CS_MANAGE_SIEGE)) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            int id = toInt(val);
            int type = toInt(st.nextToken());
            int level = toInt(st.nextToken());
            long price = getDoorCost(type, level);

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/doorConfirm.htm");
            html.replace("%id%", id);
            html.replace("%occupation%", level);
            html.replace("%type%", type);
            html.replace("%price%", price);
            player.sendPacket(html);
        } else if ("upgrade_door".equalsIgnoreCase(actualCommand)) {
            if (checkSiegeFunctions(player))
                return;

            int id = toInt(val);
            int type = toInt(st.nextToken());
            int level = toInt(st.nextToken());
            long price = getDoorCost(type, level);

            List<DoorObject> doorObjects = castle.getSiegeEvent().getObjects(SiegeEvent.DOORS);
            DoorObject targetDoorObject = doorObjects.stream()
                    .filter(o -> o.getUId() == id)
                    .findFirst().orElse(null);

            DoorInstance door = targetDoorObject.getDoor();
            int upgradeHp = (door.getMaxHp() - door.getUpgradeHp()) * level - door.getMaxHp();

            if (price == 0 || upgradeHp < 0) {
                player.sendMessage(new CustomMessage("common.Error"));
                return;
            }

            if (door.getUpgradeHp() >= upgradeHp) {
                int oldLevel = door.getUpgradeHp() / (door.getMaxHp() - door.getUpgradeHp()) + 1;
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile("castle/chamberlain/doorAlready.htm");
                html.replace("%level%", oldLevel);
                player.sendPacket(html);
                return;
            }

            if (player.getClan().getAdenaCount() < price) {
                player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                return;
            }

            player.getClan().getWarehouse().destroyItemByItemId(ItemTemplate.ITEM_ID_ADENA, price, "UpgradeDoor");

            targetDoorObject.setUpgradeValue(castle.getSiegeEvent(), upgradeHp);
            CastleDoorUpgradeDAO.INSTANCE.insert(door.getDoorId(), upgradeHp);
        } else if ("report".equalsIgnoreCase(actualCommand)) // Report page
        {
            if (!isHaveRigths(player, Clan.CP_CS_USE_FUNCTIONS)) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            NpcString ssq_period;
            if (SevenSigns.INSTANCE.getCurrentPeriod() == 1)
                ssq_period = NpcString.COMPETITION;
            else if (SevenSigns.INSTANCE.getCurrentPeriod() == 3)
                ssq_period = NpcString.SEAL_VALIDATION;
            else
                ssq_period = NpcString.PREPARATION;

            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/chamberlain-report.htm");
            html.replaceNpcString("%FeudName%", castle.getNpcStringName());
            html.replace("%CharClan%", player.getClan().getName());
            html.replace("%CharName%", player.getName());
            html.replaceNpcString("%SSPeriod%", ssq_period);
            html.replaceNpcString("%Avarice%", getSealOwner(1));
            html.replaceNpcString("%Revelation%", getSealOwner(2));
            html.replaceNpcString("%Strife%", getSealOwner(3));
            player.sendPacket(html);
        } else if ("fortressStatus".equalsIgnoreCase(actualCommand)) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/chamberlain-fortress-status.htm");
            StringBuilder b = new StringBuilder(100);

            for (Map.Entry<Integer, List<Integer>> entry : castle.getRelatedFortresses().entrySet()) {
                NpcString type;
                switch (entry.getKey()) {
                    case Fortress.DOMAIN:
                        type = NpcString.DOMAIN_FORTRESS;
                        break;
                    case Fortress.BOUNDARY:
                        type = NpcString.BOUNDARY_FORTRESS;
                        break;
                    default:
                        continue;
                }
                List<Integer> fortresses = entry.getValue();
                for (int fort : fortresses) {
                    b.append(HtmlUtils.htmlResidenceName(fort)).append(" (").append(HtmlUtils.htmlNpcString(type)).append(") : <font color=\"00FFFF\">");

                    b.append(HtmlUtils.htmlNpcString(0)).append("</font> <br>");
                }
            }
            html.replace("%list%", b);
            player.sendPacket(html);
        } else if ("Crown".equalsIgnoreCase(actualCommand)) // Give Crown to Castle Owner
        {
            if (!player.isClanLeader()) {
                player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
                return;
            }
            if (!player.haveItem(6841)) {
                player.getInventory().addItem(ItemFunctions.createItem(6841), "GettingCrown");

                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile("castle/chamberlain/chamberlain-givecrown.htm");
                html.replace("%CharName%", player.getName());
                html.replaceNpcString("%FeudName%", castle.getNpcStringName());
                player.sendPacket(html);
            } else {
                NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                html.setFile("castle/chamberlain/alreadyhavecrown.htm");
                player.sendPacket(html);
            }
        } else if ("viewTerritoryWarInfo".equalsIgnoreCase(actualCommand)) {
            player.sendPacket(new ExShowDominionRegistry(player, castle.getDominion()));
        } else if ("manageFunctions".equalsIgnoreCase(actualCommand)) {
            if (!player.hasPrivilege(Privilege.CS_FS_SET_FUNCTIONS))
                showChatWindow(player, "residence2/castle/chamberlain_saius063.htm");
            else
                showChatWindow(player, "residence2/castle/chamberlain_saius065.htm");
        } else if ("manageSiegeFunctions".equalsIgnoreCase(actualCommand)) {
            if (!player.hasPrivilege(Privilege.CS_FS_SET_FUNCTIONS))
                showChatWindow(player, "residence2/castle/chamberlain_saius063.htm");
            else if (castle.getDomainFortressContract() == 0)
                showChatWindow(player, "residence2/castle/chamberlain_saius069.htm");
            else if (SevenSigns.INSTANCE.getCurrentPeriod() != SevenSigns.PERIOD_SEAL_VALIDATION)
                showChatWindow(player, "residence2/castle/chamberlain_saius068.htm");
            else
                showChatWindow(player, "residence2/castle/chamberlain_saius052.htm");
        } else if ("items".equalsIgnoreCase(actualCommand)) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("residence2/castle/chamberlain_saius064.htm");
            html.replace("%npcId%", getNpcId());
            player.sendPacket(html);
        } else if ("default".equalsIgnoreCase(actualCommand)) {
            NpcHtmlMessage html = new NpcHtmlMessage(player, this);
            html.setFile("castle/chamberlain/chamberlain.htm");
            player.sendPacket(html);
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    protected int getCond(Player player) {
        if (player.isGM())
            return COND_OWNER;
        Residence castle = getCastle();
        if (castle != null && castle.getId() > 0)
            if (player.getClan() != null)
                if (castle.getSiegeEvent().isInProgress())
                    return COND_SIEGE; // Busy because of siege
                else if (castle.getOwnerId() == player.getClanId()) {
                    if (player.isClanLeader()) // Leader of clan
                        return COND_OWNER;
                    if (isHaveRigths(player, Clan.CP_CS_ENTRY_EXIT) || // doors
                            isHaveRigths(player, Clan.CP_CS_MANOR_ADMIN) || // manor
                            isHaveRigths(player, Clan.CP_CS_MANAGE_SIEGE) || // siege
                            isHaveRigths(player, Clan.CP_CS_USE_FUNCTIONS) || // funcs
                            isHaveRigths(player, Clan.CP_CS_DISMISS) || // banish
                            isHaveRigths(player, Clan.CP_CS_TAXES) || // tax
                            isHaveRigths(player, Clan.CP_CS_MERCENARIES) || // merc
                            isHaveRigths(player, Clan.CP_CS_SET_FUNCTIONS) //funcs
                    )
                        return COND_OWNER; // Есть какие либо замковые привилегии
                }

        return COND_FAIL;
    }

    private NpcString getSealOwner(int seal) {
        switch (SevenSigns.INSTANCE.getSealOwner(seal)) {
            case SevenSigns.CABAL_DUSK:
                return NpcString.DUSK;
            case SevenSigns.CABAL_DAWN:
                return NpcString.DAWN;
            default:
                return NpcString.NO_OWNER;
        }
    }

    private long getDoorCost(int type, int level) {
        int price = 0;

        switch (type) {
            case 1: // Главные ворота
                switch (level) {
                    case 2:
                        price = 3000000;
                        break;
                    case 3:
                        price = 4000000;
                        break;
                    case 5:
                        price = 5000000;
                        break;
                }
                break;
            case 2: // Внутренние ворота
                switch (level) {
                    case 2:
                        price = 750000;
                        break;
                    case 3:
                        price = 900000;
                        break;
                    case 5:
                        price = 1000000;
                        break;
                }
                break;
            case 3: // Стены
                switch (level) {
                    case 2:
                        price = 1600000;
                        break;
                    case 3:
                        price = 1800000;
                        break;
                    case 5:
                        price = 2000000;
                        break;
                }
                break;
        }

        return modifyPrice(price);
    }

    @Override
    protected Residence getResidence() {
        return getCastle();
    }

    @Override
    public L2GameServerPacket decoPacket() {
        return null;
    }

    @Override
    protected int getPrivUseFunctions() {
        return Clan.CP_CS_USE_FUNCTIONS;
    }

    @Override
    protected int getPrivSetFunctions() {
        return Clan.CP_CS_SET_FUNCTIONS;
    }

    @Override
    protected int getPrivDismiss() {
        return Clan.CP_CS_DISMISS;
    }

    @Override
    protected int getPrivDoors() {
        return Clan.CP_CS_ENTRY_EXIT;
    }

    private boolean checkSiegeFunctions(Player player) {
        Castle castle = getCastle();
        if (!player.hasPrivilege(Privilege.CS_FS_SIEGE_WAR)) {
            player.sendPacket(SystemMsg.YOU_ARE_NOT_AUTHORIZED_TO_DO_THAT);
            return false;
        }

        if (castle.getSiegeEvent().isInProgress() || castle.getDominion().getSiegeEvent().isInProgress()) {
            showChatWindow(player, "residence2/castle/chamberlain_saius021.htm");
            return false;
        }
        return true;
    }
}