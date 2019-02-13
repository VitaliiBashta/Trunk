package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.pledge.Clan;
import l2trunk.gameserver.model.pledge.UnitMember;

import java.util.ArrayList;
import java.util.List;


public class GMViewPledgeInfo extends L2GameServerPacket {
    private final String clan_name;
    private final String leader_name;
    private final String ally_name;
    private final int rank;
    private final int ally_id;
    private final int ally_crest_id;
    private final int hasCastle;
    private final int hasHideout;
    private final int hasFortress;
    private final boolean atWar;
    private final List<PledgeMemberInfo> infos = new ArrayList<>();
    private String char_name;
    private int clan_id;
    private int clan_crest_id;
    private int clan_level;
    private int rep;

    public GMViewPledgeInfo(Player activeChar) {
        Clan clan = activeChar.getClan();
        for (UnitMember member : clan) {
            if (member == null)
                continue;
            char_name = member.getName();
            clan_level = member.getLevel();
            clan_id = member.getClassId();
            clan_crest_id = member.isOnline() ? member.objectId() : 0;
            rep = member.getSponsor() != 0 ? 1 : 0;
            infos.add(new PledgeMemberInfo(char_name, clan_level, clan_id, clan_crest_id, member.getSex(), 1, rep));
        }

        char_name = activeChar.getName();
        clan_id = clan.clanId();
        clan_name = clan.getName();
        leader_name = clan.getLeaderName();
        clan_crest_id = clan.getCrestId();
        clan_level = clan.getLevel();
        hasCastle = clan.getCastle();
        hasHideout = clan.getHasHideout();
        hasFortress = clan.getHasFortress();
        rank = clan.getRank();
        rep = clan.getReputationScore();
        ally_id = clan.getAllyId();
        if (clan.getAlliance() != null) {
            ally_name = clan.getAlliance().getAllyName();
            ally_crest_id = clan.getAlliance().getAllyCrestId();
        } else {
            ally_name = "";
            ally_crest_id = 0;
        }
        atWar = clan.isAtWar();
    }

    @Override
    protected final void writeImpl() {
        writeC(0x96);

        writeS(char_name);
        writeD(clan_id);
        writeD(0x00);
        writeS(clan_name);
        writeS(leader_name);

        writeD(clan_crest_id);
        writeD(clan_level);
        writeD(hasCastle);
        writeD(hasHideout);
        writeD(hasFortress);
        writeD(rank);
        writeD(rep);
        writeD(0);
        writeD(0);
        writeD(ally_id);
        writeS(ally_name);
        writeD(ally_crest_id);
        writeD(atWar);
        writeD(0); // Territory castle ID

        writeD(infos.size());
        infos.forEach(i -> {
            writeS(i._name);
            writeD(i.level);
            writeD(i.class_id);
            writeD(i.sex);
            writeD(i.race);
            writeD(i.online);
            writeD(i.sponsor);
        });
        infos.clear();
    }

    static class PledgeMemberInfo {
        final String _name;
        final int level;
        final int class_id;
        final int online;
        final int sex;
        final int race;
        final int sponsor;

        PledgeMemberInfo(String __name, int _level, int _class_id, int _online, int _sex, int _race, int _sponsor) {
            _name = __name;
            level = _level;
            class_id = _class_id;
            online = _online;
            sex = _sex;
            race = _race;
            sponsor = _sponsor;
        }
    }
}