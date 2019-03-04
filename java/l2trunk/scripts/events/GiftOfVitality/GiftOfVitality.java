package l2trunk.scripts.events.GiftOfVitality;

import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.base.Race;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.network.serverpackets.MagicSkillUse;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class GiftOfVitality extends Functions implements ScriptFile {
    private static final String EVENT_NAME = "GiftOfVitality";
    private static final int REUSE_HOURS = 6; // reuse
    private static final int EVENT_MANAGER_ID = 109; // npc id
    private static List<SimpleSpawner> SPAWNER_LIST = new ArrayList<>();
    private static final Logger LOG = LoggerFactory.getLogger(GiftOfVitality.class);
    private static final int blessingOfEnergy = 23179;
    private final static List<Integer> MAGE_BUFF = List.of(
            5627,  // windwalk
            5628,  // shield
            5637,  // Magic Barrier 1
            5633,  // blessthesoul
            5634,  // acumen
            5635,  // concentration
            5636); // empower

    private final static List<Integer> WARR_BUFF = List.of(
            5627,  // windwalk
            5628,  // shield
            5637,  // Magic Barrier 1
            5629,  // btb
            5630,  // vampirerage
            5631,  // regeneration
            5632); // haste 2

    private final static List<Integer> _summonBuff = List.of(
            5627,  // windwalk
            5628,  // shield
            5637,  // Magic Barrier 1
            5629,  // btb
            5633,  // vampirerage
            5634,  // blessthesoul
            5631,  // acumen
            5635,  // concentration
            5632,  // empower
            5636); // haste 2

    private static boolean isActive() {
        return isActive(EVENT_NAME);
    }

    private void spawnEventManagers() {
        final List<Location> EVENT_MANAGERS = List.of(
                Location.of(-119494, 44882, 360, 24576),  // Kamael Village
                Location.of(-82687, 243157, -3734, 4096),  // Talking Island Village
                Location.of(45538, 48357, -3056, 18000),   // Elven Village
                Location.of(9929, 16324, -4568, 62999),    // Dark Elven Village
                Location.of(115096, -178370, -880, 0),     // Dwarven Village
                Location.of(-45372, -114104, -240, 16384), // Orc Village
                Location.of(-83156, 150994, -3120, 0),     // Gludin
                Location.of(-13727, 122117, -2984, 16384), // Gludio
                Location.of(16111, 142850, -2696, 16000),  // Dion
                Location.of(111176, 220968, -3544, 16384), // Heine
                Location.of(82792, 149448, -3494, 0),      // Giran
                Location.of(81083, 56118, -1552, 32768),   // Oren
                Location.of(117016, 77240, -2688, 49151),  // Hunters Village
                Location.of(147016, 25928, -2038, 16384),  // Aden
                Location.of(43966, -47709, -792, 49999),   // Rune
                Location.of(148088, -55416, -2728, 49151), // Goddart
                Location.of(87080, -141336, -1344, 0));     // Schutgard

        SPAWNER_LIST = SpawnNPCs(EVENT_MANAGER_ID, EVENT_MANAGERS);
    }

    private void unSpawnEventManagers() {
        deSpawnNPCs(SPAWNER_LIST);
    }

    public void startEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;

        if (setActive(EVENT_NAME, true)) {
            spawnEventManagers();
            System.out.println("Event: 'Gift Of Vitality' started.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.GiftOfVitality.AnnounceEventStarted");
        } else
            player.sendMessage("Event 'Gift Of Vitality' already started.");

        show("admin/events/events.htm", player);
    }

    public void stopEvent() {
        if (!player.getPlayerAccess().IsEventGm)
            return;
        if (setActive(EVENT_NAME, false)) {
            unSpawnEventManagers();
            System.out.println("Event: 'Gift Of Vitality' stopped.");
            Announcements.INSTANCE.announceByCustomMessage("scripts.events.GiftOfVitality.AnnounceEventStoped");
        } else
            player.sendMessage("Event: 'Gift Of Vitality' not started.");

        show("admin/events/events.htm", player);
    }

    @Override
    public void onLoad() {
        if (isActive()) {
            spawnEventManagers();
            LOG.info("Loaded Event: Gift Of Vitality [state: activated]");
        } else
            LOG.info("Loaded Event: Gift Of Vitality [state: deactivated]");
    }

    @Override
    public void onReload() {
        unSpawnEventManagers();
    }

    @Override
    public void onShutdown() {
        unSpawnEventManagers();
    }

    private void buffMe(BuffType type) {
        if (player == null || npc == null)
            return;

        String htmltext = null;
        long var = player.getVarLong("govEventTime");

        switch (type) {
            case VITALITY:
                if ( var > System.currentTimeMillis())
                    htmltext = "jack-notime.htm";
                else {
                    npc.broadcastPacket(new MagicSkillUse(npc, player, blessingOfEnergy));
                    player.altOnMagicUseTimer(player, blessingOfEnergy);
                    player.setVar("govEventTime", System.currentTimeMillis() + REUSE_HOURS * 60 * 60 * 1000L);
                    player.setVitality(Config.VITALITY_LEVELS.get(4));
                    htmltext = "jack-okvitality.htm";
                }
                break;
            case SUMMON:
                if (player.getLevel() < 76)
                    htmltext = "jack-nolevel.htm";
                else if (!(player.getPet() instanceof SummonInstance))
                    htmltext = "jack-nosummon.htm";
                else {
                    _summonBuff.forEach(skill -> {
                        npc.broadcastPacket(new MagicSkillUse(npc, player.getPet(), skill));
                        player.altOnMagicUseTimer(player.getPet(), skill);
                    });
                    htmltext = "jack-okbuff.htm";
                }
                break;
            case PLAYER:
                if (player.getLevel() < 76)
                    htmltext = "jack-nolevel.htm";
                else {
                    if (!player.isMageClass() || player.getTemplate().race == Race.orc)
                        WARR_BUFF.forEach(skill -> {
                            npc.broadcastPacket(new MagicSkillUse(npc, player, skill));
                            player.altOnMagicUseTimer(player, skill);
                        });
                    else
                        MAGE_BUFF.forEach(skill -> {
                            npc.broadcastPacket(new MagicSkillUse(npc, player, skill));
                            player.altOnMagicUseTimer(player, skill);
                        });
                    htmltext = "jack-okbuff.htm";
                }
                break;
        }
        show("scripts/events/GiftOfVitality/" + htmltext, player);
    }

    public void buffVitality() {
        buffMe(BuffType.VITALITY);
    }

    public void buffSummon() {
        buffMe(BuffType.SUMMON);
    }

    public void buffPlayer() {
        buffMe(BuffType.PLAYER);
    }

    public enum BuffType {
        PLAYER,
        SUMMON,
        VITALITY,
    }
}