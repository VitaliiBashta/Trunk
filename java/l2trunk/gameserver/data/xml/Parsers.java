package l2trunk.gameserver.data.xml;


import l2trunk.gameserver.data.StringHolder;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.*;
import l2trunk.gameserver.data.xml.parser.*;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.tables.SpawnTable;

public final class Parsers {
    private Parsers() {
    }

    public static void parseAll() {
        HtmCache.INSTANCE.reload();
        StringHolder.INSTANCE.load();
        //
        SkillTable.INSTANCE.load(); // - SkillParser.INSTANCE();
        OptionDataParser.INSTANCE.load();
        ItemParser.INSTANCE.load();
        //
        ZoneParser.INSTANCE.load();
        DoorParser.INSTANCE.load(); //
        NpcParser.INSTANCE.load();

        DomainParser.INSTANCE.load();
        RestartPointParser.INSTANCE.load();
        ExchangeItemParser.INSTANCE.load();
        StaticObjectParser.INSTANCE.load();


        SpawnTable.INSTANCE.init();
        SpawnParser.INSTANCE.load();
        InstantZoneParser.INSTANCE.load();

        ReflectionManager.INSTANCE.init();

        //
        AirshipDockParser.INSTANCE.load();
        SkillAcquireParser.INSTANCE.load();
        //
        CharTemplateParser.INSTANCE.load();
        //
        ResidenceParser.INSTANCE.load();

        EventParser.INSTANCE.load();
        // support(cubic & agathion)
        CubicParser.INSTANCE.load();
        //
        BuyListHolder.INSTANCE.init();
        RecipeHolder.getInstance();
        MultiSellHolder.INSTANCE.toString();
        ProductHolder.getInstance();
        // AgathionParser.INSTANCE();
        // item support
        HennaParser.INSTANCE.load();
        EnchantItemParser.INSTANCE.load();
//        SoulCrystalParser.INSTANCE().loadFile();
        SoulCrystalHolder.getInstance();
        ArmorSetsParser.INSTANCE.load();

        // etc
        DressArmorParser.INSTANCE.load();
        DressCloakParser.INSTANCE.load();
        DressShieldParser.INSTANCE.load();
        DressWeaponParser.INSTANCE.load();
        AugmentationDataParser.INSTANCE.load();

        // Community Board Adds
        FoundationParser.INSTANCE.load();
    }
}
