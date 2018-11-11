package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.entity.achievements.Achievements;
import l2trunk.gameserver.model.entity.events.fightclubmanager.FightClubEventManager;
import l2trunk.gameserver.model.quest.Quest;
import l2trunk.gameserver.scripts.Scripts;
import l2trunk.gameserver.utils.AccountEmail;

import java.util.Map;

public class RequestTutorialPassCmdToServer extends L2GameClientPacket {
    // format: cS

    private String _bypass = null;

    @Override
    protected void readImpl() {
        _bypass = readS();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null)
            return;

        player.isntAfk();

        if (player.isInFightClub()) {
            FightClubEventManager.getInstance().requestEventPlayerMenuBypass(player, _bypass);
        }

        // Alexander - Support for handling scripts events on tutorial windows
        else if (_bypass.startsWith("scripts_")) {
            String command = _bypass.substring(8).trim();
            String[] word = command.split("\\s+");
            String[] args = command.substring(word[0].length()).trim().split("\\s+");
            String[] path = word[0].split(":");
            if (path.length != 2)
                return;

            Map<String, Object> variables = null;

            if (word.length == 1)
                Scripts.getInstance().callScripts(player, path[0], path[1], variables);
            else
                Scripts.getInstance().callScripts(player, path[0], path[1], new Object[]{args}, variables);
        } else if (Config.ENABLE_ACHIEVEMENTS && _bypass.startsWith("_bbs_achievements")) {
            String[] cm = _bypass.split(" ");
            if (_bypass.startsWith("_bbs_achievements_cat")) {
                int page = 0;
                if (cm.length < 1)
                    page = 1;
                else
                    page = Integer.parseInt(cm[2]);

                Achievements.getInstance().generatePage(player, Integer.parseInt(cm[1]), page);
            } else
                Achievements.getInstance().onBypass(player, _bypass, cm);
        } else {
            Quest tutorial = QuestManager.getQuest(255);

            if (tutorial != null)
                player.processQuestEvent(tutorial.getName(), _bypass, null);
        }

        if (Config.ALLOW_MAIL_OPTION)
            AccountEmail.onBypass(player, _bypass);
    }
}