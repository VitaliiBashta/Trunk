package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class SeducedInvestigatorInstance extends MonsterInstance {
    public SeducedInvestigatorInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setHasChatWindow(true);
    }

    @Override
    public void showChatWindow(Player player, int val) {
        player.sendPacket(new NpcHtmlMessage(player, this, "common/seducedinvestigator.htm", val));
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        return false;
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}