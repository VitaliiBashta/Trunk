package l2trunk.scripts.ai.SkyshadowMeadow;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ChangeWaitType;
import l2trunk.gameserver.network.serverpackets.SocialAction;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.List;

/**
 * @author Grivesky
 * - AI for mobs Sel Mahum Squad Leader (22 786, 22 787, 22 788).
 * - When the fire appears Katel (18,933) nptsy sbigayutsya then sit down and put food on the character's head.
 * - When the fire comes on (18927), is a 30% chance that they will want to sleep and come running to him, and sleep will appear above his head.
 * - Before you rush to eat, shout to chat.
 * - When the characters are over the head, to be a significant decrease.
 * - AI is tested and works.
 */
public final class SelMahumSquadLeader extends Fighter {
    private static final List<NpcString> TEXT = List.of(NpcString.SCHOOL5, NpcString.SCHOOL6);
    private boolean _firstTime1 = true;
    private boolean _firstTime2 = true;
    private boolean _firstTime3 = true;
    private boolean _firstTime4 = true;
    private boolean _firstTime5 = true;
    private boolean statsIsChanged = false;

    public SelMahumSquadLeader(NpcInstance actor) {
        super(actor);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor == null)
            return true;

        if (defThink) {
            doTask();
            return true;
        }

        if (!statsIsChanged) {
            switch (actor.getNpcState()) {
                case 1: {
                    actor.doCast(6332, actor, true);
                    statsIsChanged = true;
                    break;
                }
                case 2: {
                    actor.doCast(6331, actor, true);
                    statsIsChanged = true;
                    break;
                }
            }
        }

        if (!_firstTime2) {
            actor.broadcastPacket(new SocialAction(getActor().objectId(), 2));
            actor.broadcastPacket(new ChangeWaitType(getActor(), 0));
            actor.setNpcState((byte) 1);
            _firstTime2 = true;
        }

        if (!_firstTime4) {
            actor.broadcastPacket(new SocialAction(getActor().objectId(), 2));
            actor.broadcastPacket(new ChangeWaitType(getActor(), 0));
            actor.setNpcState((byte) 2);
            _firstTime4 = true;
        }

        getActor().getAroundNpc(600, 600)
                .filter(npc -> npc.getNpcId() == 18933)
                .forEach(npc -> {
                    if (_firstTime1) {
                        _firstTime1 = false;
                        actor.setRunning();
                        addTaskMove(Location.findPointToStay(npc, 100, 200), true);
                        if (_firstTime5) {
                            _firstTime5 = false;
                            Functions.npcSay(actor, Rnd.get(TEXT));
                        }
                        if (_firstTime2) {
                            _firstTime2 = false;
                            ThreadPoolManager.INSTANCE.schedule(new Go(), Rnd.get(20, 30) * 1000);
                        }
                    }
                });


        getActor().getAroundNpc(600, 600)
                .filter(npc -> npc.getNpcId() == 18927)
                .filter(npc -> npc.getNpcState() == 1)
                .forEach(npc -> {
                    if (Rnd.chance(30)) {
                        if (_firstTime3) {
                            _firstTime3 = false;
                            actor.setRunning();
                            addTaskMove(Location.findPointToStay(npc, 100, 200), true);
                            if (_firstTime4) {
                                _firstTime4 = false;
                                ThreadPoolManager.INSTANCE.schedule(new Go(), Rnd.get(20, 30) * 1000);
                            }
                        }
                    } else if (Rnd.chance(20)) {
                        actor.setNpcState((byte) 2);
                        ThreadPoolManager.INSTANCE.schedule(() -> getActor().setNpcState((byte) 3), Rnd.get(20, 30) * 1000);
                    }
                });
        return true;
    }

    @Override
    public void onEvtDead(Creature killer) {
        _firstTime1 = true;
        _firstTime2 = true;
        _firstTime3 = true;
        _firstTime4 = true;
        _firstTime5 = true;
        super.onEvtDead(killer);
    }

    private class Go extends RunnableImpl {
        @Override
        public void runImpl() {
            NpcInstance actor = getActor();
            Location loc = Location.findPointToStay(actor, 100, 200);

            actor.setNpcState((byte) 3);
            actor.setRunning();
            addTaskMove(loc, true);
            _firstTime1 = true;
            _firstTime3 = true;
        }
    }
}