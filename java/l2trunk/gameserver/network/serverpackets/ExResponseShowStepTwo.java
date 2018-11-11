package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.petition.PetitionMainGroup;
import l2trunk.gameserver.model.petition.PetitionSubGroup;
import l2trunk.gameserver.utils.Language;

import java.util.Collection;

/**
 * @author VISTALL
 */
public class ExResponseShowStepTwo extends L2GameServerPacket {
    private final Language _language;
    private final PetitionMainGroup _petitionMainGroup;

    public ExResponseShowStepTwo(Player player, PetitionMainGroup gr) {
        _language = player.getLanguage();
        _petitionMainGroup = gr;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xAF);
        Collection<PetitionSubGroup> subGroups = _petitionMainGroup.getSubGroups();
        writeD(subGroups.size());
        writeS(_petitionMainGroup.getDescription(_language));
        for (PetitionSubGroup g : subGroups) {
            writeC(g.getId());
            writeS(g.getName(_language));
        }
    }
}