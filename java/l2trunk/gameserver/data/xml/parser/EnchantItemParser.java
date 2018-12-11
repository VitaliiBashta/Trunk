package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.EnchantItemHolder;
import l2trunk.gameserver.templates.item.support.EnchantScroll;
import l2trunk.gameserver.templates.item.support.FailResultType;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.Iterator;

public enum EnchantItemParser {
    INSTANCE;
    private static Path xml = Config.DATAPACK_ROOT.resolve("data/enchant_items.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("Loaded " + EnchantItemHolder.size() + " items");
    }

    private void readData(Element rootElement) {
        int defaultMaxEnchant = 0;
        int defaultChance = 0;
        int defaultMagicChance = 0;
        boolean defaultVisualEffect = false;

        Element defaultElement = rootElement.element("default");
        if (defaultElement != null) {
            defaultMaxEnchant = Integer.parseInt(defaultElement.attributeValue("max_enchant"));
            defaultChance = Integer.parseInt(defaultElement.attributeValue("chance"));
            defaultMagicChance = Integer.parseInt(defaultElement.attributeValue("magic_chance"));
            defaultVisualEffect = Boolean.parseBoolean(defaultElement.attributeValue("visual_effect"));
        }

        for (Iterator<Element> iterator = rootElement.elementIterator("enchant_scroll"); iterator.hasNext(); ) {
            Element enchantItemElement = iterator.next();
            int itemId = Integer.parseInt(enchantItemElement.attributeValue("id"));
            int chance = enchantItemElement.attributeValue("chance") == null ? defaultChance : Integer.parseInt(enchantItemElement.attributeValue("chance"));

            int magicChance = enchantItemElement.attributeValue("magic_chance") == null ? defaultMagicChance : Integer.parseInt(enchantItemElement.attributeValue("magic_chance"));
            int maxEnchant = enchantItemElement.attributeValue("max_enchant") == null ? defaultMaxEnchant : Integer.parseInt(enchantItemElement.attributeValue("max_enchant"));
            FailResultType resultType = FailResultType.valueOf(enchantItemElement.attributeValue("on_fail"));
            boolean visualEffect = enchantItemElement.attributeValue("visual_effect") == null ? defaultVisualEffect : Boolean.parseBoolean(enchantItemElement.attributeValue("visual_effect"));

            EnchantScroll item = new EnchantScroll(itemId, chance, maxEnchant, resultType, visualEffect);
            EnchantItemHolder.addEnchantScroll(item);

            for (Iterator<Element> iterator2 = enchantItemElement.elementIterator(); iterator2.hasNext(); ) {
                Element element2 = iterator2.next();
                if (element2.getName().equals("item_list")) {
                    for (Element e : element2.elements())
                        item.addItemId(Integer.parseInt(e.attributeValue("id")));
                } else {
                    LOG.info("Not supported for now.2");
                }
            }
        }
    }
}
