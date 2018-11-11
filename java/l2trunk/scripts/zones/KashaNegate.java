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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author n0nam3
 * @date 22/07/2010 18:08
 * @comment off-like KashaEye AI and Zones
 */
public class KashaNegate implements ScriptFile {
    private static final int[] _buffs = {
            6150,
            6152,
            6154
    };
    private static final String[] ZONES = {
            "[kasha1]",
            "[kasha2]",
            "[kasha3]",
            "[kasha4]",
            "[kasha5]",
            "[kasha6]",
            "[kasha7]",
            "[kasha8]"
    };
    private static final int[] mobs = {
            18812,
            18813,
            18814
    };
    private static final int _debuff = 6149;

    private static Future<?> _buffTask;
    private static final long TICK_BUFF_DELAY = 10000L;

    private static ZoneListener _zoneListener;

    private static final Map<Integer, Integer> KASHARESPAWN = new HashMap<>();

    static {
        KASHARESPAWN.put(18812, 18813);
        KASHARESPAWN.put(18813, 18814);
        KASHARESPAWN.put(18814, 18812);
    }

    @Override
    public void onLoad() {
        _zoneListener = new ZoneListener();
        for (String ZONE : ZONES) {
            int random = Rnd.get(60 * 1000 * 1, 60 * 1000 * 7);
            int message;
            Zone zone = ReflectionUtils.getZone(ZONE);

            ThreadPoolManager.getInstance().schedule(new CampDestroyTask(zone), random);
            if (random > 5 * 60000) {
                message = random - 5 * 60000;
                ThreadPoolManager.getInstance().schedule(new BroadcastMessageTask(0, zone), message);
            }
            if (random > 3 * 60000) {
                message = random - 3 * 60000;
                ThreadPoolManager.getInstance().schedule(new BroadcastMessageTask(0, zone), message);
            }
            if (random > 60000) {
                message = random - 60000;
                ThreadPoolManager.getInstance().schedule(new BroadcastMessageTask(0, zone), message);
            }
            if (random > 15000) {
                message = random - 15000;
                ThreadPoolManager.getInstance().schedule(new BroadcastMessageTask(1, zone), message);
            }
            zone.addListener(_zoneListener);
        }

        _buffTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new BuffTask(), TICK_BUFF_DELAY, TICK_BUFF_DELAY);
    }

    @Override
    public void onReload() {
        for (String ZONE : ZONES) {
            Zone zone = ReflectionUtils.getZone(ZONE);
            zone.removeListener(_zoneListener);
        }

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
                        if (m == mobs[0] && !c.isDead()) {
                            if (!_debuffed)
                                for (Creature p : zone.getInsidePlayables()) {
                                    addEffect((NpcInstance) c, p, SkillTable.getInstance().getInfo(_debuff, 1), false);
                                    _debuffed = true;
                                }
                            c.doDie(null);
                        }
                        ThreadPoolManager.getInstance().schedule(new KashaRespawn((NpcInstance) c), 10000L);
                    }
    }

    private void broadcastKashaMessage(int message, Zone zone) {
        for (Creature c : zone.getInsidePlayers())
            switch (message) {
                case 0:
                    c.sendPacket(Msg.I_CAN_FEEL_THAT_THE_ENERGY_BEING_FLOWN_IN_THE_KASHA_S_EYE_IS_GETTING_STRONGER_RAPIDLY);
                    break;
                case 1:
                    c.sendPacket(Msg.KASHA_S_EYE_PITCHES_AND_TOSSES_LIKE_IT_S_ABOUT_TO_EXPLODE);
                    break;
            }
    }

    private class KashaRespawn extends RunnableImpl {
        private final NpcInstance _n;

        KashaRespawn(NpcInstance n) {
            _n = n;
        }

        @Override
        public void runImpl() {
            int npcId = getRealNpcId(_n);
            if (KASHARESPAWN.containsKey(npcId))
                changeAura(_n, KASHARESPAWN.get(npcId));
        }
    }

    private class CampDestroyTask extends RunnableImpl {
        private final Zone _zone;

        CampDestroyTask(Zone zone) {
            _zone = zone;
        }

        @Override
        public void runImpl() {
            destroyKashaInCamp(_zone);
            ThreadPoolManager.getInstance().schedule(new CampDestroyTask(_zone), 7 * 60000L + 40000L);
            ThreadPoolManager.getInstance().schedule(new BroadcastMessageTask(0, _zone), 2 * 60000L + 40000L);
            ThreadPoolManager.getInstance().schedule(new BroadcastMessageTask(0, _zone), 4 * 60000L + 40000L);
            ThreadPoolManager.getInstance().schedule(new BroadcastMessageTask(0, _zone), 6 * 60000L + 40000L);
            ThreadPoolManager.getInstance().schedule(new BroadcastMessageTask(1, _zone), 7 * 60000L + 20000L);
        }
    }

    private class BroadcastMessageTask extends RunnableImpl {
        private final int _message;
        private final Zone _zone;

        BroadcastMessageTask(int message, Zone zone) {
            _message = message;
            _zone = zone;
        }

        @Override
        public void runImpl() {
            for (Creature c : _zone.getObjects())
                if (c.isMonster() && !c.isDead() && getRealNpcId((NpcInstance) c) == mobs[0]) {
                    broadcastKashaMessage(_message, _zone);
                    break;
                }
        }
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
            if (cha.isPlayable())
                for (int skillId : _buffs)
                    cha.getEffectList().stopEffect(skillId);
        }
    }

    private class BuffTask extends RunnableImpl {
        @Override
        public void runImpl() {
            for (String ZONE : ZONES) {
                Zone zone = ReflectionUtils.getZone(ZONE);
                NpcInstance npc = getKasha(zone);
                if (npc != null && zone != null) {
                    int curseLvl = 0;
                    int yearningLvl = 0;
                    int despairLvl = 0;
                    for (Creature c : zone.getObjects())
                        if (c.isMonster() && !c.isDead())
                            if (getRealNpcId((NpcInstance) c) == mobs[0])
                                curseLvl++;
                            else if (getRealNpcId((NpcInstance) c) == mobs[1])
                                yearningLvl++;
                            else if (getRealNpcId((NpcInstance) c) == mobs[2])
                                despairLvl++;
                    if (yearningLvl > 0 || curseLvl > 0 || despairLvl > 0)
                        for (Creature cha : zone.getInsidePlayables()) {
                            boolean casted = false;
                            if (curseLvl > 0) {
                                addEffect(npc, cha.getPlayer(), SkillTable.getInstance().getInfo(_buffs[0], curseLvl), true);
                                casted = true;
                            } else
                                cha.getEffectList().stopEffect(_buffs[0]);
                            if (yearningLvl > 0) {
                                addEffect(npc, cha.getPlayer(), SkillTable.getInstance().getInfo(_buffs[1], yearningLvl), true);
                                casted = true;
                            } else
                                cha.getEffectList().stopEffect(_buffs[1]);
                            if (despairLvl > 0) {
                                addEffect(npc, cha.getPlayer(), SkillTable.getInstance().getInfo(_buffs[2], despairLvl), true);
                                casted = true;
                            } else
                                cha.getEffectList().stopEffect(_buffs[2]);
                            if (casted && Rnd.chance(10))
                                cha.sendPacket(Msg.THE_KASHA_S_EYE_GIVES_YOU_A_STRANGE_FEELING);
                        }
                }
            }
        }
    }

    private NpcInstance getKasha(Zone zone) {
        List<NpcInstance> mob = new ArrayList<>();
        for (Creature c : zone.getObjects())
            if (c.isMonster() && !c.isDead())
                for (int k : mobs)
                    if (k == getRealNpcId((NpcInstance) c))
                        mob.add((NpcInstance) c);
        return mob.size() > 0 ? mob.get(Rnd.get(mob.size())) : null;
    }

    private void addEffect(NpcInstance actor, Creature player, Skill skill, boolean animation) {
        List<Effect> effect = player.getEffectList().getEffectsBySkillId(skill.getId());
        if (skill.getLevel() > 0) {
            if (effect != null)
                effect.get(0).exit();
            skill.getEffects(actor, player, false, false);
            if (animation)
                actor.broadcastPacket(new MagicSkillUse(actor, player, skill.getId(), skill.getLevel(), skill.getHitTime(), 0));
        }
    }

    private int getRealNpcId(NpcInstance npc) {
        if (npc.getDisplayId() > 0)
            return npc.getDisplayId();
        else
            return npc.getNpcId();
    }
}
