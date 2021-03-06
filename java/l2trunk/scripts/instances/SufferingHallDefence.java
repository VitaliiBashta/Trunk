package l2trunk.scripts.instances;

import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.listener.actor.OnDeathListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.Reflection;
import l2trunk.gameserver.model.instances.MonsterInstance;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.ExSendUIEvent;
import l2trunk.gameserver.network.serverpackets.ExShowScreenMessage;
import l2trunk.gameserver.network.serverpackets.components.NpcString;
import l2trunk.gameserver.utils.Location;

import java.util.List;
import java.util.concurrent.ScheduledFuture;

public final class SufferingHallDefence extends Reflection {
    private static final int AliveTumor = 18704;
    private static final int DeadTumor = 18705;
    private static final int Yehan = 25665;
    private static final int RegenerationCoffin = 18706;
    private static final List<Integer> monsters = List.of(22509, 22510, 22511, 22512, 22513, 22514, 22515, AliveTumor);
    private static final Location roomCenter = new Location(-173704, 218092, -9562, 27768);
    private final DeathListener _deathListener = new DeathListener();
    public int timeSpent;
    private int tumorIndex = 300;
    private boolean doCountCoffinNotifications = false;
    private long _savedTime = 0;
    private ScheduledFuture<?> coffinSpawnTask;
    private ScheduledFuture<?> monstersSpawnTask;
    private int stage = 1;

    @Override
    protected void onCreate() {
        super.onCreate();
        _savedTime = System.currentTimeMillis();
        timeSpent = 0;
        startDefence();
    }

    private void startDefence() {
        spawnByGroup("soi_hos_defence_tumor");
        doCountCoffinNotifications = true;
        coffinSpawnTask = ThreadPoolManager.INSTANCE.scheduleAtFixedRate(() -> addSpawnWithoutRespawn(RegenerationCoffin, Location.of(-173704, 218092, -9562, true), 250), 1000L, 10000L);
        monstersSpawnTask = ThreadPoolManager.INSTANCE.schedule(this::spawnMonsters, 60000L);
    }

    private void invokeDeathListener() {
        getNpcs().forEach(npc -> npc.addListener(_deathListener));
    }

    public void notifyCoffinActivity() {
        if (!doCountCoffinNotifications)
            return;
        tumorIndex -= 5;
        if (tumorIndex == 100)
            getPlayers().forEach(p ->
                    p.sendPacket(new ExShowScreenMessage(NpcString.THE_AREA_NEAR_THE_TUMOR_IS_FULL_OF_OMINOUS_ENERGY, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false)));
        else if (tumorIndex == 30)
            getPlayers().forEach(p ->
                    p.sendPacket(new ExShowScreenMessage(NpcString.YOU_CAN_FEEL_THE_SURGING_ENERGY_OF_DEATH_FROM_THE_TUMOR, 8000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, false, 1, -1, false)));
        if (tumorIndex <= 0) {
            if (getTumor() != null)
                getTumor().deleteMe();
            NpcInstance alivetumor = addSpawnWithoutRespawn(AliveTumor, roomCenter);
            alivetumor.setCurrentHp(alivetumor.getMaxHp() * .4, false);
            doCountCoffinNotifications = false;
            invokeDeathListener();
        }
    }

    private void spawnMonsters() {
        if (stage > 6)
            return;
        String group = null;
        switch (stage) {
            case 1:
                group = "soi_hos_defence_mobs_1";
                getZone("[soi_hos_defence_attackup_1]").setActive(true);
                getZone("[soi_hos_defence_defenceup_1]").setActive(true);
                break;
            case 2:
                group = "soi_hos_defence_mobs_2";
                getZone("[soi_hos_defence_attackup_1]").setActive(false);
                getZone("[soi_hos_defence_defenceup_1]").setActive(false);
                getZone("[soi_hos_defence_attackup_2]").setActive(true);
                getZone("[soi_hos_defence_defenceup_2]").setActive(true);
                break;
            case 3:
                group = "soi_hos_defence_mobs_3";
                getZone("[soi_hos_defence_attackup_2]").setActive(false);
                getZone("[soi_hos_defence_defenceup_2]").setActive(false);
                getZone("[soi_hos_defence_attackup_3]").setActive(true);
                getZone("[soi_hos_defence_defenceup_3]").setActive(true);
                break;
            case 4:
                group = "soi_hos_defence_mobs_4";
                getZone("[soi_hos_defence_attackup_3]").setActive(false);
                getZone("[soi_hos_defence_defenceup_3]").setActive(false);
                getZone("[soi_hos_defence_attackup_4]").setActive(true);
                getZone("[soi_hos_defence_defenceup_4]").setActive(true);
                break;
            case 5:
                group = "soi_hos_defence_mobs_5";
                getZone("[soi_hos_defence_attackup_4]").setActive(false);
                getZone("[soi_hos_defence_defenceup_4]").setActive(false);
                getZone("[soi_hos_defence_attackup_5]").setActive(true);
                getZone("[soi_hos_defence_defenceup_5]").setActive(true);
                break;
            case 6:
                doCountCoffinNotifications = false;
                group = "soi_hos_defence_brothers";
                getZone("[soi_hos_defence_attackup_5]").setActive(false);
                getZone("[soi_hos_defence_defenceup_5]").setActive(false);
                break;
            default:
                break;
        }
        stage++;
        if (group != null)
            spawnByGroup(group);
        getNpcs().filter(n -> n instanceof MonsterInstance)
                .filter(n -> (monsters.contains(n.getNpcId())))
                .forEach(n -> n.setRunning().moveToLocation(roomCenter, 200, false));
        invokeDeathListener();
    }

    private boolean checkAliveMonsters() {
        return getNpcs().filter(n -> monsters.contains(n.getNpcId()))
                .allMatch(Creature::isDead);
    }

    private NpcInstance getTumor() {
        return getNpcs().filter(npc -> npc.getNpcId() == SufferingHallDefence.DeadTumor)
                .filter(npc -> !npc.isDead())
                .findFirst().orElse(null);
    }

    @Override
    public void onPlayerEnter(Player player) {
        super.onPlayerEnter(player);
        player.sendPacket(new ExSendUIEvent(player, false, true, (int) (System.currentTimeMillis() - _savedTime) / 1000, 0, NpcString.NONE));
    }

    @Override
    public void onPlayerExit(Player player) {
        super.onPlayerExit(player);
        player.sendPacket(new ExSendUIEvent(player, true, true, 0, 0));
    }

    @Override
    protected void onCollapse() {
        if (coffinSpawnTask != null)
            coffinSpawnTask.cancel(false);
        if (monstersSpawnTask != null)
            monstersSpawnTask.cancel(false);
        super.onCollapse();
    }

    private class DeathListener implements OnDeathListener {
        @Override
        public void onDeath(Creature self, Creature killer) {
            if (self instanceof NpcInstance) {
                if (monsters.contains(self.getNpcId()) && !checkAliveMonsters()) {
                    if (monstersSpawnTask != null)
                        monstersSpawnTask.cancel(false);
                    monstersSpawnTask = ThreadPoolManager.INSTANCE.schedule(SufferingHallDefence.this::spawnMonsters, 40000L);
                }
                if (self.getNpcId() == AliveTumor) {
                    self.deleteMe();
                    addSpawnWithoutRespawn(DeadTumor, roomCenter);
                    tumorIndex = 300;
                    doCountCoffinNotifications = true;
                } else if (self.getNpcId() == Yehan) {
                    ThreadPoolManager.INSTANCE.schedule(() -> {
                        if (monstersSpawnTask != null)
                            monstersSpawnTask.cancel(false);
                        if (coffinSpawnTask != null)
                            coffinSpawnTask.cancel(false);
                        clearReflection(5, true);
                        spawnByGroup("soi_hos_defence_tepios");

                        setReenterTime(System.currentTimeMillis());
                        getPlayers().forEach(p ->
                                p.sendPacket(new ExSendUIEvent(p, true, true, 0, 0)));

                        timeSpent = (int) (System.currentTimeMillis() - _savedTime) / 1000;
                    }, 10000L);
                }
            }
        }
    }


}