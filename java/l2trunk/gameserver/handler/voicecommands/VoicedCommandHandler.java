package l2trunk.gameserver.handler.voicecommands;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.handler.voicecommands.impl.*;
import l2trunk.scripts.handler.voicecommands.DragonStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum VoicedCommandHandler {
    INSTANCE;
    private static Logger LOG = LoggerFactory.getLogger(VoicedCommandHandler.class);
    private final Map<String, IVoicedCommandHandler> datatable = new HashMap<>();

    VoicedCommandHandler() {
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
        registerVoicedCommandHandler(new Password());
        registerVoicedCommandHandler(new Relocate());
        registerVoicedCommandHandler(new Repair());
        registerVoicedCommandHandler(new ServerInfo());
        registerVoicedCommandHandler(new WhoAmI());
        registerVoicedCommandHandler(new Debug());
        registerVoicedCommandHandler(new res());
        registerVoicedCommandHandler(new FindParty());
        registerVoicedCommandHandler(new Ping());
        registerVoicedCommandHandler(new CommandSiege());
        registerVoicedCommandHandler(new LockPc());
        registerVoicedCommandHandler(new NpcSpawn());

        if (Config.ENABLE_ACHIEVEMENTS)
            registerVoicedCommandHandler(new AchievementsVoice());

        // Ady
        registerVoicedCommandHandler(new BuffStoreVoiced());
        registerVoicedCommandHandler(new VoiceGmEvent());
        registerVoicedCommandHandler(new ACP());
        registerVoicedCommandHandler(new ItemLogsCommand());
        registerVoicedCommandHandler(new DragonStatus());
    }

    public void registerVoicedCommandHandler(IVoicedCommandHandler handler) {
        handler.getVoicedCommandList().forEach(a -> datatable.put(a, handler));
    }

    public IVoicedCommandHandler getVoicedCommandHandler(String voicedCommand) {
        String[] command = voicedCommand.split(" ");
        return datatable.get(command[0]);
    }

    public void log() {
        LOG.info(String.format("loaded %d %s(s) count.", datatable.size(), getClass().getSimpleName()));
    }
}
