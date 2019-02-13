package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.CommandChannel;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.bosses.BelethManager;

import java.util.StringTokenizer;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class BelethCoffinInstance extends NpcInstance {
    private static final int RING = 10314;

    public BelethCoffinInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        StringTokenizer st = new StringTokenizer(command);
        if ("request_ring".equals(st.nextToken())) {
            if (!BelethManager.isRingAvailable()) {
                player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Stone Coffin:<br><br>Ring is not available. Get lost!"));
                return;
            }
            if (player.getParty() == null || player.getParty().getCommandChannel() == null) {
                player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Stone Coffin:<br><br>You are not allowed to take the ring. Are are not the group or Command Channel."));
                return;
            }
            if (player.getParty().getCommandChannel().getLeader() != player) {
                player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Stone Coffin:<br><br>You are not leader or the Command Channel."));
                return;
            }

            CommandChannel channel = player.getParty().getCommandChannel();

            addItem(player, RING, 1);

            SystemMessage smsg = new SystemMessage(SystemMessage.S1_HAS_OBTAINED_S2);
            smsg.addString(player.getName());
            smsg.addItemName(RING);
            channel.sendPacket(smsg);

            BelethManager.setRingAvailable(false);
            deleteMe();
        } else
            super.onBypassFeedback(player, command);
    }
}