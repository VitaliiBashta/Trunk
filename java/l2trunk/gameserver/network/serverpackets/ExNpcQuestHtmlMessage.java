package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;

import java.util.regex.Matcher;

public final class ExNpcQuestHtmlMessage extends NpcHtmlMessage {
    private final int questId;

    public ExNpcQuestHtmlMessage(int npcObjId, int questId) {
        super(npcObjId);
        this.questId = questId;
    }

    @Override
    protected void writeImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        if (file != null) {
            Functions.sendDebugMessage(player, "HTML: " + file);
            String content = HtmCache.INSTANCE.getNotNull(file, player);
            String content2 = HtmCache.INSTANCE.getNullable(file);
            if (content2 == null)
                setHtml(have_appends && file.endsWith(".htm") ? "" : content);
            else
                setHtml(content);
        }

        replaces.forEach( (k,v) -> html = html.replaceAll(k,v));

        if (html == null)
            return;

        Matcher m = objectId.matcher(html);
        html = m.replaceAll(String.valueOf(npcObjId));

        html = playername.matcher(html).replaceAll(player.getName());

        player.cleanBypasses(false);
        html = player.encodeBypasses(html, false);

        writeEx(0x8d);
        writeD(npcObjId);
        writeS(html);
        writeD(questId);
    }
}
