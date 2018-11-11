package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.data.xml.holder.PetitionGroupHolder;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.petition.PetitionMainGroup;
import l2trunk.gameserver.utils.Language;

import java.util.Collection;

/**
 * @author VISTALL
 */
public class ExResponseShowStepOne extends L2GameServerPacket {
    private final Language _language;

    public ExResponseShowStepOne(Player player) {
        _language = player.getLanguage();
    }

    @Override
    protected void writeImpl() {
        writeEx(0xAE);
        Collection<PetitionMainGroup> petitionGroups = PetitionGroupHolder.getInstance().getPetitionGroups();
        writeD(petitionGroups.size());
        for (PetitionMainGroup group : petitionGroups) {
            writeC(group.getId());
            writeS(group.getName(_language));
        }
    }
}