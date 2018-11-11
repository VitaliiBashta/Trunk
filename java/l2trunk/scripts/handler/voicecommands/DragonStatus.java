package l2trunk.scripts.handler.voicecommands;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.scripts.quests._254_LegendaryTales;

public class DragonStatus implements IVoicedCommandHandler, ScriptFile {
    private final String[] _commandList = new String[]
            {"7rb"};

    @Override
    public void onLoad() {
        VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
    }

    @Override
    public void onReload() {
    }

    @Override
    public void onShutdown() {
    }

    @Override
    public boolean useVoicedCommand(String command, Player player, String args) {
        QuestState qs = player.getQuestState(_254_LegendaryTales.class);
        if (qs == null) {
            player.sendMessage("LegendaryTales: innactive");
            return false;
        }
        QuestState st = player.getQuestState(qs.getQuest().getName());
        int var = st.getInt("RaidsKilled");
        _254_LegendaryTales.checkKilledRaids(player, var);
        return true;
    }

    @Override
    public String[] getVoicedCommandList() {
        return _commandList;
    }
}
