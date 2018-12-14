package l2trunk.scripts.events.arena;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.listener.zone.OnZoneEnterLeaveListener;
import l2trunk.gameserver.model.Creature;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.Zone;
import l2trunk.gameserver.scripts.Functions;
import l2trunk.gameserver.utils.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public abstract class ArenaTemplate extends Functions {
    // Эти переменные выставляются автоматически при вызове скрипта
    int _managerId;
    String _className;
    protected String _chatName;
    protected Long _creatorId;
    int _status = 0;
    protected int _battleType = 1;
    protected int _team1exp = 0;
    protected int _team2exp = 0;
    protected int _price = 10000;
    protected int _team1count = 1;
    protected int _team2count = 1;
    protected int _team1min = 1;
    protected int _team1max = 85;
    protected int _team2min = 1;
    protected int _team2max = 85;
    protected int _timeToStart = 10;
    protected boolean _timeOutTask;

    List<Location> _team1points;
    List<Location> _team2points;

    List<Long> _team1list;
    List<Long> _team2list;
    List<Long> _team1live;
    List<Long> _team2live;

    Map<Integer, Integer> _expToReturn;
    Map<Integer, Integer> _classToReturn;

    Zone _zone;
    ZoneListener _zoneListener;

    protected abstract void onLoad();

    protected abstract void onReload();

    public void template_stop() {
    }

    public void template_create1(Player player) {
    }

    public void template_create2(Player player) {
    }

    public void template_register(Player player) {
    }

    public void template_check1(Player player, String[] var) {
    }

    public void template_check2(Player player, String[] var) {
    }

    public void template_register_check(Player player, String[] var) {
    }

    public void template_announce() {
    }

    public void template_prepare() {
    }

    public void template_start() {
    }

    public void clearArena() {
		/*for(Creature cha : _zone.getObjects())
			if (cha.isPlayable())
				cha.teleToLocation(_zone.getSpawn());*/
    }

    public boolean checkTeams() {
		/*if(_team1live.isEmpty())
		{
			teamHasLost(1);
			return false;
		}
		else if (_team2live.isEmpty())
		{
			teamHasLost(2);
			return false;
		}*/
        return true;
    }

    public void paralyzeTeams() {
		/*Skill revengeSkill = SkillTable.INSTANCE().getInfo(Skill.SKILL_RAID_CURSE_ID, 1);
		for (Player player : getPlayers(_team1live))
		{
			player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
			revengeSkill.getEffects(player, player, false, false);
			if (player.getPet() != null)
				revengeSkill.getEffects(player, player.getPet(), false, false);
		}
		for (Player player : getPlayers(_team2live))
		{
			player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
			revengeSkill.getEffects(player, player, false, false);
			if (player.getPet() != null)
				revengeSkill.getEffects(player, player.getPet(), false, false);
		}*/
    }

    public void unParalyzeTeams() {
		/*for(Player player : getPlayers(_team1list))
		{
			player.getEffectList().stopEffect(Skill.SKILL_RAID_CURSE_ID);
			if (player.getPet() != null)
				player.getPet().getEffectList().stopEffect(Skill.SKILL_RAID_CURSE_ID);
		}
		for (Player player : getPlayers(_team2list))
		{
			player.getEffectList().stopEffect(Skill.SKILL_RAID_CURSE_ID);
			if (player.getPet() != null)
				player.getPet().getEffectList().stopEffect(Skill.SKILL_RAID_CURSE_ID);
		}*/
    }

    public void teleportTeamsToArena() {
		/*Integer n = 0;
		for (Player player : getPlayers(_team1live))
		{
			player.teleToLocation(_team1points.get(n));
			if (player.getPet() != null)
				player.getPet().teleToLocation(_team1points.get(n));
			player.setTeam(TeamType.BLUE);
			n++;
		}
		n = 0;
		for (Player player : getPlayers(_team2live))
		{
			player.teleToLocation(_team2points.get(n));
			if (player.getPet() != null)
				player.getPet().teleToLocation(_team2points.get(n));
			player.setTeam(TeamType.RED);
			n++;
		}*/
    }

    public boolean playerHasLost(Player player) {
		/*_team1live.remove(player.getStoredId());
		_team2live.remove(player.getStoredId());
		Skill revengeSkill = SkillTable.INSTANCE().getInfo(Skill.SKILL_RAID_CURSE_ID, 1);
		player.getEffectList().stopEffect(Skill.SKILL_MYSTIC_IMMUNITY);
		revengeSkill.getEffects(player, player, false, false);
		return !checkTeams();*/
        return false;
    }

    public void teamHasLost(Integer team_id) {
		/*if(team_id == 1)
		{
			say("Team 2 won");
			if (_battleType == 1)
				payAdenaToTeam(2);
			else if (_battleType == 2)
				payExpToTeam(2);
		}
		else
		{
			say("Team 1 won");
			if (_battleType == 1)
				payAdenaToTeam(1);
			else if (_battleType == 2)
				payExpToTeam(1);
		}
		unParalyzeTeams();
		clearTeams();
		_status = 0;
		_timeOutTask = false;*/
    }

    public void template_timeOut() {
		/*if(_timeOutTask && _status == 3)
		{
			say("Time has run out, a draw!");
			if (_battleType == 1)
				returnAdenaToTeams();
			else if (_battleType == 2)
				returnExpToTeams();
			unParalyzeTeams();
			clearTeams();
			_status = 0;
			_timeOutTask = false;
		}*/
    }

    public void payAdenaToTeam(Integer team_id) {
		/*if(team_id == 1)
			for (Player player : getPlayers(_team1list))
			{
				long reward = _price + _team2list.size() * _price / _team1list.size();
				player.addAdena(reward, "ArenaTemplate payToTeam");
			}
		else
			for (Player player : getPlayers(_team2list))
			{
				long reward = _price + _team1list.size() * _price / _team2list.size();
				player.addAdena(reward, "ArenaTemplate payToTeam");
			}*/
    }

    public void payExpToTeam(Integer team_id) {
		/*if(team_id == 1)
			for (Player player : getPlayers(_team1list))
			{
				returnExp(player);
				addExp(player, _team2exp / _team1list.size() / 2);
			}
		else
			for (Player player : getPlayers(_team2list))
			{
				returnExp(player);
				addExp(player, _team1exp / _team2list.size() / 2);
			}*/
    }

    public void returnAdenaToTeams() {
		/*for(Player player : getPlayers(_team1list))
		{
			player.addAdena(_price, "ArenaTemplate returnToTeams");
		}
		for (Player player : getPlayers(_team2list))
		{
			player.addAdena(_price, "ArenaTemplate returnToTeams");
		}*/
    }

    public void returnExpToTeams() {
		/*for(Player player : getPlayers(_team1list))
			returnExp(player);
		for (Player player : getPlayers(_team2list))
			returnExp(player);*/
    }

    public void clearTeams() {
		/*for(Player player : getPlayers(_team1list))
			player.setTeam(TeamType.NONE);
		for (Player player : getPlayers(_team2list))
			player.setTeam(TeamType.NONE);
		_team1list.clear();
		_team2list.clear();
		_team1live.clear();
		_team2live.clear();*/
    }

    public void removeExp(Player player, int team) {
		/*int lostExp = Math.round((Experience.LEVEL[player.getLevel() + 1] - Experience.LEVEL[player.getLevel()]) * 4 / 100);
		player.addExpAndSp(-1 * lostExp, 0);
		_expToReturn.put(player.getObjectId(), lostExp);
		_classToReturn.put(player.getObjectId(), player.getActiveClassId());

		if (team == 1)
			_team1exp += lostExp;
		else if (team == 2)
			_team2exp += lostExp;*/
    }

    public void returnExp(Player player) {
		/*int addExp = _expToReturn.get(player.getObjectId());
		int classId = _classToReturn.get(player.getObjectId());
		if (addExp > 0 && player.getActiveClassId() == classId)
			player.addExpAndSp(addExp, 0);*/
    }

    public void addExp(Player player, int exp) {
		/*int classId = _classToReturn.get(player.getObjectId());
		if (player.getActiveClassId() == classId)
			player.addExpAndSp(exp, 0);*/
    }

    void onDeath(Creature self, Creature killer) {
		/*if(_status >= 2 && self.isPlayer() && (_team1list.contains(self.getStoredId()) || _team2list.contains(self.getStoredId())))
		{
			Player player = self.getPlayer();
			Player kplayer = killer.getPlayer();
			if (kplayer != null)
			{
				say(kplayer.getName() + " killed " + player.getName());
				if (player.getTeam() == kplayer.getTeam() || !_team1list.contains(kplayer.getStoredId()) && !_team2list.contains(kplayer.getStoredId()))
				{
					say("Violation of the rules, a player " + kplayer.getName() + " fined " + _price);
					kplayer.reduceAdena(_price, true, "ArenaTemplate Death");
				}
				playerHasLost(player);
			}
			else
			{
				say(player.getName() + " killed");
				playerHasLost(player);
			}
		}*/
    }

    void onPlayerExit(Player player) {

    }

    void onTeleport(Player player) {

    }

    public class ZoneListener implements OnZoneEnterLeaveListener {
        @Override
        public void onZoneEnter(Zone zone, Creature cha) {
        }

        @Override
        public void onZoneLeave(Zone zone, Creature cha) {
        }
    }

    public class TeleportTask extends RunnableImpl {
        Location loc;
        Creature target;

        public TeleportTask(Creature target, Location loc) {
			/*this.target = target;
			this.loc = loc;
			target.setBlock();*/
        }

        @Override
        public void runImpl() {
        }
    }

    private void removePlayer(Player player) {
		/*if(player != null)
		{
			_team1list.remove(player.getStoredId());
			_team2list.remove(player.getStoredId());
			_team1live.remove(player.getStoredId());
			_team2live.remove(player.getStoredId());
			player.setTeam(TeamType.NONE);
		}*/
    }

    private List<Player> getPlayers(List<Long> list) {
        /*for(Long storeId : list)
		{
			Player player = GameObjectsStorage.getAsPlayer(storeId);
			if (player != null)
				result.add(player);
		}*/
        return new ArrayList<Player>();
    }

    public void say(String text) {
		/*Say2 cs = new Say2(0, ChatType.SHOUT, "Arena", text);
		for (Player player : GameObjectsStorage.getAllPlayersForIterate())
			if (!player.isBlockAll() && player.isInRange(_zone.getSpawn(), 4000))
				player.sendPacket(cs);*/
    }
}