package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.quests._111_ElrokianHuntersProof;

import static l2trunk.gameserver.utils.ItemFunctions.addItem;
import static l2trunk.gameserver.utils.ItemFunctions.removeItem;

public final class AsamahInstance extends NpcInstance {
    private static final int ElrokianTrap = 8763;
    private static final int TrapStone = 8764;

    public AsamahInstance(int objectId, NpcTemplate template) {
        super(objectId, template);
    }

    @Override
    public void onBypassFeedback(Player player, String command) {
        if (!canBypassCheck(player, this))
            return;

        if ("buyTrap".equals(command)) {
            String htmltext;

            if (player.getLevel() >= 75 && player.isQuestCompleted(_111_ElrokianHuntersProof.class) && player.getAdena() > 1_000_000) {
                if (player.haveItem( ElrokianTrap))
                    htmltext = getNpcId() + "-alreadyhave.htm";
                else {
                    player.reduceAdena(1_000_000, "AsamahInstance");
                    addItem(player, ElrokianTrap, 1);
                    htmltext = getNpcId() + "-given.htm";
                }

            } else
                htmltext = getNpcId() + "-cant.htm";

            showChatWindow(player, "default/" + htmltext);
        } else if ("buyStones".equals(command)) {
            String htmltext;
            QuestState ElrokianHuntersProof = player.getQuestState(_111_ElrokianHuntersProof.class);

            if (player.getLevel() >= 75 && ElrokianHuntersProof != null && ElrokianHuntersProof.isCompleted() && player.getAdena()> 1_000_000) {
                player.reduceAdena( 1_000_000, "AsamahInstance");
                addItem(player, TrapStone, 100);
                htmltext = getNpcId() + "-given.htm";
            } else
                htmltext = getNpcId() + "-cant.htm";

            showChatWindow(player, "default/" + htmltext);
        } else
            super.onBypassFeedback(player, command);
    }
}
