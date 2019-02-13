package l2trunk.scripts.instances;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlEvent;
import l2trunk.gameserver.instancemanager.SoIManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.ExStartScenePlayer;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.stats.Stats;
import l2trunk.gameserver.stats.funcs.FuncSet;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class HeartInfinityAttack extends Reflection {
    private static final int AliveTumor = 18708;
    private static final int DeadTumor = 32535;
    private static final int Ekimus = 29150;
    private static final int Hound = 29151;
    private static final int RegenerationCoffin = 18710;
    private final List<NpcInstance> hounds = new ArrayList<>(2);
    private final DeathListener deathListener = new DeathListener();
    private long tumorRespawnTime;
    private NpcInstance ekimus;
    private boolean houndBlocked = false;
    private boolean conquestBegun = false;
    private boolean conquestEnded = false;
    private Player invoker;
    private ScheduledFuture<?> timerTask;
    private long startTime;
    private ScheduledFuture<?> ekimusIdleTask;
    private boolean notifiedEkimusIdle = false;

    @Override
    protected void onCreate() {
        super.onCreate();
        spawnByGroup("soi_hoi_attack_init");
        tumorRespawnTime = 150 * 1000L;
    }

    public void notifyEchmusEntrance(Player leader) {
        if (conquestBegun)
            return;
        conquestBegun = true;
        invoker = leader;
        getPlayers().forEach(p -> p.sendPacket(
                new ExShowScreenMessage(NpcString.YOU_WILL_PARTICIPATE_IN_S1_S2_SHORTLY, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "#" + NpcString.HEART_OF_IMMORTALITY.getId(), "#" + NpcString.ATTACK.getId())));
        ThreadPoolManager.INSTANCE.schedule(() -> {
            getPlayers().forEach(p -> p.showQuestMovie(ExStartScenePlayer.SCENE_ECHMUS_OPENING));

            ThreadPoolManager.INSTANCE.schedule(this::conquestBegins, 62500L); // movie time
        }, 20000L);
    }

    private void conquestBegins() {
        despawnByGroup("soi_hoi_attack_init");
        spawnByGroup("soi_hoi_attack_mob_1");
        spawnByGroup("soi_hoi_attack_mob_2");
        spawnByGroup("soi_hoi_attack_mob_3");
        spawnByGroup("soi_hoi_attack_mob_4");
        spawnByGroup("soi_hoi_attack_mob_5");
        spawnByGroup("soi_hoi_attack_mob_6");
        spawnByGroup("soi_hoi_attack_tumors");
        getAllByNpcId(AliveTumor, true).forEach(n -> n.setCurrentHp(n.getMaxHp() * .5, false));
        spawnByGroup("soi_hoi_attack_wards");
        //spawnByGroup("soi_hoi_attack_echmus");
        ekimus = addSpawnWithoutRespawn(Ekimus, new Location(-179537, 208854, -15504, 16384));
        hounds.add(addSpawnWithoutRespawn(Hound, new Location(-179224, 209624, -15504, 16384)));
        hounds.add(addSpawnWithoutRespawn(Hound, new Location(-179880, 209464, -15504, 16384)));
        handleEkimusStats();
        getZone("[soi_hoi_attack_attackup_1]").setActive(true);
        getZone("[soi_hoi_attack_attackup_2]").setActive(true);
        getZone("[soi_hoi_attack_attackup_3]").setActive(true);
        getZone("[soi_hoi_attack_attackup_4]").setActive(true);
        getZone("[soi_hoi_attack_attackup_5]").setActive(true);
        getZone("[soi_hoi_attack_attackup_6]").setActive(true);
        getZone("[soi_hoi_attack_defenceup_1]").setActive(true);
        getZone("[soi_hoi_attack_defenceup_2]").setActive(true);
        getZone("[soi_hoi_attack_defenceup_3]").setActive(true);
        getZone("[soi_hoi_attack_defenceup_4]").setActive(true);
        getZone("[soi_hoi_attack_defenceup_5]").setActive(true);
        getZone("[soi_hoi_attack_defenceup_6]").setActive(true);
        getDoor(14240102).openMe();
        showScreenMessage(NpcString.YOU_CAN_HEAR_THE_UNDEAD_OF_EKIMUS_RUSHING_TOWARD_YOU, "#" + NpcString.HEART_OF_IMMORTALITY.getId(), "#" + NpcString.ATTACK.getId());
        if (invoker != null) {
            ekimus.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, invoker, 50000);
            Functions.npcShout(ekimus, NpcString.I_SHALL_ACCEPT_YOUR_CHALLENGE_S1_COME_AND_DIE_IN_THE_ARMS_OF_IMMORTALITY, invoker.getName());
        }
        invokeDeathListener();
        startTime = System.currentTimeMillis();
        timerTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(new TimerTask(), 298 * 1000L, 5 * 60 * 1000L);
    }

    private void invokeDeathListener() {
        getNpcs().forEach(npc -> npc.addListener(deathListener));
    }

    private void notifyTumorDeath() {
        if (getAliveTumorCount() < 1) {
            houndBlocked = true;
            hounds.forEach(h -> h.setBlock(true));
            showScreenMessage(NpcString.WITH_ALL_CONNECTIONS_TO_THE_TUMOR_SEVERED_EKIMUS_HAS_LOST_ITS_POWER_TO_CONTROL_THE_FERAL_HOUND);
        } else {
            showScreenMessage(NpcString.THE_TUMOR_INSIDE_S1_THAT_HAS_PROVIDED_ENERGY_N_TO_EKIMUS_IS_DESTROYED, "#" + NpcString.HEART_OF_IMMORTALITY.getId());
        }
        handleEkimusStats();
    }

    private void showScreenMessage(NpcString str, String... params) {
        getPlayers().forEach(p ->
                p.sendPacket(new ExShowScreenMessage(str, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, params)));

    }

    private void notifyTumorRevival() {
        if (getAliveTumorCount() == 1 && houndBlocked) {
            houndBlocked = false;
            hounds.forEach(h -> h.setBlock(false));
            showScreenMessage(NpcString.WITH_THE_CONNECTION_TO_THE_TUMOR_RESTORED_EKIMUS_HAS_REGAINED_CONTROL_OVER_THE_FERAL_HOUND);
        } else
            showScreenMessage(NpcString.THE_TUMOR_INSIDE_S1_HAS_BEEN_COMPLETELY_RESURRECTED_N_AND_STARTED_TO_ENERGIZE_EKIMUS_AGAIN, "#" + NpcString.HEART_OF_IMMORTALITY.getId());
        handleEkimusStats();
    }

    private int getAliveTumorCount() {
        return (int) getAllByNpcId(AliveTumor, true).count();
    }

    public void notifyCoffinDeath() {
        tumorRespawnTime += 8 * 1000L;
    }

    private void handleEkimusStats() {
        double[] a = getStatMultiplier();
        ekimus.removeStatsOwner(this);
        ekimus.addStatFunc(new FuncSet(Stats.POWER_ATTACK, 0x30, this, ekimus.getTemplate().basePAtk * 3)); // constant
        ekimus.addStatFunc(new FuncSet(Stats.MAGIC_ATTACK, 0x30, this, ekimus.getTemplate().baseMAtk * 10)); // constant
        ekimus.addStatFunc(new FuncSet(Stats.POWER_DEFENCE, 0x30, this, ekimus.getTemplate().basePDef * a[1]));
        ekimus.addStatFunc(new FuncSet(Stats.MAGIC_DEFENCE, 0x30, this, ekimus.getTemplate().baseMDef * a[0]));
        ekimus.addStatFunc(new FuncSet(Stats.REGENERATE_HP_RATE, 0x30, this, ekimus.getTemplate().baseHpReg * a[2]));
    }

    private double[] getStatMultiplier() {
        double[] a = new double[3];
        switch (getAliveTumorCount()) {
            case 6:
                a[0] = 2; // Mdef
                a[1] = 1; // Pdef
                a[2] = 4; // HPregen
                break;
            case 5:
                a[0] = 1.9; // Mdef
                a[1] = 0.9; // Pdef
                a[2] = 3.5; // HPregen
                break;
            case 4:
                a[0] = 1.5; // Mdef
                a[1] = 0.6; // Pdef
                a[2] = 3.0; // HPregen
                break;
            case 3:
                a[0] = 1.0; // Mdef
                a[1] = 0.4; // Pdef
                a[2] = 2.5; // HPregen
                break;
            case 2:
                a[0] = 0.7; // Mdef
                a[1] = 0.3; // Pdef
                a[2] = 2.0; // HPregen
                break;
            case 1:
                a[0] = 0.3; // Mdef
                a[1] = 0.15; // Pdef
                a[2] = 1.0; // HPregen
                break;
            case 0:
                a[0] = 0.12; // Mdef
                a[1] = 0.06; // Pdef
                a[2] = 0.25; // HPregen
                break;
        }
        return a;
    }

    private void conquestConclusion(boolean win) {
        if (timerTask != null)
            timerTask.cancel(false);
        conquestEnded = true;
        despawnByGroup("soi_hoi_attack_wards");
        despawnByGroup("soi_hoi_attack_mob_1");
        despawnByGroup("soi_hoi_attack_mob_2");
        despawnByGroup("soi_hoi_attack_mob_3");
        despawnByGroup("soi_hoi_attack_mob_4");
        despawnByGroup("soi_hoi_attack_mob_5");
        despawnByGroup("soi_hoi_attack_mob_6");
        despawnByGroup("soi_hoi_attack_bosses");
        if (ekimus != null && !ekimus.isDead()) {
            hounds.forEach(GameObject::deleteMe);
            ekimus.deleteMe();
        }
        startCollapseTimer(15 * 60 * 1000L);
        if (win)
            setReenterTime(System.currentTimeMillis());
        getPlayers().forEach(p -> {
            p.sendPacket(new SystemMessage2(SystemMsg.THIS_DUNGEON_WILL_EXPIRE_IN_S1_MINUTES).addInteger(15));
            p.showQuestMovie(win ? ExStartScenePlayer.SCENE_ECHMUS_SUCCESS : ExStartScenePlayer.SCENE_ECHMUS_FAIL);
        });
        showScreenMessage(win ? NpcString.CONGRATULATIONS_YOU_HAVE_SUCCEEDED_AT_S1_S2_THE_INSTANCE_WILL_SHORTLY_EXPIRE : NpcString.YOU_HAVE_FAILED_AT_S1_S2, "#" + NpcString.HEART_OF_IMMORTALITY.getId(), "#" + NpcString.ATTACK.getId());
        getNpcs().filter(npc -> List.of(AliveTumor, DeadTumor, RegenerationCoffin).contains(npc.getNpcId()))
                .forEach(GameObject::deleteMe);
    }

    public void notifyEkimusAttack() {
        if (ekimusIdleTask != null) {
            ekimusIdleTask.cancel(false);
            ekimusIdleTask = null;
            notifiedEkimusIdle = false;
        }
    }

    public void notifyEkimusIdle() {
        if (notifiedEkimusIdle)
            return;
        notifiedEkimusIdle = true;
        getPlayers().forEach(p ->
                p.sendPacket(new ExShowScreenMessage(NpcString.THERE_IS_NO_PARTY_CURRENTLY_CHALLENGING_EKIMUS, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, "180")));
        ekimusIdleTask = ThreadPoolManager.INSTANCE.schedule(() -> conquestConclusion(false), 180000L);
    }

    public void notifyEkimusRoomEntrance() {
        getZone("[soi_hoi_attack_echmusroom]").getInsidePlayables().forEach(p ->
                p.teleToLocation(new Location(-179537, 211233, -15472)));
        ThreadPoolManager.INSTANCE.schedule(() -> showScreenMessage(NpcString.EKIMUS_HAS_SENSED_ABNORMAL_ACTIVITY), 10000L);
    }

    @Override
    protected void onCollapse() {
        if (timerTask != null)
            timerTask.cancel(false);
        super.onCollapse();
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (self instanceof NpcInstance) {
                if (self.getNpcId() == AliveTumor) {
                    ((NpcInstance) self).dropItem(((Playable)killer).getPlayer(), 13797, Rnd.get(2, 5));
                    NpcInstance deadTumor = addSpawnWithoutRespawn(DeadTumor, self);
                    self.deleteMe();
                    notifyTumorDeath();
                    //Schedule tumor revival
                    ThreadPoolManager.INSTANCE.schedule(new TumorRevival(deadTumor), tumorRespawnTime);
                    // Schedule regeneration coffins spawn
                    ThreadPoolManager.INSTANCE.schedule(new RegenerationCoffinSpawn(deadTumor), 20000L);
                } else if (self.getNpcId() == Ekimus) {
                    conquestConclusion(true);
                    SoIManager.notifyEkimusKill();
                }
            }
        }
    }

    private class TumorRevival extends RunnableImpl {
        final NpcInstance _deadTumor;

        TumorRevival(NpcInstance deadTumor) {
            _deadTumor = deadTumor;
        }

        @Override
        public void runImpl() {
            if (conquestEnded)
                return;
            NpcInstance tumor = addSpawnWithoutRespawn(AliveTumor, _deadTumor);
            tumor.setCurrentHp(tumor.getMaxHp() * .25, false);
            notifyTumorRevival();
            _deadTumor.deleteMe();
            invokeDeathListener();
        }
    }

    private class RegenerationCoffinSpawn extends RunnableImpl {
        final NpcInstance _deadTumor;

        RegenerationCoffinSpawn(NpcInstance deadTumor) {
            _deadTumor = deadTumor;
        }

        @Override
        public void runImpl() {
            if (conquestEnded)
                return;
            for (int i = 0; i < 4; i++)
                addSpawnWithoutRespawn(RegenerationCoffin, Location.of(_deadTumor.getLoc().x, _deadTumor.getLoc().y, _deadTumor.getLoc().z, true), 250);
        }
    }

    private class TimerTask extends RunnableImpl {
        @Override
        public void runImpl() {
            long time = (startTime + 25 * 60 * 1000L - System.currentTimeMillis()) / 60000;
            if (time == 0)
                conquestConclusion(false);
            else {
                if (time == 20)
                    spawnByGroup("soi_hoi_attack_bosses");
                ExShowScreenMessage msg = new ExShowScreenMessage(NpcString.S1_MINUTES_ARE_REMAINING, 8000, ExShowScreenMessage.ScreenMessageAlign.MIDDLE_CENTER, false, 1, -1, false, String.valueOf((startTime + 25 * 60 * 1000L - System.currentTimeMillis()) / 60000));
                getPlayers().forEach(p -> p.sendPacket(msg));
            }
        }
    }
}