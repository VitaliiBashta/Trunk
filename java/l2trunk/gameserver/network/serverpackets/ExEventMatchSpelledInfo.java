package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;


public class ExEventMatchSpelledInfo extends L2GameServerPacket {
    private final List<Effect> _effects;
    // chdd(dhd)
    private int char_obj_id = 0;

    public ExEventMatchSpelledInfo() {
        _effects = new ArrayList<>();
    }

    public void addEffect(int skillId, int dat, int duration) {
        _effects.add(new Effect(skillId, dat, duration));
    }

    public void addSpellRecivedPlayer(Player cha) {
        if (cha != null)
            char_obj_id = cha.getObjectId();
    }

    @Override
    protected void writeImpl() {
        writeEx(0x04);

        writeD(char_obj_id);
        writeD(_effects.size());
        for (Effect temp : _effects) {
            writeD(temp.skillId);
            writeH(temp.dat);
            writeD(temp.duration);
        }
    }

    class Effect {
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