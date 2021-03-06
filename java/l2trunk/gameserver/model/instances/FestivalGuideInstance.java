package l2trunk.gameserver.model.instances;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.SevenSigns;
import l2trunk.gameserver.model.entity.SevenSignsFestival.DarknessFestival;
import l2trunk.gameserver.model.entity.SevenSignsFestival.SevenSignsFestival;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.Calendar;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class FestivalGuideInstance extends NpcInstance {
    private int festivalType;

    FestivalGuideInstance(int objectId, NpcTemplate template) {
        super(objectId, template);

        switch (getNpcId()) {
            case 31127:
            case 31132:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_31;
                break;
            case 31128:
            case 31133:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_42;
                break;
            case 31129:
            case 31134:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_53;
                break;
            case 31130:
            case 31135:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_64;
                break;
            case 31131:
            case 31136:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_NONE;
                break;

            case 31137:
            case 31142:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_31;
                break;
            case 31138:
            case 31143:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_42;
                break;
            case 31139:
            case 31144:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_53;
                break;
            case 31140:
            case 31145:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_64;
                break;
            case 31141:
            case 31146:
                festivalType = SevenSignsFestival.FESTIVAL_LEVEL_MAX_NONE;
                break;
        }
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (SevenSigns.INSTANCE.getPlayerCabal(player) == SevenSigns.CABAL_NULL) {
            player.sendMessage("You must be Seven Signs participant.");
            return;
        }

        if (command.startsWith("FestivalDesc")) {
            int val = toInt(command.substring(13));
            showChatWindow(player, val, null, true);
        } else if (command.startsWith("Festival")) {
            Party playerParty = player.getParty();
            int val = Integer.parseInt(command.substring(9, 10));

            switch (val) {
                case 1: // Become a Participant
                    showChatWindow(player, 1, null, false);
                    break;
                case 2: // Festival 2 xxxx
                    // Check if the festival period is active, if not then don't allow registration.
                    if (SevenSigns.INSTANCE.getCurrentPeriod() != SevenSigns.PERIOD_COMPETITION) {
                        showChatWindow(player, 2, "a", false);
                        return;
                    }

                    // Check if a festival is in progress, then don't allow registration yet.
                    if (SevenSignsFestival.INSTANCE.isFestivalInitialized()) {
                        player.sendMessage(new CustomMessage("l2trunk.gameserver.model.instances.L2FestivalGuideInstance.InProgress"));
                        return;
                    }

                    // Check if the getPlayer is in a formed party already.
                    if (playerParty == null || playerParty.size() < Config.FESTIVAL_MIN_PARTY_SIZE) {
                        showChatWindow(player, 2, "buffPrice", false);
                        return;
                    }

                    // Check if the getPlayer is the party leader.
                    if (!playerParty.isLeader(player)) {
                        showChatWindow(player, 2, "c", false);
                        return;
                    }

                    // Check if all the party members are in the required occupation range.
                    int maxlevel = SevenSignsFestival.getMaxLevelForFestival(festivalType);
                    if (playerParty.getMembersStream()
                            .filter(p -> p.getLevel() > maxlevel)
                            .peek(p -> showChatWindow(player, 2, "d", false))
                            .findAny().isPresent())
                        return;

                    if (playerParty.getMembersStream()
                            .filter(p -> SevenSigns.INSTANCE.getPlayerCabal(p) == SevenSigns.CABAL_NULL)
                            .peek(p -> showChatWindow(player, 2, "g", false))
                            .findAny().isPresent())
                        return;


                    if (player.isFestivalParticipant()) {
                        showChatWindow(player, 2, "f", false);
                        return;
                    }

                    int stoneType = toInt(command.substring(11));
                    long stonesNeeded = (long) Math.floor(SevenSignsFestival.getStoneCount(festivalType, stoneType) * Config.FESTIVAL_RATE_PRICE);

                    if (!player.getInventory().destroyItemByItemId(stoneType, stonesNeeded, "Festival Guide")) {
                        player.sendMessage(new CustomMessage("l2trunk.gameserver.model.instances.L2FestivalGuideInstance.NotEnoughSSType"));
                        return;
                    }

                    player.sendPacket(SystemMessage2.removeItems(stoneType, stonesNeeded));
                    SevenSignsFestival.INSTANCE.addAccumulatedBonus(festivalType, stoneType, stonesNeeded);

                    new DarknessFestival(player.getParty(), SevenSigns.INSTANCE.getPlayerCabal(player), festivalType);

                    showChatWindow(player, 2, "e", false);
                    break;
                case 4: // Current High Scores
                    StringBuilder strBuffer = new StringBuilder("<html><body>Festival Guide:<br>These are the top scores of the week, for the ");

                    final StatsSet dawnData = SevenSignsFestival.INSTANCE.getHighestScoreData(SevenSigns.CABAL_DAWN, festivalType);
                    final StatsSet duskData = SevenSignsFestival.INSTANCE.getHighestScoreData(SevenSigns.CABAL_DUSK, festivalType);
                    final StatsSet overallData = SevenSignsFestival.INSTANCE.getOverallHighestScoreData(festivalType);

                    final int dawnScore = dawnData.getInteger("score");
                    final int duskScore = duskData.getInteger("score");
                    int overallScore = 0;

                    // If no data is returned, assume there is no record, or all scores are 0.
                    if (overallData != null)
                        overallScore = overallData.getInteger("score");

                    strBuffer.append(SevenSignsFestival.getFestivalName(festivalType)).append(" festival.<br>");

                    if (dawnScore > 0)
                        strBuffer.append("Dawn: ").append(calculateDate(dawnData.getString("date"))).append(". Score ").append(dawnScore).append("<br>").append(dawnData.getString("names").replaceAll(",", ", ")).append("<br>");
                    else
                        strBuffer.append("Dawn: No record exists. Score 0<br>");

                    if (duskScore > 0)
                        strBuffer.append("Dusk: ").append(calculateDate(duskData.getString("date"))).append(". Score ").append(duskScore).append("<br>").append(duskData.getString("names").replaceAll(",", ", ")).append("<br>");
                    else
                        strBuffer.append("Dusk: No record exists. Score 0<br>");

                    if (overallScore > 0) {
                        String cabalStr = "Children of Dusk";
                        if (overallData.getInteger("cabal") == SevenSigns.CABAL_DAWN)
                            cabalStr = "Children of Dawn";
                        strBuffer.append("Consecutive top scores: ").append(calculateDate(overallData.getString("date"))).append(". Score ").append(overallScore).append("<br>Affilated side: ").append(cabalStr).append("<br>").append(overallData.getString("names").replaceAll(",", ", ")).append("<br>");
                    } else
                        strBuffer.append("Consecutive top scores: No record exists. Score 0<br>");

                    strBuffer.append("<a action=\"bypass -h npc_").append(objectId()).append("_Chat 0\">Go back.</a></body></html>");

                    NpcHtmlMessage html = new NpcHtmlMessage(player, this);
                    html.setHtml(strBuffer.toString());
                    player.sendPacket(html);
                    break;
                case 8: // Increase the Festival Challenge
                    if (playerParty == null)
                        return;

                    if (!playerParty.isLeader(player)) {
                        showChatWindow(player, 8, "a", false);
                        break;
                    }

                    Reflection r = getReflection();
                    if (r instanceof DarknessFestival)
                        if (((DarknessFestival) r).increaseChallenge())
                            showChatWindow(player, 8, "buffPrice", false);
                        else
                            showChatWindow(player, 8, "c", false);

                    break;
                case 9: // Leave the Festival
                    if (playerParty == null)
                        return;

                    r = getReflection();
                    if (!(r instanceof DarknessFestival))
                        return;

                    if (playerParty.isLeader(player))
                        r.collapse();
                    else if (playerParty.size() > Config.FESTIVAL_MIN_PARTY_SIZE)
                        player.leaveParty();
                    else
                        player.sendMessage("Only party leader can leave festival, if minmum party member is reached.");
                    break;
                case 3:
                case 5:
                case 6:
                case 7:
                default:
                    showChatWindow(player, val, null, false);
            }
        } else
            // this class dont know any other commands, let forward
            // the command to the parent class
            super.onBypassFeedback(player, command);
    }

    private void showChatWindow(Player player, int val, String suffix, boolean isDescription) {
        String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH + "festival/";
        filename += isDescription ? "desc_" : "festival_";
        filename += suffix != null ? val + suffix + ".htm" : val + ".htm";
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile(filename);
        html.replace("%festivalType%", SevenSignsFestival.getFestivalName(festivalType));
        html.replace("%min%", Config.FESTIVAL_MIN_PARTY_SIZE);
        // If the stats or bonus table is required, construct them.
        if (val == 1) {
            html.replace("%price1%", (long) Math.floor(SevenSignsFestival.getStoneCount(festivalType, 6362) * Config.FESTIVAL_RATE_PRICE));
            html.replace("%price2%", (long) Math.floor(SevenSignsFestival.getStoneCount(festivalType, 6361) * Config.FESTIVAL_RATE_PRICE));
            html.replace("%price3%", (long) Math.floor(SevenSignsFestival.getStoneCount(festivalType, 6360) * Config.FESTIVAL_RATE_PRICE));
        }
        if (val == 5)
            html.replace("%statsTable%", getStatsTable());
        if (val == 6)
            html.replace("%bonusTable%", getBonusTable());
        player.sendPacket(html);
        player.sendActionFailed();
    }

    @Override
    public void showChatWindow(Player player, int val) {
        String filename = SevenSigns.SEVEN_SIGNS_HTML_PATH;

        switch (getNpcId()) {
            // Dawn Festival Guides
            case 31127:
            case 31128:
            case 31129:
            case 31130:
            case 31131:
                filename += "festival/dawn_guide.htm";
                break;
            // Dusk Festival Guides
            case 31137:
            case 31138:
            case 31139:
            case 31140:
            case 31141:
                filename += "festival/dusk_guide.htm";
                break;
            // Festival Witches
            case 31132:
            case 31133:
            case 31134:
            case 31135:
            case 31136:
            case 31142:
            case 31143:
            case 31144:
            case 31145:
            case 31146:
                filename += "festival/festival_witch.htm";
                break;
            default:
                filename = getHtmlPath(getNpcId(), val, player);
        }

        player.sendPacket(new NpcHtmlMessage(player, this, filename, val));
    }

    private String getStatsTable() {
        StringBuilder tableHtml = new StringBuilder();

        // Get the scores for each of the festival occupation ranges (types).
        for (int i = 0; i < 5; i++) {
            long dawnScore = SevenSignsFestival.INSTANCE.getHighestScore(SevenSigns.CABAL_DAWN, i);
            long duskScore = SevenSignsFestival.INSTANCE.getHighestScore(SevenSigns.CABAL_DUSK, i);
            String festivalName = SevenSignsFestival.getFestivalName(i);
            String winningCabal = "Children of Dusk";

            if (dawnScore > duskScore)
                winningCabal = "Children of Dawn";
            else if (dawnScore == duskScore)
                winningCabal = "None";

            tableHtml.append("<tr><td width=\"100\" align=\"center\">").append(festivalName).append("</td><td align=\"center\" width=\"35\">").append(duskScore).append("</td><td align=\"center\" width=\"35\">").append(dawnScore).append("</td><td align=\"center\" width=\"130\">").append(winningCabal).append("</td></tr>");
        }

        return tableHtml.toString();
    }

    private String getBonusTable() {
        StringBuilder tableHtml = new StringBuilder();

        // Get the accumulated scores for each of the festival occupation ranges (types).
        for (int i = 0; i < 5; i++) {
            long accumScore = SevenSignsFestival.INSTANCE.getAccumulatedBonus(i);
            String festivalName = SevenSignsFestival.getFestivalName(i);

            tableHtml.append("<tr><td align=\"center\" width=\"150\">").append(festivalName).append("</td><td align=\"center\" width=\"150\">").append(accumScore).append("</td></tr>");
        }

        return tableHtml.toString();
    }

    private String calculateDate(String milliFromEpoch) {
        long numMillis = Long.valueOf(milliFromEpoch);
        Calendar calCalc = Calendar.getInstance();

        calCalc.setTimeInMillis(numMillis);

        return calCalc.get(Calendar.YEAR) + "/" + calCalc.get(Calendar.MONTH) + "/" + calCalc.get(Calendar.DAY_OF_MONTH);
    }
}
