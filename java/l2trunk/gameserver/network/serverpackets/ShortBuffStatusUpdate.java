package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Effect;

public final class ShortBuffStatusUpdate extends L2GameServerPacket {
    /**
     * This is client's row 2 buff packet.
     * <p>
     * Example (C4):
     * F4 CD 04 00 00 07 00 00 00 0F 00 00 00 - overlord's healing, panel2
     * <p>
     * structure cddd
     * <p>
     * NOTES:
     * 1). hex converting:
     * AcruireSkill 1229 is in hex 4CD, but in packet it is CD 04 00 00.
     * So i think that we must read the skill's hex id form behind ^^
     * 2). multipe skills on row 2:
     * i don't know what more skills can go at row2 @ offie.
     * please contact me to test it. Currently packet is working for one skill.
     * 3). Removing buff icon
     * must be sended empty packet
     * F4 00 00 00 00 00 00 00 00 00 00 00 00
     * to remove buff icon. Or it will be lasted forever.
     */

    private final int skillId;
    private final int skillLevel;
    private final int skillDuration;

    public ShortBuffStatusUpdate(Effect effect) {
        skillId = effect.skill.displayId;
        skillLevel = effect.skill.getDisplayLevel();
        skillDuration = effect.getTimeLeft();
    }

    /**
     * Zero packet to delete skill icon.
     */
    public ShortBuffStatusUpdate() {
        skillId = 0;
        skillLevel = 0;
        skillDuration = 0;
    }

    @Override
    protected final void writeImpl() {
        writeC(0xfa); //Packet type
        writeD(skillId); // skill id??? CD 04 00 00 = skill 1229, hex 4CD
        writeD(skillLevel); //AcruireSkill Level??? 07 00 00 00 = casted by heal 7 lvl.
        writeD(skillDuration); //DURATION???? 0F 00 00 00 = 15 sec = overlord's heal
    }
}