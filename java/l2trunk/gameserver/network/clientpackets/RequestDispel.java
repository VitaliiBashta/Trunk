package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Effect;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Skill.SkillType;
import l2trunk.gameserver.skills.EffectType;

public final class RequestDispel extends L2GameClientPacket {
    private int objectId, id, level;

    @Override
    protected void readImpl() {
        objectId = readD();
        id = readD();
        level = readD();
    }

    @Override
    protected void runImpl() {
        Player activeChar = getClient().getActiveChar();
        if ((activeChar == null) || ((activeChar.objectId() != objectId) && (activeChar.getPet() == null))) {
            return;
        }

        Creature target = activeChar;
        if (activeChar.objectId() != objectId) {
            target = activeChar.getPet();
        }

        target.getEffectList().getAllEffects().stream()
                .filter(e -> e.displayId == id)
                .filter(e -> e.displayLevel == level)
                .filter(e -> !e.isOffensive())
                .filter(e -> (!e.skill.isMusic() || Config.ALT_DISPEL_MUSIC))
                .filter(e -> e.skill.isSelfDispellable)
                .filter(e -> e.skill.skillType != SkillType.TRANSFORMATION)
                .filter(e -> e.getTemplate().getEffectType() != EffectType.Hourglass)
                .findFirst().ifPresent(Effect::exit);
    }
}