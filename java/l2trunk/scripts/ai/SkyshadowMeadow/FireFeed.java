package l2trunk.scripts.ai.SkyshadowMeadow;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FireFeed extends DefaultAI {
    private final long _wait_timeout = System.currentTimeMillis() + Rnd.get(10, 30) * 1000;

    private FireFeed(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        if (_wait_timeout < System.currentTimeMillis())
            actor.decayMe();

        return true;
    }

    @Override
    public void onEvtSeeSpell(Skill skill, Creature caster) {
        if (skill.getId() != 9075)
            return;

        NpcInstance actor = getActor();
        if (actor == null)
            return;

        actor.doCast(6688, caster, true);
    }
}