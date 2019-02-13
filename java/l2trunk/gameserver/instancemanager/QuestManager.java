package l2trunk.gameserver.instancemanager;

import l2trunk.gameserver.model.quest.Quest;
import l2trunk.scripts.quests.*;

import java.util.HashSet;
import java.util.Set;

public final class QuestManager {

    private static final Set<Quest> quests = new HashSet<>();

    public static Quest getQuest(String name) {
        return quests.stream()
                .filter(q -> q.name.equalsIgnoreCase(name))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no quest with name " + name));
    }

    public static Quest getQuest(Class<? extends Quest> quest) {
        return quests.stream()
                .filter(q -> q.getClass() == quest)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("not  found quest " + quest));
    }

    public static Quest getQuest(int questId) {
        return quests.stream()
                .filter(q -> q.id == questId)
                .findFirst().orElseThrow(() -> new IllegalArgumentException("no quest with Id " + questId));
    }

    public static void addQuest(Quest newQuest) {
        quests.add(newQuest);
    }

    public static void initAllQuests() {
        new _001_LettersOfLove();
        new _002_WhatWomenWant();
        new _003_WilltheSealbeBroken();
        new _004_LongLivethePaagrioLord();
        new _005_MinersFavor();
        new _006_StepIntoTheFuture();
        new _007_ATripBegins();
        new _008_AnAdventureBegins();
        new _009_IntoTheCityOfHumans();
        new _010_IntoTheWorld();
        new _011_SecretMeetingWithKetraOrcs();
        new _012_SecretMeetingWithVarkaSilenos();
        new _013_ParcelDelivery();
        new _014_WhereaboutsoftheArchaeologist();
        new _015_SweetWhispers();
        new _016_TheComingDarkness();
        new _017_LightAndDarkness();
        new _018_MeetingwiththeGoldenRam();
        new _019_GoToThePastureland();
        new _020_BringUpWithLove();
        new _021_HiddenTruth();
        new _022_TragedyInVonHellmannForest();
        new _023_LidiasHeart();
        new _024_InhabitantsOfTheForestOfTheDead();
        new _025_HidingBehindTheTruth();
        new _026_TiredOfWaiting();
        new _027_ChestCaughtWithABaitOfWind();
        new _028_ChestCaughtWithABaitOfIcyAir();
        new _029_ChestCaughtWithABaitOfEarth();
        new _030_ChestCaughtWithABaitOfFire();
        new _031_SecretBuriedInTheSwamp();
        new _032_AnObviousLie();
        new _033_MakeAPairOfDressShoes();
        new _034_InSearchOfClothes();
        new _035_FindGlitteringJewelry();
        new _036_MakeASewingKit();
        new _037_PleaseMakeMeFormalWear();
        new _038_DragonFangs();
        new _039_RedEyedInvaders();
        new _040_ASpecialOrder();
        new _042_HelpTheUncle();
        new _043_HelpTheSister();
        new _044_HelpTheSon();
        new _045_ToTalkingIsland();
        new _046_OnceMoreInTheArmsOfTheMotherTree();
        new _047_IntoTheDarkForest();
        new _048_ToTheImmortalPlateau();
        new _049_TheRoadHome();
        new _050_LanoscosSpecialBait();
        new _051_OFullesSpecialBait();
        new _052_WilliesSpecialBait();
        new _053_LinnaeusSpecialBait();
        new _060_GoodWorksReward();
        new _061_LawEnforcement();
        new _062_PathOfTheDragoon();
        new _063_PathToWarder();
        new _064_CertifiedBerserker();
        new _065_PathToSoulBreaker();
        new _066_CertifiedArbalester();
        new _067_SagaOfTheDoombringer();
        new _068_SagaOfTheSoulHound();
        new _069_SagaOfTheTrickster();
        new _070_SagaOfThePhoenixKnight();
        new _071_SagaOfEvasTemplar();
        new _072_SagaOfTheSwordMuse();
        new _073_SagaOfTheDuelist();
        new _074_SagaOfTheDreadnoughts();
        new _075_SagaOfTheTitan();
        new _076_SagaOfTheGrandKhavatari();
        new _077_SagaOfTheDominator();
        new _078_SagaOfTheDoomcryer();
        new _079_SagaOfTheAdventurer();
        new _080_SagaOfTheWindRider();
        new _081_SagaOfTheGhostHunter();
        new _082_SagaOfTheSagittarius();
        new _083_SagaOfTheMoonlightSentinel();
        new _084_SagaOfTheGhostSentinel();
        new _085_SagaOfTheCardinal();
        new _086_SagaOfTheHierophant();
        new _087_SagaOfEvasSaint();
        new _088_SagaOfTheArchmage();
        new _089_SagaOfTheMysticMuse();
        new _090_SagaOfTheStormScreamer();
        new _091_SagaOfTheArcanaLord();
        new _092_SagaOfTheElementalMaster();
        new _093_SagaOfTheSpectralMaster();
        new _094_SagaOfTheSoultaker();
        new _095_SagaOfTheHellKnight();
        new _096_SagaOfTheSpectralDancer();
        new _097_SagaOfTheShillienTemplar();
        new _098_SagaOfTheShillienSaint();
        new _099_SagaOfTheFortuneSeeker();
        new _100_SagaOfTheMaestro();
        new _101_SwordOfSolidarity();
        new _10267_JourneyToGracia();
        new _10268_ToTheSeedOfInfinity();
        new _10269_ToTheSeedOfDestruction();
        new _10270_BirthOfTheSeed();
        new _10271_TheEnvelopingDarkness();
        new _10272_LightFragment();
        new _10273_GoodDayToFly();
        new _10274_CollectingInTheAir();
        new _10275_ContainingTheAttributePower();
        new _10276_MutatedKaneusGludio();
        new _10277_MutatedKaneusDion();
        new _10278_MutatedKaneusHeine();
        new _10279_MutatedKaneusOren();
        new _10280_MutatedKaneusSchuttgart();
        new _10281_MutatedKaneusRune();
        new _10282_ToTheSeedOfAnnihilation();
        new _10283_RequestOfIceMerchant();
        new _10284_AcquisionOfDivineSword();
        new _10285_MeetingSirra();
        new _10286_ReunionWithSirra();
        new _10287_StoryOfThoseLeft();
        new _10288_SecretMission();
        new _10289_FadeToBlack();
        new _10290_LandDragonConqueror();
        new _10291_FireDragonDestroyer();
        new _10292_SevenSignsGirlOfDoubt();
        new _10293_SevenSignsForbiddenBook();
        new _10294_SevenSignsMonasteryofSilence();
        new _10295_SevenSignsSolinasTomb();
        new _10296_SevenSignsPoweroftheSeal();
        new _102_SeaofSporesFever();
        new _103_SpiritOfCraftsman();
        new _104_SpiritOfMirror();
        new _10501_CapeEmbroideredSoulOne();
        new _10502_CapeEmbroideredSoulTwo();
        new _10503_CapeEmbroideredSoulThree();
        new _10504_JewelOfAntharas();
        new _10505_JewelOfValakas();
        new _105_SkirmishWithOrcs();
        new _106_ForgottenTruth();
        new _107_MercilessPunishment();
        new _108_JumbleTumbleDiamondFuss();
        new _109_InSearchOfTheNest();
        new _1102_Nottingale();
        new _1103_OracleTeleport();
        new _110_ToThePrimevalIsle();
        new _111_ElrokianHuntersProof();
        new _112_WalkOfFate();
        new _113_StatusOfTheBeaconTower();
        new _114_ResurrectionOfAnOldManager();
        new _115_TheOtherSideOfTruth();
        new _116_BeyondtheHillsofWinter();
        new _117_OceanOfDistantStar();
        new _118_ToLeadAndBeLed();
        new _119_LastImperialPrince();
        new _1201_DarkCloudMansion();
        new _1202_CrystalCaverns();
        new _120_PavelsResearch();
        new _121_PavelTheGiants();
        new _122_OminousNews();
        new _123_TheLeaderAndTheFollower();
        new _124_MeetingTheElroki();
        new _125_InTheNameOfEvilPart1();
        new _126_IntheNameofEvilPart2();
        new _128_PailakaSongofIceandFire();
        new _129_PailakaDevilsLegacy();
        new _130_PathToHellbound();
        new _131_BirdInACage();
        new _132_MatrasCuriosity();
        new _133_ThatsBloodyHot();
        new _134_TempleMissionary();
        new _135_TempleExecutor();
        new _136_MoreThanMeetsTheEye();
        new _137_TempleChampionPart1();
        new _138_TempleChampionPart2();
        new _139_ShadowFoxPart1();
        new _140_ShadowFoxPart2();
        new _141_ShadowFoxPart3();
        new _142_FallenAngelRequestOfDawn();
        new _143_FallenAngelRequestOfDusk();
        new _144_PailakaInjuredDragon();
        new _146_TheZeroHour();
        new _147_PathToBecomingAnEliteMercenary();
        new _148_PathToBecomingAnExaltedMercenary();
        new _151_CureforFeverDisease();
        new _152_ShardsOfGolem();
        new _153_DeliverGoods();
        new _154_SacrificeToSea();
        new _155_FindSirWindawood();
        new _156_MillenniumLove();
        new _157_RecoverSmuggled();
        new _158_SeedOfEvil();
        new _159_ProtectHeadsprings();
        new _160_NerupasFavor();
        new _161_FruitsOfMothertree();
        new _162_CurseOfUndergroundFortress();
        new _163_LegacyOfPoet();
        new _164_BloodFiend();
        new _165_ShilensHunt();
        new _166_DarkMass();
        new _167_DwarvenKinship();
        new _168_DeliverSupplies();
        new _169_OffspringOfNightmares();
        new _170_DangerousSeduction();
        new _171_ActsOfEvil();
        new _172_NewHorizons();
        new _173_ToTheIsleOfSouls();
        new _174_SupplyCheck();
        new _175_TheWayOfTheWarrior();
        new _176_StepsForHonor();
        new _178_IconicTrinity();
        new _179_IntoTheLargeCavern();
        new _182_NewRecruits();
        new _183_RelicExploration();
        new _184_NikolasCooperationContract();
        new _185_NikolasCooperationConsideration();
        new _186_ContractExecution();
        new _187_NikolasHeart();
        new _188_SealRemoval();
        new _189_ContractCompletion();
        new _190_LostDream();
        new _191_VainConclusion();
        new _192_SevenSignSeriesOfDoubt();
        new _193_SevenSignDyingMessage();
        new _194_SevenSignsMammonsContract();
        new _195_SevenSignsSecretRitualofthePriests();
        new _196_SevenSignsSealoftheEmperor();
        new _197_SevenSignsTheSacredBookofSeal();
        new _198_SevenSignsEmbryo();
        new _211_TrialOfChallenger();
        new _212_TrialOfDuty();
        new _213_TrialOfSeeker();
        new _214_TrialOfScholar();
        new _215_TrialOfPilgrim();
        new _216_TrialoftheGuildsman();
        new _217_TestimonyOfTrust();
        new _218_TestimonyOfLife();
        new _219_TestimonyOfFate();
        new _220_TestimonyOfGlory();
        new _221_TestimonyOfProsperity();
        new _222_TestOfDuelist();
        new _223_TestOfChampion();
        new _224_TestOfSagittarius();
        new _225_TestOfTheSearcher();
        new _226_TestOfHealer();
        new _227_TestOfTheReformer();
        new _228_TestOfMagus();
        new _229_TestOfWitchcraft();
        new _230_TestOfSummoner();
        new _231_TestOfTheMaestro();
        new _232_TestOfLord();
        new _233_TestOfWarspirit();
        new _234_FatesWhisper();
        new _235_MimirsElixir();
        new _236_SeedsOfChaos();
        new _237_WindsOfChange();
        new _238_SuccessFailureOfBusiness();
        new _239_WontYouJoinUs();
        new _240_ImTheOnlyOneYouCanTrust();
        new _241_PossessorOfaPreciousSoul1();
        new _242_PossessorOfaPreciousSoul2();
        new _246_PossessorOfaPreciousSoul3();
        new _247_PossessorOfaPreciousSoul4();
        new _249_PoisonedPlainsOfTheLizardmen();
        new _250_WatchWhatYouEat();
        new _251_NoSecrets();
        new _252_GoodSmell();
        new _254_LegendaryTales();
        new _255_Tutorial();
        new _257_GuardIsBusy();
        new _258_BringWolfPelts();
        new _259_RanchersPlea();
        new _260_HuntTheOrcs();
        new _261_CollectorsDream();
        new _262_TradewiththeIvoryTower();
        new _263_OrcSubjugation();
        new _264_KeenClaws();
        new _265_ChainsOfSlavery();
        new _266_PleaOfPixies();
        new _267_WrathOfVerdure();
        new _268_TracesOfEvil();
        new _269_InventionAmbition();
        new _270_TheOneWhoEndsSilence();
        new _271_ProofOfValor();
        new _272_WrathOfAncestors();
        new _273_InvadersOfHolyland();
        new _274_SkirmishWithTheWerewolves();
        new _275_BlackWingedSpies();
        new _276_HestuiTotem();
        new _277_GatekeepersOffering();
        new _278_HomeSecurity();
        new _279_TargetOfOpportunity();
        new _280_TheFoodChain();
        new _281_HeadForTheHills();
        new _283_TheFewTheProudTheBrave();
        new _284_MuertosFeather();
        new _286_FabulousFeathers();
        new _287_FiguringItOut();
        new _288_HandleWithCare();
        new _289_DeliciousFoodsAreMine();
        new _290_ThreatRemoval();
        new _291_RevengeOfTheRedbonnet();
        new _292_BrigandsSweep();
        new _293_HiddenVein();
        new _294_CovertBusiness();
        new _295_DreamsOfTheSkies();
        new _296_SilkOfTarantula();
        new _297_GateKeepersFavor();
        new _298_LizardmensConspiracy();
        new _299_GatherIngredientsforPie();
        new _300_HuntingLetoLizardman();
        new _303_CollectArrowheads();
        new _306_CrystalOfFireice();
        new _307_ControlDeviceoftheGiants();
        new _308_ReedFieldMaintenance();
        new _309_ForAGoodCause();
        new _310_OnlyWhatRemains();
        new _311_ExpulsionOfEvilSpirits();
        new _312_TakeAdvantageOfTheCrisis();
        new _313_CollectSpores();
        new _316_DestroyPlaguebringers();
        new _317_CatchTheWind();
        new _319_ScentOfDeath();
        new _320_BonesTellFuture();
        new _324_SweetestVenom();
        new _325_GrimCollector();
        new _326_VanquishRemnants();
        new _327_ReclaimTheLand();
        new _328_SenseForBusiness();
        new _329_CuriosityOfDwarf();
        new _330_AdeptOfTaste();
        new _331_ArrowForVengeance();
        new _333_BlackLionHunt();
        new _334_TheWishingPotion();
        new _335_TheSongOfTheHunter();
        new _336_CoinOfMagic();
        new _337_AudienceWithLandDragon();
        new _338_AlligatorHunter();
        new _340_SubjugationofLizardmen();
        new _341_HuntingForWildBeasts();
        new _343_UndertheShadowoftheIvoryTower();
        new _344_1000YearsEndofLamentation();
        new _345_MethodToRaiseTheDead();
        new _347_GoGetTheCalculator();
        new _348_ArrogantSearch();
        new _350_EnhanceYourWeapon();
        new _351_BlackSwan();
        new _352_HelpRoodRaiseANewPet();
        new _354_ConquestofAlligatorIsland();
        new _355_FamilyHonor();
        new _356_DigUpTheSeaOfSpores();
        new _357_WarehouseKeepersAmbition();
        new _358_IllegitimateChildOfAGoddess();
        new _359_ForSleeplessDeadmen();
        new _360_PlunderTheirSupplies();
        new _362_BardsMandolin();
        new _363_SorrowfulSoundofFlute();
        new _364_JovialAccordion();
        new _365_DevilsLegacy();
        new _366_SilverHairedShaman();
        new _367_ElectrifyingRecharge();
        new _368_TrespassingIntoTheSacredArea();
        new _369_CollectorOfJewels();
        new _370_AnElderSowsSeeds();
        new _371_ShriekOfGhosts();
        new _372_LegacyOfInsolence();
        new _373_SupplierOfReagents();
        new _376_GiantsExploration1();
        new _377_GiantsExploration2();
        new _378_MagnificentFeast();
        new _379_FantasyWine();
        new _380_BringOutTheFlavorOfIngredients();
        new _381_LetsBecomeARoyalMember();
        new _382_KailsMagicCoin();
        new _383_SearchingForTreasure();
        new _384_WarehouseKeepersPastime();
        new _385_YokeOfThePast();
        new _386_StolenDignity();
        new _401_PathToWarrior();
        new _402_PathToKnight();
        new _403_PathToRogue();
        new _404_PathToWizard();
        new _405_PathToCleric();
        new _406_PathToElvenKnight();
        new _407_PathToElvenScout();
        new _408_PathToElvenwizard();
        new _409_PathToOracle();
        new _410_PathToPalusKnight();
        new _411_PathToAssassin();
        new _412_PathToDarkwizard();
        new _413_PathToShillienOracle();
        new _414_PathToOrcRaider();
        new _415_PathToOrcMonk();
        new _416_PathToOrcShaman();
        new _417_PathToScavenger();
        new _418_PathToArtisan();
        new _419_GetaPet();
        new _420_LittleWings();
        new _421_LittleWingAdventures();
        new _422_RepentYourSins();
        new _423_TakeYourBestShot();
        new _426_QuestforFishingShot();
        new _431_WeddingMarch();
        new _432_BirthdayPartySong();
        new _450_GraveRobberMemberRescue();
        new _451_LuciensAltar();
        new _452_FindingtheLostSoldiers();
        new _453_NotStrongEnough();
        new _454_CompletelyLost();
        new _455_WingsofSand();
        new _456_DontKnowDontCare();
        new _457_LostAndFound();
        new _458_PerfectForm();
        new _461_RumbleInTheBase();
        new _463_IMustBeaGenius();
        new _464_Oath();
        new _501_ProofOfClanAlliance();
        new _503_PursuitClanAmbition();
        new _504_CompetitionForTheBanditStronghold();
        new _508_TheClansReputation();
        new _509_TheClansPrestige();
        new _510_AClansReputation();
        new _511_AwlUnderFoot();
        new _512_AwlUnderFoot();
        new _551_OlympiadStarter();
        new _552_OlympiadVeteran();
        new _553_OlympiadUndefeated();
        new _601_WatchingEyes();
        new _602_ShadowofLight();
        new _603_DaimontheWhiteEyedPart1();
        new _604_DaimontheWhiteEyedPart2();
        new _605_AllianceWithKetraOrcs();
        new _606_WarwithVarkaSilenos();
        new _607_ProveYourCourage();
        new _608_SlayTheEnemyCommander();
        new _609_MagicalPowerofWater1();
        new _610_MagicalPowerofWater2();
        new _611_AllianceWithVarkaSilenos();
        new _612_WarwithKetraOrcs();
        new _613_ProveYourCourage();
        new _614_SlayTheEnemyCommander();
        new _615_MagicalPowerofFire1();
        new _616_MagicalPowerofFire2();
        new _617_GatherTheFlames();
        new _618_IntoTheFlame();
        new _619_RelicsOfTheOldEmpire();
        new _620_FourGoblets();
        new _621_EggDelivery();
        new _622_DeliveryofSpecialLiquor();
        new _623_TheFinestFood();
        new _624_TheFinestIngredientsPart1();
        new _625_TheFinestIngredientsPart2();
        new _626_ADarkTwilight();
        new _627_HeartInSearchOfPower();
        new _628_HuntGoldenRam();
        new _629_CleanUpTheSwampOfScreams();
        new _631_DeliciousTopChoiceMeat();
        new _632_NecromancersRequest();
        new _633_InTheForgottenVillage();
        new _634_InSearchofDimensionalFragments();
        new _635_InTheDimensionalRift();
        new _636_TruthBeyond();
        new _637_ThroughOnceMore();
        new _638_SeekersOfTheHolyGrail();
        new _639_GuardiansOfTheHolyGrail();
        new _640_TheZeroHour();
        new _641_AttackSailren();
        new _642_APowerfulPrimevalCreature();
        new _643_RiseAndFallOfTheElrokiTribe();
        new _644_GraveRobberAnnihilation();
        new _645_GhostsOfBatur();
        new _646_SignsOfRevolt();
        new _647_InfluxOfMachines();
        new _648_AnIceMerchantsDream();
        new _649_ALooterandaRailroadMan();
        new _650_ABrokenDream();
        new _651_RunawayYouth();
        new _652_AnAgedExAdventurer();
        new _653_WildMaiden();
        new _654_JourneytoaSettlement();
        new _655_AGrandPlanForTamingWildBeasts();
        new _659_IdRatherBeCollectingFairyBreath();
        new _660_AidingtheFloranVillage();
        new _661_TheHarvestGroundsSafe();
        new _662_AGameOfCards();
        new _663_SeductiveWhispers();
        new _688_DefeatTheElrokianRaiders();
        new _690_JudesRequest();
        new _691_MatrasSuspiciousRequest();
        new _692_HowtoOpposeEvil();
        new _694_BreakThroughTheHallOfSuffering();
        new _695_DefendtheHallofSuffering();
        new _696_ConquertheHallofErosion();
        new _697_DefendtheHallofErosion();
        new _698_BlocktheLordsEscape();
        new _699_GuardianoftheSkies();
        new _700_CursedLife();
        new _701_ProofofExistence();
        new _702_ATrapForRevenge();
        new _704_Missqueen();
        new _708_PathToBecomingALordGludio();
        new _709_PathToBecomingALordDion();
        new _710_PathToBecomingALordGiran();
        new _711_PathToBecomingALordInnadril();
        new _712_PathToBecomingALordOren();
        new _713_PathToBecomingALordAden();
        new _714_PathToBecomingALordSchuttgart();
        new _715_PathToBecomingALordGoddard();
        new _716_PathToBecomingALordRune();
        new _717_ForTheSakeOfTheTerritoryGludio();
        new _718_ForTheSakeOfTheTerritoryDion();
        new _719_ForTheSakeOfTheTerritoryGiran();
        new _720_ForTheSakeOfTheTerritoryOren();
        new _721_ForTheSakeOfTheTerritoryAden();
        new _722_ForTheSakeOfTheTerritoryInnadril();
        new _723_ForTheSakeOfTheTerritoryGoddard();
        new _724_ForTheSakeOfTheTerritoryRune();
        new _725_ForTheSakeOfTheTerritoryShuttdart();
        new _726_LightwithintheDarkness();
        new _727_HopewithintheDarkness();
        new _729_ProtectTheTerritoryCatapult();
        new _730_ProtectTheSuppliesSafe();
        new _731_ProtectTheMilitaryAssociationLeader();
        new _732_ProtectTheReligiousAssociationLeader();
        new _733_ProtectTheEconomicAssociationLeader();
        new _734_PierceThroughAShield();
        new _735_MakeSpearsDull();
        new _736_WeakenTheMagic();
        new _737_DenyBlessings();
        new _738_DestroyKeyTargets();
        new _901_HowLavasaurusesAreMade();
        new _902_ReclaimOurEra();
        new _903_TheCallofAntharas();
        new _904_DragonTrophyAntharas();
        new _905_RefinedDragonBlood();
        new _906_TheCallofValakas();
        new _907_DragonTrophyValakas();
        new _999_T1Tutorial();
    }
}