package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.gameserver.ai.CharacterAI;
import l2trunk.gameserver.ai.DefaultAI;
import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.instancemanager.ServerVariables;
import l2trunk.gameserver.model.*;
import l2trunk.gameserver.model.instances.RaidBossInstance;
import l2trunk.gameserver.network.serverpackets.NpcHtmlMessage;

import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static l2trunk.commons.lang.NumberUtils.toInt;


/**
 * This class handles following admin commands: - help path = shows
 * admin/path file to char, should not be used by GM's directly
 */
public class AdminServer implements IAdminCommandHandler {
    // PUBLIC & STATIC so other classes from package can include it directly
    private static void showHelpPage(Player targetChar, String filename) {
        NpcHtmlMessage adminReply = new NpcHtmlMessage(5);
        adminReply.setFile("admin/" + filename);
        targetChar.sendPacket(adminReply);
    }

    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (!activeChar.getPlayerAccess().Menu)
            return false;

        switch (command) {
            case admin_server:
                try {
                    String val = fullString.substring(13);
                    showHelpPage(activeChar, val);
                } catch (StringIndexOutOfBoundsException e) {
                    // case of empty filename
                }
                break;
            case admin_check_actor:
                GameObject obj = activeChar.getTarget();
                if (obj == null) {
                    activeChar.sendMessage("target == null");
                    return false;
                }

                if (!obj.isCreature()) {
                    activeChar.sendMessage("target is not a character");
                    return false;
                }

                Creature target = (Creature) obj;
                CharacterAI ai = target.getAI();
                if (ai == null) {
                    activeChar.sendMessage("ai == null");
                    return false;
                }

                Creature actor = ai.getActor();
                if (actor == null) {
                    activeChar.sendMessage("actor == null");
                    return false;
                }

                activeChar.sendMessage("actor: " + actor);
                break;
            case admin_setvar:
                if (wordList.length != 3) {
                    activeChar.sendMessage("Incorrect argument count!!!");
                    return false;
                }
                ServerVariables.set(wordList[1], wordList[2]);
                activeChar.sendMessage("Value changed.");
                break;
            case admin_set_ai_interval:
                if (wordList.length != 2) {
                    activeChar.sendMessage("Incorrect argument count!!!");
                    return false;
                }
                int interval = toInt(wordList[1]);
                GameObjectsStorage.getAllNpcs()
                        .filter(Objects::nonNull)
                        .filter(npc -> !(npc instanceof RaidBossInstance))

                        .forEach(npc -> {
                            final CharacterAI char_ai = npc.getAI();
                            if (char_ai instanceof DefaultAI)
                                try {
                                    final java.lang.reflect.Field field = l2trunk.gameserver.ai.DefaultAI.class.getDeclaredField("AI_TASK_DELAY");
                                    field.setAccessible(true);
                                    field.set(char_ai, interval);

                                    if (char_ai.isActive()) {
                                        char_ai.stopAITask();
                                        WorldRegion region = npc.getCurrentRegion();
                                        if (region != null && region.isActive()) {
                                            char_ai.startAITask();
                                        }
                                    }
                                } catch (Exception ignored) {

                                }
                        });
                activeChar.sendMessage(" AI stopped, AI started");
                break;
            case admin_spawn2: // Игнорирует запрет на спавн рейдбоссов
                StringTokenizer st = new StringTokenizer(fullString, " ");
                    st.nextToken();
                    String id = st.nextToken();
                    int respawnTime = 30;
                    int mobCount = 1;
                    if (st.hasMoreTokens())
                        mobCount = toInt(st.nextToken());
                    if (st.hasMoreTokens())
                        respawnTime = toInt(st.nextToken());
                    spawnMonster(activeChar, id, respawnTime, mobCount);
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
            templateId = NpcHolder.getTemplateByName(monsterId).get(0).npcId;
        }


        SimpleSpawner spawn = new SimpleSpawner(templateId);
        spawn.setLoc(target.getLoc())
                .setAmount(mobCount)
                .setRespawnDelay(respawnTime)
                .setReflection(activeChar.getReflection())
                .init();
        if (respawnTime == 0)
            spawn.stopRespawn();
        activeChar.sendMessage("Created " + templateId + " on " + target.getObjectId() + ".");
    }

    private enum Commands {
        admin_server,
        admin_check_actor,
        admin_setvar,
        admin_set_ai_interval,
        admin_spawn2
    }
}