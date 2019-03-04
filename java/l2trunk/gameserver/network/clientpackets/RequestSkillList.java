package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.network.serverpackets.SkillList;

public final class RequestSkillList extends L2GameClientPacket {

    @Override
    protected void readImpl() {
        // this is just a trigger packet. it has no content
    }

    @Override
    protected void runImpl() {
        Player cha = getClient().getActiveChar();

        if (cha != null)
            cha.sendPacket(new SkillList(cha));
    }

}
