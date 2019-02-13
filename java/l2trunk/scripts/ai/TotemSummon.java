package l2trunk.scripts.ai;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Map;

public final class TotemSummon extends DefaultAI {
    private static final Map<Integer, Integer> npcBuffs = Map.of(
            143, 23308,
            144, 23309,
            145, 23310,
            146, 23311);
    private long _timer = 0;

    public TotemSummon(NpcInstance actor) {
        super(actor);
        actor.setHasChatWindow(false);
        actor.startImmobilized();
    }

    @Override
    public void onEvtSpawn() {
        super.onEvtSpawn();
        ThreadPoolManager.INSTANCE.schedule(() -> getActor().deleteMe(), 30 * 60 * 1000L);
    }

    @Override
    public boolean thinkActive() {
        if (_timer < System.currentTimeMillis()) {
            _timer = System.currentTimeMillis() + 15000L;
            getActor().getAroundCharacters(450, 200)
                    .filter(c -> c instanceof Playable)
                    .filter(c -> !c.isDead())
                    .forEach(c -> c.altOnMagicUseTimer(c, getBuffId(getActor().getNpcId())));
        }

        return true;
    }

    private int getBuffId(int npcId) {
        return npcBuffs.get(npcId) != null ? npcBuffs.get(npcId) : 0;
    }
}
