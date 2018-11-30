package l2trunk.gameserver.model.entity.CCPHelpers;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.poll.Poll;
import l2trunk.gameserver.model.entity.poll.PollAnswer;
import l2trunk.gameserver.model.entity.poll.PollEngine;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

public final class CCPPoll {
    public static boolean bypass(Player activeChar, String[] vars) {
        String second = vars.length > 1 ? vars[1] : "";
        String fileName;

        Poll activePoll = PollEngine.INSTANCE.getActivePoll();

        if (activePoll == null) {
            fileName = "pollEmpty.htm";
        } else if (vars[0].equals("poll_vote")) {
            int answerId = Integer.parseInt(second.trim());
            activePoll.addVote(activeChar, answerId);
            return true;
        } else if (!vars[0].equals("poll_change")) {
            fileName = "pollResults.htm";
        } else {
            fileName = "pollVote.htm";
        }

        String html = HtmCache.INSTANCE().getNotNull("command/" + fileName, activeChar);

        if (html.contains("%question%"))
            html = html.replace("%question%", activePoll.getQuestion());

        if (html.contains("%endTime%"))
            html = html.replace("%endTime%", activePoll.getPollEndDate());

        if (html.contains("%answers%"))
            html = fillAnswers(html, activeChar);

        if (html.contains("%results%"))
            html = fillResults(html, activeChar);

        NpcHtmlMessage msg = new NpcHtmlMessage(0);
        msg.setHtml(html);
        activeChar.sendPacket(msg);

        return false;
    }

    private static String fillAnswers(String html, Player activeChar) {
        PollAnswer[] answers = PollEngine.INSTANCE.getPoll().getAnswers();
        StringBuilder resultsBuilder = new StringBuilder("<table width=280><tr><td>");


        for (int i = 0; i < answers.length; i++) {
            PollAnswer answer = answers[i];
            resultsBuilder.append("<table width=280 bgcolor=").append(getColor(i)).append("><tr><td width=200>");
            resultsBuilder.append(answer.getAnswer());
            resultsBuilder.append("</td><td width=80>");
            resultsBuilder.append(getButton("user_poll poll_vote " + answer.getId()));
            resultsBuilder.append("</td></tr></table>");
        }

        resultsBuilder.append("</td></tr></table>");

        return html.replace("%answers%", resultsBuilder.toString());
    }

    private static String fillResults(String html, Player activeChar) {
        Poll currentPoll = PollEngine.INSTANCE.getPoll();
        int answersCount = currentPoll.getAnswers().length;
        PollAnswer[] answersToSort = new PollAnswer[answersCount];

        for (int i = 0; i < answersCount; i++) {
            answersToSort[i] = currentPoll.getAnswers()[i];
        }
        answersToSort = PollEngine.INSTANCE.sortAnswers(answersToSort);

        StringBuilder resultsBuilder = new StringBuilder("<table width=280><tr><td>");

        for (PollAnswer answer : answersToSort) {
            resultsBuilder.append("<table width=280 bgcolor=");
            resultsBuilder.append("7d805a");
            resultsBuilder.append("><tr><td width=200>");
            resultsBuilder.append(answer.getAnswer());
            resultsBuilder.append("</td><td width=80><center>");
            resultsBuilder.append(PollEngine.INSTANCE.getAnswerProcentage(answer)).append('%');
            resultsBuilder.append("</center></td></tr></table>");
        }

        resultsBuilder.append("</td></tr></table>");

        return html.replace("%results%", resultsBuilder.toString());
    }

    private static String getColor(int index) {
        return (index % 2 == 0 ? "313a37" : "3a3a31");
    }

    private static String getButton(String bypass) {
        return "<button value=\"Vote!\" action=\"bypass -h "
                + bypass
                + "\" width=80 height=30 back=\"L2UI_CT1.Button_DF_Down\" fore=\"L2UI_CT1.Button_DF\">";
    }
}
