package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.commons.lang.NumberUtils;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.model.Zone.ZoneType;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SpawnTable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class NpcSpawn extends Functions implements IVoicedCommandHandler {
    private static final List<String> COMMANDS = List.of("npcspawn", "spawnnpc");

    private static final List<Integer> NPCS = List.of(
            37031, 37032, 37033, 37034, 37035, 37036, 37037, 37038, 37039, 37040, 32323, 30120, 37041);

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String target) {

        if (activeChar.getClan() == null || activeChar.getClanHall() == null || activeChar.getClan().getLeaderId() != activeChar.objectId()) {
            activeChar.sendMessage("Only clan leaders owning a clanhall can use that command.");
            return false;
        }

        Zone zone = activeChar.getZone(ZoneType.RESIDENCE);
        int clanhallId = NumberUtils.toInt(zone == null ? "" : zone.getName().substring(10), 0);
        if (clanhallId <= 0 || clanhallId > 100 || activeChar.getClanHall().getId() != clanhallId) // Residence ID check. Fortresses are beyond ID 100.
        {
            activeChar.sendMessage("You need to be in your clanhall to use that command.");
            return false;

        }

        if (target.isEmpty()) {// No variables, then display main html
            NpcHtmlMessage html = new NpcHtmlMessage(activeChar.objectId());
            html.setFile("custom/npcspawn.htm");// i think thats a what to set there?
            activeChar.sendPacket(html);
            return true;
        }

        // Fill the NPCs table. Spawned will lead to an active NpcInstance, unspawned will lead to null.
        Map<Integer, NpcInstance> npcs = new HashMap<>();
        NPCS.forEach(npcId -> npcs.put(npcId, null));

        zone.getInsideNpcs()
                .filter(npc -> NPCS.contains(npc.getNpcId()))
                .forEach(npc -> npcs.put(npc.getNpcId(), npc));


        String[] vars = target.split(" ");
        if (vars.length < 2)
            return false;

        boolean spawnNpc = vars[0].equalsIgnoreCase("spawn");
        boolean unspawnNpc = vars[0].equalsIgnoreCase("unspawn");
        int npcId = NumberUtils.toInt(vars[1], 0);

        if ((!spawnNpc && !unspawnNpc) || npcId == 0) {
            activeChar.sendMessage("Invalid action.");
            return false;
        }

        if (spawnNpc) {
            if (npcs.get(npcId) != null) {
                activeChar.sendMessage("The npc is already spawned.");
                return false;
            }

            spawnNpc(activeChar, npcId);
            activeChar.sendMessage("Npc has been spawned.");

            if (!Config.LOAD_CUSTOM_SPAWN)
                activeChar.sendMessage("Apparently the npc cannot be saved and will be deleted upon server restart.");
        } else {
            NpcInstance npc = npcs.get(npcId);
            if (npc == null) {
                activeChar.sendMessage("The npc is already unspawned.");
                return false;
            }

            unspawnNpc(npc);
            activeChar.sendMessage("Npc has been unspawned.");
        }
        return true;
    }

    private void spawnNpc(Player player, int npcId) {
        SimpleSpawner spawn = new SimpleSpawner(npcId);
        spawn.setLoc(player.getLoc())
                .setAmount(1)
                .setRespawnDelay(69) // Setting respawnDelay to 69 so it can be easily located in the database.
                .init();
        SpawnTable.INSTANCE.addNewSpawn(spawn);
    }

    private void unspawnNpc(NpcInstance npc) {
        SpawnTable.INSTANCE.deleteSpawn(npc.getLoc(), npc.getNpcId());
        npc.deleteMe();
    }

    @Override
    public List<String> getVoicedCommandList() {
        return COMMANDS;
    }
}