package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ItemFunctions;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.instances.HeartInfinityAttack;

import java.util.List;
import java.util.stream.Collectors;

public final class DeadTumorInstance extends NpcInstance {
    private long warpTimer;

    public DeadTumorInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        warpTimer = System.currentTimeMillis();
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (getReflection().getInstancedZoneId() == 119 || getReflection().getInstancedZoneId() == 120) {
            List<NpcInstance> deadTumors = getReflection().getAllByNpcId(getNpcId(), true).collect(Collectors.toList());
            deadTumors.remove(this);

            if ("examine_tumor".equalsIgnoreCase(command))
                showChatWindow(player, 1);
            else if ("showcheckpage".equalsIgnoreCase(command)) {
                if (!player.isInParty()) {
                    showChatWindow(player, 2);
                    return;
                }
                if (warpTimer + 60000 > System.currentTimeMillis()) {
                    showChatWindow(player, 4);
                    return;
                }
                if (deadTumors.size() < 1) {
                    showChatWindow(player, 3);
                    return;
                }
                showChatWindow(player, 5);
            } else if ("warp".equalsIgnoreCase(command)) {
                if (!player.haveItem(13797)) {
                    showChatWindow(player, 6);
                    return;
                }
                if (ItemFunctions.removeItem(player, 13797, 1, "DeadTumorInstance") > 0 && player.isInParty()) {
                    Location loc = Location.coordsRandomize(Rnd.get(deadTumors).getLoc(), 100, 150);
                    getReflection().getPlayers().forEach(p ->
                            p.sendPacket(new ExShowScreenMessage(NpcString.S1S_PARTY_HAS_MOVED_TO_A_DIFFERENT_LOCATION_THROUGH_THE_CRACK_IN_THE_TUMOR, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, player.getParty().getLeader().getName())));
                    for (Player p : player.getParty().getMembers())
                        if (p.isInRange(this, 500))
                            p.teleToLocation(loc);
                }
            } else
                super.onBypassFeedback(player, command);
        } else if (getReflection().getInstancedZoneId() == 121) {
            List<NpcInstance> deadTumors = getReflection().getAllByNpcId(getNpcId(), true).collect(Collectors.toList());
            deadTumors.remove(this);
            if ("examine_tumor".equalsIgnoreCase(command)) {
                if (getNpcId() == 32536)
                    showChatWindow(player, 1);
                else if (getNpcId() == 32535)
                    showChatWindow(player, 7);
            } else if ("warpechmus".equalsIgnoreCase(command)) {
                if (!player.isInParty()) {
                    showChatWindow(player, 2);
                    return;
                }
                getReflection().getPlayers().forEach(p ->
                        p.sendPacket(new ExShowScreenMessage(NpcString.S1S_PARTY_HAS_MOVED_TO_A_DIFFERENT_LOCATION_THROUGH_THE_CRACK_IN_THE_TUMOR, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, player.getParty().getLeader().getName())));
                for (Player p : player.getParty().getMembers())
                    if (p.isInRange(this, 800))
                        p.teleToLocation(new Location(-179548, 209584, -15504));
                ((HeartInfinityAttack) getReflection()).notifyEchmusEntrance(player.getParty().getLeader());
            } else if (command.equalsIgnoreCase("showcheckpage")) {
                if (!player.isInParty()) {
                    showChatWindow(player, 2);
                    return;
                }
                if (warpTimer + 60000 > System.currentTimeMillis()) {
                    showChatWindow(player, 4);
                    return;
                }
                if (deadTumors.size() < 1) {
                    showChatWindow(player, 3);
                    return;
                }
                showChatWindow(player, 5);
            } else if ("warp".equalsIgnoreCase(command)) {
                if (!player.haveItem(13797)) {
                    showChatWindow(player, 6);
                    return;
                }
                if (ItemFunctions.removeItem(player, 13797, 1, "DeadTumorInstance") > 0 && player.isInParty()) {
                    Location loc = Location.coordsRandomize(deadTumors.get(Rnd.get(deadTumors.size())).getLoc(), 100, 150);
                    getReflection().getPlayers().forEach(p ->
                            p.sendPacket(new ExShowScreenMessage(NpcString.S1S_PARTY_HAS_MOVED_TO_A_DIFFERENT_LOCATION_THROUGH_THE_CRACK_IN_THE_TUMOR, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, player.getParty().getLeader().getName())));
                    player.getParty().getMembers().stream()
                            .filter(p -> p.isInRange(this, 500))
                            .forEach(p -> p.teleToLocation(loc));
                }
            } else if ("reenterechmus".equalsIgnoreCase(command)) {
                if (!player.haveItem(13797, 3)) {
                    showChatWindow(player, 6);
                    return;
                }
                if (ItemFunctions.removeItem(player, 13797, 3, "DeadTumorInstance") >= 3 && player.isInParty()) {
                    getReflection().getPlayers().forEach(p ->
                            p.sendPacket(new ExShowScreenMessage(NpcString.S1S_PARTY_HAS_ENTERED_THE_CHAMBER_OF_EKIMUS_THROUGH_THE_CRACK_IN_THE_TUMOR, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, player.getParty().getLeader().getName())));
                    ((HeartInfinityAttack) getReflection()).notifyEkimusRoomEntrance();
                    player.getParty().getMembers().stream()
                    .filter(p-> p.isInRange(this, 400))
                        .forEach(p ->
                            p.teleToLocation(Location.of(-179548, 209584, -15504)));
                }
            } else
                super.onBypassFeedback(player, command);
        } else
            super.onBypassFeedback(player, command);
    }
}