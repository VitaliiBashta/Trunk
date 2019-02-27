package l2trunk.scripts.services;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.database.mysql;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SubClass;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.model.entity.events.impl.SiegeEvent;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.SubUnit;
import l2trunk.gameserver.network.clientpackets.CharacterCreate;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.ClanTable;
import l2trunk.gameserver.utils.Log;
import l2trunk.gameserver.utils.Util;

import java.util.Map.Entry;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class Rename extends Functions {
    public void rename_page() {
        if (player == null)
            return;
        if (!Config.SERVICES_CHANGE_NICK_ENABLED) {
            show("Service is disabled.", player);
            return;
        }
        player.sendPacket(new NpcHtmlMessage(5).setFile("scripts/services/NameChange/index.htm"));
    }

    public void separate_page(Player player, String newName) {
        if (player == null)
            return;
        if (!Config.SERVICES_SEPARATE_SUB_ENABLED) {
            show("Service is disabled.", player);
            return;
        }
        if (player.isHero()) {
            show("Not available for heroes.", player);
            return;
        }

        if (player.getSubClasses().size() == 1) {
            show("You must have at least one subclass.", player);
            return;
        }

        if (!player.getActiveClass().isBase()) {
            show("You must be on the main class.", player);
            return;
        }

        if (player.getEvent(SiegeEvent.class) != null) {
            player.sendMessage(new CustomMessage("scripts.services.Rename.SiegeNow"));
            return;
        }

        if (CharacterCreate.isValid(newName)) {
            player.sendMessage(new CustomMessage("scripts.services.Rename.incorrectinput"));
            return;
        }
        if (player.getActiveClass().getLevel() < 75) {
            show("You must have 75 sub-class occupation.", player);
            return;
        }

        String append = "Department subclass:";
        append += "<br>";
        append += "<font color=\"LEVEL\">" + new CustomMessage("scripts.services.Separate.Price").addString(Util.formatAdena(Config.SERVICES_SEPARATE_SUB_PRICE)).addItemName(Config.SERVICES_SEPARATE_SUB_ITEM) + "</font>&nbsp;";
        append += "<edit var=\"name\" width=80 height=15 /><br>";
        append += "<table>";

        for (SubClass s : player.getSubClasses().values())
            if (!s.isBase() && s.getClassId() != ClassId.inspector && s.getClassId() != ClassId.judicator)
                append += "<tr><td><button value=\"" + new CustomMessage("scripts.services.Separate.Button").addString(s.getClassId().name) + "\" action=\"bypass -h scripts_services.Rename:separate " + s.getClassId() + " $name\" width=200 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>";

        append += "</table>";
        show(append, player);
    }

    public void separate(String[] param) {
        if (player == null)
            return;
        if (!Config.SERVICES_SEPARATE_SUB_ENABLED) {
            show("Service is disabled.", player);
            return;
        }
        if (player.isHero()) {
            show("Not available for heroes.", player);
            return;
        }

        if (player.getSubClasses().size() == 1) {
            show("You must have at least one subclass.", player);
            return;
        }

        if (!player.getActiveClass().isBase()) {
            show("You must be on the main class.", player);
            return;
        }

        if (player.getActiveClass().getLevel() < 75) {
            show("You must have 75 sub-class occupation.", player);
            return;
        }

        if (param.length < 2) {
            show("You must specify a target.", player);
            return;
        }

        if (!player.haveItem(Config.SERVICES_SEPARATE_SUB_ITEM, Config.SERVICES_SEPARATE_SUB_PRICE)) {
            if (Config.SERVICES_SEPARATE_SUB_ITEM == 57)
                player.sendPacket(Msg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
            else
                player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }

        ClassId classtomove = ClassId.getById(param[0]);
        int newcharid = 0;
        for (Entry<Integer, String> e : player.getAccountChars().entrySet())
            if (e.getValue().equalsIgnoreCase(param[1]))
                newcharid = e.getKey();

        if (newcharid == 0) {
            show("The purpose is not there.", player);
            return;
        }

        if (mysql.simple_get_int("level", "character_subclasses", "char_obj_id=" + newcharid + " AND level > 1") > 1) {
            show("The aim should be level 1.", player);
            return;
        }

        mysql.set("DELETE FROM character_subclasses WHERE char_obj_id=" + newcharid);
        mysql.set("DELETE FROM character_skills WHERE char_obj_id=" + newcharid);
        mysql.set("DELETE FROM character_skills_save WHERE char_obj_id=" + newcharid);
        mysql.set("DELETE FROM character_effects_save WHERE object_id=" + newcharid);
        mysql.set("DELETE FROM character_hennas WHERE char_obj_id=" + newcharid);
        mysql.set("DELETE FROM character_shortcuts WHERE char_obj_id=" + newcharid);
        mysql.set("DELETE FROM character_variables WHERE obj_id=" + newcharid);

        mysql.set("UPDATE character_subclasses SET char_obj_id=" + newcharid + ", isBase=1, certification=0 WHERE char_obj_id=" + player.objectId() + " AND class_id=" + classtomove);
        mysql.set("UPDATE character_skills SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.objectId() + " AND class_index=" + classtomove);
        mysql.set("UPDATE character_skills_save SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.objectId() + " AND class_index=" + classtomove);
        mysql.set("UPDATE character_effects_save SET object_id=" + newcharid + " WHERE object_id=" + player.objectId() + " AND id=" + classtomove);
        mysql.set("UPDATE character_hennas SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.objectId() + " AND class_index=" + classtomove);
        mysql.set("UPDATE character_shortcuts SET char_obj_id=" + newcharid + " WHERE char_obj_id=" + player.objectId() + " AND class_index=" + classtomove);

        mysql.set("UPDATE character_variables SET obj_id=" + newcharid + " WHERE obj_id=" + player.objectId() + " AND name like 'TransferSkills%'");

        player.modifySubClass(classtomove, null);

        removeItem(player, Config.SERVICES_CHANGE_BASE_ITEM, Config.SERVICES_CHANGE_BASE_PRICE, "Rename$separate");
        player.logout();
        Log.add("Character " + player + " base changed to " + player, "services");
    }

    public void rename_clan_page() {
        if (player == null)
            return;
        if (!Config.SERVICES_CHANGE_CLAN_NAME_ENABLED) {
            show("Service is disabled.", player);
            return;
        }
        if (player.getClan() == null || !player.isClanLeader()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
        }
    }

    public void rename_clan() {
        // Special Case
        rename_clan(new String[]{""});
    }

    private void rename_clan(String[] param) {
        if (player == null || param == null || param.length == 0)
            return;

        if (!Config.SERVICES_CHANGE_CLAN_NAME_ENABLED) {
            show("Service is disabled.", player);
            return;
        }
        if (player.getClan() == null || !player.isClanLeader()) {
            player.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_A_CLAN_LEADER).addName(player));
            return;
        }

        if (player.getEvent(SiegeEvent.class) != null) {
            show(new CustomMessage("scripts.services.Rename.SiegeNow"), player);
            return;
        }

        if (!Util.isMatchingRegexp(param[0], Config.CLAN_NAME_TEMPLATE)) {
            player.sendPacket(Msg.CLAN_NAME_IS_INCORRECT);
            return;
        }
        if (ClanTable.INSTANCE.getClanByName(param[0]) != null) {
            player.sendPacket(Msg.THIS_NAME_ALREADY_EXISTS);
            return;
        }

        if (!player.haveItem(Config.SERVICES_CHANGE_CLAN_NAME_ITEM, Config.SERVICES_CHANGE_CLAN_NAME_PRICE)) {
            player.sendPacket(SystemMsg.INCORRECT_ITEM_COUNT);
            return;
        }
        show(new CustomMessage("scripts.services.Rename.changedname").addString(player.getClan().getName()).addString(param[0]), player);
        SubUnit sub = player.getClan().getSubUnit(Clan.SUBUNIT_MAIN_CLAN);
        sub.setName(param[0], true);

        removeItem(player, Config.SERVICES_CHANGE_CLAN_NAME_ITEM, Config.SERVICES_CHANGE_CLAN_NAME_PRICE, "Rename$rename_clan");
        player.getClan().broadcastClanStatus(true, true, false);
        player.broadcastCharInfo();
        player.broadcastUserInfo(true);
        player.sendUserInfo(true);
    }
}