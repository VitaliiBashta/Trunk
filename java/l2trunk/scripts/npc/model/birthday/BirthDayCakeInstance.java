package l2trunk.scripts.npc.model.birthday;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.concurrent.Future;


@SuppressWarnings("serial")
public class BirthDayCakeInstance extends NpcInstance {
    private final Skill SKILL = SkillTable.getInstance().getInfo(22035, 1);

    private class CastTask extends RunnableImpl {
        @Override
        public void runImpl() {
            for (Player player : World.getAroundPlayers(BirthDayCakeInstance.this, 500, 100)) {
                if (player.getEffectList().getEffectsBySkill(SKILL) != null)
                    continue;

                SKILL.getEffects(BirthDayCakeInstance.this, player, false, false);
            }
        }
    }

    private Future<?> _castTask;

    public BirthDayCakeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
        setTargetable(false);
    }

    @Override
    public void onSpawn() {
        super.onSpawn();

        _castTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new CastTask(), 1000L, 1000L);
    }

    @Override
    public void onDespawn() {
        super.onDespawn();

        _castTask.cancel(false);
        _castTask = null;
    }
}
