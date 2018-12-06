package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.commons.collections.MultiValueSet;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.RaidBossSpawnManager;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;
import l2trunk.gameserver.tables.SpawnTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AdminSpawn implements IAdminCommandHandler {
    private static final Logger _log = LoggerFactory.getLogger(AdminSpawn.class);

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, final Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().CanEditNPC)
            return false;
        StringTokenizer st;
        NpcInstance target;
        Spawner spawn;
        NpcInstance npc;

        switch (command) {
            case admin_show_spawns:
                activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/spawns.htm"));
                break;
            case admin_spawn_index:
                try {
                    String val = fullString.substring(18);
                    activeChar.sendPacket(new NpcHtmlMessage(5).setFile("admin/spawns/" + val + ".htm"));
                } catch (StringIndexOutOfBoundsException e) {
                }
                break;
            case admin_spawn1:
                st = new StringTokenizer(fullString, " ");
                try {
                    st.nextToken();
                    String id = st.nextToken();
                    int mobCount = 1;
                    if (st.hasMoreTokens())
                        mobCount = Integer.parseInt(st.nextToken());
                    spawnMonster(activeChar, id, 0, mobCount);
                } catch (Exception e) {
                    // Case of wrong monster data
                }
                break;
            case admin_spawn:
            case admin_spawn_monster:
                st = new StringTokenizer(fullString, " ");
                try {
                    st.nextToken();
                    String id = st.nextToken();
                    int respawnTime = 30;
                    int mobCount = 1;
                    if (st.hasMoreTokens())
                        mobCount = Integer.parseInt(st.nextToken());
                    if (st.hasMoreTokens())
                        respawnTime = Integer.parseInt(st.nextToken());
                    spawnMonster(activeChar, id, respawnTime, mobCount);
                } catch (Exception e) {
                    // Case of wrong monster data
                }
                break;
            case admin_setai:
                if (activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) {
                    activeChar.sendMessage("Please getBonuses target NPC or mob.");
                    return false;
                }

                st = new StringTokenizer(fullString, " ");
                st.nextToken();
                if (!st.hasMoreTokens()) {
                    activeChar.sendMessage("Please specify AI name.");
                    return false;
                }
                String aiName = st.nextToken();
                target = (NpcInstance) activeChar.getTarget();

                Constructor<?> aiConstructor = null;
                try {
                    if (!aiName.equalsIgnoreCase("npc"))
                        aiConstructor = Class.forName("l2trunk.gameserver.ai." + aiName).getConstructors()[0];
                } catch (Exception e) {
                    try {
                        aiConstructor = Class.forName("l2trunk.scripts.ai." + aiName).getConstructors()[0];
                    } catch (Exception e1) {
                        activeChar.sendMessage("This type AI not found.");
                        return false;
                    }
                }

                if (aiConstructor != null) {
                    try {
                        target.setAI((CharacterAI) aiConstructor.newInstance(new Object[]{target}));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    target.getAI().startAITask();
                }
                break;
            case admin_setaiparam:
                if (activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) {
                    activeChar.sendMessage("Please getBonuses target NPC or mob.");
                    return false;
                }

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
                target = (NpcInstance) activeChar.getTarget();
                target.setParameter(paramName, paramValue);
                target.decayMe();
                target.spawnMe();
                activeChar.sendMessage("AI parameter " + paramName + " succesfully setted to " + paramValue);
                break;
            case admin_dumpparams:
                if (activeChar.getTarget() == null || !activeChar.getTarget().isNpc()) {
                    activeChar.sendMessage("Please getBonuses target NPC or mob.");
                    return false;
                }
                target = (NpcInstance) activeChar.getTarget();
                MultiValueSet<String> set = target.getParameters();
                if (!set.isEmpty())
                    _log.info("Dump of Parameters:\r\n" + set.toString());
                else
                    _log.info("Parameters is empty.");
                break;
            case admin_setheading:
                GameObject obj = activeChar.getTarget();
                if (!obj.isNpc()) {
                    activeChar.sendMessage("Target is incorrect!");
                    return false;
                }

                npc = (NpcInstance) obj;
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
            case admin_generate_loc:
                if (wordList.length < 2) {
                    activeChar.sendMessage("Incorrect argument count!");
                    return false;
                }

                int id = Integer.parseInt(wordList[1]);
                int id2 = 0;
                if (wordList.length > 2)
                    id2 = Integer.parseInt(wordList[2]);

                int min_x = Integer.MIN_VALUE;
                int min_y = Integer.MIN_VALUE;
                int min_z = Integer.MIN_VALUE;
                int max_x = Integer.MAX_VALUE;
                int max_y = Integer.MAX_VALUE;
                int max_z = Integer.MAX_VALUE;

                String name = "";

                for (NpcInstance _npc : World.getAroundNpc(activeChar))
                    if (_npc.getNpcId() == id || _npc.getNpcId() == id2) {
                        name = _npc.getName();
                        min_x = Math.min(min_x, _npc.getX());
                        min_y = Math.min(min_y, _npc.getY());
                        min_z = Math.min(min_z, _npc.getZ());
                        max_x = Math.max(max_x, _npc.getX());
                        max_y = Math.max(max_y, _npc.getY());
                        max_z = Math.max(max_z, _npc.getZ());
                    }

                min_x -= 500;
                min_y -= 500;
                max_x += 500;
                max_y += 500;

                _log.info("(0,'" + name + "'," + min_x + "," + min_y + "," + min_z + "," + max_z + ",0),");
                _log.info("(0,'" + name + "'," + min_x + "," + max_y + "," + min_z + "," + max_z + ",0),");
                _log.info("(0,'" + name + "'," + max_x + "," + max_y + "," + min_z + "," + max_z + ",0),");
                _log.info("(0,'" + name + "'," + max_x + "," + min_y + "," + min_z + "," + max_z + ",0),");

                _log.info("delete from spawnlist where npc_templateid in (" + id + ", " + id2 + ")" + //
                        " and locx <= " + min_x + //
                        " and locy <= " + min_y + //
                        " and locz <= " + min_z + //
                        " and locx >= " + max_x + //
                        " and locy >= " + max_y + //
                        " and locz >= " + max_z + //
                        ";");
                break;
            case admin_dumpspawn:
                st = new StringTokenizer(fullString, " ");
                try {
                    st.nextToken();
                    String id3 = st.nextToken();
                    int respawnTime = 30;
                    int mobCount = 1;
                    spawnMonster(activeChar, id3, respawnTime, mobCount);
                    try {
                        new File("dumps").mkdir();
                        File f = new File("dumps/spawndump.txt");
                        if (!f.exists())
                            f.createNewFile();
                        FileWriter writer = new FileWriter(f, true);
                        writer.write("<spawn count=\"1\" respawn=\"60\" respawn_random=\"0\" period_of_day=\"none\">\n\t"
                                + "<point x=\"" + activeChar.getLoc().x + "\" y=\"" + activeChar.getLoc().y
                                + "\" z=\"" + activeChar.getLoc().z + "\" h=\"" + activeChar.getLoc().h
                                + "\" />\n\t" + "<npc id=\"" + Integer.parseInt(id3) + "\" /><!--"
                                + NpcHolder.getTemplate(Integer.parseInt(id3)).getName() + "-->\n" + "</spawn>\n");
                        writer.close();
                    } catch (Exception e) {

                    }
                } catch (Exception e) {
                    // Case of wrong monster data
                }
                break;
        }
        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
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
            templateId = Integer.parseInt(monsterId);
        } else {
            // First parameter wasn't just numbers so go by name not ID
            monsterId = monsterId.replace('_', ' ');
            templateId = NpcHolder.getTemplateByName(monsterId).npcId;
        }

        SimpleSpawner spawn = new SimpleSpawner(templateId);
        spawn.setLoc(target.getLoc())
                .setAmount(mobCount)
                .setRespawnDelay(respawnTime)
                .setReflection(activeChar.getReflection());

        if (RaidBossSpawnManager.getInstance().isDefined(templateId))
            activeChar.sendMessage("Raid Boss " + templateId + " already spawned.");
        else {
            if (Config.SAVE_GM_SPAWN)
                SpawnTable.INSTANCE.addNewSpawn(spawn);
            spawn.init();
            if (respawnTime == 0)
                spawn.stopRespawn();
            activeChar.sendMessage("Created " + templateId + " on " + target.getObjectId() + ".");
        }
    }

    private enum Commands {
        admin_show_spawns,
        admin_spawn,
        admin_spawn_monster,
        admin_spawn_index,
        admin_spawn1,
        admin_setheading,
        admin_setai,
        admin_setaiparam,
        admin_dumpparams,
        admin_generate_loc,
        admin_dumpspawn
    }
}