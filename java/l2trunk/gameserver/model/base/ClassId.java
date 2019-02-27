package l2trunk.gameserver.model.base;

import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toInt;
import static l2trunk.gameserver.model.base.ClassType.*;
import static l2trunk.gameserver.model.base.Race.*;

/**
 * This class defines all classes (ex : human fighter, darkFighter...) that a getPlayer can chose.<BR><BR>
 * <p>
 * Data :<BR><BR>
 * <li>id : The Identifier of the class</li>
 * <li>classType : True if the class is a mage class</li>
 * <li>race : The race of this class</li>
 * <li>parent : The parent ClassId for male or null if this class is the root</li>
 * <li>parent2 : The parent2 ClassId for female or null if parent2 like parent</li>
 * <li>profession : The child profession of this Class</li><BR><BR>
 */
public enum ClassId {
    fighter(0, "Human Fighter", Fighter, Race.human, null, null),

    warrior(1, "Human Warrior", Fighter, Race.human, fighter, null),
    gladiator(2, "Gladiator", Fighter, Race.human, warrior, ClassType2.Warrior),
    warlord(3, "Warlord", Fighter, Race.human, warrior, ClassType2.Warrior),
    knight(4, "Human Knight", Fighter, Race.human, fighter, null),
    paladin(5, "Paladin", Fighter, Race.human, knight, ClassType2.Knight),
    darkAvenger(6, "DarkAvanger", Fighter, Race.human, knight, ClassType2.Knight),
    rogue(7, "Rogue", Fighter, Race.human, fighter, null),
    treasureHunter(8, "Treausure Hunter", Fighter, Race.human, rogue, ClassType2.Rogue),
    hawkeye(9, "Hawkeye", Fighter, Race.human, rogue, ClassType2.Rogue),

    mage(10, "Human Mage", Mystic, Race.human, null, null),
    wizard(11, "Human Wizzard", Mystic, Race.human, mage, null),
    sorceror(12, "Sorcerror", Mystic, Race.human, wizard, ClassType2.Wizard),
    necromancer(13, "Necromancer", Mystic, Race.human, wizard, ClassType2.Wizard),
    warlock(14, "Warlock", Mystic, Race.human, wizard, ClassType2.Summoner),
    cleric(15, "Cleric", Priest, Race.human, mage, null),
    bishop(16, "Bishop", Priest, Race.human, cleric, ClassType2.Healer),
    prophet(17, "Prophet", Priest, Race.human, cleric, ClassType2.Enchanter),

    elvenFighter(18, "Elven Fighter", Fighter, Race.elf, null, null),
    elvenKnight(19, "Elven Knight", Fighter, Race.elf, elvenFighter, null),
    templeKnight(20, "Temple Knight", Fighter, Race.elf, elvenKnight, ClassType2.Knight),
    swordSinger(21, "Sword Singer", Fighter, Race.elf, elvenKnight, ClassType2.Enchanter),
    elvenScout(22, "Elven Scout", Fighter, Race.elf, elvenFighter, null),
    plainsWalker(23, "Plains Walker", Fighter, Race.elf, elvenScout, ClassType2.Rogue),
    silverRanger(24, "Silver Ranger", Fighter, Race.elf, elvenScout, ClassType2.Rogue),

    elvenMage(25, "Elven Mage", Mystic, Race.elf, null, null),
    elvenWizard(26, "Elven Wizard", Mystic, Race.elf, elvenMage, null),
    spellsinger(27, "Spellsinger", Mystic, Race.elf, elvenWizard, ClassType2.Wizard),
    elementalSummoner(28, "Elemental Summoner", Mystic, Race.elf, elvenWizard, ClassType2.Summoner),
    oracle(29, "Elven Oracle", Priest, Race.elf, elvenMage, null),
    elder(30, "Elven Elder", Priest, Race.elf, oracle, ClassType2.Healer),

    darkFighter(31, "Dark Fighter", Fighter, Race.darkelf, null, null),
    palusKnight(32, "Palus Knight", Fighter, Race.darkelf, darkFighter, null),
    shillienKnight(33, "Shillien Knight", Fighter, Race.darkelf, palusKnight, ClassType2.Knight),
    bladedancer(34, "Bladedancer", Fighter, Race.darkelf, palusKnight, ClassType2.Enchanter),
    assassin(35, "Assassin", Fighter, Race.darkelf, darkFighter, null),
    abyssWalker(36, "Abyss Walker", Fighter, Race.darkelf, assassin, ClassType2.Rogue),
    phantomRanger(37, "Phantom Ranger", Fighter, Race.darkelf, assassin, ClassType2.Rogue),

    darkMage(38, "Dark Mage", Mystic, Race.darkelf, null, null),
    darkWizard(39, "Dark Wizard", Mystic, Race.darkelf, darkMage, null),
    spellhowler(40, "Spellhowler", Mystic, Race.darkelf, darkWizard, ClassType2.Wizard),
    phantomSummoner(41, "Phantom Summoner", Mystic, Race.darkelf, darkWizard, ClassType2.Summoner),
    shillienOracle(42, "Shillien Oracle", Priest, Race.darkelf, darkMage, null),
    shillienElder(43, "Shillien Elder", Priest, Race.darkelf, shillienOracle, ClassType2.Healer),

    orcFighter(44, "Orc Fighter", Fighter, Race.orc, null, null),
    orcRaider(45, "Orc Raider", Fighter, Race.orc, orcFighter, null),
    destroyer(46, "Destroyer", Fighter, Race.orc, orcRaider, ClassType2.Warrior),
    orcMonk(47, "Monk", Fighter, Race.orc, orcFighter, null),
    tyrant(48, "Tyrant", Fighter, Race.orc, orcMonk, ClassType2.Warrior),

    orcMage(49, "Orc Mage", Mystic, Race.orc, null, null),
    orcShaman(50, "Orc Shaman", Mystic, Race.orc, orcMage, null),
    overlord(51, "Overlord", Mystic, Race.orc, orcShaman, ClassType2.Enchanter),
    warcryer(52, "Warcryer", Mystic, Race.orc, orcShaman, ClassType2.Enchanter),

    dwarvenFighter(53, "Dwarven Fighter", Fighter, Race.dwarf, null, null),
    scavenger(54, "Scavenger", Fighter, Race.dwarf, dwarvenFighter, null),
    bountyHunter(55, "Bounty Hunter", Fighter, Race.dwarf, scavenger, ClassType2.Warrior),
    artisan(56, "Artisan", Fighter, Race.dwarf, dwarvenFighter, null),
    warsmith(57, "Warsmith", Fighter, Race.dwarf, artisan, ClassType2.Warrior),


    dummyEntry1(58, "dummyEntry1", null, null, null, null),

    duelist(88, "Duelist", Fighter, Race.human, gladiator, ClassType2.Warrior),
    dreadnought(89, "Dreadnought", Fighter, Race.human, warlord, ClassType2.Warrior),
    phoenixKnight(90, "Phoenix Knight", Fighter, Race.human, paladin, ClassType2.Knight),
    hellKnight(91, "Hell Knight", Fighter, Race.human, darkAvenger, ClassType2.Knight),
    sagittarius(92, "Sagittarius", Fighter, Race.human, hawkeye, ClassType2.Rogue),
    adventurer(93, "Adventurer", Fighter, Race.human, treasureHunter, ClassType2.Rogue),
    archmage(94, "Archmage", Mystic, Race.human, sorceror, ClassType2.Wizard),
    soultaker(95, "Soultaker", Mystic, Race.human, necromancer, ClassType2.Wizard),
    arcanaLord(96, "Arcana Lord", Mystic, Race.human, warlock, ClassType2.Summoner),
    cardinal(97, "Cardinal", Priest, Race.human, bishop, ClassType2.Healer),
    hierophant(98, "Hierophant", Priest, Race.human, prophet, ClassType2.Enchanter),

    evaTemplar(99, "Eva Templar", Fighter, Race.elf, templeKnight, ClassType2.Knight),
    swordMuse(100, "Sword Muse", Fighter, Race.elf, swordSinger, ClassType2.Enchanter),
    windRider(101, "Wind Rider", Fighter, Race.elf, plainsWalker, ClassType2.Rogue),
    moonlightSentinel(102, "Moonlight Sentinel", Fighter, Race.elf, silverRanger, ClassType2.Rogue),
    mysticMuse(103, "Mystic Muse", Mystic, Race.elf, spellsinger, ClassType2.Wizard),
    elementalMaster(104, "Elemental Master", Mystic, Race.elf, elementalSummoner, ClassType2.Summoner),
    evaSaint(105, "Eva Saint", Priest, Race.elf, elder, ClassType2.Healer),

    shillienTemplar(106, "Shillien Templar", Fighter, Race.darkelf, shillienKnight, ClassType2.Knight),
    spectralDancer(107, "Spectral Dancer", Fighter, Race.darkelf, bladedancer, ClassType2.Enchanter),
    ghostHunter(108, "Ghost Hunter", Fighter, Race.darkelf, abyssWalker, ClassType2.Rogue),
    ghostSentinel(109, "Ghost Sentinel", Fighter, Race.darkelf, phantomRanger, ClassType2.Rogue),
    stormScreamer(110, "Storm Screamer", Mystic, Race.darkelf, spellhowler, ClassType2.Wizard),
    spectralMaster(111, "Spectral Master", Mystic, Race.darkelf, phantomSummoner, ClassType2.Summoner),
    shillienSaint(112, "Shillien Saint", Priest, Race.darkelf, shillienElder, ClassType2.Healer),

    titan(113, "Titan", Fighter, Race.orc, destroyer, ClassType2.Warrior),
    grandKhauatari(114, "Grand Khauatari", Fighter, Race.orc, tyrant, ClassType2.Warrior),
    dominator(115, "Dominator", Mystic, Race.orc, overlord, ClassType2.Enchanter),
    doomcryer(116, "Doomcryer", Mystic, Race.orc, warcryer, ClassType2.Enchanter),

    fortuneSeeker(117, "Fortune Seeker", Fighter, Race.dwarf, bountyHunter, ClassType2.Warrior),
    maestro(118, "Maestro", Fighter, Race.dwarf, warsmith, ClassType2.Warrior),

    /**
     * Kamael
     */
    maleSoldier(123, "Male Soldier", Fighter, Race.kamael, null, null),
    femaleSoldier(124, "Female Soldier", Fighter, Race.kamael, null, null),
    trooper(125, "Trooper", Fighter, Race.kamael, maleSoldier, null),
    warder(126, "Warder", Fighter, Race.kamael, femaleSoldier, null),
    berserker(127, "Berserker", Fighter, Race.kamael, trooper, ClassType2.Warrior),
    maleSoulbreaker(128, "Male Soulbreaker", Fighter, Race.kamael, trooper, ClassType2.Warrior),
    femaleSoulbreaker(129, "Female Soulbreaker", Fighter, Race.kamael, warder, ClassType2.Warrior),
    arbalester(130, "Arbalester", Fighter, Race.kamael, warder, ClassType2.Rogue),
    doombringer(131, "Doombringer", Fighter, Race.kamael, berserker, ClassType2.Warrior),
    maleSoulhound(132, "Male Soulhound", Fighter, Race.kamael, maleSoulbreaker, ClassType2.Warrior),
    femaleSoulhound(133, "Female Soulhound", Fighter, Race.kamael, femaleSoulbreaker, ClassType2.Warrior),
    trickster(134, "Trickster", Fighter, Race.kamael, arbalester, ClassType2.Rogue),
    inspector(135, "Inspector", Fighter, Race.kamael, trooper, ClassType2.Enchanter),
    judicator(136, "Judicator", Fighter, Race.kamael, inspector, ClassType2.Enchanter);

    public static final List<ClassId> VALUES = List.of(values());
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

    public final int id;
    public final String name;
    public final ClassType classType;
    public final Race race;
    public final ClassId parent;
    private final ClassType2 type2;

    ClassId(int id, String name, ClassType classType, Race race, ClassId parent, ClassType2 classType2) {
        this.id = id;
        this.name = name;
        this.classType = classType;
        this.race = race;
        this.parent = parent;

        type2 = classType2;
    }

    private static Set<ClassId> getSet(Race race) {
        Set<ClassId> allOf = new HashSet<>();

        for (ClassId playerClass : ClassId.values())
            if (race == null || playerClass.race == race)
                if (playerClass.occupation() == 2)
                    allOf.add(playerClass);

        return allOf;
    }

    public static ClassId getById(String id) {
        return getById(toInt(id));
    }

    public static ClassId getById(int id) {
        return VALUES.stream().filter(cls -> cls.id == id).findFirst().orElse(dummyEntry1);
    }

    public boolean isMage() {
        return classType != Fighter;
    }

    public final boolean childOf(ClassId cid) {
        if (parent == null) return false;

        if (parent == cid) return true;

        return parent.childOf(cid);
    }

    /**
     * Return True if this Class<?> is equal to the selected ClassId or a child of the selected ClassId.<BR><BR>
     *
     * @param cid The parent ClassId to check
     */
    public final boolean equalsOrChildOf(ClassId cid) {
        return this == cid || childOf(cid);
    }

    /**
     * Return the child profession of this Class<?> (0=root, 1=child leve 1...).<BR><BR>
     */
    public final int occupation() {
        if (parent == null)
            return 0;

        return 1 + parent.occupation();
    }

    public ClassType2 getType2() {
        return type2;
    }

    public final Set<ClassId> getAvailableSubclasses() {
        if (race == Race.kamael)
            return kamaelSubclassSet;

        Set<ClassId> subclasses = null;

        if (occupation() == 2 || occupation() == 3) {
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

    @Override
    public String toString() {
        return name;
    }

}