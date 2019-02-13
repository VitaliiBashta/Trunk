package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

import java.util.regex.Pattern;

public final class TutorialShowHtml extends L2GameServerPacket {
    private static final Pattern playername = Pattern.compile("%playername%");
    private static final Pattern playerClassName = Pattern.compile("%className%");

    /**
     * <html><head><body><center>
     * <font color="LEVEL">Quest</font>
     * </center>
     * <br>
     * Speak to the <font color="LEVEL"> Paagrio Priests </font>
     * of the Temple of Paagrio. They will explain the basics of combat through quests.
     * <br>
     * You must visit them, for they will give you a useful gift after you complete a quest.
     * <br>
     * They are marked in yellow on the radar, at the upper-right corner of the screen.
     * You must visit them if you wish to advance.
     * <br>
     * <a action="link tutorial_close_0">Close Window</a>
     * </body></html>
     * <p>
     * ВНИМАНИЕ!!! Клиент отсылает назад action!!! Используется как БАЙПАСС В RequestTutorialLinkHtml!!!
     */
    private String html;

    public TutorialShowHtml(String html) {
        this.html = html;
    }

    @Override
    protected final void writeImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        // Player name replace
        html = playername.matcher(html).replaceAll(player.getName());

        // Player class name replace
        html = playerClassName.matcher(html).replaceAll(player.getClassId().name);

        writeC(0xa6);
        writeS(html);
    }
}