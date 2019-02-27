package l2trunk.gameserver.network.clientpackets;

import l2trunk.commons.lang.ArrayUtils;
import l2trunk.gameserver.data.xml.holder.SkillAcquireHolder;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.SkillLearn;
import l2trunk.gameserver.model.base.AcquireType;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.gameserver.network.serverpackets.AcquireSkillInfo;
import l2trunk.gameserver.tables.SkillTable;

public final class RequestAquireSkillInfo extends L2GameClientPacket {
    private int id;
    private int level;
    private AcquireType type;

    @Override
    protected void readImpl() {
        id = readD();
        level = readD();
        type = AcquireType.VALUES.get(readD());
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null || player.isTrasformed() || SkillTable.INSTANCE.getInfo(id, level) == null || type == null)
            return;

        NpcInstance trainer = player.getLastNpc();
        if ((trainer == null || player.getDistance(trainer.getX(), trainer.getY()) > Creature.INTERACTION_DISTANCE) && !player.isGM())
            return;

        SkillLearn skillLearn = SkillAcquireHolder.getSkillLearn(player, id, level, type);
        if (skillLearn == null)
            return;

        sendPacket(new AcquireSkillInfo(type, skillLearn));
    }
}