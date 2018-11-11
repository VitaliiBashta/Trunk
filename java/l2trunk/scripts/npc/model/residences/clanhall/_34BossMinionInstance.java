package l2trunk.scripts.npc.model.residences.clanhall;

import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.npc.model.residences.SiegeGuardInstance;

/**
 * @author VISTALL
 * @date 17:50/13.05.2011
 */
public abstract class _34BossMinionInstance extends SiegeGuardInstance implements _34SiegeGuard {
    _34BossMinionInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onDeath(Creature killer) {
        setCurrentHp(1, true);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        Functions.npcShout(this, spawnChatSay());
    }

    protected abstract NpcString spawnChatSay();

    public abstract NpcString teleChatSay();
}
