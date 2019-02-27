package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.poll.Poll;
import l2trunk.gameserver.model.entity.poll.PollAnswer;
import l2trunk.gameserver.model.entity.poll.PollEngine;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminPoll implements IAdminCommandHandler {
    private static final String MAIN_FOLDER = "admin/poll/";

    /**
     * Use file name like: admin_new_poll.htm
     */
    private static String getPageHtml(String fileName, Player activeChar) {
        return HtmCache.INSTANCE.getNotNull(MAIN_FOLDER + fileName, activeChar);
    }

    private static String getButton(String buttonText, String bypass) {
        return "<button value=\"" + buttonText + "\" action=\"bypass -h " + bypass + "\" width=" + (buttonText.length() > 18 ? 170 : 120) + " height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
    }

    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, Player activeChar) {

        if (!activeChar.getPlayerAccess().CanEditChar || !Config.ENABLE_POLL_SYSTEM)
            return false;

        Poll currentPoll = PollEngine.INSTANCE.getPoll();

        String html = null;

        switch (comm) {
            //Main page
            case "admin_poll":

                if (currentPoll == null) {
                    //going to set question page
                    return useAdminCommand("admin_poll_set_question", wordList, fullString, activeChar);
                }

                html = HtmCache.INSTANCE.getNotNull(MAIN_FOLDER + "admin_current_poll.htm", activeChar);

                html = html.replace("%question%", currentPoll.getQuestion());
                html = html.replace("%endDate%", currentPoll.getPollEndDate());
                html = html.replace("%quietStartPause%", (PollEngine.INSTANCE.isActive() ? getButton("Pause votes", "admin_poll_end 1") : getButton("Start quietly", "admin_poll_start 1")));
                html = html.replace("%startEndAndAnnounce%", (PollEngine.INSTANCE.isActive() ? getButton("End Poll and Announce", "admin_poll_end 2") : getButton("Start and Announce", "admin_poll_start 2")));

                break;
            //Setting question for the existing or new poll
            case "admin_poll_set_question":
                if (wordList.length == 1) {
                    html = getPageHtml("admin_new_poll.htm", activeChar);
                    break;
                }

                String question = fullString.substring("admin_poll_set_question".length()).trim();
                question = correctQuestion(question);
                if (currentPoll == null) {
                    PollEngine.INSTANCE.addNewPollQuestion(question);
                    html = getPageHtml("admin_new_poll_answers.htm", activeChar);
                } else {
                    currentPoll.setQuestion(question);
                    activeChar.sendMessage("Question changed!");
                    return useAdminCommand("admin_poll", wordList, fullString, activeChar);
                }
                break;
            //Setting answers for new poll
            case "admin_poll_set_answers":
                if (wordList.length == 1) {
                    html = getPageHtml("admin_new_poll_answers.htm", activeChar);
                    break;
                }

                StringTokenizer st = new StringTokenizer(fullString.substring("admin_poll_set_answers".length()), "|");
                int firstStCount = st.countTokens();
                List<PollAnswer> answers = new ArrayList<>();

                for (int i = 0; i < firstStCount; i++) {
                    String answerTitle = st.nextToken().trim();
                    if (!answerTitle.isEmpty()) {
                        answers.add(new PollAnswer(answerTitle));
                    }
                }

                if (answers.size() == 0) {
                    html = getPageHtml("admin_new_poll_answers.htm", activeChar);
                    activeChar.sendMessage("You didnt fill any field!");
                } else {
                    currentPoll.addAnswers(answers);
                    html = getPageHtml("admin_new_poll_time.htm", activeChar);
                }
                break;
            //Setting time for new or current poll
            case "admin_poll_set_time":
                if (wordList.length == 1) {
                    html = getPageHtml("admin_new_poll_time.htm", activeChar);
                    break;
                }

                try {
                    int minutesToPollOver = toInt(wordList[1]);

                    currentPoll.setEndTime(minutesToPollOver * 60 * 1000);
                    activeChar.sendMessage("End time has been changed!");
                    return useAdminCommand("admin_poll", wordList, fullString, activeChar);
                } catch (Exception e) {
                    html = getPageHtml("admin_new_poll_time.htm", activeChar);
                    activeChar.sendMessage("Try again!");
                }
                //Starting quietly(1) or with announce(2)
            case "admin_poll_start":
                try {
                    int type = toInt(wordList[1]);
                    PollEngine.INSTANCE.startPoll((type == 2), true);
                    activeChar.sendMessage("Voting started!");
                } catch (Exception e) {
                    activeChar.sendMessage("Use just //poll");
                    return useAdminCommand("admin_poll", wordList, fullString, activeChar);
                }
                break;
            //Ending poll quietly(1) or with announce(2)
            case "admin_poll_end":
                try {
                    int type = toInt(wordList[1]);
                    PollEngine.INSTANCE.stopPoll(type == 2);
                    activeChar.sendMessage("Voting finished!");
                } catch (Exception e) {
                    activeChar.sendMessage("Use just //poll");
                    return useAdminCommand("admin_poll", wordList, fullString, activeChar);
                }
                break;
            //Checking current answers
            case "admin_poll_current_answers":
                if (currentPoll == null) {
                    html = getPageHtml("admin_new_poll_answers.htm", activeChar);
                    break;
                }

                html = getPageHtml("admin_current_answers.htm", activeChar);

                StringBuilder answersText = new StringBuilder("<table width=300>");

                for (PollAnswer pollAnswer : currentPoll.getAnswers()) {
                    answersText.append("<tr>");
                    answersText.append("<td width=300>");
                    answersText.append(pollAnswer.getAnswer());
                    answersText.append("</td></tr><tr><td width=300>");
                    answersText.append("<table width=300><tr><td><center>");
                    answersText.append("Votes: ").append(pollAnswer.getVotes());
                    answersText.append("</center></td><td><center>");
                    answersText.append(getButton("Delete Answer", "admin_poll_delete_answer " + pollAnswer.getId()));
                    answersText.append("</center></td></tr></table></td></tr>");
                }

                html += "</table><br><br><br>";
                html += "<center>Add new answer<br>";
                html += "<multiedit var=\"answer\" width=250 height=50>";
                html += getButton("Add Answer", "admin_poll_add_new_answer $answer");
                html = html.replace("%answers%", answersText.toString());
                break;
            //Deleting existing answer
            case "admin_poll_delete_answer":
                int answerId = toInt(wordList[1]);

                currentPoll.deleteAnswer(answerId);

                activeChar.sendMessage("Answer has been deleted!");

                return useAdminCommand("admin_poll_current_answers", wordList, fullString, activeChar);
            //Adding new answer
            case "admin_poll_add_new_answer":
                String answerTitle = fullString.substring("admin_poll_add_new_answer".length()).trim();
                if (answerTitle.length() > 0) {
                    currentPoll.addAnswer(answerTitle);

                    activeChar.sendMessage("Answer has been added!");
                } else
                    activeChar.sendMessage("Fill the field!");
                return useAdminCommand("admin_poll_current_answers", wordList, fullString, activeChar);
            //Deleting current poll if it isn't active
            case "admin_poll_delete":
                if (PollEngine.INSTANCE.isActive()) {
                    activeChar.sendMessage("You cannot delete active Poll!");
                    break;
                }
                PollEngine.INSTANCE.deleteCurrentPoll();
                activeChar.sendMessage("Poll has been deleted!");

                break;
        }

        //Sending html
        if (html != null && !html.isEmpty()) {
            NpcHtmlMessage msg = new NpcHtmlMessage(0);
            msg.setHtml(html);
            activeChar.sendPacket(msg);
        }
        return true;
    }

    private String correctQuestion(String question) {
        question = "<font color=9a3f33>" + question + "</font>";
        question = question.replaceAll("<b>", "<font name=\"hs12\" color=9a3f33>");
        question = question.replaceAll("</b>", "</font>");
        return question;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_poll",
                "admin_poll_set_question",
                "admin_poll_set_answers",
                "admin_poll_set_time",
                "admin_poll_start",
                "admin_poll_end",
                "admin_poll_current_answers",
                "admin_poll_delete_answer",
                "admin_poll_add_new_answer",
                "admin_poll_delete");
    }
}