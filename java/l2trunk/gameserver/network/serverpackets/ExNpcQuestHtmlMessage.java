package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.scripts.Functions;

import java.util.regex.Matcher;

public final class ExNpcQuestHtmlMessage extends NpcHtmlMessage {
    private final int _questId;

    public ExNpcQuestHtmlMessage(int npcObjId, int questId) {
        super(npcObjId);
        _questId = questId;
    }

    @Override
    protected void writeImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        if (file != null) //TODO may not be very good to do it here ...
        {
            if (player.isGM())
                Functions.sendDebugMessage(player, "HTML: " + file);
            String content = HtmCache.INSTANCE.getNotNull(file, player);
            String content2 = HtmCache.INSTANCE.getNullable(file, player);
            if (content2 == null)
                setHtml(have_appends && file.endsWith(".htm") ? "" : content);
            else
                setHtml(content);
        }

        for (int i = 0; i < _replaces.size(); i += 2)
            _html = _html.replaceAll(_replaces.get(i), _replaces.get(i + 1));

        if (_html == null)
            return;

        Matcher m = objectId.matcher(_html);
        _html = m.replaceAll(String.valueOf(_npcObjId));

        _html = playername.matcher(_html).replaceAll(player.getName());

        player.cleanBypasses(false);
        _html = player.encodeBypasses(_html, false);

        writeEx(0x8d);
        writeD(_npcObjId);
        writeS(_html);
        writeD(_questId);
    }
}
