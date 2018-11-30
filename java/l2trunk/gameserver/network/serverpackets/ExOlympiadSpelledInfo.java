package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;

import java.util.ArrayList;
import java.util.List;


public class ExOlympiadSpelledInfo extends L2GameServerPacket {
    private final List<Effect> _effects;
    private int char_obj_id = 0;

    public ExOlympiadSpelledInfo() {
        _effects = new ArrayList<>();
    }

    public void addEffect(int skillId, int level, int duration) {
        _effects.add(new Effect(skillId, level, duration));
    }

    public void addSpellRecivedPlayer(Player cha) {
        if (cha != null)
            char_obj_id = cha.getObjectId();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x7b);

        writeD(char_obj_id);
        writeD(_effects.size());
        for (Effect temp : _effects) {
            writeD(temp.skillId);
            writeH(temp.level);
            writeD(temp.duration);
        }
    }

    class Effect {
        final int skillId;
        final int level;
        final int duration;

        Effect(int skillId, int level, int duration) {
            this.skillId = skillId;
            this.level = level;
            this.duration = duration;
        }
    }
}