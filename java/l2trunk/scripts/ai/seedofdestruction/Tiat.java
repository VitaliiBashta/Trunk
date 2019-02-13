package l2trunk.scripts.ai.seedofdestruction;

import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.Fighter;
import l2trunk.gameserver.instancemanager.SoDManager;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage.ScreenMessageAlign;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.Location;

import java.util.List;

public final class Tiat extends Fighter {
    private static final List<Location> TRAP_LOCS = List.of(
            new Location(-252022, 210130, -11995, 16384),
            new Location(-248782, 210130, -11995, 16384),
            new Location(-248782, 206875, -11995, 16384),
            new Location(-252022, 206875, -11995, 16384));
    private static final long COLLAPSE_BY_INACTIVITY_INTERVAL = 10 * 60 * 1000; // 10 мин
    private static final int TRAP_NPC_ID = 18696;
    private static final List<Integer> TIAT_MINION_IDS = List.of(29162, 22538, 22540, 22547, 22542, 22548);
    private static final List<String> TIAT_TEXT = List.of(
            "You'll regret challenging me!",
            "You shall die in pain!",
            "I will wipe out your entire kind!");
    private static final int TIAT_TRANSFORMATION_SKILL = 5974;
    private boolean _notUsedTransform = true;
    private long _lastAttackTime = 0;
    private long _lastFactionNotifyTime = 0;
    private boolean _immobilized;
    private boolean _failed = false;

    public Tiat(NpcInstance actor) {
        super(actor);
        _immobilized = true;
        actor.startImmobilized();
    }

    @Override
    public void onEvtAttacked(Creature attacker, int damage) {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return;

        _lastAttackTime = System.currentTimeMillis();

        if (_notUsedTransform && actor.getCurrentHpPercents() < 50) {
            if (_immobilized) {
                _immobilized = false;
                actor.stopImmobilized();
            }
            _notUsedTransform = false;
            clearTasks();
            spawnTraps();
            actor.abortAttack(true, false);
            actor.abortCast(true, false);
            // Transform skill cast [custom: making Tiat invul while casting]
            actor.setInvul(true);
            actor.doCast(TIAT_TRANSFORMATION_SKILL, actor, true);
            ThreadPoolManager.INSTANCE.schedule(() -> {
                getActor().setFullHpMp();
                getActor().setInvul(false);
            }, SkillTable.INSTANCE.getInfo(TIAT_TRANSFORMATION_SKILL).hitTime);
        }
        if (System.currentTimeMillis() - _lastFactionNotifyTime > _minFactionNotifyInterval) {
            _lastFactionNotifyTime = System.currentTimeMillis();
            World.getAroundNpc(actor)
                    .filter(npc -> (TIAT_MINION_IDS.contains(npc.getNpcId())))
                    .forEach(npc -> npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, attacker, 30000));
            if (Rnd.chance(15) && !_notUsedTransform)
                actor.broadcastPacket(new ExShowScreenMessage(Rnd.get(TIAT_TEXT), 4000, ScreenMessageAlign.MIDDLE_CENTER, false));
        }
        super.onEvtAttacked(attacker, damage);
    }

    @Override
    public boolean thinkActive() {
        NpcInstance actor = getActor();
        if (actor.isDead())
            return true;

        // Коллапсируем инстанс, если Тиата не били более 10 мин
        if (!_failed && _lastAttackTime != 0 && _lastAttackTime + COLLAPSE_BY_INACTIVITY_INTERVAL < System.currentTimeMillis()) {
            final Reflection r = actor.getReflection();
            _failed = true;

            // Показываем финальный ролик при фейле серез секунду после очистки инстанса
            ThreadPoolManager.INSTANCE.schedule(() -> {
                r.getPlayers().forEach(pl -> pl.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_FAIL));
                r.clearReflection(5, true);
            }, 1000);
            return true;
        }
        return super.thinkActive();
    }

    @Override
    public void onEvtDead(Creature killer) {
        _notUsedTransform = true;
        _lastAttackTime = 0;
        _lastFactionNotifyTime = 0;

        SoDManager.addTiatKill();
        final Reflection r = getActor().getReflection();
        r.setReenterTime(System.currentTimeMillis());
        r.getNpcs().forEach(GameObject::deleteMe);
        // Показываем финальный ролик серез секунду после очистки инстанса
        ThreadPoolManager.INSTANCE.schedule(() -> {
            r.getPlayers().forEach(pl -> pl.showQuestMovie(ExStartScenePlayer.SCENE_TIAT_SUCCESS));
        }, 1000);
    }

    private void spawnTraps() {
        NpcInstance actor = getActor();
        actor.broadcastPacket(new ExShowScreenMessage("Come out, warriors. Protect Seed of Destruction.", 5000, ScreenMessageAlign.MIDDLE_CENTER, false));
        TRAP_LOCS.forEach(trap -> actor.getReflection().addSpawnWithRespawn(TRAP_NPC_ID, trap, 0, 180));
    }
}