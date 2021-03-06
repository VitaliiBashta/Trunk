package l2trunk.scripts.handler.voicecommands;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.database.mysql;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.UnitMember;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;

import java.util.List;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class CWHPrivileges implements IVoicedCommandHandler, ScriptFile {
    private static final String _commandList = "clan";

    @Override
    public void onLoad() {
        VoicedCommandHandler.INSTANCE.registerVoicedCommandHandler(this);
    }
    @Override
    public List<String> getVoicedCommandList() {
        return List.of(_commandList);
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        if (activeChar.getClan() == null)
            return false;
        if (command.equals("clan")) {
            if (Config.ALT_ALLOW_CLAN_COMMAND_ONLY_FOR_CLAN_LEADER && !activeChar.isClanLeader())
                return false;
            if (!((activeChar.getClanPrivileges() & Clan.CP_CL_MANAGE_RANKS) == Clan.CP_CL_MANAGE_RANKS))
                return false;
            if (args != null) {
                String[] param = args.split(" ");
                if (param.length > 0)
                    if ("allowwh".equalsIgnoreCase(param[0]) && param.length > 1) {
                        UnitMember cm = activeChar.getClan().getAnyMember(param[1]);
                        if (cm != null && cm.getPlayer() != null) // цель онлайн
                        {
                            if (cm.player.isVarSet("canWhWithdraw")) {
                                cm.player.unsetVar("canWhWithdraw");
                                activeChar.sendMessage("Privilege removed successfully");
                            } else {
                                cm.player.setVar("canWhWithdraw");
                                activeChar.sendMessage("Privilege given successfully");
                            }
                        } else if (cm != null) // цель оффлайн
                        {
                            int state = mysql.simple_get_int("value", "character_variables", "obj_id=" + cm.objectId() + " AND name LIKE 'canWhWithdraw'");
                            if (state > 0) {
                                mysql.set("DELETE FROM `character_variables` WHERE obj_id=" + cm.objectId() + " AND name LIKE 'canWhWithdraw' LIMIT 1");
                                activeChar.sendMessage("Privilege removed successfully");
                            } else {
                                mysql.set("INSERT INTO character_variables  (obj_id, type, name, value, expire_time) VALUES (" + cm.objectId() + ",'user-var','canWhWithdraw','1',-1)");
                                activeChar.sendMessage("Privilege given successfully");
                            }
                        } else
                            activeChar.sendMessage("Player not found.");
                    } else if (param[0].equalsIgnoreCase("list")) {
                        StringBuilder sb = new StringBuilder("SELECT `obj_id` FROM `character_variables` WHERE `obj_id` IN (");
                        List<UnitMember> members = activeChar.getClan().getAllMembers();
                        for (int i = 0; i < members.size(); i++) {
                            sb.append(members.get(i).objectId());
                            if (i < members.size() - 1)
                                sb.append(",");
                        }
                        sb.append(") AND `name`='canWhWithdraw'");
                        List<Object> list = mysql.get_array(sb.toString());
                        sb = new StringBuilder("<html><body>Clan CP (.clan)<br><br><table>");
                        for (Object o_id : list)
                            for (UnitMember m : members)
                                if (m.objectId() == toInt(o_id.toString()))
                                    sb.append("<tr><td width=10></td><td width=60>").append(m.getName()).append("</td><td width=20><button width=50 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_clan allowwh ").append(m.getName()).append("\" value=\"Remove\">").append("<br></td></tr>");
                        sb.append("<tr><td width=10></td><td width=20><button width=60 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" action=\"bypass -h user_clan\" value=\"Back\"></td></tr></table></body></html>");
                        activeChar.sendMessage(sb.toString());
                        return true;
                    }
            }
            String dialog = HtmCache.INSTANCE.getNotNull("scripts/services/clan.htm", activeChar);
            if (!Config.SERVICES_EXPAND_CWH_ENABLED)
                dialog = dialog.replaceFirst("%whextprice%", "service disabled");
            else
                dialog = dialog.replaceFirst("%whextprice%", Config.SERVICES_EXPAND_CWH_PRICE + " "
                        + ItemHolder.getTemplate(Config.SERVICES_EXPAND_CWH_ITEM).getName());
            Functions.show(dialog, activeChar, null);
            return true;
        }
        return false;
    }
}