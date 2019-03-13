package l2trunk.gameserver.model.entity.olympiad;

import l2trunk.commons.threading.RunnableImpl;
import l2trunk.commons.util.Rnd;
import l2trunk.gameserver.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public final class OlympiadManager extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(OlympiadManager.class);

    private final Map<Integer, OlympiadGame> olympiadInstances = new ConcurrentHashMap<>();

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void runImpl() {
        if (Olympiad.isOlympiadEnd())
            return;

        while (Olympiad.inCompPeriod()) {
            if (Olympiad.nobles.isEmpty()) {
                sleep(60000);
                continue;
            }

            while (Olympiad.inCompPeriod()) {
                // Подготовка и запуск внеклассовых боев
                if (Olympiad._nonClassBasedRegisters.size() >= Config.NONCLASS_GAME_MIN)
                    prepareBattles(CompType.NON_CLASSED, Olympiad._nonClassBasedRegisters);

                // Подготовка и запуск классовых боев
                for (List<Integer> battles : Olympiad.CLASS_BASED_REGISTERS.values())
                    if (battles.size() >= Config.CLASS_GAME_MIN)
                        prepareBattles(CompType.CLASSED, battles);

                // Подготовка и запуск командных боев
                if (Olympiad.TEAM_BASED_REGISTERS.size() >= Config.TEAM_GAME_MIN)
                    prepareTeamBattles(CompType.TEAM, Olympiad.TEAM_BASED_REGISTERS.values());

                sleep(30000);
            }

            sleep(30000);
        }

        Olympiad.CLASS_BASED_REGISTERS.clear();
        Olympiad._nonClassBasedRegisters.clear();
        Olympiad.TEAM_BASED_REGISTERS.clear();

        // when comp time finish wait for all games terminated before execute the cleanup code
        boolean allGamesTerminated = false;

        // wait for all games terminated
        while (!allGamesTerminated) {
            sleep(30000);

            if (olympiadInstances.isEmpty())
                break;

            allGamesTerminated = true;
            for (OlympiadGame game : olympiadInstances.values()) {
                if (game.getTask() != null && !game.getTask().isTerminated())
                    allGamesTerminated = false;
            }
        }

        olympiadInstances.clear();
    }

    private void prepareBattles(CompType type, List<Integer> list) {
        boolean firstGameLaunched = false;
        NobleSelector selector = new NobleSelector(list.size());
        list.stream()
                .filter(Objects::nonNull)
                .forEach(noble -> selector.add(noble, Olympiad.getNoblePoints(noble)));

        for (int i = 0; i < Olympiad.STADIUMS.length; i++) {
            try {
                if (Olympiad.STADIUMS[i].isBusy())
                    continue;
                if (selector.size() < type.getMinSize())
                    break;

                OlympiadGame game = new OlympiadGame(i, type, nextOpponents(selector, type));
                OlympiadGameTask gameTask = new OlympiadGameTask(game, BattleStatus.Begining, 0, 1);
                game.sheduleTask(gameTask);
                if (Config.OLYMPIAD_SHOUT_ONCE_PER_START && firstGameLaunched)
                    gameTask.setShoutGameStart(false);

                olympiadInstances.put(i, game);

                Olympiad.STADIUMS[i].setStadiaBusy();
                firstGameLaunched = true;
            } catch (Exception e) {
                _log.error("Error while preparing Olympiad Battle", e);
            }
        }
    }

    private void prepareTeamBattles(CompType type, Collection<List<Integer>> list) {
        for (int i = 0; i < Olympiad.STADIUMS.length; i++) {
            try {
                if (Olympiad.STADIUMS[i].isBusy())
                    continue;
                if (list.size() < type.getMinSize())
                    break;

                List<Integer> nextOpponents = nextTeamOpponents(list, type);
                if (nextOpponents == null)
                    break;

                OlympiadGame game = new OlympiadGame(i, type, nextOpponents);
                game.sheduleTask(new OlympiadGameTask(game, BattleStatus.Begining, 0, 1));

                olympiadInstances.put(i, game);

                Olympiad.STADIUMS[i].setStadiaBusy();
            } catch (Exception e) {
                _log.error("Error while preparing Olympiad Team Battle", e);
            }
        }
    }

    public void freeOlympiadInstance(int index) {
        olympiadInstances.remove(index);
        Olympiad.STADIUMS[index].setStadiaFree();
    }

    public OlympiadGame getOlympiadInstance(int index) {
        return olympiadInstances.get(index);
    }

    public Map<Integer, OlympiadGame> getOlympiadGames() {
        return olympiadInstances;
    }

    private List<Integer> nextOpponents(NobleSelector selector, CompType type) {
        List<Integer> opponents = new ArrayList<>();
        Integer noble;

        selector.reset();
        for (int i = 0; i < type.getMinSize(); i++) {
            noble = selector.select();
            if (noble == null) // DS: error handling ?
                break;
            opponents.add(noble);
            removeOpponent(noble);
        }

        return opponents;
    }

    private List<Integer> nextTeamOpponents(Collection<List<Integer>> list, CompType type) {
        if (list.isEmpty())
            return null;
        List<Integer> opponents = new CopyOnWriteArrayList<>();
        List<List<Integer>> a = new ArrayList<>(list);

        for (int i = 0; i < type.getMinSize(); i++) {
            if (a.size() < 1)
                continue;
            List<Integer> team = a.remove(Rnd.get(a.size()));
            if (team.size() == 3)
                for (Integer noble : team) {
                    opponents.add(noble);
                    removeOpponent(noble);
                }
            else {
                for (Integer noble : team)
                    removeOpponent(noble);
                i--;
            }

            list.remove(team);
        }

        return opponents;
    }

    private void removeOpponent(Integer noble) {
        Olympiad.CLASS_BASED_REGISTERS.removeValue(noble);
        Olympiad._nonClassBasedRegisters.remove(noble);
        Olympiad.TEAM_BASED_REGISTERS.removeValue(noble);
    }
}