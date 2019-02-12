package l2trunk.gameserver.scripts;

import l2trunk.gameserver.instancemanager.QuestManager;
import l2trunk.gameserver.model.Player;
import l2trunk.gameserver.model.instances.NpcInstance;
import l2trunk.scripts.actions.OnActionShift;
import l2trunk.scripts.ai.Zone.HeineFields.HeineFieldsHerbs;
import l2trunk.scripts.bosses.*;
import l2trunk.scripts.events.AprilFoolsDay.AprilFoolsDay;
import l2trunk.scripts.events.BossRandom.BossRandom;
import l2trunk.scripts.events.Christmas.Christmas;
import l2trunk.scripts.events.Christmas.NewYearTimer;
import l2trunk.scripts.events.CofferofShadows.Coffer;
import l2trunk.scripts.events.CofferofShadows.CofferofShadows;
import l2trunk.scripts.events.CustomDropItems.CustomDropItems;
import l2trunk.scripts.events.EventsConfig;
import l2trunk.scripts.events.FreyaEvent.FreyaEvent;
import l2trunk.scripts.events.GiftOfVitality.GiftOfVitality;
import l2trunk.scripts.events.Hitman.Hitman;
import l2trunk.scripts.events.March8.March8;
import l2trunk.scripts.events.MasterOfEnchanting.EnchantingReward;
import l2trunk.scripts.events.MasterOfEnchanting.MasterOfEnchanting;
import l2trunk.scripts.events.PcCafePointsExchange.PcCafePointsExchange;
import l2trunk.scripts.events.PiratesTreasure.PiratesTreasure;
import l2trunk.scripts.events.SantaEvent.SantaEvent;
import l2trunk.scripts.events.SavingSnowman.SavingSnowman;
import l2trunk.scripts.events.SummerMeleons.SummerMeleons;
import l2trunk.scripts.events.TheFallHarvest.TheFallHarvest;
import l2trunk.scripts.events.TheFlowOfTheHorror.TheFlowOfTheHorror;
import l2trunk.scripts.events.TrickOfTrans.TrickOfTrans;
import l2trunk.scripts.events.Viktorina.Viktorina;
import l2trunk.scripts.events.bountyhunters.HuntersGuild;
import l2trunk.scripts.events.glitmedal.glitmedal;
import l2trunk.scripts.events.heart.Heart;
import l2trunk.scripts.events.l2day.LettersCollection;
import l2trunk.scripts.handler.admincommands.AdminBosses;
import l2trunk.scripts.handler.items.BeastShot;
import l2trunk.scripts.handler.items.Extractable;
import l2trunk.scripts.handler.items.FishItem;
import l2trunk.scripts.scriptconfig.ScriptConfig;
import l2trunk.scripts.services.*;
import l2trunk.scripts.services.community.*;
import l2trunk.scripts.services.petevolve.*;
import l2trunk.scripts.services.villagemasters.Ally;
import l2trunk.scripts.services.villagemasters.Clan;
import l2trunk.scripts.services.villagemasters.Occupation;
import l2trunk.scripts.zones.SeedOfAnnihilation;
import l2trunk.scripts.zones.TullyWorkshopZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static l2trunk.commons.lang.NumberUtils.toInt;

public enum Scripts {
    INSTANCE;
    public static final Map<Integer, List<ScriptClassAndMethod>> dialogAppends = new HashMap<>();
    public static final Map<String, ScriptClassAndMethod> onAction = new HashMap<>();
    //    public static final Map<String, ScriptClassAndMethod> onActionShift = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(Scripts.class);
    private Map<String, ScriptFile> scripts = new HashMap<>();
    private Map<String, Functions> functions = new HashMap<>();


    private void addHandlers(Class<?> clazz) {
        try {
            for (Method method : clazz.getMethods())
                if (method.getName().contains("DialogAppend_")) {
                    Integer id = toInt(method.getName().substring(13));
                    List<ScriptClassAndMethod> handlers = dialogAppends.computeIfAbsent(id, k -> new ArrayList<>());
                    handlers.add(new ScriptClassAndMethod(clazz.getName(), method.getName()));
//                } else if (method.name().contains("OnAction_")) {
//                    String name = method.name().substring(9);
//                    onAction.put(name, new ScriptClassAndMethod(clazz.getSimpleName(), method.name()));
//                } else if (method.name().contains("OnActionShift_")) {
//                    String name = method.name().substring(14);
//                    onActionShift.put(name, new ScriptClassAndMethod(clazz.name(), method.name()));
                }
        } catch (NumberFormatException | SecurityException e) {
            LOG.error("Exception while adding Handlers ", e);
        } catch (Exception e) {
            LOG.error("something went wrong: " + e);
        }
    }

    public Object callScripts(Player caller, String className, String methodName, Object[] args) {
        return callScripts(caller, className, methodName, args, null);
    }

    public Object callScripts(Player caller, String className, String methodName, Object[] args, NpcInstance npc) {
        String shortClassName = className.replace("l2trunk.scripts.", "");
//        Object o = scripts.get(shortClassName);
//        if (o == null)
        Functions o = functions.get(shortClassName);
        if (o == null)
            throw new IllegalArgumentException("not found script " + className);

        o.setNpc(npc);
        o.setPlayer(caller);
//            for (Map.Entry<String, Object> param : variables.entrySet())
//                try {
//                    (FieldUtils.writeField(o, param.getKey(), param.getValue());
//                } catch (IllegalAccessException e) {
//                    LOG.error("Scripts: Failed setting fields for " + o.getClass().getName(), e);
//                }
//        }
//        if (caller != null)
////            try {
////                Field field;
////                if ((field = FieldUtils.getField(o.getClass(), "player")) != null) {
////
////                    FieldUtils.writeField(field, o, caller.getRef());
////                }
//                if (o instanceof Functions) {
//                    ((Functions)o).player = caller.getRef();
//                }
////            } catch (IllegalAccessException e) {
////                LOG.error("Scripts: Failed setting field for " + o.getClass().getName(), e);
////            }

        Object ret = null;
        try {
            Class<?>[] parameterTypes = new Class<?>[args.length];
            for (int i = 0; i < args.length; i++)
                parameterTypes[i] = args[i] != null ? args[i].getClass() : null;
            Method method = o.getClass().getMethod(methodName, parameterTypes);
            ret = method.invoke(o, args);
//            ret = MethodUtils.invokeMethod(o, methodName, args, parameterTypes);
        } catch (NoSuchMethodException nsme) {
            LOG.error("Scripts: No such method " + o.getClass().getName() + "." + methodName + "()!", nsme);
        } catch (InvocationTargetException ite) {
            LOG.error("Scripts: Error while calling " + o.getClass().getName() + "." + methodName + "()", ite.getTargetException());
        } catch (IllegalAccessException e) {
            LOG.error("Scripts: Failed calling " + o.getClass().getName() + "." + methodName + "()", e);
        }

        return ret;
    }

    public void init() {
        loadFunctions();
        loadScripts();
        QuestManager.initAllQuests();
        scripts.forEach((k, v) -> v.onLoad());


        functions.forEach((k, v) -> addHandlers(v.getClass()));
        LOG.info("Loaded :" + dialogAppends.size() + " dialog handlers");
    }

    private void loadFunctions() {
        functions.put("actions.OnActionShift", new OnActionShift());
        functions.put("bosses.AntharasManager", new AntharasManager());
        functions.put("bosses.BaiumManager", new BaiumManager());
        functions.put("bosses.BaylorManager", new BaylorManager());
        functions.put("bosses.BelethManager", new BelethManager());
        functions.put("bosses.FourSepulchersManager", new FourSepulchersManager());
        functions.put("bosses.FourSepulchersSpawn", new FourSepulchersSpawn());
        functions.put("bosses.SailrenManager", new SailrenManager());
        functions.put("bosses.ValakasManager", new ValakasManager());
        functions.put("events.AprilFoolsDay.AprilFoolsDay", new AprilFoolsDay());
        functions.put("events.BossRandom.BossRandom", new BossRandom());
        functions.put("events.bountyhunters.HuntersGuild", new HuntersGuild());
        functions.put("events.Christmas.Christmas", new Christmas());
        functions.put("events.CofferofShadows.CofferofShadows", new CofferofShadows());
        functions.put("events.CustomDropItems.CustomDropItems", new CustomDropItems());
        functions.put("events.EventsConfig", new EventsConfig());
        functions.put("events.FreyaEvent.FreyaEvent", new FreyaEvent());
        functions.put("events.GiftOfVitality.GiftOfVitality", new GiftOfVitality());
        functions.put("events.glitmedal.glitmedal", new glitmedal());
        functions.put("events.heart.Heart", new Heart());
        functions.put("events.Hitman.Hitman", new Hitman());
        functions.put("events.l2day.LettersCollection", new LettersCollection());
        functions.put("events.March8.March8", new March8());
        functions.put("events.MasterOfEnchanting.EnchantingReward", new EnchantingReward());
        functions.put("events.MasterOfEnchanting.MasterOfEnchanting", new MasterOfEnchanting());
        functions.put("events.PcCafePointsExchange.PcCafePointsExchange", new PcCafePointsExchange());
        functions.put("events.PiratesTreasure.PiratesTreasure", new PiratesTreasure());
        functions.put("events.SantaEvent.SantaEvent", new SantaEvent());
        functions.put("events.SavingSnowman.SavingSnowman", new SavingSnowman());
        functions.put("events.SummerMeleons.SummerMeleons", new SummerMeleons());
        functions.put("events.TheFallHarvest.TheFallHarvest", new TheFallHarvest());
        functions.put("events.TheFlowOfTheHorror.TheFlowOfTheHorror", new TheFlowOfTheHorror());
        functions.put("events.TrickOfTrans.TrickOfTrans", new TrickOfTrans());
        functions.put("events.Viktorina.Viktorina", new Viktorina());
        functions.put("handler.bypass.TeleToFantasyIsle", new TeleToFantasyIsle());
        functions.put("scriptconfig.ScriptConfig", new ScriptConfig());
        functions.put("services.Augmentation", new Augmentation());
        functions.put("services.Birthday", new Birthday());
        functions.put("services.BuyWashPk", new BuyWashPk());
        functions.put("services.ClearPK", new ClearPK());
        functions.put("services.CoinPoinExch", new CoinPoinExch());
        functions.put("services.community.CommunityClan", new CommunityClan());
        functions.put("services.community.CommunityPartyMatching", new CommunityPartyMatching());
        functions.put("services.community.ServicesCommunity", new ServicesCommunity());
        functions.put("services.community.ShowInfo", new ShowInfo());
        functions.put("services.Delevel", new Delevel());
        functions.put("services.Exchanger", new Exchanger());
        functions.put("services.ExpandCWH", new ExpandCWH());
        functions.put("services.ExpandInventory", new ExpandInventory());
        functions.put("services.ExpandWarehouse", new ExpandWarehouse());
        functions.put("services.FantasyIsle", new FantasyIsle());
        functions.put("services.GrandIsleofPrayerRace", new GrandIsleofPrayerRace());
        functions.put("services.HairChange", new HairChange());
        functions.put("services.HeroItems", new HeroItems());
        functions.put("services.ItemBroker", new ItemBroker());
        functions.put("services.LevelPanel", new LevelPanel());
        functions.put("services.LuxorAgathion", new LuxorAgathion());
        functions.put("services.lvl", new lvl());
        functions.put("services.Misc", new Misc());
        functions.put("services.NickColor", new NickColor());
        functions.put("services.NoblessSell", new NoblessSell());
        functions.put("services.ObtainTalisman", new ObtainTalisman());
        functions.put("services.PaganDoormans", new PaganDoormans());
        functions.put("services.petevolve.clanhall", new clanhall());
        functions.put("services.petevolve.exchange", new exchange());
        functions.put("services.petevolve.fenrir", new fenrir());
        functions.put("services.petevolve.ibbuffalo", new ibbuffalo());
        functions.put("services.petevolve.ibcougar", new ibcougar());
        functions.put("services.petevolve.ibkookaburra", new ibkookaburra());
        functions.put("services.petevolve.wolfevolve", new wolfevolve());
        functions.put("services.PurpleManedHorse", new PurpleManedHorse());
        functions.put("services.Pushkin", new Pushkin());
        functions.put("services.RateBonus", new RateBonus());
        functions.put("services.Rename", new Rename());
        functions.put("services.RideHire", new RideHire());
        functions.put("services.Roulette", new Roulette());
        functions.put("services.SellPcService", new SellPcService());
        functions.put("services.SupportMagic", new SupportMagic());
        functions.put("services.TakeBeastHandler", new TakeBeastHandler());
        functions.put("services.TeleToCatacomb", new TeleToCatacomb());
        functions.put("services.TeleToFantasyIsle", new TeleToFantasyIsle());
        functions.put("services.TeleToGH", new TeleToGH());
        functions.put("services.TeleToGracia", new TeleToGracia());
        functions.put("services.TeleToMDT", new TeleToMDT());
        functions.put("services.TeleToParnassus", new TeleToParnassus());
        functions.put("services.TeleToStakatoNest", new TeleToStakatoNest());
        functions.put("services.TitleColor", new TitleColor());
        functions.put("services.villagemasters.Ally", new Ally());
        functions.put("services.villagemasters.Clan", new Clan());
        functions.put("services.villagemasters.Occupation", new Occupation());
        functions.put("services.VitaminManager", new VitaminManager());
        functions.put("Util", new l2trunk.scripts.Util());
        functions.put("zones.SeedOfAnnihilation", new SeedOfAnnihilation());
    }

    public void load() {
//        loadNpcInstances();
    }

    private void loadScripts() {
//        scripts.put("actions.OnActionShift",l2trunk.scripts.actions.OnActionShift.class);
//        scripts.put("actions.RewardListInfo",l2trunk.scripts.actions.RewardListInfo.class);
        scripts.put("bosses.AntharasManager", new AntharasManager());
        scripts.put("bosses.BaiumManager", new BaiumManager());
        scripts.put("bosses.BaylorManager", new BaylorManager());
        scripts.put("bosses.BelethManager", new BelethManager());
        scripts.put("bosses.FourSepulchersManager", new FourSepulchersManager());
        scripts.put("bosses.SailrenManager", new SailrenManager());
        scripts.put("bosses.ValakasManager", new ValakasManager());
        scripts.put("events.AprilFoolsDay.AprilFoolsDay", new AprilFoolsDay());
        scripts.put("events.BossRandom.BossRandom", new BossRandom());
        scripts.put("events.bountyhunters.HuntersGuild", new HuntersGuild());
        scripts.put("events.Christmas.Christmas", new Christmas());
        scripts.put("events.Christmas.NewYearTimer", new NewYearTimer());
        scripts.put("events.Christmas.Seed", new l2trunk.scripts.events.Christmas.Seed());
        scripts.put("events.CofferofShadows.Coffer", new Coffer());
        scripts.put("events.CofferofShadows.CofferofShadows", new l2trunk.scripts.events.CofferofShadows.CofferofShadows());
        scripts.put("events.CustomDropItems.CustomDropItems", new l2trunk.scripts.events.CustomDropItems.CustomDropItems());
        scripts.put("events.EventsConfig", new EventsConfig());
        scripts.put("events.FreyaEvent.FreyaEvent", new l2trunk.scripts.events.FreyaEvent.FreyaEvent());
        scripts.put("events.GiftOfVitality.GiftOfVitality", new l2trunk.scripts.events.GiftOfVitality.GiftOfVitality());
        scripts.put("events.glitmedal.glitmedal", new l2trunk.scripts.events.glitmedal.glitmedal());
        scripts.put("events.heart.Heart", new Heart());
        scripts.put("events.Hitman.Hitman", new l2trunk.scripts.events.Hitman.Hitman());
        scripts.put("events.l2day.LettersCollection", new l2trunk.scripts.events.l2day.LettersCollection());
        scripts.put("events.March8.March8", new l2trunk.scripts.events.March8.March8());
        scripts.put("events.MasterOfEnchanting.MasterOfEnchanting", new l2trunk.scripts.events.MasterOfEnchanting.MasterOfEnchanting());
        scripts.put("events.PcCafePointsExchange.PcCafePointsExchange", new l2trunk.scripts.events.PcCafePointsExchange.PcCafePointsExchange());
        scripts.put("events.PiratesTreasure.PiratesTreasure", new l2trunk.scripts.events.PiratesTreasure.PiratesTreasure());
        scripts.put("events.SantaEvent.SantaEvent", new SantaEvent());
        scripts.put("events.SavingSnowman.SavingSnowman", new l2trunk.scripts.events.SavingSnowman.SavingSnowman());
        scripts.put("events.SummerMeleons.MeleonSeed", new l2trunk.scripts.events.SummerMeleons.MeleonSeed());
        scripts.put("events.SummerMeleons.SummerMeleons", new l2trunk.scripts.events.SummerMeleons.SummerMeleons());
        scripts.put("events.TheFallHarvest.Seed", new l2trunk.scripts.events.TheFallHarvest.Seed());
        scripts.put("events.TheFallHarvest.TheFallHarvest", new l2trunk.scripts.events.TheFallHarvest.TheFallHarvest());
        scripts.put("events.TheFlowOfTheHorror.TheFlowOfTheHorror", new l2trunk.scripts.events.TheFlowOfTheHorror.TheFlowOfTheHorror());
        scripts.put("events.TrickOfTrans.TrickOfTrans", new TrickOfTrans());
        scripts.put("events.Viktorina.Viktorina", new l2trunk.scripts.events.Viktorina.Viktorina());
        scripts.put("handler.admincommands.AdminBosses", new AdminBosses());
        scripts.put("handler.admincommands.AdminEpic", new l2trunk.scripts.handler.admincommands.AdminEpic());
        scripts.put("handler.admincommands.AdminResidence", new l2trunk.scripts.handler.admincommands.AdminResidence());
        scripts.put("handler.bypass.TeleToFantasyIsle", new l2trunk.scripts.handler.bypass.TeleToFantasyIsle());
        scripts.put("handler.items.AttributeStones", new l2trunk.scripts.handler.items.AttributeStones());
        scripts.put("handler.items.Battleground", new l2trunk.scripts.handler.items.Battleground());
        scripts.put("handler.items.BeastShot", new BeastShot());
        scripts.put("handler.items.BlessedSpiritShot", new l2trunk.scripts.handler.items.BlessedSpiritShot());
        scripts.put("handler.items.Books", new l2trunk.scripts.handler.items.Books());
        scripts.put("handler.items.Calculator", new l2trunk.scripts.handler.items.Calculator());
        scripts.put("handler.items.CharChangePotions", new l2trunk.scripts.handler.items.CharChangePotions());
        scripts.put("handler.items.Cocktails", new l2trunk.scripts.handler.items.Cocktails());
        scripts.put("handler.items.DisguiseScroll", new l2trunk.scripts.handler.items.DisguiseScroll());
        scripts.put("handler.items.EnchantScrolls", new l2trunk.scripts.handler.items.EnchantScrolls());
        scripts.put("handler.items.EquipableItem", new l2trunk.scripts.handler.items.EquipableItem());
        scripts.put("handler.items.Extractable", new Extractable());
        scripts.put("handler.items.FishItem", new FishItem());
        scripts.put("handler.items.FishShots", new l2trunk.scripts.handler.items.FishShots());
        scripts.put("handler.items.Harvester", new l2trunk.scripts.handler.items.Harvester());
        scripts.put("handler.items.HelpBook", new l2trunk.scripts.handler.items.HelpBook());
        scripts.put("handler.items.HolyWater", new l2trunk.scripts.handler.items.HolyWater());
        scripts.put("handler.items.ItemSkills", new l2trunk.scripts.handler.items.ItemSkills());
        scripts.put("handler.items.Kamaloka", new l2trunk.scripts.handler.items.Kamaloka());
        scripts.put("handler.items.Keys", new l2trunk.scripts.handler.items.Keys());
        scripts.put("handler.items.MercTicket", new l2trunk.scripts.handler.items.MercTicket());
        scripts.put("handler.items.NameColor", new l2trunk.scripts.handler.items.NameColor());
        scripts.put("handler.items.NevitVoice", new l2trunk.scripts.handler.items.NevitVoice());
        scripts.put("handler.items.PathfinderEquipment", new l2trunk.scripts.handler.items.PathfinderEquipment());
        scripts.put("handler.items.PetSummon", new l2trunk.scripts.handler.items.PetSummon());
        scripts.put("handler.items.Potions", new l2trunk.scripts.handler.items.Potions());
        scripts.put("handler.items.Recipes", new l2trunk.scripts.handler.items.Recipes());
        scripts.put("handler.items.RollingDice", new l2trunk.scripts.handler.items.RollingDice());
        scripts.put("handler.items.Seed", new l2trunk.scripts.handler.items.Seed());
        scripts.put("handler.items.SoulCrystals", new l2trunk.scripts.handler.items.SoulCrystals());
        scripts.put("handler.items.SoulShots", new l2trunk.scripts.handler.items.SoulShots());
        scripts.put("handler.items.Special", new l2trunk.scripts.handler.items.Special());
        scripts.put("handler.items.Spellbooks", new l2trunk.scripts.handler.items.Spellbooks());
        scripts.put("handler.items.SpiritShot", new l2trunk.scripts.handler.items.SpiritShot());
        scripts.put("handler.items.SupportPower", new l2trunk.scripts.handler.items.SupportPower());
        scripts.put("handler.items.TeleportBookmark", new l2trunk.scripts.handler.items.TeleportBookmark());
        scripts.put("handler.items.WorldMap", new l2trunk.scripts.handler.items.WorldMap());
        scripts.put("handler.voicecommands.CWHPrivileges", new l2trunk.scripts.handler.voicecommands.CWHPrivileges());
        scripts.put("handler.voicecommands.DragonStatus", new l2trunk.scripts.handler.voicecommands.DragonStatus());
        scripts.put("handler.voicecommands.Epics", new l2trunk.scripts.handler.voicecommands.Epics());
        scripts.put("handler.voicecommands.Quiz", new l2trunk.scripts.handler.voicecommands.Quiz());

        scripts.put("scriptconfig.ScriptConfig", new ScriptConfig());
        scripts.put("services.community.CareerManager", new CareerManager());
        scripts.put("services.community.CommunityAuctionHouse", new l2trunk.scripts.services.community.CommunityAuctionHouse());
        scripts.put("services.community.CommunityBoard", new l2trunk.scripts.services.community.CommunityBoard());
        scripts.put("services.community.CommunityBosses", new l2trunk.scripts.services.community.CommunityBosses());
        scripts.put("services.community.CommunityClan", new l2trunk.scripts.services.community.CommunityClan());
        scripts.put("services.community.CommunityDropCalculator", new l2trunk.scripts.services.community.CommunityDropCalculator());
        scripts.put("services.community.CommunityNpcs", new l2trunk.scripts.services.community.CommunityNpcs());
        scripts.put("services.community.CommunityPartyMatching", new l2trunk.scripts.services.community.CommunityPartyMatching());
        scripts.put("services.community.CommunityWarehouse", new l2trunk.scripts.services.community.CommunityWarehouse());
        scripts.put("services.community.Forge", new l2trunk.scripts.services.community.Forge());
        scripts.put("services.community.RankingCommunity", new l2trunk.scripts.services.community.RankingCommunity());
        scripts.put("services.community.ServicesCommunity", new l2trunk.scripts.services.community.ServicesCommunity());
        scripts.put("services.community.StatManager", new l2trunk.scripts.services.community.StatManager());
        scripts.put("services.FantasyIsle", new l2trunk.scripts.services.FantasyIsle());
        scripts.put("services.LindviorMovie", new l2trunk.scripts.services.LindviorMovie());
        scripts.put("services.PurpleManedHorse", new PurpleManedHorse());
        scripts.put("services.SellPcService", new SellPcService());
        scripts.put("services.TeleToGH", new l2trunk.scripts.services.TeleToGH());
        scripts.put("services.TeleToParnassus", new l2trunk.scripts.services.TeleToParnassus());
        scripts.put("zones.DragonValley", new l2trunk.scripts.zones.DragonValley());
        scripts.put("zones.EpicZone", new l2trunk.scripts.zones.EpicZone());
        scripts.put("zones.KashaNegate", new l2trunk.scripts.zones.KashaNegate());
        scripts.put("zones.MonsterTrap", new l2trunk.scripts.zones.MonsterTrap());
        scripts.put("zones.SeedOfAnnihilation", new l2trunk.scripts.zones.SeedOfAnnihilation());
        scripts.put("zones.TullyWorkshopZone", new TullyWorkshopZone());
        scripts.put("ai.Zone.HeineFields.HeineFieldsHerbs", new HeineFieldsHerbs());

    }

    public class ScriptClassAndMethod {
        public final String className;
        public final String methodName;

        ScriptClassAndMethod(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
    }
}