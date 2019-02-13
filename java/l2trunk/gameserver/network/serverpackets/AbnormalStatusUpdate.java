package l2trunk.gameserver.network.serverpackets;

import java.util.ArrayList;
import java.util.List;

/**
 * sample
 * <p>
 * 0000: 85 02 00 10 04 00 00 01 00 4b 02 00 00 2c 04 00    .........K...,..
 * 0010: 00 01 00 58 02 00 00                               ...X...
 * <p>
 * <p>
 * format   h (dhd)
 *
 * @version $Revision: 1.3.2.1.2.6 $ $Date: 2005/04/05 19:41:08 $
 */
public final class AbnormalStatusUpdate extends L2GameServerPacket {
    public static final int INFINITIVE_EFFECT = -1;
    private final List<Effect> effects;

    public AbnormalStatusUpdate() {
        effects = new ArrayList<>();
    }

    public void addEffect(int skillId, int dat, int duration) {
        effects.add(new Effect(skillId, dat, duration));
    }

    @Override
    protected final void writeImpl() {
        writeC(0x85);

        writeH(effects.size());

        effects.forEach(effect ->  {
            writeD(effect.skillId);
            writeH(effect.dat);
            writeD(effect.duration);
        });
    }

    private class Effect {
        final int skillId;
        final int dat;
        final int duration;

        Effect(int skillId, int dat, int duration) {
            this.skillId = skillId;
            this.dat = dat;
            this.duration = duration;
        }
    }
}