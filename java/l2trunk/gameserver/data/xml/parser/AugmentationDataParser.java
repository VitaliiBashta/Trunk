package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.ParserUtil;
import l2trunk.commons.math.random.RndSelector;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.AugmentationDataHolder;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.templates.augmentation.AugmentationInfo;
import l2trunk.gameserver.templates.augmentation.OptionGroup;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;

import static l2trunk.commons.lang.NumberUtils.toDouble;

public enum AugmentationDataParser {
    INSTANCE;
    private Path xml = Config.DATAPACK_ROOT.resolve("data/augmentation_data.xml");
    private final Logger LOG = LoggerFactory.getLogger(this.getClass().getName());

    public void load() {
        ParserUtil.INSTANCE.load(xml).forEach(this::readData);
        LOG.info("loaded " + AugmentationDataHolder.size() + " doors ");
    }

    private void readData(Element rootElement) {
        Map<String, int[]> items = new HashMap<>();
        Map<Integer, RndSelector<OptionGroup>[][]> variants = new HashMap<>();
        for (Iterator<Element> iterator = rootElement.elementIterator("item_group"); iterator.hasNext(); ) {
            Element element = iterator.next();

            String name = element.attributeValue("name");

            List<Element> itemElements = element.elements();
            List<Integer> list = new ArrayList<>();
            for (Element itemElement : itemElements) {
                int itemId = Integer.parseInt(itemElement.attributeValue("id"));

                ItemTemplate itemTemplate = ItemHolder.getTemplate(itemId);
                if (itemTemplate == null) {
                    LOG.warn("Not found item: " + itemId + "; item group: " + name);
                } else {
                    list.add(itemId);
                }
            }
            items.put(name, list.stream().mapToInt(Number::intValue).toArray());
        }
        for (Iterator<Element> iterator = rootElement.elementIterator("variants"); iterator.hasNext(); ) {
            Element element = iterator.next();

            int itemId = Integer.parseInt(element.attributeValue("mineral_id"));

            RndSelector<OptionGroup>[][] ar = new RndSelector[2][];

            ar[0] = readVariation(element.element("warrior_variation"));
            ar[1] = readVariation(element.element("mage_variation"));

            variants.put(itemId, ar);
        }
        for (Iterator<Element> iterator = rootElement.elementIterator("augmentation_data"); iterator.hasNext(); ) {
            Element augmentElement = iterator.next();

            int mineralId = Integer.parseInt(augmentElement.attributeValue("mineral_id"));
            int feeItemId = Integer.parseInt(augmentElement.attributeValue("fee_item_id"));
            long feeItemCount = Integer.parseInt(augmentElement.attributeValue("fee_item_count"));
            long cancelFee = Integer.parseInt(augmentElement.attributeValue("cancel_fee"));
            String itemGroup = augmentElement.attributeValue("item_group");
            RndSelector<OptionGroup>[][] rndSelectors = variants.get(mineralId);
            if (rndSelectors == null) {
                LOG.warn("Not find variants for mineral: " + mineralId);
            } else {
                AugmentationDataHolder.addStone(mineralId);

                AugmentationInfo augmentationInfo = new AugmentationInfo(mineralId, feeItemId, feeItemCount, cancelFee, rndSelectors);
                AugmentationDataHolder.addAugmentationInfo(augmentationInfo);

                int[] array = items.get(itemGroup);
                for (int i : array) {
                    ItemHolder.getTemplate(i).addAugmentationInfo(augmentationInfo);
                }
            }
        }
    }

    private RndSelector<OptionGroup>[] readVariation(Element warElement) {
        RndSelector<OptionGroup>[] sel = new RndSelector[2];
        if (warElement == null) {
            return null;
        }
        int val = 0;
        for (Element variantElement : warElement.elements()) {
            RndSelector<OptionGroup> rnd = new RndSelector<>();
            sel[(val++)] = rnd;

            int allGroupChance = 0;
            for (Element groupElement : variantElement.elements()) {
                OptionGroup optionGroup = new OptionGroup();
                int chance = (int) (toDouble(groupElement.attributeValue("chance")) * 10000.0D );
                allGroupChance += chance;

                rnd.add(optionGroup, chance);

                int allSubGroupChance = 0;
                for (Element optionElement : groupElement.elements()) {
                    int optionId = Integer.parseInt(optionElement.attributeValue("id"));
                    int optionChance = (int) (toDouble(optionElement.attributeValue("chance")) * 10000.0D);
                    allSubGroupChance += optionChance;

                    optionGroup.addOptionWithChance(optionId, optionChance);
                }
                if ((allSubGroupChance != 1000000) && (val != 2)) {
                    LOG.error("Sum of subgroups is not max, element: " + warElement.getName() + ", mineral: " + warElement.getParent().attributeValue("mineral_id"));
                }
            }
            if (allGroupChance != 1000000) {
                LOG.error("Sum of groups is not max, element: " + warElement.getName() + ", mineral: " + warElement.getParent().attributeValue("mineral_id"));
            }
        }
        return sel;
    }
}
