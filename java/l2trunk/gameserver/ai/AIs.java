package l2trunk.gameserver.ai;

import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.scripts.ai.*;
import l2trunk.scripts.ai.DrakosWarrior;
import l2trunk.scripts.ai.PaganTemplete.AndreasCaptainRoyalGuard;
import l2trunk.scripts.ai.PaganTemplete.AndreasVanHalter;
import l2trunk.scripts.ai.SkyshadowMeadow.*;
import l2trunk.scripts.ai.Zone.DragonValley.DV_RB.*;
import l2trunk.scripts.ai.Zone.LairOfAntharas.BloodyKarik;
import l2trunk.scripts.ai.adept.*;
import l2trunk.scripts.ai.crypts_of_disgrace.ContaminatedBaturCommander;
import l2trunk.scripts.ai.crypts_of_disgrace.TurkaCommanderChief;
import l2trunk.scripts.ai.custom.*;
import l2trunk.scripts.ai.den_of_evil.HestuiGuard;
import l2trunk.scripts.ai.dragonvalley.*;
import l2trunk.scripts.ai.events.SpecialTree;
import l2trunk.scripts.ai.fog.GroupAI;
import l2trunk.scripts.ai.fog.TarBeetle;
import l2trunk.scripts.ai.freya.*;
import l2trunk.scripts.ai.hellbound.*;
import l2trunk.scripts.ai.primeval_isle.SprigantPoison;
import l2trunk.scripts.ai.residences.castle.Venom;
import l2trunk.scripts.ai.residences.fortress.siege.General;
import l2trunk.scripts.ai.residences.fortress.siege.GuardCaption;
import l2trunk.scripts.ai.seedofdestruction.DimensionMovingDevice;
import l2trunk.scripts.ai.seedofdestruction.Obelisk;
import l2trunk.scripts.ai.seedofinfinity.*;
import l2trunk.scripts.ai.selmahum.DrillSergeant;
import l2trunk.scripts.ai.selmahum.Fireplace;
import l2trunk.scripts.ai.selmahum.SelChef;
import l2trunk.scripts.ai.selmahum.SelSquadLeader;
import l2trunk.scripts.ai.suspiciousmerchant.*;

public class AIs {
    private AIs() {
    }

    public static CharacterAI getNewAI(String aiName, NpcInstance npc) {

        switch (aiName) {
            case "ai.BoatAI":
                return new BoatAI(npc);
            case "ai.CharacterAI":
                return new CharacterAI(npc);
            case "ai.DefaultAI":
                return new DefaultAI(npc);
            case "ai.Fighter":
                return new Fighter(npc);
            case "ai.Mystic":
                return new Mystic(npc);
            case "ai.Priest":
                return new Priest(npc);
            case "ai.Ranger":
                return new Ranger(npc);
            case "ai.adept.Adept":
                return new Adept(npc);
            case "ai.adept.AdeptAden":
                return new AdeptAden(npc);
            case "ai.adept.AdeptGiran":
                return new AdeptGiran(npc);
            case "ai.adept.AdeptGiran1":
                return new AdeptGiran1(npc);
            case "ai.adept.AdeptGiran2":
                return new AdeptGiran2(npc);
            case "ai.adept.AdeptGiran3":
                return new AdeptGiran3(npc);
            case "ai.adept.AdeptGiran4":
                return new AdeptGiran4(npc);
            case "ai.adept.AdeptGludio":
                return new AdeptGludio(npc);
            case "ai.adept.AdeptRune":
                return new AdeptRune(npc);
            case "ai.Aenkinel":
                return new Aenkinel(npc);
            case "ai.AirshipGuard1":
                return new AirshipGuard1(npc);
            case "ai.AirshipGuard2":
                return new AirshipGuard2(npc);
            case "ai.Alhena":
                return new Alhena(npc);
            case "ai.Anais":
                return new Anais(npc);
            case "ai.AngerOfSplendor":
                return new AngerOfSplendor(npc);
            case "ai.Antharas":
                return new Antharas(npc);
            case "ai.Archangel":
                return new Archangel(npc);
            case "ai.AttackMobNotPlayerFighter":
                return new AttackMobNotPlayerFighter(npc);
            case "ai.AwakenedMucrokian":
                return new AwakenedMucrokian(npc);
            case "ai.Baium":
                return new Baium(npc);
            case "ai.BaiumNpc":
                return new BaiumNpc(npc);
            case "ai.Baylor":
                return new Baylor(npc);
            case "ai.BlacksmithMammon":
                return new BlacksmithMammon(npc);
            case "ai.BladeOfSplendor":
                return new BladeOfSplendor(npc);
            case "ai.CabaleBuffer":
                return new CabaleBuffer(npc);
            case "ai.CaughtFighter":
                return new CaughtFighter(npc);
            case "ai.CaughtMystic":
                return new CaughtMystic(npc);
            case "ai.ClawsOfSplendor":
                return new ClawsOfSplendor(npc);
            case "ai.ContaminatedMucrokian":
                return new ContaminatedMucrokian(npc);
            case "ai.Core":
                return new Core(npc);
            case "ai.crypts_of_disgrace.ContaminatedBaturCommander":
                return new ContaminatedBaturCommander(npc);
            case "ai.crypts_of_disgrace.TurkaCommanderChief":
                return new TurkaCommanderChief(npc);
            case "ai.CrystallineGolem":
                return new CrystallineGolem(npc);
            case "ai.custom.FreyaEventAI":
                return new FreyaEventAI(npc);
            case "ai.custom.GvGBoss":
                return new GvGBoss(npc);
            case "ai.custom.LabyrinthLostBeholder":
                return new LabyrinthLostBeholder(npc);
            case "ai.custom.LabyrinthLostWarden":
                return new LabyrinthLostWarden(npc);
            case "ai.custom.LabyrinthLostWatcher":
                return new LabyrinthLostWatcher(npc);
            case "ai.custom.MutantChest":
                return new MutantChest(npc);
            case "ai.custom.Scrubwoman":
                return new Scrubwoman(npc);
            case "ai.custom.SSQAnakim":
                return new SSQAnakim(npc);
            case "ai.custom.SSQAnakimMinion":
                return new SSQAnakimMinion(npc);
            case "ai.custom.SSQLilimServantFighter":
                return new SSQLilimServantFighter(npc);
            case "ai.custom.SSQLilimServantMage":
                return new SSQLilimServantMage(npc);
            case "ai.custom.SSQLilith":
                return new SSQLilith(npc);
            case "ai.custom.SSQLilithMinion":
                return new SSQLilithMinion(npc);
            case "ai.DaimonTheWhiteEyed":
                return new DaimonTheWhiteEyed(npc);
            case "ai.DeluLizardmanSpecialAgent":
                return new DeluLizardmanSpecialAgent(npc);
            case "ai.DeluLizardmanSpecialCommander":
                return new l2trunk.scripts.ai.DeluLizardmanSpecialCommander(npc);
            case "ai.den_of_evil.HestuiGuard":
                return new HestuiGuard(npc);
//        case "ai.ResidenceDoor": return new  l2trunk.scripts.ai.door.ResidenceDoor(npc);
//        case "ai.SiegeDoor": return new  l2trunk.scripts.ai.door.SiegeDoor(npc);
//        case "ai.SSQDoor": return new  l2trunk.scripts.ai.door.SSQDoor(npc);
            case "ai.dragonvalley.BatwingDrake":
                return new l2trunk.scripts.ai.dragonvalley.BatwingDrake(npc);
            case "ai.dragonvalley.DragonKnight":
                return new l2trunk.scripts.ai.dragonvalley.DragonKnight(npc);
            case "ai.dragonvalley.DragonRaid":
                return new l2trunk.scripts.ai.dragonvalley.DragonRaid(npc);
            case "ai.dragonvalley.DragonScout":
                return new l2trunk.scripts.ai.dragonvalley.DragonScout(npc);
            case "ai.dragonvalley.DragonTracker":
                return new l2trunk.scripts.ai.dragonvalley.DragonTracker(npc);
            case "ai.dragonvalley.DrakeBosses":
                return new DrakeBosses(npc);
            case "ai.dragonvalley.DrakeMagma":
                return new DrakeMagma(npc);
            case "ai.dragonvalley.DrakeRunners":
                return new DrakeRunners(npc);
            case "ai.dragonvalley.DrakosHunter":
                return new DrakosHunter(npc);
            case "ai.dragonvalley.DrakosWarrior":
                return new l2trunk.scripts.ai.dragonvalley.DrakosWarrior(npc);
            case "ai.dragonvalley.DustTracker":
                return new DustTracker(npc);
            case "ai.dragonvalley.EmeraldDrake":
                return new EmeraldDrake(npc);
            case "ai.dragonvalley.ExplodingOrcGhost":
                return new ExplodingOrcGhost(npc);
            case "ai.dragonvalley.Howl":
                return new Howl(npc);
            case "ai.dragonvalley.Knoriks":
                return new Knoriks(npc);
            case "ai.dragonvalley.Knoriks1":
                return new Knoriks1(npc);
            case "ai.dragonvalley.Knoriks2":
                return new Knoriks2(npc);
            case "ai.dragonvalley.Knoriks3":
                return new Knoriks3(npc);
            case "ai.dragonvalley.Knoriks4":
                return new Knoriks4(npc);
            case "ai.dragonvalley.Knoriks5":
                return new Knoriks5(npc);
            case "ai.dragonvalley.Necromancer":
                return new Necromancer(npc);
            case "ai.dragonvalley.Patrollers":
                return new Patrollers(npc);
            case "ai.dragonvalley.PatrollersNoWatch":
                return new PatrollersNoWatch(npc);
            case "ai.dragonvalley.SandTracker":
                return new SandTracker(npc);
            case "ai.DrakosWarrior":
                return new l2trunk.scripts.ai.DrakosWarrior(npc);
            case "ai.Edwin":
                return new Edwin(npc);
            case "ai.EdwinFollower":
                return new EdwinFollower(npc);
            case "ai.ElcardiaAssistant":
                return new ElcardiaAssistant(npc);
            case "ai.Elpy":
                return new Elpy(npc);
            case "ai.EtisEtina":
                return new EtisEtina(npc);
            case "ai.EvasGiftBox":
                return new EvasGiftBox(npc);
            case "ai.events.SpecialTree":
                return new SpecialTree(npc);
            case "ai.EvilNpc":
                return new l2trunk.scripts.ai.EvilNpc(npc);
            case "ai.EvilSpiritsMagicForce":
                return new l2trunk.scripts.ai.EvilSpiritsMagicForce(npc);
            case "ai.FangOfSplendor":
                return new l2trunk.scripts.ai.FangOfSplendor(npc);
            case "ai.FantasyIslePaddies":
                return new l2trunk.scripts.ai.FantasyIslePaddies(npc);
            case "ai.FieldMachine":
                return new l2trunk.scripts.ai.FieldMachine(npc);
            case "ai.fog.GroupAI":
                return new GroupAI(npc);
            case "ai.fog.TarBeetle":
                return new TarBeetle(npc);
            case "ai.FollowNpc":
                return new FollowNpc(npc);
            case "ai.FortuneBug":
                return new l2trunk.scripts.ai.FortuneBug(npc);
            case "ai.freya.AnnihilationFighter":
                return new l2trunk.scripts.ai.freya.AnnihilationFighter(npc);
            case "ai.freya.AntharasMinion":
                return new l2trunk.scripts.ai.freya.AntharasMinion(npc);
            case "ai.freya.FreyaQuest":
                return new l2trunk.scripts.ai.freya.FreyaQuest(npc);
            case "ai.freya.FreyaStandHard":
                return new l2trunk.scripts.ai.freya.FreyaStandHard(npc);
            case "ai.freya.FreyaStandNormal":
                return new l2trunk.scripts.ai.freya.FreyaStandNormal(npc);
            case "ai.freya.FreyaThrone":
                return new FreyaThrone(npc);
            case "ai.freya.Glacier":
                return new Glacier(npc);
            case "ai.freya.IceCaptainKnight":
                return new IceCaptainKnight(npc);
            case "ai.freya.IceCastleBreath":
                return new IceCastleBreath(npc);
            case "ai.freya.IceKnightNormal":
                return new IceKnightNormal(npc);
            case "ai.freya.JiniaGuild":
                return new l2trunk.scripts.ai.freya.JiniaGuild(npc);
            case "ai.freya.JiniaKnight":
                return new l2trunk.scripts.ai.freya.JiniaKnight(npc);
            case "ai.freya.Maguen":
                return new l2trunk.scripts.ai.freya.Maguen(npc);
            case "ai.freya.SeerUgoros":
                return new l2trunk.scripts.ai.freya.SeerUgoros(npc);
            case "ai.freya.SolinaKnight":
                return new l2trunk.scripts.ai.freya.SolinaKnight(npc);
            case "ai.freya.ValakasMinion":
                return new l2trunk.scripts.ai.freya.ValakasMinion(npc);
            case "ai.FrightenedOrc":
                return new l2trunk.scripts.ai.FrightenedOrc(npc);
            case "ai.FrostBuffalo":
                return new l2trunk.scripts.ai.FrostBuffalo(npc);
            case "ai.Furance":
                return new l2trunk.scripts.ai.Furance(npc);
            case "ai.Furnace":
                return new l2trunk.scripts.ai.Furnace(npc);
            case "ai.Gargos":
                return new l2trunk.scripts.ai.Gargos(npc);
            case "ai.GatekeeperZombie":
                return new l2trunk.scripts.ai.GatekeeperZombie(npc);
            case "ai.GeneralDilios":
                return new l2trunk.scripts.ai.GeneralDilios(npc);
            case "ai.GhostOfVonHellmannsPage":
                return new l2trunk.scripts.ai.GhostOfVonHellmannsPage(npc);
            case "ai.Gordon":
                return new l2trunk.scripts.ai.Gordon(npc);
            case "ai.GraveRobberSummoner":
                return new l2trunk.scripts.ai.GraveRobberSummoner(npc);
//        case "ai.groups.FlyingGracia": return new l2trunk.scripts.ai.groups.FlyingGracia(npc);
            case "ai.groups.ForgeoftheGods":
                return new l2trunk.scripts.ai.groups.ForgeoftheGods(npc);
            case "ai.groups.PavelRuins":
                return new l2trunk.scripts.ai.groups.PavelRuins(npc);
            case "ai.groups.StakatoNest":
                return new l2trunk.scripts.ai.groups.StakatoNest(npc);
            case "ai.GuardianAltar":
                return new l2trunk.scripts.ai.GuardianAltar(npc);
            case "ai.GuardianAngel":
                return new l2trunk.scripts.ai.GuardianAngel(npc);
            case "ai.GuardianWaterspirit":
                return new l2trunk.scripts.ai.GuardianWaterspirit(npc);
//        case "ai.GuardofDawn": return new  l2trunk.scripts.ai.GuardofDawn(npc);
//        case "ai.GuardofDawnFemale": return new  l2trunk.scripts.ai.GuardofDawnFemale(npc);
//        case "ai.GuardofDawnStat": return new  l2trunk.scripts.ai.GuardofDawnStat(npc);
            case "ai.GuardoftheGrave":
                return new l2trunk.scripts.ai.GuardoftheGrave(npc);
            case "ai.GuardRndWalkAndAnim":
                return new l2trunk.scripts.ai.GuardRndWalkAndAnim(npc);
            case "ai.HandysBlock":
                return new l2trunk.scripts.ai.HandysBlock(npc);
            case "ai.HekatonPrime":
                return new l2trunk.scripts.ai.HekatonPrime(npc);
            case "ai.hellbound.Beleth":
                return new l2trunk.scripts.ai.hellbound.Beleth(npc);
            case "ai.hellbound.BelethClone":
                return new l2trunk.scripts.ai.hellbound.BelethClone(npc);
            case "ai.hellbound.Chimera":
                return new l2trunk.scripts.ai.hellbound.Chimera(npc);
            case "ai.hellbound.CoralGardenGolem":
                return new l2trunk.scripts.ai.hellbound.CoralGardenGolem(npc);
            case "ai.hellbound.Darion":
                return new l2trunk.scripts.ai.hellbound.Darion(npc);
            case "ai.hellbound.DarionChallenger":
                return new l2trunk.scripts.ai.hellbound.DarionChallenger(npc);
            case "ai.hellbound.DarionFaithfulServant":
                return new l2trunk.scripts.ai.hellbound.DarionFaithfulServant(npc);
            case "ai.hellbound.DarionFaithfulServant6Floor":
                return new l2trunk.scripts.ai.hellbound.DarionFaithfulServant6Floor(npc);
            case "ai.hellbound.DarionFaithfulServant8Floor":
                return new l2trunk.scripts.ai.hellbound.DarionFaithfulServant8Floor(npc);
            case "ai.hellbound.Darnel":
                return new l2trunk.scripts.ai.hellbound.Darnel(npc);
            case "ai.hellbound.DemonPrince":
                return new l2trunk.scripts.ai.hellbound.DemonPrince(npc);
            case "ai.hellbound.Epidos":
                return new l2trunk.scripts.ai.hellbound.Epidos(npc);
            case "ai.hellbound.FloatingGhost":
                return new l2trunk.scripts.ai.hellbound.FloatingGhost(npc);
            case "ai.hellbound.FoundryWorker":
                return new l2trunk.scripts.ai.hellbound.FoundryWorker(npc);
            case "ai.hellbound.GreaterEvil":
                return new l2trunk.scripts.ai.hellbound.GreaterEvil(npc);
            case "ai.hellbound.Leodas":
                return new l2trunk.scripts.ai.hellbound.Leodas(npc);
            case "ai.hellbound.MasterFestina":
                return new l2trunk.scripts.ai.hellbound.MasterFestina(npc);
            case "ai.hellbound.MasterZelos":
                return new l2trunk.scripts.ai.hellbound.MasterZelos(npc);
            case "ai.hellbound.MutatedElpy":
                return new l2trunk.scripts.ai.hellbound.MutatedElpy(npc);
            case "ai.hellbound.NaiaCube":
                return new l2trunk.scripts.ai.hellbound.NaiaCube(npc);
            case "ai.hellbound.NaiaLock":
                return new l2trunk.scripts.ai.hellbound.NaiaLock(npc);
            case "ai.hellbound.NaiaRoomController":
                return new l2trunk.scripts.ai.hellbound.NaiaRoomController(npc);
            case "ai.hellbound.NaiaSpore":
                return new l2trunk.scripts.ai.hellbound.NaiaSpore(npc);
            case "ai.hellbound.OriginalSinWarden":
                return new l2trunk.scripts.ai.hellbound.OriginalSinWarden(npc);
            case "ai.hellbound.OriginalSinWarden6Floor":
                return new OriginalSinWarden6Floor(npc);
            case "ai.hellbound.OriginalSinWarden8Floor":
                return new OriginalSinWarden8Floor(npc);
            case "ai.hellbound.OutpostCaptain":
                return new OutpostCaptain(npc);
            case "ai.hellbound.OutpostGuards":
                return new OutpostGuards(npc);
            case "ai.hellbound.Pylon":
                return new Pylon(npc);
            case "ai.hellbound.Ranku":
                return new Ranku(npc);
            case "ai.hellbound.RankuScapegoat":
                return new RankuScapegoat(npc);
            case "ai.hellbound.Sandstorm":
                return new Sandstorm(npc);
            case "ai.hellbound.SteelCitadelKeymaster":
                return new SteelCitadelKeymaster(npc);
            case "ai.hellbound.TorturedNative":
                return new TorturedNative(npc);
            case "ai.hellbound.TownGuard":
                return new l2trunk.scripts.ai.hellbound.TownGuard(npc);
            case "ai.hellbound.Tully":
                return new l2trunk.scripts.ai.hellbound.Tully(npc);
            case "ai.hellbound.Typhoon":
                return new l2trunk.scripts.ai.hellbound.Typhoon(npc);
            case "ai.HotSpringsMob":
                return new l2trunk.scripts.ai.HotSpringsMob(npc);
            case "ai.isle_of_prayer.DarkWaterDragon":
                return new l2trunk.scripts.ai.isle_of_prayer.DarkWaterDragon(npc);
            case "ai.isle_of_prayer.EmeraldDoorController":
                return new l2trunk.scripts.ai.isle_of_prayer.EmeraldDoorController(npc);
            case "ai.isle_of_prayer.EvasProtector":
                return new l2trunk.scripts.ai.isle_of_prayer.EvasProtector(npc);
            case "ai.isle_of_prayer.FafurionKindred":
                return new l2trunk.scripts.ai.isle_of_prayer.FafurionKindred(npc);
            case "ai.isle_of_prayer.IsleOfPrayerFighter":
                return new l2trunk.scripts.ai.isle_of_prayer.IsleOfPrayerFighter(npc);
            case "ai.isle_of_prayer.IsleOfPrayerMystic":
                return new l2trunk.scripts.ai.isle_of_prayer.IsleOfPrayerMystic(npc);
            case "ai.isle_of_prayer.Kechi":
                return new l2trunk.scripts.ai.isle_of_prayer.Kechi(npc);
            case "ai.isle_of_prayer.Shade":
                return new l2trunk.scripts.ai.isle_of_prayer.Shade(npc);
            case "ai.isle_of_prayer.WaterDragonDetractor":
                return new l2trunk.scripts.ai.isle_of_prayer.WaterDragonDetractor(npc);
            case "ai.Jaradine":
                return new l2trunk.scripts.ai.Jaradine(npc);
            case "ai.Kama56Boss":
                return new l2trunk.scripts.ai.Kama56Boss(npc);
            case "ai.Kama56Minion":
                return new l2trunk.scripts.ai.Kama56Minion(npc);
            case "ai.Kama63Minion":
                return new l2trunk.scripts.ai.Kama63Minion(npc);
            case "ai.Kanabion":
                return new l2trunk.scripts.ai.Kanabion(npc);
            case "ai.KanadisFollower":
                return new l2trunk.scripts.ai.KanadisFollower(npc);
            case "ai.KanadisGuide":
                return new l2trunk.scripts.ai.KanadisGuide(npc);
            case "ai.KarulBugbear":
                return new l2trunk.scripts.ai.KarulBugbear(npc);
            case "ai.KashasEye":
                return new l2trunk.scripts.ai.KashasEye(npc);
            case "ai.Kasiel":
                return new l2trunk.scripts.ai.Kasiel(npc);
            case "ai.KrateisCubeWatcherBlue":
                return new l2trunk.scripts.ai.KrateisCubeWatcherBlue(npc);
            case "ai.KrateisCubeWatcherRed":
                return new l2trunk.scripts.ai.KrateisCubeWatcherRed(npc);
            case "ai.KrateisFighter":
                return new l2trunk.scripts.ai.KrateisFighter(npc);
            case "ai.Kreed":
                return new l2trunk.scripts.ai.Kreed(npc);
            case "ai.LafiLakfi":
                return new l2trunk.scripts.ai.LafiLakfi(npc);
            case "ai.Leandro":
                return new l2trunk.scripts.ai.Leandro(npc);
            case "ai.Leogul":
                return new l2trunk.scripts.ai.Leogul(npc);
            case "ai.LeylaDancer":
                return new l2trunk.scripts.ai.LeylaDancer(npc);
            case "ai.LeylaMira":
                return new l2trunk.scripts.ai.LeylaMira(npc);
            case "ai.LizardmanSummoner":
                return new l2trunk.scripts.ai.LizardmanSummoner(npc);
            case "ai.MasterYogi":
                return new l2trunk.scripts.ai.MasterYogi(npc);
            case "ai.MCIndividual":
                return new l2trunk.scripts.ai.MCIndividual(npc);
            case "ai.MCManager":
                return new l2trunk.scripts.ai.MCManager(npc);
            case "ai.monas.FurnaceSpawnRoom.DivinityMonster":
                return new l2trunk.scripts.ai.monas.FurnaceSpawnRoom.DivinityMonster(npc);
            case "ai.monas.FurnaceSpawnRoom.FurnaceBalance":
                return new l2trunk.scripts.ai.monas.FurnaceSpawnRoom.FurnaceBalance(npc);
            case "ai.monas.FurnaceSpawnRoom.FurnaceMagic":
                return new l2trunk.scripts.ai.monas.FurnaceSpawnRoom.FurnaceMagic(npc);
            case "ai.monas.FurnaceSpawnRoom.FurnaceProtection":
                return new l2trunk.scripts.ai.monas.FurnaceSpawnRoom.FurnaceProtection(npc);
            case "ai.monas.FurnaceSpawnRoom.FurnaceWill":
                return new l2trunk.scripts.ai.monas.FurnaceSpawnRoom.FurnaceWill(npc);
            case "ai.monas.Furnface":
                return new l2trunk.scripts.ai.monas.Furnface(npc);
            case "ai.monastery_of_silence.DivinityMonster":
                return new l2trunk.scripts.ai.monastery_of_silence.DivinityMonster(npc);
            case "ai.MoSMonk":
                return new l2trunk.scripts.ai.MoSMonk(npc);
            case "ai.Mucrokian":
                return new l2trunk.scripts.ai.Mucrokian(npc);
            case "ai.MusicBox":
                return new l2trunk.scripts.ai.MusicBox(npc);
            case "ai.NightAgressionMystic":
                return new l2trunk.scripts.ai.NightAgressionMystic(npc);
            case "ai.NihilInvaderChest":
                return new l2trunk.scripts.ai.NihilInvaderChest(npc);
            case "ai.OiAriosh":
                return new l2trunk.scripts.ai.OiAriosh(npc);
            case "ai.OlMahumGeneral":
                return new l2trunk.scripts.ai.OlMahumGeneral(npc);
            case "ai.Orfen":
                return new l2trunk.scripts.ai.Orfen(npc);
            case "ai.Orfen_RibaIren":
                return new l2trunk.scripts.ai.Orfen_RibaIren(npc);
            case "ai.other.PailakaDevilsLegacy.FollowersLematan":
                return new l2trunk.scripts.ai.other.PailakaDevilsLegacy.FollowersLematan(npc);
            case "ai.other.PailakaDevilsLegacy.Lematan":
                return new l2trunk.scripts.ai.other.PailakaDevilsLegacy.Lematan(npc);
            case "ai.other.PailakaDevilsLegacy.PowderKeg":
                return new l2trunk.scripts.ai.other.PailakaDevilsLegacy.PowderKeg(npc);
            case "ai.PaganGuard":
                return new l2trunk.scripts.ai.PaganGuard(npc);
            case "ai.PaganTemplete.AltarGatekeeper":
                return new l2trunk.scripts.ai.PaganTemplete.AltarGatekeeper(npc);
            case "ai.PaganTemplete.AndreasCaptainRoyalGuard":
                return new AndreasCaptainRoyalGuard(npc);
            case "ai.PaganTemplete.AndreasVanHalter":
                return new AndreasVanHalter(npc);
            case "ai.PaganTemplete.TriolsBeliever":
                return new l2trunk.scripts.ai.PaganTemplete.TriolsBeliever(npc);
            case "ai.PaganTemplete.TriolsLayperson":
                return new l2trunk.scripts.ai.PaganTemplete.TriolsLayperson(npc);
            case "ai.PiratesKing":
                return new l2trunk.scripts.ai.PiratesKing(npc);
            case "ai.primeval_isle.SprigantPoison":
                return new SprigantPoison(npc);
            case "ai.primeval_isle.SprigantStun":
                return new l2trunk.scripts.ai.primeval_isle.SprigantStun(npc);
            case "ai.PrisonGuard":
                return new l2trunk.scripts.ai.PrisonGuard(npc);
            case "ai.Pronghorn":
                return new Pronghorn(npc);
            case "ai.Pterosaur":
                return new Pterosaur(npc);
            case "ai.QueenAntNurse":
                return new QueenAntNurse(npc);
            case "ai.Quest024Fighter":
                return new Quest024Fighter(npc);
            case "ai.Quest024Mystic":
                return new l2trunk.scripts.ai.Quest024Mystic(npc);
            case "ai.Quest421FairyTree":
                return new Quest421FairyTree(npc);
            case "ai.QuestNotAggroMob":
                return new l2trunk.scripts.ai.QuestNotAggroMob(npc);
            case "ai.RagnaHealer":
                return new l2trunk.scripts.ai.RagnaHealer(npc);
            case "ai.Remy":
                return new l2trunk.scripts.ai.Remy(npc);
            case "ai.residences.castle.ArtefactAI":
                return new l2trunk.scripts.ai.residences.castle.ArtefactAI(npc);
            case "ai.residences.castle.Venom":
                return new Venom(npc);
            case "ai.residences.clanhall.AlfredVonHellmann":
                return new l2trunk.scripts.ai.residences.clanhall.AlfredVonHellmann(npc);
            case "ai.residences.clanhall.GiselleVonHellmann":
                return new l2trunk.scripts.ai.residences.clanhall.GiselleVonHellmann(npc);
            case "ai.residences.clanhall.LidiaVonHellmann":
                return new l2trunk.scripts.ai.residences.clanhall.LidiaVonHellmann(npc);
            case "ai.residences.clanhall.MatchBerserker":
                return new l2trunk.scripts.ai.residences.clanhall.MatchBerserker(npc);
            case "ai.residences.clanhall.MatchCleric":
                return new l2trunk.scripts.ai.residences.clanhall.MatchCleric(npc);
            case "ai.residences.clanhall.MatchFighter":
                return new l2trunk.scripts.ai.residences.clanhall.MatchFighter(npc);
            case "ai.residences.clanhall.MatchLeader":
                return new l2trunk.scripts.ai.residences.clanhall.MatchLeader(npc);
            case "ai.residences.clanhall.MatchScout":
                return new l2trunk.scripts.ai.residences.clanhall.MatchScout(npc);
            case "ai.residences.clanhall.MatchTrief":
                return new l2trunk.scripts.ai.residences.clanhall.MatchTrief(npc);
            case "ai.residences.clanhall.RainbowEnragedYeti":
                return new l2trunk.scripts.ai.residences.clanhall.RainbowEnragedYeti(npc);
            case "ai.residences.clanhall.RainbowYeti":
                return new l2trunk.scripts.ai.residences.clanhall.RainbowYeti(npc);
            case "ai.residences.dominion.Catapult":
                return new l2trunk.scripts.ai.residences.dominion.Catapult(npc);
            case "ai.residences.dominion.EconomicAssociationLeader":
                return new l2trunk.scripts.ai.residences.dominion.EconomicAssociationLeader(npc);
            case "ai.residences.dominion.MercenaryCaptain":
                return new l2trunk.scripts.ai.residences.dominion.MercenaryCaptain(npc);
            case "ai.residences.dominion.MilitaryAssociationLeader":
                return new l2trunk.scripts.ai.residences.dominion.MilitaryAssociationLeader(npc);
            case "ai.residences.dominion.ReligiousAssociationLeader":
                return new l2trunk.scripts.ai.residences.dominion.ReligiousAssociationLeader(npc);
            case "ai.residences.dominion.SuppliesSafe":
                return new l2trunk.scripts.ai.residences.dominion.SuppliesSafe(npc);
            case "ai.residences.fortress.siege.ArcherCaption":
                return new l2trunk.scripts.ai.residences.fortress.siege.ArcherCaption(npc);
            case "ai.residences.fortress.siege.Ballista":
                return new l2trunk.scripts.ai.residences.fortress.siege.Ballista(npc);
            case "ai.residences.fortress.siege.General":
                return new General(npc);
            case "ai.residences.fortress.siege.GuardCaption":
                return new GuardCaption(npc);
            case "ai.residences.fortress.siege.MercenaryCaption":
                return new l2trunk.scripts.ai.residences.fortress.siege.MercenaryCaption(npc);
            case "ai.residences.fortress.siege.Minister":
                return new l2trunk.scripts.ai.residences.fortress.siege.Minister(npc);
            case "ai.residences.fortress.siege.RebelCommander":
                return new l2trunk.scripts.ai.residences.fortress.siege.RebelCommander(npc);
            case "ai.residences.fortress.siege.SupportUnitCaption":
                return new l2trunk.scripts.ai.residences.fortress.siege.SupportUnitCaption(npc);
            case "ai.residences.SiegeGuard":
                return new l2trunk.scripts.ai.residences.SiegeGuard(npc);
            case "ai.residences.SiegeGuardFighter":
                return new l2trunk.scripts.ai.residences.SiegeGuardFighter(npc);
            case "ai.residences.SiegeGuardMystic":
                return new l2trunk.scripts.ai.residences.SiegeGuardMystic(npc);
            case "ai.residences.SiegeGuardPriest":
                return new l2trunk.scripts.ai.residences.SiegeGuardPriest(npc);
            case "ai.residences.SiegeGuardRanger":
                return new l2trunk.scripts.ai.residences.SiegeGuardRanger(npc);
            case "ai.RndTeleportFighter":
                return new l2trunk.scripts.ai.RndTeleportFighter(npc);
            case "ai.RndWalkAndAnim":
                return new l2trunk.scripts.ai.RndWalkAndAnim(npc);
            case "ai.Rogin":
                return new Rogin(npc);
            case "ai.Rokar":
                return new Rokar(npc);
            case "ai.Rooney":
                return new Rooney(npc);
            case "ai.Scarecrow":
                return new l2trunk.scripts.ai.Scarecrow(npc);
            case "ai.SealDevice":
                return new l2trunk.scripts.ai.SealDevice(npc);
            case "ai.SeducedInvestigator":
                return new l2trunk.scripts.ai.SeducedInvestigator(npc);
            case "ai.seedofdestruction.DimensionMovingDevice":
                return new DimensionMovingDevice(npc);
            case "ai.seedofdestruction.GreatPowerfulDevice":
                return new l2trunk.scripts.ai.seedofdestruction.GreatPowerfulDevice(npc);
            case "ai.seedofdestruction.Obelisk":
                return new Obelisk(npc);
            case "ai.seedofdestruction.ThroneofDestruction":
                return new l2trunk.scripts.ai.seedofdestruction.ThroneofDestruction(npc);
            case "ai.seedofdestruction.Tiat":
                return new l2trunk.scripts.ai.seedofdestruction.Tiat(npc);
            case "ai.seedofdestruction.TiatCamera":
                return new l2trunk.scripts.ai.seedofdestruction.TiatCamera(npc);
            case "ai.seedofdestruction.TiatsTrap":
                return new l2trunk.scripts.ai.seedofdestruction.TiatsTrap(npc);
            case "ai.seedofinfinity.AliveTumor":
                return new l2trunk.scripts.ai.seedofinfinity.AliveTumor(npc);
            case "ai.seedofinfinity.Ekimus":
                return new Ekimus(npc);
            case "ai.seedofinfinity.EkimusFood":
                return new EkimusFood(npc);
            case "ai.seedofinfinity.FeralHound":
                return new FeralHound(npc);
            case "ai.seedofinfinity.SoulCoffin":
                return new SoulCoffin(npc);
            case "ai.seedofinfinity.SymbolofCohemenes":
                return new SymbolofCohemenes(npc);
            case "ai.seedofinfinity.WardofDeath":
                return new l2trunk.scripts.ai.seedofinfinity.WardofDeath(npc);
            case "ai.seedofinfinity.YehanBrother":
                return new l2trunk.scripts.ai.seedofinfinity.YehanBrother(npc);
            case "ai.SeerFlouros":
                return new l2trunk.scripts.ai.SeerFlouros(npc);
            case "ai.selmahum.DrillSergeant":
                return new DrillSergeant(npc);
            case "ai.selmahum.Fireplace":
                return new Fireplace(npc);
            case "ai.selmahum.SelChef":
                return new SelChef(npc);
            case "ai.selmahum.SelSquadLeader":
                return new SelSquadLeader(npc);
            case "ai.SkyshadowMeadow.DrillSergeant":
                return new DrillSergeant(npc);
            case "ai.SkyshadowMeadow.Fire":
                return new Fire(npc);
            case "ai.SkyshadowMeadow.FireFeed":
                return new FireFeed(npc);
            case "ai.SkyshadowMeadow.SelMahumRecruit":
                return new l2trunk.scripts.ai.SkyshadowMeadow.SelMahumRecruit(npc);
            case "ai.SkyshadowMeadow.SelMahumShef":
                return new SelMahumShef(npc);
            case "ai.SkyshadowMeadow.SelMahumSquadLeader":
                return new SelMahumSquadLeader(npc);
            case "ai.SkyshadowMeadow.SelMahumTrainer":
                return new SelMahumTrainer(npc);
            case "ai.SolinaGuardian":
                return new SolinaGuardian(npc);
            case "ai.Suppressor":
                return new Suppressor(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantAaru":
                return new SuspiciousMerchantAaru(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantAntharas":
                return new SuspiciousMerchantAntharas(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantArchaic":
                return new SuspiciousMerchantArchaic(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantBayou":
                return new SuspiciousMerchantBayou(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantBorderland":
                return new SuspiciousMerchantBorderland(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantCloud":
                return new SuspiciousMerchantCloud(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantDemon":
                return new SuspiciousMerchantDemon(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantDragonspine":
                return new SuspiciousMerchantDragonspine(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantFloran":
                return new SuspiciousMerchantFloran(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantHive":
                return new SuspiciousMerchantHive(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantHunters":
                return new SuspiciousMerchantHunters(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantIvoryTower":
                return new SuspiciousMerchantIvoryTower(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantMarshland":
                return new SuspiciousMerchantMarshland(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantMonastic":
                return new SuspiciousMerchantMonastic(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantNarsell":
                return new SuspiciousMerchantNarsell(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantSGludio":
                return new SuspiciousMerchantSGludio(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantShanty":
                return new SuspiciousMerchantShanty(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantTanor":
                return new SuspiciousMerchantTanor(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantValley":
                return new SuspiciousMerchantValley(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantWestern":
                return new SuspiciousMerchantWestern(npc);
            case "ai.suspiciousmerchant.SuspiciousMerchantWhiteSands":
                return new SuspiciousMerchantWhiteSands(npc);
            case "ai.TalkingGuard":
                return new l2trunk.scripts.ai.TalkingGuard(npc);
            case "ai.Tate":
                return new Tate(npc);
            case "ai.Taurin":
                return new Taurin(npc);
            case "ai.Tears":
                return new Tears(npc);
            case "ai.Thomas":
                return new Thomas(npc);
            case "ai.Tiberias":
                return new l2trunk.scripts.ai.Tiberias(npc);
            case "ai.TimakOrcTroopLeader":
                return new TimakOrcTroopLeader(npc);
            case "ai.Toma":
                return new Toma(npc);
            case "ai.TotemSummon":
                return new TotemSummon(npc);
            case "ai.Valakas":
                return new Valakas(npc);
            case "ai.WatchmanMonster":
                return new WatchmanMonster(npc);
            case "ai.WitchWarder":
                return new WitchWarder(npc);
            case "ai.Yakand":
                return new Yakand(npc);
            case "ai.ZakenAnchor":
                return new ZakenAnchor(npc);
            case "ai.ZakenDaytime":
                return new ZakenDaytime(npc);
            case "ai.ZakenDaytime83":
                return new ZakenDaytime83(npc);
            case "ai.ZakenNightly":
                return new ZakenNightly(npc);
            case "ai.Zone.DragonValley.DV_RB.BlackdaggerWing":
                return new BlackdaggerWing(npc);
            case "ai.Zone.DragonValley.DV_RB.BleedingFly":
                return new BleedingFly(npc);
            case "ai.Zone.DragonValley.DV_RB.BleedingFlyMinion":
                return new BleedingFlyMinion(npc);
            case "ai.Zone.DragonValley.DV_RB.DustRider":
                return new DustRider(npc);
            case "ai.Zone.DragonValley.DV_RB.EmeraldHorn":
                return new EmeraldHorn(npc);
            case "ai.Zone.DragonValley.DV_RB.MuscleBomber":
                return new MuscleBomber(npc);
            case "ai.Zone.DragonValley.DV_RB.ShadowSummoner":
                return new ShadowSummoner(npc);
            case "ai.Zone.DragonValley.DV_RB.SpikeSlasher":
                return new SpikeSlasher(npc);
            case "ai.Zone.DragonValley.DV_RB.SpikeSlasherMinion":
                return new SpikeSlasherMinion(npc);
            case "ai.Zone.LairOfAntharas.BloodyKarik":
                return new BloodyKarik(npc);

        }
        throw new IllegalArgumentException("no AI for " + aiName);
    }
}
