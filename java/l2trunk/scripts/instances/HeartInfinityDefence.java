package l2trunk.scripts.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;
import l2trunk.scripts.quests._698_BlocktheLordsEscape;

import java.util.concurrent.ScheduledFuture;

public final class HeartInfinityDefence extends Reflection {
    private static final int DeadTumor = 32535;
    private static final int AliveTumor = 18708;
    private static final int RegenerationCoffin = 18709;
    private static final int SoulWagon = 22523;
    private static final int EchmusCoffin = 18713;
    private static final int maxCoffins = 20;
    private final DeathListener deathListener = new DeathListener();
    private ScheduledFuture<?> timerTask = null, wagonSpawnTask = null, coffinSpawnTask = null, aliveTumorSpawnTask = null;
    private boolean conquestEnded = false;
    private long startTime = 0;
    private long tumorRespawnTime = 0;
    private long wagonRespawnTime = 0;
    private int coffinsCreated = 0;
    private NpcInstance preawakenedEchmus = null;

    @Override
    protected void onCreate() {
        super.onCreate();
        tumorRespawnTime = 3 * 60 * 1000L;
        wagonRespawnTime = 60 * 1000L;
        coffinsCreated = 0;
        ThreadPoolManager.INSTANCE.schedule(this::conquestBegins, 20000L);
    }

    private void conquestBegins() {
        getPlayers().forEach(p ->
                p.sendPacket(new ExShowScreenMessage(NpcString.YOU_CAN_HEAR_THE_UNDEAD_OF_EKIMUS_RUSHING_TOWARD_YOU, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HEART_OF_IMMORTALITY.getId(), "#" + NpcString.DEFEND.getId())));
        spawnByGroup("soi_hoi_defence_mob_1");
        spawnByGroup("soi_hoi_defence_mob_2");
        spawnByGroup("soi_hoi_defence_mob_3");
        spawnByGroup("soi_hoi_defence_mob_4");
        spawnByGroup("soi_hoi_defence_mob_5");
        spawnByGroup("soi_hoi_defence_mob_6");
        spawnByGroup("soi_hoi_defence_tumors");
        spawnByGroup("soi_hoi_defence_wards");
        getDoor(14240102).openMe();
        preawakenedEchmus = addSpawnWithoutRespawn(29161, new Location(-179534, 208510, -15496, 16342));
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
                    despawnByGroup("soi_hoi_defence_tumors");
                    spawnByGroup("soi_hoi_defence_alivetumors");
                    handleTumorHp(0.5);
                    getPlayers().forEach(p ->
                            p.sendPacket(new ExShowScreenMessage(NpcString.THE_TUMOR_INSIDE_S1_HAS_COMPLETELY_REVIVED__, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HEART_OF_IMMORTALITY.getId())));
                    invokeDeathListener();
                }
            }
        }, tumorRespawnTime);
        wagonSpawnTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new RunnableImpl() {
            @Override
            public void runImpl() {
                addSpawnWithoutRespawn(SoulWagon, new Location(-179544, 207400, -15496));
            }
        }, 1000L, wagonRespawnTime);
        startTime = System.currentTimeMillis();
        timerTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new TimerTask(), 298 * 1000L, 5 * 60 * 1000L);
    }

    private void spawnCoffin(NpcInstance tumor) {
        addSpawnWithoutRespawn(RegenerationCoffin, tumor, 250);
    }

    private void handleTumorHp(double percent) {
        getAllByNpcId(AliveTumor, true).forEach(npc ->
                npc.setCurrentHp(npc.getMaxHp() * percent, false));
    }

    private void invokeDeathListener() {
        getNpcs().forEach(npc -> npc.addListener(deathListener));
    }

    public void notifyWagonArrived() {
        coffinsCreated++;
        if (coffinsCreated == 20)
            conquestConclusion(false);
        else {
            Functions.npcShout(preawakenedEchmus, NpcString.BRING_MORE_MORE_SOULS);
            getPlayers().forEach(p ->
                    p.sendPacket(new ExShowScreenMessage(NpcString.THE_SOUL_COFFIN_HAS_AWAKENED_EKIMUS, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, String.valueOf(maxCoffins - coffinsCreated))));
            addSpawnWithoutRespawn(EchmusCoffin, getZone("[soi_hoi_attack_echmusroom]").getTerritory().getRandomLoc(getGeoIndex()));
        }
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
                QuestState qs = p.getQuestState(_698_BlocktheLordsEscape.class);
                if (qs != null && qs.getCond() == 1)
                    qs.set("defenceDone");
            }
            p.sendPacket(new ExShowScreenMessage(win ? NpcString.CONGRATULATIONS_YOU_HAVE_SUCCEEDED_AT_S1_S2_THE_INSTANCE_WILL_SHORTLY_EXPIRE : NpcString.YOU_HAVE_FAILED_AT_S1_S2, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HEART_OF_IMMORTALITY.getId(), "#" + NpcString.DEFEND.getId()));
        });
    }

    public void notifyCoffinDeath() {
        tumorRespawnTime -= 5 * 1000L;
    }

    private void cancelTimers() {
        if (timerTask != null)
            timerTask.cancel(false);
        if (coffinSpawnTask != null)
            coffinSpawnTask.cancel(false);
        if (aliveTumorSpawnTask != null)
            aliveTumorSpawnTask.cancel(false);
        if (wagonSpawnTask != null)
            wagonSpawnTask.cancel(false);
    }

    @Override
    protected void onCollapse() {
        cancelTimers();
        super.onCollapse();
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (self instanceof NpcInstance) {
                if (self.getNpcId() == AliveTumor) {
                    ((NpcInstance) self).dropItem(killer.getPlayer(), 13797, Rnd.get(2, 5));
                    final NpcInstance deadTumor = addSpawnWithoutRespawn(DeadTumor, self);
                    wagonRespawnTime += 10000L;
                    self.deleteMe();
                    getPlayers().forEach(p ->
                            p.sendPacket(new ExShowScreenMessage(NpcString.THE_TUMOR_INSIDE_S1_HAS_BEEN_DESTROYED_NTHE_SPEED_THAT_EKIMUS_CALLS_OUT_HIS_PREY_HAS_SLOWED_DOWN, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HEART_OF_IMMORTALITY.getId())));
                    ThreadPoolManager.INSTANCE.schedule(new RunnableImpl() {
                        @Override
                        public void runImpl() {
                            deadTumor.deleteMe();
                            addSpawnWithoutRespawn(AliveTumor, deadTumor);
                            wagonRespawnTime -= 10000L;
                            handleTumorHp(0.25);
                            invokeDeathListener();
                            getPlayers().forEach(p ->
                                    p.sendPacket(new ExShowScreenMessage(NpcString.THE_TUMOR_INSIDE_S1_HAS_COMPLETELY_REVIVED_, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HALL_OF_EROSION.getId())));
                        }
                    }, tumorRespawnTime);
                }
            }
        }
    }

    private class TimerTask extends RunnableImpl {
        @Override
        public void runImpl() {
            long time = (startTime + 25 * 60 * 1000L - System.currentTimeMillis()) / 60000;
            if (time == 0)
                conquestConclusion(true);
            else {
                if (time == 15)
                    spawnByGroup("soi_hoi_defence_bosses");
                getPlayers().forEach(p ->
                        p.sendPacket(new ExShowScreenMessage(NpcString.S1_MINUTES_ARE_REMAINING, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, String.valueOf((startTime + 25 * 60 * 1000L - System.currentTimeMillis()) / 60000))));
            }
        }
    }
}
