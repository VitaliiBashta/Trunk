package l2trunk.scripts.npc.model;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.model.Party;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;

import java.util.List;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;

public final class WorkshopServantInstance extends NpcInstance {
    private static final List<Integer> medals = List.of(
            10427,            //Tully's Platinum Medal
            10428,            //Tully's Tin Medal
            10429,            //Tully's Lead Medal
            10430,            //Tully's Zinc Medal
            10431);            //Tully's Copper Medal
    private static final List<String> phrases = List.of(
            "We won't let you go with this knowledge! Die!",
            "Mysterious Agent has failed! Kill him!",
            "Mates! Attack those fools!");

    public WorkshopServantInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("getmedals")) {
            for (int medal : medals)
                if (player.haveItem(medal)) {
                    player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Ingenious Contraption:<br><br>You already have one of the medals. Cannot proceed."));
                    return;
                }

            addItem(player, Rnd.get(medals), 1);
            player.sendPacket(new NpcHtmlMessage(player, this).setHtml("Ingenious Contraption:<br><br>The medal for access to Anomic Founrdy has been given."));

        } else if (command.startsWith("requestteleport"))
            player.teleToLocation(-12220, 279713, -13595);
        else if (command.startsWith("teletoroof"))
            player.teleToLocation(22616, 244888, 11062);
        else if (command.startsWith("teleto6thfloor"))
            player.teleToLocation(-12176, 279696, -13596);
        else if (command.startsWith("teleto7thfloor"))
            player.teleToLocation(-12501, 281397, -11936);
        else if (command.startsWith("teleto8thfloor"))
            player.teleToLocation(-12176, 279696, -10492);
        else if (command.startsWith("acceptjob")) {
            broadcastPacket(new MagicSkillUse(this, player, 5526));
            player.altOnMagicUseTimer(player, 5526);
            player.teleToLocation(22616, 244888, 11062);
        } else if (command.startsWith("rejectjob")) {
            World.getAroundNpc(this, 600, 300).forEach(challenger -> {
                challenger.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, player, 5000);
                switch (challenger.getNpcId()) {
                    case 25600:
                        Functions.npcSay(challenger, phrases.get(0));
                        break;
                    case 25601:
                        Functions.npcSay(challenger, phrases.get(1));
                        break;
                    case 25602:
                        Functions.npcSay(challenger, phrases.get(2));
                        break;
                    default:
                        break;
                }
            });
            Functions.npcSay(this, "Oh...");
            doDie(null);
        } else if (command.startsWith("tryanomicentry")) {
            if (!player.isInParty()) {
                player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                return;
            }
            Party party = player.getParty();
            if (!party.isLeader(player)) {
                player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
                return;
            }
            if (party.getMembersStream()
                    .filter(p -> !this.isInRange(p, 500))
                    .peek(p -> player.sendPacket(Msg.ITS_TOO_FAR_FROM_THE_NPC_TO_WORK))
                    .findAny().isPresent())
                return;

            if (medals.stream()
                    .filter(medal -> !hasItem(party, medal))
                    .peek(medal -> player.sendMessage("In order to enter the Anomic Foundry your party should be carrying all 5 medals of Tully"))
                    .findFirst().isPresent())
                return;
            party.teleport(Location.of(25512, 247240, -2656));
        } else
            super.onBypassFeedback(player, command);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0)
            pom = String.valueOf(npcId);
        else
            pom = npcId + "-" + val;

        if (getNpcId() == 32372)
            if (this.isInZone("[tully5]"))
                return "default/32372-floor.htm";
        if (getNpcId() == 32467)
            if (this.isInZone("[tully6]"))
                return "default/32467-6floor.htm";
        if (this.isInZone("[tully8]"))
            return "default/32467-8floor.htm";
        return "default/" + pom + ".htm";
    }

    private boolean hasItem(Party party, int itemId) {
        return party.getMembersStream()
                .anyMatch(p -> p.getInventory().getItemByItemId(itemId) != null);
    }
}