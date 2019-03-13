package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.entity.residence.ResidenceType;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.ReflectionUtils;
import l2trunk.scripts.instances.RimPailaka;

/**
 * @author pchayka
 */

public final class PailakaGatekeeperInstance extends NpcInstance {
    private static final int rimIzId = 80;

    public PailakaGatekeeperInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.equalsIgnoreCase("rimentrance")) {
            Reflection r = player.getActiveReflection();
            if (r != null) {
                if (player.canReenterInstance(rimIzId))
                    player.teleToLocation(r.getTeleportLoc(), r);
            } else if (player.canEnterInstance(rimIzId)) {
                if (checkGroup(player)) {
                    ReflectionUtils.enterReflection(player, new RimPailaka(), rimIzId);
                } else
                    //FIXME [G1ta0] кастом сообщение
                    player.sendMessage("Failed to enter Rim Pailaka due to improper conditions");
            }
        } else
            super.onBypassFeedback(player, command);
    }

    private boolean checkGroup(Player p) {
        if (!p.isInParty())
            return false;
        for (Player member : p.getParty().getMembersStream()) {
            if (member.getClan() != null) {
                if (member.getClan().getResidenceId(ResidenceType.Castle) == 0) {
                    if (member.getClan().getResidenceId(ResidenceType.Fortress) == 0) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
        return true;
    }
}