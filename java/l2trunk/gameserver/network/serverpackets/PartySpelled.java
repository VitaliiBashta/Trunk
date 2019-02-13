package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Playable;
import l2trunk.gameserver.model.instances.PetInstance;
import l2trunk.gameserver.model.instances.SummonInstance;
import l2trunk.gameserver.utils.EffectsComparator;

import java.util.ArrayList;
import java.util.List;


public final class PartySpelled extends L2GameServerPacket {
    private final int _type;
    private final int _objId;
    private final List<Effect> _effects;

    public PartySpelled(Playable activeChar, boolean full) {
        _objId = activeChar.objectId();
        _type = activeChar instanceof PetInstance  ? 1 : activeChar instanceof SummonInstance ? 2 : 0;
        // 0 - L2Player // 1 - петы // 2 - саммоны
        _effects = new ArrayList<>();
        if (full) {
            List<l2trunk.gameserver.model.Effect> effects = activeChar.getEffectList().getAllFirstEffects();
            effects.sort(EffectsComparator.getInstance());
            for (l2trunk.gameserver.model.Effect effect : effects)
                if (effect != null && effect.isInUse())
                    effect.addPartySpelledIcon(this);
        }
    }

    @Override
    protected final void writeImpl() {
        writeC(0xf4);
        writeD(_type);
        writeD(_objId);
        writeD(_effects.size());
        for (Effect temp : _effects) {
            writeD(temp._skillId);
            writeH(temp._level);
            writeD(temp._duration);
        }
    }

    public void addPartySpelledEffect(int skillId, int level, int duration) {
        _effects.add(new Effect(skillId, level, duration));
    }

    static class Effect {
        final int _skillId;
        final int _level;
        final int _duration;

        Effect(int skillId, int level, int duration) {
            _skillId = skillId;
            _level = level;
            _duration = duration;
        }
    }
}