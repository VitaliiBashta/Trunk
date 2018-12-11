package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.data.xml.holder.ResidenceHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.residence.Castle;
import l2trunk.gameserver.network.serverpackets.CastleSiegeInfo;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class SiegeInformerInstance extends NpcInstance {
    public SiegeInformerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("siege_")) {
            Castle castle = ResidenceHolder.getResidence(Castle.class, toInt(command.substring(6)));
            if (castle != null)
                player.sendPacket(new CastleSiegeInfo(castle, player));
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0)
            pom = "SiegeInformer";
        else
            pom = "SiegeInformer-" + val;

        return "custom/" + pom + ".htm";
    }
}