package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.data.xml.holder.SkillAcquireHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.base.AcquireType;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.SkillCoolTime;
import l2trunk.gameserver.network.serverpackets.SkillList;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.stats.Calculator;
import l2trunk.gameserver.stats.Env;
import l2trunk.gameserver.stats.funcs.Func;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Log;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminSkill implements IAdminCommandHandler {

    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanEditChar)
            return false;

        switch (comm) {
            case "admin_show_skills":
                showSkillsPage(activeChar);
                break;
            case "admin_show_effects":
                showEffects(activeChar);
                break;
            case "admin_remove_skills":
                removeSkillsPage(activeChar);
                break;
            case "admin_skill_list":
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/skills.htm"));
                break;
            case "admin_skill_index":
                if (wordList.length > 1)
                    activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/skills/" + wordList[1] + ".htm"));
                break;
            case "admin_add_skill":
                adminAddSkill(activeChar, wordList);
                break;
            case "admin_remove_skill":
                adminRemoveSkill(activeChar, wordList);
                break;
            case "admin_get_skills":
                adminGetSkills(activeChar);
                break;
            case "admin_reset_skills":
                adminResetSkills(activeChar);
                break;
            case "admin_give_all_skills":
                adminGiveAllSkills(activeChar);
                break;
            case "admin_debug_stats":
                debug_stats(activeChar);
                break;
            case "admin_give_all_clan_skills":
                giveAllClanSkills(activeChar);
                break;
            case "admin_remove_cooldown":
            case "admin_resetreuse":
                Player target = activeChar;
                if (activeChar.getTarget() instanceof Player)
                    target = (Player) activeChar.getTarget();

                target.resetReuse();
                target.sendPacket(new SkillCoolTime(activeChar));
                activeChar.sendMessage("Rollback all skill reset.");
                break;
            case "admin_buff":
                for (int i = 7041; i <= 7064; i++)
                    activeChar.addSkill(i);
                activeChar.sendPacket(new SkillList(activeChar));
                break;
            case "admin_people_having_effect":
                int skillId = toInt(wordList[1]);
                GameObjectsStorage.getAllPlayersStream().forEach(player ->
                        player.getEffectList().getAllEffects().stream()
                                .filter(e -> e.skill.id == skillId)
                                .forEach(e -> {
                                    activeChar.sendMessage("Player: " + player.getName() + " Level:" + e.skill.level);
                                    activeChar.sendMessage("Finished!");
                                }));
                break;
        }

        return true;
    }

    private void giveAllClanSkills(Player activeChar) {
        Player target = null;
        if (activeChar.getTarget() != null) {
            if (activeChar.getTarget() instanceof Player) {
                target = (Player) activeChar.getTarget();
            }
        }
        if (target == null) {
            activeChar.sendMessage("[ERROR]Incorrect target.");
            return;
        }
        final Clan clan = target.getClan();
        if (clan == null) {
            activeChar.sendMessage("[ERROR] This getPlayer is NOT in a clan!");
            return;
        }
        Skill skill;
        for (int i = 0; i < 10; i++) // Lazy hack to give clan skills at max occupation for the specific clan occupation.
        {
            Collection<SkillLearn> clanSkills = SkillAcquireHolder.getAvailableSkills(target, AcquireType.CLAN);
            for (SkillLearn sl : clanSkills) {
                skill = SkillTable.INSTANCE.getInfo(sl.id, sl.level);
                clan.addSkill(skill, true);
            }
        }

        clan.broadcastToOnlineMembers(new Say2(0, ChatType.CLAN, "[CLAN]", "Congratulations! This clan just received all clan skills for clan occupation " + clan.getLevel() + "!"));
        clan.broadcastSkillListToOnlineMembers();
        activeChar.sendMessage("Clan " + clan.getName() + " sucessfully received all clan skills.");
    }

    private void debug_stats(Player activeChar) {
        GameObject target_obj = activeChar.getTarget();
        if (!(target_obj instanceof Creature)) {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        Creature target = (Creature) target_obj;

        Set<Calculator> calculators = target.getCalculators();

        StringBuilder log_str = new StringBuilder("--- Debug for " + target.getName() + " ---\r\n");

        for (Calculator calculator : calculators) {
            Env env = new Env(target, activeChar, null);
            env.value = calculator.getBase();
            log_str.append("Stat: ").append(calculator.stat.getValue()).append("\r\n");
            List<Func> funcs = calculator.getFunctions();
            for (Func func: funcs) {
                String order = Integer.toHexString(func.order).toUpperCase();
                if (order.length() == 1)
                    order = "0" + order;
                log_str.append("\tFunc #").append(func.stat).append("@ [0x").append(order).append("]").append(func.getClass().getSimpleName()).append("\t").append(env.value);
                if (func.getCondition() == null || func.getCondition().test(env))
                    func.calc(env);
                log_str.append(" -> ").append(env.value).append(func.owner != null ? "; owner: " + func.owner.toString() : "; no owner").append("\r\n");
            }
        }

        Log.add(log_str.toString(), "debug_stats");
    }

    /**
     * This function will give all the skills that the gm target can have at its
     * occupation to the traget
     */
    private void adminGiveAllSkills(Player activeChar) {
        GameObject target = activeChar.getTarget();
        Player player;
        if (target instanceof Player && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }
        int unLearnable = 0;
        int skillCounter = 0;
        Collection<SkillLearn> skills = SkillAcquireHolder.getAvailableSkills(player, AcquireType.NORMAL);
        while (skills.size() > unLearnable) {
            unLearnable = 0;
            for (SkillLearn s : skills) {
                Skill sk = SkillTable.INSTANCE.getInfo(s.id, s.level);
                if (sk == null || sk.cantLearn(player.getClassId())) {
                    unLearnable++;
                    continue;
                }
                if (player.getSkillLevel(sk.id) == -1)
                    skillCounter++;
                player.addSkill(sk, true);
            }
            skills = SkillAcquireHolder.getAvailableSkills(player, AcquireType.NORMAL);
        }

        player.sendMessage("Admin gave you " + skillCounter + " skills.");
        player.sendPacket(new SkillList(player));
        activeChar.sendMessage("You gave " + skillCounter + " skills to " + player.getName());
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_show_skills",
                "admin_remove_skills",
                "admin_skill_list",
                "admin_skill_index",
                "admin_add_skill",
                "admin_remove_skill",
                "admin_get_skills",
                "admin_reset_skills",
                "admin_give_all_skills",
                "admin_show_effects",
                "admin_debug_stats",
                "admin_remove_cooldown",
                "admin_resetreuse",
                "admin_people_having_effect",
                "admin_buff",
                "admin_give_all_clan_skills");

    }

    private void removeSkillsPage(Player activeChar) {
        GameObject target = activeChar.getTarget();
        Player player;
        if (target instanceof Player && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        Collection<Skill> skills = player.getAllSkills();

        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_show_skills\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<center>Editing character: ").append(player.getName()).append("</center>");
        replyMSG.append("<br><table width=270><tr><td>Lv: ")
                .append(player.getLevel()).append(" ").append(player.getClassId().name).append("</td></tr></table>");
        replyMSG.append("<br><center>Click on the skill you wish to remove:</center>");
        replyMSG.append("<br><table width=270>");
        replyMSG.append("<tr><td width=80>Name:</td><td width=60>Level:</td><td width=40>Id:</td></tr>");
        for (Skill element : skills)
            replyMSG.append("<tr><td width=80><a action=\"bypass -h admin_remove_skill ")
                    .append(element.id).append("\">")
                    .append(element.name)
                    .append("</a></td><td width=60>")
                    .append(element.level)
                    .append("</td><td width=40>")
                    .append(element.id)
                    .append("</td></tr>");
        replyMSG.append("</table>");
        replyMSG.append("<br><center><table>");
        replyMSG.append("Remove custom skill:");
        replyMSG.append("<tr><td>Id: </td>");
        replyMSG.append("<td><edit var=\"id_to_remove\" width=110></td></tr>");
        replyMSG.append("</table></center>");
        replyMSG.append("<center><button value=\"Remove skill\" action=\"bypass -h admin_remove_skill $id_to_remove\" width=110 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></center>");
        replyMSG.append("<br><center><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15></center>");
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void showSkillsPage(Player activeChar) {
        GameObject target = activeChar.getTarget();
        Player player;
        if (target instanceof Player && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        String replyMSG = "<html noscrollbar><body><title>Edit Character</title>" + "<table border=0 cellpadding=0 cellspacing=0 width=290 height=358 background=\"l2ui_ct1.Windows_DF_TooltipBG\">" +
                "<tr><td align=center>" +
                "<br>" +
                "<table cellpadding=0 cellspacing=-5 width=260><tr>" +
                "<td><button value=\"Main\" action=\"bypass -h admin_admin\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" +
                "<td><button value=\"Events\" action=\"bypass -h admin_show_html events/events.htm\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" +
                "<td><button value=\"Chars\" action=\"bypass -h admin_char_manage\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" +
                "<td><button value=\"Server\" action=\"bypass -h admin_server admserver.htm\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" +
                "<td><button value=\"GM Shop\" action=\"bypass -h admin_gmshop\" width=60 height=23 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" +
                "</tr></table>" +
                "<br><br><br>" +
                "<table cellpadding=0 cellspacing=-2 width=290>" +
                "<tr>" +
                "<td align=center><font name=\"hs12\" color=\"LEVEL\">Edit Player:</font></td>" +
                "<td align=center><font name=\"hs12\" color=\"00FF00\">" + player.getName() + "</font></td>" +
                "</tr>" +
                "</table>" +
                "<br>" +
                "<table cellpadding=0 cellspacing=-2 width=290>" +
                "<tr>" +
                "<td align=center><font color=\"LEVEL\">Level:" + player.getLevel() + " - " + player.getClassId().name + "</font></td>" +
                "</tr>" +
                "</table>" +
                "<br><br>" +
                "<table cellpadding=0 cellspacing=-5 width=260>" +
                "<tr><td><button value=\"Add skills\" action=\"bypass -h admin_skill_list\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td><button value=\"Get skills\" action=\"bypass -h admin_get_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>" +
                "<tr><td><button value=\"Delete skills\" action=\"bypass -h admin_remove_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td><button value=\"Reset skills\" action=\"bypass -h admin_reset_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>" +
                "<tr><td><button value=\"Give All Skills\" action=\"bypass -h admin_give_all_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>" +
                "<td><button value=\"All Clan Skills\" action=\"bypass -h admin_give_all_clan_skills\" width=130 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td></tr>" +
                "</table>" +
                "</td></tr>" +
                "</table></body></html>";
        adminReply.setHtml(replyMSG);
        activeChar.sendPacket(adminReply);
    }

    private void showEffects(Player activeChar) {
        GameObject target = activeChar.getTarget();
        Player player;
        if (target instanceof Player && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);

        StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append("<table width=260><tr>");
        replyMSG.append("<td width=40><button value=\"Main\" action=\"bypass -h admin_admin\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("<td width=180><center>Character Selection Menu</center></td>");
        replyMSG.append("<td width=40><button value=\"Back\" action=\"bypass -h admin_current_player\" width=40 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\"></td>");
        replyMSG.append("</tr></table>");
        replyMSG.append("<br><br>");
        replyMSG.append("<center>Editing character: ").append(player.getName()).append("</center>");

        replyMSG.append("<br><center><button value=\"");
        replyMSG.append("Refresh");
        replyMSG.append("\" action=\"bypass -h admin_show_effects\" width=100 height=15 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\" /></center>");
        replyMSG.append("<br>");

        player.getEffectList().getAllEffects().forEach(e ->
                replyMSG.append(e.skill.name).append(" ").append(e.skill.level).append(" - ").append(e.skill.isToggle() ? "Infinity" : (e.getTimeLeft() + " seconds")).append("<br1>"));
        replyMSG.append("<br></body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
    }

    private void adminGetSkills(Player activeChar) {
        GameObject target = activeChar.getTarget();
        Player player;
        if (target instanceof Player && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        if (player.getName().equals(activeChar.getName()))
            player.sendMessage("There is no point in doing it on your character.");
        else {
            Collection<Skill> skills = player.getAllSkills();
            activeChar.getAllSkills().forEach(s -> activeChar.removeSkill(s.id, true));
            skills.forEach(s -> activeChar.addSkill(s, true));
            activeChar.sendMessage("You now have all the skills of  " + player.getName() + ".");
        }

        showSkillsPage(activeChar);
    }

    private void adminResetSkills(Player activeChar) {
        GameObject target = activeChar.getTarget();
        Player player;
        if (target instanceof Player && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        int counter = 0;
        for (Skill element : player.getAllSkills())
            if ((!element.common) && (!SkillAcquireHolder.isSkillPossible(player, element, AcquireType.NORMAL))) {
                player.removeSkill(element.id, true);
                counter++;
            }
        player.checkSkills();
        player.sendPacket(new SkillList(player));
        player.sendMessage("[GM]" + activeChar.getName() + " has updated your skills.");
        activeChar.sendMessage(counter + " skills removed.");

        showSkillsPage(activeChar);
    }

    private void adminAddSkill(Player activeChar, String[] wordList) {
        GameObject target = activeChar.getTarget();
        Player player;
        if (target instanceof Player && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        if (wordList.length == 3) {
            int id = toInt(wordList[1]);
            int level = toInt(wordList[2]);
            Skill skill = SkillTable.INSTANCE.getInfo(id, level);
            if (skill != null) {
                player.sendMessage("Admin gave you the skill " + skill.name + ".");
                player.addSkill(skill, true);
                player.sendPacket(new SkillList(player));
                activeChar.sendMessage("You gave the skill " + skill.name + " to " + player.getName() + ".");
            } else
                activeChar.sendMessage("Error: there is no such skill.");
        }

        showSkillsPage(activeChar);
    }

    private void adminRemoveSkill(Player activeChar, String[] wordList) {
        GameObject target = activeChar.getTarget();
        Player player;
        if (target instanceof Player && (activeChar == target || activeChar.getPlayerAccess().CanEditCharAll))
            player = (Player) target;
        else {
            activeChar.sendPacket(SystemMsg.INVALID_TARGET);
            return;
        }

        if (wordList.length == 2) {
            int id = toInt(wordList[1]);
            int level = player.getSkillLevel(id);
            Skill skill = SkillTable.INSTANCE.getInfo(id, level);
            if (skill != null) {
                player.sendMessage("Admin removed the skill " + skill.name + ".");
                player.removeSkill(id, true);
                player.sendPacket(new SkillList(player));
                activeChar.sendMessage("You removed the skill " + skill.name + " from " + player.getName() + ".");
            } else
                activeChar.sendMessage("Error: there is no such skill.");
        }

        removeSkillsPage(activeChar);
    }

}