package l2trunk.gameserver.handler.voicecommands.impl;

import l2trunk.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill;
import l2trunk.gameserver.network.serverpackets.Say2;
import l2trunk.gameserver.network.serverpackets.components.ChatType;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.tables.SkillTable;

import java.util.Collections;
import java.util.List;

public class SkillUse extends Functions implements IVoicedCommandHandler {
    private static final List<String> _commandList = Collections.singletonList("useskill");

    @Override
    public boolean useVoicedCommand(String command, Player activeChar, String args) {
        int skills = Integer.parseInt(args);

        Skill skill = SkillTable.INSTANCE().getInfo(skills, activeChar.getSkillLevel(skills));

        String sk = "/useskill " + skill.getName();
        Say2 cs = new Say2(activeChar.getObjectId(), ChatType.ALL, activeChar.getName(), sk);

        activeChar.setMacroSkill(skill);
        activeChar.sendPacket(cs);

        return true;
    }

    @Override
    public List<String> getVoicedCommandList() {
        return _commandList;
    }
}