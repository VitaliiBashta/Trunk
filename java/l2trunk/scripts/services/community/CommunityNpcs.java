package l2trunk.scripts.services.community;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.ImagesCache;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.database.DatabaseFactory;
import l2trunk.gameserver.handler.bbs.CommunityBoardManager;
import l2trunk.gameserver.handler.bbs.ICommunityBoardHandler;
import l2trunk.gameserver.instancemanager.RaidBossSpawnManager;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.listener.actor.player.OnAnswerListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SubClass;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.instances.SchemeBufferInstance;
import l2trunk.gameserver.model.instances.VillageMasterInstance;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.taskmanager.AutoImageSenderManager;
import l2trunk.gameserver.templates.item.ItemTemplate;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.Util;
import l2trunk.scripts.bosses.AntharasManager;
import l2trunk.scripts.bosses.BaiumManager;
import l2trunk.scripts.bosses.ValakasManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class CommunityNpcs implements ScriptFile, ICommunityBoardHandler {
    private static final Logger _log = LoggerFactory.getLogger(CommunityNpcs.class);
    private static final int FIELDS_IN_SUB_SELECT_PAGE = 11;
    private static final int[] PKS =
            {
                    1,
                    5,
                    10,
                    50,
                    100 //, 250, 500, 1000
            };

    private static void changeClanName(Player player, String newName) {
        if (player == null)
            return;

        if (!Config.SERVICES_CHANGE_CLAN_NAME_ENABLED) {
            player.sendMessage("Service is disabled.");
            return;
        }

        if (player.getClan() == null || !player.isClanLeader()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
            return;
        }

        if (player.getEvent(SiegeEvent.class) != null) {
            player.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow"));
            return;
        }

        if (!Util.isMatchingRegexp(newName, Config.CLAN_NAME_TEMPLATE)) {
            player.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
            return;
        }

        if (ClanTable.INSTANCE.getClanByName(newName) != null) {
            player.sendPacket(Msg.THIS_NAME_ALREADY_EXISTS);
            return;
        }

        if (player.getInventory().getCountOf(Config.SERVICES_CHANGE_CLAN_NAME_ITEM) < Config.SERVICES_CHANGE_CLAN_NAME_PRICE) {
            player.sendMessage("Incorrect Coin Count. You need " + Config.SERVICES_CHANGE_CLAN_NAME_PRICE + ". You have " + player.getInventory().getCountOf(Config.SERVICES_CHANGE_CLAN_NAME_ITEM));
            return;
        }

        player.getInventory().destroyItemByItemId(Config.SERVICES_CHANGE_CLAN_NAME_ITEM, Config.SERVICES_CHANGE_CLAN_NAME_PRICE, "Clan Name Change");

        final String oldName = player.getClan().getName();
        Log.add("Clan " + oldName + " renamed to " + newName, "renames");
        player.sendMessage("You have changed your clan's name from " + oldName + " to " + newName);
        player.sendPacket(new HideBoard());

        SubUnit sub = player.getClan().getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
        sub.setName(newName, true);
        player.getClan().broadcastClanStatus(true, true, false);
    }

    private static void sendFileToPlayer(Player player, String path, boolean sendImages, String... replacements) {
        String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + path, player);

        if (sendImages)
            ImagesCache.sendUsedImages(html, player);

        for (int i = 0; i < replacements.length; i += 2) {
            String toReplace = replacements[i + 1];
            if (toReplace == null)
                toReplace = "<br>";
            html = html.replace(replacements[i], toReplace);
        }

        ShowBoard.separateAndSend(html, player);
    }

    private static void addNewSubPage(Player player, String raceName) {
        Race race = Race.valueOf(raceName);
        Set<ClassId> allSubs = VillageMasterInstance.getAllAvailableSubs(player, true);
        allSubs = getSubsByRace(allSubs, race);

        ClassId[] arraySubs = new ClassId[allSubs.size()];
        arraySubs = allSubs.toArray(arraySubs);

        String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
        for (int i = 0; i < FIELDS_IN_SUB_SELECT_PAGE; i++) {
            replacements[i * 2] = "%sub" + i + '%';
            if (arraySubs.length <= i)
                replacements[i * 2 + 1] = "<br>";
            else {
                ClassId playerClass = arraySubs[i];
                replacements[i * 2 + 1] = "<button value=\"Add " + playerClass.name() + "\" action=\"bypass _bbsAddNewSub_" + playerClass.id + "\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Fight1None_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Fight1None\">";
            }
        }
        sendFileToPlayer(player, "smallNpcs/subclassChanger_select.htm", true, replacements);
    }

    private static void changeSubPage(Player player) {
        Collection<SubClass> allSubs = player.getSubClasses().values();
        List<SubClass> availableSubs = new ArrayList<>();
        for (SubClass sub : allSubs)
            if (sub.getClassId() != player.getActiveClassId())
                availableSubs.add(sub);

        String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
        for (int i = 0; i < FIELDS_IN_SUB_SELECT_PAGE; i++) {
            replacements[i * 2] = "%sub" + i + '%';
            if (availableSubs.size() <= i)
                replacements[i * 2 + 1] = "<br>";
            else {
                SubClass playerClass = availableSubs.get(i);
                replacements[i * 2 + 1] = "<button value=\"Change To " + playerClass.getClassId().name + "\" action=\"bypass _bbsChangeSubTo_" + playerClass.getClassId() + "\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Fight1None_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Fight1None\">";
            }
        }
        sendFileToPlayer(player, "smallNpcs/subclassChanger_select.htm", true, replacements);
    }

    private static void cancelSubPage(Player player) {
        List<SubClass> subToChoose = player.getSubClasses().values().stream()
                .filter(sub -> !sub.isBase())
                .collect(Collectors.toList());

        String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
        for (int i = 0; i < FIELDS_IN_SUB_SELECT_PAGE; i++) {
            replacements[i * 2] = "%sub" + i + '%';
            if (subToChoose.size() <= i)
                replacements[i * 2 + 1] = "<br>";
            else {
                SubClass playerClass = subToChoose.get(i);
                replacements[i * 2 + 1] = "<button value=\"Remove " + playerClass.getClassId().name + "\" action=\"bypass _bbsSelectCancelSub_" + playerClass.getClassId() + "\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Fight1None_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Fight1None\">";
            }
        }
        sendFileToPlayer(player, "smallNpcs/subclassChanger_select.htm", true, replacements);
    }

    private static void changeSub(Player player, ClassId subId) {
        if (!canChangeClass(player))
            return;

        player.setActiveSubClass(subId, true);

        player.sendPacket(SystemMsg.YOU_HAVE_SUCCESSFULLY_SWITCHED_TO_YOUR_SUBCLASS);
        player.sendPacket(new HideBoard());
    }

    private static void addNewSub(Player player, ClassId subclassId) {
        if (!canChangeClass(player))
            return;

        ClassId subToRemove = ClassId.getById(player.getQuickVarI("SubToRemove"));
        boolean added;
        if (subToRemove != null) {
            added = player.modifySubClass(subToRemove, subclassId);
        } else
            added = VillageMasterInstance.addNewSubclass(player, subclassId);

        if (added) {
            player.sendMessage("Subclass was added!");
        } else {
            player.sendMessage("Subclass couldn't be added!");
        }
        player.sendPacket(new HideBoard());
    }

    private static Set<ClassId> getSubsByRace(Set<ClassId> allSubs, Race race) {
        for (ClassId sub : allSubs)
            if (sub.race != race)
                allSubs.remove(sub);
        return allSubs;
    }

    private static void chooseCertificatePage(Player player) {
        if (!canChangeClass(player))
            return;

        if (player.getBaseClassId() == player.getClassId()) {
            sendFileToPlayer(player, "smallNpcs/subclassChanger_back.htm", true);
            return;
        }

        String[][] certifications = {
                {"Level 65 Emergent", "CommunityCert65"},
                {"Level 70 Emergent", "CommunityCert70"},
                {"Level 75 Class Specific", "CommunityCert75Class"},
                {"Level 75 Master", "CommunityCert75Master"},
                {"Level 80 Divine", "CommunityCert80"}};

        String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
        for (int i = 0; i < FIELDS_IN_SUB_SELECT_PAGE; i++) {
            replacements[i * 2] = "%sub" + i + '%';
            if (certifications.length <= i)
                replacements[i * 2 + 1] = "<br>";
            else {
                String[] button = certifications[i];
                replacements[i * 2 + 1] = "<button value=\"Add " + button[0] + "\" action=\"bypass _bbsscripts:Util:" + button[1] + "\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_Fight1None_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_Fight1None\">";
            }
        }
        sendFileToPlayer(player, "smallNpcs/subclassChanger_select.htm", true, replacements);
    }

    private static String getDecreasePkButton(int pks) {
        return "<button value=\"" + pks + " PKs for " + (Config.SERVICES_WASH_PK_PRICE * pks) + " Coins\" action=\"bypass _decreasePK_" + pks + "\" width=200 height=30 back=\"L2UI_CT1.OlympiadWnd_DF_HeroConfirm_Down\" fore=\"L2UI_ct1.OlympiadWnd_DF_HeroConfirm\">";
    }

    private static boolean canChangeClass(Player player) {
        if (player.getPet() != null) {
            player.sendPacket(SystemMsg.A_SUBCLASS_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SERVITOR_OR_PET_IS_SUMMONED);
            return false;
        }

        // Sub class can not be obtained or changed while using the skill or character is in transformation
        if (player.isActionsDisabled() || player.isTrasformed()) {
            player.sendPacket(SystemMsg.SUBCLASSES_MAY_NOT_BE_CREATED_OR_CHANGED_WHILE_A_SKILL_IS_IN_USE);
            return false;
        }

        if (!player.isInPeaceZone()) {
            player.sendMessage("You cannot change Subclass right now!");
            return false;
        }
        if (Olympiad.isRegisteredInComp(player)) {
            player.sendPacket(Msg.YOU_CANT_JOIN_THE_OLYMPIAD_WITH_A_SUB_JOB_CHARACTER);
            return false;

        }

        return true;
    }

    public static String convertRespawnDate(long date) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(date);
        String appendHours;
        String appendMinutes;
        String appendHours2;

        /* Temp. appends */
        if (c.get(Calendar.HOUR_OF_DAY) > 10)
            appendHours = "" + (c.get(Calendar.HOUR_OF_DAY) + 1);
        else
            appendHours = "0" + (c.get(Calendar.HOUR_OF_DAY) + 1);

        if (c.get(Calendar.MINUTE) > 10)
            appendMinutes = "" + c.get(Calendar.MINUTE);
        else
            appendMinutes = "0" + c.get(Calendar.MINUTE);

        if (c.get(Calendar.HOUR_OF_DAY) > 10)
            appendHours2 = "" + (c.get(Calendar.HOUR_OF_DAY) - 1);
        else
            appendHours2 = "0" + (c.get(Calendar.HOUR_OF_DAY) - 1);

        if (date > 0)
            return c.get(Calendar.DAY_OF_MONTH) + "." + (c.get(Calendar.MONTH) + 1) + "." + c.get(Calendar.YEAR) + " " + appendHours2 + ":" + appendMinutes + " - " + appendHours + ":" + appendMinutes;
        else
            return "Alive";
    }

    private static long bossRespawn(int id) {
        long respawnTime = 0;

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT `respawn_delay` FROM `raidboss_status` WHERE `id` = ?")) {
            statement.setInt(1, id);
            ResultSet rset = statement.executeQuery();

            if (rset.next())
                respawnTime = rset.getLong("respawn_delay");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return respawnTime;
    }

    @Override
    public void onLoad() {
        if (Config.COMMUNITYBOARD_ENABLED) {
            _log.info("CommunityBoard: Npcs loaded.");
            CommunityBoardManager.registerHandler(this);
        }
    }

    @Override
    public void onReload() {
        if (Config.COMMUNITYBOARD_ENABLED)
            CommunityBoardManager.removeHandler(this);
    }

    @Override
    public List<String> getBypassCommands() {
        return List.of(
                "_bbsgetfav",
                "_bbsnpcs",
                "_bbsgatekeeper",
                "_bbsbuffer",
                "_bbsbufferbypass",
                "_bbsNewSubPage",
                "_bbsAddNewSub",
                "_changeSubPage",
                "_bbsChangeSubTo",
                "_bbsCancelSubPage",
                "_bbsSelectCancelSub",
                "_bbsChooseCertificate",
                "_decreasePKPage",
                "_decreasePK",
                "_actionToAsk",
                "_changeNick",
                "_changeClanName",
                "_bbsepicsRespawn"
        );
    }

    @Override
    public void onBypassCommand(Player player, String bypass) {
        //Checking if all required images were sent to the getPlayer, if not - not allowing to pass
        if (!AutoImageSenderManager.wereAllImagesSent(player)) {
            player.sendPacket(new Say2(player.objectId(), ChatType.CRITICAL_ANNOUNCE, "CB", "Community wasn't loaded yet, try again in few seconds."));
            return;
        }

        StringTokenizer st = new StringTokenizer(bypass, "_");
        String cmd = st.nextToken();
        String folder = "";
        String file = "";
        ClassId subclassId;
        if (!cmd.equals("bbsNewSubPage") && !cmd.equals("bbsAddNewSub"))
            player.deleteQuickVar("SubToRemove");

        if ("bbsgetfav".equals(cmd) || "bbsnpcs".equals(cmd)) {
            sendFileToPlayer(player, "bbs_npcs.htm", true);
            return;
        }

        if ("bbsgatekeeper".equals(cmd))
            folder = "gatekeeper";

        if ("bbsbuffer".equals(cmd)) {
            SchemeBufferInstance.showWindow(player);
            return;
        }

        if ("bbsbufferbypass".equals(cmd)) {
            SchemeBufferInstance.onBypass(player, bypass.substring("_bbsbufferbypass_".length()));
            return;
        }

        if ("bbsNewSubPage".equals(cmd)) {
            String race = st.nextToken();
            addNewSubPage(player, race);
            return;
        }

        if ("bbsAddNewSub".equals(cmd)) {
            subclassId = ClassId.getById(st.nextToken());
            addNewSub(player, subclassId);
            return;
        }

        if ("changeSubPage".equals(cmd)) {
            changeSubPage(player);
            return;
        }

        if ("bbsChangeSubTo".equals(cmd)) {
            subclassId = ClassId.getById(st.nextToken());
            changeSub(player, subclassId);
            return;
        }

        if ("bbsCancelSubPage".equals(cmd)) {
            cancelSubPage(player);
            return;
        }

        if ("bbsSelectCancelSub".equals(cmd)) {
            subclassId = ClassId.getById(st.nextToken());
            player.addQuickVar("SubToRemove", subclassId.id);
            sendFileToPlayer(player, "smallNpcs/subclassChanger_add.htm", true);
            return;
        }

        if ("bbsChooseCertificate".equals(cmd)) {
            chooseCertificatePage(player);
            return;
        }
        if ("decreasePKPage".equals(cmd)) {
            decreasePKPage(player);
            return;
        }

        if ("decreasePK".equals(cmd)) {
            if (!player.isInPeaceZone()) {
                player.sendMessage("You cannot do it right now!");
                return;
            }
            int pksToDecrease = Integer.parseInt(st.nextToken());
            if (player.getInventory().getCountOf(Config.SERVICES_WASH_PK_ITEM) >= Config.SERVICES_WASH_PK_PRICE) {
                player.getInventory().destroyItemByItemId(Config.SERVICES_WASH_PK_ITEM, Config.SERVICES_WASH_PK_PRICE, "Decreasing Pk");
                player.setPkKills(player.getPkKills() - pksToDecrease);
                player.broadcastCharInfo();
                player.sendMessage(pksToDecrease + " PKs were decreased!");
            } else {
                player.sendMessage("You don't have required items!");
            }
            onBypassCommand(player, "decreasePKPage");
            return;
        }

        if ("actionToAsk".equals(cmd)) {
            int id = Integer.parseInt(st.nextToken());
            String message = null;
            if (id == 0)
                message = "Would you like to increase Warehouse Slots by 1 for " + Config.SERVICES_EXPAND_INVENTORY_PRICE + " Coin?";
            else if (id == 1)
                message = "Would you like to increase Clan Warehouse Slots by 1 for " + Config.SERVICES_EXPAND_CWH_PRICE + " Coin?";

            if (message != null) {
                player.ask(new ConfirmDlg(SystemMsg.S1, 60000).addString(message), new ActionAnswerListener(player, id));
            }
            sendFileToPlayer(player, "smallNpcs/exclusiveShop.htm", true);
            return;
        }

        if ("bbsepicsRespawn".equals(cmd)) {
            //convertRespawnDate(RaidBossSpawnManager.INSTANCE().getRespawntime(29001)*1000L);
            String html = HtmCache.INSTANCE.getNotNull(Config.BBS_HOME_DIR + "epicsRespawn/index.htm", player);

            html = html.replace("%respawnAntharas%", convertRespawnDate(AntharasManager.getState().getRespawnDate()));
            html = html.replace("%respawnValakas%", convertRespawnDate(ValakasManager.getState().getRespawnDate()));
            html = html.replace("%respawnBaium%", convertRespawnDate(BaiumManager.getState().getRespawnDate()));
            html = html.replace("%respawnBeleth%", convertRespawnDate(ServerVariables.getLong("BelethKillTime")));
            html = html.replace("%respawnQueenAnt%", convertRespawnDate(RaidBossSpawnManager.INSTANCE.getRespawntime(29001) * 1000L));
            html = html.replace("%respawnOrfen%", convertRespawnDate(RaidBossSpawnManager.INSTANCE.getRespawntime(29014) * 1000L));

            ShowBoard.separateAndSend(html, player);

            return;
        }


        if ("changeClanName".equals(cmd)) {
            if (st.hasMoreTokens()) {
                String newName = st.nextToken().trim();
                changeClanName(player, newName);
            }
            return;
        }

        if (!folder.isEmpty()) {
            file = st.hasMoreTokens() ? st.nextToken() : "main.htm";
        }

        sendFileToPlayer(player, folder + '/' + file, false);
    }

    private String[] fillReplacements(String[] currentReplacements, String stringInside, int count) {
        for (int i = 0; i <= count; i++)
            currentReplacements[i * 2] = '%' + stringInside + i + '%';
        return currentReplacements;
    }

    private void decreasePKPage(Player player) {
        String[] replacements = new String[FIELDS_IN_SUB_SELECT_PAGE * 2];
        replacements = fillReplacements(replacements, "action", 10);

        int replaceIndex = 1;

        for (int pk : PKS) {
            if (player.getPkKills() <= pk)
                break;
            replacements[replaceIndex] = getDecreasePkButton(pk);
            replaceIndex += 2;
        }

        if (player.getPkKills() > 0) {
            replacements[replaceIndex] = getDecreasePkButton(player.getPkKills());
            replaceIndex += 2;
        }
        replacements[replaceIndex] = "<button value=\"Back\" action=\"bypass _bbsfile:smallNpcs/exclusiveShop\" width=100 height=25 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";

        sendFileToPlayer(player, "smallNpcs/exclusiveShop_select.htm", true, replacements);
    }

    private static class ActionAnswerListener implements OnAnswerListener {
        private final Player player;
        private final int action;

        private ActionAnswerListener(Player player, int action) {
            this.player = player;
            this.action = action;
        }

        @Override
        public void sayYes() {
            if (player == null) return;
            if (action == 0)// Inventory
            {
                if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_WAREHOUSE_ITEM, Config.SERVICES_EXPAND_WAREHOUSE_PRICE, "Inventory Expand")) {
                    player.setExpandWarehouse(player.getExpandWarehouse() + 1);
                    player.setVar("ExpandWarehouse", player.getExpandWarehouse());
                    player.sendMessage("Warehouse capacity is now " + player.getWarehouseLimit());
                } else if (Config.SERVICES_EXPAND_WAREHOUSE_ITEM == 57)
                    player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                else
                    player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            } else if (action == 1)// CWH
            {
                if (player.getClan() == null) {
                    player.sendMessage("You need to have Clan, to use that option!");
                    return;
                }
                if (player.getClan().getWhBonus() >= 500) {
                    player.sendMessage("500 Slots is Max for Clan Warehouse");
                    return;
                }

                if (player.getInventory().destroyItemByItemId(Config.SERVICES_EXPAND_CWH_ITEM, Config.SERVICES_EXPAND_CWH_PRICE, "CWH Expand")) {
                    player.getClan().setWhBonus(player.getClan().getWhBonus() + 1);
                    player.sendMessage("Warehouse capacity is now " + (Config.WAREHOUSE_SLOTS_CLAN + player.getClan().getWhBonus()));
                } else if (Config.SERVICES_EXPAND_CWH_ITEM == ItemTemplate.ITEM_ID_ADENA)
                    player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                else
                    player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            }
        }

    }
}