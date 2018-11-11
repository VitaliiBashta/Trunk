package l2trunk.gameserver.handler.voicecommands;

import l2trunk.scripts.handler.voicecommands.DragonStatus;
import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.voicecommands.impl.*;
import l2trunk.gameserver.handler.voicecommands.impl.BotReport.ReportCommand;

import java.util.HashMap;
import java.util.Map;

public class VoicedCommandHandler extends AbstractHolder {
    private static final VoicedCommandHandler _instance = new VoicedCommandHandler();
    private final Map<String, IVoicedCommandHandler> _datatable = new HashMap<>();

    private VoicedCommandHandler() {
        registerVoicedCommandHandler(new Away());
        registerVoicedCommandHandler(new Atod());
        registerVoicedCommandHandler(new AntiGrief());
        registerVoicedCommandHandler(new CombineTalismans());
        registerVoicedCommandHandler(new Cfg());
        registerVoicedCommandHandler(new Help());
        registerVoicedCommandHandler(new Online());
        registerVoicedCommandHandler(new Hellbound());
        registerVoicedCommandHandler(new Teleport());
        registerVoicedCommandHandler(new PollCommand());
        registerVoicedCommandHandler(new CWHPrivileges());
        registerVoicedCommandHandler(new Offline());
        registerVoicedCommandHandler(new Password());
        registerVoicedCommandHandler(new Relocate());
        registerVoicedCommandHandler(new ReportCommand());
        registerVoicedCommandHandler(new Repair());
        registerVoicedCommandHandler(new ServerInfo());
        registerVoicedCommandHandler(new Wedding());
        registerVoicedCommandHandler(new WhoAmI());
        registerVoicedCommandHandler(new Debug());
        registerVoicedCommandHandler(new Security());
        registerVoicedCommandHandler(new ReportBot());
        registerVoicedCommandHandler(new res());
        registerVoicedCommandHandler(new FindParty());
        registerVoicedCommandHandler(new Ping());
        registerVoicedCommandHandler(new CommandSiege());
        registerVoicedCommandHandler(new LockPc());
        registerVoicedCommandHandler(new NpcSpawn());
        registerVoicedCommandHandler(new Donate());

        if (Config.ENABLE_ACHIEVEMENTS)
            registerVoicedCommandHandler(new AchievementsVoice());

        // Ady
        registerVoicedCommandHandler(new BuffStoreVoiced());
        registerVoicedCommandHandler(new VoiceGmEvent());
        registerVoicedCommandHandler(new ACP());
        registerVoicedCommandHandler(new ItemLogsCommand());
        registerVoicedCommandHandler(new DragonStatus());
    }

    public static VoicedCommandHandler getInstance() {
        return _instance;
    }

    public void registerVoicedCommandHandler(IVoicedCommandHandler handler) {
        String[] ids = handler.getVoicedCommandList();
        for (String element : ids) {
            _datatable.put(element, handler);
        }
    }

    public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand) {
        String command = voicedCommand;
        if (voicedCommand.contains(" ")) {
            command = voicedCommand.substring(0, voicedCommand.indexOf(" "));
        }

        return _datatable.get(command);
    }

    @Override
    public int size() {
        return _datatable.size();
    }

    @Override
    public void clear() {
        _datatable.clear();
    }
}
