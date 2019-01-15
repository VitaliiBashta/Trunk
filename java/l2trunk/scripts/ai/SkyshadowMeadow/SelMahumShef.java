package l2trunk.scripts.ai.SkyshadowMeadow;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;

import java.util.List;

public final class SelMahumShef extends Fighter {
    private static final List<NpcString> _text = List.of(NpcString.SCHOOL3, NpcString.SCHOOL4);
    private long _wait_timeout = System.currentTimeMillis() + 30000;
    private boolean _firstTime = true;

    public SelMahumShef(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor != null) {
            if (_wait_timeout < System.currentTimeMillis()) {
                World.getAroundPlayers(actor, 100, 100).forEach(p ->
                        actor.doCast(6330, p, true));
                _wait_timeout = (System.currentTimeMillis() + 30000);
            }

            actor.getAroundNpc(150, 150)
                    .filter(GameObject::isMonster)
                    .filter(npc -> npc.getNpcId() == 18927)
                    .forEach(npc -> {
                        if (_firstTime) {
                            // Включаем паузу что бы не зафлудить чат.
                            _firstTime = false;
                            Functions.npcSay(actor, Rnd.get(_text));
                            ThreadPoolManager.INSTANCE.schedule(new NewText(), 20000); // Время паузы
                        }
                    });
            return super.thinkActive();
        }
        return true;
    }

    private class NewText extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            if (actor == null)
                return;

            // Выключаем паузу
            _firstTime = true;
        }
    }
}