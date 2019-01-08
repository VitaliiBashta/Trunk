package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.templates.npc.NpcTemplate;
import l2trunk.scripts.npc.model.residences.fortress.peace.SupportUnitCaptionInstance;
import l2trunk.scripts.npc.model.residences.fortress.peace.SuspiciousMerchantInstance;
import l2trunk.scripts.npc.model.residences.fortress.siege.BackupPowerUnitInstance;
import l2trunk.scripts.npc.model.residences.fortress.siege.BallistaInstance;
import l2trunk.scripts.npc.model.residences.fortress.siege.ControlUnitInstance;

public class AllNpcInstances {

    private AllNpcInstances() {
    }

    @SuppressWarnings("unchecked")
    public static <T extends NpcInstance> T getInstance(int id, String type) {
        NpcTemplate template = NpcHolder.getTemplateByType(type);

        switch (type + "Instance") {
            case "NpcInstance":
                return (T) new NpcInstance(id, template);
//            npcInstances.put("PetBabyInstance", PetBabyInstance.class);
//            npcInstances.put("PetInstance", PetInstance.class);

            case "AdventurerInstance":
                return (T) new AdventurerInstance(id, template);
            case "AirShipControllerInstance":
                return (T) new l2trunk.gameserver.model.instances.AirShipControllerInstance(id, template);
            case "ArtefactInstance":
                return (T) new l2trunk.gameserver.model.instances.ArtefactInstance(id, template);
            case "BetaNPCInstance":
                return (T) new l2trunk.gameserver.model.instances.BetaNPCInstance(id, template);
            case "BlockInstance":
                return (T) new l2trunk.gameserver.model.instances.BlockInstance(id, template);
            case "BossInstance":
                return (T) new l2trunk.gameserver.model.instances.BossInstance(id, template);
            case "ChestInstance":
                return (T) new l2trunk.gameserver.model.instances.ChestInstance(id, template);
            case "ClanAirShipControllerInstance":
                return (T) new l2trunk.gameserver.model.instances.ClanAirShipControllerInstance(id, template);
            case "ClanRewardInstance":
                return (T) new l2trunk.gameserver.model.instances.ClanRewardInstance(id, template);
            case "ClanTraderInstance":
                return (T) new l2trunk.gameserver.model.instances.ClanTraderInstance(id, template);
//            case "ControlKeyInstance":
//                return (T) new l2trunk.gameserver.model.instances.ControlKeyInstance();
            case "DeadManInstance":
                return (T) new l2trunk.gameserver.model.instances.DeadManInstance(id, template);
//            case "DecoyInstance":
//                return (T) new l2trunk.gameserver.model.instances.DecoyInstance(id, template);
            case "DonateNPCInstance":
                return (T) new l2trunk.gameserver.model.instances.DonateNPCInstance(id, template);
//            case "DoorInstance":
//                return (T) new l2trunk.gameserver.model.instances.DoorInstance(id, template);
            case "FameManagerInstance":
                return (T) new l2trunk.gameserver.model.instances.FameManagerInstance(id, template);
            case "FeedableBeastInstance":
                return (T) new l2trunk.gameserver.model.instances.FeedableBeastInstance(id, template);
            case "FestivalGuideInstance":
                return (T) new l2trunk.gameserver.model.instances.FestivalGuideInstance(id, template);
            case "FestivalMonsterInstance":
                return (T) new l2trunk.gameserver.model.instances.FestivalMonsterInstance(id, template);
            case "FishermanInstance":
                return (T) new l2trunk.gameserver.model.instances.FishermanInstance(id, template);
            case "FurnaceInstance":
                return (T) new l2trunk.gameserver.model.instances.FurnaceInstance(id, template);
            case "GuardInstance":
                return (T) new l2trunk.gameserver.model.instances.GuardInstance(id, template);
            case "ItemAuctionBrokerInstance":
                return (T) new l2trunk.gameserver.model.instances.ItemAuctionBrokerInstance(id, template);
            case "LotteryManagerInstance":
                return (T) new l2trunk.gameserver.model.instances.LotteryManagerInstance(id, template);
            case "ManorManagerInstance":
                return (T) new l2trunk.gameserver.model.instances.ManorManagerInstance(id, template);
            case "MerchantInstance":
                return (T) new l2trunk.gameserver.model.instances.MerchantInstance(id, template);
            case "MercManagerInstance":
                return (T) new l2trunk.gameserver.model.instances.MercManagerInstance(id, template);
            case "MinionInstance":
                return (T) new l2trunk.gameserver.model.instances.MinionInstance(id, template);
            case "MonsterInstance":
                return (T) new l2trunk.gameserver.model.instances.MonsterInstance(id, template);
            case "NoActionNpcInstance":
                return (T) new l2trunk.gameserver.model.instances.NoActionNpcInstance(id, template);
            case "NpcFriendInstance":
                return (T) new l2trunk.gameserver.model.instances.NpcFriendInstance(id, template);
            case "NpcNotSayInstance":
                return (T) new l2trunk.gameserver.model.instances.NpcNotSayInstance(id, template);
            case "ObservationInstance":
                return (T) new l2trunk.gameserver.model.instances.ObservationInstance(id, template);
            case "OlympiadBufferInstance":
                return (T) new l2trunk.gameserver.model.instances.OlympiadBufferInstance(id, template);
            case "OlympiadManagerInstance":
                return (T) new l2trunk.gameserver.model.instances.OlympiadManagerInstance(id, template);
            case "RaidBossInstance":
                return (T) new l2trunk.gameserver.model.instances.RaidBossInstance(id, template);
            case "ReflectionBossInstance":
                return (T) new l2trunk.gameserver.model.instances.ReflectionBossInstance(id, template);
            case "SchemeBufferInstance":
                return (T) new l2trunk.gameserver.model.instances.SchemeBufferInstance(id, template);
            case "SiegeInformerInstance":
                return (T) new l2trunk.gameserver.model.instances.SiegeInformerInstance(id, template);
            case "SignsPriestInstance":
                return (T) new l2trunk.gameserver.model.instances.SignsPriestInstance(id, template);
            case "SpecialMonsterInstance":
                return (T) new l2trunk.gameserver.model.instances.SpecialMonsterInstance(id, template);
//            case "StaticObjectInstance":
//                return (T) new l2trunk.gameserver.model.instances.StaticObjectInstance(id, template);
//            case "SummonInstance":
//                return (T) new l2trunk.gameserver.model.instances.SummonInstance(id, template);
//            case "SymbolInstance":
//                return (T) new l2trunk.gameserver.model.instances.SymbolInstance(id, template);
            case "SymbolMakerInstance":
                return (T) new l2trunk.gameserver.model.instances.SymbolMakerInstance(id, template);
            case "TamedBeastInstance":
                return (T) new l2trunk.gameserver.model.instances.TamedBeastInstance(id, template);
//            case "TerritoryWardInstance":
//                return (T) new l2trunk.gameserver.model.instances.TerritoryWardInstance(id, template);
            case "TrainerInstance":
                return (T) new l2trunk.gameserver.model.instances.TrainerInstance(id, template);
//            case "TrapInstance":
//                return (T) new l2trunk.gameserver.model.instances.TrapInstance(id, template);
            case "VillageMasterInstance":
                return (T) new l2trunk.gameserver.model.instances.VillageMasterInstance(id, template);
            case "WarehouseInstance":
                return (T) new l2trunk.gameserver.model.instances.WarehouseInstance(id, template);
            case "WeaverInstance":
                return (T) new l2trunk.gameserver.model.instances.WeaverInstance(id, template);
            case "WyvernManagerInstance":
                return (T) new l2trunk.gameserver.model.instances.WyvernManagerInstance(id, template);
            case "XmassTreeInstance":
                return (T) new l2trunk.gameserver.model.instances.XmassTreeInstance(id, template);
            case "AbyssGazeInstance":
                return (T) new l2trunk.scripts.npc.model.AbyssGazeInstance(id, template);
            case "AllenosInstance":
                return (T) new l2trunk.scripts.npc.model.AllenosInstance(id, template);
            case "ArenaManagerInstance":
                return (T) new l2trunk.scripts.npc.model.ArenaManagerInstance(id, template);
            case "AsamahInstance":
                return (T) new l2trunk.scripts.npc.model.AsamahInstance(id, template);
            case "BaiumGatekeeperInstance":
                return (T) new l2trunk.scripts.npc.model.BaiumGatekeeperInstance(id, template);
            case "BatracosInstance":
                return (T) new l2trunk.scripts.npc.model.BatracosInstance(id, template);
            case "BelethCoffinInstance":
                return (T) new l2trunk.scripts.npc.model.BelethCoffinInstance(id, template);
            case "birthday.AlegriaInstance":
                return (T) new l2trunk.scripts.npc.model.birthday.AlegriaInstance(id, template);
            case "birthday.BirthDayCakeInstance":
                return (T) new l2trunk.scripts.npc.model.birthday.BirthDayCakeInstance(id, template);
            case "BlackJudeInstance":
                return (T) new l2trunk.scripts.npc.model.BlackJudeInstance(id, template);
            case "BorderOutpostDoormanInstance":
                return (T) new l2trunk.scripts.npc.model.BorderOutpostDoormanInstance(id, template);
            case "CabaleBufferInstance":
                return (T) new l2trunk.scripts.npc.model.CabaleBufferInstance(id, template);
            case "CannibalisticStakatoChiefInstance":
                return (T) new l2trunk.scripts.npc.model.CannibalisticStakatoChiefInstance(id, template);
            case "CaravanTraderInstance":
                return (T) new l2trunk.scripts.npc.model.CaravanTraderInstance(id, template);
            case "ClassMasterInstance":
                return (T) new l2trunk.scripts.npc.model.ClassMasterInstance(id, template);
            case "CoralGardenGateInstance":
                return (T) new l2trunk.scripts.npc.model.CoralGardenGateInstance(id, template);
            case "CrystalCavernControllerInstance":
                return (T) new l2trunk.scripts.npc.model.CrystalCavernControllerInstance(id, template);
            case "DeadTumorInstance":
                return (T) new l2trunk.scripts.npc.model.DeadTumorInstance(id, template);
            case "DelustionGatekeeperInstance":
                return (T) new l2trunk.scripts.npc.model.DelustionGatekeeperInstance(id, template);
            case "DragonVortexInstance":
                return (T) new l2trunk.scripts.npc.model.DragonVortexInstance(id, template);
            case "EkimusMouthInstance":
                return (T) new l2trunk.scripts.npc.model.EkimusMouthInstance(id, template);
            case "ElcardiaAssistantInstance":
                return (T) new l2trunk.scripts.npc.model.ElcardiaAssistantInstance(id, template);
            case "EmeraldSquareTrapInstance":
                return (T) new l2trunk.scripts.npc.model.EmeraldSquareTrapInstance(id, template);
            case "EnergySeedInstance":
                return (T) new l2trunk.scripts.npc.model.EnergySeedInstance(id, template);
            case "events.CleftVortexGateInstance":
                return (T) new l2trunk.scripts.npc.model.events.CleftVortexGateInstance(id, template);
            case "events.ColiseumHelperInstance":
                return (T) new l2trunk.scripts.npc.model.events.ColiseumHelperInstance(id, template);
            case "events.ColiseumManagerInstance":
                return (T) new l2trunk.scripts.npc.model.events.ColiseumManagerInstance(id, template);
            case "events.FurnfaceInstance":
                return (T) new l2trunk.scripts.npc.model.events.FurnfaceInstance(id, template);
            case "events.HitmanInstance":
                return (T) new l2trunk.scripts.npc.model.events.HitmanInstance(id, template);
            case "events.KrateisCubeManagerInstance":
                return (T) new l2trunk.scripts.npc.model.events.KrateisCubeManagerInstance(id, template);
            case "events.KrateisCubeMatchManagerInstance":
                return (T) new l2trunk.scripts.npc.model.events.KrateisCubeMatchManagerInstance(id, template);
            case "events.SumielInstance":
                return (T) new l2trunk.scripts.npc.model.events.SumielInstance(id, template);
//            case "events.UndergroundColiseumInstance":
//                return (T) new l2trunk.scripts.npc.model.events.UndergroundColiseumInstance(id, template);
            case "FakeObeliskInstance":
                return (T) new l2trunk.scripts.npc.model.FakeObeliskInstance(id, template);
            case "FreightSenderInstance":
                return (T) new l2trunk.scripts.npc.model.FreightSenderInstance(id, template);
            case "FrintezzaGatekeeperInstance":
                return (T) new l2trunk.scripts.npc.model.FrintezzaGatekeeperInstance(id, template);
            case "FrintezzaInstance":
                return (T) new l2trunk.scripts.npc.model.FrintezzaInstance(id, template);
            case "GruffManInstance":
                return (T) new l2trunk.scripts.npc.model.GruffManInstance(id, template);
            case "GuradsOfDawnInstance":
                return (T) new l2trunk.scripts.npc.model.GuradsOfDawnInstance(id, template);
            case "GvGBossInstance":
                return (T) new l2trunk.scripts.npc.model.GvGBossInstance(id, template);
            case "HandysBlockCheckerInstance":
                return (T) new l2trunk.scripts.npc.model.HandysBlockCheckerInstance(id, template);
            case "HeartOfWardingInstance":
                return (T) new l2trunk.scripts.npc.model.HeartOfWardingInstance(id, template);
            case "HellboundRemnantInstance":
                return (T) new l2trunk.scripts.npc.model.HellboundRemnantInstance(id, template);
            case "ImmuneMonsterInstance":
                return (T) new l2trunk.scripts.npc.model.ImmuneMonsterInstance(id, template);
            case "JiniaNpcInstance":
                return (T) new l2trunk.scripts.npc.model.JiniaNpcInstance(id, template);
            case "Kama26BossInstance":
                return (T) new l2trunk.scripts.npc.model.Kama26BossInstance(id, template);
            case "KamalokaBossInstance":
                return (T) new l2trunk.scripts.npc.model.KamalokaBossInstance(id, template);
            case "KamalokaGuardInstance":
                return (T) new l2trunk.scripts.npc.model.KamalokaGuardInstance(id, template);
            case "KegorNpcInstance":
                return (T) new l2trunk.scripts.npc.model.KegorNpcInstance(id, template);
            case "KeplonInstance":
                return (T) new l2trunk.scripts.npc.model.KeplonInstance(id, template);
            case "LekonInstance":
                return (T) new l2trunk.scripts.npc.model.LekonInstance(id, template);
            case "LostCaptainInstance":
                return (T) new l2trunk.scripts.npc.model.LostCaptainInstance(id, template);
            case "MaguenInstance":
                return (T) new l2trunk.scripts.npc.model.MaguenInstance(id, template);
            case "MaguenTraderInstance":
                return (T) new l2trunk.scripts.npc.model.MaguenTraderInstance(id, template);
            case "MeleonInstance":
                return (T) new l2trunk.scripts.npc.model.MeleonInstance(id, template);
            case "MobInvulInstance":
                return (T) new l2trunk.scripts.npc.model.MobInvulInstance(id, template);
            case "MoonlightTombstoneInstance":
                return (T) new l2trunk.scripts.npc.model.MoonlightTombstoneInstance(id, template);
            case "MushroomInstance":
                return (T) new l2trunk.scripts.npc.model.MushroomInstance(id, template);
            case "NaiaControllerInstance":
                return (T) new l2trunk.scripts.npc.model.NaiaControllerInstance(id, template);
            case "NaiaRoomControllerInstance":
                return (T) new l2trunk.scripts.npc.model.NaiaRoomControllerInstance(id, template);
            case "NativeCorpseInstance":
                return (T) new l2trunk.scripts.npc.model.NativeCorpseInstance(id, template);
            case "NativePrisonerInstance":
                return (T) new l2trunk.scripts.npc.model.NativePrisonerInstance(id, template);
            case "NevitHeraldInstance":
                return (T) new l2trunk.scripts.npc.model.NevitHeraldInstance(id, template);
            case "NewbieGuideInstance":
                return (T) new l2trunk.scripts.npc.model.NewbieGuideInstance(id, template);
            case "NihilInvaderChestInstance":
                return (T) new l2trunk.scripts.npc.model.NihilInvaderChestInstance(id, template);
            case "OddGlobeInstance":
                return (T) new l2trunk.scripts.npc.model.OddGlobeInstance(id, template);
            case "OrfenInstance":
                return (T) new l2trunk.scripts.npc.model.OrfenInstance(id, template);
            case "PailakaGatekeeperInstance":
                return (T) new l2trunk.scripts.npc.model.PailakaGatekeeperInstance(id, template);
            case "PassagewayMobWithHerbInstance":
                return (T) new l2trunk.scripts.npc.model.PassagewayMobWithHerbInstance(id, template);
            case "PathfinderInstance":
                return (T) new l2trunk.scripts.npc.model.PathfinderInstance(id, template);
            case "PriestAquilaniInstance":
                return (T) new l2trunk.scripts.npc.model.PriestAquilaniInstance(id, template);
            case "PriestOfBlessingInstance":
                return (T) new l2trunk.scripts.npc.model.PriestOfBlessingInstance(id, template);
            case "QuarrySlaveInstance":
                return (T) new l2trunk.scripts.npc.model.QuarrySlaveInstance(id, template);
            case "QueenAntInstance":
                return (T) new l2trunk.scripts.npc.model.QueenAntInstance(id, template);
            case "QueenAntLarvaInstance":
                return (T) new l2trunk.scripts.npc.model.QueenAntLarvaInstance(id, template);
            case "RafortyInstance":
                return (T) new l2trunk.scripts.npc.model.RafortyInstance(id, template);


            case "residences.SiegeFlagInstance":
                return (T) new l2trunk.gameserver.model.instances.residences.SiegeFlagInstance(id, template);
            case "residences.dominion.OutpostInstance":
                return (T) new l2trunk.gameserver.model.instances.residences.dominion.OutpostInstance(id, template);

            case "residences.castle.BlacksmithInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.BlacksmithInstance(id, template);
            case "residences.castle.CastleControlTowerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.CastleControlTowerInstance(id, template);
            case "residences.castle.CastleFakeTowerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.CastleFakeTowerInstance(id, template);
            case "residences.castle.CastleFlameTowerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.CastleFlameTowerInstance(id, template);
            case "residences.castle.CastleMassTeleporterInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.CastleMassTeleporterInstance(id, template);
            case "residences.castle.CastleMessengerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.CastleMessengerInstance(id, template);
            case "residences.castle.ChamberlainInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.ChamberlainInstance(id, template);
            case "residences.castle.CourtInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.CourtInstance(id, template);
            case "residences.castle.DoormanInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.DoormanInstance(id, template);
            case "residences.castle.MercenaryManagerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.MercenaryManagerInstance(id, template);
            case "residences.castle.VenomTeleportCubicInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.VenomTeleportCubicInstance(id, template);
            case "residences.castle.VenomTeleporterInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.VenomTeleporterInstance(id, template);
            case "residences.castle.WarehouseInstance":
                return (T) new l2trunk.scripts.npc.model.residences.castle.WarehouseInstance(id, template);
            case "residences.clanhall.AuctionedDoormanInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.AuctionedDoormanInstance(id, template);
            case "residences.clanhall.AuctionedManagerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.AuctionedManagerInstance(id, template);
            case "residences.clanhall.AuctioneerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.AuctioneerInstance(id, template);
            case "residences.clanhall.BanditMessagerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.BanditMessagerInstance(id, template);
            case "residences.clanhall.BrakelInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.BrakelInstance(id, template);
            case "residences.clanhall.DietrichInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.DietrichInstance(id, template);
            case "residences.clanhall.DoormanInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.DoormanInstance(id, template);
            case "residences.clanhall.FarmMessengerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.FarmMessengerInstance(id, template);
            case "residences.clanhall.GustavInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.GustavInstance(id, template);
            case "residences.clanhall.LidiaVonHellmannInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.LidiaVonHellmannInstance(id, template);
            case "residences.clanhall.ManagerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.ManagerInstance(id, template);
            case "residences.clanhall.MatchBerserkerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.MatchBerserkerInstance(id, template);
            case "residences.clanhall.MatchClericInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.MatchClericInstance(id, template);
            case "residences.clanhall.MatchLeaderInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.MatchLeaderInstance(id, template);
            case "residences.clanhall.MatchMassTeleporterInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.MatchMassTeleporterInstance(id, template);
            case "residences.clanhall.MatchScoutInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.MatchScoutInstance(id, template);
            case "residences.clanhall.MatchTriefInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.MatchTriefInstance(id, template);
            case "residences.clanhall.MessengerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.MessengerInstance(id, template);
            case "residences.clanhall.MikhailInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.MikhailInstance(id, template);
            case "residences.clanhall.NurkaInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.NurkaInstance(id, template);
            case "residences.clanhall.RainbowChestInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.RainbowChestInstance(id, template);
            case "residences.clanhall.RainbowCoordinatorInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.RainbowCoordinatorInstance(id, template);
            case "residences.clanhall.RainbowGourdInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.RainbowGourdInstance(id, template);
            case "residences.clanhall.RainbowMessengerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.RainbowMessengerInstance(id, template);
            case "residences.clanhall.RainbowYetiInstance":
                return (T) new l2trunk.scripts.npc.model.residences.clanhall.RainbowYetiInstance(id, template);
//            case "residences.clanhall._34BossMinionInstance":
//                return (T) new l2trunk.scripts.npc.model.residences.clanhall._34BossMinionInstance(id, template);
            case "residences.dominion.CatapultInstance":
                return (T) new l2trunk.scripts.npc.model.residences.dominion.CatapultInstance(id, template);
            case "residences.dominion.MercenaryCaptainInstance":
                return (T) new l2trunk.scripts.npc.model.residences.dominion.MercenaryCaptainInstance(id, template);
            case "residences.dominion.TerritoryManagerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.dominion.TerritoryManagerInstance(id, template);
//            case "residences.DoormanInstance":
//                return (T) new l2trunk.scripts.npc.model.residences.DoormanInstance(id, template);
            case "residences.fortress.DoormanInstance":
                return (T) new l2trunk.scripts.npc.model.residences.fortress.DoormanInstance(id, template);
            case "residences.fortress.EnvoyInstance":
                return (T) new l2trunk.scripts.npc.model.residences.fortress.EnvoyInstance(id, template);
//            case "residences.fortress.FacilityManagerInstance":
//                return (T) new l2trunk.scripts.npc.model.residences.fortress.FacilityManagerInstance(id, template);
            case "residences.fortress.LogisticsOfficerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.fortress.LogisticsOfficerInstance(id, template);
            case "residences.fortress.ManagerInstance":
                return (T) new l2trunk.scripts.npc.model.residences.fortress.ManagerInstance(id, template);
            case "residences.fortress.peace.ArcherCaptionInstance":
                return (T) new l2trunk.scripts.npc.model.residences.fortress.peace.ArcherCaptionInstance(id, template);
            case "residences.fortress.peace.GuardCaptionInstance":
                return (T) new l2trunk.scripts.npc.model.residences.fortress.peace.GuardCaptionInstance(id, template);
            case "residences.fortress.peace.SupportUnitCaptionInstance":
                return (T) new SupportUnitCaptionInstance(id, template);
            case "residences.fortress.peace.SuspiciousMerchantInstance":
                return (T) new SuspiciousMerchantInstance(id, template);
            case "residences.fortress.siege.BackupPowerUnitInstance":
                return (T) new BackupPowerUnitInstance(id, template);
            case "residences.fortress.siege.BallistaInstance":
                return (T) new BallistaInstance(id, template);
            case "residences.fortress.siege.ControlUnitInstance":
                return (T) new ControlUnitInstance(id, template);
            case "residences.fortress.siege.MainMachineInstance":
                return (T) new l2trunk.scripts.npc.model.residences.fortress.siege.MainMachineInstance(id, template);
            case "residences.fortress.siege.MercenaryCaptionInstance":
                return (T) new l2trunk.scripts.npc.model.residences.fortress.siege.MercenaryCaptionInstance(id, template);
            case "residences.fortress.siege.PowerControlUnitInstance":
                return (T) new l2trunk.scripts.npc.model.residences.fortress.siege.PowerControlUnitInstance(id, template);
            case "residences.QuestSiegeGuardInstance":
                return (T) new l2trunk.scripts.npc.model.residences.QuestSiegeGuardInstance(id, template);
            case "residences.SiegeGuardInstance":
                return (T) new l2trunk.scripts.npc.model.residences.SiegeGuardInstance(id, template);
            case "residences.TeleportSiegeGuardInstance":
                return (T) new l2trunk.scripts.npc.model.residences.TeleportSiegeGuardInstance(id, template);
            case "RiganInstance":
                return (T) new l2trunk.scripts.npc.model.RiganInstance(id, template);
            case "RignosInstance":
                return (T) new l2trunk.scripts.npc.model.RignosInstance(id, template);
            case "SairlenGatekeeperInstance":
                return (T) new l2trunk.scripts.npc.model.SairlenGatekeeperInstance(id, template);
            case "SallyInstance":
                return (T) new l2trunk.scripts.npc.model.SallyInstance(id, template);
            case "SandstormInstance":
                return (T) new l2trunk.scripts.npc.model.SandstormInstance(id, template);
            case "SealDeviceInstance":
                return (T) new l2trunk.scripts.npc.model.SealDeviceInstance(id, template);
            case "SeducedInvestigatorInstance":
                return (T) new l2trunk.scripts.npc.model.SeducedInvestigatorInstance(id, template);
            case "SeedOfAnnihilationInstance":
                return (T) new l2trunk.scripts.npc.model.SeedOfAnnihilationInstance(id, template);
            case "SepulcherMonsterInstance":
                return (T) new l2trunk.scripts.npc.model.SepulcherMonsterInstance(id, template);
            case "SepulcherNpcInstance":
                return (T) new l2trunk.scripts.npc.model.SepulcherNpcInstance(id, template);
            case "SepulcherRaidInstance":
                return (T) new l2trunk.scripts.npc.model.SepulcherRaidInstance(id, template);
            case "SirraInstance":
                return (T) new l2trunk.scripts.npc.model.SirraInstance(id, template);
            case "SnowmanInstance":
                return (T) new l2trunk.scripts.npc.model.SnowmanInstance(id, template);
            case "SpecialMinionInstance":
                return (T) new l2trunk.scripts.npc.model.SpecialMinionInstance(id, template);
            case "SquashInstance":
                return (T) new l2trunk.scripts.npc.model.SquashInstance(id, template);
            case "StarStoneInstance":
                return (T) new l2trunk.scripts.npc.model.StarStoneInstance(id, template);
            case "SteamCorridorControllerInstance":
                return (T) new l2trunk.scripts.npc.model.SteamCorridorControllerInstance(id, template);
            case "SteelCitadelTeleporterInstance":
                return (T) new l2trunk.scripts.npc.model.SteelCitadelTeleporterInstance(id, template);
            case "TepiosRewardInstance":
                return (T) new l2trunk.scripts.npc.model.TepiosRewardInstance(id, template);
            case "ThomasInstance":
                return (T) new l2trunk.scripts.npc.model.ThomasInstance(id, template);
            case "TreasureChestInstance":
                return (T) new l2trunk.scripts.npc.model.TreasureChestInstance(id, template);
            case "TriolsMirrorInstance":
                return (T) new l2trunk.scripts.npc.model.TriolsMirrorInstance(id, template);
            case "TullyWorkShopTeleporterInstance":
                return (T) new l2trunk.scripts.npc.model.TullyWorkShopTeleporterInstance(id, template);
            case "ValakasGatekeeperInstance":
                return (T) new l2trunk.scripts.npc.model.ValakasGatekeeperInstance(id, template);
            case "WarpgateInstance":
                return (T) new l2trunk.scripts.npc.model.WarpgateInstance(id, template);
            case "WorkshopGatekeeperInstance":
                return (T) new l2trunk.scripts.npc.model.WorkshopGatekeeperInstance(id, template);
            case "WorkshopServantInstance":
                return (T) new l2trunk.scripts.npc.model.WorkshopServantInstance(id, template);
            case "YehanBrotherInstance":
                return (T) new l2trunk.scripts.npc.model.YehanBrotherInstance(id, template);
            case "ZakenCandleInstance":
                return (T) new l2trunk.scripts.npc.model.ZakenCandleInstance(id, template);
            case "ZakenGatekeeperInstance":
                return (T) new l2trunk.scripts.npc.model.ZakenGatekeeperInstance(id, template);

//            case "PetInstance":
//                return null;
            default:
                throw new IllegalArgumentException("no template for name: " + type);
        }

    }
}
