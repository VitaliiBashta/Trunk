package l2trunk.scripts.ai.SkyshadowMeadow;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;

import java.util.List;

public final class SelMahumShef extends Fighter {
    private long _wait_timeout = System.currentTimeMillis() + 30000;
    private boolean _firstTime = true;
    private static final NpcString[] _text = {NpcString.SCHOOL3, NpcString.SCHOOL4};

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

            for (NpcInstance npc : actor.getAroundNpc(150, 150)) {
                if (npc.isMonster() && npc.getNpcId() == 18927) {
                    if (_firstTime) {
                        // Включаем паузу что бы не зафлудить чат.
                        _firstTime = false;
                        Functions.npcSay(actor, Rnd.get(_text));
                        ThreadPoolManager.INSTANCE.schedule(new NewText(), 20000); // Время паузы
                    }
                }
            }

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