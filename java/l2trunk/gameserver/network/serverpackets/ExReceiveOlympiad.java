package l2trunk.gameserver.network.serverpackets;

import l2trunk.gameserver.Config;
import l2trunk.gameserver.model.base.TeamType;
import l2trunk.gameserver.model.entity.olympiad.Olympiad;
import l2trunk.gameserver.model.entity.olympiad.OlympiadGame;
import l2trunk.gameserver.model.entity.olympiad.OlympiadManager;
import l2trunk.gameserver.model.entity.olympiad.TeamMember;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ExReceiveOlympiad extends L2GameServerPacket {
    private final int _type;

    ExReceiveOlympiad(int type) {
        _type = type;
    }

    @Override
    protected void writeImpl() {
        writeEx(0xD4);
        writeD(_type);
    }

    public static class MatchList extends ExReceiveOlympiad {
        private List<ArenaInfo> _arenaList = Collections.emptyList();

        public MatchList() {
            super(0);
            OlympiadManager manager = Olympiad._manager;
            if (manager != null) {
                _arenaList = new ArrayList<>();
                for (int i = 0; i < Olympiad.STADIUMS.length; i++) {
                    OlympiadGame game = manager.getOlympiadInstance(i);
                    if (game != null && game.getState() > 0)
                        _arenaList.add(new ArenaInfo(i, game.getState(), game.getType().ordinal(), game.getTeamName1(), game.getTeamName2()));
                }
            }
        }

        public MatchList(List<ArenaInfo> arenaList) {
            super(0);
            _arenaList = arenaList;
        }

        @Override
        protected void writeImpl() {
            super.writeImpl();
            writeD(_arenaList.size());
            writeD(0x00); //unknown
            for (ArenaInfo arena : _arenaList) {
                writeD(arena._id);
                writeD(arena._matchType);
                writeD(arena._status);
                writeS(arena._name1);
                writeS(arena._name2);
            }
        }

        static class ArenaInfo {
            final int _status;
            final String _name1;
            final String _name2;
            private final int _id, _matchType;

            ArenaInfo(int id, int status, int match_type, String name1, String name2) {
                _id = id;
                _status = status;
                _matchType = match_type;
                _name1 = name1;
                _name2 = name2;
            }
        }
    }

    public static class MatchResult extends ExReceiveOlympiad {
        private final boolean _tie;
        private final String _name;
        private final List<PlayerInfo> _teamOne = new ArrayList<>(3);
        private final List<PlayerInfo> _teamTwo = new ArrayList<>(3);

        public MatchResult(boolean tie, String winnerName) {
            super(1);
            _tie = tie;
            _name = winnerName;
        }

        public void addPlayer(TeamType team, TeamMember member, int gameResultPoints) {
            int points = Config.OLYMPIAD_OLDSTYLE_STAT ? 0 : member.getStat().getInteger(Olympiad.POINTS, 0);

            addPlayer(team, member.getName(), member.getClanName(), member.getClassId(), points, gameResultPoints, (int) member.getDamage());
        }

        void addPlayer(TeamType team, String name, String clanName, int classId, int points, int resultPoints, int damage) {
            switch (team) {
                case RED:
                    _teamOne.add(new PlayerInfo(name, clanName, classId, points, resultPoints, damage));
                    break;
                case BLUE:
                    _teamTwo.add(new PlayerInfo(name, clanName, classId, points, resultPoints, damage));
                    break;
            }
        }

        @Override
        protected void writeImpl() {
            super.writeImpl();
            writeD(_tie);
            writeS(_name);
            writeD(1);
            writeD(_teamOne.size());
            for (PlayerInfo playerInfo : _teamOne) {
                writeS(playerInfo._name);
                writeS(playerInfo._clanName);
                writeD(0x00);
                writeD(playerInfo._classId);
                writeD(playerInfo._damage);
                writeD(playerInfo._currentPoints);
                writeD(playerInfo._gamePoints);
            }
            writeD(2);
            writeD(_teamTwo.size());
            for (PlayerInfo playerInfo : _teamTwo) {
                writeS(playerInfo._name);
                writeS(playerInfo._clanName);
                writeD(0x00);
                writeD(playerInfo._classId);
                writeD(playerInfo._damage);
                writeD(playerInfo._currentPoints);
                writeD(playerInfo._gamePoints);
            }
        }

        private static class PlayerInfo {
            private final String _name, _clanName;
            private final int _classId, _currentPoints, _gamePoints, _damage;

            PlayerInfo(String name, String clanName, int classId, int currentPoints, int gamePoints, int damage) {
                _name = name;
                _clanName = clanName;
                _classId = classId;
                _currentPoints = currentPoints;
                _gamePoints = gamePoints;
                _damage = damage;
            }
        }
    }
}
