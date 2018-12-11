package l2trunk.gameserver.data.xml.holder;

import l2trunk.gameserver.templates.augmentation.AugmentationInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AugmentationDataHolder  {
    private final static Set<AugmentationInfo> AUGMENTATION_INFOS = new HashSet<>();
    private final static List<Integer> lifestones = new ArrayList<>();

    public static int size() {
        return AUGMENTATION_INFOS.size();
    }

    public static void clear() {
        AUGMENTATION_INFOS.clear();
    }

    public static void addAugmentationInfo(AugmentationInfo augmentationInfo) {
        AUGMENTATION_INFOS.add(augmentationInfo);
    }

    public static void addStone(int item) {
        lifestones.add(item);
    }

    public static boolean isStone(int item) {
        return lifestones.contains(item);
    }
}
