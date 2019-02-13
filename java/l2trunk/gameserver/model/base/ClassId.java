package l2trunk.gameserver.model.base;

import java.util.List;

/**
 * This class defines all classes (ex : human fighter, darkFighter...) that a player can chose.<BR><BR>
 * <p>
 * Data :<BR><BR>
 * <li>id : The Identifier of the class</li>
 * <li>isMage : True if the class is a mage class</li>
 * <li>race : The race of this class</li>
 * <li>parent : The parent ClassId for male or null if this class is the root</li>
 * <li>parent2 : The parent2 ClassId for female or null if parent2 like parent</li>
 * <li>profession : The child profession of this Class</li><BR><BR>
 */
public enum ClassId {
    fighter(0, "Human Fighter", false, Race.human, null, null),

    warrior(1, "Human Warrior", false, Race.human, fighter, null),
    gladiator(2, "Gladiator", false, Race.human, warrior, ClassType2.Warrior),
    warlord(3, "Warlord", false, Race.human, warrior, ClassType2.Warrior),
    knight(4, "Human Knight", false, Race.human, fighter, null),
    paladin(5, "Paladin", false, Race.human, knight, ClassType2.Knight),
    darkAvenger(6, "DarkAvanger", false, Race.human, knight, ClassType2.Knight),
    rogue(7, "Rogue", false, Race.human, fighter, null),
    treasureHunter(8, "Treausure Hunter", false, Race.human, rogue, ClassType2.Rogue),
    hawkeye(9, "Hawkeye", false, Race.human, rogue, ClassType2.Rogue),

    mage(10, "Human Mage", true, Race.human, null, null),
    wizard(11, "Human Wizzard", true, Race.human, mage, null),
    sorceror(12, "Sorcerror", true, Race.human, wizard, ClassType2.Wizard),
    necromancer(13, "Necromancer", true, Race.human, wizard, ClassType2.Wizard),
    warlock(14, "Warlock", true, Race.human, wizard, ClassType2.Summoner),
    cleric(15, "Cleric", true, Race.human, mage, null),
    bishop(16, "Bishop", true, Race.human, cleric, ClassType2.Healer),
    prophet(17, "Prophet", true, Race.human, cleric, ClassType2.Enchanter),

    elvenFighter(18, "Elven Fighter", false, Race.elf, null, null),
    elvenKnight(19, "Elven Knight", false, Race.elf, elvenFighter, null),
    templeKnight(20, "Temple Knight", false, Race.elf, elvenKnight, ClassType2.Knight),
    swordSinger(21, "Sword Singer", false, Race.elf, elvenKnight, ClassType2.Enchanter),
    elvenScout(22, "Elven Scout", false, Race.elf, elvenFighter, null),
    plainsWalker(23, "Plains Walker", false, Race.elf, elvenScout, ClassType2.Rogue),
    silverRanger(24, "Silver Ranger", false, Race.elf, elvenScout, ClassType2.Rogue),

    elvenMage(25, "Elven Mage", true, Race.elf, null, null),
    elvenWizard(26, "Elven Wizard", true, Race.elf, elvenMage, null),
    spellsinger(27, "Spellsinger", true, Race.elf, elvenWizard, ClassType2.Wizard),
    elementalSummoner(28, "Elemental Summoner", true, Race.elf, elvenWizard, ClassType2.Summoner),
    oracle(29, "Elven Oracle", true, Race.elf, elvenMage, null),
    elder(30, "Elven Elder", true, Race.elf, oracle, ClassType2.Healer),

    darkFighter(31, "Dark Fighter", false, Race.darkelf, null, null),
    palusKnight(32, "Palus Knight", false, Race.darkelf, darkFighter, null),
    shillienKnight(33, "Shillien Knight", false, Race.darkelf, palusKnight, ClassType2.Knight),
    bladedancer(34, "Bladedancer", false, Race.darkelf, palusKnight, ClassType2.Enchanter),
    assassin(35, "Assassin", false, Race.darkelf, darkFighter, null),
    abyssWalker(36, "Abyss Walker", false, Race.darkelf, assassin, ClassType2.Rogue),
    phantomRanger(37, "Phantom Ranger", false, Race.darkelf, assassin, ClassType2.Rogue),

    darkMage(38, "Dark Mage", true, Race.darkelf, null, null),
    darkWizard(39, "Dark Wizard", true, Race.darkelf, darkMage, null),
    spellhowler(40, "Spellhowler", true, Race.darkelf, darkWizard, ClassType2.Wizard),
    phantomSummoner(41, "Phantom Summoner", true, Race.darkelf, darkWizard, ClassType2.Summoner),
    shillienOracle(42, "Shillien Oracle", true, Race.darkelf, darkMage, null),
    shillienElder(43, "Shillien Elder", true, Race.darkelf, shillienOracle, ClassType2.Healer),

    orcFighter(44, "Orc Fighter", false, Race.orc, null, null),
    orcRaider(45, "Orc Raider", false, Race.orc, orcFighter, null),
    destroyer(46, "Destroyer", false, Race.orc, orcRaider, ClassType2.Warrior),
    orcMonk(47, "Monk", false, Race.orc, orcFighter, null),
    tyrant(48, "Tyrant", false, Race.orc, orcMonk, ClassType2.Warrior),

    orcMage(49, "Orc Mage", true, Race.orc, null, null),
    orcShaman(50, "Orc Shaman", true, Race.orc, orcMage, null),
    overlord(51, "Overlord", true, Race.orc, orcShaman, ClassType2.Enchanter),
    warcryer(52, "Warcryer", true, Race.orc, orcShaman, ClassType2.Enchanter),

    dwarvenFighter(53, "Dwarven Fighter", false, Race.dwarf, null, null),
    scavenger(54, "Scavenger", false, Race.dwarf, dwarvenFighter, null),
    bountyHunter(55, "Bounty Hunter", false, Race.dwarf, scavenger, ClassType2.Warrior),
    artisan(56, "Artisan", false, Race.dwarf, dwarvenFighter, null),
    warsmith(57, "Warsmith", false, Race.dwarf, artisan, ClassType2.Warrior),


    dummyEntry1(58, "dummyEntry1", false, null, null, null),
    dummyEntry2(59, "dummyEntry2", false, null, null, null),
    dummyEntry3(60, "dummyEntry3", false, null, null, null),
    dummyEntry4(61, "dummyEntry4", false, null, null, null),
    dummyEntry5(62, "dummyEntry5", false, null, null, null),
    dummyEntry6(63, "dummyEntry6", false, null, null, null),
    dummyEntry7(64, "dummyEntry7", false, null, null, null),
    dummyEntry8(65, "dummyEntry8", false, null, null, null),
    dummyEntry9(66, "dummyEntry9", false, null, null, null),
    dummyEntry10(67, "dummyEntry10", false, null, null, null),
    dummyEntry11(68, "dummyEntry11", false, null, null, null),
    dummyEntry12(69, "dummyEntry12", false, null, null, null),
    dummyEntry13(70, "dummyEntry13", false, null, null, null),
    dummyEntry14(71, "dummyEntry14", false, null, null, null),
    dummyEntry15(72, "dummyEntry15", false, null, null, null),
    dummyEntry16(73, "dummyEntry16", false, null, null, null),
    dummyEntry17(74, "dummyEntry17", false, null, null, null),
    dummyEntry18(75, "dummyEntry18", false, null, null, null),
    dummyEntry19(76, "dummyEntry19", false, null, null, null),
    dummyEntry20(77, "dummyEntry20", false, null, null, null),
    dummyEntry21(78, "dummyEntry21", false, null, null, null),
    dummyEntry22(79, "dummyEntry22", false, null, null, null),
    dummyEntry23(80, "dummyEntry23", false, null, null, null),
    dummyEntry24(81, "dummyEntry24", false, null, null, null),
    dummyEntry25(82, "dummyEntry25", false, null, null, null),
    dummyEntry26(83, "dummyEntry26", false, null, null, null),
    dummyEntry27(84, "dummyEntry27", false, null, null, null),
    dummyEntry28(85, "dummyEntry28", false, null, null, null),
    dummyEntry29(86, "dummyEntry29", false, null, null, null),
    dummyEntry30(87, "dummyEntry30", false, null, null, null),


    duelist(88, "Duelist", false, Race.human, gladiator, ClassType2.Warrior),
    dreadnought(89, "Dreadnought", false, Race.human, warlord, ClassType2.Warrior),
    phoenixKnight(90, "Phoenix Knight", false, Race.human, paladin, ClassType2.Knight),
    hellKnight(91, "Hell Knight", false, Race.human, darkAvenger, ClassType2.Knight),
    sagittarius(92, "Sagittarius", false, Race.human, hawkeye, ClassType2.Rogue),
    adventurer(93, "Adventurer", false, Race.human, treasureHunter, ClassType2.Rogue),
    archmage(94, "Archmage", true, Race.human, sorceror, ClassType2.Wizard),
    soultaker(95, "Soultaker", true, Race.human, necromancer, ClassType2.Wizard),
    arcanaLord(96, "Arcana Lord", true, Race.human, warlock, ClassType2.Summoner),
    cardinal(97, "Cardinal", true, Race.human, bishop, ClassType2.Healer),
    hierophant(98, "Hierophant", true, Race.human, prophet, ClassType2.Enchanter),

    evaTemplar(99, "Eva Templar", false, Race.elf, templeKnight, ClassType2.Knight),
    swordMuse(100, "Sword Muse", false, Race.elf, swordSinger, ClassType2.Enchanter),
    windRider(101, "Wind Rider", false, Race.elf, plainsWalker, ClassType2.Rogue),
    moonlightSentinel(102, "Moonlight Sentinel", false, Race.elf, silverRanger, ClassType2.Rogue),
    mysticMuse(103, "Mystic Muse", true, Race.elf, spellsinger, ClassType2.Wizard),
    elementalMaster(104, "Elemental Master", true, Race.elf, elementalSummoner, ClassType2.Summoner),
    evaSaint(105, "Eva Saint", true, Race.elf, elder, ClassType2.Healer),

    shillienTemplar(106, "Shillien Templar", false, Race.darkelf, shillienKnight, ClassType2.Knight),
    spectralDancer(107, "Spectral Dancer", false, Race.darkelf, bladedancer, ClassType2.Enchanter),
    ghostHunter(108, "Ghost Hunter", false, Race.darkelf, abyssWalker, ClassType2.Rogue),
    ghostSentinel(109, "Ghost Sentinel", false, Race.darkelf, phantomRanger, ClassType2.Rogue),
    stormScreamer(110, "Storm Screamer", true, Race.darkelf, spellhowler, ClassType2.Wizard),
    spectralMaster(111, "Spectral Master", true, Race.darkelf, phantomSummoner, ClassType2.Summoner),
    shillienSaint(112, "Shillien Saint", true, Race.darkelf, shillienElder, ClassType2.Healer),

    titan(113, "Titan", false, Race.orc, destroyer, ClassType2.Warrior),
    grandKhauatari(114, "Grand Khauatari", false, Race.orc, tyrant, ClassType2.Warrior),
    dominator(115, "Dominator", true, Race.orc, overlord, ClassType2.Enchanter),
    doomcryer(116, "Doomcryer", true, Race.orc, warcryer, ClassType2.Enchanter),

    fortuneSeeker(117, "Fortune Seeker", false, Race.dwarf, bountyHunter, ClassType2.Warrior),
    maestro(118, "Maestro", false, Race.dwarf, warsmith, ClassType2.Warrior),

    dummyEntry31(119, "dummyEntry31", false, null, null, null),
    dummyEntry32(120, "dummyEntry32", false, null, null, null),
    dummyEntry33(121, "dummyEntry33", false, null, null, null),
    dummyEntry34(122, "dummyEntry34", false, null, null, null),

    /**
     * Kamael
     */
    maleSoldier(123, "Male Soldier", false, Race.kamael, null, null),
    femaleSoldier(124, "Female Soldier", false, Race.kamael, null, null),
    trooper(125, "Trooper", false, Race.kamael, maleSoldier, null),
    warder(126, "Warder", false, Race.kamael, femaleSoldier, null),
    berserker(127, "Berserker", false, Race.kamael, trooper, ClassType2.Warrior),
    maleSoulbreaker(128, "Male Soulbreaker", false, Race.kamael, trooper, ClassType2.Warrior),
    femaleSoulbreaker(129, "Female Soulbreaker", false, Race.kamael, warder, ClassType2.Warrior),
    arbalester(130, "Arbalester", false, Race.kamael, warder, ClassType2.Rogue),
    doombringer(131, "Doombringer", false, Race.kamael, berserker, ClassType2.Warrior),
    maleSoulhound(132, "Male Soulhound", false, Race.kamael, maleSoulbreaker, ClassType2.Warrior),
    femaleSoulhound(133, "Female Soulhound", false, Race.kamael, femaleSoulbreaker, ClassType2.Warrior),
    trickster(134, "Trickster", false, Race.kamael, arbalester, ClassType2.Rogue),
    inspector(135, "Inspector", false, Race.kamael, trooper, ClassType2.Enchanter),
    judicator(136, "Judicator", false, Race.kamael, inspector, ClassType2.Enchanter);

    public static final List<ClassId> VALUES = List.of(values());
    public final int id;
    public final String name;
    public final boolean isMage;
    public final Race race;
    public final ClassId parent;
    private final ClassType2 type2;

    ClassId(int id, String name, boolean isMage, Race race, ClassId parent, ClassType2 classType2) {
        this.id = id;
        this.name = name;
        this.isMage = isMage;
        this.race = race;
        this.parent = parent;

        type2 = classType2;
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

    public final ClassId parent() {
        return parent;
    }

    public ClassType2 getType2() {
        return type2;
    }

    @Override
    public String toString() {
        return name;
    }

}