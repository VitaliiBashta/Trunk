package l2trunk.gameserver.network.serverpackets;

import l2trunk.commons.collections.StatsSet;
import l2trunk.commons.lang.StringUtils;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;

import java.util.Map;

public final class ExHeroList extends L2GameServerPacket {
    private final Map<Integer, StatsSet> heroList;

    public ExHeroList() {
        heroList = Hero.INSTANCE.getHeroes();
    }

    @Override
    protected final void writeImpl() {
        writeEx(0x79);

        writeD(heroList.size());
        heroList.values().forEach(hero -> {
            writeS(hero.getString(Olympiad.CHAR_NAME));
            writeD(hero.getInteger(Olympiad.CLASS_ID));
            writeS(hero.getString(Hero.CLAN_NAME, ""));
            writeD(hero.getInteger(Hero.CLAN_CREST));
            writeS(hero.getString(Hero.ALLY_NAME, ""));
            writeD(hero.getInteger(Hero.ALLY_CREST));
            writeD(hero.getInteger(Hero.COUNT));
        });
    }
}