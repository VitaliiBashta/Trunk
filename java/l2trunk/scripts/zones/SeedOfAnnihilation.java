package l2trunk.scripts.zones;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.ai.CtrlIntention;
import l2trunk.gameserver.cache.Msg;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import l2trunk.gameserver.utils.ReflectionUtils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public final class SeedOfAnnihilation extends Functions implements ScriptFile {
    private static final int ANNIHILATION_FURNACE = 18928;
    private static final int[][] ZONE_BUFFS_LIST = {{1, 2, 3}, {1, 3, 2}, {2, 1, 3}, {2, 3, 1}, {3, 2, 1}, {3, 1, 2}};
    private static final Map<String, Location> TELEPORT_ZONES = new HashMap<>();
    private static ZoneListener _zoneListener;

    static {
        TELEPORT_ZONES.put("[14_23_telzone_to_cocracon]", new Location(-213175, 182648, -10992)); // In Kokracon location teleport zone.
        TELEPORT_ZONES.put("[14_23_telzone_to_raptilicon]", new Location(-180211, 182984, -15152)); // In Reptilikon location teleport zone.
        TELEPORT_ZONES.put("[13_23_telzone_from_cocracon]", new Location(-181217, 186711, -10528)); // Out Kokracon location teleport zone.
        TELEPORT_ZONES.put("[14_23_telzone_from_raptilicon]", new Location(-179275, 186802, -10720)); // Out Reptilikon location teleport zone.
    }

    // 0: Bistakon, 1: Reptilikon, 2: Cokrakon
    private final SeedRegion[] _regionsData = new SeedRegion[3];
    private Long _seedsNextStatusChange;

    private void loadSeedRegionData() {
        _zoneListener = new ZoneListener();
        TELEPORT_ZONES.keySet().forEach(s -> ReflectionUtils.getZone(s).addListener(_zoneListener));

        _regionsData[0] = new SeedRegion(new String[]{"[14_23_beastacon_for_melee_for_pc]", "[14_23_beastacon_for_archer_for_pc]", "[14_23_beastacon_for_mage_for_pc]"}, new String[]{"[14_23_beastacon_for_melee]", "[14_23_beastacon_for_archer]", "[14_23_beastacon_for_mage]"}, new int[][]{
                {-180450, 185507, -10574, 11632},
                {-180005, 185489, -10577, 11632}}); // Bistakon data
        _regionsData[1] = new SeedRegion(new String[]{"[14_23_raptilicon_for_melee_for_pc]", "[14_23_raptilicon_for_archer_for_pc]", "[14_23_raptilicon_for_mage_for_pc]"}, new String[]{"[14_23_raptilicon_for_melee]", "[14_23_raptilicon_for_archer]", "[14_23_raptilicon_for_mage]"}, new int[][]{
                {-179600, 186998, -10737, 11632},
                {-179295, 186444, -10733, 11632}}); // Reptilikon data
        _regionsData[2] = new SeedRegion(new String[]{"[13_23_cocracon_for_melee_for_pc]", "[13_23_cocracon_for_archer_for_pc]", "[13_23_cocracon_for_mage_for_pc]"}, new String[]{"[13_23_cocracon_for_melee]", "[13_23_cocracon_for_archer]", "[13_23_cocracon_for_mage]"}, new int[][]{
                {-180971, 186361, -10557, 11632},
                {-180758, 186739, -10556, 11632}}); // Cokrakon data

        int buffsNow;
        long nextStatusChange = ServerVariables.getLong("SeedNextStatusChange", 0);
        if (nextStatusChange < System.currentTimeMillis()) {
            buffsNow = Rnd.get(ZONE_BUFFS_LIST.length);
            _seedsNextStatusChange = getNextSeedsStatusChangeTime();
            ServerVariables.set("SeedBuffsList", buffsNow);
            ServerVariables.set("SeedNextStatusChange", _seedsNextStatusChange);
        } else {
            _seedsNextStatusChange = nextStatusChange;
            buffsNow = ServerVariables.getInt("SeedBuffsList", 0);
        }
        for (int i = 0; i < _regionsData.length; i++)
            _regionsData[i].activeBuff = ZONE_BUFFS_LIST[buffsNow][i];
    }

    private Long getNextSeedsStatusChangeTime() {
        Calendar reenter = Calendar.getInstance();
        reenter.set(Calendar.SECOND, 0);
        reenter.set(Calendar.MINUTE, 0);
        reenter.set(Calendar.HOUR_OF_DAY, 13);
        reenter.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        if (reenter.getTimeInMillis() <= System.currentTimeMillis())
            reenter.add(Calendar.DAY_OF_MONTH, 7);
        return reenter.getTimeInMillis();
    }

    @Override
    public void onLoad() {
        loadSeedRegionData();
        startEffectZonesControl();
    }

    private void startEffectZonesControl() {
        for (SeedRegion sr : _regionsData) {
            NpcTemplate template = NpcHolder.getTemplate(ANNIHILATION_FURNACE);
            for (int i = 0; i < sr.af_spawns.length; i++) {
                NpcInstance npc = template.getNewInstance();
                npc.setFullHpMp();
                npc.spawnMe(new Location(sr.af_spawns[i][0], sr.af_spawns[i][1], sr.af_spawns[i][2], sr.af_spawns[i][3]));
                npc.setNpcState(sr.activeBuff);
                sr.af_npcs[i] = npc;
            }
            chanceZoneActive(sr.buff_zone_pc[sr.activeBuff - 1], true);
            chanceZoneActive(sr.buff_zone_npc[sr.activeBuff - 1], true);
        }
        ThreadPoolManager.INSTANCE.schedule(new ChangeSeedsStatus(), _seedsNextStatusChange - System.currentTimeMillis());
    }

    private void chanceZoneActive(String zoneName, boolean val) {
        Zone zone = ReflectionUtils.getZone(zoneName);
        zone.setActive(val);
    }

    public void transform() {
        if (player == null || npc == null)
            return;

        if (!NpcInstance.canBypassCheck(player, npc))
            return;

        if (player.getTransformation() != 0 || player.isMounted()) {
            player.sendPacket(Msg.YOU_ALREADY_POLYMORPHED_AND_CANNOT_POLYMORPH_AGAIN);
            return;
        }
        if (player.getEffectList().getEffectsBySkillId(6408) != null) {
            show("default/32739-2.htm", player);
        } else {
            npc.setTarget(player);
            SkillTable.INSTANCE.getInfo(6408).getEffects(player);
            SkillTable.INSTANCE.getInfo(6649).getEffects(player);
            show("default/32739-1.htm", player);
        }
    }

    private class SeedRegion {
        final String[] buff_zone_pc;
        final String[] buff_zone_npc;
        final int[][] af_spawns;
        final NpcInstance[] af_npcs = new NpcInstance[2];
        int activeBuff = 0;

        SeedRegion(String[] bz_pc, String[] bz_npc, int[][] as) {
            buff_zone_pc = bz_pc;
            buff_zone_npc = bz_npc;
            af_spawns = as;
        }
    }

    private class ChangeSeedsStatus extends RunnableImpl {
        public void runImpl() {
            int buffsNow = Rnd.get(ZONE_BUFFS_LIST.length);
            _seedsNextStatusChange = getNextSeedsStatusChangeTime();
            ServerVariables.set("SeedBuffsList", buffsNow);
            ServerVariables.set("SeedNextStatusChange", _seedsNextStatusChange);
            for (int i = 0; i < _regionsData.length; i++) {
                int oldBuff = _regionsData[i].activeBuff;
                _regionsData[i].activeBuff = ZONE_BUFFS_LIST[buffsNow][i];

                for (NpcInstance af : _regionsData[i].af_npcs)
                    af.setNpcState(_regionsData[i].activeBuff);

                chanceZoneActive(_regionsData[i].buff_zone_pc[oldBuff - 1], false);
                chanceZoneActive(_regionsData[i].buff_zone_npc[oldBuff - 1], false);
                chanceZoneActive(_regionsData[i].buff_zone_pc[_regionsData[i].activeBuff - 1], true);
                chanceZoneActive(_regionsData[i].buff_zone_npc[_regionsData[i].activeBuff - 1], true);
            }
            ThreadPoolManager.INSTANCE.schedule(new ChangeSeedsStatus(), _seedsNextStatusChange - System.currentTimeMillis());
        }
    }

    private class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Player player) {
            if (TELEPORT_ZONES.containsKey(zone.getName())) {
                //Заглушка для 454 квеста.
                player.getAroundNpc(500, 300)
                        .filter(npc -> npc.getNpcId() == 32738)
                        .filter(npc -> npc.getFollowTarget() != null)
                        .filter(npc -> npc.getFollowTarget().objectId() == player.objectId())
                        .forEach(npc -> {
                            npc.teleToLocation(TELEPORT_ZONES.get(zone.getName()));
                            npc.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, player, Config.FOLLOW_RANGE);
                        });
                player.teleToLocation(TELEPORT_ZONES.get(zone.getName()));
            }
        }

    }
}