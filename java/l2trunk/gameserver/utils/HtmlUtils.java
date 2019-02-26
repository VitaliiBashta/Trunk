package l2trunk.gameserver.utils;

import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SysString;

public final class HtmlUtils {
    public static final String PREV_BUTTON = "<button value=\"&$1037;\" action=\"bypass %prev_bypass%\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
    public static final String NEXT_BUTTON = "<button value=\"&$1038;\" action=\"bypass %next_bypass%\" width=60 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";

    public static String htmlResidenceName(int id) {
        return "&%" + id + ";";
    }

    public static String htmlNpcName(int npcId) {
        return "&@" + npcId + ";";
    }

    public static String htmlSysString(SysString sysString) {
        return htmlSysString(sysString.getId());
    }

    private static String htmlSysString(int id) {
        return "&$" + id + ";";
    }

    public static String htmlItemName(int itemId) {
        return "&#" + itemId + ";";
    }

    public static String htmlClassName(int classId) {
        return "<ClassId>" + classId + "</ClassId>";
    }

    public static String htmlNpcString(NpcString id, Object... params) {
        return htmlNpcString(id.getId(), params);
    }

    public static String htmlNpcString(int id, Object... params) {
        StringBuilder replace = new StringBuilder("<fstring");
        if (params.length > 0)
            for (int i = 0; i < params.length; i++)
                replace.append(" p").append(i + 1).append("=\"").append(params[i]).append("\"");
        replace.append(">").append(id).append("</fstring>");
        return replace.toString();
    }

    public static String htmlButton(String value, String action, int width) {
        return htmlButton(value, action, width, 22);
    }

    private static String htmlButton(String value, String action, int width, int height) {
        return String.format("<button value=\"%s\" action=\"%s\" back=\"L2UI_CT1.Button_DF_Small_Down\" width=%d height=%d fore=\"L2UI_CT1.Button_DF_Small\">", value, action, width, height);
    }

    public static String getCpGauge(int width, long current, long max, boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_CP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_CP_Center", 17, -13);
    }

    public static String getHpGauge(int width, long current, long max, boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_HP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_HP_Center", 17, -13);
    }

    public static String getHpWarnGauge(int width, long current, long max, boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_HPWarn_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_HPWarn_Center", 17, -13);
    }

    public static String getHpFillGauge(int width, long current, long max, boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_HPFill_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_HPFill_Center", 17, -13);
    }

    public static String getMpGauge(int width, long current, long max, boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_MP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_MP_Center", 17, -13);
    }

    public static String getExpGauge(int width, long current, long max, boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_EXP_bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_EXP_Center", 17, -13);
    }

    public static String getFoodGauge(int width, long current, long max, boolean displayAsPercentage) {
        return getGauge(width, current, max, displayAsPercentage, "L2UI_CT1.Gauges.Gauge_DF_Large_Food_Bg_Center", "L2UI_CT1.Gauges.Gauge_DF_Large_Food_Center", 17, -13);
    }

    private static String getGauge(int width, long current, long max, boolean displayAsPercentage, String backgroundImage, String image, long imageHeight, long top) {
        current = Math.min(current, max);
        final StringBuilder sb = new StringBuilder();
        sb.append("<table width=" + width + " cellpadding=0 cellspacing=0><tr><td background=\"" + backgroundImage + "\">");
        sb.append("<img src=\"" + image + "\" width=" + (long) (((double) current / max) * width) + " height=" + imageHeight + ">");
        sb.append("</td></tr><tr><td align=center><table cellpadding=0 cellspacing=" + top + "><tr><td>");
        if (displayAsPercentage) {
            sb.append("<table cellpadding=0 cellspacing=2><tr><td>" + String.format("%.2f%%", ((double) current / max) * 100) + "</td></tr></table>");
        } else {
            final String tdWidth = String.valueOf((width - 10) / 2);
            sb.append("<table cellpadding=0 cellspacing=0><tr><td width=" + tdWidth + " align=right>" + current + "</td>");
            sb.append("<td width=10 align=center>/</td><td width=" + tdWidth + ">" + max + "</td></tr></table>");
        }
        sb.append("</td></tr></table></td></tr></table>");
        return sb.toString();
    }

    public static String bbParse(String s) {
        if (s == null)
            return null;

        s = s.replace("\r", "");
        s = s.replaceAll("(\\s|\"|'|\\(|^|\n)\\*(.*?)\\*(\\s|\"|'|\\)|\\?|\\.|!|:|;|,|$|\n)", "$1<font color=\"LEVEL\">$2</font>$3");
        s = s.replaceAll("(\\s|\"|'|\\(|^|\n)\\$(.*?)\\$(\\s|\"|'|\\)|\\?|\\.|!|:|;|,|$|\n)", "$1<font color=\"00FFFF\">$2</font>$3");
        s = Strings.replace(s, "^!(.*?)$", 8, "<font color=\"FFFFFF\">$1</font>\n\n");
        s = s.replaceAll("%%\\s*\n", "<br1>");
        s = s.replaceAll("\n\n+", "<br>");
        s = Strings.replace(s, "\\[([^\\]\\|]*?)\\|([^\\]]*?)\\]", 32, "<a action=\"bypass -h $1\">$2</a>");
        s = s.replaceAll(" @", "\" msg=\"");

        return s;
    }
}
