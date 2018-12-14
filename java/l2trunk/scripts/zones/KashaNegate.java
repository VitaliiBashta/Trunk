package l2trunk.scripts.zones;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.*;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

public final class KashaNegate implements ScriptFile {
    private static final List<Integer> BUFFS = List.of(6150, 6152, 6154);
    private static final List<String> ZONES = List.of(
            "[kasha1]",
            "[kasha2]",
            "[kasha3]",
            "[kasha4]",
            "[kasha5]",
            "[kasha6]",
            "[kasha7]",
            "[kasha8]");
    private static final List<Integer> mobs = List.of(18812, 18813, 18814);
    private static final int _debuff = 6149;
    private static final long TICK_BUFF_DELAY = 10000L;
    private static final Map<Integer, Integer> KASHARESPAWN = Map.of(
            18812, 18813,
            18813, 18814,
            18814, 18812);
    private static Future<?> _buffTask;
    private static ZoneListener removeListener;

    @Override
    public void onLoad() {
        removeListener = new ZoneListener();
        ZONES.forEach(ZONE -> {
            int random = Rnd.get(60 * 1000, 60 * 1000 * 7);
            int message;
            Zone zone = ReflectionUtils.getZone(ZONE);

            ThreadPoolManager.INSTANCE.schedule(new CampDestroyTask(zone), random);
            if (random > 5 * 60000) {
                message = random - 5 * 60000;
                ThreadPoolManager.INSTANCE.schedule(new BroadcastMessageTask(true, zone), message);
            }
            if (random > 3 * 60000) {
                message = random - 3 * 60000;
                ThreadPoolManager.INSTANCE.schedule(new BroadcastMessageTask(true, zone), message);
            }
            if (random > 60000) {
                message = random - 60000;
                ThreadPoolManager.INSTANCE.schedule(new BroadcastMessageTask(true, zone), message);
            }
            if (random > 15000) {
                message = random - 15000;
                ThreadPoolManager.INSTANCE.schedule(new BroadcastMessageTask(false, zone), message);
            }
            zone.addListener(removeListener);
        });

        _buffTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new BuffTask(), TICK_BUFF_DELAY, TICK_BUFF_DELAY);
    }

    @Override
    public void onReload() {
        ZONES.forEach(z -> ReflectionUtils.getZone(z).removeListener(removeListener));

        if (_buffTask != null) {
            _buffTask.cancel(false);
            _buffTask = null;
        }
    }

    @Override
    public void onShutdown() {
    }

    private void changeAura(NpcInstance actor, int npcId) {
        if (npcId != actor.getDisplayId()) {
            actor.setDisplayId(npcId);
            DeleteObject d = new DeleteObject(actor);
            L2GameServerPacket su = actor.makeStatusUpdate(StatusUpdate.CUR_HP, StatusUpdate.MAX_HP);
            for (Player player : World.getAroundPlayers(actor)) {
                player.sendPacket(d, new NpcInfo(actor, player));
                if (player.getTarget() == actor) {
                    player.setTarget(null);
                    player.setTarget(actor);
                    player.sendPacket(su);
                }
            }
        }
    }

    private void destroyKashaInCamp(Zone zone) {
        boolean _debuffed = false;
        for (Creature c : zone.getObjects())
            if (c.isMonster())
                for (int m : mobs)
                    if (m == getRealNpcId((NpcInstance) c)) {
                        if (m == mobs.get(0) && !c.isDead()) {
                            if (!_debuffed)
                                for (Creature p : zone.getInsidePlayables()) {
                                    addEffect((NpcInstance) c, p, _debuff,1, false);
                                    _debuffed = true;
                                }
                            c.doDie(null);
                        }
                        ThreadPoolManager.INSTANCE.schedule(new KashaRespawn((NpcInstance) c), 10000L);
                    }
    }

    private void broadcastKashaMessage(boolean message, Zone zone) {
        SystemMessage msg = message ?
                Msg.I_CAN_FEEL_THAT_THE_ENERGY_BEING_FLOWN_IN_THE_KASHA_S_EYE_IS_GETTING_STRONGER_RAPIDLY :
                Msg.KASHA_S_EYE_PITCHES_AND_TOSSES_LIKE_IT_S_ABOUT_TO_EXPLODE;
        zone.getInsidePlayers().forEach(c -> c.sendPacket(msg));
    }

    private NpcInstance getKasha(Zone zone) {
        List<NpcInstance> mobs = new ArrayList<>();
        for (Creature c : zone.getObjects())
            if (c.isMonster() && !c.isDead())
                for (int k : KashaNegate.mobs)
                    if (k == getRealNpcId((NpcInstance) c))
                        mobs.add((NpcInstance) c);
        return mobs.isEmpty() ? null : Rnd.get(mobs);
    }

    private void addEffect(NpcInstance actor, Creature player, int skillId, int skillLvl, boolean animation) {
        List<Effect> effect = player.getEffectList().getEffectsBySkillId(skillId);
        if (effect != null)
            effect.get(0).exit();
        if (skillLvl > 0) {
            SkillTable.INSTANCE.getInfo(skillId, skillLvl).getEffects(actor, player);
            if (animation)
                actor.broadcastPacket(new MagicSkillUse(actor, player, skillId));
        }
    }

    private int getRealNpcId(NpcInstance npc) {
        if (npc.getDisplayId() > 0)
            return npc.getDisplayId();
        else
            return npc.getNpcId();
    }

    private class KashaRespawn extends RunnableImpl {
        private final NpcInstance n;

        KashaRespawn(NpcInstance n) {
            this.n = n;
        }

        @Override
        public void runImpl() {
            int npcId = getRealNpcId(n);
            if (KASHARESPAWN.containsKey(npcId))
                changeAura(n, KASHARESPAWN.get(npcId));
        }
    }

    private class CampDestroyTask extends RunnableImpl {
        private final Zone zone;

        CampDestroyTask(Zone zone) {
            this.zone = zone;
        }

        @Override
        public void runImpl() {
            destroyKashaInCamp(zone);
            ThreadPoolManager.INSTANCE.schedule(new CampDestroyTask(zone), 7 * 60000L + 40000L);
            ThreadPoolManager.INSTANCE.schedule(new BroadcastMessageTask(true, zone), 2 * 60000L + 40000L);
            ThreadPoolManager.INSTANCE.schedule(new BroadcastMessageTask(true, zone), 4 * 60000L + 40000L);
            ThreadPoolManager.INSTANCE.schedule(new BroadcastMessageTask(true, zone), 6 * 60000L + 40000L);
            ThreadPoolManager.INSTANCE.schedule(new BroadcastMessageTask(false, zone), 7 * 60000L + 20000L);
        }
    }

    private class BroadcastMessageTask extends RunnableImpl {
        private final boolean message;
        private final Zone zone;

        BroadcastMessageTask(boolean message, Zone zone) {
            this.message = message;
            this.zone = zone;
        }

        @Override
        public void runImpl() {
            if (zone.getObjects().stream()
                    .filter(GameObject::isMonster)
                    .filter(c -> !c.isDead())
                    .map(c -> (NpcInstance) c)
                    .anyMatch(c -> getRealNpcId(c) == mobs.get(0))) {
                broadcastKashaMessage(message, zone);
            }
        }
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
        }

        @Override
        public void onZoneLeave(Zone zone, Creature pl) {
            if (pl.isPlayable())
                BUFFS.forEach(skillId -> pl.getEffectList().stopEffect(skillId));
        }
    }

    private class BuffTask extends RunnableImpl {
        @Override
        public void runImpl() {
            for (String ZONE : ZONES) {
                Zone zone = ReflectionUtils.getZone(ZONE);
                NpcInstance npc = getKasha(zone);
                if (npc != null) {
                    int curseLvl = 0;
                    int yearningLvl = 0;
                    int despairLvl = 0;
                    for (Creature c : zone.getObjects())
                        if (c.isMonster() && !c.isDead())
                            if (getRealNpcId((NpcInstance) c) == mobs.get(0))
                                curseLvl++;
                            else if (getRealNpcId((NpcInstance) c) == mobs.get(1))
                                yearningLvl++;
                            else if (getRealNpcId((NpcInstance) c) == mobs.get(2))
                                despairLvl++;
                    if (yearningLvl > 0 || curseLvl > 0 || despairLvl > 0)
                        for (Creature cha : zone.getInsidePlayables()) {
                            if (curseLvl > 0) {
                                addEffect(npc, cha.getPlayer(), BUFFS.get(0), curseLvl, true);
                            } else
                                cha.getEffectList().stopEffect(BUFFS.get(0));
                            if (yearningLvl > 0) {
                                addEffect(npc, cha.getPlayer(), BUFFS.get(1), yearningLvl, true);
                            } else
                                cha.getEffectList().stopEffect(BUFFS.get(1));
                            if (despairLvl > 0) {
                                addEffect(npc, cha.getPlayer(), BUFFS.get(2), despairLvl, true);
                            } else
                                cha.getEffectList().stopEffect(BUFFS.get(2));
                            if (Rnd.chance(10))
                                cha.sendPacket(Msg.THE_KASHA_S_EYE_GIVES_YOU_A_STRANGE_FEELING);
                        }
                }
            }
        }
    }
}
