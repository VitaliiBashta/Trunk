package l2trunk.scripts.handler.voicecommands;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.quest.QuestState;
import l2trunk.gameserver.scripts.ScriptFile;
import l2trunk.scripts.quests._254_LegendaryTales;

import java.util.List;

public final class DragonStatus implements IVoicedCommandHandler, ScriptFile {
    private static final String COMMAND_LIST = "7rb";

    @Override
    public void onLoad() {
        VoicedCommandHandler.INSTANCE.registerVoicedCommandHandler(this);
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
    public List<String> getVoicedCommandList() {
        return List.of(COMMAND_LIST);
    }
}
