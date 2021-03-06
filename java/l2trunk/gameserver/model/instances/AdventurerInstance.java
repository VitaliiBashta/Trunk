package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.instancemanager.RaidBossSpawnManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Spawner;
import l2trunk.gameserver.network.serverpackets.ExShowQuestInfo;
import l2trunk.gameserver.network.serverpackets.RadarControl;
import l2trunk.gameserver.network.serverpackets.components.CustomMessage;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.gameserver.utils.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AdventurerInstance extends NpcInstance {
    private static final Logger _log = LoggerFactory.getLogger(AdventurerInstance.class);

    public AdventurerInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if (command.startsWith("npcfind_byid"))
            try {
                int bossId = Integer.parseInt(command.substring(12).trim());
                switch (RaidBossSpawnManager.INSTANCE.getRaidBossStatusId(bossId)) {
                    case ALIVE:
                    case DEAD:
                        Spawner spawn = RaidBossSpawnManager.INSTANCE.getSpawnTable().get(bossId);

                        Location loc = spawn.getCurrentSpawnRange().getRandomLoc(spawn.getReflection().getGeoIndex());

                        // Убираем и ставим флажок на карте и стрелку на компасе
                        player.sendPacket(new RadarControl(2, 2, loc), new RadarControl(0, 1, loc));
                        break;
                    case UNDEFINED:
                        player.sendMessage(new CustomMessage("l2trunk.gameserver.model.instances.L2AdventurerInstance.BossNotInGame").addNumber(bossId));
                        break;
                }
            } catch (NumberFormatException e) {
                _log.warn("AdventurerInstance: Invalid Bypass to Server command parameter.");
            }
        else if (command.startsWith("raidInfo")) {
            int bossLevel = Integer.parseInt(command.substring(9).trim());

            String filename = "adventurer_guildsman/raid_info/info.htm";
            if (bossLevel != 0)
                filename = "adventurer_guildsman/raid_info/occupation" + bossLevel + ".htm";

            showChatWindow(player, filename);
        } else if (command.equalsIgnoreCase("questlist"))
            player.sendPacket(ExShowQuestInfo.STATIC);
        else
            super.onBypassFeedback(player, command);
    }

    @Override
    public String getHtmlPath(int npcId, int val, Player player) {
        String pom;
        if (val == 0)
            pom = "" + npcId;
        else
            pom = npcId + "-" + val;

        return "adventurer_guildsman/" + pom + ".htm";
    }
}