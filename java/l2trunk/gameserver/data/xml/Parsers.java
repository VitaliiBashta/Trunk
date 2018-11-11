package l2trunk.gameserver.data.xml;


import l2trunk.gameserver.data.StringHolder;
import l2trunk.gameserver.data.htm.HtmCache;
import l2trunk.gameserver.data.xml.holder.BuyListHolder;
import l2trunk.gameserver.data.xml.holder.MultiSellHolder;
import l2trunk.gameserver.data.xml.holder.ProductHolder;
import l2trunk.gameserver.data.xml.holder.RecipeHolder;
import l2trunk.gameserver.data.xml.parser.*;
import l2trunk.gameserver.instancemanager.ReflectionManager;
import l2trunk.gameserver.tables.SkillTable;
import l2trunk.gameserver.tables.SpawnTable;

public final class Parsers {
    private Parsers() {
    }

    public static void parseAll() {
        HtmCache.getInstance().reload();
        StringHolder.getInstance().load();
        //
        SkillTable.getInstance().load(); // - SkillParser.getInstance();
        OptionDataParser.getInstance().load();
        ItemParser.getInstance().load();
        //
        ZoneParser.getInstance().load();
        DoorParser.getInstance().load(); //
        NpcParser.getInstance().load();

        DomainParser.getInstance().load();
        RestartPointParser.getInstance().load();
        ExchangeItemParser.getInstance().load();
        StaticObjectParser.getInstance().load();


        SpawnTable.getInstance();
        SpawnParser.getInstance().load();
        InstantZoneParser.getInstance().load();

        ReflectionManager.getInstance();

        //
        AirshipDockParser.getInstance().load();
        SkillAcquireParser.getInstance().load();
        //
        CharTemplateParser.getInstance().load();
        //
        ResidenceParser.getInstance().load();
        EventParser.getInstance().load();
        FightClubMapParser.getInstance().load();
        // support(cubic & agathion)
        CubicParser.getInstance().load();
        //
        BuyListHolder.getInstance();
        RecipeHolder.getInstance();
        MultiSellHolder.getInstance();
        ProductHolder.getInstance();
        // AgathionParser.getInstance();
        // item support
        HennaParser.getInstance().load();
        EnchantItemParser.getInstance().load();
        SoulCrystalParser.getInstance().load();
        ArmorSetsParser.getInstance().load();

        // etc
        PetitionGroupParser.getInstance().load();
        DressArmorParser.getInstance().load();
        DressCloakParser.getInstance().load();
        DressShieldParser.getInstance().load();
        DressWeaponParser.getInstance().load();
        AugmentationDataParser.getInstance().load();

        // Premium
        PremiumParser.getInstance().load();

        // Community Board Adds
        FoundationParser.getInstance().load();
    }
}