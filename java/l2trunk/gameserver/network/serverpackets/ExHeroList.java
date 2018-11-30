package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.templates.StatsSet;

import java.util.Map;


/**
 * Format: (ch) d [SdSdSdd]
 * d: size
 * [
 * S: hero name
 * d: hero class ID
 * S: hero clan name
 * d: hero clan crest id
 * S: hero ally name
 * d: hero Ally id
 * d: count
 * ]
 */
public final class ExHeroList extends L2GameServerPacket {
    private final Map<Integer, StatsSet> _heroList;

    public ExHeroList() {
        _heroList = Hero.INSTANCE.getHeroes();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x79);

        writeD(_heroList.size());
        for (StatsSet hero : _heroList.values()) {
            writeS(hero.getString(Olympiad.CHAR_NAME));
            writeD(hero.getInteger(Olympiad.CLASS_ID));
            writeS(hero.getString(Hero.CLAN_NAME, StringUtils.EMPTY));
            writeD(hero.getInteger(Hero.CLAN_CREST, 0));
            writeS(hero.getString(Hero.ALLY_NAME, StringUtils.EMPTY));
            writeD(hero.getInteger(Hero.ALLY_CREST, 0));
            writeD(hero.getInteger(Hero.COUNT));
        }
    }
}