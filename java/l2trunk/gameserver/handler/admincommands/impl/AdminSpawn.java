package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.AIs;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.RaidBossSpawnManager;
import l2trunk.gameserver.model.GameObject;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SimpleSpawner;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.tables.SpawnTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static l2trunk.commons.lang.NumberUtils.toInt;

public final class AdminSpawn implements IAdminCommandHandler {
    private static final Logger _log = LoggerFactory.getLogger(AdminSpawn.class);

    @Override
    public boolean useAdminCommand(String comm, String[] wordList, String fullString, final Player activeChar) {
        GameObject target = activeChar.getTarget();
        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;
        StringTokenizer st;
        NpcInstance npcInstance;
        Spawner spawn;
        NpcInstance npc;

        switch (comm) {
            case "admin_show_spawns":
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/spawns.htm"));
                break;
            case "admin_spawn_index":
                try {
                    String val = fullString.substring(18);
                    activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/spawns/" + val + ".htm"));
                } catch (StringIndexOutOfBoundsException ignored) {
                }
                break;
            case "admin_spawn1":
                st = new StringTokenizer(fullString, " ");
                try {
                    st.nextToken();
                    String id = st.nextToken();
                    int mobCount = 1;
                    if (st.hasMoreTokens())
                        mobCount = toInt(st.nextToken());
                    spawnMonster(activeChar, id, 0, mobCount);
                } catch (Exception e) {
                    // Case of wrong monster data
                }
                break;
            case "admin_spawn":
            case "admin_spawn_monster":
                st = new StringTokenizer(fullString, " ");
                try {
                    st.nextToken();
                    String id = st.nextToken();
                    int respawnTime = 30;
                    int mobCount = 1;
                    if (st.hasMoreTokens())
                        mobCount = toInt(st.nextToken());
                    if (st.hasMoreTokens())
                        respawnTime = toInt(st.nextToken());
                    spawnMonster(activeChar, id, respawnTime, mobCount);
                } catch (Exception e) {
                    // Case of wrong monster data
                }
                break;
            case "admin_setai":
                if (!(target instanceof NpcInstance)) {
                    activeChar.sendMessage("Please getBonuses npcInstance NPC or mob.");
                    return false;
                }

                st = new StringTokenizer(fullString, " ");
                st.nextToken();
                if (!st.hasMoreTokens()) {
                    activeChar.sendMessage("Please specify AI name.");
                    return false;
                }
                String aiName = st.nextToken();
                npcInstance = (NpcInstance) target;
                npcInstance.setAI(AIs.getNewAI("ai." + aiName, npcInstance));
                npcInstance.getAI().startAITask();

                break;
            case "admin_setaiparam":
                if (target instanceof NpcInstance) {
                    st = new StringTokenizer(fullString, " ");
                    st.nextToken();

                    if (!st.hasMoreTokens()) {
                        activeChar.sendMessage("Please specify AI parameter name.");
                        activeChar.sendMessage("USAGE: //setaiparam <param> <value>");
                        return false;
                    }

                    String paramName = st.nextToken();
                    if (!st.hasMoreTokens()) {
                        activeChar.sendMessage("Please specify AI parameter value.");
                        activeChar.sendMessage("USAGE: //setaiparam <param> <value>");
                        return false;
                    }
                    String paramValue = st.nextToken();
                    npcInstance = (NpcInstance) target;
                    npcInstance.setParameter(paramName, paramValue);
                    npcInstance.decayMe();
                    npcInstance.spawnMe();
                    activeChar.sendMessage("AI parameter " + paramName + " succesfully setted to " + paramValue);
                    break;
                } else {
                    activeChar.sendMessage("Please getBonuses npcInstance NPC or mob.");
                    return false;
                }

            case "admin_dumpparams":
                if (target instanceof NpcInstance) {
                    npcInstance = (NpcInstance) target;
                    StatsSet set = npcInstance.getParameters();
                    if (!set.isEmpty())
                        _log.info("Dump of Parameters:\r\n" + set.toString());
                    else
                        _log.info("Parameters is empty.");
                    break;
                } else {
                    activeChar.sendMessage("Please getBonuses npcInstance NPC or mob.");
                    return false;
                }
            case "admin_setheading":
                if (target instanceof NpcInstance) {
                    npc = (NpcInstance) target;
                    npc.setHeading(activeChar.getHeading());
                    npc.decayMe();
                    npc.spawnMe();
                    activeChar.sendMessage("New heading : " + activeChar.getHeading());

                    spawn = npc.getSpawn();
                    if (spawn == null) {
                        activeChar.sendMessage("Spawn for this npc == null!");
                        return false;
                    }
                    break;
                } else {
                    activeChar.sendMessage("Target is incorrect!");
                    return false;
                }

        }
        return true;
    }

    @Override
    public List<String> getAdminCommands() {
        return List.of(
                "admin_show_spawns",
                "admin_spawn",
                "admin_spawn_monster",
                "admin_spawn_index",
                "admin_spawn1",
                "admin_setheading",
                "admin_setai",
                "admin_setaiparam",
                "admin_dumpparams",
                "admin_generate_loc",
                "admin_dumpspawn");
    }

    private void spawnMonster(Player activeChar, String monsterId, int respawnTime, int mobCount) {
        GameObject target = activeChar.getTarget();
        if (target == null)
            target = activeChar;

        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher regexp = pattern.matcher(monsterId);
        int templateId;
        if (regexp.matches()) {
            // First parameter was an ID number
            templateId = toInt(monsterId);
        } else {
            // First parameter wasn't just numbers so go by name not ID
            monsterId = monsterId.replace('_', ' ');
            templateId = NpcHolder.getTemplateByName(monsterId).map(npc -> npc.npcId).findFirst().orElse(0);
        }

        SimpleSpawner spawn = new SimpleSpawner(templateId);
        spawn.setLoc(target.getLoc())
                .setAmount(mobCount)
                .setRespawnDelay(respawnTime)
                .setReflection(activeChar.getReflection());

        if (RaidBossSpawnManager.INSTANCE.isDefined(templateId))
            activeChar.sendMessage("Raid Boss " + templateId + " already spawned.");
        else {
            if (Config.SAVE_GM_SPAWN)
                SpawnTable.INSTANCE.addNewSpawn(spawn);
            spawn.init();
            if (respawnTime == 0)
                spawn.stopRespawn();
            activeChar.sendMessage("Created " + templateId + " on " + target.objectId() + ".");
        }
    }

}