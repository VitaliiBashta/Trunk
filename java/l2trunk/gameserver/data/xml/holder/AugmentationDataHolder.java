package l2trunk.gameserver.data.xml.holder;

import l2trunk.commons.data.xml.AbstractHolder;
import l2trunk.gameserver.templates.augmentation.AugmentationInfo;

import java.util.*;

public class AugmentationDataHolder extends AbstractHolder {
    private static final AugmentationDataHolder _instance = new AugmentationDataHolder();
    private final Set<AugmentationInfo> _augmentationInfos = new HashSet<>();
    private final List<Integer> _lifestone = new ArrayList<>();

    public static AugmentationDataHolder getInstance() {
        return _instance;
    }

    @Override
    public int size() {
        return _augmentationInfos.size();
    }

    @Override
    public void clear() {
        _augmentationInfos.clear();
    }

    public void addAugmentationInfo(AugmentationInfo augmentationInfo) {
        _augmentationInfos.add(augmentationInfo);
    }

    public void addStone(int item) {
        _lifestone.add(item);
    }

    public boolean isStone(int item) {
        for (Integer a_lifestone : _lifestone) {
            int id = a_lifestone;
            if (id == item) {
                return true;
            }
        }
        return false;
    }
}
