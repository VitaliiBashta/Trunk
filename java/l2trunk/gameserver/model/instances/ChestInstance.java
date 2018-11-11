package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public class ChestInstance extends MonsterInstance {
    public ChestInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    public void tryOpen(Player opener, Skill skill) {
        getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, opener, 100);
    }

    @Override
    public boolean canChampion() {
        return false;
    }
}