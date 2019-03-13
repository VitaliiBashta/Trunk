package l2trunk.scripts.npc.model;

import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.instancemanager.naia.NaiaTowerManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.network.serverpackets.SystemMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.ai.hellbound.NaiaLock;

public final class NaiaControllerInstance extends NpcInstance {
    public NaiaControllerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("tryenter")) {
            if (NaiaLock.isEntranceActive()) {
                //instance
                if (!player.isInParty()) {
                    player.sendPacket(Msg.YOU_ARE_NOT_CURRENTLY_IN_A_PARTY_SO_YOU_CANNOT_ENTER);
                    return;
                }
                if (!player.getParty().isLeader(player)) {
                    player.sendPacket(Msg.ONLY_A_PARTY_LEADER_CAN_TRY_TO_ENTER);
                    return;
                }
                if (player.getParty().getMembersStream()
                        .filter(member -> member.getLevel() < 80)
                        .peek(member -> player.sendPacket(new SystemMessage(SystemMessage.C1S_LEVEL_REQUIREMENT_IS_NOT_SUFFICIENT_AND_CANNOT_BE_ENTERED).addName(member)))
                        .findAny().isPresent())
                    return;


                if (player.getParty().getMembersStream()
                        .filter(member -> !member.isInRange(this, 500))
                        .peek(member -> player.sendPacket(new SystemMessage(SystemMessage.C1_IS_IN_A_LOCATION_WHICH_CANNOT_BE_ENTERED_THEREFORE_IT_CANNOT_BE_PROCESSED).addName(member)))
                        .findAny().isPresent())
                    return;

                NaiaTowerManager.startNaiaTower(player);
                broadcastPacket(new MagicSkillUse(this, 5527));
                doDie(null);
            }
        } else
            super.onBypassFeedback(player, command);
    }
}