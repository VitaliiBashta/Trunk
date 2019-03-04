package l2trunk.gameserver.model.instances;

import l2trunk.gameserver.data.xml.holder.NpcHolder;
import l2trunk.gameserver.templates.npc.NpcTemplate;

import java.util.List;
import java.util.stream.Collectors;

public final class AllNpcInstances {
    private AllNpcInstances() {
    }

    public static NpcInstance getInstance(int id, String type, String name) {
        NpcTemplate template;

        List<NpcTemplate> list = NpcHolder.getTemplateByName(name).collect(Collectors.toList());
        if (list.size() == 1)
            template = list.get(0);
        else
            template = NpcHolder.getTemplateByType(type);
        if (template == null)
            throw new IllegalArgumentException("no template found for id=" + id + " type=" + type);
        switch (type + "Instance") {
            case "NpcInstance":
                return new NpcInstance(id, template);
            case "AdventurerInstance":
                return new AdventurerInstance(id, template);
            case "AirShipControllerInstance":
                return new l2trunk.gameserver.model.instances.AirShipControllerInstance(id, template);
            case "ArtefactInstance":
                return new l2trunk.gameserver.model.instances.ArtefactInstance(id, template);
            case "BetaNPCInstance":
                return new l2trunk.gameserver.model.instances.BetaNPCInstance(id, template);
            case "BlockInstance":
                return new l2trunk.gameserver.model.instances.BlockInstance(id, template);
            case "BossInstance":
                return new l2trunk.gameserver.model.instances.BossInstance(id, template);
            case "ChestInstance":
                return new l2trunk.gameserver.model.instances.ChestInstance(id, template);
            case "ClanAirShipControllerInstance":
                return new l2trunk.gameserver.model.instances.ClanAirShipControllerInstance(id, template);
            case "ClanRewardInstance":
                return new l2trunk.gameserver.model.instances.ClanRewardInstance(id, template);
            case "ClanTraderInstance":
                return new l2trunk.gameserver.model.instances.ClanTraderInstance(id, template);
//            case "ControlKeyInstance":
//                return  new l2trunk.gameserver.model.instances.ControlKeyInstance();
            case "DeadManInstance":
                return new l2trunk.gameserver.model.instances.DeadManInstance(id, template);
//            case "DecoyInstance":
//                return  new l2trunk.gameserver.model.instances.DecoyInstance(id, template);
            case "DonateNPCInstance":
                return new l2trunk.gameserver.model.instances.DonateNPCInstance(id, template);
//            case "DoorInstance":
//                return  new l2trunk.gameserver.model.instances.DoorInstance(id, template);
            case "FameManagerInstance":
                return new l2trunk.gameserver.model.instances.FameManagerInstance(id, template);
            case "FeedableBeastInstance":
                return new l2trunk.gameserver.model.instances.FeedableBeastInstance(id, template);
            case "FestivalGuideInstance":
                return new l2trunk.gameserver.model.instances.FestivalGuideInstance(id, template);
            case "FestivalMonsterInstance":
                return new l2trunk.gameserver.model.instances.FestivalMonsterInstance(id, template);
            case "FishermanInstance":
                return new l2trunk.gameserver.model.instances.FishermanInstance(id, template);
            case "FurnaceInstance":
                return new l2trunk.gameserver.model.instances.FurnaceInstance(id, template);
            case "GuardInstance":
                return new l2trunk.gameserver.model.instances.GuardInstance(id, template);
            case "ItemAuctionBrokerInstance":
                return new l2trunk.gameserver.model.instances.ItemAuctionBrokerInstance(id, template);
            case "LotteryManagerInstance":
                return new l2trunk.gameserver.model.instances.LotteryManagerInstance(id, template);
            case "ManorManagerInstance":
                return new l2trunk.gameserver.model.instances.ManorManagerInstance(id, template);
            case "MerchantInstance":
                return new l2trunk.gameserver.model.instances.MerchantInstance(id, template);
            case "MercManagerInstance":
                return new l2trunk.gameserver.model.instances.MercManagerInstance(id, template);
            case "MinionInstance":
                return new l2trunk.gameserver.model.instances.MinionInstance(id, template);
            case "MonsterInstance":
                return new l2trunk.gameserver.model.instances.MonsterInstance(id, template);
            case "NpcFriendInstance":
                return new l2trunk.gameserver.model.instances.NpcFriendInstance(id, template);
            case "NpcNotSayInstance":
                return new l2trunk.gameserver.model.instances.NpcNotSayInstance(id, template);
            case "ObservationInstance":
                return new l2trunk.gameserver.model.instances.ObservationInstance(id, template);
            case "OlympiadBufferInstance":
                return new l2trunk.gameserver.model.instances.OlympiadBufferInstance(id, template);
            case "OlympiadManagerInstance":
                return new l2trunk.gameserver.model.instances.OlympiadManagerInstance(id, template);
            case "RaidBossInstance":
                return new l2trunk.gameserver.model.instances.RaidBossInstance(id, template);
            case "ReflectionBossInstance":
                return new l2trunk.gameserver.model.instances.ReflectionBossInstance(id, template);
            case "SchemeBufferInstance":
                return new l2trunk.gameserver.model.instances.SchemeBufferInstance(id, template);
            case "SiegeInformerInstance":
                return new l2trunk.gameserver.model.instances.SiegeInformerInstance(id, template);
            case "SignsPriestInstance":
                return new l2trunk.gameserver.model.instances.SignsPriestInstance(id, template);
            case "SpecialMonsterInstance":
                return new l2trunk.gameserver.model.instances.SpecialMonsterInstance(id, template);
//            case "StaticObjectInstance":
//                return  new l2trunk.gameserver.model.instances.StaticObjectInstance(id, template);
//            case "SummonInstance":
//                return  new l2trunk.gameserver.model.instances.SummonInstance(id, template);
//            case "SymbolInstance":
//                return  new l2trunk.gameserver.model.instances.SymbolInstance(id, template);
            case "SymbolMakerInstance":
                return new l2trunk.gameserver.model.instances.SymbolMakerInstance(id, template);
            case "TamedBeastInstance":
                return new l2trunk.gameserver.model.instances.TamedBeastInstance(id, template);
//            case "TerritoryWardInstance":
//                return  new l2trunk.gameserver.model.instances.TerritoryWardInstance(id, template);
            case "TrainerInstance":
                return new l2trunk.gameserver.model.instances.TrainerInstance(id, template);
//            case "TrapInstance":
//                return  new l2trunk.gameserver.model.instances.TrapInstance(id, template);
            case "VillageMasterInstance":
                return new l2trunk.gameserver.model.instances.VillageMasterInstance(id, template);
            case "WarehouseInstance":
                return new l2trunk.gameserver.model.instances.WarehouseInstance(id, template);
            case "WeaverInstance":
                return new l2trunk.gameserver.model.instances.WeaverInstance(id, template);
            case "WyvernManagerInstance":
                return new l2trunk.gameserver.model.instances.WyvernManagerInstance(id, template);
            case "XmassTreeInstance":
                return new l2trunk.gameserver.model.instances.XmassTreeInstance(id, template);
            case "AbyssGazeInstance":
                return new l2trunk.scripts.npc.model.AbyssGazeInstance(id, template);
            case "AllenosInstance":
                return new l2trunk.scripts.npc.model.AllenosInstance(id, template);
            case "ArenaManagerInstance":
                return new l2trunk.scripts.npc.model.ArenaManagerInstance(id, template);
            case "AsamahInstance":
                return new l2trunk.scripts.npc.model.AsamahInstance(id, template);
            case "BaiumGatekeeperInstance":
                return new l2trunk.scripts.npc.model.BaiumGatekeeperInstance(id, template);
            case "BatracosInstance":
                return new l2trunk.scripts.npc.model.BatracosInstance(id, template);
            case "BelethCoffinInstance":
                return new l2trunk.scripts.npc.model.BelethCoffinInstance(id, template);
            case "birthday.AlegriaInstance":
                return new l2trunk.scripts.npc.model.birthday.AlegriaInstance(id, template);
            case "birthday.BirthDayCakeInstance":
                return new l2trunk.scripts.npc.model.birthday.BirthDayCakeInstance(id, template);
            case "BlackJudeInstance":
                return new l2trunk.scripts.npc.model.BlackJudeInstance(id, template);
            case "BorderOutpostDoormanInstance":
                return new l2trunk.scripts.npc.model.BorderOutpostDoormanInstance(id, template);
            case "CabaleBufferInstance":
                return new l2trunk.scripts.npc.model.CabaleBufferInstance(id, template);
            case "CannibalisticStakatoChiefInstance":
                return new l2trunk.scripts.npc.model.CannibalisticStakatoChiefInstance(id, template);
            case "CaravanTraderInstance":
                return new l2trunk.scripts.npc.model.CaravanTraderInstance(id, template);
            case "ClassMasterInstance":
                return new l2trunk.scripts.npc.model.ClassMasterInstance(id, template);
            case "CoralGardenGateInstance":
                return new l2trunk.scripts.npc.model.CoralGardenGateInstance(id, template);
            case "CrystalCavernControllerInstance":
                return new l2trunk.scripts.npc.model.CrystalCavernControllerInstance(id, template);
            case "DeadTumorInstance":
                return new l2trunk.scripts.npc.model.DeadTumorInstance(id, template);
            case "DelustionGatekeeperInstance":
                return new l2trunk.scripts.npc.model.DelustionGatekeeperInstance(id, template);
            case "DragonVortexInstance":
                return new l2trunk.scripts.npc.model.DragonVortexInstance(id, template);
            case "EkimusMouthInstance":
                return new l2trunk.scripts.npc.model.EkimusMouthInstance(id, template);
            case "ElcardiaAssistantInstance":
                return new l2trunk.scripts.npc.model.ElcardiaAssistantInstance(id, template);
            case "EmeraldSquareTrapInstance":
                return new l2trunk.scripts.npc.model.EmeraldSquareTrapInstance(id, template);
            case "EnergySeedInstance":
                return new l2trunk.scripts.npc.model.EnergySeedInstance(id, template);
            case "events.CleftVortexGateInstance":
                return new l2trunk.scripts.npc.model.events.CleftVortexGateInstance(id, template);
            case "events.ColiseumHelperInstance":
                return new l2trunk.scripts.npc.model.events.ColiseumHelperInstance(id, template);
            case "events.ColiseumManagerInstance":
                return new l2trunk.scripts.npc.model.events.ColiseumManagerInstance(id, template);
            case "events.FurnfaceInstance":
                return new l2trunk.scripts.npc.model.events.FurnfaceInstance(id, template);
            case "events.HitmanInstance":
                return new l2trunk.scripts.npc.model.events.HitmanInstance(id, template);
            case "events.KrateisCubeManagerInstance":
                return new l2trunk.scripts.npc.model.events.KrateisCubeManagerInstance(id, template);
            case "events.KrateisCubeMatchManagerInstance":
                return new l2trunk.scripts.npc.model.events.KrateisCubeMatchManagerInstance(id, template);
            case "events.SumielInstance":
                return new l2trunk.scripts.npc.model.events.SumielInstance(id, template);
            case "events.UndergroundColiseumInstance":
                return  new l2trunk.scripts.npc.model.events.UndergroundColiseumInstance(id, template);
            case "FakeObeliskInstance":
                return new l2trunk.scripts.npc.model.FakeObeliskInstance(id, template);
            case "FreightSenderInstance":
                return new l2trunk.scripts.npc.model.FreightSenderInstance(id, template);
            case "FrintezzaGatekeeperInstance":
                return new l2trunk.scripts.npc.model.FrintezzaGatekeeperInstance(id, template);
            case "FrintezzaInstance":
                return new l2trunk.scripts.npc.model.FrintezzaInstance(id, template);
            case "GruffManInstance":
                return new l2trunk.scripts.npc.model.GruffManInstance(id, template);
            case "GuradsOfDawnInstance":
                return new l2trunk.scripts.npc.model.GuradsOfDawnInstance(id, template);
            case "GvGBossInstance":
                return new l2trunk.scripts.npc.model.GvGBossInstance(id, template);
            case "HandysBlockCheckerInstance":
                return new l2trunk.scripts.npc.model.HandysBlockCheckerInstance(id, template);
            case "HeartOfWardingInstance":
                return new l2trunk.scripts.npc.model.HeartOfWardingInstance(id, template);
            case "HellboundRemnantInstance":
                return new l2trunk.scripts.npc.model.HellboundRemnantInstance(id, template);
            case "ImmuneMonsterInstance":
                return new l2trunk.scripts.npc.model.ImmuneMonsterInstance(id, template);
            case "JiniaNpcInstance":
                return new l2trunk.scripts.npc.model.JiniaNpcInstance(id, template);
            case "Kama26BossInstance":
                return new l2trunk.scripts.npc.model.Kama26BossInstance(id, template);
            case "KamalokaBossInstance":
                return new l2trunk.scripts.npc.model.KamalokaBossInstance(id, template);
            case "KamalokaGuardInstance":
                return new l2trunk.scripts.npc.model.KamalokaGuardInstance(id, template);
            case "KegorNpcInstance":
                return new l2trunk.scripts.npc.model.KegorNpcInstance(id, template);
            case "KeplonInstance":
                return new l2trunk.scripts.npc.model.KeplonInstance(id, template);
            case "LekonInstance":
                return new l2trunk.scripts.npc.model.LekonInstance(id, template);
            case "LostCaptainInstance":
                return new l2trunk.scripts.npc.model.LostCaptainInstance(id, template);
            case "MaguenInstance":
                return new l2trunk.scripts.npc.model.MaguenInstance(id, template);
            case "MaguenTraderInstance":
                return new l2trunk.scripts.npc.model.MaguenTraderInstance(id, template);
            case "MeleonInstance":
                return new l2trunk.scripts.npc.model.MeleonInstance(id, template);
            case "MobInvulInstance":
                return new l2trunk.scripts.npc.model.MobInvulInstance(id, template);
            case "MoonlightTombstoneInstance":
                return new l2trunk.scripts.npc.model.MoonlightTombstoneInstance(id, template);
            case "MushroomInstance":
                return new l2trunk.scripts.npc.model.MushroomInstance(id, template);
            case "NaiaControllerInstance":
                return new l2trunk.scripts.npc.model.NaiaControllerInstance(id, template);
            case "NaiaRoomControllerInstance":
                return new l2trunk.scripts.npc.model.NaiaRoomControllerInstance(id, template);
            case "NativeCorpseInstance":
                return new l2trunk.scripts.npc.model.NativeCorpseInstance(id, template);
            case "NativePrisonerInstance":
                return new l2trunk.scripts.npc.model.NativePrisonerInstance(id, template);
            case "NevitHeraldInstance":
                return new l2trunk.scripts.npc.model.NevitHeraldInstance(id, template);
            case "NewbieGuideInstance":
                return new l2trunk.scripts.npc.model.NewbieGuideInstance(id, template);
            case "NihilInvaderChestInstance":
                return new l2trunk.scripts.npc.model.NihilInvaderChestInstance(id, template);
            case "OddGlobeInstance":
                return new l2trunk.scripts.npc.model.OddGlobeInstance(id, template);
            case "OrfenInstance":
                return new l2trunk.scripts.npc.model.OrfenInstance(id, template);
            case "PailakaGatekeeperInstance":
                return new l2trunk.scripts.npc.model.PailakaGatekeeperInstance(id, template);
            case "PassagewayMobWithHerbInstance":
                return new l2trunk.scripts.npc.model.PassagewayMobWithHerbInstance(id, template);
            case "PathfinderInstance":
                return new l2trunk.scripts.npc.model.PathfinderInstance(id, template);
            case "PriestAquilaniInstance":
                return new l2trunk.scripts.npc.model.PriestAquilaniInstance(id, template);
            case "PriestOfBlessingInstance":
                return new l2trunk.scripts.npc.model.PriestOfBlessingInstance(id, template);
            case "QuarrySlaveInstance":
                return new l2trunk.scripts.npc.model.QuarrySlaveInstance(id, template);
            case "QueenAntInstance":
                return new l2trunk.scripts.npc.model.QueenAntInstance(id, template);
            case "QueenAntLarvaInstance":
                return new l2trunk.scripts.npc.model.QueenAntLarvaInstance(id, template);
            case "RafortyInstance":
                return new l2trunk.scripts.npc.model.RafortyInstance(id, template);


            case "residences.SiegeFlagInstance":
                return new l2trunk.gameserver.model.instances.residences.SiegeFlagInstance(id, template);
            case "residences.dominion.OutpostInstance":
                return new l2trunk.gameserver.model.instances.residences.dominion.OutpostInstance(id, template);

            case "residences.castle.BlacksmithInstance":
                return new l2trunk.scripts.npc.model.residences.castle.BlacksmithInstance(id, template);
            case "residences.castle.CastleControlTowerInstance":
                return new l2trunk.scripts.npc.model.residences.castle.CastleControlTowerInstance(id, template);
            case "residences.castle.CastleFakeTowerInstance":
                return new l2trunk.scripts.npc.model.residences.castle.CastleFakeTowerInstance(id, template);
            case "residences.castle.CastleFlameTowerInstance":
                return new l2trunk.scripts.npc.model.residences.castle.CastleFlameTowerInstance(id, template);
            case "residences.castle.CastleMassTeleporterInstance":
                return new l2trunk.scripts.npc.model.residences.castle.CastleMassTeleporterInstance(id, template);
            case "residences.castle.CastleMessengerInstance":
                return new l2trunk.scripts.npc.model.residences.castle.CastleMessengerInstance(id, template);
            case "residences.castle.ChamberlainInstance":
                return new l2trunk.scripts.npc.model.residences.castle.ChamberlainInstance(id, template);
            case "residences.castle.CourtInstance":
                return new l2trunk.scripts.npc.model.residences.castle.CourtInstance(id, template);
            case "residences.castle.DoormanInstance":
                return new l2trunk.scripts.npc.model.residences.castle.DoormanInstance(id, template);
            case "residences.castle.MercenaryManagerInstance":
                return new l2trunk.scripts.npc.model.residences.castle.MercenaryManagerInstance(id, template);
            case "residences.castle.VenomTeleportCubicInstance":
                return new l2trunk.scripts.npc.model.residences.castle.VenomTeleportCubicInstance(id, template);
            case "residences.castle.VenomTeleporterInstance":
                return new l2trunk.scripts.npc.model.residences.castle.VenomTeleporterInstance(id, template);
            case "residences.castle.WarehouseInstance":
                return new l2trunk.scripts.npc.model.residences.castle.WarehouseInstance(id, template);
            case "residences.clanhall.AuctionedDoormanInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.AuctionedDoormanInstance(id, template);
            case "residences.clanhall.AuctionedManagerInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.AuctionedManagerInstance(id, template);
            case "residences.clanhall.AuctioneerInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.AuctioneerInstance(id, template);
            case "residences.clanhall.BanditMessagerInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.BanditMessagerInstance(id, template);
            case "residences.clanhall.BrakelInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.BrakelInstance(id, template);
            case "residences.clanhall.DietrichInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.DietrichInstance(id, template);
            case "residences.clanhall.DoormanInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.DoormanInstance(id, template);
            case "residences.clanhall.FarmMessengerInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.FarmMessengerInstance(id, template);
            case "residences.clanhall.GustavInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.GustavInstance(id, template);
            case "residences.clanhall.LidiaVonHellmannInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.LidiaVonHellmannInstance(id, template);
            case "residences.clanhall.ManagerInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.ManagerInstance(id, template);
            case "residences.clanhall.MatchBerserkerInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.MatchBerserkerInstance(id, template);
            case "residences.clanhall.MatchClericInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.MatchClericInstance(id, template);
            case "residences.clanhall.MatchLeaderInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.MatchLeaderInstance(id, template);
            case "residences.clanhall.MatchMassTeleporterInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.MatchMassTeleporterInstance(id, template);
            case "residences.clanhall.MatchScoutInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.MatchScoutInstance(id, template);
            case "residences.clanhall.MatchTriefInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.MatchTriefInstance(id, template);
            case "residences.clanhall.MessengerInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.MessengerInstance(id, template);
            case "residences.clanhall.MikhailInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.MikhailInstance(id, template);
            case "residences.clanhall.NurkaInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.NurkaInstance(id, template);
            case "residences.clanhall.RainbowChestInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.RainbowChestInstance(id, template);
            case "residences.clanhall.RainbowCoordinatorInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.RainbowCoordinatorInstance(id, template);
            case "residences.clanhall.RainbowGourdInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.RainbowGourdInstance(id, template);
            case "residences.clanhall.RainbowMessengerInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.RainbowMessengerInstance(id, template);
            case "residences.clanhall.RainbowYetiInstance":
                return new l2trunk.scripts.npc.model.residences.clanhall.RainbowYetiInstance(id, template);
//            case "residences.clanhall._34BossMinionInstance":
//                return  new l2trunk.scripts.npc.model.residences.clanhall._34BossMinionInstance(id, template);
            case "residences.dominion.CatapultInstance":
                return new l2trunk.scripts.npc.model.residences.dominion.CatapultInstance(id, template);
            case "residences.dominion.MercenaryCaptainInstance":
                return new l2trunk.scripts.npc.model.residences.dominion.MercenaryCaptainInstance(id, template);
            case "residences.dominion.TerritoryManagerInstance":
                return new l2trunk.scripts.npc.model.residences.dominion.TerritoryManagerInstance(id, template);
//            case "residences.DoormanInstance":
//                return  new l2trunk.scripts.npc.model.residences.DoormanInstance(id, template);
            case "residences.fortress.DoormanInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.DoormanInstance(id, template);
            case "residences.fortress.EnvoyInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.EnvoyInstance(id, template);
//            case "residences.fortress.FacilityManagerInstance":
//                return  new l2trunk.scripts.npc.model.residences.fortress.FacilityManagerInstance(id, template);
            case "residences.fortress.LogisticsOfficerInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.LogisticsOfficerInstance(id, template);
            case "residences.fortress.ManagerInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.ManagerInstance(id, template);
            case "residences.fortress.peace.ArcherCaptionInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.peace.ArcherCaptionInstance(id, template);
            case "residences.fortress.peace.GuardCaptionInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.peace.GuardCaptionInstance(id, template);
            case "residences.fortress.peace.SupportUnitCaptionInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.peace.SupportUnitCaptionInstance(id, template);
            case "residences.fortress.peace.SuspiciousMerchantInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.peace.SuspiciousMerchantInstance(id, template);
            case "residences.fortress.siege.BackupPowerUnitInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.siege.BackupPowerUnitInstance(id, template);
            case "residences.fortress.siege.BallistaInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.siege.BallistaInstance(id, template);
            case "residences.fortress.siege.ControlUnitInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.siege.ControlUnitInstance(id, template);
            case "residences.fortress.siege.MainMachineInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.siege.MainMachineInstance(id, template);
            case "residences.fortress.siege.MercenaryCaptionInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.siege.MercenaryCaptionInstance(id, template);
            case "residences.fortress.siege.PowerControlUnitInstance":
                return new l2trunk.scripts.npc.model.residences.fortress.siege.PowerControlUnitInstance(id, template);
            case "residences.QuestSiegeGuardInstance":
                return new l2trunk.scripts.npc.model.residences.QuestSiegeGuardInstance(id, template);
            case "residences.SiegeGuardInstance":
                return new l2trunk.scripts.npc.model.residences.SiegeGuardInstance(id, template);
            case "residences.TeleportSiegeGuardInstance":
                return new l2trunk.scripts.npc.model.residences.TeleportSiegeGuardInstance(id, template);
            case "RiganInstance":
                return new l2trunk.scripts.npc.model.RiganInstance(id, template);
            case "RignosInstance":
                return new l2trunk.scripts.npc.model.RignosInstance(id, template);
            case "SairlenGatekeeperInstance":
                return new l2trunk.scripts.npc.model.SairlenGatekeeperInstance(id, template);
            case "SallyInstance":
                return new l2trunk.scripts.npc.model.SallyInstance(id, template);
            case "SandstormInstance":
                return new l2trunk.scripts.npc.model.SandstormInstance(id, template);
            case "SealDeviceInstance":
                return new l2trunk.scripts.npc.model.SealDeviceInstance(id, template);
            case "SeducedInvestigatorInstance":
                return new l2trunk.scripts.npc.model.SeducedInvestigatorInstance(id, template);
            case "SeedOfAnnihilationInstance":
                return new l2trunk.scripts.npc.model.SeedOfAnnihilationInstance(id, template);
            case "SepulcherMonsterInstance":
                return new l2trunk.scripts.npc.model.SepulcherMonsterInstance(id, template);
            case "SepulcherNpcInstance":
                return new l2trunk.scripts.npc.model.SepulcherNpcInstance(id, template);
            case "SepulcherRaidInstance":
                return new l2trunk.scripts.npc.model.SepulcherRaidInstance(id, template);
            case "SirraInstance":
                return new l2trunk.scripts.npc.model.SirraInstance(id, template);
            case "SnowmanInstance":
                return new l2trunk.scripts.npc.model.SnowmanInstance(id, template);
            case "SpecialMinionInstance":
                return new l2trunk.scripts.npc.model.SpecialMinionInstance(id, template);
            case "SquashInstance":
                return new l2trunk.scripts.npc.model.SquashInstance(id, template);
            case "StarStoneInstance":
                return new l2trunk.scripts.npc.model.StarStoneInstance(id, template);
            case "SteamCorridorControllerInstance":
                return new l2trunk.scripts.npc.model.SteamCorridorControllerInstance(id, template);
            case "SteelCitadelTeleporterInstance":
                return new l2trunk.scripts.npc.model.SteelCitadelTeleporterInstance(id, template);
            case "TepiosRewardInstance":
                return new l2trunk.scripts.npc.model.TepiosRewardInstance(id, template);
            case "ThomasInstance":
                return new l2trunk.scripts.npc.model.ThomasInstance(id, template);
            case "TreasureChestInstance":
                return new l2trunk.scripts.npc.model.TreasureChestInstance(id, template);
            case "TriolsMirrorInstance":
                return new l2trunk.scripts.npc.model.TriolsMirrorInstance(id, template);
            case "TullyWorkShopTeleporterInstance":
                return new l2trunk.scripts.npc.model.TullyWorkShopTeleporterInstance(id, template);
            case "ValakasGatekeeperInstance":
                return new l2trunk.scripts.npc.model.ValakasGatekeeperInstance(id, template);
            case "WarpgateInstance":
                return new l2trunk.scripts.npc.model.WarpgateInstance(id, template);
            case "WorkshopGatekeeperInstance":
                return new l2trunk.scripts.npc.model.WorkshopGatekeeperInstance(id, template);
            case "WorkshopServantInstance":
                return new l2trunk.scripts.npc.model.WorkshopServantInstance(id, template);
            case "YehanBrotherInstance":
                return new l2trunk.scripts.npc.model.YehanBrotherInstance(id, template);
            case "ZakenCandleInstance":
                return new l2trunk.scripts.npc.model.ZakenCandleInstance(id, template);
            case "ZakenGatekeeperInstance":
                return new l2trunk.scripts.npc.model.ZakenGatekeeperInstance(id, template);

            default:
                throw new IllegalArgumentException("no template for name: " + type);
        }

    }
}
