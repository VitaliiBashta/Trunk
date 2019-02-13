package l2trunk.scripts.ai.SkyshadowMeadow;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.utils.NpcUtils;

public final class Fire extends DefaultAI {
    private static final int FEED = 18933;
    private boolean _firstTime = true;
    private long _wait_timeout = System.currentTimeMillis() + Rnd.get(120, 240) * 1000;

    public Fire(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        // При простое загораемся через 210 секунд
        if (_wait_timeout < System.currentTimeMillis()) {
            // Ставим следущее загорание через 180-240 секунд
            _wait_timeout = (System.currentTimeMillis() + Rnd.get(120, 240) * 1000);

            if (actor.getNpcState() == 0 || actor.getNpcState() == 2)
                actor.setNpcState((byte) 1); // Загорелись
            else if (actor.getNpcState() == 1)
                actor.setNpcState((byte) 2); // Затушились
        }

        actor.getAroundNpc(150, 150)
                .filter(npc -> npc.getNpcId() == 18908)
                .filter(o -> o instanceof MonsterInstance)
                .forEach(npc -> {
                    if (_firstTime) {
                        // Включаем паузу что бы не спавнилось много Катлов.
                        _firstTime = false;
                        if (actor.getNpcState() < 1)
                            actor.setNpcState((byte) 1); // Зажигаем кастер.
                        NpcUtils.spawnSingle(FEED, actor.getLoc());
                        ThreadPoolManager.INSTANCE.schedule(() -> {
                            if (getActor() == null) return;
                            _firstTime = true;
                        }, 20000); // Время паузы
                    }
                });

        return true;
    }

}