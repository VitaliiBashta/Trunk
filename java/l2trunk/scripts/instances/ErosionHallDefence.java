package l2trunk.scripts.instances;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.EventTrigger;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.quests._697_DefendtheHallofErosion;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class ErosionHallDefence extends Reflection {
    private static final int AliveTumor = 18708;
    private static final int DeadTumor = 32535;
    private static final int UnstableSeed = 32541;
    private static final int RegenerationCoffin = 18709;
    private static final int SoulWagon = 25636;
    private final List<Integer> zoneEventTriggers = ArrayUtils.createAscendingList(14240001, 14240012);
    private final ZoneListener startZoneListener = new ZoneListener();
    private final DeathListener deathListener = new DeathListener();
    private boolean conquestBegun = false;
    private ScheduledFuture<?> timerTask, agressionTask, coffinSpawnTask, aliveTumorSpawnTask, failureTask;
    private long startTime;
    private long tumorRespawnTime;
    private boolean conquestEnded = false;
    private int tumorKillCount;
    private boolean soulwagonSpawned = false;

    @Override
    protected void onCreate() {
        super.onCreate();
        getZone("[soi_hoe_attack_pc_vicera_7]").addListener(startZoneListener);
        tumorRespawnTime = 3 * 60 * 1000L;
        tumorKillCount = 0;
    }

    private void conquestBegins() {
        getPlayers().forEach(p ->
                p.sendPacket(new ExShowScreenMessage(NpcString.YOU_CAN_HEAR_THE_UNDEAD_OF_EKIMUS_RUSHING_TOWARD_YOU, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HALL_OF_EROSION.getId(), "#" + NpcString.DEFEND.getId())));
        spawnByGroup("soi_hoe_defence_lifeseed");
        spawnByGroup("soi_hoe_defence_tumor");
        spawnByGroup("soi_hoe_defence_wards");
        invokeDeathListener();
        // Rooms
        spawnByGroup("soi_hoe_defence_mob_1");
        spawnByGroup("soi_hoe_defence_mob_2");
        spawnByGroup("soi_hoe_defence_mob_3");
        spawnByGroup("soi_hoe_defence_mob_4");
        spawnByGroup("soi_hoe_defence_mob_5");
        spawnByGroup("soi_hoe_defence_mob_6");
        spawnByGroup("soi_hoe_defence_mob_7");
        spawnByGroup("soi_hoe_defence_mob_8");
        agressionTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                if (!conquestEnded)
                    notifyAttackSeed();
            }
        }, 15000L, 25000L);
        coffinSpawnTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                if (!conquestEnded)
                    getAllByNpcId(DeadTumor, true).forEach(npc -> spawnCoffin(npc));
            }
        }, 1000L, 60000L);
        aliveTumorSpawnTask = ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                if (!conquestEnded) {
                    despawnByGroup("soi_hoe_defence_tumor");
                    spawnByGroup("soi_hoe_defence_alivetumor");
                    handleTumorHp(0.5);
                    getPlayers().forEach(p ->
                            p.sendPacket(new ExShowScreenMessage(NpcString.THE_TUMOR_INSIDE_S1_HAS_COMPLETELY_REVIVED_, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HALL_OF_EROSION.getId())));
                    invokeDeathListener();
                }
            }
        }, tumorRespawnTime);

        startTime = System.currentTimeMillis();
        timerTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new TimerTask(), 298 * 1000L, 5 * 60 * 1000L);
    }

    private void notifyAttackSeed() {
        getNpcs().forEach(npc -> {
            NpcInstance seed = getNearestSeed(npc);
            if (seed != null) {
                if (npc.getAI().getIntention() == CtrlIntention.AI_INTENTION_ACTIVE) {
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, seed, 100);
                    ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {

                            npc.getAggroList().clear(true);
                            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                            npc.moveToLocation(Location.findAroundPosition(npc, 400), 0, false);
                        }
                    }, 7000L);
                }
            }
        });
    }

    public void notifyCoffinDeath() {
        tumorRespawnTime -= 5 * 1000L;
    }

    private void spawnCoffin(NpcInstance tumor) {
        addSpawnWithoutRespawn(RegenerationCoffin, new Location(tumor.getLoc().x, tumor.getLoc().y, tumor.getLoc().z, Location.getRandomHeading()), 250);
    }

    private NpcInstance getNearestSeed(NpcInstance mob) {
        return mob.getAroundNpc(900, 300)
                .filter(npc -> npc.getNpcId() == UnstableSeed)
                .filter(npc -> mob.getZone(Zone.ZoneType.poison) == npc.getZone(Zone.ZoneType.poison))
                .findFirst().orElse(null);
    }

    private void invokeDeathListener() {
        getNpcs().forEach(npc -> npc.addListener(deathListener));
    }

    private void conquestConclusion(boolean win) {
        if (conquestEnded)
            return;
        cancelTimers();
        conquestEnded = true;
        clearReflection(15, true);
        if (win)
            setReenterTime(System.currentTimeMillis());
        getPlayers().forEach(p -> {
            if (win) {
                QuestState qs = p.getQuestState(_697_DefendtheHallofErosion.class);
                if (qs != null && qs.getCond() == 1)
                    qs.set("defenceDone", 1);
            }
            p.sendPacket(new ExShowScreenMessage(win ? NpcString.CONGRATULATIONS_YOU_HAVE_SUCCEEDED_AT_S1_S2_THE_INSTANCE_WILL_SHORTLY_EXPIRE : NpcString.YOU_HAVE_FAILED_AT_S1_S2, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HALL_OF_EROSION.getId(), "#" + NpcString.DEFEND.getId()));
        });
    }

    private void handleTumorHp(double percent) {
        getAllByNpcId(AliveTumor, true).forEach(npc ->
                npc.setCurrentHp(npc.getMaxHp() * percent, false));
    }

    private void notifyTumorDeath() {
        tumorKillCount++;
        if (tumorKillCount > 4 && !soulwagonSpawned) {// 16
            soulwagonSpawned = true;
            spawnByGroup("soi_hoe_defence_soulwagon");
            getAllByNpcId(SoulWagon, true).forEach(npc -> {
                Functions.npcShout(npc, NpcString.HA_HA_HA);
                NpcInstance seed = getNearestSeed(npc);
                if (seed != null)
                    npc.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, seed, 100);
                rescheduleFailureTask(180000L);
            });
            invokeDeathListener();
        }
    }

    private void rescheduleFailureTask(long time) {
        if (failureTask != null) {
            failureTask.cancel(false);
            failureTask = null;
        }
        failureTask = ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
            @Override
            public void runImpl() {
                conquestConclusion(false);
            }
        }, time);
    }

    private void cancelTimers() {
        if (timerTask != null)
            timerTask.cancel(false);
        if (agressionTask != null)
            agressionTask.cancel(false);
        if (coffinSpawnTask != null)
            coffinSpawnTask.cancel(false);
        if (aliveTumorSpawnTask != null)
            aliveTumorSpawnTask.cancel(false);
        if (failureTask != null)
            failureTask.cancel(false);
    }

    @Override
    public void onPlayerEnter(Player player) {
        super.onPlayerEnter(player);
        for (int i : zoneEventTriggers)
            player.sendPacket(new EventTrigger(i, true));
    }

    @Override
    protected void onCollapse() {
        cancelTimers();
        super.onCollapse();
    }

    public class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
            if (!conquestBegun) {
                conquestBegun = true;
                conquestBegins();
            }
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (!self.isNpc())
                return;
            if (self.getNpcId() == AliveTumor) {
                ((NpcInstance) self).dropItem(killer.getPlayer(), 13797, Rnd.get(2, 5));
                final NpcInstance deadTumor = addSpawnWithoutRespawn(DeadTumor, self.getLoc(), 0);
                notifyTumorDeath();
                self.deleteMe();
                getPlayers().forEach(p ->
                        p.sendPacket(new ExShowScreenMessage(NpcString.THE_TUMOR_INSIDE_S1_HAS_BEEN_DESTROYED_NTHE_NEARBY_UNDEAD_THAT_WERE_ATTACKING_SEED_OF_LIFE_START_LOSING_THEIR_ENERGY_AND_RUN_AWAY, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HALL_OF_EROSION.getId())));
                ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
                    @Override
                    public void runImpl() {
                        deadTumor.deleteMe();
                        addSpawnWithoutRespawn(AliveTumor, deadTumor.getLoc(), 0);
                        handleTumorHp(0.25);
                        invokeDeathListener();
                        getPlayers().forEach(p ->
                                p.sendPacket(new ExShowScreenMessage(NpcString.THE_TUMOR_INSIDE_S1_HAS_COMPLETELY_REVIVED_, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HALL_OF_EROSION.getId())));
                    }
                }, tumorRespawnTime);
            } else if (self.getNpcId() == SoulWagon) {
                if (getAllByNpcId(SoulWagon, true).count() > 0)
                    rescheduleFailureTask(60000L);
                else
                    conquestConclusion(true);
            }
        }
    }

    private class TimerTask extends RunnableImpl {
        @Override
        public void runImpl() {
            long time = (startTime + 25 * 60 * 1000L - System.currentTimeMillis()) / 60000;
            if (time == 0)
                conquestConclusion(false);
            else
                getPlayers().forEach(p ->
                        p.sendPacket(new ExShowScreenMessage(NpcString.S1_MINUTES_ARE_REMAINING, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, String.valueOf((startTime + 25 * 60 * 1000L - System.currentTimeMillis()) / 60000))));
        }
    }
}
