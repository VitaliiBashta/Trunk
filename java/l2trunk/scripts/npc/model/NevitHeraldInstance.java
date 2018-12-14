package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.ArrayList;
import java.util.List;

public final class NevitHeraldInstance extends NpcInstance {
    public NevitHeraldInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("request_blessing".equalsIgnoreCase(command)) {
            if (player.getEffectList().getEffectsBySkillId(23312) != null) {
                showChatWindow(player, 1);
                return;
            }
            List<Creature> target = new ArrayList<>();
            target.add(player);
            broadcastPacket(new MagicSkillUse(this, player, 23312));
            callSkill(23312, target, true);
        } else
            super.onBypassFeedback(player, command);
    }
}