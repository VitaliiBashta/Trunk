package l2trunk.gameserver.model.entity.olympiad;


import l2trunk.commons.threading.RunnableImpl;
import l2trunk.gameserver.instancemanager.OlympiadHistoryManager;
import l2trunk.gameserver.model.entity.Hero;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ValidationTask extends RunnableImpl {
    private static final Logger _log = LoggerFactory.getLogger(ValidationTask.class);

    @Override
    public void runImpl() {
        OlympiadHistoryManager.INSTANCE.switchData();

        OlympiadDatabase.sortHerosToBe();
        OlympiadDatabase.saveNobleData();
        if (Hero.INSTANCE.computeNewHeroes(Olympiad.heroesToBe))
            _log.warn("Olympiad: Error while computing new heroes!");
        Olympiad.period = 0;
        Olympiad.currentCycle++;

        OlympiadDatabase.cleanupNobles();
        OlympiadDatabase.loadNoblesRank();
        OlympiadDatabase.setNewOlympiadEnd();

        Olympiad.init();
        OlympiadDatabase.save();
    }
}