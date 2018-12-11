package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.PetitionGroupHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.petition.PetitionMainGroup;
import l2trunk.gameserver.network.serverpackets.ExResponseShowStepTwo;

public final class RequestExShowStepTwo extends L2GameClientPacket {
    private int petitionGroupId;

    @Override
    protected void readImpl() {
        petitionGroupId = readC();
    }

    @Override
    protected void runImpl() {
        Player player = getClient().getActiveChar();
        if (player == null || !Config.EX_NEW_PETITION_SYSTEM)
            return;

        PetitionMainGroup group = PetitionGroupHolder.getPetitionGroup(petitionGroupId);
        if (group == null)
            return;

        player.setPetitionGroup(group);
        player.sendPacket(new ExResponseShowStepTwo(player, group));
    }
}