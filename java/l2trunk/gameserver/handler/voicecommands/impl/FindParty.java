package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.idfactory.IdFactory;
import l2trunk.gameserver.listener.actor.player.OnQuestionMarkListener;
import l2trunk.gameserver.model.GameObjectsStorage;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.actor.listener.CharListenerList;
import l2trunk.gameserver.network.serverpackets.JoinParty;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.network.serverpackets.components.IStaticPacket;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.loginserver.ThreadPoolManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ScheduledFuture;

public final class FindParty extends Functions implements IVoicedCommandHandler {
    private static final List<String> COMMANDS = List.of("party", "invite", "partylist");
    private static final int PARTY_REQUEST_DURATION = 600_000; // 10 minutes delay until the request is rendered invalid.
    private static final int PARTY_REQUEST_DELAY = 60_000; // 1 minute delay until you can send a party request to the whole server again.

    private static final OnPartyQuestionMarkClicked LISTENER = new OnPartyQuestionMarkClicked();
    private static final Map<Integer, FindPartyRequest> _requests = new HashMap<>(); // PartyRequestObjId, RequestorPlayerObjId
    @SuppressWarnings("unused")
    private static ScheduledFuture<?> _requestsCleanupTask;

    static {
        CharListenerList.addGlobal(LISTENER);
        _requestsCleanupTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(() -> {
            synchronized (_requests) // Dont touch me while cleaning-up!
            {
                for (Entry<Integer, FindPartyRequest> entry : _requests.entrySet()) {
                    if (entry.getValue().requestStartTimeMilis + PARTY_REQUEST_DURATION < System.currentTimeMillis())
                        _requests.remove(entry.getKey());
                }
            }
        }, 60000, 60000);
    }

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {
        if (command.startsWith("partylist")) {
            int i = 0;
            Say2[] packets = new Say2[_requests.size() + 2];
            packets[i++] = new Say2(activeChar.objectId(), ChatType.BATTLEFIELD, "[Party Request]", "---------=[List Party Requests]=---------");
            for (FindPartyRequest request : _requests.values()) {
                if (target != null && !target.isEmpty() && !request.message.toLowerCase().contains(target.toLowerCase()))
                    continue;

                Player partyLeader = World.getPlayer(request.requestorObjId);
                if (partyLeader == null)
                    continue;

                int freeSlots = Party.MAX_SIZE - 1; // One taken by the party leader.
                if (partyLeader.getParty() != null)
                    freeSlots = Party.MAX_SIZE - partyLeader.getParty().size();
                if (freeSlots <= 0)
                    continue;

                packets[i++] = new Say2(activeChar.objectId(), ChatType.PARTY, "[Find Party]", "\b\tType=1 \tID=" + partyLeader.objectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b" + partyLeader.getName() + " (" + freeSlots + "/" + Party.MAX_SIZE + ")" + " free slots. " + request.message);
            }
            packets[i++] = new Say2(activeChar.objectId(), ChatType.BATTLEFIELD, "[Party Request]", "---------=[End Party Requests]=---------");
            activeChar.sendPacket(packets);
            return true;
        } else if (command.startsWith("invite")) {
            Player playerToInvite = null;
            if (activeChar.isInParty() && !activeChar.getParty().isLeader(activeChar) && activeChar.getParty().isFull())
                playerToInvite = GameObjectsStorage.getPlayer(target); // Possibly this is a getPlayer invite request within the party.

            if (playerToInvite != null) // A party member asks the party leader to invite specified getPlayer.
            {
                Say2 packetLeader = new Say2(activeChar.objectId(), ChatType.PARTY, "[Party Request]", "Please invite " + playerToInvite.getName() + " to the party. \b\tType=1 \tID=" + playerToInvite.objectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b");
                Say2 packet = new Say2(activeChar.objectId(), ChatType.PARTY, "[Party Request]", "Please invite " + playerToInvite.getName() + " to the party.");
                for (Player ptMem : activeChar.getParty()) {
                    if (activeChar.getParty().getLeader() == ptMem)
                        ptMem.sendPacket(packetLeader);
                    else
                        ptMem.sendPacket(packet);
                }
            }
        } else if (command.startsWith("party")) // The party leader requests whole server for party members.
        {
            // Only party leaders can use it.
            if (activeChar.isInParty() && !activeChar.getParty().isLeader(activeChar)) {
                activeChar.sendMessage("Only your party leaader can use this command now.");
                return true;
            }
            // Only players 40+
            if (activeChar.getLevel() < 40) {
                activeChar.sendMessage("Command available only for players lvl 40+");
                return true;
            }
            int partyRequestObjId = 0;
            for (Entry<Integer, FindPartyRequest> entry : _requests.entrySet()) {
                if (entry.getValue().requestorObjId == activeChar.objectId()) {
                    partyRequestObjId = entry.getKey();
                    break;
                }
            }
            if (partyRequestObjId == 0)
                partyRequestObjId = IdFactory.getInstance().getNextId();

            int freeSlots = Party.MAX_SIZE - 1; // One taken by the party leader.
            if (activeChar.getParty() != null)
                freeSlots = Party.MAX_SIZE - activeChar.getParty().size();
            if (freeSlots <= 0) {
                activeChar.sendMessage("Your party is full. Try again when you have free slots.");
                return true;
            }

            if (target != null && !target.isEmpty()) {
                target = String.valueOf(target.charAt(0)).toUpperCase() + target.substring(1);
            }

            FindPartyRequest request = _requests.get(partyRequestObjId);
            if (request == null) {
                request = new FindPartyRequest(activeChar, target);
            } else {
                long delay = System.currentTimeMillis() - request.requestStartTimeMilis;
                if (delay < PARTY_REQUEST_DELAY) {
                    activeChar.sendMessage("You can send a request every " + PARTY_REQUEST_DELAY / 1000 + " seconds. " + (PARTY_REQUEST_DELAY - delay) / 1000 + " seconds remaining until you can try again.");
                    return true;
                }

                if (target == null || target.isEmpty())
                    request.update(); // Update perserving the message so players can type only .party, but displaying the same message as before.
                else
                    request.update(target); // Update with overriding the message
            }
            _requests.put(partyRequestObjId, request);

            //																															 [Party Find]: [?] Nik (3/9) free slots. Message
            Say2 packet = new Say2(activeChar.objectId(), ChatType.PARTY, "[Find Party]", "\b\tType=1 \tID=" + partyRequestObjId + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b" + activeChar.getName() + " (" + freeSlots + "/" + Party.MAX_SIZE + ")" + " free slots. " + request.message);
            GameObjectsStorage.getAllPlayersStream()
                    .filter(player -> (player.canJoinParty(activeChar) == null))
                    .filter(player -> (activeChar.isInParty()))
                    .filter(player -> !activeChar.getParty().containsMember(player))
                    .forEach(player -> player.sendPacket(packet));
        }
        return false;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMANDS;
    }

    private static class OnPartyQuestionMarkClicked implements OnQuestionMarkListener {
        @Override
        public void onQuestionMarkClicked(Player player, int targetObjId) {
            int requestorObjId = _requests.containsKey(targetObjId) ? _requests.get(targetObjId).requestorObjId : 0;
            if (requestorObjId > 0) // Its a regular party request to the server for additional party members.
            {
                if (player.objectId() != requestorObjId) {
                    Player partyLeader = World.getPlayer(requestorObjId);
                    if (partyLeader == null) {
                        player.sendMessage("Party leader is offline.");
                    } else// if (partyLeader.isInParty())
                    {
                        //requestParty(partyLeader, getPlayer);
                        long delay = System.currentTimeMillis() - player.getQuickVarL("partyrequestsent");
                        if (delay < PARTY_REQUEST_DELAY) {
                            player.sendMessage("You can send a request every " + PARTY_REQUEST_DELAY / 1000 + " seconds. " + (PARTY_REQUEST_DELAY - delay) / 1000 + " seconds remaining until you can try again.");
                            return;
                        }
                        player.addQuickVar("partyrequestsent", System.currentTimeMillis());
                        Say2 packetLeader = new Say2(player, ChatType.BATTLEFIELD, "I'm Level: " + player.getLevel() + ", Class: " + player.getClassId().name + ". Invite \b\tType=1 \tID=" + player.objectId() + " \tColor=0 \tUnderline=0 \tTitle=\u001B\u001B\b");
                        partyLeader.sendPacket(packetLeader);
                        player.sendMessage("Party request sent to " + partyLeader.getName());
                    }
                }
            } else {
                Player target = GameObjectsStorage.getPlayer(targetObjId); // Looks like a party request within a party to invite a certain member.
                if (target != null)
                    requestParty(player, target);
                else
                    player.sendMessage("The request is no longer valid.");
            }
        }

        private void requestParty(Player partyLeader, Player target) {
            if (partyLeader.isOutOfControl()) {
                partyLeader.sendActionFailed();
                return;
            }

            if (partyLeader.isProcessingRequest()) {
                partyLeader.sendPacket(SystemMsg.WAITING_FOR_ANOTHER_REPLY);
                return;
            }

            if (target == null) {
                partyLeader.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
                return;
            }

            if (target == partyLeader) {
                partyLeader.sendPacket(SystemMsg.THAT_IS_AN_INCORRECT_TARGET);
                partyLeader.sendActionFailed();
                return;
            }

            if (target.isBusy()) {
                partyLeader.sendPacket(new SystemMessage2(SystemMsg.C1_IS_ON_ANOTHER_TASK).addName(target));
                return;
            }

            IStaticPacket problem = target.canJoinParty(partyLeader);
            if (problem != null) {
                partyLeader.sendPacket(problem);
                return;
            }

            if (partyLeader.isInParty()) {
                if (partyLeader.getParty().isFull()) {
                    partyLeader.sendPacket(SystemMsg.THE_PARTY_IS_FULL);
                    return;
                }

                // Only the Party Leader may invite new members
                if (Config.PARTY_LEADER_ONLY_CAN_INVITE && !partyLeader.getParty().isLeader(partyLeader)) {
                    partyLeader.sendPacket(SystemMsg.ONLY_THE_LEADER_CAN_GIVE_OUT_INVITATIONS);
                    return;
                }

                if (partyLeader.getParty().isInDimensionalRift()) {
                    partyLeader.sendMessage(new CustomMessage("l2trunk.gameserver.clientpackets.RequestJoinParty.InDimensionalRift"));
                    partyLeader.sendActionFailed();
                    return;
                }
            }

            int itemDistribution = partyLeader.getParty() == null ? 0 : partyLeader.getParty().getLootDistribution();
            //new Request(l2requestType.PARTY, partyLeader, target).setTimeout(10000L).set("itemDistribution", itemDistribution);
            //target.sendPacket(new AskJoinParty(partyLeader.name(), itemDistribution));
            //partyLeader.sendPacket(new SystemMessage2(SystemMsg.C1_HAS_BEEN_INVITED_TO_THE_PARTY).addName(target));
            Party party = partyLeader.getParty();
            if (party == null)
                partyLeader.setParty(party = new Party(partyLeader, itemDistribution));

            target.joinParty(party);
            partyLeader.sendPacket(JoinParty.SUCCESS);
        }
    }

    private static class FindPartyRequest {
        final int requestorObjId;
        long requestStartTimeMilis;
        String message;

        @SuppressWarnings("unused")
        public FindPartyRequest(Player player) {
            requestorObjId = player.objectId();
            requestStartTimeMilis = System.currentTimeMillis();
            message = "";
        }

        FindPartyRequest(Player player, String msg) {
            requestorObjId = player.objectId();
            requestStartTimeMilis = System.currentTimeMillis();
            message = msg == null ? "" : msg;
        }

        void update() {
            requestStartTimeMilis = System.currentTimeMillis();
        }

        void update(String newMsg) {
            requestStartTimeMilis = System.currentTimeMillis();
            message = newMsg;
        }

    }
}