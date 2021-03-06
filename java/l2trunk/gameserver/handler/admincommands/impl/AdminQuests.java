package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.commons.text.PrintfFormat;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.scripts.quests._255_Tutorial;

import java.util.List;
import java.util.Map;
import java.util.Objects;


public final class AdminQuests implements IAdminCommandHandler {
    private static final PrintfFormat fmtHEAD = new PrintfFormat("<center><font color=\"LEVEL\">%s [id=%d]</font><br><edit var=\"new_val\" width=100 height=12></center><br>");
    private static final PrintfFormat fmtRow = new PrintfFormat("<tr><td>%s</td><td>%s</td><td width=30>%s</td></tr>");
    private static final PrintfFormat fmtSetButton = new PrintfFormat("<button value=\"Set\" action=\"bypass -h admin_quest %d %s %s %s %s\" width=30 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\">");
    private static final PrintfFormat fmtFOOT = new PrintfFormat("<br><br><br><center><button value=\"Clear Quest\" action=\"bypass -h admin_quest %d CLEAR %s\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"> <button value=\"Quests List\" action=\"bypass -h admin_quests %s\" width=100 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></center>");
    private static final PrintfFormat fmtListRow = new PrintfFormat("<tr><td><a action=\"bypass -h admin_quest %d %s\">%s</a></td><td>%s</td></tr>");
    private static final PrintfFormat fmtListNew = new PrintfFormat("<tr><td><edit var=\"new_quest\" width=100 height=12></td><td><button value=\"Add\" action=\"bypass -h admin_quest $new_quest STATE 2 %s\" width=40 height=20 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_ct1.button_df\"></td></tr>");

    private static boolean ShowQuestState(QuestState qs, Player activeChar) {
        Map<String, String> vars = qs.getVars();
        int id = qs.quest.id;
        String char_name = qs.player.getName();

        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        StringBuilder replyMSG = new StringBuilder("<html><body>");
        replyMSG.append(fmtHEAD.sprintf(qs.quest.name, id));
        replyMSG.append("<table width=260>");
        replyMSG.append(fmtRow.sprintf("PLAYER: ", char_name, ""));
        replyMSG.append(fmtRow.sprintf("STATE: ",
                qs.getStateName(),
                fmtSetButton.sprintf(id, "STATE", "$new_val", char_name, "")));
        for (String key : vars.keySet())
            if (!"<state>".equalsIgnoreCase(key))
                replyMSG.append(fmtRow.sprintf(key + ": ",
                        vars.get(key),
                        fmtSetButton.sprintf(id, "VAR", key, "$new_val", char_name)));
        replyMSG.append(fmtRow.sprintf("<edit var=\"new_name\" width=50 height=12>",
                "~new var~",
                fmtSetButton.sprintf(id, "VAR", "$new_name", "$new_val", char_name)));
        replyMSG.append("</table>");
        replyMSG.append(fmtFOOT.sprintf(id, char_name, char_name));
        replyMSG.append("</body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);
        vars.clear();
        return true;
    }

    private static boolean ShowQuestList(Player targetChar, Player activeChar) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        StringBuilder replyMSG = new StringBuilder("<html><body><table width=260>");
        targetChar.getAllQuestsStates().stream()
                .filter(Objects::nonNull)
                .filter(qs -> qs.quest.getClass() != _255_Tutorial.class)
                .forEach(qs -> replyMSG.append(fmtListRow.sprintf(qs.quest.id,
                        targetChar.getName(),
                        qs.quest.name,
                        qs.getStateName())));
        replyMSG.append(fmtListNew.sprintf(new Object[]{targetChar.getName()}));
        replyMSG.append("</table></body></html>");

        adminReply.setHtml(replyMSG.toString());
        activeChar.sendPacket(adminReply);

        return true;
    }

    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {
        if (!activeChar.getPlayerAccess().CanEditCharAll)
            return false;

        switch (comm) {
            case "admin_quests":
                return ShowQuestList(getTargetChar(wordList, 1, activeChar), activeChar);
            case "admin_quest":
                if (wordList.length < 2) {
                    activeChar.sendMessage("USAGE: //quest name [SHOW|STATE|VAR|CLEAR] ...");
                    return true;
                }
                Quest _quest = QuestManager.getQuest(wordList[1]);
                if (_quest == null) {
                    activeChar.sendMessage("Quest " + wordList[1] + " undefined");
                    return true;
                }
                if (wordList.length < 3 || "SHOW".equalsIgnoreCase(wordList[2]))
                    return cmd_Show(_quest, wordList, activeChar);
                if ("STATE".equalsIgnoreCase(wordList[2]))
                    return cmd_State(_quest, wordList, activeChar);
                if ("VAR".equalsIgnoreCase(wordList[2]))
                    return cmd_Var(_quest, wordList, activeChar);
                if ("CLEAR".equalsIgnoreCase(wordList[2]))
                    return cmd_Clear(_quest, wordList, activeChar);
                return cmd_Show(_quest, wordList, activeChar);
        }
        return true;
    }

    private boolean cmd_Clear(Quest quest, String[] wordList, Player activeChar) {
        // quest id|name CLEAR [target]
        Player targetChar = getTargetChar(wordList, 3, activeChar);
        QuestState st = targetChar.getQuestState(quest);
        if (st == null) {
            activeChar.sendMessage("Player " + targetChar.getName() + " havn't Quest [" + quest.name + "]");
            return false;
        }
        st.exitCurrentQuest();
        return ShowQuestList(targetChar, activeChar);
    }

    private boolean cmd_Show(Quest quest, String[] wordList, Player activeChar) {
        // quest id|name SHOW [target]
        Player targetChar = getTargetChar(wordList, 3, activeChar);
        QuestState st = targetChar.getQuestState(quest);
        if (st == null) {
            activeChar.sendMessage("Player " + targetChar.getName() + " havn't Quest [" + quest.name + "]");
            return false;
        }
        return ShowQuestState(st, activeChar);
    }

    private boolean cmd_Var(Quest _quest, String[] wordList, Player activeChar) {
        if (wordList.length < 5) {
            activeChar.sendMessage("USAGE: //quest id|name VAR varname newvalue [target]");
            return false;
        }

        Player targetChar = getTargetChar(wordList, 5, activeChar);
        QuestState qs = targetChar.getQuestState(_quest);
        if (qs == null) {
            activeChar.sendMessage("Player " + targetChar.getName() + " havn't Quest [" + _quest.name + "], init quest by command:");
            activeChar.sendMessage("//quest id|name STATE 1|2|3 [target]");
            return false;
        }
        if (wordList[4].equalsIgnoreCase("~") || wordList[4].equalsIgnoreCase("#"))
            qs.unset(wordList[3]);
        else
            qs.set(wordList[3], wordList[4]);
        return ShowQuestState(qs, activeChar);
    }

    private boolean cmd_State(Quest _quest, String[] wordList, Player activeChar) {
        if (wordList.length < 4) {
            activeChar.sendMessage("USAGE: //quest id|name STATE 1|2|3 [target]");
            return false;
        }

        int state;
        try {
            state = Integer.parseInt(wordList[3]);
        } catch (Exception e) {
            activeChar.sendMessage("Wrong State ID: " + wordList[3]);
            return false;
        }

        Player targetChar = getTargetChar(wordList, 4, activeChar);
        QuestState qs = targetChar.getQuestState(_quest);
        if (qs == null) {
            activeChar.sendMessage("Init Quest [" + _quest.name + "] for " + targetChar.getName());
            qs = _quest.newQuestState(targetChar, state);
            qs.set("cond");
        } else
            qs.setState(state);

        return ShowQuestState(qs, activeChar);
    }

    private Player getTargetChar(String[] wordList, int wordListIndex, Player activeChar) {
        // цель задана аргументом
        if (wordListIndex >= 0 && wordList.length > wordListIndex) {
            Player player = World.getPlayer(wordList[wordListIndex]);
            if (player == null)
                activeChar.sendMessage("Can't find getPlayer: " + wordList[wordListIndex]);
            return player;
        }
        // цель задана текущим таргетом
        GameObject my_target = activeChar.getTarget();
        if (my_target instanceof Player)
            return (Player) my_target;
        // в качестве цели сам админ
        return activeChar;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_quests",
                "admin_quest");
    }
}