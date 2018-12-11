package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.templates.npc.NpcTemplate;

public final class BlackJudeInstance extends NpcInstance {
    public BlackJudeInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        switch (command) {
            case "tryRemovePenalty":
                if (player.getDeathPenalty().getLevel() > 0)
                    showChatWindow(player, 2, "%price%", getPrice(player));
                else
                    showChatWindow(player, 1);
                break;
            case "removePenalty":
                if (player.getDeathPenalty().getLevel() > 0)
                    if (player.getAdena() >= getPrice(player)) {
                        player.reduceAdena(getPrice(player), true, "BlackJudeInstance");
                        doCast(SkillTable.INSTANCE.getInfo(5077), player, false);
                    } else
                        player.sendPacket(SystemMsg.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
                else
                    showChatWindow(player, 1);
                break;
            default:
                super.onBypassFeedback(player, command);
                break;
        }
    }

    private int getPrice(Player player) {
        int playerLvl = player.getLevel();
        if (playerLvl <= 19)
            return 3600; // Non-grade (confirmed)
        else if (playerLvl <= 39)
            return 16400; // D-grade
        else if (playerLvl <= 51)
            return 36200; // C-grade
        else if (playerLvl <= 60)
            return 50400; // B-grade (confirmed)
        else if (playerLvl <= 75)
            return 78200; // A-grade
        else
            return 102800; // S-grade
    }
}
