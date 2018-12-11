package l2trunk.gameserver.network.clientpackets;

import l2trunk.gameserver.data.xml.holder.CharTemplateHolder;
import l2trunk.gameserver.model.base.ClassId;
import l2trunk.gameserver.network.serverpackets.NewCharacterSuccess;

public final class NewCharacter extends L2GameClientPacket {
    @Override
    protected void readImpl() {
    }

    @Override
    protected void runImpl() {
        NewCharacterSuccess ct = new NewCharacterSuccess();

        ct.addChar(CharTemplateHolder.getTemplate(ClassId.fighter, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.mage, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.elvenFighter, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.elvenMage, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.darkFighter, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.darkMage, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.orcFighter, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.orcMage, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.dwarvenFighter, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.maleSoldier, false));
        ct.addChar(CharTemplateHolder.getTemplate(ClassId.femaleSoldier, false));

        sendPacket(ct);
    }
}