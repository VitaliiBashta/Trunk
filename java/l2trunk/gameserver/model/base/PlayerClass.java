package l2trunk.gameserver.model.base;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static l2trunk.gameserver.model.base.ClassId.*;
import static l2trunk.gameserver.model.base.Race.*;

public class PlayerClass {


    private static final Set<ClassId> mainSubclassSet;
    private static final Set<ClassId> kamaelSubclassSet;
    private static final Set<ClassId> neverSubclassed = Set.of(overlord, warsmith);
    private static final Set<ClassId> TANKS = Set.of(darkAvenger, paladin, templeKnight, shillienKnight);
    private static final Set<ClassId> DAGGERS = Set.of(treasureHunter, abyssWalker, plainsWalker);
    private static final Set<ClassId> ARCHERS = Set.of(hawkeye, silverRanger, phantomRanger);
    private static final Set<ClassId> SUMMONERS = Set.of(warlock, elementalSummoner, phantomSummoner);
    private static final Set<ClassId> MAGES = Set.of(sorceror, spellsinger, spellhowler);
    /**
     * kamael SubClasses
     */
    private static final Set<ClassId> subclasseSet6 = Set.of(inspector);
    private static final Map<ClassId, Set<ClassId>> subclassSetMap = new HashMap<>();

    static {
        kamaelSubclassSet = getSet(kamael);

        Set<ClassId> subclasses = getSet(null);
        subclasses.removeAll(neverSubclassed);
        subclasses.removeAll(kamaelSubclassSet);

        mainSubclassSet = subclasses;

        subclassSetMap.put(darkAvenger, TANKS);
        subclassSetMap.put(hellKnight, TANKS);
        subclassSetMap.put(paladin, TANKS);
        subclassSetMap.put(phoenixKnight, TANKS);
        subclassSetMap.put(templeKnight, TANKS);
        subclassSetMap.put(evaTemplar, TANKS);
        subclassSetMap.put(shillienKnight, TANKS);
        subclassSetMap.put(shillienTemplar, TANKS);

        subclassSetMap.put(treasureHunter, DAGGERS);
        subclassSetMap.put(adventurer, DAGGERS);
        subclassSetMap.put(abyssWalker, DAGGERS);
        subclassSetMap.put(ghostHunter, DAGGERS);
        subclassSetMap.put(plainsWalker, DAGGERS);
        subclassSetMap.put(windRider, DAGGERS);

        subclassSetMap.put(hawkeye, ARCHERS);
        subclassSetMap.put(sagittarius, ARCHERS);
        subclassSetMap.put(silverRanger, ARCHERS);
        subclassSetMap.put(moonlightSentinel, ARCHERS);
        subclassSetMap.put(phantomRanger, ARCHERS);
        subclassSetMap.put(ghostSentinel, ARCHERS);

        subclassSetMap.put(warlock, SUMMONERS);
        subclassSetMap.put(arcanaLord, SUMMONERS);
        subclassSetMap.put(elementalSummoner, SUMMONERS);
        subclassSetMap.put(elementalMaster, SUMMONERS);
        subclassSetMap.put(phantomSummoner, SUMMONERS);
        subclassSetMap.put(spectralMaster, SUMMONERS);

        subclassSetMap.put(sorceror, MAGES);
        subclassSetMap.put(archmage, MAGES);
        subclassSetMap.put(spellsinger, MAGES);
        subclassSetMap.put(mysticMuse, MAGES);
        subclassSetMap.put(spellhowler, MAGES);
        subclassSetMap.put(stormScreamer, MAGES);

        subclassSetMap.put(doombringer, subclasseSet6);
        subclassSetMap.put(maleSoulhound, subclasseSet6);
        subclassSetMap.put(femaleSoulhound, subclasseSet6);
        subclassSetMap.put(trickster, subclasseSet6);

        subclassSetMap.put(duelist, Set.of(gladiator));
        subclassSetMap.put(dreadnought, Set.of(warlord));
        subclassSetMap.put(soultaker, Set.of(necromancer));
        subclassSetMap.put(cardinal, Set.of(bishop));
        subclassSetMap.put(hierophant, Set.of(prophet));
        subclassSetMap.put(swordMuse, Set.of(swordSinger));
        subclassSetMap.put(evaSaint, Set.of(elder));
        subclassSetMap.put(spectralDancer, Set.of(bladedancer));
        subclassSetMap.put(titan, Set.of(destroyer));
        subclassSetMap.put(grandKhauatari, Set.of(tyrant));
        subclassSetMap.put(dominator, Set.of(overlord));
        subclassSetMap.put(doomcryer, Set.of(warcryer));
    }

    public final Race race;
    public ClassType type;
    private int occupation;

    PlayerClass(Race race, ClassType type, int occupation) {
        this.race = race;
        this.occupation = occupation;
        this.type = type;
    }

    private static Set<ClassId> getSet(Race race) {
        Set<ClassId> allOf = new HashSet<>();

        for (ClassId playerClass : ClassId.values())
            if (race == null || playerClass.race == race)
                if (playerClass.occupation() == 2)
                    allOf.add(playerClass);

        return allOf;
    }

    public final Set<ClassId> getAvailableSubclasses() {
        if (race == Race.kamael)
            return kamaelSubclassSet;

        Set<ClassId> subclasses = null;

        if (occupation == 2 || occupation == 3) {
            subclasses = new HashSet<>(mainSubclassSet);

            subclasses.removeAll(neverSubclassed);
            subclasses.remove(this);

            switch (race) {
                case elf:
                    subclasses.removeAll(getSet(darkelf));
                    break;
                case darkelf:
                    subclasses.removeAll(getSet(elf));
                    break;
            }

            Set<ClassId> unavailableClasses = subclassSetMap.get(this);

            if (unavailableClasses != null)
                subclasses.removeAll(unavailableClasses);
        }

        return subclasses;
    }
}