package l2trunk.gameserver.handler.admincommands.impl;

import l2trunk.commons.collections.StatsSet;
import l2trunk.gameserver.Announcements;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.ThreadPoolManager;
import l2trunk.gameserver.handler.admincommands.IAdminCommandHandler;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.World;
import l2trunk.gameserver.model.entity.Hero;
import l2trunk.gameserver.model.entity.olympiad.*;
import l2trunk.gameserver.network.serverpackets.SystemMessage2;
import l2trunk.gameserver.network.serverpackets.components.SystemMsg;

import java.util.ArrayList;
import java.util.List;


public class AdminOlympiad implements IAdminCommandHandler {
    @Override
    public boolean useAdminCommand(Enum comm, String[] wordList, String fullString, Player activeChar) {
        Commands command = (Commands) comm;

        if (activeChar.getPlayerAccess().CanGmEdit)

            switch (command) {
                case admin_oly_save: {
                    if (!Config.ENABLE_OLYMPIAD)
                        return false;

                    try {
                        OlympiadDatabase.save();
                    } catch (Exception e) {

                    }
                    activeChar.sendMessage("olympaid data saved.");
                    break;
                }
                case admin_add_oly_points: {
                    if (wordList.length < 3) {
                        activeChar.sendMessage("Command syntax: //add_oly_points <char_name> <point_to_add>");
                        activeChar.sendMessage("This command can be applied only for online players.");
                        return false;
                    }

                    Player player = World.getPlayer(wordList[1]);
                    if (player == null) {
                        activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
                        return false;
                    }

                    int pointToAdd;

                    try {
                        pointToAdd = Integer.parseInt(wordList[2]);
                    } catch (NumberFormatException e) {
                        activeChar.sendMessage("Please specify integer value for olympiad points.");
                        return false;
                    }

                    int curPoints = Olympiad.getNoblePoints(player.objectId());
                    Olympiad.manualSetNoblePoints(player.objectId(), curPoints + pointToAdd);
                    int newPoints = Olympiad.getNoblePoints(player.objectId());

                    activeChar.sendMessage("Added " + pointToAdd + " points to character " + player.getName());
                    activeChar.sendMessage("Old points: " + curPoints + ", new points: " + newPoints);
                    break;
                }
                case admin_oly_start: {
                    Olympiad._manager = new OlympiadManager();
                    Olympiad._inCompPeriod = true;

                    new Thread(Olympiad._manager).start();

                    Announcements.INSTANCE.announceToAll(new SystemMessage2(SystemMsg.THE_OLYMPIAD_GAME_HAS_STARTED));
                    break;
                }
                case admin_oly_stop: {
                    Olympiad._inCompPeriod = false;
                    Announcements.INSTANCE.announceToAll(new SystemMessage2(SystemMsg.THE_OLYMPIAD_GAME_HAS_ENDED));
                    try {
                        OlympiadDatabase.save();
                    } catch (Exception e) {

                    }

                    break;
                }
                case admin_add_hero: {
                    if (wordList.length < 2) {
                        activeChar.sendMessage("Command syntax: //add_hero <char_name>");
                        activeChar.sendMessage("This command can be applied only for online players.");
                        return false;
                    }

                    Player player = World.getPlayer(wordList[1]);
                    if (player == null) {
                        activeChar.sendMessage("Character " + wordList[1] + " not found in game.");
                        return false;
                    }

                    StatsSet hero = new StatsSet();
                    hero.set(Olympiad.CLASS_ID, player.getBaseClassId());
                    hero.set(Olympiad.CHAR_ID, player.objectId());
                    hero.set(Olympiad.CHAR_NAME, player.getName());

                    List<StatsSet> heroesToBe = new ArrayList<>();
                    heroesToBe.add(hero);

                    Hero.INSTANCE.computeNewHeroes(heroesToBe);

                    activeChar.sendMessage("Hero status added to getPlayer " + player.getName());
                    break;
                }
                case admin_olympiad_stop_period: {
                    Olympiad.cancelPeriodTasks();
                    ThreadPoolManager.INSTANCE.execute(new OlympiadEndTask());
                    break;
                }
                case admin_olympiad_start_period: {
                    Olympiad.cancelPeriodTasks();
                    ThreadPoolManager.INSTANCE.execute(new ValidationTask());
                    break;
                }
            }

        return true;
    }

    @Override
    public Enum[] getAdminCommandEnum() {
        return Commands.values();
    }

    private enum Commands {
        admin_oly_save,
        admin_add_oly_points,
        admin_oly_start,
        admin_add_hero,
        admin_oly_stop,
        admin_olympiad_stop_period,
        admin_olympiad_start_period
    }
}