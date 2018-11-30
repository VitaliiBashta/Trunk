package l2trunk.gameserver.data.xml.parser;

import l2trunk.commons.data.xml.AbstractFileParser;
import l2trunk.commons.math.random.RndSelector;
import l2trunk.gameserver.Config;
import l2trunk.gameserver.data.xml.holder.AugmentationDataHolder;
import l2trunk.gameserver.data.xml.holder.ItemHolder;
import l2trunk.gameserver.templates.augmentation.AugmentationInfo;
import l2trunk.gameserver.templates.augmentation.OptionGroup;
import l2trunk.gameserver.templates.item.ItemTemplate;
import org.dom4j.Element;

import java.nio.file.Path;
import java.util.*;

//import org.napile.primitive.lists.IntList;
//import org.napile.primitive.lists.impl.ArrayIntList;
//import org.napile.primitive.maps.IntObjectMap;
//import org.napile.primitive.maps.impl.HashIntObjectMap;

public final class AugmentationDataParser extends AbstractFileParser<AugmentationDataHolder> {
    private static final AugmentationDataParser _instance = new AugmentationDataParser();

    private AugmentationDataParser() {
        super(AugmentationDataHolder.getInstance());
    }

    public static AugmentationDataParser getInstance() {
        return _instance;
    }

    @Override
    public Path getXMLFile() {
        return Config.DATAPACK_ROOT.resolve("data/augmentation_data.xml");
    }

    @Override
    public String getDTDFileName() {
        return "augmentation_data.dtd";
    }

    @Override
    protected void readData(Element rootElement) {
        Map<String, int[]> items = new HashMap<>();
        Map<Integer, RndSelector<OptionGroup>[][]> variants = new HashMap<>();
        for (Iterator<Element> iterator = rootElement.elementIterator("item_group"); iterator.hasNext(); ) {
            Element element = iterator.next();

            String name = element.attributeValue("name");

            List<Element> itemElements = element.elements();
            List<Integer> list = new ArrayList<>();
            for (Element itemElement : itemElements) {
                int itemId = Integer.parseInt(itemElement.attributeValue("id"));

                ItemTemplate itemTemplate = ItemHolder.getInstance().getTemplate(itemId);
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
                getHolder().addStone(mineralId);

                AugmentationInfo augmentationInfo = new AugmentationInfo(mineralId, feeItemId, feeItemCount, cancelFee, rndSelectors);
                getHolder().addAugmentationInfo(augmentationInfo);

                int[] array = items.get(itemGroup);
                for (int i : array) {
                    ItemHolder.getInstance().getTemplate(i).addAugmentationInfo(augmentationInfo);
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
                int chance = (int) (Double.parseDouble(groupElement.attributeValue("chance")) * 10000.0D * Config.AUGMENTATION_CHANCE_MOD[0]);
                allGroupChance += chance;

                rnd.add(optionGroup, chance);

                int allSubGroupChance = 0;
                for (Element optionElement : groupElement.elements()) {
                    int optionId = Integer.parseInt(optionElement.attributeValue("id"));
                    int optionChance = (int) (Double.parseDouble(optionElement.attributeValue("chance")) * 10000.0D * Config.AUGMENTATION_CHANCE_MOD[1]);
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
