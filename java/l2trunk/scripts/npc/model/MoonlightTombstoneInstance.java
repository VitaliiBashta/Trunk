package l2trunk.scripts.npc.model;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.StringTokenizer;

import static l2trunk.gameserver.utils.ItemFunctions.removeItem;


public final class MoonlightTombstoneInstance extends NpcInstance {
    private static final int KEY_ID = 9714;
    private final static long COLLAPSE_TIME = 5; // 5 мин
    private boolean _activated = false;

    public MoonlightTombstoneInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        StringTokenizer st = new StringTokenizer(command);
        if (st.nextToken().equals("insertKey")) {
            if (player.getParty() == null) {
                player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                return;
            }

            if (!player.getParty().isLeader(player)) {
                player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
                return;
            }

            if (player.getParty().getMembersStream()
                    .anyMatch(partyMember -> !isInRange(partyMember, INTERACTION_DISTANCE * 2))) {
                // Члены партии слишком далеко
                Functions.show("default/32343-3.htm", player, this);
                return;
            }

            if (_activated) {
                // Уже активировано
                Functions.show("default/32343-1.htm", player, this);
                return;
            }

            if (player.haveItem(KEY_ID)) {
                removeItem(player, KEY_ID, 1, "MoonlightTombstoneInstance");
                player.getReflection().startCollapseTimer(COLLAPSE_TIME * 60 * 1000L);
                _activated = true;
                broadcastPacketToOthers(new SystemMessage(SystemMessage.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addNumber(COLLAPSE_TIME));
                player.getReflection().setCoreLoc(player.getReflection().getReturnLoc());
                player.getReflection().setReturnLoc(new Location(16280, 283448, -9704));
                Functions.show("default/32343-1.htm", player, this);
                return;
            }
            // Нет ключа
            Functions.show("default/32343-2.htm", player, this);
            return;
        }
        super.onBypassFeedback(player, command);
    }
}