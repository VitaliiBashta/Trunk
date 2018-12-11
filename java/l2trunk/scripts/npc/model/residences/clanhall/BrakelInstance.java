package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.ClanHall;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.TimeUtils;

public final class BrakelInstance extends NpcInstance {
    public BrakelInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void showChatWindow(Player player, int val, Object... arg) {
        ClanHall clanhall = ResidenceHolder.getResidence(ClanHall.class, 21);
        if (clanhall == null)
            return;
        NpcHtmlMessage html = new NpcHtmlMessage(player, this);
        html.setFile("residence2/clanhall/partisan_ordery_brakel001.htm");
        html.replace("%next_siege%", TimeUtils.toSimpleFormat(clanhall.getSiegeDate().getTimeInMillis()));
        player.sendPacket(html);
    }
}
