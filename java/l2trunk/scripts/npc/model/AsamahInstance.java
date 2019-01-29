package l2trunk.scripts.npc.model;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.quests._111_ElrokianHuntersProof;

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

        if (command.equals("buyTrap")) {
            String htmltext = null;
            QuestState ElrokianHuntersProof = player.getQuestState(_111_ElrokianHuntersProof.class);

            if (player.getLevel() >= 75 && ElrokianHuntersProof != null && ElrokianHuntersProof.isCompleted() && Functions.getItemCount(player, 57) > 1000000) {
                if (Functions.getItemCount(player, ElrokianTrap) > 0)
                    htmltext = getNpcId() + "-alreadyhave.htm";
                else {
                    Functions.removeItem(player, 57, 1000000, "AsamahInstance");
                    Functions.addItem(player, ElrokianTrap, 1, "AsamahInstance");
                    htmltext = getNpcId() + "-given.htm";
                }

            } else
                htmltext = getNpcId() + "-cant.htm";

            showChatWindow(player, "default/" + htmltext);
        } else if (command.equals("buyStones")) {
            String htmltext;
            QuestState ElrokianHuntersProof = player.getQuestState(_111_ElrokianHuntersProof.class);

            if (player.getLevel() >= 75 && ElrokianHuntersProof != null && ElrokianHuntersProof.isCompleted() && Functions.getItemCount(player, 57) > 1000000) {
                Functions.removeItem(player, 57, 1000000, "AsamahInstance");
                Functions.addItem(player, TrapStone, 100, "AsamahInstance");
                htmltext = getNpcId() + "-given.htm";
            } else
                htmltext = getNpcId() + "-cant.htm";

            showChatWindow(player, "default/" + htmltext);
        } else
            super.onBypassFeedback(player, command);
    }
}
